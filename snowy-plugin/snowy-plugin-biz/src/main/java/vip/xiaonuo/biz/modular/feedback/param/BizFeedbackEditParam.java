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
package vip.xiaonuo.biz.modular.feedback.param;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户反馈编辑参数
 *
 * @author jetox
 * @date  2025/10/19 00:13
 **/
@Getter
@Setter
public class BizFeedbackEditParam {

    /** 反馈ID */
    @ExcelProperty("反馈ID")
    @Schema(description = "反馈ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "id不能为空")
    private String id;

    /** 反馈标题 */
    @ExcelProperty("反馈标题")
    @Schema(description = "反馈标题")
    private String title;

    /** 反馈内容 */
    @ExcelProperty("反馈内容")
    @Schema(description = "反馈内容")
    private String content;

    /** 反馈类型 */
    @ExcelProperty("反馈类型")
    @Schema(description = "反馈类型")
    private String type;

    /** 联系人姓名 */
    @ExcelProperty("联系人姓名")
    @Schema(description = "联系人姓名")
    private String contactName;

    /** 联系电话 */
    @ExcelProperty("联系电话")
    @Schema(description = "联系电话")
    private String contactPhone;

    /** 联系邮箱 */
    @ExcelProperty("联系邮箱")
    @Schema(description = "联系邮箱")
    private String contactEmail;

    /** 附件URL */
    @ExcelProperty("附件URL")
    @Schema(description = "附件URL")
    private String attachmentUrl;

    /** 处理状态：PENDING-待处理，PROCESSING-处理中，RESOLVED-已解决，CLOSED-已关闭 */
    @ExcelProperty("处理状态：PENDING-待处理，PROCESSING-处理中，RESOLVED-已解决，CLOSED-已关闭")
    @Schema(description = "处理状态：PENDING-待处理，PROCESSING-处理中，RESOLVED-已解决，CLOSED-已关闭")
    private String status;

    /** 优先级：LOW-低，NORMAL-中，HIGH-高，URGENT-紧急 */
    @ExcelProperty("优先级：LOW-低，NORMAL-中，HIGH-高，URGENT-紧急")
    @Schema(description = "优先级：LOW-低，NORMAL-中，HIGH-高，URGENT-紧急")
    private String priority;

    /** 反馈用户ID */
    @ExcelProperty("反馈用户ID")
    @Schema(description = "反馈用户ID")
    private String userId;

    /** 用户类型：VOLUNTEER-志愿者，ORGANIZER-组织者，ADMIN-管理员 */
    @ExcelProperty("用户类型：VOLUNTEER-志愿者，ORGANIZER-组织者，ADMIN-管理员")
    @Schema(description = "用户类型：VOLUNTEER-志愿者，ORGANIZER-组织者，ADMIN-管理员")
    private String userType;

    /** 分配给处理人ID */
    @ExcelProperty("分配给处理人ID")
    @Schema(description = "分配给处理人ID")
    private String assignTo;

    /** 处理时间 */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelProperty("处理时间")
    @Schema(description = "处理时间")
    private Date processTime;

    /** 解决时间 */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelProperty("解决时间")
    @Schema(description = "解决时间")
    private Date resolveTime;

    /** 处理结果 */
    @ExcelProperty("处理结果")
    @Schema(description = "处理结果")
    private String resolveResult;

    /** 满意度评分（1-5分） */
    @ExcelProperty("满意度评分（1-5分）")
    @Schema(description = "满意度评分（1-5分）")
    private Integer satisfaction;

    /** 评价内容 */
    @ExcelProperty("评价内容")
    @Schema(description = "评价内容")
    private String evaluation;

    /** 排序码 */
    @ExcelProperty("排序码")
    @Schema(description = "排序码")
    private Integer sortCode;

    /** 备注 */
    @ExcelProperty("备注")
    @Schema(description = "备注")
    private String remark;

    /** 扩展信息 */
    @ExcelProperty("扩展信息")
    @Schema(description = "扩展信息")
    private String extJson;

}
