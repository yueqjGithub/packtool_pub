package com.avalon.packer.dto.packerRecord;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SetMediaDto {
    @NotNull(message = "打包记录不能为空")
    private String recordId;
    private String mediaIds;
}
