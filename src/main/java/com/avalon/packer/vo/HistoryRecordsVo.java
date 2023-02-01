package com.avalon.packer.vo;

import com.avalon.packer.model.ChannelMediaPackage;
import com.avalon.packer.model.HistoryRecord;
import com.avalon.packer.model.MediaFlag;
import com.avalon.packer.model.Plugins;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class HistoryRecordsVo extends HistoryRecord {
    private String id;

    private String configId;

    private String appId;

    @ApiModelProperty(value = "App的简称")
    private String app;

    private String channelId;
    private String channelCode;

    private String channelName;

    private String channelVersion;

    private String supersdkVersion;

    private String motherShortName;

    private String motherName;

    private LocalDateTime createTime;

    private List<Plugins> pluginsList;

    private List<ChannelMediaPackage> mediaFinishedPackagesList;

    private String downloadHost;
}
