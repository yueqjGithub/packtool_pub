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
 * @since 2021-12-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("T_Record_Plugins")
@ApiModel(value="RecordPlugins对象", description="")
public class RecordPlugins extends Model<RecordPlugins> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String recordId;

    private String pluginsId;

    private String pluginsConfig;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String pluginsVersion;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
