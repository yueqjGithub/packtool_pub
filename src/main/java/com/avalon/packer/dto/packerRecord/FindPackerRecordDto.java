package com.avalon.packer.dto.packerRecord;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("查询打包记录列表模型")
public class FindPackerRecordDto {
    @NotNull(message = "appId不能为空")
    private String appId;
}
