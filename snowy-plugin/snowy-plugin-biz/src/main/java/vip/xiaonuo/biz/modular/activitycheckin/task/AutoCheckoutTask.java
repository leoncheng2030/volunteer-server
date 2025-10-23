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
package vip.xiaonuo.biz.modular.activitycheckin.task;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vip.xiaonuo.biz.modular.activity.entity.VolActivity;
import vip.xiaonuo.biz.modular.activity.service.VolActivityService;
import vip.xiaonuo.biz.modular.activitycheckin.entity.VolActivityCheckin;
import vip.xiaonuo.biz.modular.activitycheckin.service.VolActivityCheckinService;
import vip.xiaonuo.client.ClientUserApi;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 自动签退定时任务
 * 功能：为已结束的活动自动签退
 * 执行频率：每小时执行一次
 *
 * @author jetox
 * @date  2025/10/22
 */
@Slf4j
@Component
public class AutoCheckoutTask {

    @Resource
    private VolActivityService volActivityService;

    @Resource
    private VolActivityCheckinService volActivityCheckinService;

    @Resource
    private ClientUserApi clientUserApi;

    /**
     * 自动签退定时任务
     * cron表达式：0 0 * * * ? 表示每小时整点执行
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void autoCheckout() {
        log.info("开始执行自动签退定时任务");
        
        try {
            // 1. 查询所有已结束的活动
            LambdaQueryWrapper<VolActivity> activityQuery = new LambdaQueryWrapper<>();
            activityQuery.eq(VolActivity::getStatus, "ENDED")
                        .lt(VolActivity::getEndTime, new Date()); // 结束时间小于当前时间
            
            List<VolActivity> endedActivities = volActivityService.list(activityQuery);
            
            if (endedActivities.isEmpty()) {
                log.info("没有需要处理的已结束活动");
                return;
            }
            
            int totalProcessed = 0;
            int totalAutoCheckedOut = 0;
            
            // 2. 遍历每个已结束的活动
            for (VolActivity activity : endedActivities) {
                try {
                    // 查询该活动中未签退的签到记录
                    LambdaQueryWrapper<VolActivityCheckin> checkinQuery = new LambdaQueryWrapper<>();
                    checkinQuery.eq(VolActivityCheckin::getActivityId, activity.getId())
                               .isNull(VolActivityCheckin::getCheckoutTime) // 未签退
                               .eq(VolActivityCheckin::getCheckinType, "CHECKIN"); // 签到类型
                    
                    List<VolActivityCheckin> unCheckedOutRecords = volActivityCheckinService.list(checkinQuery);
                    
                    if (unCheckedOutRecords.isEmpty()) {
                        continue;
                    }
                    
                    // 3. 批量更新签退信息
                    for (VolActivityCheckin checkin : unCheckedOutRecords) {
                        checkin.setCheckoutTime(activity.getEndTime()); // 签退时间设为活动结束时间
                        checkin.setActualHours(activity.getServiceHours()); // 实际服务时长设为活动设定时长
                        checkin.setCheckoutType("AUTO"); // 自动签退
                        checkin.setIsEarlyLeave(0); // 未提前离开
                        
                        totalAutoCheckedOut++;
                    }
                    
                    // 批量更新签到记录
                    boolean updated = volActivityCheckinService.updateBatchById(unCheckedOutRecords);
                    
                    if (updated) {
                        totalProcessed++;
                        log.info("活动[{}]自动签退完成，处理{}条记录", 
                                activity.getTitle(), unCheckedOutRecords.size());
                        
                        // 4. 累加用户积分
                        addActivityPointsToUsers(activity, unCheckedOutRecords);
                    }
                    
                } catch (Exception e) {
                    log.error("处理活动[{}]自动签退失败：{}", activity.getTitle(), e.getMessage(), e);
                }
            }
            
            log.info("自动签退定时任务执行完成，共处理{}个活动，自动签退{}条记录", 
                    totalProcessed, totalAutoCheckedOut);
            
        } catch (Exception e) {
            log.error("自动签退定时任务执行失败：{}", e.getMessage(), e);
        }
    }
    
    /**
     * 手动触发自动签退（可选）
     * 用于测试或紧急情况下手动触发
     */
    public void manualTriggerAutoCheckout() {
        log.info("手动触发自动签退任务");
        autoCheckout();
    }
    
    /**
     * 为用户累加活动积分
     * @param activity 活动信息
     * @param checkinRecords 签到记录列表
     */
    private void addActivityPointsToUsers(VolActivity activity, List<VolActivityCheckin> checkinRecords) {
        try {
            // 从活动的 extJson 中读取活动积分
            String extJson = activity.getExtJson();
            if (extJson == null || extJson.isEmpty()) {
                log.debug("活动[{}]未设置扩展信息", activity.getTitle());
                return;
            }
            
            JSONObject extInfo = JSONUtil.parseObj(extJson);
            Integer activityPoints = extInfo.getInt("activityPoints");
            
            if (activityPoints == null || activityPoints <= 0) {
                log.debug("活动[{}]未设置积分或积分为0", activity.getTitle());
                return;
            }
            
            log.info("开始为活动[{}]的参与者累加积分，每人{}分", activity.getTitle(), activityPoints);
            
            int successCount = 0;
            int failCount = 0;
            
            // 为每个签到用户累加积分
            for (VolActivityCheckin checkin : checkinRecords) {
                try {
                    String userId = checkin.getUserId();
                    
                    // 获取或创建用户扩展信息
                    JSONObject userExt = clientUserApi.getOrCreateClientUserExt(userId);
                    
                    if (userExt != null) {
                        // 累加积分
                        Integer currentPoints = userExt.getInt("volunteerIntegral");
                        if (currentPoints == null) {
                            currentPoints = 0;
                        }
                        
                        int newPoints = currentPoints + activityPoints;
                        
                        // 更新积分
                        clientUserApi.updateUserPoints(userId, newPoints);
                        
                        successCount++;
                        log.debug("用户[{}]积分累加成功：{} -> {}", userId, currentPoints, newPoints);
                    } else {
                        failCount++;
                        log.warn("获取用户[{}]扩展信息失败", userId);
                    }
                    
                } catch (Exception e) {
                    failCount++;
                    log.error("为用户[{}]累加积分失败：{}", checkin.getUserId(), e.getMessage(), e);
                }
            }
            
            log.info("活动[{}]积分累加完成，成功{}人，失败{}人", activity.getTitle(), successCount, failCount);
            
        } catch (Exception e) {
            log.error("活动[{}]积分累加过程出错：{}", activity.getTitle(), e.getMessage(), e);
        }
    }
}
