package com.avalon.packer.dto.historyrecord;

import com.avalon.packer.dto.BasePage;
import com.avalon.packer.dto.RangeDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@ApiModel(value = "按照条件查询历史记录")
@Data
public class FindHistoryRecordDto extends BasePage {
    @ApiModelProperty(value = "查询的时间区间,默认但当前时间往后推7天",required = true,example = "{'start':'2021-12-24 00:00:00','end':'2021-12-31 00:00:00'}")
    private RangeDto<LocalDateTime> range;
    @NotNull(message = "appId不能为空")
    private String appId;

    private Integer versionCode;
}
