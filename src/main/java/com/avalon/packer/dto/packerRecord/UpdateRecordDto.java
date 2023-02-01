package com.avalon.packer.dto.packerRecord;

import com.avalon.packer.model.PackerRecord;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UpdateRecordDto extends PackerRecord {
    @NotBlank(message = "渠道不能为空")
    private String channelId;
    @NotBlank(message = "APP不能为空")
    private String appId;
}
