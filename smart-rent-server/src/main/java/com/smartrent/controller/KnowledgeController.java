package com.smartrent.controller;

import com.smartrent.common.RequireRole;
import com.smartrent.common.Result;
import com.smartrent.common.Role;
import com.smartrent.dto.KnowledgeDTO;
import com.smartrent.entity.Knowledge;
import com.smartrent.service.KnowledgeService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 知识库接口
 *
 * 权限说明：
 * - 列表/搜索：登录即可（租客问询、AI 问答检索用）
 * - 新增/修改/删除：仅 ADMIN
 */
@RestController
@RequestMapping("/knowledge")
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    public KnowledgeController(KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }

    /** 知识列表（可按分类过滤） */
    @GetMapping("/list")
    public Result<List<Knowledge>> list(@RequestParam(required = false) String category) {
        return Result.success(knowledgeService.list(category));
    }

    /** 关键词搜索（供 AI 问答检索） */
    @GetMapping("/search")
    public Result<List<Knowledge>> search(@RequestParam("q") String q) {
        return Result.success(knowledgeService.search(q));
    }

    /** 管理员新增知识 */
    @PostMapping
    @RequireRole(Role.ADMIN)
    public Result<Long> create(@RequestBody KnowledgeDTO dto) {
        return Result.success(knowledgeService.create(dto));
    }

    /** 管理员修改知识 */
    @PutMapping("/{id}")
    @RequireRole(Role.ADMIN)
    public Result<Void> update(@PathVariable Long id, @RequestBody KnowledgeDTO dto) {
        knowledgeService.update(id, dto);
        return Result.success();
    }

    /** 管理员删除知识 */
    @DeleteMapping("/{id}")
    @RequireRole(Role.ADMIN)
    public Result<Void> remove(@PathVariable Long id) {
        knowledgeService.remove(id);
        return Result.success();
    }
}
