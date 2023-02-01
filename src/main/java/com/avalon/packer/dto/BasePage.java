package com.avalon.packer.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ClassName: BasePage
 * @Author: wxb
 * @Description:
 * @Date: 2021/2/8 17:05
 */
@Data
public class BasePage extends BaseDto{
    @ApiModelProperty(value = "第几页，从第一页开始", required = true, example = "1")
    @NotNull(message = "页数不能为空")
    private int page = 1;

    @ApiModelProperty(value = "每页多少条数据", required = true, example = "10")
    @NotNull(message = "每页条数不能为空")
    private int pageSize = 10;
}
