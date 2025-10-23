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

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vip.xiaonuo.biz.modular.notice.entity.BizNotice;
import vip.xiaonuo.biz.modular.notice.param.BizNoticePageParam;
import vip.xiaonuo.biz.modular.notice.param.BizNoticeIdParam;
import vip.xiaonuo.biz.modular.notice.service.BizNoticeService;
import vip.xiaonuo.common.pojo.CommonResult;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 小程序通知公告接口控制器
 *
 * @author jetox
 * @date  2025/10/19 00:08
 */
@Slf4j
@Tag(name = "小程序通知公告接口")
@RestController
@RequestMapping("/mini/notice")
@Validated
public class NoticeController {

    @Resource
    private BizNoticeService bizNoticeService;

    /**
     * 获取通知公告分页
     *
     * @author jetox
     * @date  2025/10/19 00:08
     */
    @Operation(summary = "获取通知公告分页")
    @GetMapping("/page")
    public CommonResult<Page<BizNotice>> page(@Valid BizNoticePageParam bizNoticePageParam) {
        // 只获取发布位置包含CLIENT_MOBILE的公告
        if (bizNoticePageParam.getPlace() == null || bizNoticePageParam.getPlace().isEmpty()) {
            bizNoticePageParam.setPlace("CLIENT_MOBILE");
        }
        Page<BizNotice> result = bizNoticeService.page(bizNoticePageParam);
        return CommonResult.data(result);
    }

    /**
     * 获取通知公告详情
     *
     * @author jetox
     * @date  2025/10/19 00:08
     */
    @Operation(summary = "获取通知公告详情")
    @GetMapping("/detail")
    public CommonResult<BizNotice> detail(@Valid BizNoticeIdParam bizNoticeIdParam) {
        BizNotice result = bizNoticeService.detail(bizNoticeIdParam);
        return CommonResult.data(result);
    }

    /**
     * 获取最新通知公告
     *
     * @author jetox
     * @date  2025/10/19 00:08
     */
    @Operation(summary = "获取最新通知公告")
    @GetMapping("/latest")
    public CommonResult<List<BizNotice>> getLatestNotices(@RequestParam(defaultValue = "5") Integer limit) {
        BizNoticePageParam param = new BizNoticePageParam();
        param.setCurrent(1);
        param.setSize(limit);
        // 只获取发布位置包含CLIENT_MOBILE的公告
        param.setPlace("CLIENT_MOBILE");
        // 按创建时间降序排列获取最新通知公告
        param.setSortField("CREATE_TIME");
        param.setSortOrder("descend");
        
        Page<BizNotice> pageResult = bizNoticeService.page(param);
        return CommonResult.data(pageResult.getRecords());
    }

    /**
     * 根据类型获取通知公告
     *
     * @author jetox
     * @date  2025/10/19 00:08
     */
    @Operation(summary = "根据类型获取通知公告")
    @GetMapping("/type")
    public CommonResult<List<BizNotice>> getNoticesByType(@RequestParam String type, @RequestParam(defaultValue = "5") Integer limit) {
        BizNoticePageParam param = new BizNoticePageParam();
        param.setCurrent(1);
        param.setSize(limit);
        param.setType(type);
        // 只获取发布位置包含CLIENT_MOBILE的公告
        param.setPlace("CLIENT_MOBILE");
        param.setSortField("CREATE_TIME");
        param.setSortOrder("descend");
        
        Page<BizNotice> pageResult = bizNoticeService.page(param);
        return CommonResult.data(pageResult.getRecords());
    }

    /**
     * 获取通知公告类型枚举
     *
     * @author jetox
     * @date  2025/10/19 00:08
     */
    @Operation(summary = "获取通知公告类型枚举")
    @GetMapping("/typeEnum")
    public CommonResult<java.util.Map<String, String>> getNoticeTypeEnum() {
        java.util.Map<String, String> result = new java.util.HashMap<>();
        result.put("NOTICE", "通知");
        result.put("ANNOUNCEMENT", "公告");
        result.put("ACTIVITY", "活动通知");
        result.put("SYSTEM", "系统通知");
        return CommonResult.data(result);
    }

    /**
     * 获取发布位置枚举
     *
     * @author jetox
     * @date  2025/10/19 00:08
     */
    @Operation(summary = "获取发布位置枚举")
    @GetMapping("/placeEnum")
    public CommonResult<java.util.Map<String, String>> getPlaceEnum() {
        java.util.Map<String, String> result = new java.util.HashMap<>();
        result.put("CLIENT_MOBILE", "小程序端");
        result.put("ADMIN_WEB", "管理后台");
        result.put("ALL", "全部");
        return CommonResult.data(result);
    }
}