package com.avalon.packer.controller;


import com.avalon.packer.dto.historyrecord.GetStatusDto;
import com.avalon.packer.dto.packerStatus.FindStatusDto;
import com.avalon.packer.dto.packerStatus.GetResultDto;
import com.avalon.packer.dto.packerStatus.StatusResultDto;
import com.avalon.packer.http.AvalonHttpResp;
import com.avalon.packer.mapper.PackerStatusMapper;
import com.avalon.packer.model.HistoryRecord;
import com.avalon.packer.model.PackerStatus;
import com.avalon.packer.service.HistoryRecordService;
import com.avalon.packer.service.PackerStatusService;
import com.avalon.packer.utils.PathUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.python.bouncycastle.crypto.util.Pack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xiaobin.wang
 * @since 2021-12-27
 */
@RestController
@RequestMapping("/admin/packerStatus")
@Api(tags = "打包状态")
public class PackerStatusController {
    @Autowired
    PackerStatusService packerStatusService;

    @Resource
    HistoryRecordService historyRecordService;

    @Value("${file.win.path}")
    private String winPath;

    @Value("${file.unix.path}")
    private String unixPath;

    @Value("${file.downloadUrl}")
    private String downloadUrl;

    @Resource
    PackerStatusMapper packerStatusMapper;

    @PostMapping("")
    @ApiOperation("查询打包状态")
    public AvalonHttpResp<?> getPackStatus (@RequestBody @Validated GetStatusDto dto) {
        List<HistoryRecord> hisList = historyRecordService.listByIds(Arrays.stream(dto.getHisIds()).collect(Collectors.toList()));
        return AvalonHttpResp.ok(hisList);
    }

    @PostMapping("/download")
    @ApiOperation(value = "获取打包结果")
    public AvalonHttpResp<?> getResults (@Validated @RequestBody GetResultDto dto) {
        // rootPath
        String rootPath = PathUtil.isWindows() ? winPath : unixPath;

        String versionNum = dto.getBuildVersion();
        String recordId = dto.getRecordId();
        QueryWrapper<PackerStatus> wrapper = new QueryWrapper<>();
        wrapper = wrapper.eq("record_id", recordId);
        PackerStatus packerStatus = packerStatusService.getOne(wrapper);

        String resourcePath = packerStatus.getResult();

        if (!StringUtils.isEmpty(versionNum)) {
            List<String> list = Arrays.stream(resourcePath.split("/")).collect(Collectors.toList());
            int len = list.size();
            list.remove(len - 1);
            list.add(versionNum);
            StringBuilder newPath = new StringBuilder();
            for (String item :list) {
                newPath.append(item).append("/");
            }
            resourcePath = newPath.toString();
        }

        File file = new File(resourcePath);
        if (!file.exists()) {
            return AvalonHttpResp.failed("无法获取打包结果，文件不存在");
        }
        File[] tempList = file.listFiles();
        List<String> nameList = new ArrayList<>(Collections.emptyList());
        if (tempList != null) {
            String onlinePath = resourcePath.replaceAll(rootPath, downloadUrl);
            for (File fileItem : tempList) {
                String fullPath = onlinePath + fileItem.getName();
                nameList.add(fullPath);
            }
        }

        return AvalonHttpResp.ok(nameList);
    }

    @GetMapping("/failReason/{recordeId}")
    @ApiOperation("/获取打包失败原因")
    public AvalonHttpResp<?> queryFailReason (@PathVariable String recordeId) {
        if (StringUtils.isEmpty(recordeId)) {
            return AvalonHttpResp.failed("打包记录ID不能为空");
        }
        QueryWrapper<PackerStatus> wrapper = new QueryWrapper<>();
        wrapper = wrapper.eq("record_id", recordeId);
        PackerStatus packerStatus = packerStatusService.getOne(wrapper);
        if (packerStatus == null) {
            return AvalonHttpResp.failed("未找到对应打包记录");
        }
        return AvalonHttpResp.ok(packerStatus.getReason());
    }
}
