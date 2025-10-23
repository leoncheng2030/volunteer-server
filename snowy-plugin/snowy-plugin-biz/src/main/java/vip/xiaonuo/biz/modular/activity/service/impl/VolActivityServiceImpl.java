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
package vip.xiaonuo.biz.modular.activity.service.impl;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import java.math.BigDecimal;
import java.util.Date;
import vip.xiaonuo.biz.modular.activity.entity.VolActivity;
import vip.xiaonuo.biz.modular.activity.mapper.VolActivityMapper;
import vip.xiaonuo.biz.modular.activity.param.VolActivityAddParam;
import vip.xiaonuo.biz.modular.activity.param.VolActivityEditParam;
import vip.xiaonuo.biz.modular.activity.param.VolActivityIdParam;
import vip.xiaonuo.biz.modular.activity.param.VolActivityPageParam;
import vip.xiaonuo.biz.modular.activity.service.VolActivityService;

import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 志愿活动Service接口实现类
 *
 * @author jetox
 * @date  2025/10/19 00:08
 **/
@Slf4j
@Service
public class VolActivityServiceImpl extends ServiceImpl<VolActivityMapper, VolActivity> implements VolActivityService {

    @Override
    public Page<VolActivity> page(VolActivityPageParam volActivityPageParam) {
        QueryWrapper<VolActivity> queryWrapper = new QueryWrapper<VolActivity>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(volActivityPageParam.getTitle())) {
            queryWrapper.lambda().like(VolActivity::getTitle, volActivityPageParam.getTitle());
        }
        if(ObjectUtil.isNotEmpty(volActivityPageParam.getLocation())) {
            queryWrapper.lambda().like(VolActivity::getLocation, volActivityPageParam.getLocation());
        }
        if(ObjectUtil.isNotEmpty(volActivityPageParam.getContactPerson())) {
            queryWrapper.lambda().eq(VolActivity::getContactPerson, volActivityPageParam.getContactPerson());
        }
        if(ObjectUtil.isNotEmpty(volActivityPageParam.getContactPhone())) {
            queryWrapper.lambda().like(VolActivity::getContactPhone, volActivityPageParam.getContactPhone());
        }
        if(ObjectUtil.isNotEmpty(volActivityPageParam.getStatus()) && !Objects.equals(volActivityPageParam.getStatus(), "undefined")) {
            queryWrapper.lambda().eq(VolActivity::getStatus, volActivityPageParam.getStatus());
        }
        if(ObjectUtil.isAllNotEmpty(volActivityPageParam.getSortField(), volActivityPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(volActivityPageParam.getSortOrder());
            queryWrapper.orderBy(true, volActivityPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(volActivityPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(VolActivity::getSortCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(VolActivityAddParam volActivityAddParam) {
        VolActivity volActivity = BeanUtil.toBean(volActivityAddParam, VolActivity.class);
        this.save(volActivity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(VolActivityEditParam volActivityEditParam) {
        VolActivity volActivity = this.queryEntity(volActivityEditParam.getId());
        BeanUtil.copyProperties(volActivityEditParam, volActivity);
        
        // 处理扩展信息中的重复报名配置
        if (volActivityEditParam.getAllowResignup() != null) {
            try {
                Map<String, Object> extInfo = new HashMap<>();
                // 保留原有的扩展信息
                if (StrUtil.isNotEmpty(volActivity.getExtJson())) {
                    extInfo = JSONUtil.toBean(volActivity.getExtJson(), Map.class);
                }
                // 设置重复报名配置
                extInfo.put("allowResignup", volActivityEditParam.getAllowResignup());
                volActivity.setExtJson(JSONUtil.toJsonStr(extInfo));
            } catch (Exception e) {
                log.error("处理活动扩展信息失败", e);
            }
        }
        
        this.updateById(volActivity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<VolActivityIdParam> volActivityIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(volActivityIdParamList, VolActivityIdParam::getId));
    }

    @Override
    public VolActivity detail(VolActivityIdParam volActivityIdParam) {
        VolActivity activity = this.queryEntity(volActivityIdParam.getId());
        
        // 解析扩展信息中的allowResignup配置
        if (StrUtil.isNotEmpty(activity.getExtJson())) {
            try {
                Map<String, Object> extInfo = JSONUtil.toBean(activity.getExtJson(), Map.class);
                Boolean allowResignup = Boolean.TRUE.equals(extInfo.get("allowResignup"));
                // 设置到实体对象的某个字段中，这里可以扩展实体类或者使用其他方式
                // 暂时将配置信息保留在extJson中，前端解析
            } catch (Exception e) {
                log.error("解析活动扩展信息失败", e);
            }
        }
        
        return activity;
    }

    @Override
    public VolActivity queryEntity(String id) {
        VolActivity volActivity = this.getById(id);
        if(ObjectUtil.isEmpty(volActivity)) {
            throw new CommonException("志愿活动不存在，id值为：{}", id);
        }
        return volActivity;
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<VolActivityEditParam> dataList = CollectionUtil.newArrayList();
         String fileName = "志愿活动导入模板_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), VolActivityEditParam.class).sheet("志愿活动").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 志愿活动导入模板下载失败：", e);
         CommonResponseUtil.renderError(response, "志愿活动导入模板下载失败");
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
                    FileUtil.FILE_SEPARATOR + "volActivityImportTemplate.xlsx"));
            // 读取excel
            List<VolActivityEditParam> volActivityEditParamList =  EasyExcel.read(tempFile).head(VolActivityEditParam.class).sheet()
                    .headRowNumber(1).doReadSync();
            List<VolActivity> allDataList = this.list();
            for (int i = 0; i < volActivityEditParamList.size(); i++) {
                JSONObject jsonObject = this.doImport(allDataList, volActivityEditParamList.get(i), i);
                if(jsonObject.getBool("success")) {
                    successCount += 1;
                } else {
                    errorCount += 1;
                    errorDetail.add(jsonObject);
                }
            }
            return JSONUtil.createObj()
                    .set("totalCount", volActivityEditParamList.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> 志愿活动导入失败：", e);
            throw new CommonException("志愿活动导入失败");
        }
    }

    public JSONObject doImport(List<VolActivity> allDataList, VolActivityEditParam volActivityEditParam, int i) {
        String id = volActivityEditParam.getId();
        String title = volActivityEditParam.getTitle();
        String coverImage = volActivityEditParam.getCoverImage();
        String content = volActivityEditParam.getContent();
        Date activityDate = volActivityEditParam.getActivityDate();
        Date startTime = volActivityEditParam.getStartTime();
        Date endTime = volActivityEditParam.getEndTime();
        String location = volActivityEditParam.getLocation();
        Integer recruitCount = volActivityEditParam.getRecruitCount();
        BigDecimal serviceHours = volActivityEditParam.getServiceHours();
        Date signupDeadline = volActivityEditParam.getSignupDeadline();
        String contactPerson = volActivityEditParam.getContactPerson();
        String contactPhone = volActivityEditParam.getContactPhone();
        String status = volActivityEditParam.getStatus();
        if(ObjectUtil.hasEmpty(id, title, coverImage, content, activityDate, startTime, endTime, location, recruitCount, serviceHours, signupDeadline, contactPerson, contactPhone, status)) {
            return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
        } else {
            try {
                int index = CollStreamUtil.toList(allDataList, VolActivity::getId).indexOf(volActivityEditParam.getId());
                VolActivity volActivity;
                boolean isAdd = false;
                if(index == -1) {
                    isAdd = true;
                    volActivity = new VolActivity();
                } else {
                    volActivity = allDataList.get(index);
                }
                BeanUtil.copyProperties(volActivityEditParam, volActivity);
                if(isAdd) {
                    allDataList.add(volActivity);
                } else {
                    allDataList.remove(index);
                    allDataList.add(index, volActivity);
                }
                this.saveOrUpdate(volActivity);
                return JSONUtil.createObj().set("success", true);
            } catch (Exception e) {
              log.error(">>> 数据导入异常：", e);
              return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
            }
        }
    }

    @Override
    public void exportData(List<VolActivityIdParam> volActivityIdParamList, HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<VolActivityEditParam> dataList;
         if(ObjectUtil.isNotEmpty(volActivityIdParamList)) {
            List<String> idList = CollStreamUtil.toList(volActivityIdParamList, VolActivityIdParam::getId);
            dataList = BeanUtil.copyToList(this.listByIds(idList), VolActivityEditParam.class);
         } else {
            dataList = BeanUtil.copyToList(this.list(), VolActivityEditParam.class);
         }
         String fileName = "志愿活动_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), VolActivityEditParam.class).sheet("志愿活动").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 志愿活动导出失败：", e);
         CommonResponseUtil.renderError(response, "志愿活动导出失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }
}
