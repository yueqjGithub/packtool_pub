package com.avalon.packer.service;

import com.avalon.packer.dto.upload.SourceUploadDto;
import com.avalon.packer.dto.upload.UploadResDto;
import com.avalon.packer.exception.AvalonException;
import com.avalon.packer.model.UploadFile;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xiaobin.wang
 * @since 2021-12-13
 */
public interface UploadFileService extends IService<UploadFile> {
    String createFileUpload(MultipartFile file, String savePath, String type, String fileName) throws IOException;

    /**
     * 分片上传母包
     */
    UploadResDto UploadSourcePiece (
            SourceUploadDto dto,
            String appCode
    ) throws AvalonException, IOException;
}