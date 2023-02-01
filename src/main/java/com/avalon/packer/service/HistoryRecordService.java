package com.avalon.packer.service;

import com.avalon.packer.dto.historyrecord.FindHistoryRecordDto;
import com.avalon.packer.dto.historyrecord.GetDetailsDto;
import com.avalon.packer.model.HistoryRecord;
import com.avalon.packer.vo.HistoryRecordsVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xiaobin.wang
 * @since 2022-01-06
 */
public interface HistoryRecordService extends IService<HistoryRecord> {

    default LambdaQueryWrapper<HistoryRecord> getWrapper(){
        LambdaQueryWrapper<HistoryRecord> wp = Wrappers.lambdaQuery();
        return wp;
    }

    /**
     * 保存打包历史记录
     * @param supersdkVersion
     * @param recordId
     * @param motherPackageName
     * @param mediaPackagesMap
     */
    void saveHistoryRecord(
            String supersdkVersion,
            String recordId,
            String motherPackageName,
            Map<String, Map<String, String>> mediaPackagesMap,
            List<Map<String, Object>> pluginsList,
            String packageName,
            String resultPath,
            String logFilePath,
            String opsUser,
            String envDesc,
            int buildNum,
            String hisId,
            int publicType
    );

    /**
     * 分页获取打包历史列表
     * @param dto
     * @return
     */
    Page<HistoryRecord> doPageHistoryRecords(FindHistoryRecordDto dto);

    /**
     * 通过历史记录ID获取，打包详情
     * @param id
     * @return
     */
    HistoryRecordsVo  getHistoryRecordDetail(String id);

    /**
     * 通过configId存储历史记录,在打包执行初始阶段调用,提供打包状态查询消费
     * @param
     * @return 历史记录id
     */
    String saveHistoryByConfig(
            String configId,
            String motherPackage,
            String channelId,
            String appId,
            String ops,
            int isFtp,
            int versionCode
    );

    /**
     * 通过历史ID集合查询详情集合
     * @param
     * @return
     */
    List<HistoryRecordsVo> getDetailList (String[] ids);

    /**
     * 上传至APPSTORE
     */
    void uploadIpaToStore (
            String id,
            String account,
            String pwd
    ) throws Exception;
}
