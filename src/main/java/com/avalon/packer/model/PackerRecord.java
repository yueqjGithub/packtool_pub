package com.avalon.packer.model;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author xiaobin.wang
 * @version 2.2.1 本次修改将此实体更新为 渠道的打包配置
 * @since 2021-12-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("T_Packer_Record")
@ApiModel(value="PackerRecord对象", description="")
public class PackerRecord extends Model<PackerRecord> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String configName;

    private String appId;

    private String channelId;

//    private String sourceName;

    @ApiModelProperty(value = "渠道参数配置")
    private String baseConfig;

    @ApiModelProperty(value = "插件参数配置")
    private String pluginsConfig;

    @ApiModelProperty(value = "iCON地址")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String iconUrl;

    @ApiModelProperty(value = "闪屏文件地址")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String splashUrl;

    private String gameName;

    private String resultType;

//    private int versionCode;

    private Integer buildNum;

    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    private String publicArea;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String signFilePath;

    private String signFileKeystorePassword;

    private String signFileKeyPassword;

    private String signFileAlias;

    @TableField(fill = FieldFill.INSERT)
    private String updateTime;

    @ApiModelProperty(value = "渠道包名")
    private String packerName;

    @ApiModelProperty(value = "配置是否完成")
    private boolean couldPack;

//    private boolean couldDownload;

    @ApiModelProperty(value = "渠道版本")
    private String channelVersion;

    private String envCode;

//    private Integer motherIsFtp;

    private String ftpPath;

    @ApiModelProperty(value = "最近一次修改人")
    private String lastUpdateAs;

    @ApiModelProperty(value = "最后一次打包人")
    private String lastOps;

    @ApiModelProperty(value = "最后一次打包时间")
    private String lastPackTime;

    @ApiModelProperty(value = "最近一次打包历史ID")
    private String lastHisId;
    @ApiModelProperty(value = "归属于mac平台，创建时跟随渠道自动设定")
    private Boolean isMac;
    @ApiModelProperty(value = "mac证书")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String macSignFile;
    @ApiModelProperty(value = "mac平台下，描述文件名")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String descFileName;
    @ApiModelProperty(value = "其他文件上传")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String otherFile;
    @ApiModelProperty(value = "mac其他文件")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String macOtherFile;
    @ApiModelProperty(value = "发布方式")
    private Integer publicType;

    private String macCertPwd;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
