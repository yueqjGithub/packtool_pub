package com.avalon.packer.threadpool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wangxb
 */
public class ThreadFactoryBuilder implements ThreadFactory {

    /**
     * poolNumber是static的原子变量用来记录当前线程池的编号是应用级别的，所有线程池公用一个，比如创建第一个线程池时候线程池编号为1，
     * 创建第二个线程池时候线程池的编号为2，这里pool-1-thread-1里面的pool-1中的1就是这个值
     */
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    /**
     * 创建线程的线程组
     */
    private final ThreadGroup group;
    /**
     * threadNumber是线程池级别的，每个线程池有一个该变量用来记录该线程池中线程的编号，
     * 这里pool-1-thread-1里面的thread-1中的1就是这个值
     */
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    /**
     * namePrefix是线程池中线程的前缀，默认固定为pool
     */
    private final String namePrefix;

    public ThreadFactoryBuilder(String name) {

        SecurityManager s = System.getSecurityManager();
        //安全管理员的组或者当前线程的组赋值给组
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        if (null == name || name.isEmpty()) {
            name = "pool";
        }

        namePrefix = name + "-" + poolNumber.getAndIncrement() + "-thread-";
    }

    public String getNamePrefix() {
        return namePrefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
        if (t.isDaemon()){
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY){
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }
}
