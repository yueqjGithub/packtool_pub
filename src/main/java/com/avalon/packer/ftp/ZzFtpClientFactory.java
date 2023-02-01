package com.avalon.packer.ftp;

import com.avalon.packer.exception.AvalonException;
import com.avalon.packer.http.AvalonError;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
@Slf4j
public class ZzFtpClientFactory {
    private static FTPClient ftpClient = null;

    
    public synchronized static FTPClient getFtpClient(ZzFtpClient zzFtpClient) throws IOException {
        if(null != ftpClient){
            return ftpClient;
        }
        ftpClient = new FTPClient();
        ftpClient.setConnectTimeout(zzFtpClient.getConnectTimeOut());
        ftpClient.setControlEncoding("UTF-8");
        ftpClient.enterLocalPassiveMode();
        ftpClient.connect(zzFtpClient.getUrl());
        ftpClient.login(zzFtpClient.getUsername(),zzFtpClient.getPassword());
        int replyCode = ftpClient.getReplyCode();
        if(!FTPReply.isPositiveCompletion(replyCode)){
            log.error("connect ftp is fail, url: {}",zzFtpClient.getUrl());
            throw new AvalonException(AvalonError.FTP_CONNECT_ERROR);
        }
        return ftpClient;
    }

    public static void closeConnect( FTPClient ftpClient){
        try {
            if(null != ftpClient){
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
