package com.avalon.packer.controller;


import com.avalon.packer.dto.channel.AddOrUpdateChannel;
import com.avalon.packer.dto.channel.FindChannelDto;
import com.avalon.packer.http.AvalonHttpResp;
import com.avalon.packer.model.App;
import com.avalon.packer.model.Channel;
import com.avalon.packer.service.ChannelService;
import com.avalon.packer.utils.FileUtils;
import com.avalon.packer.utils.PathUtil;
import com.avalon.packer.utils.ZzBeanCopierUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
@RequestMapping("/admin/channel")
@Api(tags = "渠道")
public class ChannelController {
    @Autowired
    ChannelService channelService;
    @Value("${file.win.path}")
    private String winPath;

    @Value("${file.unix.path}")
    private String unixPath;

    @Value("${sys.device}")
    private String deviceType;

    @Value("${file.signPath.channel}")
    private String signPath;
    @GetMapping("")
    @ApiOperation(value = "获取渠道list")
    public AvalonHttpResp<?> getList (FindChannelDto dto) {
        QueryWrapper<Channel> wrapper = new QueryWrapper<>();
        String channelCode = dto.getChannelCode();
        if (channelCode != null) {
            wrapper = wrapper.like("channel_code", channelCode);
        }
        String channelName = dto.getChannelName();
        if (channelName != null) {
            wrapper = wrapper.like("channel_name", channelName);
        }
        if (dto.getIsMac() != null) {
            wrapper.eq("is_mac", dto.getIsMac());
        }
        List<Channel> list = channelService.list(wrapper);
        return AvalonHttpResp.ok(list);
    }

    @PostMapping("")
    @ApiOperation(value = "添加、更新渠道, unikey=id")
    public AvalonHttpResp<?> addOrUpdate (@Validated @RequestBody AddOrUpdateChannel dto) {
        String id = dto.getId();
        String channel_code = dto.getChannelCode();
        QueryWrapper<Channel> wrapper = new QueryWrapper<>();
        wrapper = wrapper.eq("channel_code", channel_code);
        Channel check = channelService.getOne(wrapper);
        if (StringUtils.isEmpty(id)) {
            if (check != null) {
                return AvalonHttpResp.failed("渠道code重复");
            }
            Channel channel = ZzBeanCopierUtils.copy(dto, new Channel());
            channelService.save(channel);
            return AvalonHttpResp.ok();
        }
        Channel channel = channelService.getById(id);
        if (channel == null) {
            return AvalonHttpResp.failed("未找到对应渠道");
        }
        if (check != null && !Objects.equals(check.getId(), id)) {
            return AvalonHttpResp.failed("code已被占用");
        }
        Channel copy = ZzBeanCopierUtils.copy(dto, channel);
        channelService.updateById(copy);
        return AvalonHttpResp.ok();
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除渠道")
    public AvalonHttpResp<?> deleteChannel (@PathVariable String id) throws IOException {
        return AvalonHttpResp.ok(channelService.clearChannelById(id));
    }

    @GetMapping("/signFile/{channelId}")
    @ApiOperation(value = "读取已有签名文件List")
    public AvalonHttpResp<Map<String, List<String>>> readSignFile (@PathVariable String channelId) {
        if (channelId == null) {
            return AvalonHttpResp.failed("channelId不能为空");
        }
        Channel channel = channelService.getById(channelId);
        if (channel == null) {
            return AvalonHttpResp.failed("没有找到对应渠道");
        }

        Map<String, List<String>> resultMap = new HashMap<>();

        String channelCode = channel.getChannelCode();
        String rootPath = PathUtil.isWindows() ? winPath : unixPath;
        String path = rootPath + String.format(signPath, channelCode);

        if (Objects.equals(deviceType, "mac")) {
            List<String> descList = FileUtils.readDir(path + "mac_desc/");
            resultMap.put("descList", descList);
            path += "mac_sign/";
        }
        List<String> signList = FileUtils.readDir(path);
        resultMap.put("signList", signList);

        return AvalonHttpResp.ok(resultMap);
    }
}
