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
package vip.xiaonuo.biz.modular.activitycategory.controller;

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
import vip.xiaonuo.biz.modular.activitycategory.entity.VolActivityCategory;
import vip.xiaonuo.biz.modular.activitycategory.param.VolActivityCategoryAddParam;
import vip.xiaonuo.biz.modular.activitycategory.param.VolActivityCategoryEditParam;
import vip.xiaonuo.biz.modular.activitycategory.param.VolActivityCategoryIdParam;
import vip.xiaonuo.biz.modular.activitycategory.param.VolActivityCategoryPageParam;
import vip.xiaonuo.biz.modular.activitycategory.service.VolActivityCategoryService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 活动分类控制器
 *
 * @author jetox
 * @date  2025/10/19 00:04
 */
@Tag(name = "活动分类控制器")
@RestController
@Validated
public class VolActivityCategoryController {

    @Resource
    private VolActivityCategoryService volActivityCategoryService;

    /**
     * 获取活动分类分页
     *
     * @author jetox
     * @date  2025/10/19 00:04
     */
    @Operation(summary = "获取活动分类分页")
    @SaCheckPermission("/biz/activitycategory/page")
    @GetMapping("/biz/activitycategory/page")
    public CommonResult<Page<VolActivityCategory>> page(VolActivityCategoryPageParam volActivityCategoryPageParam) {
        return CommonResult.data(volActivityCategoryService.page(volActivityCategoryPageParam));
    }

    /**
     * 添加活动分类
     *
     * @author jetox
     * @date  2025/10/19 00:04
     */
    @Operation(summary = "添加活动分类")
    @CommonLog("添加活动分类")
    @SaCheckPermission("/biz/activitycategory/add")
    @PostMapping("/biz/activitycategory/add")
    public CommonResult<String> add(@RequestBody @Valid VolActivityCategoryAddParam volActivityCategoryAddParam) {
        volActivityCategoryService.add(volActivityCategoryAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑活动分类
     *
     * @author jetox
     * @date  2025/10/19 00:04
     */
    @Operation(summary = "编辑活动分类")
    @CommonLog("编辑活动分类")
    @SaCheckPermission("/biz/activitycategory/edit")
    @PostMapping("/biz/activitycategory/edit")
    public CommonResult<String> edit(@RequestBody @Valid VolActivityCategoryEditParam volActivityCategoryEditParam) {
        volActivityCategoryService.edit(volActivityCategoryEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除活动分类
     *
     * @author jetox
     * @date  2025/10/19 00:04
     */
    @Operation(summary = "删除活动分类")
    @CommonLog("删除活动分类")
    @SaCheckPermission("/biz/activitycategory/delete")
    @PostMapping("/biz/activitycategory/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<VolActivityCategoryIdParam> volActivityCategoryIdParamList) {
        volActivityCategoryService.delete(volActivityCategoryIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取活动分类详情
     *
     * @author jetox
     * @date  2025/10/19 00:04
     */
    @Operation(summary = "获取活动分类详情")
    @SaCheckPermission("/biz/activitycategory/detail")
    @GetMapping("/biz/activitycategory/detail")
    public CommonResult<VolActivityCategory> detail(@Valid VolActivityCategoryIdParam volActivityCategoryIdParam) {
        return CommonResult.data(volActivityCategoryService.detail(volActivityCategoryIdParam));
    }

    /**
     * 下载活动分类导入模板
     *
     * @author jetox
     * @date  2025/10/19 00:04
     */
    @Operation(summary = "下载活动分类导入模板")
    @GetMapping(value = "/biz/activitycategory/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        volActivityCategoryService.downloadImportTemplate(response);
    }

    /**
     * 导入活动分类
     *
     * @author jetox
     * @date  2025/10/19 00:04
     */
    @Operation(summary = "导入活动分类")
    @CommonLog("导入活动分类")
    @SaCheckPermission("/biz/activitycategory/importData")
    @PostMapping("/biz/activitycategory/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(volActivityCategoryService.importData(file));
    }

    /**
     * 导出活动分类
     *
     * @author jetox
     * @date  2025/10/19 00:04
     */
    @Operation(summary = "导出活动分类")
    @SaCheckPermission("/biz/activitycategory/exportData")
    @PostMapping(value = "/biz/activitycategory/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<VolActivityCategoryIdParam> volActivityCategoryIdParamList, HttpServletResponse response) throws IOException {
        volActivityCategoryService.exportData(volActivityCategoryIdParamList, response);
    }
}
