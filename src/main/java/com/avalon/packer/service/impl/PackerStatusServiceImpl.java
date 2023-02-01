package com.avalon.packer.service.impl;

import com.avalon.packer.model.PackerStatus;
import com.avalon.packer.mapper.PackerStatusMapper;
import com.avalon.packer.service.PackerStatusService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xiaobin.wang
 * @since 2021-12-27
 */
@Service
public class PackerStatusServiceImpl extends ServiceImpl<PackerStatusMapper, PackerStatus> implements PackerStatusService {
    @Override
    public void setRecordFail (String recordId) {
        QueryWrapper<PackerStatus> wrapper = new QueryWrapper<>();
        wrapper = wrapper.eq("record_id", recordId);
        PackerStatus packerStatus = getOne(wrapper);
        if (packerStatus != null) {
            packerStatus.setStatus(3);
            updateById(packerStatus);
        }
    }
}
