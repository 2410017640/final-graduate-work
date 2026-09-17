package com.smartrent.controller;

import com.smartrent.common.RequireRole;
import com.smartrent.common.Result;
import com.smartrent.common.Role;
import com.smartrent.dto.TagCreateDTO;
import com.smartrent.dto.TagUpdateDTO;
import com.smartrent.entity.Tag;
import com.smartrent.service.TagService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 标签接口
 *
 * 权限说明：
 * - 获取全部标签：登录即可（房东发布房源时要勾选、租客筛选时也要看）
 * - 新增/修改/删除标签：仅 ADMIN
 */
@RestController
@RequestMapping("/tag")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    /** 获取全部标签（按分组展示） */
    @GetMapping("/list")
    public Result<List<Tag>> list() {
        return Result.success(tagService.listAll());
    }

    /** 管理员新增标签 */
    @PostMapping
    @RequireRole(Role.ADMIN)
    public Result<Long> create(@RequestBody TagCreateDTO dto) {
        return Result.success(tagService.create(dto));
    }

    /** 管理员修改标签 */
    @PutMapping("/{id}")
    @RequireRole(Role.ADMIN)
    public Result<Void> update(@PathVariable Long id, @RequestBody TagUpdateDTO dto) {
        tagService.update(id, dto);
        return Result.success();
    }

    /** 管理员删除标签（同时解除房源关联） */
    @DeleteMapping("/{id}")
    @RequireRole(Role.ADMIN)
    public Result<Void> remove(@PathVariable Long id) {
        tagService.remove(id);
        return Result.success();
    }
}
