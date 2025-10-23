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
package vip.xiaonuo.biz.modular.activitysignup.controller;

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
import vip.xiaonuo.biz.modular.activitysignup.entity.VolActivitySignup;
import vip.xiaonuo.biz.modular.activitysignup.param.VolActivitySignupAddParam;
import vip.xiaonuo.biz.modular.activitysignup.param.VolActivitySignupEditParam;
import vip.xiaonuo.biz.modular.activitysignup.param.VolActivitySignupIdParam;
import vip.xiaonuo.biz.modular.activitysignup.param.VolActivitySignupPageParam;
import vip.xiaonuo.biz.modular.activitysignup.service.VolActivitySignupService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 报名记录控制器
 *
 * @author jetox
 * @date  2025/10/19 00:11
 */
@Tag(name = "报名记录控制器")
@RestController
@Validated
public class VolActivitySignupController {

    @Resource
    private VolActivitySignupService volActivitySignupService;

    /**
     * 获取报名记录分页
     *
     * @author jetox
     * @date  2025/10/19 00:11
     */
    @Operation(summary = "获取报名记录分页")
    @SaCheckPermission("/biz/activitysignup/page")
    @GetMapping("/biz/activitysignup/page")
    public CommonResult<Page<VolActivitySignup>> page(VolActivitySignupPageParam volActivitySignupPageParam) {
        return CommonResult.data(volActivitySignupService.page(volActivitySignupPageParam));
    }

    /**
     * 添加报名记录
     *
     * @author jetox
     * @date  2025/10/19 00:11
     */
    @Operation(summary = "添加报名记录")
    @CommonLog("添加报名记录")
    @SaCheckPermission("/biz/activitysignup/add")
    @PostMapping("/biz/activitysignup/add")
    public CommonResult<String> add(@RequestBody @Valid VolActivitySignupAddParam volActivitySignupAddParam) {
        volActivitySignupService.add(volActivitySignupAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑报名记录
     *
     * @author jetox
     * @date  2025/10/19 00:11
     */
    @Operation(summary = "编辑报名记录")
    @CommonLog("编辑报名记录")
    @SaCheckPermission("/biz/activitysignup/edit")
    @PostMapping("/biz/activitysignup/edit")
    public CommonResult<String> edit(@RequestBody @Valid VolActivitySignupEditParam volActivitySignupEditParam) {
        volActivitySignupService.edit(volActivitySignupEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除报名记录
     *
     * @author jetox
     * @date  2025/10/19 00:11
     */
    @Operation(summary = "删除报名记录")
    @CommonLog("删除报名记录")
    @SaCheckPermission("/biz/activitysignup/delete")
    @PostMapping("/biz/activitysignup/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<VolActivitySignupIdParam> volActivitySignupIdParamList) {
        volActivitySignupService.delete(volActivitySignupIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取报名记录详情
     *
     * @author jetox
     * @date  2025/10/19 00:11
     */
    @Operation(summary = "获取报名记录详情")
    @SaCheckPermission("/biz/activitysignup/detail")
    @GetMapping("/biz/activitysignup/detail")
    public CommonResult<VolActivitySignup> detail(@Valid VolActivitySignupIdParam volActivitySignupIdParam) {
        return CommonResult.data(volActivitySignupService.detail(volActivitySignupIdParam));
    }

    /**
     * 下载报名记录导入模板
     *
     * @author jetox
     * @date  2025/10/19 00:11
     */
    @Operation(summary = "下载报名记录导入模板")
    @GetMapping(value = "/biz/activitysignup/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        volActivitySignupService.downloadImportTemplate(response);
    }

    /**
     * 导入报名记录
     *
     * @author jetox
     * @date  2025/10/19 00:11
     */
    @Operation(summary = "导入报名记录")
    @CommonLog("导入报名记录")
    @SaCheckPermission("/biz/activitysignup/importData")
    @PostMapping("/biz/activitysignup/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(volActivitySignupService.importData(file));
    }

    /**
     * 导出报名记录
     *
     * @author jetox
     * @date  2025/10/19 00:11
     */
    @Operation(summary = "导出报名记录")
    @SaCheckPermission("/biz/activitysignup/exportData")
    @PostMapping(value = "/biz/activitysignup/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<VolActivitySignupIdParam> volActivitySignupIdParamList, HttpServletResponse response) throws IOException {
        volActivitySignupService.exportData(volActivitySignupIdParamList, response);
    }
}
