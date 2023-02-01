package com.avalon.packer.controller;


import com.avalon.packer.dto.mediaFlag.AddOrUpdateMediaFlagDto;
import com.avalon.packer.dto.mediaFlag.FindMediaFlagDto;
import com.avalon.packer.http.AvalonHttpResp;
import com.avalon.packer.model.MediaFlag;
import com.avalon.packer.model.RecordMedia;
import com.avalon.packer.service.MediaFlagService;
import com.avalon.packer.service.RecordMediaService;
import com.avalon.packer.utils.ZzBeanCopierUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
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
@RequestMapping("/admin/mediaFlag")
@Api(tags = "媒体标识")
public class MediaFlagController {
    @Autowired
    MediaFlagService mediaFlagService;

    @Resource
    RecordMediaService recordMediaService;

    @GetMapping("")
    @ApiOperation(value = "获取媒体表示列表")
    public AvalonHttpResp<?> getList (FindMediaFlagDto dto) {
        QueryWrapper<MediaFlag> wrapper = new QueryWrapper<>();
        String code = dto.getCode();
        String mediaName = dto.getMediaName();
        if (code != null) {
            wrapper = wrapper.like("code", code);
        }
        if (mediaName != null) {
            wrapper = wrapper.like("media_name", mediaName);
        }
        List<MediaFlag> list = mediaFlagService.list(wrapper);
        return AvalonHttpResp.ok(list);
    }

    @PostMapping("")
    @ApiOperation(value = "添加、修改媒体表示，unikey=id")
    public AvalonHttpResp<?> addOrUpdate (@Validated @RequestBody AddOrUpdateMediaFlagDto dto) {
        String id = dto.getId();
        String code = dto.getCode();
        QueryWrapper<MediaFlag> wrapper = new QueryWrapper<>();
        wrapper = wrapper.eq("code", code);
        MediaFlag check = mediaFlagService.getOne(wrapper);
        wrapper.clear();
        wrapper = wrapper.eq("media_name", dto.getMediaName());
        MediaFlag checkNameDuplicate = mediaFlagService.getOne(wrapper);
        if (StringUtils.isEmpty(id)) {
            if (check != null) {
                return AvalonHttpResp.failed("ID重复");
            }
            if (checkNameDuplicate != null) {
                return AvalonHttpResp.failed("名称被占用");
            }
            MediaFlag mediaFlag = ZzBeanCopierUtils.copy(dto, new MediaFlag());
            mediaFlagService.save(mediaFlag);
            return AvalonHttpResp.ok();
        }
        MediaFlag mediaFlag = mediaFlagService.getById(id);
        if (mediaFlag == null) {
            return AvalonHttpResp.failed("未找到对应媒体标识");
        }
        if (checkNameDuplicate != null && !Objects.equals(check.getId(), id)) {
            return AvalonHttpResp.failed("code已被占用");
        }
        MediaFlag copy = ZzBeanCopierUtils.copy(dto, mediaFlag);
        mediaFlagService.updateById(copy);
        return AvalonHttpResp.ok();
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除媒体标识")
    public AvalonHttpResp<?> deleteFlag (@PathVariable String id) {
        if (id == null) {
            return AvalonHttpResp.failed("id不能为空");
        }
        MediaFlag mediaFlag = mediaFlagService.getById(id);
        if (mediaFlag == null) {
            return AvalonHttpResp.failed("没有找到对应插件");
        }
        mediaFlagService.removeById(id);
        QueryWrapper<RecordMedia> delWp = new QueryWrapper<>();
        delWp.eq("media_id", id);
        recordMediaService.remove(delWp);
        return AvalonHttpResp.ok();
    }
}
