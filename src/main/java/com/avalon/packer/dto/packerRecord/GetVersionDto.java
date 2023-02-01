package com.avalon.packer.dto.packerRecord;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GetVersionDto {
    @NotNull(message = "请传递类型")
    private String type;
    @NotNull(message = "id不能为空")
    private String id;
}
