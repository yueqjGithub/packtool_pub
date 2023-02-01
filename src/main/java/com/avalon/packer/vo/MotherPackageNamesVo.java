package com.avalon.packer.vo;

import com.avalon.sdk.common.utils.ZzFtpClient;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @description:
 * @author: xb.wang
 * @create: 2022-04-06 16:02
 */
@Data
public class MotherPackageNamesVo {
    private Set<ZzFtpClient.FtpFileAttr> ftpNames;
    private List<String> uploadNames;
}
