package com.avalon.packer.dto.packerStatus;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class GetResultDto {
    private String buildVersion;
    @NotBlank(message = "recordId不能为空")
    private String recordId;
}
