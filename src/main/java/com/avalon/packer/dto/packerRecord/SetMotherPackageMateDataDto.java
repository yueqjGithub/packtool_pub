package com.avalon.packer.dto.packerRecord;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: xb.wang
 * @create: 2022-04-06 17:03
 */
@Data
public class SetMotherPackageMateDataDto {
    private String id;
    private String sourceName;
    @ApiModelProperty(value = "0表示本地上传，1表示Ftp")
    private Integer motherIsFtp;

    private String ftpPath;
}
