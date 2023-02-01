package com.avalon.packer.service;

import com.avalon.packer.model.PackerStatus;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xiaobin.wang
 * @since 2021-12-27
 */
public interface PackerStatusService extends IService<PackerStatus> {
    void setRecordFail(String recordId);
}
