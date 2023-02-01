package com.avalon.packer.controller;


import com.avalon.packer.http.AvalonHttpResp;
import com.avalon.packer.model.SystemEnv;
import com.avalon.packer.service.SystemEnvService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xiaobin.wang
 * @since 2022-01-06
 */
@RestController
@RequestMapping("/admin/systemEnv")
@Api(tags = "SDK系统环境")
public class SystemEnvController {
    @Resource
    private SystemEnvService systemEnvService;

    @ApiOperation(
            value = "获取SDK系统的环境参数"
    )
    @GetMapping("")
    public AvalonHttpResp<List<SystemEnv>> doEnvList(@RequestParam(required = false) Integer isEnable){
        // isEnable 0表示false 1表示true  null表示全部
        LambdaQueryWrapper<SystemEnv> wp = Wrappers.lambdaQuery();
        if(isEnable != null && 1 == isEnable){
            wp.eq(SystemEnv::isEnable,true);
        }
        if(isEnable != null && 0 == isEnable){
            wp.eq(SystemEnv::isEnable,false);
        }
        wp.orderByAsc(SystemEnv::getSortNum);
        return AvalonHttpResp.ok(systemEnvService.list(wp));
    }

    @ApiOperation(
            value = "新增环境配置"
    )
    @PostMapping("")
    public AvalonHttpResp<Boolean> saveEnv(@RequestBody SystemEnv dto){
        SystemEnv byId = systemEnvService.getById(dto.getEnvCode());
        if(null != byId){
            return AvalonHttpResp.failed(String.format("EnvCode为%s的环境配置已经存在",dto.getEnvCode()));
        }
        boolean insert = dto.insert();
        if(!insert){
            return AvalonHttpResp.failed("新增失败");
        }
        return AvalonHttpResp.ok(true);
    }

    @ApiOperation(
            value = "修改环境配置"
    )
    @PutMapping("")
    public AvalonHttpResp<Boolean> updateEnv(@RequestBody SystemEnv dto){
        SystemEnv byId = systemEnvService.getById(dto.getEnvCode());
        if(null == byId){
            return AvalonHttpResp.failed(String.format("EnvCode为%s的环境配置不存在",dto.getEnvCode()));
        }
        boolean update = dto.updateById();
        if(!update){
            return AvalonHttpResp.failed("修改失败");
        }
        return AvalonHttpResp.ok(true);
    }
    @ApiOperation(
            value = "删除环境配置"
    )
    @DeleteMapping("/{envCode}")
    public AvalonHttpResp<Boolean> deleteEnv(@PathVariable String envCode){
        SystemEnv byId = systemEnvService.getById(envCode);
        if(null == byId){
            return AvalonHttpResp.failed(String.format("EnvCode为%s的环境配置不存在",envCode));
        }
        boolean del = systemEnvService.removeById(envCode);
        if(!del){
            return AvalonHttpResp.failed("删除失败");
        }
        return AvalonHttpResp.ok(true);
    }






}
