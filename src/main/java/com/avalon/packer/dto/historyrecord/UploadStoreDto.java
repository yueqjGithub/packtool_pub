package com.avalon.packer.dto.historyrecord;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UploadStoreDto {

    @NotNull(message = "账号不能为空")
    private String account;

    @NotNull(message = "密码不能为空")
    private String pwd;

    @NotNull(message = "id不能为空")
    private String id;
}
