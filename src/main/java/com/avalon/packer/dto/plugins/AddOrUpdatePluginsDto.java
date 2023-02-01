package com.avalon.packer.dto.plugins;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AddOrUpdatePluginsDto {
    private String id;
    @NotNull(message = "插件名称不能为空")
    private String name;
    @NotNull(message = "插件code不能为空")
    private String code;
    private String description;
    private String type;
    private String serverConfigDoc;
    private String clientConfigDoc;
    private String extra;
}
