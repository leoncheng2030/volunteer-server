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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.hutool.extra.pinyin.PinyinUtil;
import vip.xiaonuo.auth.core.util.StpClientLoginUserUtil;
import vip.xiaonuo.client.modular.user.entity.ClientUser;
import vip.xiaonuo.client.modular.user.service.ClientUserService;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.client.modular.user.param.ClientUserAddParam;
import vip.xiaonuo.client.modular.user.param.ClientUserIdParam;
import vip.xiaonuo.client.modular.user.param.ClientUserEditParam;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 小程序小雷锋管理接口控制器
 *
 * @author jetox
 * @date  2025/10/19 00:08
 */
@Slf4j
@Tag(name = "小程序小雷锋管理接口")
@RestController
@Validated
public class VolunteerController {

    @Resource
    private ClientUserService clientUserService;
    

    /**
     * 获取监护人下的小雷锋列表
     *
     * @author jetox
     * @date  2025/10/19 00:08
     */
    @Operation(summary = "获取监护人下的小雷锋列表")
    @GetMapping("/mini/volunteer/list")
    public CommonResult<List<ClientUser>> list() {
        // 获取当前登录用户
        String currentUserId = StpClientLoginUserUtil.getClientLoginUser().getId();
        if (currentUserId == null) {
            return CommonResult.error("用户未登录");
        }
        
        // 查询当前用户下的小雷锋
        List<ClientUser> volunteerList = clientUserService.getVolunteerListByParentId(currentUserId);
        return CommonResult.data(volunteerList);
    }

    /**
     * 获取小雷锋详情
     *
     * @author jetox
     * @date  2025/10/22 00:00
     */
    @Operation(summary = "获取小雷锋详情")
    @GetMapping("/mini/volunteer/detail")
    public CommonResult<ClientUser> detail(String id) {
        try {
            // 获取当前登录用户
            String currentUserId = StpClientLoginUserUtil.getClientLoginUser().getId();
            if (currentUserId == null) {
                return CommonResult.error("用户未登录");
            }
            
            // 查询小雷锋信息
            ClientUser volunteer = clientUserService.queryEntity(id);
            if (volunteer == null) {
                return CommonResult.error("小雷锋不存在");
            }
            
            // 验证权限：只能查看自己的小雷锋
            if (!currentUserId.equals(volunteer.getParentId())) {
                return CommonResult.error("无权限查看该小雷锋");
            }
            
            return CommonResult.data(volunteer);
        } catch (Exception e) {
            log.error("获取小雷锋详情失败", e);
            return CommonResult.error("获取失败：" + e.getMessage());
        }
    }

    /**
     * 添加小雷锋
     *
     * @author jetox
     * @date  2025/10/19 00:08
     */
    @Operation(summary = "添加小雷锋")
    @PostMapping("/mini/volunteer/add")
    public CommonResult<String> add(@Valid @RequestBody ClientUserAddParam clientUserAddParam) {
        try {
            // 获取当前登录用户
            String currentUserId = StpClientLoginUserUtil.getClientLoginUser().getId();
            if (currentUserId == null) {
                return CommonResult.error("用户未登录");
            }
            
            // 验证当前用户是否为监护人
            ClientUser currentUser = clientUserService.queryEntity(currentUserId);
            if (currentUser == null || !"guardian".equals(currentUser.getType())) {
                return CommonResult.error("只有监护人可以添加小雷锋");
            }
            
            // 设置小雷锋的类型和监护人ID
            clientUserAddParam.setType("volunteer");
            clientUserAddParam.setParentId(currentUserId);
            
            // 生成账号（基于姓名拼音）
            String generatedAccount = generateAccountFromName(clientUserAddParam.getName());
            clientUserAddParam.setAccount(generatedAccount);
            
            // 设置密码与监护人一致
            clientUserAddParam.setPassword(currentUser.getPassword());
            
            // 添加小雷锋
            clientUserService.add(clientUserAddParam, "SYSTEM_ADD");
            return CommonResult.ok("添加成功，账号：" + generatedAccount);
        } catch (Exception e) {
            log.error("添加小雷锋失败", e);
            return CommonResult.error("添加失败：" + e.getMessage());
        }
    }

    /**
     * 删除小雷锋
     *
     * @author jetox
     * @date  2025/10/19 00:08
     */
    @Operation(summary = "删除小雷锋")
    @PostMapping("/mini/volunteer/delete")
    public CommonResult<String> delete(@Valid @RequestBody ClientUserIdParam clientUserIdParam) {
        try {
            // 获取当前登录用户
            String currentUserId = StpClientLoginUserUtil.getClientLoginUser().getId();
            if (currentUserId == null) {
                return CommonResult.error("用户未登录");
            }
            
            // 验证权限：查询小雷锋信息
            ClientUser volunteer = clientUserService.queryEntity(clientUserIdParam.getId());
            if (!currentUserId.equals(volunteer.getParentId())) {
                return CommonResult.error("无权限删除该小雷锋");
            }
            
            // 删除小雷锋
            clientUserService.delete(List.of(clientUserIdParam));
            return CommonResult.ok("删除成功");
        } catch (Exception e) {
            log.error("删除小雷锋失败", e);
            return CommonResult.error("删除失败：" + e.getMessage());
        }
    }

    /**
     * 更新小雷锋信息
     *
     * @author jetox
     * @date  2025/10/19 00:08
     */
    @Operation(summary = "更新小雷锋信息")
    @PostMapping("/mini/volunteer/update")
    public CommonResult<String> update(@Valid @RequestBody ClientUserEditParam clientUserEditParam) {
        try {
            // 获取当前登录用户
            String currentUserId = StpClientLoginUserUtil.getClientLoginUser().getId();
            if (currentUserId == null) {
                return CommonResult.error("用户未登录");
            }
            
            // 验证权限：查询小雷锋信息
            ClientUser volunteer = clientUserService.queryEntity(clientUserEditParam.getId());
            if (!currentUserId.equals(volunteer.getParentId())) {
                return CommonResult.error("无权限修改该小雷锋信息");
            }
            
            // 更新小雷锋信息
            clientUserService.edit(clientUserEditParam);
            return CommonResult.ok("更新成功");
        } catch (Exception e) {
            log.error("更新小雷锋失败", e);
            return CommonResult.error("更新失败：" + e.getMessage());
        }
    }

    /**
     * 根据姓名生成账号
     *
     * @author jetox
     * @date  2025/10/19 00:08
     */
    private String generateAccountFromName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new CommonException("姓名不能为空");
        }
        
        // 使用Hutool的PinyinUtil进行拼音转换
        String pinyin = PinyinUtil.getPinyin(name.trim(), "");
        
        // 确保账号唯一性
        String baseAccount = pinyin.toLowerCase();
        String account = baseAccount;
        int suffix = 1;
        
        while (clientUserService.count(new LambdaQueryWrapper<ClientUser>()
                .eq(ClientUser::getAccount, account)) > 0) {
            account = baseAccount + suffix;
            suffix++;
        }
        
        return account;
    }
}