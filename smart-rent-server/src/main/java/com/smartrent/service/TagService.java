package com.smartrent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.smartrent.common.BusinessException;
import com.smartrent.dto.TagCreateDTO;
import com.smartrent.dto.TagUpdateDTO;
import com.smartrent.entity.HouseTag;
import com.smartrent.entity.Tag;
import com.smartrent.mapper.HouseTagMapper;
import com.smartrent.mapper.TagMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 标签业务：标签字典的查询/增删改，以及房源-标签关系的维护与批量查询
 */
@Service
public class TagService {

    private final TagMapper tagMapper;
    private final HouseTagMapper houseTagMapper;

    public TagService(TagMapper tagMapper, HouseTagMapper houseTagMapper) {
        this.tagMapper = tagMapper;
        this.houseTagMapper = houseTagMapper;
    }

    /**
     * 列出全部标签（按分组、名称排序），供前端展示和房东发布房源时勾选
     */
    public List<Tag> listAll() {
        return tagMapper.selectList(new LambdaQueryWrapper<Tag>()
                .orderByAsc(Tag::getCategory)
                .orderByAsc(Tag::getName));
    }

    /**
     * 管理员新增标签（名称不能重复）
     */
    public Long create(TagCreateDTO dto) {
        if (StringUtils.isBlank(dto.getName())) {
            throw new BusinessException("标签名称不能为空");
        }
        Long count = tagMapper.selectCount(
                new LambdaQueryWrapper<Tag>().eq(Tag::getName, dto.getName().trim()));
        if (count != null && count > 0) {
            throw new BusinessException("标签名称已存在");
        }
        Tag tag = new Tag();
        tag.setName(dto.getName().trim());
        tag.setCategory(StringUtils.isBlank(dto.getCategory()) ? "" : dto.getCategory().trim());
        tag.setCreateTime(LocalDateTime.now());
        tagMapper.insert(tag);
        return tag.getId();
    }

    /**
     * 管理员修改标签
     */
    public void update(Long id, TagUpdateDTO dto) {
        Tag tag = tagMapper.selectById(id);
        if (tag == null) {
            throw new BusinessException("标签不存在");
        }
        if (StringUtils.isNotBlank(dto.getName())) {
            String newName = dto.getName().trim();
            if (!newName.equals(tag.getName())) {
                Long count = tagMapper.selectCount(
                        new LambdaQueryWrapper<Tag>().eq(Tag::getName, newName));
                if (count != null && count > 0) {
                    throw new BusinessException("标签名称已存在");
                }
                tag.setName(newName);
            }
        }
        if (dto.getCategory() != null) {
            tag.setCategory(dto.getCategory().trim());
        }
        tagMapper.updateById(tag);
    }

    /**
     * 管理员删除标签，同时删除该标签与房源的关联关系
     */
    public void remove(Long id) {
        if (tagMapper.selectById(id) == null) {
            throw new BusinessException("标签不存在");
        }
        // 先删关系，再删字典
        houseTagMapper.delete(
                new LambdaQueryWrapper<HouseTag>().eq(HouseTag::getTagId, id));
        tagMapper.deleteById(id);
    }

    /**
     * 重新设置某房源的标签：先删旧关系，再批量插入新关系
     * tagIds 为 null 或空表示清空该房源的全部标签
     */
    public void setHouseTags(Long houseId, List<Long> tagIds) {
        houseTagMapper.delete(
                new LambdaQueryWrapper<HouseTag>().eq(HouseTag::getHouseId, houseId));
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }
        List<HouseTag> relations = new ArrayList<>();
        for (Long tagId : tagIds) {
            HouseTag rel = new HouseTag();
            rel.setHouseId(houseId);
            rel.setTagId(tagId);
            relations.add(rel);
        }
        // 逐条插入（数据量小，简单清晰；本科项目足够）
        for (HouseTag rel : relations) {
            houseTagMapper.insert(rel);
        }
    }

    /**
     * 批量查询多个房源各自的标签列表，返回 map: houseId -> 标签列表
     * 用于房源列表/详情一次性回填标签，避免循环查库（N+1 问题）
     */
    public Map<Long, List<Tag>> listTagsByHouseIds(List<Long> houseIds) {
        Map<Long, List<Tag>> result = new LinkedHashMap<>();
        if (houseIds == null || houseIds.isEmpty()) {
            return result;
        }
        // 1. 查出这些房源的所有关系行
        List<HouseTag> relations = houseTagMapper.selectList(
                new LambdaQueryWrapper<HouseTag>().in(HouseTag::getHouseId, houseIds));
        if (relations.isEmpty()) {
            return result;
        }
        // 2. 收集涉及的标签ID，一次性查出标签
        List<Long> tagIds = relations.stream()
                .map(HouseTag::getTagId)
                .distinct()
                .collect(Collectors.toList());
        List<Tag> tags = tagMapper.selectList(
                new LambdaQueryWrapper<Tag>().in(Tag::getId, tagIds));
        Map<Long, Tag> tagMap = tags.stream()
                .collect(Collectors.toMap(Tag::getId, t -> t, (a, b) -> a));

        // 3. 组装成 houseId -> 标签列表
        for (Long houseId : houseIds) {
            result.put(houseId, new ArrayList<>());
        }
        for (HouseTag rel : relations) {
            Tag tag = tagMap.get(rel.getTagId());
            if (tag != null && result.containsKey(rel.getHouseId())) {
                result.get(rel.getHouseId()).add(tag);
            }
        }
        return result;
    }

    /**
     * 查询"同时拥有所有给定标签"的房源ID列表（用于按标签筛选，需全部满足）
     */
    public List<Long> listHouseIdsByTags(List<Long> tagIds) {
        List<Long> result = new ArrayList<>();
        if (tagIds == null || tagIds.isEmpty()) {
            return result;
        }
        List<HouseTag> relations = houseTagMapper.selectList(
                new LambdaQueryWrapper<HouseTag>().in(HouseTag::getTagId, tagIds));
        // 统计每个房源命中的不同标签数
        Map<Long, Set<Long>> houseTagSet = new HashMap<>();
        for (HouseTag rel : relations) {
            houseTagSet.computeIfAbsent(rel.getHouseId(), k -> new HashSet<>()).add(rel.getTagId());
        }
        for (Map.Entry<Long, Set<Long>> e : houseTagSet.entrySet()) {
            if (e.getValue().containsAll(tagIds)) {
                result.add(e.getKey());
            }
        }
        return result;
    }
}
