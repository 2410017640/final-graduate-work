package com.smartrent.controller;

import com.smartrent.common.RequireRole;
import com.smartrent.common.Result;
import com.smartrent.common.Role;
import com.smartrent.dto.HouseAuditDTO;
import com.smartrent.dto.HousePublishDTO;
import com.smartrent.entity.House;
import com.smartrent.service.HouseService;
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
 * 房源接口：发布/修改/删除/上下架/查询/审核
 *
 * 权限说明：
 * - 发布/修改/删除/上下架/我的房源：仅 LANDLORD，且只能操作自己的房源
 * - 管理员待审核列表/审核：仅 ADMIN
 * - 公开列表/详情：需登录即可（拦截器保证已登录），但只返回已通过房源
 */
@RestController
@RequestMapping("/house")
public class HouseController {

    private final HouseService houseService;

    public HouseController(HouseService houseService) {
        this.houseService = houseService;
    }

    /** 发布房源（房东） */
    @PostMapping("/publish")
    @RequireRole(Role.LANDLORD)
    public Result<Long> publish(@RequestBody HousePublishDTO dto) {
        return Result.success(houseService.publish(dto));
    }

    /** 修改房源（房东，仅自己的） */
    @PutMapping("/{id}")
    @RequireRole(Role.LANDLORD)
    public Result<Void> update(@PathVariable Long id, @RequestBody HousePublishDTO dto) {
        houseService.update(id, dto);
        return Result.success();
    }

    /** 删除房源（房东，仅自己的） */
    @DeleteMapping("/{id}")
    @RequireRole(Role.LANDLORD)
    public Result<Void> remove(@PathVariable Long id) {
        houseService.remove(id);
        return Result.success();
    }

    /** 下架（房东） */
    @PostMapping("/{id}/offline")
    @RequireRole(Role.LANDLORD)
    public Result<Void> offline(@PathVariable Long id) {
        houseService.offline(id);
        return Result.success();
    }

    /** 重新上架（房东，重新进入待审核） */
    @PostMapping("/{id}/online")
    @RequireRole(Role.LANDLORD)
    public Result<Void> online(@PathVariable Long id) {
        houseService.online(id);
        return Result.success();
    }

    /** 我的房源（房东） */
    @GetMapping("/my")
    @RequireRole(Role.LANDLORD)
    public Result<List<House>> my() {
        return Result.success(houseService.myHouses());
    }

    /** 公开列表：仅已通过房源，支持关键词 */
    @GetMapping
    public Result<List<House>> list(@RequestParam(required = false) String keyword) {
        return Result.success(houseService.listApproved(keyword));
    }

    /** 详情（仅已通过） */
    @GetMapping("/{id}")
    public Result<House> detail(@PathVariable Long id) {
        return Result.success(houseService.detail(id));
    }

    /** 管理员：待审核列表 */
    @GetMapping("/admin/pending")
    @RequireRole(Role.ADMIN)
    public Result<List<House>> pending() {
        return Result.success(houseService.pendingList());
    }

    /** 管理员：审核通过/拒绝 */
    @PostMapping("/admin/{id}/audit")
    @RequireRole(Role.ADMIN)
    public Result<Void> audit(@PathVariable Long id, @RequestBody HouseAuditDTO dto) {
        houseService.audit(id, dto);
        return Result.success();
    }
}
