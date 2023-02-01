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
 * @since 2022-01-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("T_Channel_Media_Package")
@ApiModel(value="ChannelMediaPackage对象", description="")
public class ChannelMediaPackage extends Model<ChannelMediaPackage> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String hrId;

    private String packageName;

    private String mediaName;

    private String downUrl;

    private String md5Val;

    private Integer publicType;

    private String failReason;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
