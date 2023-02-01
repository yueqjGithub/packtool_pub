package com.avalon.packer.dto.mediaFlag;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("新增媒体标识模型")
public class AddOrUpdateMediaFlagDto {

    public String id;
    @NotNull(message = "code不能为空")
    public String code;
    @NotNull(message = "名称不能为空")
    public String mediaName;
    public String description;
}
