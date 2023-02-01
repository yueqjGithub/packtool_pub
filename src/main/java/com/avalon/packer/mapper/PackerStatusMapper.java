package com.avalon.packer.mapper;

import com.avalon.packer.dto.packerStatus.StatusResultDto;
import com.avalon.packer.model.PackerStatus;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xiaobin.wang
 * @since 2021-12-27
 */
public interface PackerStatusMapper extends BaseMapper<PackerStatus> {
    @Select("SELECT T_Packer_Status.*,(SELECT could_download FROM T_Packer_Record WHERE id=T_Packer_Status.record_id) AS could_download,(SELECT MAX(create_time) FROM T_History_Record WHERE record_id=T_Packer_Status.record_id) AS packer_time FROM T_Packer_Status LEFT JOIN T_Packer_Record ON T_Packer_Record.id=T_Packer_Status.record_id LEFT JOIN T_History_Record ON T_History_Record.record_id=T_Packer_Status.record_id WHERE T_Packer_Status.app_id=#{appId}")
    ArrayList<StatusResultDto> getFullList(@Param("appId") String appId);
}
