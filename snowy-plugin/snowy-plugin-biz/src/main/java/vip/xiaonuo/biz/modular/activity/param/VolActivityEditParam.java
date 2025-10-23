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
package vip.xiaonuo.biz.modular.activity.param;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * 志愿活动编辑参数
 *
 * @author jetox
 * @date  2025/10/19 00:08
 **/
@Getter
@Setter
public class VolActivityEditParam {

    /** 活动ID */
    @ExcelProperty("活动ID")
    @Schema(description = "活动ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "id不能为空")
    private String id;

    /** 活动标题 */
    @ExcelProperty("活动标题")
    @Schema(description = "活动标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "title不能为空")
    private String title;

    /** 封面图片URL */
    @ExcelProperty("封面图片URL")
    @Schema(description = "封面图片URL", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "coverImage不能为空")
    private String coverImage;

    /** 活动详情内容（富文本） */
    @ExcelProperty("活动详情内容（富文本）")
    @Schema(description = "活动详情内容（富文本）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "content不能为空")
    private String content;

    /** 活动日期 */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ExcelProperty("活动日期")
    @Schema(description = "活动日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "activityDate不能为空")
    private Date activityDate;
    /** 开始时间 */
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    @ExcelProperty("开始时间")
    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "startTime不能为空")
    private Time startTime;

    /** 结束时间 */
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    @ExcelProperty("结束时间")
    @Schema(description = "结束时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "endTime不能为空")
    private Time endTime;

    /** 活动地点 */
    @ExcelProperty("活动地点")
    @Schema(description = "活动地点", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "location不能为空")
    private String location;

    /** 招募人数 */
    @ExcelProperty("招募人数")
    @Schema(description = "招募人数", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "recruitCount不能为空")
    private Integer recruitCount;

    /** 已报名人数 */
    @ExcelProperty("已报名人数")
    @Schema(description = "已报名人数")
    private Integer signedCount;

    /** 服务时长（小时） */
    @ExcelProperty("服务时长（小时）")
    @Schema(description = "服务时长（小时）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "serviceHours不能为空")
    private BigDecimal serviceHours;

    /** 报名截止时间 */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("报名截止时间")
    @Schema(description = "报名截止时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "signupDeadline不能为空")
    private Date signupDeadline;

    /** 联系人 */
    @ExcelProperty("联系人")
    @Schema(description = "联系人", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "contactPerson不能为空")
    private String contactPerson;

    /** 联系电话 */
    @ExcelProperty("联系电话")
    @Schema(description = "联系电话", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "contactPhone不能为空")
    private String contactPhone;

    /** 活动状态 */
    @ExcelProperty("活动状态")
    @Schema(description = "活动状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "status不能为空")
    private String status;

    /** 组织ID */
    @ExcelProperty("组织ID")
    @Schema(description = "组织ID")
    private String orgId;

    /** 创建用户ID */
    @ExcelProperty("创建用户ID")
    @Schema(description = "创建用户ID")
    private String createUserId;

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

    /** 是否允许重复报名 */
    @Schema(description = "是否允许重复报名")
    private Boolean allowResignup;

}
