package com.avalon.packer.service.impl;

import com.avalon.packer.datarepo.SourceRepoItem;
import com.avalon.packer.datarepo.SourceUploadRepo;
import com.avalon.packer.dto.upload.SourceUploadDto;
import com.avalon.packer.dto.upload.UploadResDto;
import com.avalon.packer.exception.AvalonException;
import com.avalon.packer.http.AvalonError;
import com.avalon.packer.model.MotherPackages;
import com.avalon.packer.model.UploadFile;
import com.avalon.packer.mapper.UploadFileMapper;
import com.avalon.packer.service.MotherPackagesService;
import com.avalon.packer.service.UploadFileService;
import com.avalon.packer.utils.FileUtils;
import com.avalon.packer.utils.PathUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.util.*;

import static com.avalon.packer.http.AvalonError.FILE_DIR_ERROR;
import static com.avalon.packer.http.AvalonError.UPLOAD_ERROR;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xiaobin.wang
 * @since 2021-12-13
 */
@Slf4j
@Service
public class UploadFileServiceImpl extends ServiceImpl<UploadFileMapper, UploadFile> implements UploadFileService {

    /**
     *   win:
     *     path: ${file.win.path}
     *   unix:
     *     path: ${file.unix.path}
     * @param file
     * @return
     */
    @Value("${file.win.path}")
    private String winPath;

    @Value("${file.unix.path}")
    private String unixPath;

    @Value("${file.url}")
    private String url;

    @Value("${file.motherPath}")
    private String motherPath;

    @Resource
    private MotherPackagesService motherPackagesService;

    @Override
    public String createFileUpload(MultipartFile file, String savePath, String type, String fileName) throws IOException {
        if(file.isEmpty()){
            throw new AvalonException(UPLOAD_ERROR,"File is Empty");
        }
        // 资源总路径
        String rootPath = PathUtil.isWindows() ? winPath : unixPath;
        rootPath = rootPath.endsWith("/") ? rootPath : rootPath + "/";
        // 中间日期路径
//        String saveDir = PathUtil.generateRelativeSaveDir();
        // 文件名,母包上传时不要时间戳
        List<String> needTimeStampType = new ArrayList<>();
        needTimeStampType.add("1");
        needTimeStampType.add("2");
        String aliasName = !needTimeStampType.contains(type) ? file.getOriginalFilename() : System.currentTimeMillis() + file.getOriginalFilename();
        File dir = new File(rootPath + savePath);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new AvalonException(FILE_DIR_ERROR);
        }
        File saveFile = new File(rootPath + savePath + aliasName);
        FileCopyUtils.copy(file.getBytes(), saveFile);
        if (Objects.equals(type, "6")) {
            FileUtils.byteFileToFile(file, savePath + fileName);
            FileUtils.deleteDir(file.getOriginalFilename());
        }
        return url+savePath+aliasName;
    }

    @Override
    public UploadResDto UploadSourcePiece (
            SourceUploadDto dto,
            String appCode
    ) throws AvalonException, IOException {
        UploadResDto result = new UploadResDto();
        // 存放目录
        String rootPath = PathUtil.isWindows() ? winPath : unixPath;
        rootPath = rootPath.endsWith("/") ? rootPath : rootPath + "/";
        String path = rootPath + motherPath + appCode + "/";
        // 验证map种是否已有对应Md5
        SourceRepoItem target = SourceUploadRepo.getValue(dto.getMd5());
        if (target == null) { // map中没有对应md5key
            log.info("MD5MAP:target======null");
            // 查询是否有md5目录，有则说明上次未上传成功，且中途map被清理过
            File md5File = new File(path + dto.getMd5());
            dealFileWithOutKey(md5File, dto.getLength(), dto.getMd5(), dto.getFileName(), appCode);
            // 此时map中已经有了对应md5key
        }
        // 校验当前片是否上传过
        target = SourceUploadRepo.getValue(dto.getMd5());
        if (target.length == 0) { // 之前已上传成功
            log.info("MD5MAP:之前已上传成功");
            result.setSuccess(true);
            result.setComplete(true);
            result.setAlready(new HashSet<>());
            SourceUploadRepo.delSourceMapKey(dto.getMd5());
            return result;
        } else {
            boolean hasSave = target.getAlready().contains(Integer.parseInt(dto.getIdx()));
            if (!hasSave) {
                log.info("MD5MAP:该切片未上传");
                // 存放切片
                // 判断目录是否存在
                File dir = new File(path + dto.getMd5());
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                // 写入文件
                FileUtils.byteFileToFile(dto.getFile(), path + dto.getMd5() + "/" + dto.getIdx());
                // 录入Map
                Set<Integer> before = target.getAlready();
                before.add(Integer.parseInt(dto.getIdx()));
                target.setAlready(before);
                SourceUploadRepo.setSourceMap(dto.getMd5(), target);
            }
            result.setSuccess(true);
            result.setAlready(target.getAlready());
            result.setComplete(target.getLength() == target.getAlready().size());
        }
        // 上传完成操作
        if (target.length == target.getAlready().size()) {
            dealFileComplete(dto.getMd5(), dto.getFileName(), target.getLength(), appCode);
            SourceUploadRepo.delSourceMapKey(dto.getMd5());
            motherPackagesService.saveMotherPackageInfo(dto.getFileName(), appCode);
        }
        return result;
    }

    /**
     * 处理分片上传时，map中无法找到对应key的情况
     * @param file
     */
    public void dealFileWithOutKey (File file, String length, String md5, String fileName, String appCode) throws IOException {
        // 存放目录
        String rootPath = PathUtil.isWindows() ? winPath : unixPath;
        rootPath = rootPath.endsWith("/") ? rootPath : rootPath + "/";
        String path = rootPath + motherPath + appCode + "/";

        Set<Integer> set = new HashSet<>(); // already
        SourceRepoItem saveItem = new SourceRepoItem(); // md5: value<-
        if (file.exists()) {
            File[] fileList = file.listFiles();
            if (Objects.requireNonNull(fileList).length > 0) {
                for (File item : fileList) {
                    try {
                        Integer idx = Integer.parseInt(item.getName());
                        set.add(idx);
                    } catch (NumberFormatException e) {
                        log.error(file.getName() + "非碎片文件");
                    }
                }
            }

            saveItem.setLength(Integer.parseInt(length));
            saveItem.setAlready(set);
        } else {
            File alreadyFile = new File(path + fileName);
            if (alreadyFile.exists()) {
                String alreadyMd5 = DigestUtils.md5DigestAsHex(new FileInputStream(alreadyFile));
                if (alreadyMd5.equals(md5)) {
                    saveItem.setLength(0);
                } else {
                    saveItem.setLength(Integer.parseInt(length));
                }
            } else {
                saveItem.setLength(Integer.parseInt(length));
            }
            saveItem.setAlready(new HashSet<>());
        }
        SourceUploadRepo.setSourceMap(md5, saveItem);
    }

    /**
     * 上传完成组合文件
     */
    public void dealFileComplete (String md5, String fileName, Integer length, String appCode) throws IOException {
        // 存放目录
        String rootPath = PathUtil.isWindows() ? winPath : unixPath;
        rootPath = rootPath.endsWith("/") ? rootPath : rootPath + "/";
        String path = rootPath + motherPath + appCode + "/";

        File dir = new File(path + md5);
        if (dir.exists()) { // md5目录存在，才进行文件组合，如果不在，则说明本次服务启动期间，已经上传完成
            RandomAccessFile raf = null;
            raf = new RandomAccessFile(new File(path + fileName), "rw");
            try {
                for (int i = 0; i < length; i++) {
                    File file = new File(path + md5 + "/" + i);
                    if (!file.exists()) {
                        throw new AvalonException(UPLOAD_ERROR, i + "文件丢失");
                    }
                    RandomAccessFile reader = new RandomAccessFile(file, "r");
                    byte[] b = new byte[1024];

                    int n = 0;

                    while((n = reader.read(b)) != -1){

                        raf.write(b, 0, n);//一边读，一边写

                    }
                    reader.close();
                }
            } finally {
                try {
                    raf.close();
                    FileUtils.deleteDir(path + md5);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
