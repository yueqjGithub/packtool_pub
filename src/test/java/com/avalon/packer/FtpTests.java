package com.avalon.packer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.avalon.packer.biz.FtpService;
import com.avalon.packer.utils.SVNUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.util.*;

import com.avalon.packer.utils.FileUtils;
import org.springframework.web.multipart.MultipartFile;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {PackerApplication.class})
public class FtpTests {
    @Autowired
    private FtpService ftpService;

//    @Value("${ftp.downloadUrl}")
//    private String ftpDownloadUrl;

    @Test
    public void fileNamesTest() throws IOException {
        String[] dir = new String[]{"/cave2/cn/client/"};
        Set<String> fileNames = ftpService.getFileNames(dir);
        fileNames.forEach(item->{
            System.out.println(item);
        });
    }

//    @Test
//    public void downTest() throws IOException {
//        ftpService.downLoadFile("/cave2/cn/client/","Cave2Client_3.0.5_R207286_B19569_CN_CHANNEL.apk",ftpDownloadUrl);
//    }

    @Test
    public void uploadTest () throws IOException {
        File file = new File("F:/coding/data/packer_source/mother_package/DEV_TT/file.dat");
        FileInputStream inputStream = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(),
                "text/plain", inputStream);
        FileUtils.byteFileToFile(multipartFile, "F:/coding/data/packer_source/mother_package/DEV_TT/test.apk");
    }

    @Test
    public void svnPathTest () {
        List<String> list = SVNUtils.getSvnPathList(
                "https://svn.avalongames.com/svn/Avalon/AvalonDevHub/AvalonPacker/trunk/client/channelSource/avalon-global-appstore/",
                "avalon.dev.release.web",
                "AvalonWeb@1024"
        );
        System.out.println(list);
    }
}
