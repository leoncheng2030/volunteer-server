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
package vip.xiaonuo.biz.modular.activitycheckin.controller;

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
import vip.xiaonuo.biz.modular.activitycheckin.entity.VolActivityCheckin;
import vip.xiaonuo.biz.modular.activitycheckin.param.VolActivityCheckinAddParam;
import vip.xiaonuo.biz.modular.activitycheckin.param.VolActivityCheckinEditParam;
import vip.xiaonuo.biz.modular.activitycheckin.param.VolActivityCheckinIdParam;
import vip.xiaonuo.biz.modular.activitycheckin.param.VolActivityCheckinPageParam;
import vip.xiaonuo.biz.modular.activitycheckin.service.VolActivityCheckinService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 活动签到表控制器
 *
 * @author jetox
 * @date  2025/10/22 06:17
 */
@Tag(name = "活动签到表控制器")
@RestController
@Validated
public class VolActivityCheckinController {

    @Resource
    private VolActivityCheckinService volActivityCheckinService;

    /**
     * 获取活动签到表分页
     *
     * @author jetox
     * @date  2025/10/22 06:17
     */
    @Operation(summary = "获取活动签到表分页")
    @SaCheckPermission("/biz/activitycheckin/page")
    @GetMapping("/biz/activitycheckin/page")
    public CommonResult<Page<VolActivityCheckin>> page(VolActivityCheckinPageParam volActivityCheckinPageParam) {
        return CommonResult.data(volActivityCheckinService.page(volActivityCheckinPageParam));
    }

    /**
     * 添加活动签到表
     *
     * @author jetox
     * @date  2025/10/22 06:17
     */
    @Operation(summary = "添加活动签到表")
    @CommonLog("添加活动签到表")
    @SaCheckPermission("/biz/activitycheckin/add")
    @PostMapping("/biz/activitycheckin/add")
    public CommonResult<String> add(@RequestBody @Valid VolActivityCheckinAddParam volActivityCheckinAddParam) {
        volActivityCheckinService.add(volActivityCheckinAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑活动签到表
     *
     * @author jetox
     * @date  2025/10/22 06:17
     */
    @Operation(summary = "编辑活动签到表")
    @CommonLog("编辑活动签到表")
    @SaCheckPermission("/biz/activitycheckin/edit")
    @PostMapping("/biz/activitycheckin/edit")
    public CommonResult<String> edit(@RequestBody @Valid VolActivityCheckinEditParam volActivityCheckinEditParam) {
        volActivityCheckinService.edit(volActivityCheckinEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除活动签到表
     *
     * @author jetox
     * @date  2025/10/22 06:17
     */
    @Operation(summary = "删除活动签到表")
    @CommonLog("删除活动签到表")
    @SaCheckPermission("/biz/activitycheckin/delete")
    @PostMapping("/biz/activitycheckin/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<VolActivityCheckinIdParam> volActivityCheckinIdParamList) {
        volActivityCheckinService.delete(volActivityCheckinIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取活动签到表详情
     *
     * @author jetox
     * @date  2025/10/22 06:17
     */
    @Operation(summary = "获取活动签到表详情")
    @SaCheckPermission("/biz/activitycheckin/detail")
    @GetMapping("/biz/activitycheckin/detail")
    public CommonResult<VolActivityCheckin> detail(@Valid VolActivityCheckinIdParam volActivityCheckinIdParam) {
        return CommonResult.data(volActivityCheckinService.detail(volActivityCheckinIdParam));
    }

    /**
     * 下载活动签到表导入模板
     *
     * @author jetox
     * @date  2025/10/22 06:17
     */
    @Operation(summary = "下载活动签到表导入模板")
    @GetMapping(value = "/biz/activitycheckin/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        volActivityCheckinService.downloadImportTemplate(response);
    }

    /**
     * 导入活动签到表
     *
     * @author jetox
     * @date  2025/10/22 06:17
     */
    @Operation(summary = "导入活动签到表")
    @CommonLog("导入活动签到表")
    @SaCheckPermission("/biz/activitycheckin/importData")
    @PostMapping("/biz/activitycheckin/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(volActivityCheckinService.importData(file));
    }

    /**
     * 导出活动签到表
     *
     * @author jetox
     * @date  2025/10/22 06:17
     */
    @Operation(summary = "导出活动签到表")
    @SaCheckPermission("/biz/activitycheckin/exportData")
    @PostMapping(value = "/biz/activitycheckin/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<VolActivityCheckinIdParam> volActivityCheckinIdParamList, HttpServletResponse response) throws IOException {
        volActivityCheckinService.exportData(volActivityCheckinIdParamList, response);
    }
}
