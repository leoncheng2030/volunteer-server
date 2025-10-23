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
package vip.xiaonuo.biz.modular.feedbackreply.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.biz.modular.feedbackreply.entity.BizFeedbackReply;
import vip.xiaonuo.biz.modular.feedbackreply.param.BizFeedbackReplyAddParam;
import vip.xiaonuo.biz.modular.feedbackreply.param.BizFeedbackReplyEditParam;
import vip.xiaonuo.biz.modular.feedbackreply.param.BizFeedbackReplyIdParam;
import vip.xiaonuo.biz.modular.feedbackreply.param.BizFeedbackReplyPageParam;
import java.io.IOException;
import java.util.List;

/**
 * 反馈回复Service接口
 *
 * @author jetox
 * @date  2025/10/19 00:15
 **/
public interface BizFeedbackReplyService extends IService<BizFeedbackReply> {

    /**
     * 获取反馈回复分页
     *
     * @author jetox
     * @date  2025/10/19 00:15
     */
    Page<BizFeedbackReply> page(BizFeedbackReplyPageParam bizFeedbackReplyPageParam);

    /**
     * 添加反馈回复
     *
     * @author jetox
     * @date  2025/10/19 00:15
     */
    void add(BizFeedbackReplyAddParam bizFeedbackReplyAddParam);

    /**
     * 编辑反馈回复
     *
     * @author jetox
     * @date  2025/10/19 00:15
     */
    void edit(BizFeedbackReplyEditParam bizFeedbackReplyEditParam);

    /**
     * 删除反馈回复
     *
     * @author jetox
     * @date  2025/10/19 00:15
     */
    void delete(List<BizFeedbackReplyIdParam> bizFeedbackReplyIdParamList);

    /**
     * 获取反馈回复详情
     *
     * @author jetox
     * @date  2025/10/19 00:15
     */
    BizFeedbackReply detail(BizFeedbackReplyIdParam bizFeedbackReplyIdParam);

    /**
     * 获取反馈回复详情
     *
     * @author jetox
     * @date  2025/10/19 00:15
     **/
    BizFeedbackReply queryEntity(String id);

    /**
     * 下载反馈回复导入模板
     *
     * @author jetox
     * @date  2025/10/19 00:15
     */
    void downloadImportTemplate(HttpServletResponse response) throws IOException;

    /**
     * 导入反馈回复
     *
     * @author jetox
     * @date  2025/10/19 00:15
     **/
    JSONObject importData(MultipartFile file);

    /**
     * 导出反馈回复
     *
     * @author jetox
     * @date  2025/10/19 00:15
     */
    void exportData(List<BizFeedbackReplyIdParam> bizFeedbackReplyIdParamList, HttpServletResponse response) throws IOException;
}
