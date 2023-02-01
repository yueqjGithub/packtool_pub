package com.avalon.packer.dto.packerRecord;

import com.avalon.packer.model.PackerRecord;
import lombok.Data;

@Data
public class ResultDto extends PackerRecord {
//    private String pluginsList;
    private String mediaList;
    private String packerTime;
}
