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
package vip.xiaonuo.biz.modular.activitycategory.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import java.math.BigDecimal;
import java.util.Date;
import vip.xiaonuo.biz.modular.activitycategory.entity.VolActivityCategory;
import vip.xiaonuo.biz.modular.activitycategory.mapper.VolActivityCategoryMapper;
import vip.xiaonuo.biz.modular.activitycategory.param.VolActivityCategoryAddParam;
import vip.xiaonuo.biz.modular.activitycategory.param.VolActivityCategoryEditParam;
import vip.xiaonuo.biz.modular.activitycategory.param.VolActivityCategoryIdParam;
import vip.xiaonuo.biz.modular.activitycategory.param.VolActivityCategoryPageParam;
import vip.xiaonuo.biz.modular.activitycategory.service.VolActivityCategoryService;

import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 活动分类Service接口实现类
 *
 * @author jetox
 * @date  2025/10/19 00:04
 **/
@Service
public class VolActivityCategoryServiceImpl extends ServiceImpl<VolActivityCategoryMapper, VolActivityCategory> implements VolActivityCategoryService {

    @Override
    public Page<VolActivityCategory> page(VolActivityCategoryPageParam volActivityCategoryPageParam) {
        QueryWrapper<VolActivityCategory> queryWrapper = new QueryWrapper<VolActivityCategory>().checkSqlInjection();
        if(ObjectUtil.isAllNotEmpty(volActivityCategoryPageParam.getSortField(), volActivityCategoryPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(volActivityCategoryPageParam.getSortOrder());
            queryWrapper.orderBy(true, volActivityCategoryPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(volActivityCategoryPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(VolActivityCategory::getSortCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(VolActivityCategoryAddParam volActivityCategoryAddParam) {
        VolActivityCategory volActivityCategory = BeanUtil.toBean(volActivityCategoryAddParam, VolActivityCategory.class);
        this.save(volActivityCategory);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(VolActivityCategoryEditParam volActivityCategoryEditParam) {
        VolActivityCategory volActivityCategory = this.queryEntity(volActivityCategoryEditParam.getId());
        BeanUtil.copyProperties(volActivityCategoryEditParam, volActivityCategory);
        this.updateById(volActivityCategory);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<VolActivityCategoryIdParam> volActivityCategoryIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(volActivityCategoryIdParamList, VolActivityCategoryIdParam::getId));
    }

    @Override
    public VolActivityCategory detail(VolActivityCategoryIdParam volActivityCategoryIdParam) {
        return this.queryEntity(volActivityCategoryIdParam.getId());
    }

    @Override
    public VolActivityCategory queryEntity(String id) {
        VolActivityCategory volActivityCategory = this.getById(id);
        if(ObjectUtil.isEmpty(volActivityCategory)) {
            throw new CommonException("活动分类不存在，id值为：{}", id);
        }
        return volActivityCategory;
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<VolActivityCategoryEditParam> dataList = CollectionUtil.newArrayList();
         String fileName = "活动分类导入模板_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), VolActivityCategoryEditParam.class).sheet("活动分类").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 活动分类导入模板下载失败：", e);
         CommonResponseUtil.renderError(response, "活动分类导入模板下载失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public JSONObject importData(MultipartFile file) {
        try {
            int successCount = 0;
            int errorCount = 0;
            JSONArray errorDetail = JSONUtil.createArray();
            // 创建临时文件
            File tempFile = FileUtil.writeBytes(file.getBytes(), FileUtil.file(FileUtil.getTmpDir() +
                    FileUtil.FILE_SEPARATOR + "volActivityCategoryImportTemplate.xlsx"));
            // 读取excel
            List<VolActivityCategoryEditParam> volActivityCategoryEditParamList =  EasyExcel.read(tempFile).head(VolActivityCategoryEditParam.class).sheet()
                    .headRowNumber(1).doReadSync();
            List<VolActivityCategory> allDataList = this.list();
            for (int i = 0; i < volActivityCategoryEditParamList.size(); i++) {
                JSONObject jsonObject = this.doImport(allDataList, volActivityCategoryEditParamList.get(i), i);
                if(jsonObject.getBool("success")) {
                    successCount += 1;
                } else {
                    errorCount += 1;
                    errorDetail.add(jsonObject);
                }
            }
            return JSONUtil.createObj()
                    .set("totalCount", volActivityCategoryEditParamList.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> 活动分类导入失败：", e);
            throw new CommonException("活动分类导入失败");
        }
    }

    public JSONObject doImport(List<VolActivityCategory> allDataList, VolActivityCategoryEditParam volActivityCategoryEditParam, int i) {
        String id = volActivityCategoryEditParam.getId();
        if(ObjectUtil.hasEmpty(id)) {
            return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
        } else {
            try {
                int index = CollStreamUtil.toList(allDataList, VolActivityCategory::getId).indexOf(volActivityCategoryEditParam.getId());
                VolActivityCategory volActivityCategory;
                boolean isAdd = false;
                if(index == -1) {
                    isAdd = true;
                    volActivityCategory = new VolActivityCategory();
                } else {
                    volActivityCategory = allDataList.get(index);
                }
                BeanUtil.copyProperties(volActivityCategoryEditParam, volActivityCategory);
                if(isAdd) {
                    allDataList.add(volActivityCategory);
                } else {
                    allDataList.remove(index);
                    allDataList.add(index, volActivityCategory);
                }
                this.saveOrUpdate(volActivityCategory);
                return JSONUtil.createObj().set("success", true);
            } catch (Exception e) {
              log.error(">>> 数据导入异常：", e);
              return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
            }
        }
    }

    @Override
    public void exportData(List<VolActivityCategoryIdParam> volActivityCategoryIdParamList, HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<VolActivityCategoryEditParam> dataList;
         if(ObjectUtil.isNotEmpty(volActivityCategoryIdParamList)) {
            List<String> idList = CollStreamUtil.toList(volActivityCategoryIdParamList, VolActivityCategoryIdParam::getId);
            dataList = BeanUtil.copyToList(this.listByIds(idList), VolActivityCategoryEditParam.class);
         } else {
            dataList = BeanUtil.copyToList(this.list(), VolActivityCategoryEditParam.class);
         }
         String fileName = "活动分类_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), VolActivityCategoryEditParam.class).sheet("活动分类").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 活动分类导出失败：", e);
         CommonResponseUtil.renderError(response, "活动分类导出失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }
}
