package com.avalon.packer.dto.packerRecord;

import com.avalon.packer.model.RecordPlugins;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class SetPluginsDto {
    @NotBlank(message = "打包记录不能为空")
    private String recordId;
    private List<RecordPlugins> pluginIds;
}
