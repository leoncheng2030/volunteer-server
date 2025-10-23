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
package vip.xiaonuo.biz.modular.feedback.service.impl;

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
import vip.xiaonuo.biz.modular.feedback.entity.BizFeedback;
import vip.xiaonuo.biz.modular.feedback.mapper.BizFeedbackMapper;
import vip.xiaonuo.biz.modular.feedback.param.BizFeedbackAddParam;
import vip.xiaonuo.biz.modular.feedback.param.BizFeedbackEditParam;
import vip.xiaonuo.biz.modular.feedback.param.BizFeedbackIdParam;
import vip.xiaonuo.biz.modular.feedback.param.BizFeedbackPageParam;
import vip.xiaonuo.biz.modular.feedback.service.BizFeedbackService;

import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 用户反馈Service接口实现类
 *
 * @author jetox
 * @date  2025/10/19 00:13
 **/
@Service
public class BizFeedbackServiceImpl extends ServiceImpl<BizFeedbackMapper, BizFeedback> implements BizFeedbackService {

    @Override
    public Page<BizFeedback> page(BizFeedbackPageParam bizFeedbackPageParam) {
        QueryWrapper<BizFeedback> queryWrapper = new QueryWrapper<BizFeedback>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(bizFeedbackPageParam.getTitle())) {
            queryWrapper.lambda().like(BizFeedback::getTitle, bizFeedbackPageParam.getTitle());
        }
        if(ObjectUtil.isNotEmpty(bizFeedbackPageParam.getContent())) {
            queryWrapper.lambda().like(BizFeedback::getContent, bizFeedbackPageParam.getContent());
        }
        if(ObjectUtil.isNotEmpty(bizFeedbackPageParam.getType())) {
            queryWrapper.lambda().eq(BizFeedback::getType, bizFeedbackPageParam.getType());
        }
        if(ObjectUtil.isNotEmpty(bizFeedbackPageParam.getContactName())) {
            queryWrapper.lambda().like(BizFeedback::getContactName, bizFeedbackPageParam.getContactName());
        }
        if(ObjectUtil.isNotEmpty(bizFeedbackPageParam.getContactPhone())) {
            queryWrapper.lambda().like(BizFeedback::getContactPhone, bizFeedbackPageParam.getContactPhone());
        }
        if(ObjectUtil.isNotEmpty(bizFeedbackPageParam.getUserId())) {
            queryWrapper.lambda().eq(BizFeedback::getUserId, bizFeedbackPageParam.getUserId());
        }
        if(ObjectUtil.isNotEmpty(bizFeedbackPageParam.getStatus())) {
            queryWrapper.lambda().eq(BizFeedback::getStatus, bizFeedbackPageParam.getStatus());
        }
        if(ObjectUtil.isAllNotEmpty(bizFeedbackPageParam.getSortField(), bizFeedbackPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(bizFeedbackPageParam.getSortOrder());
            queryWrapper.orderBy(true, bizFeedbackPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(bizFeedbackPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(BizFeedback::getSortCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(BizFeedbackAddParam bizFeedbackAddParam) {
        BizFeedback bizFeedback = BeanUtil.toBean(bizFeedbackAddParam, BizFeedback.class);
        this.save(bizFeedback);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(BizFeedbackEditParam bizFeedbackEditParam) {
        BizFeedback bizFeedback = this.queryEntity(bizFeedbackEditParam.getId());
        BeanUtil.copyProperties(bizFeedbackEditParam, bizFeedback);
        this.updateById(bizFeedback);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<BizFeedbackIdParam> bizFeedbackIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(bizFeedbackIdParamList, BizFeedbackIdParam::getId));
    }

    @Override
    public BizFeedback detail(BizFeedbackIdParam bizFeedbackIdParam) {
        return this.queryEntity(bizFeedbackIdParam.getId());
    }

    @Override
    public BizFeedback queryEntity(String id) {
        BizFeedback bizFeedback = this.getById(id);
        if(ObjectUtil.isEmpty(bizFeedback)) {
            throw new CommonException("用户反馈不存在，id值为：{}", id);
        }
        return bizFeedback;
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<BizFeedbackEditParam> dataList = CollectionUtil.newArrayList();
         String fileName = "用户反馈导入模板_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), BizFeedbackEditParam.class).sheet("用户反馈").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 用户反馈导入模板下载失败：", e);
         CommonResponseUtil.renderError(response, "用户反馈导入模板下载失败");
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
                    FileUtil.FILE_SEPARATOR + "bizFeedbackImportTemplate.xlsx"));
            // 读取excel
            List<BizFeedbackEditParam> bizFeedbackEditParamList =  EasyExcel.read(tempFile).head(BizFeedbackEditParam.class).sheet()
                    .headRowNumber(1).doReadSync();
            List<BizFeedback> allDataList = this.list();
            for (int i = 0; i < bizFeedbackEditParamList.size(); i++) {
                JSONObject jsonObject = this.doImport(allDataList, bizFeedbackEditParamList.get(i), i);
                if(jsonObject.getBool("success")) {
                    successCount += 1;
                } else {
                    errorCount += 1;
                    errorDetail.add(jsonObject);
                }
            }
            return JSONUtil.createObj()
                    .set("totalCount", bizFeedbackEditParamList.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> 用户反馈导入失败：", e);
            throw new CommonException("用户反馈导入失败");
        }
    }

    public JSONObject doImport(List<BizFeedback> allDataList, BizFeedbackEditParam bizFeedbackEditParam, int i) {
        String id = bizFeedbackEditParam.getId();
        String title = bizFeedbackEditParam.getTitle();
        String content = bizFeedbackEditParam.getContent();
        String type = bizFeedbackEditParam.getType();
        String contactName = bizFeedbackEditParam.getContactName();
        String contactPhone = bizFeedbackEditParam.getContactPhone();
        if(ObjectUtil.hasEmpty(id, title, content, type, contactName, contactPhone)) {
            return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
        } else {
            try {
                int index = CollStreamUtil.toList(allDataList, BizFeedback::getId).indexOf(bizFeedbackEditParam.getId());
                BizFeedback bizFeedback;
                boolean isAdd = false;
                if(index == -1) {
                    isAdd = true;
                    bizFeedback = new BizFeedback();
                } else {
                    bizFeedback = allDataList.get(index);
                }
                BeanUtil.copyProperties(bizFeedbackEditParam, bizFeedback);
                if(isAdd) {
                    allDataList.add(bizFeedback);
                } else {
                    allDataList.remove(index);
                    allDataList.add(index, bizFeedback);
                }
                this.saveOrUpdate(bizFeedback);
                return JSONUtil.createObj().set("success", true);
            } catch (Exception e) {
              log.error(">>> 数据导入异常：", e);
              return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
            }
        }
    }

    @Override
    public void exportData(List<BizFeedbackIdParam> bizFeedbackIdParamList, HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<BizFeedbackEditParam> dataList;
         if(ObjectUtil.isNotEmpty(bizFeedbackIdParamList)) {
            List<String> idList = CollStreamUtil.toList(bizFeedbackIdParamList, BizFeedbackIdParam::getId);
            dataList = BeanUtil.copyToList(this.listByIds(idList), BizFeedbackEditParam.class);
         } else {
            dataList = BeanUtil.copyToList(this.list(), BizFeedbackEditParam.class);
         }
         String fileName = "用户反馈_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), BizFeedbackEditParam.class).sheet("用户反馈").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 用户反馈导出失败：", e);
         CommonResponseUtil.renderError(response, "用户反馈导出失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }
}
