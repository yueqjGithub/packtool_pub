package com.avalon.packer.service.impl;

import com.alibaba.fastjson.JSON;
import com.avalon.packer.dto.historyrecord.FindHistoryRecordDto;
import com.avalon.packer.model.*;
import com.avalon.packer.mapper.HistoryRecordMapper;
import com.avalon.packer.service.*;
import com.avalon.packer.utils.*;
import com.avalon.packer.vo.HistoryRecordsVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Wrapper;
import java.text.DateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xiaobin.wang
 * @since 2022-01-06
 */
@Slf4j
@Service
public class HistoryRecordServiceImpl extends ServiceImpl<HistoryRecordMapper, HistoryRecord> implements HistoryRecordService {
    @Resource
    private PackerRecordService recordService;
    @Resource
    private AppService appService;

    @Resource
    private ChannelService channelService;

    @Resource
    private RecordPluginsService recordPluginsService;

    @Resource
    private RecordMediaService recordMediaService;

    @Resource
    private PluginsService pluginsService;

    @Resource
    private ChannelMediaPackageServiceImpl channelMediaPackageService;

    @Resource
    private SystemEnvService systemEnvService;

    @Value("${file.win.path}")
    private String winPath;

    @Value("${file.unix.path}")
    private String unixPath;

    @Value("${file.downloadUrl}")
    private String downloadUrl;

    @Value("${appstore.upload.command}")
    private String uploadCommand;

    @Value("${appstore.upload.public}")
    private String publicPath;

    @Value("${sys.device}")
    private String deviceType;

    @Override
    public void saveHistoryRecord(
            String supersdkVersion,
            String recordId,
            String motherPackageName,
            Map<String,Map<String, String>> mediaPackagesMap,
            List<Map<String, Object>> pluginsList,
            String packageName,
            String resultPath,
            String logFilePath,
            String opsUser,
            String envDesc,
            int buildNum,
            String hisId,
            int publicType
    ) {
        try {
            log.info("执行历史记录");
            log.info(String.valueOf(mediaPackagesMap));
            String rootPath = PathUtil.isWindows() ? winPath : unixPath;
            HistoryRecord historyRecord = getById(hisId);
            historyRecord.setOpsUser(opsUser);
            historyRecord.setEnvDesc(envDesc);
            PackerRecord record = recordService.getById(recordId);

            if(record == null){
                log.info("saveHistoryRecord record is null");
                return;
            }
            historyRecord.setConfigId(record.getId());
            historyRecord.setSupersdkVersion(supersdkVersion);
            // app相关
            App app = appService.getById(record.getAppId());
            historyRecord.setAppId(app.getId());
            if(null != app){
                historyRecord.setApp(app.getAppId());
            }
            // 包名
            historyRecord.setPackageName(packageName);
            // 渠道相关
            Channel channel = channelService.getById(record.getChannelId());
            historyRecord.setChannelId(record.getChannelId());
            if(null != channel){
                historyRecord.setChannelCode(channel.getChannelCode());
                historyRecord.setChannelName(channel.getChannelName());
                historyRecord.setChannelVersion(record.getChannelVersion());
            }
            log.info("app历史记录完成");
            // 母包相关
            historyRecord.setBuildNum(buildNum);
            historyRecord.setMotherName(motherPackageName);
            try {
                if(!StringUtils.isEmpty(motherPackageName)){
                    // 这个规则项目组需要统一命名的规则，暂时使用项目名称
                    String[] s = motherPackageName.split("_");
                    if(null !=s && s.length>0){
                        historyRecord.setMotherShortName(s[0] + "_" + s[2]);
                    }
                }
            } catch (Exception e) {
                historyRecord.setMotherShortName(motherPackageName);
            }
            log.info("母包历史记录完成");
            // 插件
            StringBuilder pluginCodeList = new StringBuilder();
            if (pluginsList != null) {
                for (Map<String, Object> plugin :pluginsList) {
                    if (plugin.get("plugin_code") != null & plugin.get("plugin_code") != "") {
                        pluginCodeList.append(plugin.get("plugin_code"));
                        pluginCodeList.append(",");
                    }
                }
            }
            historyRecord.setPluginList(pluginCodeList.toString());
//            historyRecord.setId(UUID.randomUUID().toString());
            updateById(historyRecord);
            log.info("插件历史记录完成");
            // 存放媒体资源包信息
            List<ChannelMediaPackage> channelMediaPackageList = new ArrayList<>(mediaPackagesMap.size());
            log.info("size====" + mediaPackagesMap.size());
            String id = historyRecord.getConfigId();
            mediaPackagesMap.forEach((k,v)->{
                log.info("k===" + k + "," + "v=" + v.get("apk_name"));
                ChannelMediaPackage channelMediaPackage = new ChannelMediaPackage();
                channelMediaPackage.setHrId(hisId);
                channelMediaPackage.setMediaName(k);
                channelMediaPackage.setPackageName(v.get("apk_name"));
                channelMediaPackage.setPublicType(publicType);
                if (Objects.equals(deviceType, "mac")) {
                    channelMediaPackage.setMd5Val(v.get("md5"));
                }
                String downUrl = resultPath + v.get("apk_name");
                downUrl = downUrl.replace(rootPath, "");
                channelMediaPackage.setDownUrl(downUrl);
                channelMediaPackageList.add(channelMediaPackage);
            });
            channelMediaPackageService.saveBatch(channelMediaPackageList);
            FileUtils.deleteDir(logFilePath);
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    @Override
    public Page<HistoryRecord> doPageHistoryRecords(FindHistoryRecordDto dto) {
        LambdaQueryWrapper<HistoryRecord> wrapper = getWrapper();
        wrapper.eq(HistoryRecord::getAppId, dto.getAppId());
        wrapper.eq(HistoryRecord::getPackStatus, 2);
        if (dto.getVersionCode() != null) {
            wrapper.eq(HistoryRecord::getVersionCode, dto.getVersionCode());
        }
        wrapper.orderByDesc(HistoryRecord::getCreateTime);
        wrapper.between(HistoryRecord::getCreateTime,dto.getRange().getStart(),dto.getRange().getEnd());
        Page<HistoryRecord> page = PageUtils.buildPage(dto);
        Page<HistoryRecord> pageResp = page(page, wrapper);
        return pageResp;
    }

    @Override
    public HistoryRecordsVo getHistoryRecordDetail(String id) {
        HistoryRecord byId = getById(id);
        HistoryRecordsVo historyRecordsVo = new HistoryRecordsVo();
        ZzBeanCopierUtils.copy(byId,historyRecordsVo);
        // 获取插件列表
//        LambdaQueryWrapper<RecordPlugins> recordPluginsWrapper = Wrappers.lambdaQuery();
//        recordPluginsWrapper.eq(RecordPlugins::getRecordId,byId.getRecordId());
//        List<RecordPlugins> recordPluginsList = recordPluginsService.list(recordPluginsWrapper);
        List<String> pluginsCodeList = Arrays.stream(byId.getPluginList().split(",")).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(pluginsCodeList)){
//            Set<String> pluginIds = recordPluginsList.stream().map(RecordPlugins::getPluginsId).collect(Collectors.toSet());
//            LambdaQueryWrapper<Plugins> pluginsWrapper = Wrappers.lambdaQuery();
//            pluginsWrapper.in(Plugins::getId,pluginIds);
            List<Plugins> pluginsList = new ArrayList<>(Collections.emptyList());
            for (String pluginCode :pluginsCodeList) {
                LambdaQueryWrapper<Plugins> pluginsWrapper = Wrappers.lambdaQuery();
                pluginsWrapper.eq(Plugins::getCode, pluginCode);
                Plugins target = pluginsService.getOne(pluginsWrapper);
                pluginsList.add(target);
            }
            historyRecordsVo.setPluginsList(pluginsList);
        }
        // 获取媒体标识包
        LambdaQueryWrapper<ChannelMediaPackage> recordMediaWrapper = Wrappers.lambdaQuery();
        recordMediaWrapper.eq(ChannelMediaPackage::getHrId,id);
        historyRecordsVo.setMediaFinishedPackagesList(channelMediaPackageService.list(recordMediaWrapper));
        historyRecordsVo.setDownloadHost(downloadUrl);
        return historyRecordsVo;
    }

    @Override
    public String saveHistoryByConfig (
            String configId,
            String motherPackage,
            String channelId,
            String appId,
            String ops,
            int isFtp,
            int versionCode
    ) {
        // 获取config详细
        PackerRecord config = recordService.getById(configId);
        HistoryRecord history = new HistoryRecord();
        // 3.5.0 versioncode
        history.setVersionCode(versionCode);
        // 母包,操作人
        history.setMotherIsFtp(isFtp);
        history.setMotherName(motherPackage);
        try {
            if(!StringUtils.isEmpty(motherPackage)){
                // 这个规则项目组需要统一命名的规则，暂时使用项目名称
                String[] s = motherPackage.split("_");
                if(s.length>0){
                    history.setMotherShortName(s[0] + "_" + s[2]);
                }
            }
        } catch (Exception e) {
            history.setMotherShortName(motherPackage);
        }
        history.setOpsUser(ops);
        // 配置ID app
        history.setConfigId(configId);
        history.setAppId(appId);
        App app = appService.getById(appId);
        history.setApp(app.getAppId());
        // 渠道
        Channel channel = channelService.getById(channelId);
        history.setChannelId(channelId);
        history.setChannelCode(channel.getChannelCode());
        history.setChannelName(channel.getChannelName());
        history.setChannelVersion(config.getChannelVersion());
        // env
        String envCode = config.getEnvCode();
        LambdaQueryWrapper<SystemEnv> envWp = new LambdaQueryWrapper<>();
        SystemEnv env = systemEnvService.getOne(envWp.eq(SystemEnv::getEnvCode, envCode));
        history.setEnvDesc(env.getEnvDesc());
        // 插件
        LambdaQueryWrapper<RecordPlugins> pWp = new LambdaQueryWrapper<>();
        List<RecordPlugins> pluginsList = recordPluginsService.list(pWp.eq(RecordPlugins::getRecordId, configId));
        StringBuilder pluginCodes = new StringBuilder();
        for (RecordPlugins item : pluginsList) {
            String pluginId = item.getPluginsId();
            Plugins plugin = pluginsService.getById(pluginId);
            pluginCodes.append(plugin.getCode());
            pluginCodes.append(",");
        }
        history.setPluginList(pluginCodes.toString());
        history.setPackStatus(1);
        // 生成id,返回
        String uid = UUID.randomUUID().toString();
        history.setId(uid);
        save(history);
        return uid;
    }

    @Override
    public List<HistoryRecordsVo> getDetailList (String[] ids) {
        List<HistoryRecord> hisList = listByIds(Arrays.stream(ids).collect(Collectors.toList()));
        List<HistoryRecordsVo> result = new ArrayList<>();
        for (HistoryRecord item : hisList) {
            String hisJson = JSON.toJSONString(item);
            HistoryRecordsVo vo = JSON.parseObject(hisJson, HistoryRecordsVo.class);
            // 插件列表
            List<String> pluginsCodeList = Arrays.stream(item.getPluginList().split(",")).collect(Collectors.toList());
            List<Plugins> pluginsList = new ArrayList<>(Collections.emptyList());
            if(!CollectionUtils.isEmpty(pluginsCodeList)){
                for (String pluginCode :pluginsCodeList) {
                    LambdaQueryWrapper<Plugins> pluginsWrapper = Wrappers.lambdaQuery();
                    pluginsWrapper.eq(Plugins::getCode, pluginCode);
                    Plugins target = pluginsService.getOne(pluginsWrapper);
                    pluginsList.add(target);
                }
            }
            vo.setPluginsList(pluginsList);
            // 获取媒体标识包
            LambdaQueryWrapper<ChannelMediaPackage> recordMediaWrapper = Wrappers.lambdaQuery();
            recordMediaWrapper.eq(ChannelMediaPackage::getHrId,item.getId());
            vo.setMediaFinishedPackagesList(channelMediaPackageService.list(recordMediaWrapper));
            vo.setDownloadHost(downloadUrl);
            result.add(vo);
        }
        return result;
    }

    @Override
    public void uploadIpaToStore (
            String id,
            String account,
            String pwd
    ) throws Exception {
        String rootPath = PathUtil.isWindows() ? winPath : unixPath;
        ChannelMediaPackage target = channelMediaPackageService.getById(id);
        String filePath = rootPath + target.getDownUrl();
        File file = new File(filePath);
        String targetPath = rootPath + publicPath + file.getName();
        FileUtils.CopyFile(filePath, targetPath);

        String xmlPath = XmlTools.SetXml(
                account,
                Long.toString(file.length()),
                file.getName(),
                target.getMd5Val(),
                rootPath + publicPath + "ipa_metadata.xml"
        );
        String[] args = new String[]{
                uploadCommand,
                "-m",
                "upload",
                "-u",
                account,
                "-p",
                pwd,
                "-f",
                xmlPath
        };
        new Thread(() -> {
            log.info("【上传APPSTORE】:" + file.getName());
            log.info("【上传APPSTORE】:" + file.getPath());
            try {
                Process proc = Runtime.getRuntime().exec(args);
                BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                String err = null;
                try (BufferedReader stdError = new BufferedReader(
                        new InputStreamReader(proc.getErrorStream()))) {
                    while ((err = stdError.readLine()) != null) {
                        log.info("【APPSTORE上传进程err】:" + err);
                        if (err.length() > 50) {
                            err = err.substring(0, 50);
                        }
                        target.setFailReason(err);
                    }
                } catch (Exception e) {
                    log.error("读取进程错误日志输出时发生了异常" + e);
                }
                String line = null;
                while ((line = in.readLine()) != null) {
                    log.info("【APPSTORE上传进程info】:" + line);
                }
                proc.waitFor();
                int code = proc.exitValue();
                if (code == 0) { // 成功
                    target.setFailReason("success");
                } else {
                    log.info("exitValue================================================" + code);
                }
                in.close();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                channelMediaPackageService.updateById(target);
                try {
                    FileUtils.deleteDir(targetPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
