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
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.Valid;
import java.util.List;

/**
 * 活动批量报名参数
 *
 * @author jetox
 * @date  2025/10/20 06:11
 **/
@Getter
@Setter
public class VolActivityBatchSignupParam {

    /** 活动ID */
    @Schema(description = "活动ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "活动ID不能为空")
    private String activityId;

    /** 报名用户ID（操作者） */
    @Schema(description = "报名用户ID（操作者）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "报名用户ID不能为空")
    private String signupUserId;

    /** 联系电话 */
    @Schema(description = "联系电话", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "联系电话不能为空")
    private String phone;

    /** 备注信息 */
    @Schema(description = "备注信息")
    private String remark;

    /** 是否需要保险 */
    @Schema(description = "是否需要保险")
    private Boolean needInsurance;

    /** 保险类型 */
    @Schema(description = "保险类型")
    private String insuranceType;

    /** 同行人数 */
    @Schema(description = "同行人数")
    private Integer peerNumber;

    /** 实际报名用户列表 */
    @Schema(description = "实际报名用户列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "报名用户列表不能为空")
    @Valid
    private List<ActualUserItem> actualUsers;

    /**
     * 实际报名用户项
     */
    @Getter
    @Setter
    public static class ActualUserItem {
        
        /** 实际报名用户ID */
        @Schema(description = "实际报名用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "实际报名用户ID不能为空")
        private String userId;

        /** 用户姓名 */
        @Schema(description = "用户姓名")
        private String userName;

        /** 用户类型 */
        @Schema(description = "用户类型", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "用户类型不能为空")
        private String userType; // SELF, VOLUNTEER
    }
}