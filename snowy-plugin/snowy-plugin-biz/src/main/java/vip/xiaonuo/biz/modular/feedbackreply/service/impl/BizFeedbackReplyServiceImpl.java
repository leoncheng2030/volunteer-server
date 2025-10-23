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
package vip.xiaonuo.biz.modular.feedbackreply.service.impl;

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
import vip.xiaonuo.biz.modular.feedbackreply.entity.BizFeedbackReply;
import vip.xiaonuo.biz.modular.feedbackreply.mapper.BizFeedbackReplyMapper;
import vip.xiaonuo.biz.modular.feedbackreply.param.BizFeedbackReplyAddParam;
import vip.xiaonuo.biz.modular.feedbackreply.param.BizFeedbackReplyEditParam;
import vip.xiaonuo.biz.modular.feedbackreply.param.BizFeedbackReplyIdParam;
import vip.xiaonuo.biz.modular.feedbackreply.param.BizFeedbackReplyPageParam;
import vip.xiaonuo.biz.modular.feedbackreply.service.BizFeedbackReplyService;

import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 反馈回复Service接口实现类
 *
 * @author jetox
 * @date  2025/10/19 00:15
 **/
@Service
public class BizFeedbackReplyServiceImpl extends ServiceImpl<BizFeedbackReplyMapper, BizFeedbackReply> implements BizFeedbackReplyService {

    @Override
    public Page<BizFeedbackReply> page(BizFeedbackReplyPageParam bizFeedbackReplyPageParam) {
        QueryWrapper<BizFeedbackReply> queryWrapper = new QueryWrapper<BizFeedbackReply>().checkSqlInjection();
        if(ObjectUtil.isAllNotEmpty(bizFeedbackReplyPageParam.getSortField(), bizFeedbackReplyPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(bizFeedbackReplyPageParam.getSortOrder());
            queryWrapper.orderBy(true, bizFeedbackReplyPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(bizFeedbackReplyPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(BizFeedbackReply::getId);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(BizFeedbackReplyAddParam bizFeedbackReplyAddParam) {
        BizFeedbackReply bizFeedbackReply = BeanUtil.toBean(bizFeedbackReplyAddParam, BizFeedbackReply.class);
        this.save(bizFeedbackReply);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(BizFeedbackReplyEditParam bizFeedbackReplyEditParam) {
        BizFeedbackReply bizFeedbackReply = this.queryEntity(bizFeedbackReplyEditParam.getId());
        BeanUtil.copyProperties(bizFeedbackReplyEditParam, bizFeedbackReply);
        this.updateById(bizFeedbackReply);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<BizFeedbackReplyIdParam> bizFeedbackReplyIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(bizFeedbackReplyIdParamList, BizFeedbackReplyIdParam::getId));
    }

    @Override
    public BizFeedbackReply detail(BizFeedbackReplyIdParam bizFeedbackReplyIdParam) {
        return this.queryEntity(bizFeedbackReplyIdParam.getId());
    }

    @Override
    public BizFeedbackReply queryEntity(String id) {
        BizFeedbackReply bizFeedbackReply = this.getById(id);
        if(ObjectUtil.isEmpty(bizFeedbackReply)) {
            throw new CommonException("反馈回复不存在，id值为：{}", id);
        }
        return bizFeedbackReply;
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<BizFeedbackReplyEditParam> dataList = CollectionUtil.newArrayList();
         String fileName = "反馈回复导入模板_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), BizFeedbackReplyEditParam.class).sheet("反馈回复").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 反馈回复导入模板下载失败：", e);
         CommonResponseUtil.renderError(response, "反馈回复导入模板下载失败");
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
                    FileUtil.FILE_SEPARATOR + "bizFeedbackReplyImportTemplate.xlsx"));
            // 读取excel
            List<BizFeedbackReplyEditParam> bizFeedbackReplyEditParamList =  EasyExcel.read(tempFile).head(BizFeedbackReplyEditParam.class).sheet()
                    .headRowNumber(1).doReadSync();
            List<BizFeedbackReply> allDataList = this.list();
            for (int i = 0; i < bizFeedbackReplyEditParamList.size(); i++) {
                JSONObject jsonObject = this.doImport(allDataList, bizFeedbackReplyEditParamList.get(i), i);
                if(jsonObject.getBool("success")) {
                    successCount += 1;
                } else {
                    errorCount += 1;
                    errorDetail.add(jsonObject);
                }
            }
            return JSONUtil.createObj()
                    .set("totalCount", bizFeedbackReplyEditParamList.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> 反馈回复导入失败：", e);
            throw new CommonException("反馈回复导入失败");
        }
    }

    public JSONObject doImport(List<BizFeedbackReply> allDataList, BizFeedbackReplyEditParam bizFeedbackReplyEditParam, int i) {
        String id = bizFeedbackReplyEditParam.getId();
        if(ObjectUtil.hasEmpty(id)) {
            return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
        } else {
            try {
                int index = CollStreamUtil.toList(allDataList, BizFeedbackReply::getId).indexOf(bizFeedbackReplyEditParam.getId());
                BizFeedbackReply bizFeedbackReply;
                boolean isAdd = false;
                if(index == -1) {
                    isAdd = true;
                    bizFeedbackReply = new BizFeedbackReply();
                } else {
                    bizFeedbackReply = allDataList.get(index);
                }
                BeanUtil.copyProperties(bizFeedbackReplyEditParam, bizFeedbackReply);
                if(isAdd) {
                    allDataList.add(bizFeedbackReply);
                } else {
                    allDataList.remove(index);
                    allDataList.add(index, bizFeedbackReply);
                }
                this.saveOrUpdate(bizFeedbackReply);
                return JSONUtil.createObj().set("success", true);
            } catch (Exception e) {
              log.error(">>> 数据导入异常：", e);
              return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
            }
        }
    }

    @Override
    public void exportData(List<BizFeedbackReplyIdParam> bizFeedbackReplyIdParamList, HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<BizFeedbackReplyEditParam> dataList;
         if(ObjectUtil.isNotEmpty(bizFeedbackReplyIdParamList)) {
            List<String> idList = CollStreamUtil.toList(bizFeedbackReplyIdParamList, BizFeedbackReplyIdParam::getId);
            dataList = BeanUtil.copyToList(this.listByIds(idList), BizFeedbackReplyEditParam.class);
         } else {
            dataList = BeanUtil.copyToList(this.list(), BizFeedbackReplyEditParam.class);
         }
         String fileName = "反馈回复_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), BizFeedbackReplyEditParam.class).sheet("反馈回复").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 反馈回复导出失败：", e);
         CommonResponseUtil.renderError(response, "反馈回复导出失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }
}
