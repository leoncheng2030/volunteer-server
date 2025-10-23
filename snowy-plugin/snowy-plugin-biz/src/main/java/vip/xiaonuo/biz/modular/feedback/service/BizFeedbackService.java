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
package vip.xiaonuo.biz.modular.feedback.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.biz.modular.feedback.entity.BizFeedback;
import vip.xiaonuo.biz.modular.feedback.param.BizFeedbackAddParam;
import vip.xiaonuo.biz.modular.feedback.param.BizFeedbackEditParam;
import vip.xiaonuo.biz.modular.feedback.param.BizFeedbackIdParam;
import vip.xiaonuo.biz.modular.feedback.param.BizFeedbackPageParam;
import java.io.IOException;
import java.util.List;

/**
 * 用户反馈Service接口
 *
 * @author jetox
 * @date  2025/10/19 00:13
 **/
public interface BizFeedbackService extends IService<BizFeedback> {

    /**
     * 获取用户反馈分页
     *
     * @author jetox
     * @date  2025/10/19 00:13
     */
    Page<BizFeedback> page(BizFeedbackPageParam bizFeedbackPageParam);

    /**
     * 添加用户反馈
     *
     * @author jetox
     * @date  2025/10/19 00:13
     */
    void add(BizFeedbackAddParam bizFeedbackAddParam);

    /**
     * 编辑用户反馈
     *
     * @author jetox
     * @date  2025/10/19 00:13
     */
    void edit(BizFeedbackEditParam bizFeedbackEditParam);

    /**
     * 删除用户反馈
     *
     * @author jetox
     * @date  2025/10/19 00:13
     */
    void delete(List<BizFeedbackIdParam> bizFeedbackIdParamList);

    /**
     * 获取用户反馈详情
     *
     * @author jetox
     * @date  2025/10/19 00:13
     */
    BizFeedback detail(BizFeedbackIdParam bizFeedbackIdParam);

    /**
     * 获取用户反馈详情
     *
     * @author jetox
     * @date  2025/10/19 00:13
     **/
    BizFeedback queryEntity(String id);

    /**
     * 下载用户反馈导入模板
     *
     * @author jetox
     * @date  2025/10/19 00:13
     */
    void downloadImportTemplate(HttpServletResponse response) throws IOException;

    /**
     * 导入用户反馈
     *
     * @author jetox
     * @date  2025/10/19 00:13
     **/
    JSONObject importData(MultipartFile file);

    /**
     * 导出用户反馈
     *
     * @author jetox
     * @date  2025/10/19 00:13
     */
    void exportData(List<BizFeedbackIdParam> bizFeedbackIdParamList, HttpServletResponse response) throws IOException;
}
