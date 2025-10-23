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
package vip.xiaonuo.biz.modular.activitycheckin.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 活动签到表实体
 *
 * @author jetox
 * @date  2025/10/22 06:17
 **/
@Getter
@Setter
@TableName("vol_activity_checkin")
public class VolActivityCheckin {

    /** 签到ID */
    @TableId
    @Schema(description = "签到ID")
    private String id;

    /** 活动ID */
    @Schema(description = "活动ID")
    private String activityId;

    /** 用户ID */
    @Schema(description = "用户ID")
    private String userId;

    /** 报名记录ID */
    @Schema(description = "报名记录ID")
    private String signupId;

    /** 签到类型：CHECKIN-签到，CHECKOUT-签退 */
    @Schema(description = "签到类型：CHECKIN-签到，CHECKOUT-签退")
    private String checkinType;

    /** 签到时间 */
    @Schema(description = "签到时间")
    private Date checkinTime;

    /** 签退时间（自动或手动） */
    @Schema(description = "签退时间")
    private Date checkoutTime;

    /** 实际服务时长（小时） */
    @Schema(description = "实际服务时长")
    private BigDecimal actualHours;

    /** 是否提前离开（0-否，1-是） */
    @Schema(description = "是否提前离开")
    private Integer isEarlyLeave;

    /** 签退类型：AUTO-自动签退，MANUAL-手动签退 */
    @Schema(description = "签退类型：AUTO-自动，MANUAL-手动")
    private String checkoutType;

    /** 签到地点 */
    @Schema(description = "签到地点")
    private String location;

    /** 签到纬度 */
    @Schema(description = "签到纬度")
    private BigDecimal locationLat;

    /** 签到经度 */
    @Schema(description = "签到经度")
    private BigDecimal locationLng;

    /** 设备信息 */
    @Schema(description = "设备信息")
    private String deviceInfo;

    /** 签到照片URL */
    @Schema(description = "签到照片URL")
    private String photoUrl;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 状态：NORMAL-正常，LATE-迟到，EARLY_LEAVE-早退，ABSENTEEISM-旷工 */
    @Schema(description = "状态：NORMAL-正常，LATE-迟到，EARLY_LEAVE-早退，ABSENTEEISM-旷工")
    private String status;

    /** 创建时间 */
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /** 创建用户 */
    @Schema(description = "创建用户")
    @TableField(fill = FieldFill.INSERT)
    private String createUser;
}
