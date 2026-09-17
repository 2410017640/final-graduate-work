package com.smartrent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.smartrent.common.BusinessException;
import com.smartrent.common.LoginUserContext;
import com.smartrent.dto.FilterCondition;
import com.smartrent.dto.HouseAuditDTO;
import com.smartrent.dto.HousePublishDTO;
import com.smartrent.entity.House;
import com.smartrent.mapper.HouseMapper;
import com.smartrent.service.TagService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 房源业务：发布、修改、删除、上下架、查询、审核
 */
@Service
public class HouseService {

    private final HouseMapper houseMapper;
    private final TagService tagService;
    private final NlParser nlParser;

    public HouseService(HouseMapper houseMapper, TagService tagService, NlParser nlParser) {
        this.houseMapper = houseMapper;
        this.tagService = tagService;
        this.nlParser = nlParser;
    }

    /**
     * 房东发布房源：初始状态为“待审核”
     */
    public Long publish(HousePublishDTO dto) {
        Long landlordId = LoginUserContext.getUserId();
        if (StringUtils.isBlank(dto.getTitle())) {
            throw new BusinessException("房源标题不能为空");
        }
        House house = new House();
        house.setLandlordId(landlordId);
        house.setTitle(dto.getTitle());
        house.setDescription(dto.getDescription());
        house.setAddress(dto.getAddress());
        house.setArea(dto.getArea());
        house.setRoomCount(dto.getRoomCount());
        house.setHallCount(dto.getHallCount());
        house.setRent(dto.getRent());
        house.setRentType(dto.getRentType() == null ? 1 : dto.getRentType());
        house.setOrientation(dto.getOrientation());
        house.setFloor(dto.getFloor());
        house.setTotalFloor(dto.getTotalFloor());
        house.setImages(dto.getImages());
        house.setStatus(House.STATUS_PENDING);
        house.setCreateTime(LocalDateTime.now());
        house.setUpdateTime(LocalDateTime.now());
        houseMapper.insert(house);
        // 维护房源-标签关系
        tagService.setHouseTags(house.getId(), dto.getTagIds());
        return house.getId();
    }

    /**
     * 房东修改自己的房源
     */
    public void update(Long id, HousePublishDTO dto) {
        House house = getOwnedHouse(id);
        house.setTitle(dto.getTitle());
        house.setDescription(dto.getDescription());
        house.setAddress(dto.getAddress());
        house.setArea(dto.getArea());
        house.setRoomCount(dto.getRoomCount());
        house.setHallCount(dto.getHallCount());
        house.setRent(dto.getRent());
        if (dto.getRentType() != null) {
            house.setRentType(dto.getRentType());
        }
        house.setOrientation(dto.getOrientation());
        house.setFloor(dto.getFloor());
        house.setTotalFloor(dto.getTotalFloor());
        house.setImages(dto.getImages());
        house.setUpdateTime(LocalDateTime.now());
        houseMapper.updateById(house);
        // 重新设置房源-标签关系
        tagService.setHouseTags(id, dto.getTagIds());
    }

    /**
     * 房东删除自己的房源（同时解除标签关联）
     */
    public void remove(Long id) {
        getOwnedHouse(id);
        houseMapper.deleteById(id);
        tagService.setHouseTags(id, null);
    }

    /**
     * 房东下架
     */
    public void offline(Long id) {
        House house = getOwnedHouse(id);
        house.setStatus(House.STATUS_OFFLINE);
        house.setUpdateTime(LocalDateTime.now());
        houseMapper.updateById(house);
    }

    /**
     * 房东重新上架：重新进入“待审核”
     */
    public void online(Long id) {
        House house = getOwnedHouse(id);
        house.setStatus(House.STATUS_PENDING);
        house.setUpdateTime(LocalDateTime.now());
        houseMapper.updateById(house);
    }

    /**
     * 我的房源（房东视角，按创建时间倒序）
     */
    public List<House> myHouses() {
        Long landlordId = LoginUserContext.getUserId();
        List<House> houses = houseMapper.selectList(new LambdaQueryWrapper<House>()
                .eq(House::getLandlordId, landlordId)
                .orderByDesc(House::getCreateTime));
        fillTags(houses);
        return houses;
    }

    /**
     * 公开列表：只显示已通过(上架)的房源，支持标题/地址关键词模糊搜索
     */
    public List<House> listApproved(String keyword) {
        LambdaQueryWrapper<House> q = new LambdaQueryWrapper<House>()
                .eq(House::getStatus, House.STATUS_APPROVED);
        if (StringUtils.isNotBlank(keyword)) {
            q.and(w -> w.like(House::getTitle, keyword).or().like(House::getAddress, keyword));
        }
        q.orderByDesc(House::getCreateTime);
        List<House> houses = houseMapper.selectList(q);
        fillTags(houses);
        return houses;
    }

    /**
     * 自然语言筛选：把"近地铁的两居室4000以内"解析成结构化条件，再查已通过房源
     */
    public List<House> searchByQuery(String query) {
        return search(nlParser.parse(query));
    }

    /**
     * 按结构化条件筛选已通过房源（手动筛选与自然语言筛选共用同一套过滤逻辑）
     * 支持：标签(AND)、租金区间、室数、面积下限、关键词
     */
    public List<House> search(FilterCondition fc) {
        LambdaQueryWrapper<House> q = new LambdaQueryWrapper<House>()
                .eq(House::getStatus, House.STATUS_APPROVED);
        if (fc.getRoomCount() != null) {
            q.eq(House::getRoomCount, fc.getRoomCount());
        }
        if (fc.getMinRent() != null) {
            q.ge(House::getRent, fc.getMinRent());
        }
        if (fc.getMaxRent() != null) {
            q.le(House::getRent, fc.getMaxRent());
        }
        if (fc.getMinArea() != null) {
            q.ge(House::getArea, fc.getMinArea());
        }
        if (StringUtils.isNotBlank(fc.getKeyword())) {
            q.and(w -> w.like(House::getTitle, fc.getKeyword())
                    .or().like(House::getAddress, fc.getKeyword())
                    .or().like(House::getDescription, fc.getKeyword()));
        }
        if (fc.getTagIds() != null && !fc.getTagIds().isEmpty()) {
            List<Long> houseIds = tagService.listHouseIdsByTags(fc.getTagIds());
            if (houseIds.isEmpty()) {
                return new ArrayList<>();
            }
            q.in(House::getId, houseIds);
        }
        q.orderByDesc(House::getCreateTime);
        List<House> houses = houseMapper.selectList(q);
        fillTags(houses);
        return houses;
    }

    /**
     * 详情：仅已通过的房源可公开查看
     */
    public House detail(Long id) {
        House house = houseMapper.selectById(id);
        if (house == null || house.getStatus() != House.STATUS_APPROVED) {
            throw new BusinessException("房源不存在或未通过审核");
        }
        fillTags(List.of(house));
        return house;
    }

    /**
     * 管理员：待审核列表（按提交时间正序）
     */
    public List<House> pendingList() {
        return houseMapper.selectList(new LambdaQueryWrapper<House>()
                .eq(House::getStatus, House.STATUS_PENDING)
                .orderByAsc(House::getCreateTime));
    }

    /**
     * 管理员审核：通过或拒绝
     */
    public void audit(Long id, HouseAuditDTO dto) {
        House house = houseMapper.selectById(id);
        if (house == null) {
            throw new BusinessException("房源不存在");
        }
        if (house.getStatus() != House.STATUS_PENDING) {
            throw new BusinessException("该房源已审核，不能重复审核");
        }
        if (dto.getPass() != null && dto.getPass() == 1) {
            house.setStatus(House.STATUS_APPROVED);
            house.setRejectReason(null);
        } else {
            if (StringUtils.isBlank(dto.getRejectReason())) {
                throw new BusinessException("拒绝时必须填写原因");
            }
            house.setStatus(House.STATUS_REJECTED);
            house.setRejectReason(dto.getRejectReason());
        }
        house.setUpdateTime(LocalDateTime.now());
        houseMapper.updateById(house);
    }

    /**
     * 校验房源存在且属于当前登录房东（写操作共用）
     */
    private House getOwnedHouse(Long id) {
        House house = houseMapper.selectById(id);
        if (house == null) {
            throw new BusinessException("房源不存在");
        }
        Long current = LoginUserContext.getUserId();
        if (!current.equals(house.getLandlordId())) {
            throw new BusinessException("只能操作自己发布的房源");
        }
        return house;
    }

    /**
     * 给房源列表批量回填标签（一次性查询，避免循环查库）
     */
    private void fillTags(List<House> houses) {
        if (houses == null || houses.isEmpty()) {
            return;
        }
        List<Long> houseIds = houses.stream()
                .map(House::getId)
                .collect(Collectors.toList());
        Map<Long, List<com.smartrent.entity.Tag>> tagMap = tagService.listTagsByHouseIds(houseIds);
        for (House house : houses) {
            house.setTags(tagMap.getOrDefault(house.getId(), List.of()));
        }
    }
}
