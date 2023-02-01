package com.avalon.packer.threadpool.handler;

import com.avalon.packer.threadpool.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ClassName: CustomSmsRejectedExecutionHandler
 * @Author: wxb
 * @Description:
 * @Date: 2021/2/19 11:03
 */
@Slf4j
public class CustomSmsRejectedExecutionHandler implements RejectedExecutionHandler {
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        try {
            ThreadFactoryBuilder threadFactory = (ThreadFactoryBuilder) executor.getThreadFactory();
            //线程池溢出放弃任务，打印日志记录
            log.error(threadFactory.getNamePrefix() + "最大任务数超过最大线程数+队列容量...");
            log.info("CustomSmsRejectedExecutionHandler rejectedExecution 线程拒绝，进行等待 500ms");
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //executor.execute(r);
    }
}
