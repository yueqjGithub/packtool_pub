package com.avalon.packer.biz;

import java.io.IOException;
import java.util.Set;

public interface FtpService {

    /**
     * 获取目录下非重复的文件
     * @param remoteDir
     * @return
     */
    Set<String> getFileNames(String[] remoteDir) throws IOException;

    /**
     * 通过app 下载母包到打包工具服务器
     * @param remoteDir
     * @param fileName
     */
    boolean downLoadFile(String remoteDir,String fileName,String downPath);
}
