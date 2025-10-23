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
package vip.xiaonuo.biz.modular.activitysignup.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.biz.modular.activitysignup.entity.VolActivitySignup;
import vip.xiaonuo.biz.modular.activitysignup.param.VolActivitySignupAddParam;
import vip.xiaonuo.biz.modular.activitysignup.param.VolActivitySignupEditParam;
import vip.xiaonuo.biz.modular.activitysignup.param.VolActivitySignupIdParam;
import vip.xiaonuo.biz.modular.activitysignup.param.VolActivitySignupPageParam;
import java.io.IOException;
import java.util.List;

/**
 * 报名记录Service接口
 *
 * @author jetox
 * @date  2025/10/19 00:11
 **/
public interface VolActivitySignupService extends IService<VolActivitySignup> {

    /**
     * 获取报名记录分页
     *
     * @author jetox
     * @date  2025/10/19 00:11
     */
    Page<VolActivitySignup> page(VolActivitySignupPageParam volActivitySignupPageParam);

    /**
     * 添加报名记录
     *
     * @author jetox
     * @date  2025/10/19 00:11
     */
    void add(VolActivitySignupAddParam volActivitySignupAddParam);

    /**
     * 编辑报名记录
     *
     * @author jetox
     * @date  2025/10/19 00:11
     */
    void edit(VolActivitySignupEditParam volActivitySignupEditParam);

    /**
     * 删除报名记录
     *
     * @author jetox
     * @date  2025/10/19 00:11
     */
    void delete(List<VolActivitySignupIdParam> volActivitySignupIdParamList);

    /**
     * 获取报名记录详情
     *
     * @author jetox
     * @date  2025/10/19 00:11
     */
    VolActivitySignup detail(VolActivitySignupIdParam volActivitySignupIdParam);

    /**
     * 获取报名记录详情
     *
     * @author jetox
     * @date  2025/10/19 00:11
     **/
    VolActivitySignup queryEntity(String id);

    /**
     * 下载报名记录导入模板
     *
     * @author jetox
     * @date  2025/10/19 00:11
     */
    void downloadImportTemplate(HttpServletResponse response) throws IOException;

    /**
     * 导入报名记录
     *
     * @author jetox
     * @date  2025/10/19 00:11
     **/
    JSONObject importData(MultipartFile file);

    /**
     * 导出报名记录
     *
     * @author jetox
     * @date  2025/10/19 00:11
     */
    void exportData(List<VolActivitySignupIdParam> volActivitySignupIdParamList, HttpServletResponse response) throws IOException;
}
