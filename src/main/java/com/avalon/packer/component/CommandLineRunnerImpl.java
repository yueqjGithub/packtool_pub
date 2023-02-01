package com.avalon.packer.component;

import com.avalon.packer.model.ChannelMediaPackage;
import com.avalon.packer.model.HistoryRecord;
import com.avalon.packer.service.ChannelMediaPackageService;
import com.avalon.packer.service.HistoryRecordService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 启动后执行
 */

@Component
@Slf4j
public class CommandLineRunnerImpl implements CommandLineRunner {
    @Resource
    HistoryRecordService historyRecordService;

    @Resource
    ChannelMediaPackageService channelMediaPackageService;

    @Override
    public void run(String... args) throws Exception {
        fixPackStatusOnServerStart();
        // TODO 启动时为之前的分包历史分配versioncode，下个版本去掉该方法调用，减少工程启动成本
        setHistoryVersionOnServerStart();
    }

    /**
     * 将停机前的打包中全部置为打包失败
     */
    public void fixPackStatusOnServerStart () {
        QueryWrapper<HistoryRecord> wp = new QueryWrapper<>();
        wp.eq("pack_status", 1);
        List<HistoryRecord> list = historyRecordService.list(wp);
        list.forEach(item -> {
            log.info("停服前未结束的打包进程：{}", item.getConfigId());
            item.setPackStatus(3);
            item.setReason("服务停机或意外宕机导致分包无法继续执行");
        });
        historyRecordService.saveOrUpdateBatch(list);
    }

    /**
     * 服务启动时，为之前的分包历史分配versioncode
     */
    public void setHistoryVersionOnServerStart () {
        System.out.println("给之前的历史记录分配versioncode开始，当前时间:" + new Date());
        QueryWrapper<HistoryRecord> wp = new QueryWrapper<>();
        wp.eq("pack_status", 2);
        wp.eq("version_code", -1);
        List<HistoryRecord> list = historyRecordService.list(wp);
        QueryWrapper<ChannelMediaPackage> lwp = new QueryWrapper<>();
        list.forEach(item -> {
            lwp.eq("hr_id", item.getId());
            List<ChannelMediaPackage> targetList = channelMediaPackageService.list(lwp);
            targetList.forEach(target -> {
                if (Objects.equals(target.getMediaName(), "10000") || Objects.equals(target.getMediaName(), "")) {
                    String str = target.getPackageName();
                    String reg = "(?<=PT)[0-9]+";
                    Pattern pattern = Pattern.compile(reg);
                    Matcher matcher = pattern.matcher(str);
                    matcher.find();
                    String version = matcher.group();
                    item.setVersionCode(Integer.parseInt(version));
                }
            });
            lwp.clear();
        });
        historyRecordService.saveOrUpdateBatch(list);
        System.out.println("给之前的历史记录分配versioncode结束，当前时间:" + new Date());
    }
}
