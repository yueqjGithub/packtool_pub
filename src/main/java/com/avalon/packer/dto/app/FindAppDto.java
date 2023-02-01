package com.avalon.packer.dto.app;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("查询APP模型")
public class FindAppDto {
    private String appId;
    private String appName;
    private String appLimit;
}
