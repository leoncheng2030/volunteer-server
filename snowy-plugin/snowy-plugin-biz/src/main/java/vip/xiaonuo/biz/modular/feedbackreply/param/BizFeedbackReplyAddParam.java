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
package vip.xiaonuo.biz.modular.feedbackreply.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 反馈回复添加参数
 *
 * @author jetox
 * @date  2025/10/19 00:15
 **/
@Getter
@Setter
public class BizFeedbackReplyAddParam {

    /** 反馈ID */
    @Schema(description = "反馈ID")
    private String feedbackId;

    /** 回复内容 */
    @Schema(description = "回复内容")
    private String content;

    /** 回复类型 */
    @Schema(description = "回复类型")
    private String replyType;

    /** 回复人ID */
    @Schema(description = "回复人ID")
    private String replyUserId;

    /** 回复人姓名 */
    @Schema(description = "回复人姓名")
    private String replyUserName;

    /** 是否内部回复 */
    @Schema(description = "是否内部回复")
    private Integer isInternal;

    /** 附件URL */
    @Schema(description = "附件URL")
    private String attachmentUrl;

}
