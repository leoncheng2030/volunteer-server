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

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vip.xiaonuo.auth.core.util.StpClientLoginUserUtil;
import vip.xiaonuo.biz.modular.feedback.entity.BizFeedback;
import vip.xiaonuo.biz.modular.feedback.param.BizFeedbackAddParam;
import vip.xiaonuo.biz.modular.feedback.param.BizFeedbackIdParam;
import vip.xiaonuo.biz.modular.feedback.param.BizFeedbackPageParam;
import vip.xiaonuo.biz.modular.feedback.service.BizFeedbackService;
import vip.xiaonuo.biz.modular.feedbackreply.entity.BizFeedbackReply;
import vip.xiaonuo.biz.modular.feedbackreply.service.BizFeedbackReplyService;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.mini.param.SatisfactionParam;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 小程序反馈接口控制器
 *
 * @author jetox
 * @date  2025/10/23 00:00
 */
@Slf4j
@Tag(name = "小程序反馈接口")
@RestController
@RequestMapping("/mini/feedback")
@Validated
public class FeedbackController {

    @Resource
    private BizFeedbackService bizFeedbackService;
    
    @Resource
    private BizFeedbackReplyService bizFeedbackReplyService;

    /**
     * 获取用户反馈分页（仅查询当前登录用户的反馈）
     *
     * @author jetox
     * @date  2025/10/23 00:00
     */
    @Operation(summary = "获取用户反馈分页")
    @GetMapping("/page")
    public CommonResult<Page<BizFeedback>> page(@Valid BizFeedbackPageParam bizFeedbackPageParam) {
        // 获取当前登录用户ID
        String currentUserId = StpClientLoginUserUtil.getClientLoginUser().getId();
        
        // 只查询当前用户的反馈
        bizFeedbackPageParam.setUserId(currentUserId);
        
        // 按创建时间降序排列
        if (StrUtil.isEmpty(bizFeedbackPageParam.getSortField())) {
            bizFeedbackPageParam.setSortField("CREATE_TIME");
            bizFeedbackPageParam.setSortOrder("descend");
        }
        
        Page<BizFeedback> result = bizFeedbackService.page(bizFeedbackPageParam);
        return CommonResult.data(result);
    }

    /**
     * 提交反馈
     *
     * @author jetox
     * @date  2025/10/23 00:00
     */
    @Operation(summary = "提交反馈")
    @PostMapping("/add")
    public CommonResult<String> add(@RequestBody @Valid BizFeedbackAddParam bizFeedbackAddParam) {
        try {
            // 获取当前登录用户ID
            String currentUserId = StpClientLoginUserUtil.getClientLoginUser().getId();
            
            // 设置用户ID
            bizFeedbackAddParam.setUserId(currentUserId);
            
            // 如果没有传入标题，使用内容前50个字符作为标题
            if (StrUtil.isEmpty(bizFeedbackAddParam.getTitle()) && StrUtil.isNotEmpty(bizFeedbackAddParam.getContent())) {
                String title = bizFeedbackAddParam.getContent();
                if (title.length() > 50) {
                    title = title.substring(0, 50) + "...";
                }
                bizFeedbackAddParam.setTitle(title);
            }
            
            // 设置默认状态为待处理
            if (StrUtil.isEmpty(bizFeedbackAddParam.getStatus())) {
                bizFeedbackAddParam.setStatus("PENDING");
            }
            
            // 设置默认优先级为普通
            if (StrUtil.isEmpty(bizFeedbackAddParam.getPriority())) {
                bizFeedbackAddParam.setPriority("NORMAL");
            }
            
            // 设置用户类型为志愿者（从客户端提交的默认为志愿者）
            if (StrUtil.isEmpty(bizFeedbackAddParam.getUserType())) {
                bizFeedbackAddParam.setUserType("VOLUNTEER");
            }
            
            bizFeedbackService.add(bizFeedbackAddParam);
            return CommonResult.ok();
        } catch (Exception e) {
            log.error("提交反馈失败", e);
            return CommonResult.error("提交反馈失败：" + e.getMessage());
        }
    }

    /**
     * 获取反馈详情
     *
     * @author jetox
     * @date  2025/10/23 00:00
     */
    @Operation(summary = "获取反馈详情")
    @GetMapping("/detail")
    public CommonResult<BizFeedback> detail(@Valid BizFeedbackIdParam bizFeedbackIdParam) {
        // 获取当前登录用户ID
        String currentUserId = StpClientLoginUserUtil.getClientLoginUser().getId();
        
        BizFeedback feedback = bizFeedbackService.detail(bizFeedbackIdParam);
        
        // 验证反馈是否属于当前用户
        if (!currentUserId.equals(feedback.getUserId())) {
            return CommonResult.error("无权查看此反馈");
        }
        
        return CommonResult.data(feedback);
    }

    /**
     * 满意度评价
     *
     * @author jetox
     * @date  2025/10/23 00:00
     */
    @Operation(summary = "满意度评价")
    @PostMapping("/satisfaction")
    public CommonResult<String> satisfaction(@RequestBody @Valid SatisfactionParam satisfactionParam) {
        try {
            // 获取当前登录用户ID
            String currentUserId = StpClientLoginUserUtil.getClientLoginUser().getId();
            
            // 获取反馈详情
            BizFeedback feedback = bizFeedbackService.queryEntity(satisfactionParam.getId());
            
            if (ObjectUtil.isNull(feedback)) {
                return CommonResult.error("反馈不存在");
            }
            
            // 验证反馈是否属于当前用户
            if (!currentUserId.equals(feedback.getUserId())) {
                return CommonResult.error("无权评价此反馈");
            }
            
            // 只有已解决的反馈才能评价
            if (!"RESOLVED".equals(feedback.getStatus())) {
                return CommonResult.error("只有已解决的反馈才能评价");
            }
            
            // 更新满意度和评价
            feedback.setSatisfaction(satisfactionParam.getSatisfaction());
            feedback.setEvaluation(satisfactionParam.getEvaluation());
            
            bizFeedbackService.updateById(feedback);
            
            return CommonResult.ok();
        } catch (Exception e) {
            log.error("满意度评价失败", e);
            return CommonResult.error("满意度评价失败：" + e.getMessage());
        }
    }

    /**
     * 获取反馈回复列表（仅返回非内部回复）
     *
     * @author jetox
     * @date  2025/10/23 00:00
     */
    @Operation(summary = "获取反馈回复列表")
    @GetMapping("/reply/list")
    public CommonResult<List<BizFeedbackReply>> replyList(@RequestParam String feedbackId) {
        try {
            // 获取当前登录用户ID
            String currentUserId = StpClientLoginUserUtil.getClientLoginUser().getId();
            
            // 验证反馈是否属于当前用户
            BizFeedback feedback = bizFeedbackService.queryEntity(feedbackId);
            if (ObjectUtil.isNull(feedback)) {
                return CommonResult.error("反馈不存在");
            }
            
            if (!currentUserId.equals(feedback.getUserId())) {
                return CommonResult.error("无权查看此反馈的回复");
            }
            
            // 查询所有非内部回复（isInternal = 0）
            List<BizFeedbackReply> allReplies = bizFeedbackReplyService.lambdaQuery()
                .eq(BizFeedbackReply::getFeedbackId, feedbackId)
                .eq(BizFeedbackReply::getIsInternal, 0)
                .orderByAsc(BizFeedbackReply::getCreateTime)
                .list();
            
            return CommonResult.data(allReplies);
        } catch (Exception e) {
            log.error("获取回复列表失败", e);
            return CommonResult.error("获取回复列表失败：" + e.getMessage());
        }
    }
}
