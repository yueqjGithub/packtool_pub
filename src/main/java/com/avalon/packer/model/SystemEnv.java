package com.avalon.packer.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
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
@TableName("T_System_Env")
@ApiModel(value="SystemEnv对象", description="")
public class SystemEnv extends Model<SystemEnv> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "env_code", type = IdType.ASSIGN_UUID)
    private String envCode;

    private String envDesc;

    private String supersdkUrl;

    private String avalonsdkUrl;
    private boolean enable;
    private Integer sortNum;


    @Override
    protected Serializable pkVal() {
        return this.envCode;
    }

}
