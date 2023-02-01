package com.avalon.packer.threadpool;

import com.avalon.packer.threadpool.handler.CustomSmsRejectedExecutionHandler;
import com.avalon.sdk.common.utils.ZzFtpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * @author wangxb
 * 验证码发送线程池
 **/
@Slf4j
@Component
public class UploadFullPackageTheadPool {



    private static ThreadPoolExecutor pool;
    /**
     * volatile 避免指令重排
     */
    private static volatile UploadFullPackageTheadPool util;

    private static ZzFtpClient ftpClientStat;
    @Autowired
    private ZzFtpClient ftpClient;

    @PostConstruct
    public void initBean() {
        ftpClientStat = this.ftpClient;
    }

    /**
     * 私有构造方法
     */
    private UploadFullPackageTheadPool() {

        if (pool == null) {
            initExecutorPool();
        }
    }

    /**
     * 线程池工具类实例获取（单例懒汉双重锁）
     *
     * @return 工具类
     */
    public static UploadFullPackageTheadPool getInstance() {

        if (util == null) {
            util = initThreadPoolUtil();
        }
        return util;
    }

    /**
     * 初始化线程池工具类
     *
     * @return 线程池工具类实例
     */
    private static synchronized UploadFullPackageTheadPool initThreadPoolUtil() {

        if (util == null) {
            return new UploadFullPackageTheadPool();
        }
        return util;
    }

    /**
     * 邮件告警
     * 线程池初始化
     */
    private static synchronized void initExecutorPool() {
        if (pool == null) {
            ThreadPoolExecutor executor = new ThreadPoolExecutor(8,
                    8,
                    60L,
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(1000),
                    new ThreadFactoryBuilder("UploadFullPackageTheadPool"),
                    new CustomSmsRejectedExecutionHandler()
            );
            //没有任务时，空闲线程数可以小于核心线程数，使keepAliveTime对核心线程也生效
            executor.allowCoreThreadTimeOut(true);
            pool = executor;
        }
    }

    /**
     * 批量Callable处理
     *
     * @param callables 批量Callable处理线程
     * @param <T>       Callable实现类
     * @param <E>       响应结果类型
     * @return 响应结果集
     * @throws ExecutionException   线程处理异常
     * @throws InterruptedException 线程处理异常
     */
    public <T extends Callable<E>, E> List<E> executeCallableThread(List<T> callables) throws ExecutionException, InterruptedException {
        log.info("[{}]调用线程池处理", Thread.currentThread().getStackTrace()[2].getClassName());
        List<E> tasks = new ArrayList<>();
        List<Future> list = new ArrayList<>();
        for (T callable : callables) {
            Future<E> submit = pool.submit(callable);
            list.add(submit);
        }
        log.info("当前执行线程数:[{}]", pool.getActiveCount());
        for (Future future : list) {
            tasks.add((E) future.get());
        }
        return tasks;
    }

    public void UploadFullPackageToFtp(Map<String,Map<String, String>> fullPackageMap, String rootPath,String ftpPath){
        final String flagStr = "SUCCESS";
        CompletableFuture<String> future = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                try {
                    if(CollectionUtils.isEmpty(fullPackageMap)){
                           return flagStr;
                    }
                    for(Map.Entry<String,Map<String, String>> entry:fullPackageMap.entrySet()){
                        String value = entry.getValue().get("apk_name");
                        InputStream inputStream = new FileInputStream(rootPath+value);
                        ftpClientStat.uploadFile(ftpPath, value, inputStream);
                    }
                } catch (Exception e) {
                    log.error("{}", e);
                }
                return flagStr;
            }
        }, pool);
        future.thenAccept(e -> {
            log.info("Sms Send Result: {}", e);
        });
    }
}
