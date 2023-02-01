package com.avalon.packer.service;


import com.avalon.packer.model.ConfigVersion;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author quanjiang.yue
 * @since 2022-07-24
 */
public interface ConfigVersionService extends IService<ConfigVersion> {
    /**
     * 将当前配置转换成JSON，存放至版本表
     * @param configId 配置id
     */
    Boolean saveConfigVersion (String configId);
}
