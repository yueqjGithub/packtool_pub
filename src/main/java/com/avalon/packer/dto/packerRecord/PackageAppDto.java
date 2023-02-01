package com.avalon.packer.dto.packerRecord;

import com.avalon.packer.model.App;
import lombok.Data;

@Data
public class PackageAppDto extends App {
    private String certFilePath;

    private String certFilePassword;

    private String provisionFilePath;

    private String xcodePath;
}
