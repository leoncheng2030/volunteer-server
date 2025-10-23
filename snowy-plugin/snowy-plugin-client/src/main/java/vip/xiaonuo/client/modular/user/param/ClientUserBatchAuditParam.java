package vip.xiaonuo.client.modular.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * 小雷锋批量审核参数
 *
 * @author jetox
 * @date 2025/10/20 06:42
 **/
@Getter
@Setter
@Schema(description = "小雷锋批量审核参数")
public class ClientUserBatchAuditParam {

    @Schema(description = "用户ID列表")
    @NotEmpty(message = "用户ID列表不能为空")
    private List<String> ids;
}