package com.avalon.packer.controller;


import com.avalon.packer.dto.plugins.AddOrUpdatePluginsDto;
import com.avalon.packer.dto.plugins.FindPluginsDto;
import com.avalon.packer.dto.plugins.PluginsType;
import com.avalon.packer.http.AvalonHttpResp;
import com.avalon.packer.model.Channel;
import com.avalon.packer.model.Plugins;
import com.avalon.packer.model.RecordPlugins;
import com.avalon.packer.service.PluginsService;
import com.avalon.packer.service.RecordPluginsService;
import com.avalon.packer.utils.ZzBeanCopierUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.bytebuddy.build.Plugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xiaobin.wang
 * @since 2021-12-13
 */
@RestController
@RequestMapping("/admin/plugins")
@Api(tags = "插件")
public class PluginsController {
    @Autowired
    PluginsService pluginsService;
    @Resource
    private RecordPluginsService recordPluginsService;
    @GetMapping("")
    @ApiOperation(value = "获取插件list")
    public AvalonHttpResp<?> getList (FindPluginsDto dto) {
        QueryWrapper<Plugins> wrapper = new QueryWrapper<>();
        String name = dto.getName();
        String type = dto.getType();
        if (name != null) {
            wrapper = wrapper.like("name", dto.getName());
        }
        if (type != null) {
            wrapper = wrapper.eq("type", type);
        }
        return AvalonHttpResp.ok(pluginsService.list(wrapper));
    }

    @GetMapping("/types")
    @ApiOperation(value = "获取插件类型")
    public AvalonHttpResp<?> getTypes () {
        return AvalonHttpResp.ok(PluginsType.GetPluginsTypes());
    }

    @PostMapping("")
    @ApiOperation(value = "添加、更新插件, unikey=id")
    public AvalonHttpResp<?> addOrUpdate (@Validated @RequestBody AddOrUpdatePluginsDto dto) throws IOException {
        String type = dto.getType();
        String id = dto.getId();
        String code = dto.getCode();
        QueryWrapper<Plugins> wrapper = new QueryWrapper<>();
        wrapper = wrapper.eq("code", code);
        Plugins check = pluginsService.getOne(wrapper);
        if (StringUtils.isEmpty(id)) {
            if (check != null) {
                return AvalonHttpResp.failed("code重复");
            }
            Plugins plugins = ZzBeanCopierUtils.copy(dto, new Plugins());
            if (type == null) {
                plugins.setType("7"); // 未设置类型，归到“其他(7)”
            }
//            boolean getSource = pluginsService.getPluginSourceFromSvn(plugins.getCode());
            pluginsService.save(plugins);
            return AvalonHttpResp.ok();
        }
        Plugins plugins = pluginsService.getById(id);
        if (plugins == null) {
            return AvalonHttpResp.failed("未找到对应插件");
        }
        if (check != null && !Objects.equals(check.getId(), id)) {
            return AvalonHttpResp.failed("code已被占用");
        }
        Plugins copy = ZzBeanCopierUtils.copy(dto, plugins);
        if (type == null) {
            copy.setType("7"); // 未设置类型，归到“其他(7)”
        }
        pluginsService.updateById(copy);
//        boolean getSource = pluginsService.getPluginSourceFromSvn(plugins.getCode());
//        if (getSource) {
//            pluginsService.updateById(copy);
//        } else {
//            return AvalonHttpResp.failed("无法获取插件资源");
//        }
        return AvalonHttpResp.ok();
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除插件")
    public AvalonHttpResp<?> deletePlugins (@PathVariable String id) {
        if (id == null) {
            return AvalonHttpResp.failed("id不能为空");
        }
        Plugins channel = pluginsService.getById(id);
        if (channel == null) {
            return AvalonHttpResp.failed("没有找到对应插件");
        }
        // 删除记录与插件绑定关系
        LambdaQueryWrapper<RecordPlugins> wrapper = new LambdaQueryWrapper<>();
        wrapper = wrapper.eq(RecordPlugins::getPluginsId, id);
        recordPluginsService.remove(wrapper);
        pluginsService.removeById(id);
        return AvalonHttpResp.ok();
    }
}
