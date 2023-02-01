package com.avalon.packer.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.avalon.packer.advice.LogAspect;
import com.avalon.packer.bo.PackerThreadBo;
import com.avalon.packer.dto.packerRecord.*;
import com.avalon.packer.exception.AvalonException;
import com.avalon.packer.http.AvalonHttpResp;
import com.avalon.packer.mapper.PackerRecordMapper;
import com.avalon.packer.model.*;
import com.avalon.packer.service.*;
import com.avalon.packer.threadpool.UploadFullPackageTheadPool;
import com.avalon.packer.utils.FileUtils;
import com.avalon.packer.utils.PathUtil;
import com.avalon.packer.utils.ZzBeanCopierUtils;
import com.avalon.packer.vo.MotherPackageNamesVo;
import com.avalon.sdk.common.utils.ZzFtpClient;
import com.avalon.sdk.iam.authInfohelper.AuthUserInfoHelper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.python.bouncycastle.crypto.util.Pack;
import org.python.util.PythonInterpreter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.*;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.avalon.packer.http.AvalonError.FTP_DOWN_ERROR;
import static com.avalon.packer.http.AvalonError.PARAM_ERROR;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author xiaobin.wang
 * @since 2021-12-21
 */
@RestController
@RequestMapping("/admin/packerRecord")
@Api(tags = "打包记录")
@Slf4j
public class PackerRecordController {
    @Resource
    private MotherPackagesService motherPackagesService;

    @Autowired
    private PackerRecordMapper packerRecordMapper;

    @Autowired
    private PackerRecordService packerRecordService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private PluginsService pluginsService;

    @Autowired
    private RecordPluginsService recordPluginsService;

    @Autowired
    private RecordMediaService recordMediaService;

    @Autowired
    private PackerStatusService packerStatusService;

    @Autowired
    private AppService appService;

    @Autowired
    private MediaFlagService mediaFlagService;

    @Resource
    private HistoryRecordService historyRecordService;

    @Resource
    private ConfigVersionService configVersionService;

    @Resource
    private SystemEnvService systemEnvService;

    @Value("${python.file.path}")
    private String pythonFilePath;

    @Value("${file.win.path}")
    private String winPath;

    @Value("${file.unix.path}")
    private String unixPath;

    @Value("${file.motherPath}")
    private String motherPath;

    @Value("${file.url}")
    private String onlineHost;

    @Value("${file.signPath.app}")
    private String appSignPath;

    @Value("${file.signPath.channel}")
    private String channelSignPath;

    @Value("${path.package.channel.source}")
    private String channelTargetPath;

    @Value("${ftp.url}")
    private String ftpHost;

    @Value("${python.command}")
    private String pythonCommand;

    @Value("${sys.device}")
    private String deviceType;

    @Value("${devops.username}")
    private String devUsername;

    @Value("${devops.pwd}")
    private String devPwd;

    @Resource(name="gmMotherFtp")
    private ZzFtpClient ftpClient;

    @GetMapping("")
    @ApiOperation(value = "获取打包配置列表")
    public AvalonHttpResp<?> getFullList(FindPackerRecordDto dto) {
        String appId = dto.getAppId();
        if (appId == null) {
            return AvalonHttpResp.failed("请选择APP");
        }
        ArrayList<ResultDto> resultList = packerRecordMapper.getFullList(dto.getAppId());
        ArrayList<ResultDto> filterList = new ArrayList<>();
        for (ResultDto item : resultList) {
            if (Objects.equals(deviceType, "mac") && item.getIsMac()) {
                filterList.add(item);
            }
            if (!Objects.equals(deviceType, "mac") && !item.getIsMac()) {
                filterList.add(item);
            }
        }
        return AvalonHttpResp.ok(filterList);
    }

    @PostMapping("/getPlugins")
    @ApiOperation(value = "获取分包配置包含的插件id集合")
    public AvalonHttpResp<List<RecordPlugins>> getPluginsIds (@Validated @RequestBody GetPluginIdsDto dto) {
        return AvalonHttpResp.ok(packerRecordService.getPluginsByRecordIds(dto.getIds()));
    }

    @PostMapping("/update")
    @ApiOperation(value = "更新打包配置")
    @Transactional
    public AvalonHttpResp<?> updateRecord(@Validated @RequestBody UpdateRecordDto dto) {
        String recordId = dto.getId();
        PackerRecord packerRecord = packerRecordService.getById(recordId);
        // 历史记录存放
        configVersionService.saveConfigVersion(recordId);
        PackerRecord already = JSON.parseObject(JSON.toJSONString(packerRecord), PackerRecord.class);
        if (packerRecord == null) {
            return AvalonHttpResp.failed("未找到对应打包配置");
        }
        PackerRecord copy = ZzBeanCopierUtils.copy(dto, packerRecord);
        boolean couldPack = packerRecordService.checkRecordCompleted(copy);
        copy.setCouldPack(couldPack);
        copy.setUpdateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        // packerRecordService.delNoUseOtherFile(preFileList, Objects.equals(deviceType, "mac") ? dto.getMacOtherFile() : dto.getOtherFile(), Objects.equals(deviceType, "mac"), channel.getChannelCode());
        packerRecordService.updateById(copy);
        return AvalonHttpResp.ok();
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加打包配置")
    public AvalonHttpResp<?> addChannel(@Validated @RequestBody AddRecordDto dto) {
        if (dto.getIsCopy() != null && dto.getIsCopy()) {
            packerRecordService.copyPackerConfig(dto);
        } else {
            packerRecordService.addPackerConfig(dto);
        }
        return AvalonHttpResp.ok();
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除打包配置")
    public AvalonHttpResp<?> deleteRecord(@PathVariable String id) {
        return AvalonHttpResp.ok(packerRecordService.delConfig(id));
    }

    @PostMapping("/setMedia")
    @ApiOperation(value = "设置媒体标识")
    public AvalonHttpResp<?> setMedia(@Validated @RequestBody SetMediaDto dto) {
        String recordId = dto.getRecordId();
        String medias = dto.getMediaIds();
        PackerRecord record = packerRecordService.getById(recordId);
        if (record == null) {
            return AvalonHttpResp.failed("未找到对应打包配置");
        }
        Set<String> mediaSet = StringUtils.isEmpty(medias) ? Collections.emptySet() : Arrays.stream(medias.split(",")).collect(Collectors.toSet());
        QueryWrapper<RecordMedia> listWrapper = new QueryWrapper<>();
        listWrapper = listWrapper.eq("record_id", recordId);
        List<RecordMedia> alreadyList = recordMediaService.list(listWrapper);
        List<RecordMedia> removeList = alreadyList.stream().filter(item -> !mediaSet.contains(item.getMediaId())).collect(Collectors.toList());
        for (RecordMedia removeItem : removeList) {
            recordMediaService.removeById(removeItem.getId());
        }
        for (String media : mediaSet) {
            QueryWrapper<RecordMedia> wrapper = new QueryWrapper<>();
            wrapper = wrapper.eq("record_id", recordId);
            wrapper = wrapper.eq("media_id", media);
            RecordMedia check = recordMediaService.getOne(wrapper);
            if (check == null) {
                RecordMedia recordMedia = new RecordMedia();
                recordMedia.setRecordId(recordId);
                recordMedia.setMediaId(media);
                recordMediaService.save(recordMedia);
            }
        }
        record.setUpdateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        packerRecordService.updateById(record);
//        List<RecordMedia> nowList = recordMediaService.list(listWrapper);
//        boolean couldDownload = ZzBeanCopierUtils.compareObject(alreadyList, nowList);
//        if (!couldDownload) {
//            record.setCouldDownload(false);
//            packerRecordService.updateById(record);
//        }
        return AvalonHttpResp.ok();
    }

    @PostMapping("/setPlugins")
    @ApiOperation(value = "设置插件")
    public AvalonHttpResp<?> setPlugins(@Validated @RequestBody SetPluginsDto dto) {
        String recordId = dto.getRecordId();
        List<RecordPlugins> plugins = dto.getPluginIds();
        PackerRecord record = packerRecordService.getById(recordId);
        if (record == null) {
            return AvalonHttpResp.failed("未找到对应打包配置");
        }
        // 获得已有pluginsConfig
        String pluginsConfig = record.getPluginsConfig();
        JSONObject jsonConfig = JSONObject.parseObject(pluginsConfig);
        // 获取本次配置的plugins的id集合
//        Set<String> pluginSet = plugins.isEmpty() ? Collections.emptySet() : Arrays.stream(plugins.split(",")).collect(Collectors.toSet());
        Set<String> pluginSet = new HashSet<>();
        for (RecordPlugins rp : plugins) {
            pluginSet.add(rp.getPluginsId());
        }
        QueryWrapper<RecordPlugins> listWrapper = new QueryWrapper<>();
        listWrapper = listWrapper.eq("record_id", recordId);
        List<RecordPlugins> alreadyList = recordPluginsService.list(listWrapper);
        List<RecordPlugins> removeList = alreadyList.stream().filter(item -> !pluginSet.contains(item.getPluginsId())).collect(Collectors.toList());
        for (RecordPlugins removeItem : removeList) {
            // 获取插件信息
            Plugins targetPlugins = pluginsService.getById(removeItem.getPluginsId());
            String pluginsCode = targetPlugins.getCode();
            if (jsonConfig != null) {
                jsonConfig.remove(pluginsCode);
            }
            recordPluginsService.removeById(removeItem.getId());
        }
        record.setPluginsConfig(jsonConfig == null ? "" : JSONObject.toJSONString(jsonConfig));
        for (RecordPlugins plugin : plugins) {
            QueryWrapper<RecordPlugins> wrapper = new QueryWrapper<>();
            wrapper = wrapper.eq("record_id", recordId);
            wrapper = wrapper.eq("plugins_id", plugin.getPluginsId());
            RecordPlugins check = recordPluginsService.getOne(wrapper);
            if (check == null) {
                RecordPlugins recordPlugins = new RecordPlugins();
                recordPlugins.setRecordId(recordId);
                recordPlugins.setPluginsId(plugin.getPluginsId());
                recordPlugins.setPluginsVersion(plugin.getPluginsVersion());
                recordPluginsService.save(recordPlugins);
            } else {
                check.setPluginsVersion(plugin.getPluginsVersion());
                recordPluginsService.updateById(check);
            }
        }
        boolean couldPack = packerRecordService.checkRecordCompleted(record);
        record.setCouldPack(couldPack);
        record.setUpdateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        packerRecordService.updateById(record);
        return AvalonHttpResp.ok();
    }

    @GetMapping("/querySourceList")
    @ApiOperation(value = "获取母包列表")
    public AvalonHttpResp<MotherPackageNamesVo> querySourceList(@RequestParam String appId) throws IOException {
        App app = appService.getById(appId);
        if (app == null) {
            return AvalonHttpResp.failed("未找到应用");
        }
        MotherPackageNamesVo vo = new MotherPackageNamesVo();
        // 获取本地上传母包的文件名
        LambdaQueryWrapper<MotherPackages> wrapper = new LambdaQueryWrapper<>();
        wrapper = wrapper.eq(MotherPackages::getAppId, app.getAppId())
                        .eq(MotherPackages::getIsMac, Objects.equals(deviceType, "mac"))
                        .select(MotherPackages::getPackageName);
        wrapper.orderByDesc(MotherPackages::getCreateTime);
        List<MotherPackages> tempList = motherPackagesService.list(wrapper);
        List<String> nameList = new ArrayList<>(Collections.emptyList());
        if (tempList != null) {
            for (MotherPackages fileItem : tempList) {
                nameList.add(fileItem.getPackageName());
            }
        }
        vo.setUploadNames(nameList);
        // 获取ftp服务器上母包的名称列表
        String motherFtpPaths = app.getMotherFtpPaths();
        if (StringUtils.isEmpty(motherFtpPaths)) {
            return AvalonHttpResp.ok(vo);
        }
        String[] splitPaths = motherFtpPaths.split(",");
        Set<ZzFtpClient.FtpFileAttr> fileAttr = ftpClient.getFileNames(splitPaths);
        Set<ZzFtpClient.FtpFileAttr> ftpResult = new HashSet<>();
        for (ZzFtpClient.FtpFileAttr item : fileAttr) {
            if (!item.getFileName().startsWith("Finished_")) {
                if (Objects.equals(deviceType, "mac")) {
                    if (item.getFileName().endsWith("Xcode.zip")) {
                        ftpResult.add(item);
                    }
                } else {
                    String[] arr = item.getFileName().split("\\.");
                    if (Objects.equals(arr[arr.length - 1], "apk") || Objects.equals(arr[arr.length - 1], "aab")) {
                        ftpResult.add(item);
                    }
                }
            }
        }
        // 进行排序
        Set<ZzFtpClient.FtpFileAttr> collect = ftpResult.stream().sorted(new Comparator<ZzFtpClient.FtpFileAttr>() {
            @Override
            public int compare(ZzFtpClient.FtpFileAttr o1, ZzFtpClient.FtpFileAttr o2) {
                long d1 = o1.getTimestamp().getTime().getTime();
                long d2 = o2.getTimestamp().getTime().getTime();
                if (d1 >= d2) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }).collect(Collectors.toCollection(LinkedHashSet::new));
        vo.setFtpNames(collect);
        return AvalonHttpResp.ok(vo);
    }

    @PostMapping("/package")
    @ApiOperation(value="打包")
    public AvalonHttpResp<?> doPackage (@RequestBody @Valid packageDto dto) throws Exception {
        // 检测参数有效性
        App app = appService.getById(dto.getId());
        if (null == app) {
            return AvalonHttpResp.failed("应用ID无效");
        }

        String[] configs = dto.getConfigs();
        List<String> configIds = new ArrayList<>();
        List<PackerRecord> configList = packerRecordService.list();
        for (PackerRecord item : configList) {
            configIds.add(item.getId());
        }
        for (String item : configs) {
            if (!configIds.contains(item)) {
                return AvalonHttpResp.failed("未找到" + item + "配置");
            }
        }

        String ops = dto.getOps();
        String motherPackage = dto.getMotherPackage();

        List<String> idList = new ArrayList<>();
        Map<String, List<PackerThreadBo>> confMap = new HashMap<>();
        List<String> denyList = new ArrayList<>();

        int versionCode = app.getVersionCode();

        for (String conf : configs) {
            PackerRecord confEntity = packerRecordService.getById(conf);
            QueryWrapper<HistoryRecord> wp = new QueryWrapper<>();
            wp = wp.eq("config_id", conf).eq("pack_status", 1);
            List<HistoryRecord> packagingConfigs = historyRecordService.list(wp);
            if (packagingConfigs.size() > 0) { // 该配置正在使用
                denyList.add(confEntity.getConfigName());
                continue;
            }
            // 生成历史记录提供打包状态消费
            String hisId = historyRecordService.saveHistoryByConfig(
                    conf,
                    motherPackage,
                    confEntity.getChannelId(),
                    dto.getId(),
                    ops,
                    dto.getMotherIsFtp(),
                    versionCode
            );
            idList.add(hisId);
            String confChannelId = confEntity.getChannelId();
            if (confMap.get(confChannelId) == null) {
                List<PackerThreadBo> entityList = new ArrayList<>();
                String jsonObject = JSON.toJSONString(confEntity);
                PackerThreadBo ptb = JSON.parseObject(jsonObject, PackerThreadBo.class);
                ptb.setHisId(hisId);
                entityList.add(ptb);
                confMap.put(confChannelId, entityList);
            } else {
                List<PackerThreadBo> entityList = confMap.get(confChannelId);
                String jsonObject = JSON.toJSONString(confEntity);
                PackerThreadBo ptb = JSON.parseObject(jsonObject, PackerThreadBo.class);
                ptb.setHisId(hisId);
                entityList.add(ptb);
                confMap.put(confChannelId, entityList);
            }
        }
        // 统一的versionCode供使用
//        int versionCode = app.getVersionCode();
        log.info("versionCode ============={}", versionCode);
        for (List<PackerThreadBo> val : confMap.values()) {
            packageThread(
                    val,
                    dto.getId(),
                    dto.getMotherIsFtp(),
                    ops,
                    motherPackage,
                    dto.getFtpPath(),
                    versionCode
            );
        }
        app.setVersionCode(versionCode + 1);
        appService.updateById(app);
        Map<String, List<?>> boList = new HashMap<>();
        boList.put("hisList", idList);
        boList.put("denyList", denyList);
        return AvalonHttpResp.ok(boList);
    }


    @GetMapping("/queryVersion")
    @ApiOperation(
            value = "获取渠道、插件版本"
    )
    public AvalonHttpResp<?> getChannel(@Validated GetVersionDto dto) throws IOException {
        return AvalonHttpResp.ok(packerRecordService.getVersionsFromFtp(dto.getType(), dto.getId()));
    }

    @PutMapping("/setMotherPackageMateData")
    @ApiOperation(
            value = "选择母包时设置母包的元数据"
    )
    public AvalonHttpResp<?> setMotherPackageMateData(@RequestBody SetMotherPackageMateDataDto dto) {
        if (StringUtils.isEmpty(dto.getId())) {
            throw new AvalonException(PARAM_ERROR);
        }
        if (1 == dto.getMotherIsFtp()) {
            if (StringUtils.isEmpty(dto.getFtpPath())) {
                throw new AvalonException(PARAM_ERROR, "FtpPath is not Empty when 1== motherIsFtp");
            }
        }
        PackerRecord record = packerRecordMapper.selectById(dto.getId());
        if (null == record) {
            throw new AvalonException(PARAM_ERROR, "操作的对象不存在");
        }
//        record.setMotherIsFtp(dto.getMotherIsFtp());
        record.setFtpPath(dto.getFtpPath());
        int update = packerRecordMapper.updateById(record);
        if (update <= 0) {
            return AvalonHttpResp.failed("操作失败");
        }
        return AvalonHttpResp.ok();
    }


    private String getMotherFromFtp(String downRootPath, String sourceName, String ftpPath) throws Exception {
        // Ftp下载的包存放在app路径下的ftp路径下
        StringBuilder downPath = new StringBuilder(downRootPath)
                .append("ftp/");
        File downPathFile = new File(downPath.toString());
        if (!downPathFile.exists()) {
            downPathFile.mkdirs();
        }
        //判断文件是否存在，如果不存在则去下载
        String sourceFullPath = downPath + sourceName;
        File sourceFile = new File(sourceFullPath);
        if (sourceFile.exists()) {
            return sourceFullPath;
        }
        boolean down = ftpClient.downLoadFile(ftpPath, sourceName, downPath.toString());
        if (!down) {
            throw new AvalonException(FTP_DOWN_ERROR);
        }
        return sourceFullPath;
    }

    private void packageThread (
//            PackerRecord config,
            List<PackerThreadBo> stock,
//            String historyId,
            String appId,
            int isFtp,
            String ops,
            String motherPackage,
            String ftpPath,
            int versionCode
    ) throws Exception {
        // isMac
        boolean isMac = Objects.equals(deviceType, "mac");
        // app
        PackageAppDto app = JSON.parseObject(JSON.toJSONString(appService.getById(appId)), PackageAppDto.class);
        int stockSize = stock.size();
        PackerThreadBo config = stock.get(stockSize - 1);
        // 3.3.0-拼接临时文件目录
        // String tempDir = config.getId();
        // 3.3.0-渠道资源拉取
        // TODO 后续版本增加临时目录，避免打包资源文件冲突


        String historyId = config.getHisId();
        HistoryRecord history = historyRecordService.getById(historyId);
        // rootpath
        String rootPath = PathUtil.isWindows() ? winPath : unixPath;

        Map<String, Object> map = new HashMap<>();

        if (Objects.equals(app.getScreenOrientation(), "landscape")) {
            app.setScreenOrientation("sensorLandscape");
        }
        if (Objects.equals(app.getScreenOrientation(), "portrait")) {
            app.setScreenOrientation("sensorPortrait");
        }
        if (app.getSignFileAlias() == null) {
            app.setSignFileAlias("");
        }
        if (app.getSignFilePath() == null) {
            app.setSignFilePath("");
        } else {
            String fullSignPath = rootPath + String.format(appSignPath, app.getAppId()) + app.getSignFilePath();
            app.setSignFilePath(fullSignPath);
        }
        if (app.getSignFileKeyPassword() == null) {
            app.setSignFileKeyPassword("");
        }
        if (app.getSignFileKeystorePassword() == null) {
            app.setSignFileKeystorePassword("");
        }
        // ios证书
        if (app.getMacSignFile() == null) {
            app.setCertFilePath("");
        } else {
            String fullMacSignPath = rootPath + String.format(appSignPath, app.getAppId()) + "/mac_sign/" + app.getMacSignFile();
            app.setCertFilePath(fullMacSignPath);
        }
        // ios证书密码
        app.setCertFilePassword(app.getMacCertPwd() == null ? "" : app.getMacCertPwd());
        // ios描述文件
        app.setProvisionFilePath(app.getDescFileName() == null ? "" : rootPath + String.format(appSignPath, app.getAppId()) + "/mac_desc/" + app.getDescFileName());

        // 拼接母包路径
        // 此处母包的获取方式有两种，一种为本地获取，另一种为从FTP获取。
        //若果从Ftp获取，则需要判断改文件在本地是否存在，如果存在直接进行打包，如果不存在则先去Ftp上下载改文件到本地，然后再去打包
        String sourceFullPath = "";
        String appRootPath = rootPath + motherPath + app.getAppId() + "/";
        if (isFtp == 1) {
//            sourceFullPath = getMotherFromFtp(appRootPath, motherPackage, ftpPath);
            sourceFullPath = appRootPath + "ftp/" + motherPackage;
        } else {
            sourceFullPath = appRootPath + motherPackage;
        }
        if (isMac) {
            app.setXcodePath(sourceFullPath);
            app.setSourcePath("");
        } else {
            app.setSourcePath(sourceFullPath);
        }
        map.put("app_config", app);

        // 渠道参数
        Channel channel = channelService.getById(config.getChannelId());
        // 渠道资源更新
        packerRecordService.updateSource(
                channel.getChannelCode(),
                config.getChannelVersion(),
                1
        );
        JSONObject channelConfig = JSONObject.parseObject(config.getBaseConfig());
        JSONObject clientConfig = (JSONObject) channelConfig.get("clientConfigDoc");
        if (clientConfig == null && !isMac) {
            history.setPackStatus(3);
            history.setReason("渠道客户端参数未配置");
            throw new Exception("渠道客户端参数未配置");
        }
        channelConfig.put("channel_code", channel.getChannelCode());
        channelConfig.put("channelId", channel.getChannelId());

        String iconUrl = config.getIconUrl();
        iconUrl = StringUtils.isEmpty(iconUrl) ? "" : iconUrl.replaceAll(onlineHost, rootPath);
        channelConfig.put("icon", iconUrl);

        String splashUrl = config.getSplashUrl();
        splashUrl = StringUtils.isEmpty(splashUrl) ? "" : splashUrl.replaceAll(onlineHost, rootPath);
        channelConfig.put("sdk_splash", splashUrl);

        channelConfig.put("channelVersion", config.getChannelVersion());
        channelConfig.put("gameName", config.getGameName() == null ? "" : config.getGameName());
        channelConfig.put("versionCode", String.valueOf(versionCode));
        channelConfig.put("resultType", config.getResultType() == null ? "apk" : config.getResultType());
        String signChannelPath = config.getSignFilePath() == null ? "" : rootPath + String.format(channelSignPath, channel.getChannelCode()) + config.getSignFilePath();
        channelConfig.put("signFilePath", signChannelPath);
        channelConfig.put("signFileKeystorePassword", config.getSignFileKeystorePassword() != null ? config.getSignFileKeystorePassword() : "");
        channelConfig.put("signFileKeyPassword", config.getSignFileKeyPassword() != null ? config.getSignFileKeyPassword() : "");
        channelConfig.put("signFileAlias", config.getSignFileAlias() != null ? config.getSignFileAlias() : "");
        channelConfig.put("publicArea", config.getPublicArea() != null ? config.getPublicArea() : "CN");
        // ios证书
        if (isMac) {
            channelConfig.put("publicType", config.getPublicType() == null ? "" : config.getPublicType());
            channelConfig.put("devUsername", devUsername);
            channelConfig.put("devPwd", devPwd);
            channelConfig.put("certFilePassword", config.getMacCertPwd() == null ? "" : config.getMacCertPwd());
            channelConfig.put("certFilePath", config.getMacSignFile() == null ? "" : rootPath + String.format(channelSignPath, channel.getChannelCode()) + "mac_sign/" + config.getMacSignFile());
            channelConfig.put("provisionFilePath", config.getMacSignFile() == null ? "" : rootPath + String.format(channelSignPath, channel.getChannelCode()) + "mac_desc/" + config.getDescFileName());
        }
        if (StringUtils.isEmpty(config.getPackerName())) {
            history.setPackStatus(3);
            history.setReason("基础配置-渠道包名/bundleId未配置");
            historyRecordService.updateById(history);
            throw new Exception("基础配置-渠道包名/bundleId未配置");
        }
        if (isMac) {
            channelConfig.put("bundleId", config.getPackerName());
            channelConfig.put("packageName", "");
        } else {
            channelConfig.put("packageName", config.getPackerName());
        }
        // 传python3启动位置
        channelConfig.put("pythonCommand", pythonCommand);
        // 其他文件
        String otherFileStr = isMac ? config.getMacOtherFile() : config.getOtherFile();
        ArrayList<String> otherResult = new ArrayList<>();
        String otherDirName = isMac ? "mac_other_file/" : "other_file/";
        String otherPath = rootPath + String.format(channelSignPath, channel.getChannelCode()) + otherDirName;

        if (!Objects.equals(otherFileStr, null)) {
            String[] otherFileArr = otherFileStr.split(",");
            for (String item : otherFileArr) {
                otherResult.add(otherPath + item);
            }
        }

        channelConfig.put("otherFiles", otherResult);
        // 日志路径
        String logPath = rootPath + "logs/" + app.getAppId() + "/" + channel.getChannelCode() + "/";
        channelConfig.put("logPath", logPath);
        // 渠道资源路径
        String channelSourcePath = String.format(channelTargetPath, channel.getChannelCode()) + config.getChannelVersion() + "/";
        channelConfig.put("channelSourcePath", channelSourcePath);

        int buildNum = config.getBuildNum() + 1;
        // 打包结果输出路径
        String resultPath = rootPath + "result/" + app.getAppId() + "/" + channel.getChannelCode() + "/" + buildNum + "/";
        channelConfig.put("resultPath", resultPath);
        // env参数
        SystemEnv systemEnv = systemEnvService.getById(config.getEnvCode());
        channelConfig.put("supersdkUrl", systemEnv != null ? systemEnv.getSupersdkUrl() : "");
        channelConfig.put("avalonsdkUrl", systemEnv != null ? systemEnv.getAvalonsdkUrl() : "");
        map.put("channel_config", channelConfig);

        // 插件参数
        String pluginsConfigStr = config.getPluginsConfig();
        JSONObject pluginsConfig = JSONObject.parseObject(pluginsConfigStr);
        if (pluginsConfig == null) {
            pluginsConfig = JSONObject.parseObject("{}");
        }
        List<Map<String, Object>> pluginsList = new ArrayList<>();
        // 获取record已有插件
        QueryWrapper<RecordPlugins> rpWp = new QueryWrapper<>();
        rpWp = rpWp.eq("record_id", config.getId());
        List<RecordPlugins> alreadyPluginsList = recordPluginsService.list(rpWp);
        for (RecordPlugins rp : alreadyPluginsList) {

            Map<String, Object> pluginMap = new HashMap<>();
            Plugins targetPlugin = pluginsService.getById(rp.getPluginsId());
            if (rp.getPluginsVersion() == null) {
                history.setPackStatus(3);
                history.setReason(targetPlugin.getName() + "未设置版本");
                throw new Exception(targetPlugin.getName() + "未设置版本");
            }
            pluginMap.put("ver", rp.getPluginsVersion());
            pluginMap.put("plugin_code", targetPlugin.getCode());
            pluginMap.put("config", pluginsConfig.get(targetPlugin.getCode()) == null ? "{}" : pluginsConfig.get(targetPlugin.getCode()));
            // 拉取插件资源
            packerRecordService.updateSource(
                    targetPlugin.getCode(),
                    rp.getPluginsVersion(),
                    2
            );
            pluginsList.add(pluginMap);
        }
        map.put("plugins_config", pluginsList);

        // 媒体标识
        QueryWrapper<RecordMedia> wrapper = new QueryWrapper<>();
        wrapper = wrapper.eq("record_id", config.getId());
        List<RecordMedia> recordMediaList = recordMediaService.list(wrapper);
        List<MediaFlag> list = new ArrayList<>();
        for (RecordMedia record : recordMediaList) {
            MediaFlag mediaFlag = mediaFlagService.getById(record.getMediaId());
            list.add(mediaFlag);
        }
        map.put("media_config", list);

        // JSON
        String json = JSON.toJSONString(map);
        // base64
        String baseString = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        // 调用python脚本
        String[] args1 = new String[]{pythonCommand, pythonFilePath, baseString};
        log.info("【base64】:" + baseString);
        new Thread(() -> {
            config.setBuildNum(buildNum);
            log.info("【当前打包】：" + config.getConfigName() + ",环境：" + systemEnv.getEnvDesc());
            try {
                // 拉取母包
                try {
                    if (isFtp == 1) {
                        getMotherFromFtp(appRootPath, motherPackage, ftpPath);
                    }
                } catch (Exception e) {
                    history.setReason("获取母包失败");
                    throw new Exception("获取母包失败");
                }
                Process proc = Runtime.getRuntime().exec(args1);
                BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                String err = null;
                try (BufferedReader stdError = new BufferedReader(
                        new InputStreamReader(proc.getErrorStream()))) {
                    while ((err = stdError.readLine()) != null) {
                        log.info("【{}-error】" + err, config.getConfigName());
                        if (err.length() > 200) {
                            err = err.substring(0, 200);
                        }
                        history.setReason(err);
                    }
                } catch (IOException e) {
                    log.error("读取进程错误日志输出时发生了异常" + e);
                }
                String line = null;
                while ((line = in.readLine()) != null) {
                    log.info("【{}】" + line, config.getConfigName());
                }
                proc.waitFor();
                int code = proc.exitValue();
                if (code == 0) { // 打包成功
//                    packerRecord.setCouldDownload(true);
                    config.setBuildNum(buildNum);
                    history.setPackStatus(2);
                } else {
                    log.info("exitValue================================================" + code);
//                    packerRecord.setCouldDownload(false);
                    history.setPackStatus(3);
                    throw new Exception(err);
                }
//                packerRecordService.updateById(config);
                in.close();
                // 需要赋值并传递到打包历史记录
                String supersdkVersion = "";
                // 需要返回每个媒体标识对应的包名 key: 媒体标识code，value: 成品包包名  默认包为code为：default
                Map<String, Map<String, String>> mediaPackagesMap = new HashMap<>();
                try {
                    String resultKeyName = Objects.equals(deviceType, "mac") ? "ipa_name" : "apk_name";

                    File logFile = new File(logPath + "result.json");
                    FileReader fileReader = new FileReader(logFile);
                    Reader reader = new InputStreamReader(new FileInputStream(logFile), "utf-8");
                    int ch = 0;
                    StringBuffer sb = new StringBuffer();
                    while ((ch = reader.read()) != -1) {
                        sb.append((char) ch);
                    }
                    String resultStr = sb.toString();
                    JSONObject result = JSON.parseObject(resultStr);
                    supersdkVersion = result.getString("supersdkVersion");
                    JSONArray resultList = result.getJSONArray("results");
                    if (resultList != null) {
                        for (int i = 0; i < resultList.size(); i++) {
                            JSONObject item = (JSONObject) resultList.get(i);
                            log.info("【serverInfo】:" + item.get("media_code") + "==" + item.get("apk_name"));
                            Map<String, String> itemMap = new HashMap<>();
                            itemMap.put("apk_name", (String) item.get(resultKeyName));
                            if (Objects.equals(deviceType, "mac")) {
                                itemMap.put("md5", (String) item.get("md5"));
                            }
                            mediaPackagesMap.put((String) item.get("media_code"), itemMap);
                        }
                    }
                    fileReader.close();
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    history.setPackStatus(3);
                    history.setReason("读取打包成果文件出错");
                    throw new Exception("读取打包成果文件出错");
                }
                // 保存成品包到FTP服务器上
                try {
                    log.info("【执行FTP回传】=========================isFTP=" + isFtp);
                    if(isFtp == 1){
                        UploadFullPackageTheadPool.getInstance().UploadFullPackageToFtp(mediaPackagesMap, resultPath,config.getFtpPath());
                    } else {
                        if (config.getFtpPath() == null) {
                            UploadFullPackageTheadPool.getInstance().UploadFullPackageToFtp(
                                    mediaPackagesMap,
                                    resultPath,
                                    app.getMotherFtpPaths().split(",")[0]
                            );
                        } else {
                            UploadFullPackageTheadPool.getInstance().UploadFullPackageToFtp(mediaPackagesMap, resultPath,config.getFtpPath());
                        }
                    }
                    UploadFullPackageTheadPool.getInstance().UploadFullPackageToFtp(mediaPackagesMap, resultPath,config.getFtpPath());
                } catch (Exception e) {
                    log.error("【回传ftp出错】：" + e.getMessage());
                    history.setPackStatus(3);
                    history.setReason("【回传ftp出错】：" + e.getMessage());
                    throw new Exception("【回传ftp出错】：" + e.getMessage());
//                    historyRecordService.updateById(history);
                }
//                String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                historyRecordService.saveHistoryRecord(
                        supersdkVersion,
                        config.getId(),
                        motherPackage,
                        mediaPackagesMap,
                        pluginsList,
                        config.getPackerName(),
                        resultPath,
                        logPath + "result.json",
                        ops,
                        systemEnv.getEnvDesc(),
                        buildNum,
                        historyId,
                        config.getPublicType() == null ? 1 : config.getPublicType()
                );
                history.setPackStatus(2);
                // 最后打包时间和最后一次打包人
                config.setLastOps(ops);
                String time = historyRecordService.getById(historyId).getCreateTime().toString();
                config.setLastPackTime(time);
                config.setLastHisId(historyId);
            } catch (Exception e) {
                e.printStackTrace();
                log.info("catch错误================================================" + e.getMessage());
                log.info(String.valueOf(e));
                if (e.getMessage() != null) {
                    history.setReason(e.getMessage());
                }
                history.setPackStatus(3);
//                packerRecord.setCouldDownload(false);
            } finally {
                packerRecordService.updateById(config);
                historyRecordService.updateById(history);
                // 根据stock长度，判断是否为同一渠道多配置排队打包
                stock.removeIf(next -> Objects.equals(next.getId(), config.getId()));
                int newSize = stock.size();
                if (newSize > 0) {
                    try {
                        packageThread(
                                stock,
                                appId,
                                isFtp,
                                ops,
                                motherPackage,
                                ftpPath,
                                versionCode
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}