package com.avalon.packer.dto.packerRecord;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class GetPluginIdsDto {
    @NotEmpty(message = "id不能为空")
    private List<String> ids;
}
