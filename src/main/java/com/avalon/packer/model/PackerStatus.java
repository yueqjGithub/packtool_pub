package com.avalon.packer.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
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
 * @since 2021-12-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("T_Packer_Status")
@ApiModel(value="PackerStatus对象", description="")
public class PackerStatus extends Model<PackerStatus> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String recordId;

    @ApiModelProperty(value = "0.未打包，1.打包中，2.打包成功，3.打包失败")
    private Integer status;

    @ApiModelProperty(value = "失败原因")
    private String failReason;

    @ApiModelProperty(value = "打包结果")
    private String result;

    @ApiModelProperty(value = "打包版本")
    private int version;

    private String appId;

    private String reason;

    private int versionCode;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
