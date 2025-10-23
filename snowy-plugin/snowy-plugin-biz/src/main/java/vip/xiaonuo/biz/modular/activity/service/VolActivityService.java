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
package vip.xiaonuo.biz.modular.activity.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.biz.modular.activity.entity.VolActivity;
import vip.xiaonuo.biz.modular.activity.param.VolActivityAddParam;
import vip.xiaonuo.biz.modular.activity.param.VolActivityEditParam;
import vip.xiaonuo.biz.modular.activity.param.VolActivityIdParam;
import vip.xiaonuo.biz.modular.activity.param.VolActivityPageParam;
import java.io.IOException;
import java.util.List;

/**
 * 志愿活动Service接口
 *
 * @author jetox
 * @date  2025/10/19 00:08
 **/
public interface VolActivityService extends IService<VolActivity> {

    /**
     * 获取志愿活动分页
     *
     * @author jetox
     * @date  2025/10/19 00:08
     */
    Page<VolActivity> page(VolActivityPageParam volActivityPageParam);

    /**
     * 添加志愿活动
     *
     * @author jetox
     * @date  2025/10/19 00:08
     */
    void add(VolActivityAddParam volActivityAddParam);

    /**
     * 编辑志愿活动
     *
     * @author jetox
     * @date  2025/10/19 00:08
     */
    void edit(VolActivityEditParam volActivityEditParam);

    /**
     * 删除志愿活动
     *
     * @author jetox
     * @date  2025/10/19 00:08
     */
    void delete(List<VolActivityIdParam> volActivityIdParamList);

    /**
     * 获取志愿活动详情
     *
     * @author jetox
     * @date  2025/10/19 00:08
     */
    VolActivity detail(VolActivityIdParam volActivityIdParam);

    /**
     * 获取志愿活动详情
     *
     * @author jetox
     * @date  2025/10/19 00:08
     **/
    VolActivity queryEntity(String id);

    /**
     * 下载志愿活动导入模板
     *
     * @author jetox
     * @date  2025/10/19 00:08
     */
    void downloadImportTemplate(HttpServletResponse response) throws IOException;

    /**
     * 导入志愿活动
     *
     * @author jetox
     * @date  2025/10/19 00:08
     **/
    JSONObject importData(MultipartFile file);

    /**
     * 导出志愿活动
     *
     * @author jetox
     * @date  2025/10/19 00:08
     */
    void exportData(List<VolActivityIdParam> volActivityIdParamList, HttpServletResponse response) throws IOException;
}
