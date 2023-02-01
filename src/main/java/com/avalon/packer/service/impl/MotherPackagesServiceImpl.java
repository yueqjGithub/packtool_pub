package com.avalon.packer.service.impl;

import com.avalon.packer.exception.AvalonException;
import com.avalon.packer.http.AvalonError;
import com.avalon.packer.model.MotherPackages;
import com.avalon.packer.mapper.MotherPackagesMapper;
import com.avalon.packer.service.MotherPackagesService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xiaobin.wang
 * @since 2022-01-11
 */
@Service
public class MotherPackagesServiceImpl extends ServiceImpl<MotherPackagesMapper, MotherPackages> implements MotherPackagesService {

    @Value("${sys.device}")
    private String deviceType;

    @Override
    public void saveMotherPackageInfo(String name, String appId) {
        if (Objects.equals(deviceType, "mac")) {
            if (name.endsWith("apk") || name.endsWith("aab")) {
                throw new AvalonException(AvalonError.PARAM_ERROR, "文件类型传错啦");
            }
        } else {
            if (!name.endsWith("apk") && !name.endsWith("aab")) {
                throw new AvalonException(AvalonError.PARAM_ERROR, "文件类型传错啦");
            }
        }
        LambdaQueryWrapper<MotherPackages> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MotherPackages::getAppId,appId).eq(MotherPackages::getPackageName,name);
        MotherPackages already = getOne(wrapper);
        MotherPackages motherPackages = new MotherPackages();
        motherPackages.setPackageName(name);
        motherPackages.setAppId(appId);
        if (Objects.equals(deviceType, "mac")) {
            motherPackages.setIsMac(true);
        } else {
            motherPackages.setIsMac(false);
        }
        if (already == null) {
            save(motherPackages);
        } else {
            updateById(motherPackages);
        }
    }
}
