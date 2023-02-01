package com.avalon.packer.controller;


import com.avalon.packer.dto.upload.SourceUploadDto;
import com.avalon.packer.dto.upload.UploadDto;
import com.avalon.packer.exception.AvalonException;
import com.avalon.packer.http.AvalonError;
import com.avalon.packer.http.AvalonHttpResp;
import com.avalon.packer.model.App;
import com.avalon.packer.model.Channel;
import com.avalon.packer.model.UploadFile;
import com.avalon.packer.service.*;
import com.avalon.packer.utils.FileUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.rmi.AccessException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.avalon.packer.http.AvalonError.PARAM_ERROR;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xiaobin.wang
 * @since 2021-12-13
 */
@Api(tags = "文件上传下载")
@RestController
@RequestMapping("/file")
public class UploadFileController {
    @Resource
    private UploadFileService uploadFileService;

    @Autowired
    AppService appService;

    @Autowired
    private ChannelService channelService;

    @Resource
    private MotherPackagesService motherPackagesService;

    @Value("${file.iconPath}")
    private String iconPath;

    @Value("${file.splashPath}")
    private String splashPath;

    @Value("${file.motherPath}")
    private String motherPath;
    @Value("${file.signPath.app}")
    private String appSignPath;
    @Value("${file.signPath.channel}")
    private String channelSignPath;
    @Value("${sys.device}")
    private String deviceType;

    @Resource
    private PackerRecordService packerRecordService;
    @ApiOperation(
            value = "文件上传"
    )
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AvalonHttpResp<?> upload(UploadDto dto)
            throws Exception {
        List<String> noNeedChannels = Arrays.asList("3", "4", "6", "7");
        MultipartFile file = dto.getFile();
        String type = dto.getType();
        String appId = dto.getAppId();
        LambdaQueryWrapper<App> wp = Wrappers.lambdaQuery();
        wp.eq(!StringUtils.isEmpty(appId),App::getAppId,appId);
        App app = appService.getOne(wp);
        if (app == null) {
            return AvalonHttpResp.failed("无效的应用");
        }
        String channelCode ="";
        if(!noNeedChannels.contains(type)){
            String channelId = dto.getChannelId();
            Channel channel = channelService.getById(channelId);
            if (channel == null) {
                return AvalonHttpResp.failed("无效的渠道");
            }
            channelCode = channel.getChannelCode();
        }

        if (type == null) {
            return AvalonHttpResp.failed("未知的上传动作");
        }
        String appCode = app.getAppId();
        String savePath = "";
        String fileName = dto.getFileName();
        switch (type) {
            case "1":  // ICON
                savePath = iconPath + appCode + "/" + channelCode + "/";
                break;
            case "2": // splash
                savePath = splashPath + appCode + "/" + channelCode + "/" + app.getScreenOrientation() + "/";
                break;
            case "3": // 母包
                savePath = motherPath + appCode + "/";
                motherPackagesService.saveMotherPackageInfo(file.getOriginalFilename(),appId);
                break;
            case "4": // app对应的signFilePath或ios证书
                savePath = String.format(appSignPath,appCode);
                if (Objects.equals(deviceType, "mac")) {
                    savePath += "mac_sign/";
                    app.setMacSignFile(file.getOriginalFilename());
                } else {
                    app.setSignFilePath(file.getOriginalFilename());
                }
                appService.updateById(app);
                break;
            case "5": // 打包配置中 渠道基础配置中的SignFile
                if(StringUtils.isEmpty(dto.getRecordId())){
                    throw new AvalonException(PARAM_ERROR,"配置ID不能为空");
                }
                savePath = String.format(channelSignPath,channelCode);
                if (Objects.equals(deviceType, "mac")) {
                    savePath += "mac_sign/";
                }
                packerRecordService.updateRecordSignFileName(dto.getRecordId(),file.getOriginalFilename());
                break;
            case "6": // 项目组上传母包
                savePath = motherPath + appCode + "/";
//                FileUtils.byteFileToFile(file, savePath + fileName);
                motherPackagesService.saveMotherPackageInfo(fileName,appId);
            case "7": // mac下上传app描述文件
                savePath = String.format(appSignPath,appCode) + "mac_desc/";
                app.setDescFileName(file.getOriginalFilename());
                appService.updateById(app);
                break;
            case "8": // mac下上传channel描述文件
                if(StringUtils.isEmpty(dto.getRecordId())){
                    throw new AvalonException(PARAM_ERROR,"配置ID不能为空");
                }
                savePath = String.format(channelSignPath, channelCode) + "mac_desc/";
                packerRecordService.updateConfigDescFile(dto.getRecordId(), file.getOriginalFilename());
                break;
            case "9": // 上传其他文件
                if(StringUtils.isEmpty(dto.getRecordId())){
                    throw new AvalonException(PARAM_ERROR,"配置ID不能为空");
                }
                savePath = String.format(channelSignPath, channelCode);
                if (Objects.equals(deviceType, "mac")) {
                    savePath += "mac_other_file/";
                } else {
                    savePath += "other_file/";
                }
                packerRecordService.setConfigOtherFile(dto.getRecordId(), file.getOriginalFilename());
                break;
            default:
                return AvalonHttpResp.failed("未知的上传类型");
        }
        String fileUpload = "";
        fileUpload = uploadFileService.createFileUpload(file, savePath, type, fileName);
        return AvalonHttpResp.ok(fileUpload);
    }

    @PostMapping(value = "/uploadSource", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AvalonHttpResp<?> uploadSource (SourceUploadDto dto) throws AvalonException,IOException {
        App app = appService.getById(dto.getAppId());
        if (null == app) {
            throw new AvalonException(PARAM_ERROR, "无效的appId");
        }
        return AvalonHttpResp.ok(uploadFileService.UploadSourcePiece(dto, app.getAppId()));
    }
}
