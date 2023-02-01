package com.avalon.packer.service;

import com.avalon.packer.model.App;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xiaobin.wang
 * @since 2021-12-13
 */
public interface AppService extends IService<App> {
     void removeSource (String appId);

}
