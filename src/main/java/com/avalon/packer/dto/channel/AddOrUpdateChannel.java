package com.avalon.packer.dto.channel;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "新增渠道模型")
public class AddOrUpdateChannel {
    private String id;
    @NotNull(message = "渠道编码不能为空")
    private String channelCode;
    @NotNull(message = "渠道名称不能为空")
    private String channelName;
    @NotNull(message = "渠道ID不能为空")
    private int channelId;
    private String serverConfigDoc;
    private String clientConfigDoc;
    private String description;
    private String versionNum;
    @NotNull(message = "请设置渠道所属平台")
    private Boolean isMac;
    private String extra;
}
