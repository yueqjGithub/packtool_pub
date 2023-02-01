package com.avalon.packer.dto.app;

import com.avalon.packer.model.App;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("新增App模型")
public class AddOrUpdateAppDto {
    private String id;
    @NotBlank(message = "appid不能为空")
    private String appId;
    @NotBlank(message = "appName不能为空")
    private String appName;
    private String sourcePath;
    @NotBlank
    @ApiModelProperty(value = "屏幕方向，portrait是竖屏 landscape是横屏", required = true)
    private String screenOrientation;

    private String signFilePath;
    private String signFileKeystorePassword;
    private String signFileKeyPassword;
    private String signFileAlias;
    private String IconPath;
    private String motherFtpPaths;
    private Integer versionCode;

    private String macSignFile;
    private String descFileName;
    private String macCertPwd;
}
