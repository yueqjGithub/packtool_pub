package com.avalon.packer.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel(value = "区间参数模型")
@Data
public class RangeDto<T> {
    private T start;
    private T end;
}
