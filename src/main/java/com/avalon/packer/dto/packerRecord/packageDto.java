package com.avalon.packer.dto.packerRecord;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class packageDto {
    @NotBlank(message = "应用ID为空")
    private String id;
    @NotBlank(message = "未设置母包")
    private String motherPackage;

    @NotNull(message = "无法确定来源的母包")
    private Integer motherIsFtp;

    private String ftpPath;

    private String ops;
    @NotEmpty(message = "未选择配置")
    private String[] configs;
}
