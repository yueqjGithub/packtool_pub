package com.avalon.packer.bo;

import lombok.Data;

import java.util.Map;

@Data
public class PyResultBo {
    private  String supersdkVersion;
    private Map<String,String> mediaPackageName;
}
