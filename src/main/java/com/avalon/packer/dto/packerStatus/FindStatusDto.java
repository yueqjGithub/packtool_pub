package com.avalon.packer.dto.packerStatus;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class FindStatusDto {
    @NotBlank(message = "appId不能为空")
    private String appId;
}
