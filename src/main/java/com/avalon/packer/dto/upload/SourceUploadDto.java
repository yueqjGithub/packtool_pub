package com.avalon.packer.dto.upload;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class SourceUploadDto {
    @NotNull(message = "应用ID不能为空")
    private String appId;

    @NotNull(message = "总分片数不能为空")
    private String length;

    private MultipartFile file;
    @NotNull(message = "序号不能为空")
    private String idx;
    @NotNull(message = "md5不能为空")
    private String md5;
    @NotNull(message = "传输类型不能为空")
    private String type;

    private String fileName;
}
