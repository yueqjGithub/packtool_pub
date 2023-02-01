package com.avalon.packer.dto.packerRecord;

import com.avalon.packer.model.Channel;
import lombok.Data;

@Data
public class PackageChannelDto extends Channel {
    private String certFilePath;

    private String certFilePassword;

    private String provisionFilePath;

    private String xcodePath;
}
