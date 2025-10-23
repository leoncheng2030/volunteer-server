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

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vip.xiaonuo.biz.modular.activity.entity.VolActivity;
import vip.xiaonuo.biz.modular.activity.param.VolActivityPageParam;
import vip.xiaonuo.biz.modular.activity.param.VolActivityIdParam;
import vip.xiaonuo.biz.modular.activity.service.VolActivityService;
import vip.xiaonuo.biz.modular.activitysignup.entity.VolActivitySignup;
import vip.xiaonuo.biz.modular.activitysignup.param.VolActivitySignupParam;
import vip.xiaonuo.biz.modular.activitysignup.param.VolActivityBatchSignupParam;
import vip.xiaonuo.biz.modular.activitysignup.service.VolActivitySignupService;
import vip.xiaonuo.client.modular.user.entity.ClientUser;
import vip.xiaonuo.client.modular.user.service.ClientUserService;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.mini.param.UserActivityParam;
import vip.xiaonuo.mini.param.CancelSignupParam;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

import vip.xiaonuo.biz.modular.activitycheckin.entity.VolActivityCheckin;
import vip.xiaonuo.biz.modular.activitycheckin.service.VolActivityCheckinService;

/**
 * 小程序活动接口控制器
 *
 * @author jetox
 * @date  2025/10/19 00:08
 */
@Slf4j
@Tag(name = "小程序活动接口")
@RestController
@Validated
public class ActivityController {

    @Resource
    private VolActivityService volActivityService;

    @Resource
    private VolActivitySignupService volActivitySignupService;

    @Resource
    private ClientUserService clientUserService;
    
    @Resource
    private VolActivityCheckinService volActivityCheckinService;

    /**
     * 获取志愿活动分页
     *
     * @author jetox
     * @date  2025/10/19 00:08
     */
    @Operation(summary = "获取志愿活动分页")
    @GetMapping("/mini/activity/page")
    public CommonResult<Page<VolActivity>> page(@Valid VolActivityPageParam volActivityPageParam) {
        Page<VolActivity> result = volActivityService.page(volActivityPageParam);
        return CommonResult.data(result);
    }

    /**
     * 获取志愿活动详情
     *
     * @author jetox
     * @date  2025/10/19 00:08
     */
    @Operation(summary = "获取志愿活动详情")
    @GetMapping("/mini/activity/detail")
    public CommonResult<VolActivity> detail(@Valid VolActivityIdParam volActivityIdParam) {
        VolActivity result = volActivityService.detail(volActivityIdParam);
        return CommonResult.data(result);
    }

    /**
     * 获取热门活动
     *
     * @author jetox
     * @date  2025/10/19 00:08
     */
    @Operation(summary = "获取热门活动")
    @GetMapping("/mini/activity/hot")
    public CommonResult<List<VolActivity>> getHotActivities(@RequestParam(defaultValue = "5") Integer limit) {
        VolActivityPageParam param = new VolActivityPageParam();
        param.setCurrent(1);
        param.setSize(limit);
        // 按报名人数降序排列获取热门活动
        param.setSortField("SIGNED_COUNT");
        param.setSortOrder("descend");
        
        Page<VolActivity> pageResult = volActivityService.page(param);
        return CommonResult.data(pageResult.getRecords());
    }

    /**
     * 获取最新活动
     *
     * @author jetox
     * @date  2025/10/19 00:08
     */
    @Operation(summary = "获取最新活动")
    @GetMapping("/mini/activity/latest")
    public CommonResult<List<VolActivity>> getLatestActivities(@RequestParam(defaultValue = "5") Integer limit) {
        VolActivityPageParam param = new VolActivityPageParam();
        param.setCurrent(1);
        param.setSize(limit);
        // 按创建时间降序排列获取最新活动
        param.setSortField("CREATE_TIME");
        param.setSortOrder("descend");
        
        Page<VolActivity> pageResult = volActivityService.page(param);
        return CommonResult.data(pageResult.getRecords());
    }

    /**
     * 根据状态获取活动
     *
     * @author jetox
     * @date  2025/10/19 00:08
     */
    @Operation(summary = "根据状态获取活动")
    @GetMapping("/mini/activity/status")
    public CommonResult<List<VolActivity>> getActivitiesByStatus(@RequestParam String status, @RequestParam(defaultValue = "5") Integer limit) {
        VolActivityPageParam param = new VolActivityPageParam();
        param.setCurrent(1);
        param.setSize(limit);
        param.setStatus(status);
        param.setSortField("CREATE_TIME");
        param.setSortOrder("descend");
        
        Page<VolActivity> pageResult = volActivityService.page(param);
        return CommonResult.data(pageResult.getRecords());
    }

    /**
     * 获取活动状态枚举
     *
     * @author jetox
     * @date  2025/10/19 00:08
     */
    @Operation(summary = "获取活动状态枚举")
    @GetMapping("/mini/activity/statusEnum")
    public CommonResult<java.util.Map<String, String>> getActivityStatusEnum() {
        java.util.Map<String, String> result = new java.util.HashMap<>();
        result.put("DRAFT", "草稿");
        result.put("RECRUITING", "报名中");
        result.put("ONGOING", "进行中");
        result.put("ENDED", "已结束");
        result.put("CANCELLED", "已取消");
        return CommonResult.data(result);
    }

    /**
     * 立即报名活动
     *
     * @author jetox
     * @date  2025/10/19 00:08
     */
    @Operation(summary = "立即报名活动")
    @PostMapping("/mini/activity/signup")
    public CommonResult<String> signupActivity(@RequestBody @Valid VolActivitySignupParam signupParam) {
        try {
            // 验证活动是否存在且状态为报名中
            VolActivity activity = volActivityService.queryEntity(signupParam.getActivityId());
            if (!"RECRUITING".equals(activity.getStatus())) {
                return CommonResult.error("活动不在报名状态");
            }
            
            // 检查报名是否已截止
            if (activity.getSignupDeadline() != null && activity.getSignupDeadline().before(new java.util.Date())) {
                return CommonResult.error("报名已截止");
            }
            
            // 确定实际报名用户ID
            String userId = signupParam.getActualUserId() != null ? signupParam.getActualUserId() : signupParam.getSignupUserId();
            
            // 检查是否已报名（排除已取消的记录）
            VolActivitySignup existingSignup = 
                volActivitySignupService.getOne(new LambdaQueryWrapper<VolActivitySignup>()
                    .eq(VolActivitySignup::getActivityId, signupParam.getActivityId())
                    .eq(VolActivitySignup::getUserId, userId)
                    .ne(VolActivitySignup::getStatus, "CANCELLED"));
            
            // 检查活动配置是否允许重复报名
            boolean allowResignup = false;
            if (activity.getExtJson() != null && !activity.getExtJson().isEmpty()) {
                try {
                    Map<String, Object> extInfo = cn.hutool.json.JSONUtil.toBean(activity.getExtJson(), Map.class);
                    allowResignup = Boolean.TRUE.equals(extInfo.get("allowResignup"));
                } catch (Exception e) {
                    // 解析失败，使用默认值
                    allowResignup = false;
                }
            }
            
            if (existingSignup != null && !allowResignup) {
                return CommonResult.error("该用户已报名此活动");
            }
            
            // 如果允许重复报名且存在已取消的记录，删除旧记录重新报名
            if (allowResignup && existingSignup != null) {
                volActivitySignupService.removeById(existingSignup.getId());
            }
            
            // 检查招募人数是否已满
            if (activity.getSignedCount() >= activity.getRecruitCount()) {
                return CommonResult.error("报名人数已满");
            }
            
            // 创建报名记录
            VolActivitySignup signup = new VolActivitySignup();
            signup.setId(cn.hutool.core.util.IdUtil.getSnowflakeNextIdStr());
            signup.setActivityId(signupParam.getActivityId());
            signup.setUserId(userId);
            signup.setSignupTime(new java.util.Date());
            signup.setStatus("SIGNED_UP");
            
            // 在扩展信息中记录报名操作者
            java.util.Map<String, Object> extInfo = new java.util.HashMap<>();
            extInfo.put("signupOperatorId", signupParam.getSignupUserId());
            if (signupParam.getActualUserId() != null) {
                extInfo.put("signupType", "GUARDIAN_FOR_VOLUNTEER"); // 监护人为小雷锋报名
            } else {
                extInfo.put("signupType", "SELF"); // 自己报名
            }
            signup.setExtJson(cn.hutool.json.JSONUtil.toJsonStr(extInfo));
            
            volActivitySignupService.save(signup);
            
            // 更新活动报名人数
            activity.setSignedCount(activity.getSignedCount() + 1);
            volActivityService.updateById(activity);
            
            return CommonResult.ok("报名成功");
        } catch (Exception e) {
            return CommonResult.error("报名失败：" + e.getMessage());
        }
    }

    /**
     * 批量报名活动
     *
     * @author jetox
     * @date  2025/10/20 06:12
     */
    @Operation(summary = "批量报名活动")
    @PostMapping("/mini/activity/batchSignup")
    public CommonResult<Map<String, Object>> batchSignupActivity(@RequestBody @Valid VolActivityBatchSignupParam batchSignupParam) {
        try {
            // 验证活动是否存在且状态为报名中
            VolActivity activity = volActivityService.queryEntity(batchSignupParam.getActivityId());
            if (!"RECRUITING".equals(activity.getStatus())) {
                return CommonResult.error("活动不在报名状态");
            }
            
            // 检查报名是否已截止
            if (activity.getSignupDeadline() != null && activity.getSignupDeadline().before(new java.util.Date())) {
                return CommonResult.error("报名已截止");
            }
            
            // 检查活动配置是否允许重复报名
            boolean allowResignup = false;
            if (activity.getExtJson() != null && !activity.getExtJson().isEmpty()) {
                try {
                    Map<String, Object> extInfo = cn.hutool.json.JSONUtil.toBean(activity.getExtJson(), Map.class);
                    allowResignup = Boolean.TRUE.equals(extInfo.get("allowResignup"));
                } catch (Exception e) {
                    // 解析失败，使用默认值
                    allowResignup = false;
                }
            }
            
            // 验证联系电话格式
            if (!batchSignupParam.getPhone().matches("^1[3-9]\\d{9}$")) {
                return CommonResult.error("联系电话格式不正确");
            }
            
            // 检查活动配置：同行人数是否占用招募名额
            boolean peerCountOccupiesQuota = true; // 默认占用
            if (activity.getExtJson() != null && !activity.getExtJson().isEmpty()) {
                try {
                    Map<String, Object> extInfo = cn.hutool.json.JSONUtil.toBean(activity.getExtJson(), Map.class);
                    peerCountOccupiesQuota = Boolean.TRUE.equals(extInfo.get("peerCountOccupiesQuota"));
                } catch (Exception e) {
                    // 解析失败，使用默认值
                    peerCountOccupiesQuota = true;
                }
            }
            
            // 检查招募人数是否足够（根据配置决定是否考虑同行人数）
            int totalSignedCount = activity.getSignedCount();
            int requestedCount = batchSignupParam.getActualUsers().size();
            int peerNumber = batchSignupParam.getPeerNumber() != null ? batchSignupParam.getPeerNumber() : 0;
            int totalRequestedCount = requestedCount + (peerCountOccupiesQuota ? peerNumber : 0);
            
            if (totalSignedCount + totalRequestedCount > activity.getRecruitCount()) {
                return CommonResult.error("报名人数不足，剩余名额：" + (activity.getRecruitCount() - totalSignedCount));
            }
            
            // 批量处理报名
            List<String> successUserIds = new ArrayList<>();
            List<Map<String, String>> failedUsers = new ArrayList<>();
            
            // 先批量检查所有用户的报名状态，避免重复查询
            List<String> userIds = batchSignupParam.getActualUsers().stream()
                .map(VolActivityBatchSignupParam.ActualUserItem::getUserId)
                .toList();
            
            Map<String, VolActivitySignup> existingSignupsMap = new HashMap<>();
            if (!userIds.isEmpty()) {
                // 查询所有用户的报名记录（MyBatis-Plus 自动排除逻辑删除的）
                List<VolActivitySignup> allSignups = 
                    volActivitySignupService.list(new LambdaQueryWrapper<VolActivitySignup>()
                        .eq(VolActivitySignup::getActivityId, batchSignupParam.getActivityId())
                        .in(VolActivitySignup::getUserId, userIds));
                
                log.info("查询到的报名记录数: {}", allSignups.size());
                
                for (VolActivitySignup signup : allSignups) {
                    existingSignupsMap.put(signup.getUserId(), signup);
                    log.info("报名记录: userId={}, status={}", signup.getUserId(), signup.getStatus());
                }
            }
            
            // 处理每个用户的报名
            
            for (VolActivityBatchSignupParam.ActualUserItem userItem : batchSignupParam.getActualUsers()) {
                try {
                    // 检查用户审核状态（只检查小雷锋类型）
                    ClientUser user = clientUserService.getById(userItem.getUserId());
                    if (user != null && "volunteer".equals(user.getType())) {
                        if (!"APPROVED".equals(user.getAuditStatus())) {
                            String statusText = switch (user.getAuditStatus()) {
                                case "PENDING" -> "待审核";
                                case "REJECTED" -> "审核未通过";
                                default -> "审核状态异常";
                            };
                            failedUsers.add(Map.of(
                                "userId", userItem.getUserId(),
                                "userName", userItem.getUserName() != null ? userItem.getUserName() : "未知",
                                "reason", "小雷锋" + statusText + "，无法参与活动"
                            ));
                            continue;
                        }
                    }
                    
                    VolActivitySignup existingSignup = existingSignupsMap.get(userItem.getUserId());
                    
                    // 如果已有记录（包括已取消的），复用该记录
                    VolActivitySignup signup;
                    if (existingSignup != null) {
                        // 检查是否允许重复报名
                        if ("SIGNED_UP".equals(existingSignup.getStatus()) && !allowResignup) {
                            // 已经是有效报名状态，且不允许重复报名
                            failedUsers.add(Map.of(
                                "userId", userItem.getUserId(),
                                "userName", userItem.getUserName() != null ? userItem.getUserName() : "未知",
                                "reason", "该用户已报名此活动"
                            ));
                            continue;
                        }
                        
                        // 复用已有记录（可能是已取消的，或者允许重复报名）
                        log.info("复用已有报名记录: userId={}, activityId={}, signupId={}, 原状态={}", 
                            userItem.getUserId(), batchSignupParam.getActivityId(), 
                            existingSignup.getId(), existingSignup.getStatus());
                        
                        signup = existingSignup;
                        signup.setStatus("SIGNED_UP");
                        signup.setSignupTime(new java.util.Date());
                        signup.setCancelTime(null);
                        signup.setCancelReason(null);
                    } else {
                        // 创建新的报名记录
                        log.info("创建新的报名记录: userId={}, activityId={}", 
                            userItem.getUserId(), batchSignupParam.getActivityId());
                        
                        signup = new VolActivitySignup();
                        signup.setId(cn.hutool.core.util.IdUtil.getSnowflakeNextIdStr());
                        signup.setActivityId(batchSignupParam.getActivityId());
                        signup.setUserId(userItem.getUserId());
                        signup.setSignupTime(new java.util.Date());
                        signup.setStatus("SIGNED_UP");
                    }
                    signup.setRemark(batchSignupParam.getRemark());
                    
                    // 在扩展信息中记录报名操作者和相关信息
                    java.util.Map<String, Object> extInfo = new java.util.HashMap<>();
                    extInfo.put("signupOperatorId", batchSignupParam.getSignupUserId());
                    extInfo.put("signupType", userItem.getUserType());
                    extInfo.put("needInsurance", batchSignupParam.getNeedInsurance());
                    extInfo.put("phone", batchSignupParam.getPhone());
                    if (batchSignupParam.getNeedInsurance() != null && batchSignupParam.getNeedInsurance()) {
                        extInfo.put("insuranceType", batchSignupParam.getInsuranceType());
                    }
                    if (batchSignupParam.getPeerNumber() != null && batchSignupParam.getPeerNumber() > 0) {
                        extInfo.put("peerNumber", batchSignupParam.getPeerNumber());
                    }
                    signup.setExtJson(cn.hutool.json.JSONUtil.toJsonStr(extInfo));
                    
                    // 保存或更新记录
                    if (existingSignup != null) {
                        // 更新已存在的记录
                        volActivitySignupService.updateById(signup);
                        log.info("更新报名记录成功: userId={}, signupId={}", userItem.getUserId(), signup.getId());
                    } else {
                        // 插入新记录
                        volActivitySignupService.save(signup);
                        log.info("创建新报名记录成功: userId={}, signupId={}", userItem.getUserId(), signup.getId());
                    }
                    
                    successUserIds.add(userItem.getUserId());
                    
                    // 更新活动报名人数
                    activity.setSignedCount(activity.getSignedCount() + 1);
                    
                } catch (Exception e) {
                    log.error("处理用户报名失败，用户ID: {}, 错误: {}", userItem.getUserId(), e.getMessage(), e);
                    failedUsers.add(Map.of(
                        "userId", userItem.getUserId(),
                        "userName", userItem.getUserName() != null ? userItem.getUserName() : "未知",
                        "reason", "报名处理失败，请稍后重试"
                    ));
                }
            }
            
            // 更新活动报名人数（已在循环中累加）
            if (!successUserIds.isEmpty()) {
                volActivityService.updateById(activity);
            }
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("successCount", successUserIds.size());
            result.put("failedCount", failedUsers.size());
            result.put("successUserIds", successUserIds);
            result.put("failedUsers", failedUsers);
            
            if (failedUsers.isEmpty()) {
                return CommonResult.data(result).setMsg("批量报名成功");
            } else if (successUserIds.isEmpty()) {
                return CommonResult.<Map<String, Object>>error("所有用户报名失败").setData(result);
            } else {
                return CommonResult.data(result).setMsg("部分用户报名成功");
            }
            
        } catch (Exception e) {
            log.error("批量报名失败: {}", e.getMessage(), e);
            return CommonResult.error("批量报名失败，请稍后重试");
        }
    }

    /**
     * 检查用户是否已报名活动
     *
     * @author jetox
     * @date  2025/10/19 00:08
     */
    @Operation(summary = "检查用户是否已报名活动")
    @GetMapping("/mini/activity/checkSignup")
    public CommonResult<Map<String, Object>> checkSignupStatus(@RequestParam String activityId, @RequestParam String userId) {
        try {
            log.info("=== 开始检查报名状态 ===");
            log.info("活动ID: {}, 用户ID: {}", activityId, userId);
            
            // 检查用户是否已报名（排除已取消的记录）
            VolActivitySignup signup = 
                volActivitySignupService.getOne(new LambdaQueryWrapper<VolActivitySignup>()
                    .eq(VolActivitySignup::getActivityId, activityId)
                    .eq(VolActivitySignup::getUserId, userId)
                    .ne(VolActivitySignup::getStatus, "CANCELLED"));
            
            log.info("用户自身报名记录: {}", signup != null ? signup.getId() : "null");
            
            // 检查监护人的所有小雷锋是否已报名
            List<String> signedVolunteerIds = new ArrayList<>();
            
            // 获取用户信息，判断是否为监护人
            ClientUser user = clientUserService.getById(userId);
            log.info("用户信息: ID={}, Type={}", user != null ? user.getId() : "null", user != null ? user.getType() : "null");
            
            if (user != null && ("GUARDIAN".equals(user.getType()) || "guardian".equalsIgnoreCase(user.getType()))) {
                log.info("用户是监护人，开始查找其报名的小雷锋...");
                
                // 如果是监护人，查找该活动中由该监护人报名的所有小雷锋
                LambdaQueryWrapper<VolActivitySignup> activitySignupsQuery = new LambdaQueryWrapper<>();
                activitySignupsQuery.eq(VolActivitySignup::getActivityId, activityId)
                                   .ne(VolActivitySignup::getStatus, "CANCELLED");
                List<VolActivitySignup> activitySignups = volActivitySignupService.list(activitySignupsQuery);
                
                log.info("该活动总报名记录数: {}", activitySignups.size());
                
                // 遍历所有报名记录，找出由当前监护人报名的
                for (VolActivitySignup activitySignup : activitySignups) {
                    log.info("检查报名记录: ID={}, UserId={}, ExtJson={}", 
                        activitySignup.getId(), 
                        activitySignup.getUserId(), 
                        activitySignup.getExtJson());
                    
                    if (activitySignup.getExtJson() != null && !activitySignup.getExtJson().isEmpty()) {
                        try {
                            Map<String, Object> extInfo = cn.hutool.json.JSONUtil.toBean(activitySignup.getExtJson(), Map.class);
                            String signupOperatorId = (String) extInfo.get("signupOperatorId");
                            
                            log.info("ExtJson解析成功: signupOperatorId={}, 当前监护人ID={}", signupOperatorId, userId);
                            
                            // 如果报名操作者是当前监护人，将小雷锋 ID 添加到列表
                            if (userId.equals(signupOperatorId)) {
                                log.info("匹配成功! 添加小雷锋ID: {}", activitySignup.getUserId());
                                signedVolunteerIds.add(activitySignup.getUserId());
                            } else {
                                log.info("signupOperatorId不匹配，跳过");
                            }
                        } catch (Exception e) {
                            log.error("ExtJson解析失败: {}", e.getMessage(), e);
                        }
                    } else {
                        log.info("ExtJson为空，跳过");
                    }
                }
                
                log.info("最终找到的已报名小雷锋ID列表: {}", signedVolunteerIds);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("isSignedUp", signup != null);
            result.put("signedVolunteerIds", signedVolunteerIds);
            result.put("signupInfo", signup);
            
            log.info("=== 检查报名状态完成 ===");
            log.info("返回结果: {}", cn.hutool.json.JSONUtil.toJsonStr(result));
            
            return CommonResult.data(result);
        } catch (Exception e) {
            log.error("检查报名状态异常: {}", e.getMessage(), e);
            return CommonResult.error("检查报名状态失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户活动列表
     *
     * @author jetox
     * @date  2025/10/19 00:08
     */
    @Operation(summary = "获取用户活动列表")
    @GetMapping("/mini/activity/userActivities")
    public CommonResult<Page<VolActivity>> getUserActivities(@Valid UserActivityParam param) {
        try {
            // 构建分页参数
            Page<VolActivity> page = new Page<>(param.getCurrent(), param.getSize());
            
            // 获取用户信息，判断用户类型
            ClientUser user = clientUserService.getById(param.getUserId());
            if (user == null) {
                return CommonResult.error("用户不存在");
            }
            
            List<String> activityIds = new ArrayList<>();
            
            // 根据用户类型查询不同的活动
            if ("guardian".equalsIgnoreCase(user.getType())) {
                // 监护人：查询作为操作者（signupOperatorId）报名的活动
                List<VolActivitySignup> allSignups = volActivitySignupService.list(
                    new LambdaQueryWrapper<VolActivitySignup>()
                        .ne(VolActivitySignup::getStatus, "CANCELLED")
                );
                
                for (VolActivitySignup signup : allSignups) {
                    if (signup.getExtJson() != null && !signup.getExtJson().isEmpty()) {
                        try {
                            Map extInfo = BeanUtil.copyProperties(signup.getExtJson(), Map.class);
                            String signupOperatorId = (String) extInfo.get("signupOperatorId");
                            if (param.getUserId().equals(signupOperatorId)) {
                                activityIds.add(signup.getActivityId());
                            }
                        } catch (Exception e) {
                            // 解析失败，跳过
                        }
                    }
                }
            } else {
                // 志愿者：查询自己报名的活动
                LambdaQueryWrapper<VolActivitySignup> signupQuery = new LambdaQueryWrapper<>();
                signupQuery.eq(VolActivitySignup::getUserId, param.getUserId())
                          .ne(VolActivitySignup::getStatus, "CANCELLED"); // 排除已取消的报名
                
                List<VolActivitySignup> signups = volActivitySignupService.list(signupQuery);
                for (VolActivitySignup signup : signups) {
                    activityIds.add(signup.getActivityId());
                }
            }
            
            if (activityIds.isEmpty()) {
                return CommonResult.data(new Page<>());
            }
            
            // 查询活动信息
            LambdaQueryWrapper<VolActivity> activityQuery = new LambdaQueryWrapper<>();
            activityQuery.in(VolActivity::getId, activityIds);
            
            // 如果指定了状态，添加状态筛选
            if (param.getStatus() != null && !param.getStatus().isEmpty()) {
                activityQuery.eq(VolActivity::getStatus, param.getStatus());
            }
            
            // 按创建时间降序排列
            activityQuery.orderByDesc(VolActivity::getCreateTime);
            
            Page<VolActivity> result = volActivityService.page(page, activityQuery);
            
            // 为每个活动添加报名信息
            for (VolActivity activity : result.getRecords()) {
                Map<String, Object> extInfo = new HashMap<>();
                
                // 获取该活动中用户报名的所有志愿者信息
                List<Map<String, Object>> signedVolunteers = new ArrayList<>();
                
                if ("guardian".equalsIgnoreCase(user.getType())) {
                    // 监护人：查找该活动中由该监护人报名的所有志愿者
                    LambdaQueryWrapper<VolActivitySignup> activitySignupsQuery = new LambdaQueryWrapper<>();
                    activitySignupsQuery.eq(VolActivitySignup::getActivityId, activity.getId())
                                       .ne(VolActivitySignup::getStatus, "CANCELLED");
                    List<VolActivitySignup> activitySignups = volActivitySignupService.list(activitySignupsQuery);
                    
                    for (VolActivitySignup signup : activitySignups) {
                        // 检查是否由当前监护人报名
                        if (signup.getExtJson() != null && !signup.getExtJson().isEmpty()) {
                            try {
                                Map<String, Object> signupExtInfo = cn.hutool.json.JSONUtil.toBean(signup.getExtJson(), Map.class);
                                String signupOperatorId = (String) signupExtInfo.get("signupOperatorId");
                                if (param.getUserId().equals(signupOperatorId)) {
                                    // 获取志愿者信息
                                    ClientUser volunteer = clientUserService.getById(signup.getUserId());
                                    if (volunteer != null) {
                                        Map<String, Object> volunteerInfo = new HashMap<>();
                                        volunteerInfo.put("userId", volunteer.getId());
                                        volunteerInfo.put("userName", volunteer.getName());
                                        volunteerInfo.put("avatar", volunteer.getAvatar());
                                        volunteerInfo.put("signupStatus", signup.getStatus());
                                        volunteerInfo.put("signupTime", signup.getSignupTime());
                                        volunteerInfo.put("checkInTime", signup.getCheckInTime());
                                        volunteerInfo.put("serviceHoursActual", signup.getServiceHoursActual());
                                        signedVolunteers.add(volunteerInfo);
                                    }
                                }
                            } catch (Exception e) {
                                // 解析失败，跳过
                            }
                        }
                    }
                } else {
                    // 志愿者：查找自己的报名记录
                    LambdaQueryWrapper<VolActivitySignup> userSignupQuery = new LambdaQueryWrapper<>();
                    userSignupQuery.eq(VolActivitySignup::getActivityId, activity.getId())
                                  .eq(VolActivitySignup::getUserId, param.getUserId())
                                  .ne(VolActivitySignup::getStatus, "CANCELLED");
                    VolActivitySignup userSignup = volActivitySignupService.getOne(userSignupQuery);
                    
                    if (userSignup != null) {
                        Map<String, Object> volunteerInfo = new HashMap<>();
                        volunteerInfo.put("userId", user.getId());
                        volunteerInfo.put("userName", user.getName());
                        volunteerInfo.put("avatar", user.getAvatar());
                        volunteerInfo.put("signupStatus", userSignup.getStatus());
                        volunteerInfo.put("signupTime", userSignup.getSignupTime());
                        volunteerInfo.put("checkInTime", userSignup.getCheckInTime());
                        volunteerInfo.put("serviceHoursActual", userSignup.getServiceHoursActual());
                        signedVolunteers.add(volunteerInfo);
                    }
                }
                
                // 将报名的志愿者信息添加到扩展信息中
                extInfo.put("signedVolunteers", signedVolunteers);
                activity.setExtJson(cn.hutool.json.JSONUtil.toJsonStr(extInfo));
            }
            
            return CommonResult.data(result);
        } catch (Exception e) {
            return CommonResult.error("获取用户活动列表失败：" + e.getMessage());
        }
    }

    /**
     * 取消报名
     *
     * @author jetox
     * @date  2025/10/19 00:08
     */
    @Operation(summary = "取消报名")
    @PostMapping("/mini/activity/cancelSignup")
    public CommonResult<String> cancelSignup(@RequestBody @Valid CancelSignupParam param) {
        try {
            // 查找报名记录
            LambdaQueryWrapper<VolActivitySignup> signupQuery = new LambdaQueryWrapper<>();
            signupQuery.eq(VolActivitySignup::getActivityId, param.getActivityId())
                      .eq(VolActivitySignup::getUserId, param.getUserId())
                      .ne(VolActivitySignup::getStatus, "CANCELLED");
            
            VolActivitySignup signup = volActivitySignupService.getOne(signupQuery);
            if (signup == null) {
                return CommonResult.error("未找到报名记录");
            }
            
            // 检查活动状态，只有报名中的活动才能取消报名
            VolActivity activity = volActivityService.getById(param.getActivityId());
            if (activity == null) {
                return CommonResult.error("活动不存在");
            }
            
            if (!"RECRUITING".equals(activity.getStatus())) {
                return CommonResult.error("活动不在报名状态，无法取消报名");
            }
            
            // 检查报名截止时间
            if (activity.getSignupDeadline() != null && activity.getSignupDeadline().before(new java.util.Date())) {
                return CommonResult.error("报名已截止，无法取消报名");
            }
            
            // 更新报名状态
            signup.setStatus("CANCELLED");
            signup.setCancelTime(new java.util.Date());
            signup.setCancelReason("用户主动取消");
            
            volActivitySignupService.updateById(signup);
            
            // 更新活动报名人数
            if (activity.getSignedCount() > 0) {
                activity.setSignedCount(activity.getSignedCount() - 1);
                volActivityService.updateById(activity);
            }
            
            return CommonResult.ok("取消报名成功");
        } catch (Exception e) {
            return CommonResult.error("取消报名失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取签到候选人列表
     * 查询该监护人为哪些志愿者报名了此活动
     *
     * @author jetox
     * @date  2025/10/22 00:00
     */
    @Operation(summary = "获取签到候选人列表")
    @GetMapping("/mini/checkin/candidates")
    public CommonResult<Map<String, Object>> getCheckinCandidates(
            @RequestParam String activityId,
            @RequestParam String operatorId) {
        try {
            // 查询活动信息
            VolActivity activity = volActivityService.getById(activityId);
            if (activity == null) {
                return CommonResult.error("活动不存在");
            }
            
            // 查询该监护人为哪些人报名了此活动
            List<VolActivitySignup> allSignups = volActivitySignupService.list(
                new LambdaQueryWrapper<VolActivitySignup>()
                    .eq(VolActivitySignup::getActivityId, activityId)
                    .ne(VolActivitySignup::getStatus, "CANCELLED")
            );
            
            List<Map<String, Object>> candidates = new ArrayList<>();
            for (VolActivitySignup signup : allSignups) {
                // 检查是否由该监护人报名
                if (signup.getExtJson() != null && !signup.getExtJson().isEmpty()) {
                    try {
                        Map<String, Object> extInfo = cn.hutool.json.JSONUtil.toBean(signup.getExtJson(), Map.class);
                        String signupOperatorId = (String) extInfo.get("signupOperatorId");
                        
                        if (operatorId.equals(signupOperatorId)) {
                            // 获取志愿者信息
                            ClientUser user = clientUserService.getById(signup.getUserId());
                            if (user != null) {
                                Map<String, Object> candidate = new HashMap<>();
                                candidate.put("signupId", signup.getId());
                                candidate.put("userId", user.getId());
                                candidate.put("userName", user.getName());
                                candidate.put("avatar", user.getAvatar());
                                candidate.put("isCheckedIn", signup.getCheckInTime() != null);
                                candidate.put("checkInTime", signup.getCheckInTime());
                                candidates.add(candidate);
                            }
                        }
                    } catch (Exception e) {
                        // 解析失败，跳过
                    }
                }
            }
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            
            // 活动信息
            Map<String, Object> activityInfo = new HashMap<>();
            activityInfo.put("id", activity.getId());
            activityInfo.put("title", activity.getTitle());
            activityInfo.put("activityDate", activity.getActivityDate());
            activityInfo.put("startTime", activity.getStartTime());
            activityInfo.put("endTime", activity.getEndTime());
            activityInfo.put("location", activity.getLocation());
            activityInfo.put("serviceHours", activity.getServiceHours());
            
            result.put("activity", activityInfo);
            result.put("candidates", candidates);
            
            return CommonResult.data(result);
        } catch (Exception e) {
            return CommonResult.error("获取签到候选人列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量签到
     *
     * @author jetox
     * @date  2025/10/22 00:00
     */
    @Operation(summary = "批量签到")
    @PostMapping("/mini/checkin/batch")
    public CommonResult<Map<String, Object>> batchCheckin(@RequestBody Map<String, Object> params) {
        try {
            String activityId = (String) params.get("activityId");
            String operatorId = (String) params.get("operatorId");
            @SuppressWarnings("unchecked")
            List<String> signupIds = (List<String>) params.get("signupIds");
            @SuppressWarnings("unchecked")
            Map<String, Object> locationData = (Map<String, Object>) params.get("location");
            
            if (signupIds == null || signupIds.isEmpty()) {
                return CommonResult.error("请选择签到人员");
            }
            
            // 查询活动
            VolActivity activity = volActivityService.getById(activityId);
            if (activity == null) {
                return CommonResult.error("活动不存在");
            }
            
            int successCount = 0;
            int failedCount = 0;
            List<Map<String, Object>> results = new ArrayList<>();
            
            for (String signupId : signupIds) {
                Map<String, Object> result = new HashMap<>();
                result.put("signupId", signupId);
                
                try {
                    // 查询报名记录
                    VolActivitySignup signup = volActivitySignupService.getById(signupId);
                    if (signup == null) {
                        result.put("success", false);
                        result.put("message", "报名记录不存在");
                        failedCount++;
                        results.add(result);
                        continue;
                    }
                    
                    // 检查是否已签到
                    if (signup.getCheckInTime() != null) {
                        result.put("success", false);
                        result.put("message", "已经签到，无需重复签到");
                        failedCount++;
                        results.add(result);
                        continue;
                    }
                    
                    // 创建签到记录
                    VolActivityCheckin checkin = new VolActivityCheckin();
                    checkin.setId(cn.hutool.core.util.IdUtil.getSnowflakeNextIdStr());
                    checkin.setActivityId(activityId);
                    checkin.setUserId(signup.getUserId());
                    checkin.setSignupId(signupId);
                    checkin.setCheckinType("CHECKIN");
                    checkin.setCheckinTime(new Date());
                    checkin.setStatus("NORMAL");
                    
                    // 初始化签退相关字段（等待自动签退）
                    checkin.setCheckoutType("AUTO"); // 默认自动签退
                    checkin.setIsEarlyLeave(0); // 默认不提前离开
                    checkin.setActualHours(activity.getServiceHours()); // 预设为活动设定时长
                    
                    // 设置地理位置（如果提供）
                    if (locationData != null) {
                        checkin.setLocation((String) locationData.get("address"));
                        if (locationData.get("latitude") != null) {
                            checkin.setLocationLat(new java.math.BigDecimal(locationData.get("latitude").toString()));
                        }
                        if (locationData.get("longitude") != null) {
                            checkin.setLocationLng(new java.math.BigDecimal(locationData.get("longitude").toString()));
                        }
                    }
                    
                    volActivityCheckinService.save(checkin);
                    
                    // 更新报名记录
                    signup.setCheckInTime(new Date());
                    signup.setStatus("ATTENDED");
                    volActivitySignupService.updateById(signup);
                    
                    result.put("success", true);
                    result.put("message", "签到成功");
                    successCount++;
                    
                } catch (Exception e) {
                    result.put("success", false);
                    result.put("message", "签到失败：" + e.getMessage());
                    failedCount++;
                }
                
                results.add(result);
            }
            
            // 构建返回结果
            Map<String, Object> response = new HashMap<>();
            response.put("successCount", successCount);
            response.put("failedCount", failedCount);
            response.put("results", results);
            
            return CommonResult.data(response);
            
        } catch (Exception e) {
            return CommonResult.error("批量签到失败：" + e.getMessage());
        }
    }
}