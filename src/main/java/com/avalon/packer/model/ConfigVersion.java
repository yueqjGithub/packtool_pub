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
 * @author quanjiang.yue
 * @since 2022-07-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("T_Config_Version")
@ApiModel(value="ConfigVersion对象", description="")
public class ConfigVersion extends Model<ConfigVersion> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @ApiModelProperty(value = "配置id")
    private String configId;

    @ApiModelProperty(value = "版本")
    private Integer version;

    @ApiModelProperty(value = "配置转md5存放")
    private String config;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
