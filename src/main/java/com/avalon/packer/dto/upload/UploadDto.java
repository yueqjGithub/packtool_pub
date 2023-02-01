package com.avalon.packer.dto.upload;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadDto {
    private MultipartFile file;
    private String type; // 1-icon 2-splash 3-母包
    private String appId;
    private String ChannelId;
    private String recordId; // 打包记录上signFile文件需要
    private String fileName; // 项目组上传时携带文件名
}
