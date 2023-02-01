package com.avalon.packer.controller;


import com.avalon.packer.dto.app.AddOrUpdateAppDto;
import com.avalon.packer.dto.app.FindAppDto;
import com.avalon.packer.http.AvalonHttpResp;
import com.avalon.packer.model.App;
import com.avalon.packer.model.MotherPackages;
import com.avalon.packer.model.PackerRecord;
import com.avalon.packer.service.AppService;
import com.avalon.packer.service.MotherPackagesService;
import com.avalon.packer.service.PackerRecordService;
import com.avalon.packer.utils.FileUtils;
import com.avalon.packer.utils.PathUtil;
import com.avalon.packer.utils.ZzBeanCopierUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *springboot启动后执行方法
 * @author xiaobin.wang
 * @since 2021-12-13
 */
@RestController
@RequestMapping("/admin/app")
@Api(tags = "App")
public class AppController {
    @Autowired
    AppService appService;

    @Autowired
    PackerRecordService packerRecordService;

    @Resource
    MotherPackagesService motherPackagesService;

    @Value("${file.win.path}")
    private String winPath;

    @Value("${file.unix.path}")
    private String unixPath;

    @Value("${file.signPath.app}")
    private String signPath;

    @Value("${sys.device}")
    private String deviceType;

    @GetMapping("")
    @ApiOperation(value = "app列表")
    public AvalonHttpResp<List<App>> getAppList (FindAppDto dto) {
        QueryWrapper<App> wrapper = new QueryWrapper<>(null);
        if (dto.getAppId() != null) {
            wrapper = wrapper.like("app_id", dto.getAppId());
        }
        if (dto.getAppName() != null) {
            wrapper = wrapper.like("app_name", dto.getAppName());
        }
        List<App> appList = appService.list(wrapper);
        if (dto.getAppLimit() != null && !Objects.equals(dto.getAppLimit(), "*")) {
            List<String> limits = Arrays.asList(dto.getAppLimit().split(","));
            List<App> results = new ArrayList<>();
            for (App item : appList) {
                if (limits.contains(item.getAppId())) {
                    results.add(item);
                }
            }
            return AvalonHttpResp.ok(results);
        }
        return AvalonHttpResp.ok(appList);
    }

    @PostMapping("")
    @ApiOperation(value = "添加、更新APP，unikey=id")
    public AvalonHttpResp<?> addOrUpdate (@RequestBody @Validated AddOrUpdateAppDto dto) {
        String id = dto.getId();
        String appId = dto.getAppId();
        QueryWrapper<App> wrapper = new QueryWrapper<>();
        wrapper = wrapper.eq("app_id", appId);
        App check = appService.getOne(wrapper);
        if (StringUtils.isEmpty(id)) {
            if (check != null) {
                return AvalonHttpResp.failed("APPID重复");
            }
            App copy = ZzBeanCopierUtils.copy(dto, new App());
            appService.save(copy);
            return AvalonHttpResp.ok();
        }
        App app = appService.getById(id);
        if(null == app){
            return AvalonHttpResp.failed("没有找到对应APP");
        }
        if (!Objects.equals(check.getId(), id)) {
            return AvalonHttpResp.failed("APPID已被占用");
        }
        App copy = ZzBeanCopierUtils.copy(dto, app);
        copy.setVersionCode(dto.getVersionCode());
        appService.updateById(copy);
        return AvalonHttpResp.ok();
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除APP，unikey=id")
    public AvalonHttpResp<?> deleteApp (@PathVariable String id) {
        if (id == null) {
            return AvalonHttpResp.failed("id不能为空");
        }
        App app = appService.getById(id);
        if (app == null) {
            return AvalonHttpResp.failed("没有找到对应APP");
        }
        QueryWrapper<PackerRecord> wrapper = new QueryWrapper<>();
        wrapper = wrapper.eq("app_id", id);
        packerRecordService.remove(wrapper);
        appService.removeById(id);
        // 删除APP对应资源文件 母包、打包结果、images
        appService.removeSource(app.getAppId());
        // 删除T_Mother_Package中对应数据
        LambdaQueryWrapper<MotherPackages> motherPackageWrapper = new LambdaQueryWrapper<>();
        motherPackageWrapper.eq(MotherPackages::getAppId,app.getAppId());
        motherPackagesService.remove(motherPackageWrapper);
        return AvalonHttpResp.ok();
    }

    @GetMapping("/signFile/{id}")
    @ApiOperation(value = "读取已有签名文件List")
    public AvalonHttpResp<Map<String, List<String>>> readSignFile (@PathVariable String id) {
        if (id == null) {
            return AvalonHttpResp.failed("id不能为空");
        }
        App app = appService.getById(id);
        if (app == null) {
            return AvalonHttpResp.failed("没有找到对应APP");
        }
        String appId = app.getAppId();
        String rootPath = PathUtil.isWindows() ? winPath : unixPath;
        String path = rootPath + String.format(signPath, appId);

        Map<String, List<String>> resultMap = new HashMap<>();

        if (Objects.equals(deviceType, "mac")) { // mac时加一段路径
            List<String> descList = FileUtils.readDir(path + "mac_desc/");
            resultMap.put("descList", descList);
            path += "mac_sign/";
        }
        List<String> signList = FileUtils.readDir(path);
        resultMap.put("signList", signList);
        return AvalonHttpResp.ok(resultMap);
    }
}
