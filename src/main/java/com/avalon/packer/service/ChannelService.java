package com.avalon.packer.service;

import com.avalon.packer.model.Channel;
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
public interface ChannelService extends IService<Channel> {
    /**
     * 删除渠道时，删除相关资源（渠道文件，渠道绑定配置）
     */
    boolean clearChannelById (String id) throws IOException;
}
