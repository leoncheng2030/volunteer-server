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
package vip.xiaonuo.biz.modular.activity.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 志愿活动实体
 *
 * @author jetox
 * @date  2025/10/19 00:08
 **/
@Getter
@Setter
@TableName("vol_activity")
public class VolActivity {

    /** 活动ID */
    @TableId
    @Schema(description = "活动ID")
    private String id;

    /** 活动标题 */
    @Schema(description = "活动标题")
    private String title;

    /** 封面图片URL */
    @Schema(description = "封面图片URL")
    private String coverImage;

    /** 活动详情内容（富文本） */
    @Schema(description = "活动详情内容（富文本）")
    private String content;

    /** 活动日期 */
    @Schema(description = "活动日期")
    private Date activityDate;

    /** 开始时间 */
    @Schema(description = "开始时间")
    private Date startTime;

    /** 结束时间 */
    @Schema(description = "结束时间")
    private Date endTime;

    /** 活动地点 */
    @Schema(description = "活动地点")
    private String location;

    /** 招募人数 */
    @Schema(description = "招募人数")
    private Integer recruitCount;

    /** 已报名人数 */
    @Schema(description = "已报名人数")
    private Integer signedCount;

    /** 服务时长（小时） */
    @Schema(description = "服务时长（小时）")
    private BigDecimal serviceHours;

    /** 报名截止时间 */
    @Schema(description = "报名截止时间")
    private Date signupDeadline;

    /** 联系人 */
    @Schema(description = "联系人")
    private String contactPerson;

    /** 联系电话 */
    @Schema(description = "联系电话")
    private String contactPhone;

    /** 活动状态 */
    @Schema(description = "活动状态")
    private String status;

    /** 组织ID */
    @Schema(description = "组织ID")
    private String orgId;

    /** 创建用户ID */
    @Schema(description = "创建用户ID")
    private String createUserId;

    /** 排序码 */
    @Schema(description = "排序码")
    private Integer sortCode;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 扩展信息 */
    @Schema(description = "扩展信息")
    private String extJson;

    /** 删除标志 */
    @Schema(description = "删除标志")
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private String deleteFlag;

    /** 创建时间 */
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /** 创建用户 */
    @Schema(description = "创建用户")
    @TableField(fill = FieldFill.INSERT)
    private String createUser;

    /** 修改时间 */
    @Schema(description = "修改时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    /** 修改用户 */
    @Schema(description = "修改用户")
    @TableField(fill = FieldFill.UPDATE)
    private String updateUser;
}
