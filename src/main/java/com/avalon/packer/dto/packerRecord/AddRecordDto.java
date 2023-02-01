package com.avalon.packer.dto.packerRecord;

import com.avalon.packer.model.PackerRecord;
import com.avalon.packer.model.RecordPlugins;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class AddRecordDto extends PackerRecord {
    @NotBlank(message = "渠道不能为空")
    private String channelId;

    @NotBlank(message = "APP不能为空")
    private String appId;

    @NotBlank(message = "配置名称不能为空")
    @Size(message = "配置名称不能超过60字", min = 0, max = 60)
    private String configName;

    @NotBlank(message = "无法获取操作人")
    private String lastUpdateAs;
    // 是否为复制操作
    private Boolean isCopy;

    // 媒体标识列
    private String mediaList;

    // 插件列表
    private List<RecordPlugins> pluginList;
}
