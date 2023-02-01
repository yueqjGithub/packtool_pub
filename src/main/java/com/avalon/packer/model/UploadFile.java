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
@TableName("T_Upload_File")
@ApiModel(value="UploadFile对象", description="")
public class UploadFile extends Model<UploadFile> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String name;

    private String type;

    private String belongId;

    private Long fileSize;

    private String path;

    private String oriName;

    private String remark;

    private LocalDateTime createTime;

    private String userName;

    private String relativePath;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
