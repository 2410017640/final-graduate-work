package com.smartrent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.smartrent.common.BusinessException;
import com.smartrent.dto.KnowledgeDTO;
import com.smartrent.entity.Knowledge;
import com.smartrent.mapper.KnowledgeMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 知识库业务：列表 / 关键词搜索 / 管理员增删改
 */
@Service
public class KnowledgeService {

    private final KnowledgeMapper knowledgeMapper;

    public KnowledgeService(KnowledgeMapper knowledgeMapper) {
        this.knowledgeMapper = knowledgeMapper;
    }

    /** 列出全部知识（可按分类过滤） */
    public List<Knowledge> list(String category) {
        LambdaQueryWrapper<Knowledge> q = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(category)) {
            q.eq(Knowledge::getCategory, category);
        }
        q.orderByAsc(Knowledge::getId);
        return knowledgeMapper.selectList(q);
    }

    /**
     * 关键词搜索（问题/答案模糊匹配），供 AI 问答(P8) 与 RAG(P9) 检索复用
     */
    public List<Knowledge> search(String keyword) {
        LambdaQueryWrapper<Knowledge> q = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(keyword)) {
            q.and(w -> w.like(Knowledge::getQuestion, keyword)
                    .or().like(Knowledge::getAnswer, keyword));
        }
        q.orderByAsc(Knowledge::getId);
        return knowledgeMapper.selectList(q);
    }

    /** 管理员新增知识 */
    public Long create(KnowledgeDTO dto) {
        if (StringUtils.isBlank(dto.getQuestion())) {
            throw new BusinessException("问题不能为空");
        }
        Knowledge k = new Knowledge();
        k.setQuestion(dto.getQuestion().trim());
        k.setAnswer(dto.getAnswer());
        k.setCategory(StringUtils.isBlank(dto.getCategory()) ? "" : dto.getCategory().trim());
        k.setCreateTime(LocalDateTime.now());
        k.setUpdateTime(LocalDateTime.now());
        knowledgeMapper.insert(k);
        return k.getId();
    }

    /** 管理员修改知识 */
    public void update(Long id, KnowledgeDTO dto) {
        Knowledge k = knowledgeMapper.selectById(id);
        if (k == null) {
            throw new BusinessException("知识不存在");
        }
        if (StringUtils.isNotBlank(dto.getQuestion())) {
            k.setQuestion(dto.getQuestion().trim());
        }
        if (dto.getAnswer() != null) {
            k.setAnswer(dto.getAnswer());
        }
        if (dto.getCategory() != null) {
            k.setCategory(dto.getCategory().trim());
        }
        k.setUpdateTime(LocalDateTime.now());
        knowledgeMapper.updateById(k);
    }

    /** 管理员删除知识 */
    public void remove(Long id) {
        if (knowledgeMapper.selectById(id) == null) {
            throw new BusinessException("知识不存在");
        }
        knowledgeMapper.deleteById(id);
    }
}
