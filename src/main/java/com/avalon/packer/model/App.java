package com.avalon.packer.model;

import com.avalon.packer.dto.app.AddOrUpdateAppDto;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 
 * </p>
 *
 * @author xiaobin.wang
 * @since 2021-12-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("T_App")
@ApiModel(value="App对象", description="")
public class App extends Model<App> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @NotBlank(message = "appId不能为空")
    private String appId;

    private String appName;

    @ApiModelProperty(value = "母包文件夹路径")
    private String sourcePath;

    @ApiModelProperty(value = "portrait是竖屏 landscape是横屏")
    private String screenOrientation;

    private String signFilePath;

    private String signFileKeystorePassword;

    private String signFileKeyPassword;

    private String signFileAlias;

    private LocalDateTime updateTime;

    private LocalDateTime createTime;

    private String motherFtpPaths;

    // versionCode由配置级别提到APP级别
    private int versionCode;
    @ApiModelProperty(value = "mac证书")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String macSignFile;

    @ApiModelProperty(value = "mac平台下，描述文件名")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String descFileName;

    private String macCertPwd;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
