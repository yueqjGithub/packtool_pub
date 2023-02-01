package com.avalon.packer.biz.impl;

import com.avalon.packer.biz.FtpService;
import com.avalon.packer.exception.AvalonException;
import com.avalon.packer.ftp.ZzFtpClient;
import com.avalon.packer.ftp.ZzFtpClientFactory;
import com.avalon.packer.http.AvalonError;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.avalon.packer.http.AvalonError.FTP_DOWN_ERROR;

@Slf4j
@Service
public class FtpServiceImpl  implements FtpService {
    @Autowired
    private ZzFtpClient zzFtpClient;
    @Override
    public Set<String> getFileNames(String[] remoteDir) throws IOException {
        if(null == remoteDir || remoteDir.length<0){
            return null;
        }
        Set<String> fileNames = new HashSet<>();
        FTPClient ftpClientConnect = getFtpClientConnect();
        for (int i = 0; i < remoteDir.length; i++) {
            Set<String> dirFilenames = getDirFilenames(remoteDir[i],ftpClientConnect);
            System.out.println("--------- size:"+dirFilenames.size());
            fileNames.addAll(dirFilenames);
        }
        return fileNames;
    }

    @Override
    public boolean downLoadFile(String remoteDir, String fileName,String downPath) {
        boolean flag = true;
        try {
            FTPClient ftpClientConnect = getFtpClientConnect();
            ftpClientConnect.enterLocalPassiveMode();
            ftpClientConnect.changeWorkingDirectory(remoteDir);
            BufferedOutputStream buffOut = null;
            buffOut = new BufferedOutputStream(new FileOutputStream(downPath+fileName));
            flag = ftpClientConnect.retrieveFile(fileName, buffOut);
        } catch (IOException e) {
            log.error("{}",e);
            throw new AvalonException(FTP_DOWN_ERROR);
        }
        return flag;
    }

    private Set<String> getDirFilenames(String dir,FTPClient  ftpClient) throws IOException {
        ftpClient.changeWorkingDirectory(dir);
        ftpClient.enterLocalPassiveMode();
        String[] names = ftpClient.listNames(dir);
        if(names!=null &&names.length>0){
            return Arrays.stream(names).collect(Collectors.toSet());
        }
        return null;
    }

    private FTPClient getFtpClientConnect() throws IOException {
        FTPClient ftpClient = ZzFtpClientFactory.getFtpClient(zzFtpClient);
        if(null == ftpClient){
            throw new AvalonException(AvalonError.FTP_CONNECT_ERROR);
        }
        return ftpClient;
    }
}
