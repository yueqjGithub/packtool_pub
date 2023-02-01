package com.avalon.packer.service.impl;

import com.alibaba.fastjson.JSON;
import com.avalon.packer.model.ConfigVersion;
import com.avalon.packer.mapper.ConfigVersionMapper;
import com.avalon.packer.model.PackerRecord;
import com.avalon.packer.service.ConfigVersionService;
import com.avalon.packer.service.PackerRecordService;
import com.avalon.packer.utils.MD5Util;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author quanjiang.yue
 * @since 2022-07-24
 */
@Service
public class ConfigVersionServiceImpl extends ServiceImpl<ConfigVersionMapper, ConfigVersion> implements ConfigVersionService {
    @Resource
    private PackerRecordService packerRecordService;

    @Override
    public Boolean saveConfigVersion (String configId) {
        PackerRecord config = packerRecordService.getById(configId);
        if (config == null) {
            return false;
        }
        // 获得当前最大版本号
        LambdaQueryWrapper<ConfigVersion> wp = new LambdaQueryWrapper<>();
        wp.eq(ConfigVersion::getConfigId, configId).select(ConfigVersion::getVersion);
        List<ConfigVersion> vList = list(wp);
        int MaxVersion = -1;
        for (ConfigVersion item : vList) {
            if (item.getVersion() > MaxVersion) {
                MaxVersion = item.getVersion();
            }
        }
        String str = JSON.toJSONString(config);
        ConfigVersion vo = new ConfigVersion();
        vo.setConfig(str);
        vo.setConfigId(configId);
        vo.setVersion(MaxVersion + 1);

        return save(vo);
    }
}
