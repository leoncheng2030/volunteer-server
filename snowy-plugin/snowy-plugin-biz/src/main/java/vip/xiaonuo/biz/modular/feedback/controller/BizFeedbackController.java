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
package vip.xiaonuo.biz.modular.feedback.controller;

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
import vip.xiaonuo.biz.modular.feedback.entity.BizFeedback;
import vip.xiaonuo.biz.modular.feedback.param.BizFeedbackAddParam;
import vip.xiaonuo.biz.modular.feedback.param.BizFeedbackEditParam;
import vip.xiaonuo.biz.modular.feedback.param.BizFeedbackIdParam;
import vip.xiaonuo.biz.modular.feedback.param.BizFeedbackPageParam;
import vip.xiaonuo.biz.modular.feedback.service.BizFeedbackService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 用户反馈控制器
 *
 * @author jetox
 * @date  2025/10/19 00:13
 */
@Tag(name = "用户反馈控制器")
@RestController
@Validated
public class BizFeedbackController {

    @Resource
    private BizFeedbackService bizFeedbackService;

    /**
     * 获取用户反馈分页
     *
     * @author jetox
     * @date  2025/10/19 00:13
     */
    @Operation(summary = "获取用户反馈分页")
    @SaCheckPermission("/biz/feedback/page")
    @GetMapping("/biz/feedback/page")
    public CommonResult<Page<BizFeedback>> page(BizFeedbackPageParam bizFeedbackPageParam) {
        return CommonResult.data(bizFeedbackService.page(bizFeedbackPageParam));
    }

    /**
     * 添加用户反馈
     *
     * @author jetox
     * @date  2025/10/19 00:13
     */
    @Operation(summary = "添加用户反馈")
    @CommonLog("添加用户反馈")
    @SaCheckPermission("/biz/feedback/add")
    @PostMapping("/biz/feedback/add")
    public CommonResult<String> add(@RequestBody @Valid BizFeedbackAddParam bizFeedbackAddParam) {
        bizFeedbackService.add(bizFeedbackAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑用户反馈
     *
     * @author jetox
     * @date  2025/10/19 00:13
     */
    @Operation(summary = "编辑用户反馈")
    @CommonLog("编辑用户反馈")
    @SaCheckPermission("/biz/feedback/edit")
    @PostMapping("/biz/feedback/edit")
    public CommonResult<String> edit(@RequestBody @Valid BizFeedbackEditParam bizFeedbackEditParam) {
        bizFeedbackService.edit(bizFeedbackEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除用户反馈
     *
     * @author jetox
     * @date  2025/10/19 00:13
     */
    @Operation(summary = "删除用户反馈")
    @CommonLog("删除用户反馈")
    @SaCheckPermission("/biz/feedback/delete")
    @PostMapping("/biz/feedback/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<BizFeedbackIdParam> bizFeedbackIdParamList) {
        bizFeedbackService.delete(bizFeedbackIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取用户反馈详情
     *
     * @author jetox
     * @date  2025/10/19 00:13
     */
    @Operation(summary = "获取用户反馈详情")
    @SaCheckPermission("/biz/feedback/detail")
    @GetMapping("/biz/feedback/detail")
    public CommonResult<BizFeedback> detail(@Valid BizFeedbackIdParam bizFeedbackIdParam) {
        return CommonResult.data(bizFeedbackService.detail(bizFeedbackIdParam));
    }

    /**
     * 下载用户反馈导入模板
     *
     * @author jetox
     * @date  2025/10/19 00:13
     */
    @Operation(summary = "下载用户反馈导入模板")
    @GetMapping(value = "/biz/feedback/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        bizFeedbackService.downloadImportTemplate(response);
    }

    /**
     * 导入用户反馈
     *
     * @author jetox
     * @date  2025/10/19 00:13
     */
    @Operation(summary = "导入用户反馈")
    @CommonLog("导入用户反馈")
    @SaCheckPermission("/biz/feedback/importData")
    @PostMapping("/biz/feedback/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(bizFeedbackService.importData(file));
    }

    /**
     * 导出用户反馈
     *
     * @author jetox
     * @date  2025/10/19 00:13
     */
    @Operation(summary = "导出用户反馈")
    @SaCheckPermission("/biz/feedback/exportData")
    @PostMapping(value = "/biz/feedback/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<BizFeedbackIdParam> bizFeedbackIdParamList, HttpServletResponse response) throws IOException {
        bizFeedbackService.exportData(bizFeedbackIdParamList, response);
    }
}
