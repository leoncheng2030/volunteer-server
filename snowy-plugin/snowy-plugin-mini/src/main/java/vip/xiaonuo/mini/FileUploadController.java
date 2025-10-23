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
package vip.xiaonuo.mini;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.dev.modular.config.service.DevConfigService;
import vip.xiaonuo.dev.modular.file.enums.DevFileEngineTypeEnum;
import vip.xiaonuo.dev.modular.file.service.DevFileService;

import java.util.HashMap;
import java.util.Map;

/**
 * 小程序文件上传控制器
 *
 * @author jetox
 * @date  2025/10/19 10:00
 */
@Slf4j
@Tag(name = "小程序文件上传接口")
@RestController
@Validated
@RequestMapping("/mini/file")
public class FileUploadController {

    /** 默认文件引擎 */
    private static final String SNOWY_SYS_DEFAULT_FILE_ENGINE_KEY = "SNOWY_SYS_DEFAULT_FILE_ENGINE_KEY";

    @Resource
    private DevConfigService devConfigService;

    @Resource
    private DevFileService devFileService;

    /**
     * 小程序图片上传接口
     * 
     * @author jetox
     * @date 2025/10/19 10:00
     **/
    @ApiOperationSupport(order = 1)
    @Operation(summary = "小程序图片上传")
    @CommonLog("小程序图片上传")
    @PostMapping("/uploadImage")
    public CommonResult<Map<String, Object>> uploadImage(@RequestPart("file") MultipartFile file) {
        try {
            // 验证文件类型
            if (!isValidImageFile(file)) {
                return CommonResult.error("只支持上传图片文件（jpg, jpeg, png, gif）");
            }

            // 验证文件大小（限制为5MB）
            if (file.getSize() > 5 * 1024 * 1024) {
                return CommonResult.error("图片文件大小不能超过5MB");
            }

            // 使用默认文件引擎上传
            String defaultEngine = devConfigService.getValueByKey(SNOWY_SYS_DEFAULT_FILE_ENGINE_KEY);
            if (defaultEngine == null || defaultEngine.trim().isEmpty()) {
                defaultEngine = DevFileEngineTypeEnum.LOCAL.getValue();
            }

            String fileUrl = devFileService.uploadReturnUrl(defaultEngine, file);
            
            // 构造返回数据
            Map<String, Object> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("size", file.getSize());
            result.put("originalName", file.getOriginalFilename());
            result.put("contentType", file.getContentType());
            
            log.info("小程序图片上传成功: {}", fileUrl);
            
            return CommonResult.data(result);
        } catch (Exception e) {
            log.error("小程序图片上传失败", e);
            return CommonResult.error("图片上传失败：" + e.getMessage());
        }
    }

    /**
     * 小程序头像上传接口
     * 
     * @author jetox
     * @date 2025/10/19 10:00
     **/
    @ApiOperationSupport(order = 2)
    @Operation(summary = "小程序头像上传")
    @CommonLog("小程序头像上传")
    @PostMapping("/uploadAvatar")
    public CommonResult<Map<String, Object>> uploadAvatar(@RequestPart("file") MultipartFile file) {
        try {
            // 验证文件类型（头像只支持 jpg, png）
            if (!isValidAvatarFile(file)) {
                return CommonResult.error("头像只支持 JPG 或 PNG 格式");
            }
            // 使用本地存储上传头像
            String fileUrl = devFileService.uploadReturnUrl(DevFileEngineTypeEnum.LOCAL.getValue(), file);
            
            // 构造返回数据
            Map<String, Object> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("size", file.getSize());
            result.put("originalName", file.getOriginalFilename());
            result.put("contentType", file.getContentType());
            
            log.info("小程序头像上传成功: {}", fileUrl);
            
            return CommonResult.data(result);
        } catch (Exception e) {
            log.error("小程序头像上传失败", e);
            return CommonResult.error("头像上传失败：" + e.getMessage());
        }
    }

    /**
     * 验证是否为有效的图片文件
     * 
     * @author jetox
     * @date 2025/10/19 10:00
     **/
    private boolean isValidImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return false;
        }
        
        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }
        
        // 检查文件扩展名
        String extension = originalFilename.toLowerCase();
        boolean validExtension = extension.endsWith(".jpg") || 
                               extension.endsWith(".jpeg") || 
                               extension.endsWith(".png") || 
                               extension.endsWith(".gif");
        
        // 检查MIME类型
        boolean validContentType = contentType.equals("image/jpeg") ||
                                  contentType.equals("image/jpg") ||
                                  contentType.equals("image/png") ||
                                  contentType.equals("image/gif");
        
        return validExtension && validContentType;
    }

    /**
     * 验证是否为有效的头像文件
     * 
     * @author jetox
     * @date 2025/10/19 10:00
     **/
    private boolean isValidAvatarFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return false;
        }
        
        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }
        
        // 检查文件扩展名（头像只支持 jpg, png）
        String extension = originalFilename.toLowerCase();
        boolean validExtension = extension.endsWith(".jpg") || 
                               extension.endsWith(".jpeg") || 
                               extension.endsWith(".png");
        
        // 检查MIME类型
        boolean validContentType = contentType.equals("image/jpeg") ||
                                  contentType.equals("image/jpg") ||
                                  contentType.equals("image/png");
        
        return validExtension && validContentType;
    }
}