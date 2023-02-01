package com.avalon.packer.controller;


import com.avalon.packer.dto.historyrecord.FindHistoryRecordDto;
import com.avalon.packer.dto.historyrecord.GetDetailsDto;
import com.avalon.packer.dto.historyrecord.GetStatusDto;
import com.avalon.packer.dto.historyrecord.UploadStoreDto;
import com.avalon.packer.http.AvalonHttpResp;
import com.avalon.packer.model.ChannelMediaPackage;
import com.avalon.packer.model.HistoryRecord;
import com.avalon.packer.service.ChannelMediaPackageService;
import com.avalon.packer.service.HistoryRecordService;
import com.avalon.packer.vo.HistoryRecordsVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xiaobin.wang
 * @since 2022-01-06
 */
@RestController
@RequestMapping("/admin/history-record")
@Api(tags = "打包历史记录")
public class HistoryRecordController {
    @Value("${sys.device}")
    private String deviceType;

    @Resource
    private HistoryRecordService historyRecordService;

    @Resource
    private ChannelMediaPackageService channelMediaPackageService;
    @PostMapping("/doPage")
    @ApiOperation(
            value = "查询打包历史记录"
    )
    public AvalonHttpResp<Page<HistoryRecord>> doPageHistoryRecord(@Validated @RequestBody FindHistoryRecordDto dto){
        return AvalonHttpResp.ok(historyRecordService.doPageHistoryRecords(dto));
    }

    @GetMapping("/detail/{id}")
    @ApiOperation(
            value = "获取记录详情"
    )
    public AvalonHttpResp<HistoryRecordsVo> doHistoryRecordDetail(@PathVariable String id){
        return AvalonHttpResp.ok(historyRecordService.getHistoryRecordDetail(id));
    }

    @PostMapping("/detailList")
    @ApiOperation("根据历史ID集合查询多条历史记录")
    public AvalonHttpResp<List<HistoryRecordsVo>> getDetailsByIds (@RequestBody @Validated GetDetailsDto dto) {
        return AvalonHttpResp.ok(historyRecordService.getDetailList(dto.getIds()));
    }

    @PostMapping("/upload")
    @ApiOperation("上传ipa至appstore")
    public AvalonHttpResp<?> uploadIpaToStore (@RequestBody @Validated UploadStoreDto dto) throws Exception {
        if (!Objects.equals(deviceType, "mac")) {
            return AvalonHttpResp.failed("当前系统不支持上传至APPSTORE");
        }
        String id = dto.getId();
        ChannelMediaPackage target = channelMediaPackageService.getById(id);
        if (null == target) {
            return AvalonHttpResp.failed("未找到对应包记录");
        }
        if (target.getPublicType() != 1) {
            return AvalonHttpResp.failed("当前包发布方式不支持上传");
        }
        historyRecordService.uploadIpaToStore(
                id,
                dto.getAccount(),
                dto.getPwd()
        );
        return AvalonHttpResp.ok();
    }
}
