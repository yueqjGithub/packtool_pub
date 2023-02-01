package com.avalon.packer.mapper;

import com.avalon.packer.dto.packerRecord.ResultDto;
import com.avalon.packer.model.PackerRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xiaobin.wang
 * @since 2021-12-21
 */
@Repository
public interface PackerRecordMapper extends BaseMapper<PackerRecord> {
//    (SELECT GROUP_CONCAT(plugins_id) AS plugins_id FROM T_Record_Plugins WHERE record_id=T_Packer_Record.id) AS plugins_list,
    @Select("SELECT T_Packer_Record.*,(SELECT GROUP_CONCAT(media_id) AS media_id FROM T_Record_Media WHERE record_id=T_Packer_Record.id) AS media_list,(SELECT MAX(create_time) FROM T_History_Record WHERE config_id=T_Packer_Record.id) AS packer_time FROM T_Packer_Record LEFT JOIN T_Record_Plugins ON T_Packer_Record.id = T_Record_Plugins.record_id LEFT JOIN T_Record_Media ON T_Packer_Record.id = T_Record_Media.record_id LEFT JOIN T_History_Record ON T_Packer_Record.id = T_History_Record.config_id WHERE T_Packer_Record.app_id=#{appId} GROUP BY id")
    ArrayList<ResultDto> getFullList(@Param("appId") String appId);
}
