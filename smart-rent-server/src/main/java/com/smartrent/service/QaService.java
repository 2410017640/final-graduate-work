package com.smartrent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.smartrent.common.BusinessException;
import com.smartrent.entity.House;
import com.smartrent.entity.Knowledge;
import com.smartrent.entity.Question;
import com.smartrent.mapper.HouseMapper;
import com.smartrent.mapper.QuestionMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AI 问答业务：租客提问 -> 关键词匹配知识库 -> 命中自动回答；未命中记录为待回答(P10 由房东处理)。
 */
@Service
public class QaService {

    /** 领域关键词（从租客问题中抽取，用于匹配知识库） */
    private static final String[] KEYWORDS = {
            "押金", "合同", "退租", "费用", "合租", "宠物", "养宠物", "水电", "燃气",
            "维修", "违约", "提前", "通知", "中介", "租金", "入住", "租房"
    };

    private final QuestionMapper questionMapper;
    private final KnowledgeService knowledgeService;
    private final HouseMapper houseMapper;
    private final AnswerGenerator answerGenerator;

    public QaService(QuestionMapper questionMapper, KnowledgeService knowledgeService,
                     HouseMapper houseMapper, AnswerGenerator answerGenerator) {
        this.questionMapper = questionMapper;
        this.knowledgeService = knowledgeService;
        this.houseMapper = houseMapper;
        this.answerGenerator = answerGenerator;
    }

    /**
     * 租客提问：先查知识库，命中则自动回答；未命中则记录为待回答并转交房东
     */
    public Question ask(Long askerId, String questionText, Long houseId) {
        if (StringUtils.isBlank(questionText)) {
            throw new BusinessException("问题不能为空");
        }
        List<Knowledge> context = retrieveKnowledge(questionText);
        String answer = answerGenerator.generate(questionText, context);

        Question q = new Question();
        q.setQuestion(questionText.trim());
        q.setAskerId(askerId);
        q.setHouseId(houseId);
        // 关联了房源则定位到该房源的房东
        if (houseId != null) {
            House house = houseMapper.selectById(houseId);
            if (house != null) {
                q.setLandlordId(house.getLandlordId());
            }
        }
        q.setCreateTime(LocalDateTime.now());
        q.setUpdateTime(LocalDateTime.now());

        if (answer != null) {
            q.setAnswer(answer);
            q.setStatus(Question.STATUS_ANSWERED);
            q.setAnsweredByKb(1);
        } else {
            q.setAnswer(null);
            q.setStatus(Question.STATUS_PENDING);
            q.setAnsweredByKb(0);
        }
        questionMapper.insert(q);
        return q;
    }

    /** 我的提问（租客视角） */
    public List<Question> myQuestions(Long askerId) {
        return questionMapper.selectList(new LambdaQueryWrapper<Question>()
                .eq(Question::getAskerId, askerId)
                .orderByDesc(Question::getCreateTime));
    }

    /** 待我回答的问题（房东视角，P10 通知） */
    public List<Question> pendingQuestions(Long landlordId) {
        return questionMapper.selectList(new LambdaQueryWrapper<Question>()
                .eq(Question::getLandlordId, landlordId)
                .eq(Question::getStatus, Question.STATUS_PENDING)
                .orderByDesc(Question::getCreateTime));
    }

    /** 房东回答待处理问题 */
    public void answer(Long questionId, Long landlordId, String answer) {
        if (StringUtils.isBlank(answer)) {
            throw new BusinessException("回答不能为空");
        }
        Question q = questionMapper.selectById(questionId);
        if (q == null) {
            throw new BusinessException("问题不存在");
        }
        if (q.getLandlordId() != null && !q.getLandlordId().equals(landlordId)) {
            throw new BusinessException("只能回答分配给自己的问题");
        }
        q.setAnswer(answer.trim());
        q.setStatus(Question.STATUS_ANSWERED);
        q.setAnsweredByKb(0);
        q.setUpdateTime(LocalDateTime.now());
        questionMapper.updateById(q);
    }

    /**
     * RAG 检索阶段：按"命中词在问题/答案/分类"加权打分，返回相关度最高的 top-K 条知识。
     */
    private List<Knowledge> retrieveKnowledge(String question) {
        List<Knowledge> all = knowledgeService.list(null);
        Map<Knowledge, Integer> scores = new LinkedHashMap<>();
        for (Knowledge k : all) {
            int score = 0;
            for (String kw : KEYWORDS) {
                if (question.contains(kw)) {
                    if (k.getQuestion() != null && k.getQuestion().contains(kw)) {
                        score += 3;
                    }
                    if (k.getAnswer() != null && k.getAnswer().contains(kw)) {
                        score += 1;
                    }
                    if (kw.equals(k.getCategory())) {
                        score += 2;
                    }
                }
            }
            if (score >= 3) {
                scores.put(k, score);
            }
        }
        return scores.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
