package vip.xiaonuo.client.modular.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;

/**
 * 小雷锋审核拒绝参数
 *
 * @author jetox
 * @date 2025/10/20 06:42
 **/
@Getter
@Setter
@Schema(description = "小雷锋审核拒绝参数")
public class ClientUserRejectParam {

    @Schema(description = "用户ID")
    @NotBlank(message = "用户ID不能为空")
    private String id;

    @Schema(description = "拒绝原因")
    @NotBlank(message = "拒绝原因不能为空")
    private String reason;
}