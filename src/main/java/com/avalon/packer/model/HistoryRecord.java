package com.avalon.packer.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2022-01-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("T_History_Record")
@ApiModel(value="HistoryRecord对象", description="")
public class HistoryRecord extends Model<HistoryRecord> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
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

    private String pluginList;

    private String packageName;

    private String envCode;

    private int buildNum;

    private String opsUser;
    private String envDesc;
    @ApiModelProperty("打包状态,1-打包中,2-打包成功,3-打包失败")
    private int packStatus;
    @ApiModelProperty("失败原因")
    private String reason;
    @ApiModelProperty("母包是否为ftp")
    private Integer motherIsFtp;
    // 3.5.0
    private Integer versionCode;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
