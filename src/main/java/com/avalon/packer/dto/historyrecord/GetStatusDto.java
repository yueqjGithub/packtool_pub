package com.avalon.packer.dto.historyrecord;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class GetStatusDto {
    @NotEmpty(message = "无效的历史记录")
    private String[] hisIds;
}
