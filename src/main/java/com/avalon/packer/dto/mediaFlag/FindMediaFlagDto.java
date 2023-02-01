package com.avalon.packer.dto.mediaFlag;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("查询媒体标识模型")
public class FindMediaFlagDto {
    private String code;
    private String mediaName;
}
