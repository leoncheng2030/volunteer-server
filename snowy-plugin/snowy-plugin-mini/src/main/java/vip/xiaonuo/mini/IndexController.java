package vip.xiaonuo.mini;

import cn.hutool.json.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.dev.modular.config.service.DevConfigService;
import vip.xiaonuo.dev.modular.slideshow.service.DevSlideshowService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Tag(name = "小程序首页接口")
@RestController
@Validated
public class IndexController {

    @Resource
    private DevConfigService devConfigService;

    @Resource
    private DevSlideshowService devSlideshowService;
    /**
     * 获取基础配置
     *
     * @author yubaoshan
     * @date 2022/4/24 20:00
     */
    @Operation(summary = "获取基础配置")
    @GetMapping("/mini/config/sysBaseConfig")
    public CommonResult<Map<String, String>> sysBaseConfig() {
        List<vip.xiaonuo.dev.modular.config.entity.DevConfig> baseConfigList = devConfigService.sysBaseList();
        Map<String, String> stringStringMap = new HashMap<>();
        baseConfigList.forEach(config -> {
            if (config.getConfigKey() != null && Objects.equals(config.getConfigKey(), "SNOWY_SYS_LOGO")){
                stringStringMap.put("logo", config.getConfigValue());
            }
            if (config.getConfigKey() != null && Objects.equals(config.getConfigKey(), "SNOWY_SYS_NAME")){
                stringStringMap.put("name", config.getConfigValue());
            }
            if (config.getConfigKey() != null && Objects.equals(config.getConfigKey(), "SNOWY_SYS_DESC")){
                stringStringMap.put("desc", config.getConfigValue());
            }
            if (config.getConfigKey() != null && Objects.equals(config.getConfigKey(), "SNOWY_SYS_COPYRIGHT")){
                stringStringMap.put("copyright", config.getConfigValue());
            }
            if (config.getConfigKey() != null && Objects.equals(config.getConfigKey(), "SNOWY_SYS_VERSION")){
                stringStringMap.put("version", config.getConfigValue());
            }
        });
        return CommonResult.data(stringStringMap);
    }
    /**
     * 获取广告轮播图
     *
     * @author yubaoshan
     * @date 2022/4/24 20:00
     */
    @Operation(summary = "获取轮播图")
    @GetMapping("/mini/slider/sysBanner")
    public CommonResult<List<JSONObject>> sysBanner() {
        List<JSONObject> jsonObjectList = devSlideshowService.getListByPlace("CLIENT_MOBILE");
        return CommonResult.data(jsonObjectList);
    }
}
