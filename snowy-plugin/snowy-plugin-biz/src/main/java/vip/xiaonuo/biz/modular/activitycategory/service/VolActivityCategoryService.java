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
package vip.xiaonuo.biz.modular.activitycategory.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.biz.modular.activitycategory.entity.VolActivityCategory;
import vip.xiaonuo.biz.modular.activitycategory.param.VolActivityCategoryAddParam;
import vip.xiaonuo.biz.modular.activitycategory.param.VolActivityCategoryEditParam;
import vip.xiaonuo.biz.modular.activitycategory.param.VolActivityCategoryIdParam;
import vip.xiaonuo.biz.modular.activitycategory.param.VolActivityCategoryPageParam;
import java.io.IOException;
import java.util.List;

/**
 * 活动分类Service接口
 *
 * @author jetox
 * @date  2025/10/19 00:04
 **/
public interface VolActivityCategoryService extends IService<VolActivityCategory> {

    /**
     * 获取活动分类分页
     *
     * @author jetox
     * @date  2025/10/19 00:04
     */
    Page<VolActivityCategory> page(VolActivityCategoryPageParam volActivityCategoryPageParam);

    /**
     * 添加活动分类
     *
     * @author jetox
     * @date  2025/10/19 00:04
     */
    void add(VolActivityCategoryAddParam volActivityCategoryAddParam);

    /**
     * 编辑活动分类
     *
     * @author jetox
     * @date  2025/10/19 00:04
     */
    void edit(VolActivityCategoryEditParam volActivityCategoryEditParam);

    /**
     * 删除活动分类
     *
     * @author jetox
     * @date  2025/10/19 00:04
     */
    void delete(List<VolActivityCategoryIdParam> volActivityCategoryIdParamList);

    /**
     * 获取活动分类详情
     *
     * @author jetox
     * @date  2025/10/19 00:04
     */
    VolActivityCategory detail(VolActivityCategoryIdParam volActivityCategoryIdParam);

    /**
     * 获取活动分类详情
     *
     * @author jetox
     * @date  2025/10/19 00:04
     **/
    VolActivityCategory queryEntity(String id);

    /**
     * 下载活动分类导入模板
     *
     * @author jetox
     * @date  2025/10/19 00:04
     */
    void downloadImportTemplate(HttpServletResponse response) throws IOException;

    /**
     * 导入活动分类
     *
     * @author jetox
     * @date  2025/10/19 00:04
     **/
    JSONObject importData(MultipartFile file);

    /**
     * 导出活动分类
     *
     * @author jetox
     * @date  2025/10/19 00:04
     */
    void exportData(List<VolActivityCategoryIdParam> volActivityCategoryIdParamList, HttpServletResponse response) throws IOException;
}
