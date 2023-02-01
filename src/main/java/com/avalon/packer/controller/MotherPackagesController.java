package com.avalon.packer.controller;


import com.avalon.packer.dto.historyrecord.FindHistoryRecordDto;
import com.avalon.packer.dto.motherPackages.FindMotherPackagesDto;
import com.avalon.packer.http.AvalonHttpResp;
import com.avalon.packer.model.HistoryRecord;
import com.avalon.packer.model.MotherPackages;
import com.avalon.packer.service.MotherPackagesService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xiaobin.wang
 * @since 2022-01-11
 */
@RestController
@RequestMapping("/admin/mother-packages")
@Api(tags = "获取母包")
public class MotherPackagesController {
    @Resource
    private MotherPackagesService motherPackagesService;
    @GetMapping("/find")
    @ApiOperation(
            value = "查询母包"
    )
    public AvalonHttpResp<List<MotherPackages>> doPageHistoryRecord(FindMotherPackagesDto dto){
        LambdaQueryWrapper<MotherPackages> wrapper = Wrappers.lambdaQuery();
        if(null == dto.getLimitCount()){
            dto.setLimitCount(10);
        }
        List<MotherPackages> motherPackagesList = motherPackagesService.list(
                wrapper
                        .like(StringUtils.isNotEmpty(dto.getMotherPackageName()), MotherPackages::getPackageName, dto.getMotherPackageName())
                        .orderByDesc(MotherPackages::getCreateTime)
                        .last("limit " + dto.getLimitCount())
        );
        return AvalonHttpResp.ok(motherPackagesList);
    }
}
