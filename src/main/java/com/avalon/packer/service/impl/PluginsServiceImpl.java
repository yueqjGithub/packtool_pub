package com.avalon.packer.service.impl;

import com.avalon.packer.exception.AvalonException;
import com.avalon.packer.model.Plugins;
import com.avalon.packer.mapper.PluginsMapper;
import com.avalon.packer.service.PluginsService;
import com.avalon.packer.utils.FileUtils;
import com.avalon.packer.utils.PathUtil;
import com.avalon.packer.utils.SVNUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static com.avalon.packer.http.AvalonError.SVN_ERROR;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xiaobin.wang
 * @since 2021-12-13
 */
@Service
@Slf4j
public class PluginsServiceImpl extends ServiceImpl<PluginsMapper, Plugins> implements PluginsService {

    @Value("${path.package.plugin.source}")
    private String pluginTargetPath;
    @Value("${plugins.source.svn.url}")
    private String pluginSvnUrl;
    @Value("${svn.username}")
    private String svnUsername;
    @Value("${svn.password}")
    private String svnPassword;

    @Override
    public boolean getPluginSourceFromSvn (String pluginCode) throws IOException {
        String toPluginsTargetPath = String.format(pluginTargetPath,pluginCode);
        File sourceFile = new File(toPluginsTargetPath);
        SVNUtils svnUtils = new SVNUtils(svnUsername,svnPassword);
        String fromSvnUrl = String.format(pluginSvnUrl,pluginCode);
        boolean getStatus = false;
        if(sourceFile.exists()){
            File[] files = sourceFile.listFiles();
            for (int i = 0; i < Objects.requireNonNull(files).length; i++) {
                FileUtils.deleteDir(toPluginsTargetPath + "/" + files[i].getName());
            }
            log.info("check out [{}] source",pluginCode);
            log.info("fromSvnUrl="+fromSvnUrl);
            log.info("toPluginsTargetPath="+toPluginsTargetPath);
        }else{
            log.info("create dir and check out [{}] source",pluginCode);
            sourceFile.mkdirs();
        }
        getStatus = svnUtils.checkOutModel(fromSvnUrl,toPluginsTargetPath, true);
        if(!getStatus){
            throw new AvalonException(SVN_ERROR,"svn 获取资源失败");
        }
        return true;
    };
}
