package com.smartrent.controller;

import com.smartrent.common.LoginUserContext;
import com.smartrent.common.RequireRole;
import com.smartrent.common.Result;
import com.smartrent.common.Role;
import com.smartrent.dto.QaAnswerDTO;
import com.smartrent.dto.QaAskDTO;
import com.smartrent.entity.Question;
import com.smartrent.service.QaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * AI 问答接口
 *
 * 权限说明：
 * - 提问 / 我的提问：登录即可（租客）
 * - 待回答列表 / 回答：仅 LANDLORD（P10 未知问题通知房东）
 */
@RestController
@RequestMapping("/qa")
public class QaController {

    private final QaService qaService;

    public QaController(QaService qaService) {
        this.qaService = qaService;
    }

    /** 提问（租客） */
    @PostMapping("/ask")
    public Result<Question> ask(@RequestBody QaAskDTO dto) {
        Long askerId = LoginUserContext.getUserId();
        return Result.success(qaService.ask(askerId, dto.getQuestion(), dto.getHouseId()));
    }

    /** 我的提问（租客） */
    @GetMapping("/my")
    public Result<List<Question>> my() {
        return Result.success(qaService.myQuestions(LoginUserContext.getUserId()));
    }

    /** 待我回答的问题（房东） */
    @GetMapping("/pending")
    @RequireRole(Role.LANDLORD)
    public Result<List<Question>> pending() {
        return Result.success(qaService.pendingQuestions(LoginUserContext.getUserId()));
    }

    /** 房东回答待处理问题 */
    @PostMapping("/{id}/answer")
    @RequireRole(Role.LANDLORD)
    public Result<Void> answer(@PathVariable Long id, @RequestBody QaAnswerDTO dto) {
        qaService.answer(id, LoginUserContext.getUserId(), dto.getAnswer());
        return Result.success();
    }
}
