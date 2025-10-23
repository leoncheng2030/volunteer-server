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
package vip.xiaonuo.biz.modular.activitysignup.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

/**
 * 活动报名参数
 *
 * @author jetox
 * @date  2025/10/19 00:08
 **/
@Getter
@Setter
public class VolActivitySignupParam {

    /** 活动ID */
    @Schema(description = "活动ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "活动ID不能为空")
    private String activityId;

    /** 报名用户ID */
    @Schema(description = "报名用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "报名用户ID不能为空")
    private String signupUserId;

    /** 实际报名用户ID（监护人为小雷锋报名时使用） */
    @Schema(description = "实际报名用户ID（监护人为小雷锋报名时使用）")
    private String actualUserId;
}