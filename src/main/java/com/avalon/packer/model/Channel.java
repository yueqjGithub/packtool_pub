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
 * @since 2021-12-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("T_Channel")
@ApiModel(value="Channel对象", description="")
public class Channel extends Model<Channel> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private int channelId;

    private String channelCode;

    private String channelName;

    private String serverConfigDoc;

    private String clientConfigDoc;

    private String extra;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String description;

    private Boolean isMac;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
