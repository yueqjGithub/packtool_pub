package com.avalon.packer.service.impl;

import com.avalon.packer.model.App;
import com.avalon.packer.mapper.AppMapper;
import com.avalon.packer.service.AppService;
import com.avalon.packer.utils.FileUtils;
import com.avalon.packer.utils.PathUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xiaobin.wang
 * @since 2021-12-13
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {
    @Value("${file.win.path}")
    private String winPath;

    @Value("${file.unix.path}")
    private String unixPath;

    @Value("${file.motherPath}")
    private String motherPath;

    @Value("${file.resultsPath}")
    private String resultsPath;
    @Value("${file.iconPath}")
    private String iconPath;
    @Value("${file.splashPath}")
    private String splashPath;
    @Override
    public void removeSource (String appId) {
        String rootPath = PathUtil.isWindows() ? winPath : unixPath;
        File file = new File(rootPath + motherPath + appId);
        File resultFile = new File(rootPath + resultsPath + appId);
        File iconFile = new File(rootPath + iconPath + appId);
        File splashFile = new File(rootPath + splashPath + appId);
        boolean r = false;
        try {
            FileUtils.deleteDir(file.getPath());
            FileUtils.deleteDir(resultFile.getPath());
            FileUtils.deleteDir(iconFile.getPath());
            FileUtils.deleteDir(splashFile.getPath());
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }


}
