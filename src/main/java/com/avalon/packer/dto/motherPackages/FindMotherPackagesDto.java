package com.avalon.packer.dto.motherPackages;

import lombok.Data;

@Data
public class FindMotherPackagesDto {
    private String motherPackageName;
    private Integer limitCount;
}
