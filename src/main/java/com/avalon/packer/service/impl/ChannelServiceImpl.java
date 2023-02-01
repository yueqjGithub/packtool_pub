package com.avalon.packer.service.impl;

import com.avalon.packer.exception.AvalonException;
import com.avalon.packer.http.AvalonError;
import com.avalon.packer.http.AvalonHttpResp;
import com.avalon.packer.model.Channel;
import com.avalon.packer.mapper.ChannelMapper;
import com.avalon.packer.model.PackerRecord;
import com.avalon.packer.service.ChannelService;
import com.avalon.packer.service.PackerRecordService;
import com.avalon.packer.utils.FileUtils;
import com.avalon.packer.utils.PathUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xiaobin.wang
 * @since 2021-12-13
 */
@Service
public class ChannelServiceImpl extends ServiceImpl<ChannelMapper, Channel> implements ChannelService {
    @Value("${file.win.path}")
    private String winPath;

    @Value("${file.unix.path}")
    private String unixPath;

    @Value("${file.signPath.channel}")
    private String channelSignPath;

    @Resource
    private PackerRecordService packerRecordService;

    @Override
    public boolean clearChannelById (String id) throws IOException {
        String rootPath = PathUtil.isWindows() ? winPath : unixPath;

        Channel channel = getById(id);

        String path = rootPath + String.format(channelSignPath, channel.getChannelCode());
        // 清除渠道文件
        FileUtils.deleteDir(path);
        // 清除对应配置
        LambdaQueryWrapper<PackerRecord> wp = new LambdaQueryWrapper<>();
        wp.eq(PackerRecord::getChannelId, id);
        List<PackerRecord> configList = packerRecordService.list(wp);
        for (PackerRecord item : configList) {
            packerRecordService.delConfig(item.getId());
        }
        return removeById(id);
    }
}
