package vip.xiaonuo.auth.core.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaBaseClientLoginUserExt {
    /** 服务时长 */
    @Schema(description = "服务时长")
    private Integer serviceDuration;

    /** 服务次数 */
    @Schema(description = "服务次数")
    private Integer serviceTimes;

    /** 志愿积分 */
    @Schema(description = "志愿积分")
    private Integer volunteerIntegral;
}
