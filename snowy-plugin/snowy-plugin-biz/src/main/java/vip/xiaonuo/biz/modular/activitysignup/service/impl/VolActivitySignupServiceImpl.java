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
package vip.xiaonuo.biz.modular.activitysignup.service.impl;

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
import vip.xiaonuo.biz.modular.activitysignup.entity.VolActivitySignup;
import vip.xiaonuo.biz.modular.activitysignup.mapper.VolActivitySignupMapper;
import vip.xiaonuo.biz.modular.activitysignup.param.VolActivitySignupAddParam;
import vip.xiaonuo.biz.modular.activitysignup.param.VolActivitySignupEditParam;
import vip.xiaonuo.biz.modular.activitysignup.param.VolActivitySignupIdParam;
import vip.xiaonuo.biz.modular.activitysignup.param.VolActivitySignupPageParam;
import vip.xiaonuo.biz.modular.activitysignup.service.VolActivitySignupService;

import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 报名记录Service接口实现类
 *
 * @author jetox
 * @date  2025/10/19 00:11
 **/
@Service
public class VolActivitySignupServiceImpl extends ServiceImpl<VolActivitySignupMapper, VolActivitySignup> implements VolActivitySignupService {

    @Override
    public Page<VolActivitySignup> page(VolActivitySignupPageParam volActivitySignupPageParam) {
        QueryWrapper<VolActivitySignup> queryWrapper = new QueryWrapper<VolActivitySignup>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(volActivitySignupPageParam.getActivityId())) {
            queryWrapper.lambda().eq(VolActivitySignup::getActivityId, volActivitySignupPageParam.getActivityId());
        }
        if(ObjectUtil.isNotEmpty(volActivitySignupPageParam.getUserId())) {
            queryWrapper.lambda().eq(VolActivitySignup::getUserId, volActivitySignupPageParam.getUserId());
        }
        if(ObjectUtil.isNotEmpty(volActivitySignupPageParam.getStatus())) {
            queryWrapper.lambda().eq(VolActivitySignup::getStatus, volActivitySignupPageParam.getStatus());
        }
        if(ObjectUtil.isAllNotEmpty(volActivitySignupPageParam.getSortField(), volActivitySignupPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(volActivitySignupPageParam.getSortOrder());
            queryWrapper.orderBy(true, volActivitySignupPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(volActivitySignupPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(VolActivitySignup::getId);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(VolActivitySignupAddParam volActivitySignupAddParam) {
        VolActivitySignup volActivitySignup = BeanUtil.toBean(volActivitySignupAddParam, VolActivitySignup.class);
        this.save(volActivitySignup);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(VolActivitySignupEditParam volActivitySignupEditParam) {
        VolActivitySignup volActivitySignup = this.queryEntity(volActivitySignupEditParam.getId());
        BeanUtil.copyProperties(volActivitySignupEditParam, volActivitySignup);
        this.updateById(volActivitySignup);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<VolActivitySignupIdParam> volActivitySignupIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(volActivitySignupIdParamList, VolActivitySignupIdParam::getId));
    }

    @Override
    public VolActivitySignup detail(VolActivitySignupIdParam volActivitySignupIdParam) {
        return this.queryEntity(volActivitySignupIdParam.getId());
    }

    @Override
    public VolActivitySignup queryEntity(String id) {
        VolActivitySignup volActivitySignup = this.getById(id);
        if(ObjectUtil.isEmpty(volActivitySignup)) {
            throw new CommonException("报名记录不存在，id值为：{}", id);
        }
        return volActivitySignup;
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<VolActivitySignupEditParam> dataList = CollectionUtil.newArrayList();
         String fileName = "报名记录导入模板_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), VolActivitySignupEditParam.class).sheet("报名记录").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 报名记录导入模板下载失败：", e);
         CommonResponseUtil.renderError(response, "报名记录导入模板下载失败");
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
                    FileUtil.FILE_SEPARATOR + "volActivitySignupImportTemplate.xlsx"));
            // 读取excel
            List<VolActivitySignupEditParam> volActivitySignupEditParamList =  EasyExcel.read(tempFile).head(VolActivitySignupEditParam.class).sheet()
                    .headRowNumber(1).doReadSync();
            List<VolActivitySignup> allDataList = this.list();
            for (int i = 0; i < volActivitySignupEditParamList.size(); i++) {
                JSONObject jsonObject = this.doImport(allDataList, volActivitySignupEditParamList.get(i), i);
                if(jsonObject.getBool("success")) {
                    successCount += 1;
                } else {
                    errorCount += 1;
                    errorDetail.add(jsonObject);
                }
            }
            return JSONUtil.createObj()
                    .set("totalCount", volActivitySignupEditParamList.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> 报名记录导入失败：", e);
            throw new CommonException("报名记录导入失败");
        }
    }

    public JSONObject doImport(List<VolActivitySignup> allDataList, VolActivitySignupEditParam volActivitySignupEditParam, int i) {
        String id = volActivitySignupEditParam.getId();
        String activityId = volActivitySignupEditParam.getActivityId();
        String userId = volActivitySignupEditParam.getUserId();
        Date signupTime = volActivitySignupEditParam.getSignupTime();
        String status = volActivitySignupEditParam.getStatus();
        if(ObjectUtil.hasEmpty(id, activityId, userId, signupTime, status)) {
            return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
        } else {
            try {
                int index = CollStreamUtil.toList(allDataList, VolActivitySignup::getId).indexOf(volActivitySignupEditParam.getId());
                VolActivitySignup volActivitySignup;
                boolean isAdd = false;
                if(index == -1) {
                    isAdd = true;
                    volActivitySignup = new VolActivitySignup();
                } else {
                    volActivitySignup = allDataList.get(index);
                }
                BeanUtil.copyProperties(volActivitySignupEditParam, volActivitySignup);
                if(isAdd) {
                    allDataList.add(volActivitySignup);
                } else {
                    allDataList.remove(index);
                    allDataList.add(index, volActivitySignup);
                }
                this.saveOrUpdate(volActivitySignup);
                return JSONUtil.createObj().set("success", true);
            } catch (Exception e) {
              log.error(">>> 数据导入异常：", e);
              return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
            }
        }
    }

    @Override
    public void exportData(List<VolActivitySignupIdParam> volActivitySignupIdParamList, HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<VolActivitySignupEditParam> dataList;
         if(ObjectUtil.isNotEmpty(volActivitySignupIdParamList)) {
            List<String> idList = CollStreamUtil.toList(volActivitySignupIdParamList, VolActivitySignupIdParam::getId);
            dataList = BeanUtil.copyToList(this.listByIds(idList), VolActivitySignupEditParam.class);
         } else {
            dataList = BeanUtil.copyToList(this.list(), VolActivitySignupEditParam.class);
         }
         String fileName = "报名记录_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), VolActivitySignupEditParam.class).sheet("报名记录").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 报名记录导出失败：", e);
         CommonResponseUtil.renderError(response, "报名记录导出失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }
}
