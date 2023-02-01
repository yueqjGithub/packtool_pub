package com.avalon.packer.service;

import com.avalon.packer.model.MotherPackages;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xiaobin.wang
 * @since 2022-01-11
 */
public interface MotherPackagesService extends IService<MotherPackages> {

    /**
     * 保存母包数据
     * @param name
     * @param appId
     */
    void saveMotherPackageInfo(String name,String appId);
}
