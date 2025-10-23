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
package vip.xiaonuo.biz.modular.feedbackreply.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.biz.modular.feedbackreply.entity.BizFeedbackReply;
import vip.xiaonuo.biz.modular.feedbackreply.param.BizFeedbackReplyAddParam;
import vip.xiaonuo.biz.modular.feedbackreply.param.BizFeedbackReplyEditParam;
import vip.xiaonuo.biz.modular.feedbackreply.param.BizFeedbackReplyIdParam;
import vip.xiaonuo.biz.modular.feedbackreply.param.BizFeedbackReplyPageParam;
import vip.xiaonuo.biz.modular.feedbackreply.service.BizFeedbackReplyService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 反馈回复控制器
 *
 * @author jetox
 * @date  2025/10/19 00:15
 */
@Tag(name = "反馈回复控制器")
@RestController
@Validated
public class BizFeedbackReplyController {

    @Resource
    private BizFeedbackReplyService bizFeedbackReplyService;

    /**
     * 获取反馈回复分页
     *
     * @author jetox
     * @date  2025/10/19 00:15
     */
    @Operation(summary = "获取反馈回复分页")
    @SaCheckPermission("/biz/feedbackreply/page")
    @GetMapping("/biz/feedbackreply/page")
    public CommonResult<Page<BizFeedbackReply>> page(BizFeedbackReplyPageParam bizFeedbackReplyPageParam) {
        return CommonResult.data(bizFeedbackReplyService.page(bizFeedbackReplyPageParam));
    }

    /**
     * 添加反馈回复
     *
     * @author jetox
     * @date  2025/10/19 00:15
     */
    @Operation(summary = "添加反馈回复")
    @CommonLog("添加反馈回复")
    @SaCheckPermission("/biz/feedbackreply/add")
    @PostMapping("/biz/feedbackreply/add")
    public CommonResult<String> add(@RequestBody @Valid BizFeedbackReplyAddParam bizFeedbackReplyAddParam) {
        bizFeedbackReplyService.add(bizFeedbackReplyAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑反馈回复
     *
     * @author jetox
     * @date  2025/10/19 00:15
     */
    @Operation(summary = "编辑反馈回复")
    @CommonLog("编辑反馈回复")
    @SaCheckPermission("/biz/feedbackreply/edit")
    @PostMapping("/biz/feedbackreply/edit")
    public CommonResult<String> edit(@RequestBody @Valid BizFeedbackReplyEditParam bizFeedbackReplyEditParam) {
        bizFeedbackReplyService.edit(bizFeedbackReplyEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除反馈回复
     *
     * @author jetox
     * @date  2025/10/19 00:15
     */
    @Operation(summary = "删除反馈回复")
    @CommonLog("删除反馈回复")
    @SaCheckPermission("/biz/feedbackreply/delete")
    @PostMapping("/biz/feedbackreply/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<BizFeedbackReplyIdParam> bizFeedbackReplyIdParamList) {
        bizFeedbackReplyService.delete(bizFeedbackReplyIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取反馈回复详情
     *
     * @author jetox
     * @date  2025/10/19 00:15
     */
    @Operation(summary = "获取反馈回复详情")
    @SaCheckPermission("/biz/feedbackreply/detail")
    @GetMapping("/biz/feedbackreply/detail")
    public CommonResult<BizFeedbackReply> detail(@Valid BizFeedbackReplyIdParam bizFeedbackReplyIdParam) {
        return CommonResult.data(bizFeedbackReplyService.detail(bizFeedbackReplyIdParam));
    }

    /**
     * 下载反馈回复导入模板
     *
     * @author jetox
     * @date  2025/10/19 00:15
     */
    @Operation(summary = "下载反馈回复导入模板")
    @GetMapping(value = "/biz/feedbackreply/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        bizFeedbackReplyService.downloadImportTemplate(response);
    }

    /**
     * 导入反馈回复
     *
     * @author jetox
     * @date  2025/10/19 00:15
     */
    @Operation(summary = "导入反馈回复")
    @CommonLog("导入反馈回复")
    @SaCheckPermission("/biz/feedbackreply/importData")
    @PostMapping("/biz/feedbackreply/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(bizFeedbackReplyService.importData(file));
    }

    /**
     * 导出反馈回复
     *
     * @author jetox
     * @date  2025/10/19 00:15
     */
    @Operation(summary = "导出反馈回复")
    @SaCheckPermission("/biz/feedbackreply/exportData")
    @PostMapping(value = "/biz/feedbackreply/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<BizFeedbackReplyIdParam> bizFeedbackReplyIdParamList, HttpServletResponse response) throws IOException {
        bizFeedbackReplyService.exportData(bizFeedbackReplyIdParamList, response);
    }
}
