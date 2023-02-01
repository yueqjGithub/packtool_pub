package com.avalon.packer.service;

import com.avalon.packer.dto.packerRecord.AddRecordDto;
import com.avalon.packer.model.PackerRecord;
import com.avalon.packer.model.RecordPlugins;
import com.avalon.packer.vo.PackageConfigVo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xiaobin.wang
 * @since 2021-12-21
 */
public interface PackerRecordService extends IService<PackerRecord> {

    /**
     * 更新或者拉取渠道资源
     * @param recordId
     * @return
     */
    PackageConfigVo  checkOutOrUpdateChannelSource(String recordId) throws InterruptedException, IOException;

    PackageConfigVo checkOutOrUpdateChannelSourceDeep (
            String recordId,
            String version
    ) throws InterruptedException, IOException;

    /**
     * 从ftp获取渠道、插件版本号
     * @param type
     * @param id
     * @return List
     */
    List<String> getVersionsFromFtp (
            String type,
            String id
    ) throws IOException;

    /**
     * 修改打包记录中appSignFile 信息
     * @param id
     * @param name
     */
    void updateRecordSignFileName(String id,String name);

    /**
     * 修改配置的描述文件
     */
    void updateConfigDescFile (String id, String name);

    /**
     * 检查打包记录是否配置完成
     * @param packerRecord
     */
    boolean checkRecordCompleted(PackerRecord packerRecord);

    /**
     *  添加打包配置
     * @param dto
     */
    void addPackerConfig (AddRecordDto dto);

    /**
     * 复制打包配置
     */
    void copyPackerConfig (AddRecordDto dto);

    /**
     * 上传其他文件
     */
    void setConfigOtherFile (String id, String name);

    /**
     * 删除配置
     */
    boolean delConfig (String id);

    /**
     * 根据配置id集合获取对应插件绑定关系
     * @param list
     */
    List<RecordPlugins> getPluginsByRecordIds (
            List<String> list
    );

    /**
     * 对比其他文件列，删除无用文件
     * @Param pre 之前的其余文件字符串
     * @Param cur 当前其余文件字符串
     * @Param isMac 平台
     * @Param channelCode 渠道code,拼接其余文件存放位置
     */
    void delNoUseOtherFile (String pre, String cur, Boolean isMac, String channelCode) throws IOException;

    /**
     * 更新渠道插件资源
     * @param fileName 资源名称，可理解渠道code或插件code
     * @param version 版本
     * @param sourceType 1-渠道 2-插件
     */
     void updateSource (
            String fileName,
            String version,
            int sourceType // 1-渠道，2-插件
    ) throws Exception;
}
