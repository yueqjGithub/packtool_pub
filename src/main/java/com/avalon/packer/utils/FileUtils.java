package com.avalon.packer.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class FileUtils {
    /**
     * 读取某个路径下的文件
     * @param path
     */
    public static List<String> readDir (String path) {
        File file = new File(path);
        File[] tempList = file.listFiles();
        List<String> nameList = new ArrayList<>(Collections.emptyList());
        if (tempList != null) {
            for (File fileItem : tempList) {
                nameList.add(fileItem.getName());
            }
        }
        return nameList;
    }
    /**
     * 删除某个路径下的文件
     * @param path
     */
    public static void deleteDir(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            log.info(path + "不存在");
            file.delete();
        } else {
            if (file.isFile()) {
                if (file.delete()) {
                    log.info("删除" + path + "成功");
                } else {
                    log.info("删除" + path + "失败");
                }
            } else {
                List<File> fileList = Arrays.stream(Objects.requireNonNull(file.listFiles())).collect(Collectors.toList());
                if (fileList.size() == 0) {
                    if (file.delete()) {
                        log.info("删除" + path + "成功");
                    } else {
                        log.info("删除" + path + "失败");
                    }
                } else {
                    for (File fileItem : fileList) {
                        deleteDir(fileItem.getPath());
                    }
                    deleteDir(path);
                }
            }
        }
    }

    /**
     * copy文件
     * @param filePath
     * @param targetPath
     */
    public static void CopyFile (String filePath, String targetPath) throws Exception {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new Exception("file is not exists");
        }

        File fileTo = new File(targetPath);
        if (!fileTo.exists()) {
            fileTo.createNewFile();
        }

        InputStream in = new FileInputStream(file);
        OutputStream out = new FileOutputStream(fileTo);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }
    }

    public static MultipartFile fileToMultipartFile (File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        return new MockMultipartFile(file.getName(), file.getName(),
                "text/plain", inputStream);
    }
    public static void byteFileToFile (MultipartFile file, String savePath) throws IOException {
        byte[] bytes = file.getBytes();
        FileOutputStream fos = new FileOutputStream(savePath);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        bos.write(bytes);
        bos.close();
        fos.close();
    }
}
