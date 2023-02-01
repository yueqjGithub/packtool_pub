package com.avalon.packer.service;

import com.avalon.packer.model.Plugins;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.IOException;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xiaobin.wang
 * @since 2021-12-13
 */
public interface PluginsService extends IService<Plugins> {
    boolean getPluginSourceFromSvn (String pluginCode) throws IOException;
}
