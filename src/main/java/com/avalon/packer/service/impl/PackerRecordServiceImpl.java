package com.avalon.packer.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.avalon.packer.datarepo.SourceFileRepo;
import com.avalon.packer.dto.packerRecord.AddRecordDto;
import com.avalon.packer.exception.AvalonException;
import com.avalon.packer.http.AvalonError;
import com.avalon.packer.http.AvalonHttpResp;
import com.avalon.packer.model.*;
import com.avalon.packer.mapper.PackerRecordMapper;
import com.avalon.packer.service.*;
import com.avalon.packer.utils.FileUtils;
import com.avalon.packer.utils.PathUtil;
import com.avalon.packer.utils.SVNUtils;
import com.avalon.packer.utils.ZzBeanCopierUtils;
import com.avalon.packer.vo.ConfigItemVo;
import com.avalon.packer.vo.PackageConfigVo;
import com.avalon.sdk.common.utils.ZipUtils;
import com.avalon.sdk.common.utils.ZzFtpClient;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jdk.nashorn.internal.runtime.regexp.RegExp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.avalon.packer.http.AvalonError.PARAM_ERROR;
import static com.avalon.packer.http.AvalonError.SVN_ERROR;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xiaobin.wang
 * @since 2021-12-21
 */
@Slf4j
@Service
public class PackerRecordServiceImpl extends ServiceImpl<PackerRecordMapper, PackerRecord> implements PackerRecordService {
    @Value("${file.win.path}")
    private String winPath;

    @Value("${file.unix.path}")
    private String unixPath;

    @Value("${path.package.channel.source}")
    private String channelTargetPath;
    @Value("${path.package.plugin.source}")
    private String pluginTargetPath;
    @Value("${channel.source.svn.url}")
    private String channelSvnUrl;
    @Value("${svn.username}")
    private String svnUsername;
    @Value("${svn.password}")
    private String svnPassword;
    @Value("${file.signPath.channel}")
    private String channelSignPath;
    @Value("${sys.device}")
    private String deviceType;
    @Value("${ftp.source.root}")
    private String ftpRoot;

    @Resource
    private ChannelService channelService;
    @Resource
    private RecordPluginsService recordPluginsService;
    @Resource
    private PluginsService pluginsService;
    @Resource
    private AppService appService;
    @Resource
    private RecordMediaService recordMediaService;
    @Resource
    private ConfigVersionService configVersionService;
    @Resource(name="gmMotherFtp")
    private ZzFtpClient ftpClient;

    @Override
    public PackageConfigVo checkOutOrUpdateChannelSourceDeep (
            String recordId,
            String version
    ) throws InterruptedException, IOException {
        PackerRecord packerRecord = getById(recordId);
        if(null ==packerRecord){
            throw new AvalonException(PARAM_ERROR,"记录ID错误");
        }
        Channel channel = channelService.getById(packerRecord.getChannelId());
        String channelCode = channel.getChannelCode();
        String toChannelTargetPath = String.format(channelTargetPath,channelCode + "/" + version);
        File sourceFile = new File(toChannelTargetPath);
        SVNUtils svnUtils = new SVNUtils(svnUsername,svnPassword);
        String fromSvnUrl = String.format(channelSvnUrl,channelCode + "/" + version);
        boolean getStatus = false;
        if(sourceFile.exists()){
            File[] files = sourceFile.listFiles();
            for (int i = 0; i < files.length; i++) {
                FileUtils.deleteDir(toChannelTargetPath + "/" + files[i].getName());
            }
            log.info("check out [{}] source",channelCode);
            log.info("fromSvnUrl="+fromSvnUrl);
            log.info("toChannelTargetPath="+toChannelTargetPath);
            getStatus = svnUtils.checkOutModel(fromSvnUrl,toChannelTargetPath, true);
        }else{
            log.info("create dir and check out [{}] source",channelCode);
            sourceFile.mkdirs();
            getStatus = svnUtils.checkOutModel(fromSvnUrl,toChannelTargetPath, true);
        }
        if(!getStatus){
            throw new AvalonException(SVN_ERROR,"svn 获取渠道资源失败");
        }
        List<String> versions = new ArrayList<>();
        File[] files = sourceFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if(file.isDirectory()&&!file.getName().contains("svn")){
                versions.add(file.getName());
            }
        }
        PackageConfigVo packageConfigVo = new PackageConfigVo();
        packageConfigVo.setVersions(versions);
        boolean returnFlag= true;
        while (returnFlag){
            Thread.sleep(2000);
            //去检测是否存在渠道文件夹
            File file = new File(toChannelTargetPath);
            if (file.exists()) {
                returnFlag= false;
            }
        }

        return packageConfigVo;
    }

    @Override
    public PackageConfigVo checkOutOrUpdateChannelSource(String recordId) throws InterruptedException, IOException {

        PackerRecord packerRecord = getById(recordId);
        if(null ==packerRecord){
            throw new AvalonException(PARAM_ERROR,"记录ID错误");
        }
        Channel channel = channelService.getById(packerRecord.getChannelId());
        String channelCode = channel.getChannelCode();
        String toChannelTargetPath = String.format(channelTargetPath,channelCode);
        File sourceFile = new File(toChannelTargetPath);
        SVNUtils svnUtils = new SVNUtils(svnUsername,svnPassword);
        String fromSvnUrl = String.format(channelSvnUrl,channelCode);
        boolean getStatus = false;
        if(sourceFile.exists()){
            File[] files = sourceFile.listFiles();
            for (int i = 0; i < files.length; i++) {
                FileUtils.deleteDir(toChannelTargetPath + "/" + files[i].getName());
            }
            log.info("check out [{}] source",channelCode);
            log.info("fromSvnUrl="+fromSvnUrl);
            log.info("toChannelTargetPath="+toChannelTargetPath);
            getStatus = svnUtils.checkOutModel(fromSvnUrl,toChannelTargetPath, true);
        }else{
            log.info("create dir and check out [{}] source",channelCode);
            sourceFile.mkdirs();
            getStatus = svnUtils.checkOutModel(fromSvnUrl,toChannelTargetPath, true);
        }
        if(!getStatus){
            throw new AvalonException(SVN_ERROR,"svn 获取渠道资源失败");
        }
        List<String> versions = new ArrayList<>();
        File[] files = sourceFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if(file.isDirectory()&&!file.getName().contains("svn")){
                versions.add(file.getName());
            }
        }
        PackageConfigVo packageConfigVo = new PackageConfigVo();
        packageConfigVo.setVersions(versions);
        boolean returnFlag= true;
        while (returnFlag){
            Thread.sleep(2000);
            //去检测是否存在渠道文件夹
            File file = new File(toChannelTargetPath);
            if (file.exists()) {
                returnFlag= false;
            }
        }

        return packageConfigVo;
    }

    @Override
    public void updateRecordSignFileName(String id, String name) {
        PackerRecord packerRecord = getById(id);
        if(null ==packerRecord){
            throw new AvalonException(PARAM_ERROR,"记录ID错误");
        }
        if (packerRecord.getIsMac()) {
            packerRecord.setMacSignFile(name);
        } else {
            packerRecord.setSignFilePath(name);
        }
        updateById(packerRecord);
    }

    @Override
    public void updateConfigDescFile (String id, String name) {
        PackerRecord packerRecord = getById(id);
        packerRecord.setDescFileName(name);
        updateById(packerRecord);
    }

    @Override
    public boolean checkRecordCompleted (PackerRecord packerRecord) throws AvalonException {
        // 母包是否配置
//        if (packerRecord.getSourceName() == null) {
//            return false;
//        }
        // 基础配置-渠道包名
        if (packerRecord.getPackerName() == null) {
            return false;
        }
        // 基础配置-安装游戏名
//        if (packerRecord.getGameName() == null) {
//            return false;
//        }
        // 基础配置-产物
        if (packerRecord.getResultType() == null && !Objects.equals(deviceType, "mac")) {
            return false;
        }
        // 渠道参数-渠道版本
        if (packerRecord.getChannelVersion() == null) {
            return false;
        }
        // 基础配置-打包环境
        if (packerRecord.getEnvCode() == null) {
            return false;
        }
        // 渠道参数-必填项验证
        Channel channel = channelService.getById(packerRecord.getChannelId());
        JSONObject baseConfig = JSON.parseObject(packerRecord.getBaseConfig());
        // client_config_doc
        List<ConfigItemVo> clientConfigDoc = JSON.parseArray(channel.getClientConfigDoc(), ConfigItemVo.class);
        JSONObject clientConfig = (JSONObject) baseConfig.get("clientConfigDoc");
        if (clientConfigDoc != null) {
            for (ConfigItemVo item :clientConfigDoc) {
                if (item.required && StringUtils.isEmpty(clientConfig.get(item.keyName))) {
                    return false;
                }
            }
        }
        // server_config_doc
        List<ConfigItemVo> serverConfigDoc = JSON.parseArray(channel.getServerConfigDoc(), ConfigItemVo.class);
        JSONObject serverConfig = (JSONObject) baseConfig.get("serverConfigDoc");
        if (serverConfigDoc != null) {
            for (ConfigItemVo item :serverConfigDoc) {
                if (item.required && StringUtils.isEmpty(serverConfig.get(item.keyName))) {
                    return false;
                }
            }
        }
        // extra
        List<ConfigItemVo> extra = JSON.parseArray(channel.getExtra(), ConfigItemVo.class);
        JSONObject extraConfig = (JSONObject) baseConfig.get("extra");
        if (extra != null) {
            for (ConfigItemVo item :extra) {
                if (item.required && StringUtils.isEmpty(extraConfig.get(item.keyName))) {
                    return false;
                }
            }
        }
        // 插件参数验证
        JSONObject plugins_config = JSON.parseObject(packerRecord.getPluginsConfig());
        LambdaQueryWrapper<RecordPlugins> pluginsWrapper = new LambdaQueryWrapper<>();
        pluginsWrapper.eq(RecordPlugins::getRecordId,packerRecord.getId());
        List<RecordPlugins> pluginsList = recordPluginsService.list(pluginsWrapper);
        for (RecordPlugins item :pluginsList) {
            Plugins plugin = pluginsService.getById(item.getPluginsId());
            String pluginCode = plugin.getCode();
            JSONObject nowConfig = plugins_config.getJSONObject(pluginCode);
            List<ConfigItemVo> pluginConfigDoc = JSON.parseArray(plugin.getExtra(), ConfigItemVo.class);
            if (pluginConfigDoc != null) {
                for (ConfigItemVo configItem :pluginConfigDoc) {
                    if (configItem.required) {
                        if (nowConfig == null) {
                            return false;
                        }
                        if (StringUtils.isEmpty(nowConfig.get(configItem.keyName))) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    @Override
    public void addPackerConfig (AddRecordDto dto) {
        if (null == appService.getById(dto.getAppId())) {
            throw new AvalonException(AvalonError.CACHE_ERROR,"appid无效");
        }
        Channel channel = channelService.getById(dto.getChannelId());
        if (null == channel) {
            throw new AvalonException(AvalonError.CHANNEL_DISABLED, "渠道无效");
        }
        QueryWrapper<PackerRecord> wp = new QueryWrapper<>();
        wp = wp.eq("app_id", dto.getAppId()).eq("config_name", dto.getConfigName());
        if (null != getOne(wp)) {
            throw new AvalonException(PARAM_ERROR, "配置名重复");
        }
        PackerRecord record = new PackerRecord();
        record.setAppId(dto.getAppId());
        record.setChannelId(dto.getChannelId());
        record.setConfigName(dto.getConfigName());
        record.setIsMac(channel.getIsMac());
        record.setCouldPack(false); // 创建时默认不可打包
//        record.setCouldDownload(false);
        record.setLastUpdateAs(dto.getLastUpdateAs()); // 创建人
        save(record);
    };

    @Override
    public void copyPackerConfig (AddRecordDto dto) {
        QueryWrapper<PackerRecord> wp = new QueryWrapper<>();
        wp = wp.eq("app_id", dto.getAppId()).eq("config_name", dto.getConfigName());
        if (null != getOne(wp)) {
            throw new AvalonException(PARAM_ERROR, "配置名重复");
        }
        PackerRecord copy = ZzBeanCopierUtils.copy(dto, new PackerRecord());
        String uid = UUID.randomUUID().toString().replaceAll("-", "");
        copy.setId(uid);
        copy.setLastUpdateAs(dto.getLastUpdateAs());
        save(copy);
        // 与媒体标识的关系
        List<String> mList = dto.getMediaList() == null ? new ArrayList<>() : Arrays.asList(dto.getMediaList().split(","));
        List<RecordMedia> rList = new ArrayList<>(Collections.emptyList());
        mList.forEach(i -> {
            RecordMedia cur = new RecordMedia();
            cur.setMediaId(i);
            cur.setRecordId(uid);
            cur.setId(null);
            rList.add(cur);
        });
        if (rList.size() > 0) {
            recordMediaService.saveBatch(rList);
        }
        // 与插件的关系
        List<RecordPlugins> pList = dto.getPluginList();
        List<RecordPlugins> sList = new ArrayList<>(Collections.emptyList());
        pList.forEach(i -> {
            RecordPlugins cur = new RecordPlugins();
            cur.setPluginsVersion(i.getPluginsVersion());
            cur.setPluginsId(i.getPluginsId());
            cur.setRecordId(uid);
            cur.setId(null);
            sList.add(cur);
        });
        recordPluginsService.saveBatch(sList);
    }

    @Override
    public void setConfigOtherFile (String id, String name) {
        PackerRecord packerRecord = getById(id);
        if (null == packerRecord) {
            throw new AvalonException(PARAM_ERROR, "无效的配置id");
        }
        String cur = "";
        if (Objects.equals(deviceType, "mac")) {
            cur = packerRecord.getMacOtherFile() == null ?  "" : packerRecord.getMacOtherFile() + ",";
            cur += name;
            packerRecord.setMacOtherFile(cur);
        } else {
            cur = packerRecord.getOtherFile() == null ?  "" : packerRecord.getOtherFile() + ",";
            cur += name;
            packerRecord.setOtherFile(cur);
        }
        updateById(packerRecord);
    }

    @Override
    public boolean delConfig (String id) {
        if (id == null) {
            throw new AvalonException(PARAM_ERROR, "id不能为空");
        }
        PackerRecord packerRecord = getById(id);

        if (packerRecord == null) {
            throw new AvalonException(PARAM_ERROR, "无效的配置ID");
        }
//        删除记录与插件的绑定
        QueryWrapper<RecordPlugins> wrapper = new QueryWrapper<>();
        wrapper = wrapper.eq("record_id", id);
        recordPluginsService.remove(wrapper);
//        删除记录与媒体标识的绑定
        QueryWrapper<RecordMedia> mediaWrapper = new QueryWrapper<>();
        mediaWrapper = mediaWrapper.eq("record_id", id);
        recordMediaService.remove(mediaWrapper);
        // 删除配置历史记录
        QueryWrapper<ConfigVersion> cvWp = new QueryWrapper<>();
        cvWp.eq("config_id", id);
        configVersionService.remove(cvWp);
        return removeById(id);
    }

    @Override
    public void delNoUseOtherFile (String pre, String cur, Boolean isMac, String channelCode) throws IOException {
         String rootPath = PathUtil.isWindows() ? winPath : unixPath;
        String path = rootPath + String.format(channelSignPath, channelCode) + (isMac ? "mac_other_file/" : "other_file/");
        String[] preList = pre == null ? new String[0] : pre.split(",");
        List<String> curList = cur == null ? new ArrayList<>() : Arrays.asList(cur.split(","));
        for (String item : preList) {
            if (!curList.contains(item)) {
                FileUtils.deleteDir(path + item);
            }
        }
    }

    @Override
    public List<String> getVersionsFromFtp (
            String type,
            String id
    ) throws IOException {
        String ftpPath = Objects.equals(type, "1") ? ftpRoot + "/channels" : ftpRoot + "/plugins";
        String symbol = null;
        if (Objects.equals(type, "1")) {
            Channel channel = channelService.getById(id);
            if (null != channel) {
                symbol = channel.getChannelCode();
            } else {
                throw new AvalonException(PARAM_ERROR, "无效的id");
            }
        }
        if (Objects.equals(type, "2")) {
            Plugins plugins = pluginsService.getById(id);
            if (null != plugins) {
                symbol = plugins.getCode();
            } else {
                throw new AvalonException(PARAM_ERROR, "无效的id");
            }
        }
        if (symbol == null) {
            throw new AvalonException(PARAM_ERROR, "无法获取code");
        }
        String[] ftpPaths = new String[]{ftpPath};
        Set<ZzFtpClient.FtpFileAttr> fileAttr = ftpClient.getFileNames(ftpPaths);
        List<String> ftpResult = new ArrayList<>();
        for (ZzFtpClient.FtpFileAttr item : fileAttr) {
            String fileName = item.getFileName();

            if (fileName.startsWith(symbol)) {
                List<String> nameArr = Arrays.asList(fileName.split(symbol));
                String reg = "^-[0-9.]*.zip$";
                boolean isMatch = Pattern.matches(reg, nameArr.get(1));
                if (isMatch) {
                    List<String> arr = Arrays.asList(fileName.split(".zip")[0].split("-"));
                    String version = arr.get(arr.size() - 1);
                    ftpResult.add(version);
                }
            }
        }
        return ftpResult;
    }

    @Override
    public List<RecordPlugins> getPluginsByRecordIds (
            List<String> list
    ) {
        List<RecordPlugins> result = new ArrayList<>();
        for (String item: list) {
            LambdaQueryWrapper<RecordPlugins> wp = new LambdaQueryWrapper<>();
            wp.eq(RecordPlugins::getRecordId, item);
            List<RecordPlugins> bList = recordPluginsService.list(wp);
            result.addAll(bList);
        }
        return result;
    }

    @Override
    public void updateSource (
            String fileName,
            String version,
            int sourceType // 1-渠道，2-插件
    ) throws Exception {
        String zipName = fileName + "-" + version + ".zip";
        String ftpPath = sourceType == 1 ? ftpRoot + "/channels" : ftpRoot + "/plugins";
        String fileDir = sourceType == 1 ? String.format(channelTargetPath, fileName) : String.format(pluginTargetPath, fileName);

        // 读取内存中文件的时间
        Calendar saveTime = SourceFileRepo.getTime(zipName);
        // 从对应路径读取文件信息
        Set<ZzFtpClient.FtpFileAttr> ftpFileInfo = ftpClient.getFileNames(new String[]{ftpPath});

        for (ZzFtpClient.FtpFileAttr item : ftpFileInfo) {
            if (Objects.equals(item.getFileName(), zipName)) {
                Calendar originTimeStamp = item.getTimestamp();
                if (originTimeStamp != saveTime) { // 时间戳不一致，需要更新
                    // 删除原资源文件
                    FileUtils.deleteDir(fileDir + version);
                    // 从ftp获取最新文件
                    File file = new File(fileDir);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    ftpClient.downLoadFile(ftpPath, zipName, fileDir);
                    File dir = new File(fileDir + "/" + version);
                    ZipUtils.unZip(fileDir + "/" + zipName, fileDir);
                    // 解压完成后更新文件时间
                    SourceFileRepo.updateFileInfo(item.getFileName(), item.getTimestamp());
                }
            }
        }
    }
}
