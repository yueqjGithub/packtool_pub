package com.avalon.packer.dto.historyrecord;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class GetDetailsDto {
    @NotEmpty(message = "无效的历史记录ID")
    String[] ids;
}
