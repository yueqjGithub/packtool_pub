package com.avalon.packer.dto.upload;

import lombok.Data;

import java.util.Set;

@Data
public class UploadResDto {
    private boolean success; // 单片上传结果
    private Set<Integer> already; // 已上传分片的排序集合
    private boolean complete; // 是否全部上传完成
}
