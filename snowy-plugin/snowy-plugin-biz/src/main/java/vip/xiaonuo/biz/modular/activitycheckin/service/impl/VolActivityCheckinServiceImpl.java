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
package vip.xiaonuo.biz.modular.activitycheckin.service.impl;

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
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.biz.modular.activity.entity.VolActivity;
import vip.xiaonuo.biz.modular.activity.service.VolActivityService;
import vip.xiaonuo.client.ClientUserApi;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import java.math.BigDecimal;
import java.util.Date;
import vip.xiaonuo.biz.modular.activitycheckin.entity.VolActivityCheckin;
import vip.xiaonuo.biz.modular.activitycheckin.mapper.VolActivityCheckinMapper;
import vip.xiaonuo.biz.modular.activitycheckin.param.VolActivityCheckinAddParam;
import vip.xiaonuo.biz.modular.activitycheckin.param.VolActivityCheckinEditParam;
import vip.xiaonuo.biz.modular.activitycheckin.param.VolActivityCheckinIdParam;
import vip.xiaonuo.biz.modular.activitycheckin.param.VolActivityCheckinPageParam;
import vip.xiaonuo.biz.modular.activitycheckin.service.VolActivityCheckinService;

import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 活动签到表Service接口实现类
 *
 * @author jetox
 * @date  2025/10/22 06:17
 **/
@Service
@Slf4j
public class VolActivityCheckinServiceImpl extends ServiceImpl<VolActivityCheckinMapper, VolActivityCheckin> implements VolActivityCheckinService {

    @Resource
    private VolActivityService volActivityService;

    @Resource
    private ClientUserApi clientUserApi;

    @Override
    public Page<VolActivityCheckin> page(VolActivityCheckinPageParam volActivityCheckinPageParam) {
        QueryWrapper<VolActivityCheckin> queryWrapper = new QueryWrapper<VolActivityCheckin>().checkSqlInjection();
        if(ObjectUtil.isAllNotEmpty(volActivityCheckinPageParam.getSortField(), volActivityCheckinPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(volActivityCheckinPageParam.getSortOrder());
            queryWrapper.orderBy(true, volActivityCheckinPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(volActivityCheckinPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(VolActivityCheckin::getId);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(VolActivityCheckinAddParam volActivityCheckinAddParam) {
        VolActivityCheckin volActivityCheckin = BeanUtil.toBean(volActivityCheckinAddParam, VolActivityCheckin.class);
        this.save(volActivityCheckin);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(VolActivityCheckinEditParam volActivityCheckinEditParam) {
        VolActivityCheckin volActivityCheckin = this.queryEntity(volActivityCheckinEditParam.getId());
        
        // 检查是否是新增签退（从无到有）
        boolean isNewCheckout = volActivityCheckin.getCheckoutTime() == null && 
                               volActivityCheckinEditParam.getCheckoutTime() != null;
        
        BeanUtil.copyProperties(volActivityCheckinEditParam, volActivityCheckin);
        this.updateById(volActivityCheckin);
        
        // 如果是新增签退，累加用户积分
        if (isNewCheckout) {
            addActivityPointsToUser(volActivityCheckin);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<VolActivityCheckinIdParam> volActivityCheckinIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(volActivityCheckinIdParamList, VolActivityCheckinIdParam::getId));
    }

    @Override
    public VolActivityCheckin detail(VolActivityCheckinIdParam volActivityCheckinIdParam) {
        return this.queryEntity(volActivityCheckinIdParam.getId());
    }

    @Override
    public VolActivityCheckin queryEntity(String id) {
        VolActivityCheckin volActivityCheckin = this.getById(id);
        if(ObjectUtil.isEmpty(volActivityCheckin)) {
            throw new CommonException("活动签到表不存在，id值为：{}", id);
        }
        return volActivityCheckin;
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<VolActivityCheckinEditParam> dataList = CollectionUtil.newArrayList();
         String fileName = "活动签到表导入模板_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), VolActivityCheckinEditParam.class).sheet("活动签到表").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 活动签到表导入模板下载失败：", e);
         CommonResponseUtil.renderError(response, "活动签到表导入模板下载失败");
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
                    FileUtil.FILE_SEPARATOR + "volActivityCheckinImportTemplate.xlsx"));
            // 读取excel
            List<VolActivityCheckinEditParam> volActivityCheckinEditParamList =  EasyExcel.read(tempFile).head(VolActivityCheckinEditParam.class).sheet()
                    .headRowNumber(1).doReadSync();
            List<VolActivityCheckin> allDataList = this.list();
            for (int i = 0; i < volActivityCheckinEditParamList.size(); i++) {
                JSONObject jsonObject = this.doImport(allDataList, volActivityCheckinEditParamList.get(i), i);
                if(jsonObject.getBool("success")) {
                    successCount += 1;
                } else {
                    errorCount += 1;
                    errorDetail.add(jsonObject);
                }
            }
            return JSONUtil.createObj()
                    .set("totalCount", volActivityCheckinEditParamList.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> 活动签到表导入失败：", e);
            throw new CommonException("活动签到表导入失败");
        }
    }

    public JSONObject doImport(List<VolActivityCheckin> allDataList, VolActivityCheckinEditParam volActivityCheckinEditParam, int i) {
        String id = volActivityCheckinEditParam.getId();
        if(ObjectUtil.hasEmpty(id)) {
            return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
        } else {
            try {
                int index = CollStreamUtil.toList(allDataList, VolActivityCheckin::getId).indexOf(volActivityCheckinEditParam.getId());
                VolActivityCheckin volActivityCheckin;
                boolean isAdd = false;
                if(index == -1) {
                    isAdd = true;
                    volActivityCheckin = new VolActivityCheckin();
                } else {
                    volActivityCheckin = allDataList.get(index);
                }
                BeanUtil.copyProperties(volActivityCheckinEditParam, volActivityCheckin);
                if(isAdd) {
                    allDataList.add(volActivityCheckin);
                } else {
                    allDataList.remove(index);
                    allDataList.add(index, volActivityCheckin);
                }
                this.saveOrUpdate(volActivityCheckin);
                return JSONUtil.createObj().set("success", true);
            } catch (Exception e) {
              log.error(">>> 数据导入异常：", e);
              return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
            }
        }
    }

    @Override
    public void exportData(List<VolActivityCheckinIdParam> volActivityCheckinIdParamList, HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<VolActivityCheckinEditParam> dataList;
         if(ObjectUtil.isNotEmpty(volActivityCheckinIdParamList)) {
            List<String> idList = CollStreamUtil.toList(volActivityCheckinIdParamList, VolActivityCheckinIdParam::getId);
            dataList = BeanUtil.copyToList(this.listByIds(idList), VolActivityCheckinEditParam.class);
         } else {
            dataList = BeanUtil.copyToList(this.list(), VolActivityCheckinEditParam.class);
         }
         String fileName = "活动签到表_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), VolActivityCheckinEditParam.class).sheet("活动签到表").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 活动签到表导出失败：", e);
         CommonResponseUtil.renderError(response, "活动签到表导出失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }

    /**
     * 为用户累加活动积分（单个签到记录）
     * @param checkin 签到记录
     */
    private void addActivityPointsToUser(VolActivityCheckin checkin) {
        try {
            // 获取活动信息
            VolActivity activity = volActivityService.getById(checkin.getActivityId());
            if (activity == null) {
                log.warn("活动不存在，activityId: {}");
                return;
            }
            
            // 从活动的 extJson 中读取活动积分
            String extJson = activity.getExtJson();
            if (extJson == null || extJson.isEmpty()) {
                log.debug("活动[{}]未设置扩展信息");
                return;
            }
            
            JSONObject extInfo = JSONUtil.parseObj(extJson);
            Integer activityPoints = extInfo.getInt("activityPoints");
            
            if (activityPoints == null || activityPoints <= 0) {
                log.debug("活动[{}]未设置积分或积分为0");
                return;
            }
            
            log.info("开始为用户[{}]累加活动[{}]积分: {}分", checkin.getUserId(), activity.getTitle(), activityPoints);
            
            String userId = checkin.getUserId();
            
            // 获取或创建用户扩展信息
            JSONObject userExt = clientUserApi.getOrCreateClientUserExt(userId);
            
            if (userExt != null) {
                // 累加积分
                Integer currentPoints = userExt.getInt("volunteerIntegral");
                if (currentPoints == null) {
                    currentPoints = 0;
                }
                
                int newPoints = currentPoints + activityPoints;
                
                // 更新积分
                clientUserApi.updateUserPoints(userId, newPoints);
                
                log.info("用户[{}]积分累加成功：{} -> {}", userId, currentPoints, newPoints);
            } else {
                log.warn("获取用户[{}]扩展信息失败");
            }
            
        } catch (Exception e) {
            log.error("为用户[{}]累加积分失败：{}");
        }
    }
}
