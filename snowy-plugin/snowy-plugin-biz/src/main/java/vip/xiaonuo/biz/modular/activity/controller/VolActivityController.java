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
package vip.xiaonuo.biz.modular.activity.controller;

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
import vip.xiaonuo.biz.modular.activity.entity.VolActivity;
import vip.xiaonuo.biz.modular.activity.param.VolActivityAddParam;
import vip.xiaonuo.biz.modular.activity.param.VolActivityEditParam;
import vip.xiaonuo.biz.modular.activity.param.VolActivityIdParam;
import vip.xiaonuo.biz.modular.activity.param.VolActivityPageParam;
import vip.xiaonuo.biz.modular.activity.service.VolActivityService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 志愿活动控制器
 *
 * @author jetox
 * @date  2025/10/19 00:08
 */
@Tag(name = "志愿活动控制器")
@RestController
@Validated
public class VolActivityController {

    @Resource
    private VolActivityService volActivityService;

    /**
     * 获取志愿活动分页
     *
     * @author jetox
     * @date  2025/10/19 00:08
     */
    @Operation(summary = "获取志愿活动分页")
    @SaCheckPermission("/biz/activity/page")
    @GetMapping("/biz/activity/page")
    public CommonResult<Page<VolActivity>> page(VolActivityPageParam volActivityPageParam) {
        return CommonResult.data(volActivityService.page(volActivityPageParam));
    }

    /**
     * 添加志愿活动
     *
     * @author jetox
     * @date  2025/10/19 00:08
     */
    @Operation(summary = "添加志愿活动")
    @CommonLog("添加志愿活动")
    @SaCheckPermission("/biz/activity/add")
    @PostMapping("/biz/activity/add")
    public CommonResult<String> add(@RequestBody @Valid VolActivityAddParam volActivityAddParam) {
        volActivityService.add(volActivityAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑志愿活动
     *
     * @author jetox
     * @date  2025/10/19 00:08
     */
    @Operation(summary = "编辑志愿活动")
    @CommonLog("编辑志愿活动")
    @SaCheckPermission("/biz/activity/edit")
    @PostMapping("/biz/activity/edit")
    public CommonResult<String> edit(@RequestBody @Valid VolActivityEditParam volActivityEditParam) {
        volActivityService.edit(volActivityEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除志愿活动
     *
     * @author jetox
     * @date  2025/10/19 00:08
     */
    @Operation(summary = "删除志愿活动")
    @CommonLog("删除志愿活动")
    @SaCheckPermission("/biz/activity/delete")
    @PostMapping("/biz/activity/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<VolActivityIdParam> volActivityIdParamList) {
        volActivityService.delete(volActivityIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取志愿活动详情
     *
     * @author jetox
     * @date  2025/10/19 00:08
     */
    @Operation(summary = "获取志愿活动详情")
    @SaCheckPermission("/biz/activity/detail")
    @GetMapping("/biz/activity/detail")
    public CommonResult<VolActivity> detail(@Valid VolActivityIdParam volActivityIdParam) {
        return CommonResult.data(volActivityService.detail(volActivityIdParam));
    }

    /**
     * 下载志愿活动导入模板
     *
     * @author jetox
     * @date  2025/10/19 00:08
     */
    @Operation(summary = "下载志愿活动导入模板")
    @GetMapping(value = "/biz/activity/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        volActivityService.downloadImportTemplate(response);
    }

    /**
     * 导入志愿活动
     *
     * @author jetox
     * @date  2025/10/19 00:08
     */
    @Operation(summary = "导入志愿活动")
    @CommonLog("导入志愿活动")
    @SaCheckPermission("/biz/activity/importData")
    @PostMapping("/biz/activity/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(volActivityService.importData(file));
    }

    /**
     * 导出志愿活动
     *
     * @author jetox
     * @date  2025/10/19 00:08
     */
    @Operation(summary = "导出志愿活动")
    @SaCheckPermission("/biz/activity/exportData")
    @PostMapping(value = "/biz/activity/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<VolActivityIdParam> volActivityIdParamList, HttpServletResponse response) throws IOException {
        volActivityService.exportData(volActivityIdParamList, response);
    }
}
