/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 *
 * Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *
 * 1.请不要删除和修改根目录下的LICENSE文件。
 * 2.请不要删除和修改Snowy源码头部的版权声明。
 * 3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 * 4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
 * 5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
 * 6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package vip.xiaonuo.mini;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vip.xiaonuo.biz.modular.activity.entity.VolActivity;
import vip.xiaonuo.biz.modular.activity.service.VolActivityService;
import vip.xiaonuo.biz.modular.activitycheckin.entity.VolActivityCheckin;
import vip.xiaonuo.biz.modular.activitycheckin.service.VolActivityCheckinService;
import vip.xiaonuo.client.modular.user.entity.ClientUserExt;
import vip.xiaonuo.client.modular.user.service.ClientUserService;
import vip.xiaonuo.common.pojo.CommonResult;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 服务记录控制器
 *
 * @author jetox
 * @date  2025/10/22
 */
@Tag(name = "服务记录控制器")
@RestController
@Validated
@Slf4j
public class ServiceRecordController {

    @Resource
    private VolActivityCheckinService volActivityCheckinService;

    @Resource
    private VolActivityService volActivityService;
    
    @Resource
    private ClientUserService clientUserService;

    /**
     * 获取服务记录分页列表
     */
    @Operation(summary = "获取服务记录分页列表")
    @GetMapping("/mini/serviceRecord/page")
    public CommonResult<Page<Map<String, Object>>> getServiceRecordPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam String userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            Page<VolActivityCheckin> page = new Page<>(current, size);
            
            // 查询签到记录（已签退的）
            LambdaQueryWrapper<VolActivityCheckin> query = new LambdaQueryWrapper<>();
            query.eq(VolActivityCheckin::getUserId, userId)
                 .isNotNull(VolActivityCheckin::getCheckoutTime) // 只查已签退的记录
                 .eq(VolActivityCheckin::getCheckinType, "CHECKIN")
                 .orderByDesc(VolActivityCheckin::getCheckinTime);
            
            // 时间范围筛选
            if (startDate != null && !startDate.isEmpty()) {
                query.ge(VolActivityCheckin::getCheckinTime, startDate);
            }
            if (endDate != null && !endDate.isEmpty()) {
                query.le(VolActivityCheckin::getCheckinTime, endDate + " 23:59:59");
            }
            
            Page<VolActivityCheckin> checkinPage = volActivityCheckinService.page(page, query);
            
            // 构建返回结果
            Page<Map<String, Object>> resultPage = new Page<>(current, size);
            resultPage.setTotal(checkinPage.getTotal());
            resultPage.setPages(checkinPage.getPages());
            
            List<Map<String, Object>> records = new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            
            for (VolActivityCheckin checkin : checkinPage.getRecords()) {
                Map<String, Object> record = new HashMap<>();
                
                // 查询活动信息
                VolActivity activity = volActivityService.getById(checkin.getActivityId());
                if (activity == null) continue;
                
                // 基本信息
                record.put("id", checkin.getId());
                record.put("activityId", activity.getId());
                record.put("activityTitle", activity.getTitle());
                record.put("location", activity.getLocation());
                record.put("serviceDate", dateFormat.format(checkin.getCheckinTime()));
                
                // 签到签退时间
                record.put("checkinTime", checkin.getCheckinTime());
                record.put("checkoutTime", checkin.getCheckoutTime());
                
                // 服务时间范围（格式化）
                String serviceTime = timeFormat.format(checkin.getCheckinTime()) + "-" + 
                                    timeFormat.format(checkin.getCheckoutTime());
                record.put("serviceTime", serviceTime);
                
                // 实际服务时长
                BigDecimal actualHours = checkin.getActualHours() != null ? 
                                        checkin.getActualHours() : activity.getServiceHours();
                record.put("serviceHours", actualHours);
                record.put("actualHours", actualHours);
                
                // 签退信息
                record.put("checkoutType", checkin.getCheckoutType()); // AUTO/MANUAL
                record.put("isEarlyLeave", checkin.getIsEarlyLeave() != null && checkin.getIsEarlyLeave() == 1);
                
                // 状态和积分（这里简化处理，实际可以从其他表获取）
                record.put("status", "COMPLETED");
                record.put("verifyStatus", "VERIFIED");
                record.put("credits", actualHours.multiply(new BigDecimal("10")).intValue()); // 简单计算：1小时=10积分
                record.put("canDownload", true);
                
                // 描述信息
                String description = "";
                if (checkin.getIsEarlyLeave() != null && checkin.getIsEarlyLeave() == 1) {
                    description += "提前离开 · ";
                }
                if ("AUTO".equals(checkin.getCheckoutType())) {
                    description += "自动签退";
                } else if ("MANUAL".equals(checkin.getCheckoutType())) {
                    description += "手动签退";
                }
                record.put("description", description.isEmpty() ? activity.getContent() : description);
                
                // 创建时间
                record.put("createTime", checkin.getCreateTime());
                
                records.add(record);
            }
            
            resultPage.setRecords(records);
            
            return CommonResult.data(resultPage);
            
        } catch (Exception e) {
            return CommonResult.error("获取服务记录失败：" + e.getMessage());
        }
    }

    /**
     * 获取服务统计数据
     */
    @Operation(summary = "获取服务统计数据")
    @GetMapping("/mini/serviceRecord/stats")
    public CommonResult<Map<String, Object>> getServiceRecordStats(
            @RequestParam String userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            // 查询签到记录
            LambdaQueryWrapper<VolActivityCheckin> query = new LambdaQueryWrapper<>();
            query.eq(VolActivityCheckin::getUserId, userId)
                 .isNotNull(VolActivityCheckin::getCheckoutTime) // 只统计已签退的记录
                 .eq(VolActivityCheckin::getCheckinType, "CHECKIN");
            
            // 时间范围筛选
            if (startDate != null && !startDate.isEmpty()) {
                query.ge(VolActivityCheckin::getCheckinTime, startDate);
            }
            if (endDate != null && !endDate.isEmpty()) {
                query.le(VolActivityCheckin::getCheckinTime, endDate + " 23:59:59");
            }
            
            List<VolActivityCheckin> checkins = volActivityCheckinService.list(query);
            
            // 统计数据
            BigDecimal totalHours = BigDecimal.ZERO;
            Set<String> activityIds = new HashSet<>();
            
            for (VolActivityCheckin checkin : checkins) {
                if (checkin.getActualHours() != null) {
                    totalHours = totalHours.add(checkin.getActualHours());
                }
                activityIds.add(checkin.getActivityId());
            }
            
            // 计算积分（简单规则：1小时=10积分）
            int totalCredits = totalHours.multiply(new BigDecimal("10")).intValue();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalHours", totalHours.doubleValue());
            stats.put("totalActivities", activityIds.size());
            stats.put("totalCredits", totalCredits);
            
            return CommonResult.data(stats);
            
        } catch (Exception e) {
            return CommonResult.error("获取统计数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取服务记录详情
     */
    @Operation(summary = "获取服务记录详情")
    @GetMapping("/mini/serviceRecord/detail")
    public CommonResult<Map<String, Object>> getServiceRecordDetail(@RequestParam String id) {
        try {
            VolActivityCheckin checkin = volActivityCheckinService.getById(id);
            if (checkin == null) {
                return CommonResult.error("服务记录不存在");
            }
            
            VolActivity activity = volActivityService.getById(checkin.getActivityId());
            if (activity == null) {
                return CommonResult.error("活动不存在");
            }
            
            Map<String, Object> detail = new HashMap<>();
            
            // 活动信息
            detail.put("activityTitle", activity.getTitle());
            detail.put("activityContent", activity.getContent());
            detail.put("location", activity.getLocation());
            
            // 签到签退信息
            detail.put("checkinTime", checkin.getCheckinTime());
            detail.put("checkoutTime", checkin.getCheckoutTime());
            detail.put("actualHours", checkin.getActualHours());
            detail.put("checkoutType", checkin.getCheckoutType());
            detail.put("isEarlyLeave", checkin.getIsEarlyLeave() != null && checkin.getIsEarlyLeave() == 1);
            
            // 地理位置
            if (checkin.getLocation() != null) {
                detail.put("checkinLocation", checkin.getLocation());
                detail.put("locationLat", checkin.getLocationLat());
                detail.put("locationLng", checkin.getLocationLng());
            }
            
            // 照片
            if (checkin.getPhotoUrl() != null) {
                detail.put("photoUrl", checkin.getPhotoUrl());
            }
            
            // 备注
            detail.put("remark", checkin.getRemark());
            
            return CommonResult.data(detail);
            
        } catch (Exception e) {
            return CommonResult.error("获取详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户统计数据（用于个人中心页面）
     */
    @Operation(summary = "获取用户统计数据")
    @GetMapping("/mini/user/stats")
    public CommonResult<Map<String, Object>> getUserStats(@RequestParam String userId) {
        try {
            // 查询签到记录（已签退的）
            LambdaQueryWrapper<VolActivityCheckin> query = new LambdaQueryWrapper<>();
            query.eq(VolActivityCheckin::getUserId, userId)
                 .isNotNull(VolActivityCheckin::getCheckoutTime)
                 .eq(VolActivityCheckin::getCheckinType, "CHECKIN");
            
            List<VolActivityCheckin> checkins = volActivityCheckinService.list(query);
            
            // 统计数据
            BigDecimal totalHours = BigDecimal.ZERO;
            Set<String> activityIds = new HashSet<>();
            
            for (VolActivityCheckin checkin : checkins) {
                if (checkin.getActualHours() != null) {
                    totalHours = totalHours.add(checkin.getActualHours());
                }
                activityIds.add(checkin.getActivityId());
            }
            
            // 从用户扩展表读取积分
            int totalCredits = 0;
            try {
                ClientUserExt userExt = clientUserService.getOrCreateClientUserExt(userId);
                if (userExt != null && userExt.getVolunteerIntegral() != null) {
                    totalCredits = userExt.getVolunteerIntegral();
                }
            } catch (Exception e) {
                log.error("获取用户积分失败: {}", e.getMessage());
            }
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalActivities", activityIds.size());
            stats.put("totalHours", totalHours.doubleValue());
            stats.put("totalCredits", totalCredits);
            
            return CommonResult.data(stats);
            
        } catch (Exception e) {
            return CommonResult.error("获取统计数据失败：" + e.getMessage());
        }
    }
}
