package com.github.threadpool;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yatrikp@gmail.com    4/16/14      4:41 PM
 */
public class LocalThreadPoolExecutor {

    private static final LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(1);
    private static final LocalThreadPoolExecutor LOCAL_THREAD_POOL_EXECUTOR = new LocalThreadPoolExecutor();
    private static ThreadPoolExecutor threadPoolExecutor;

    private LocalThreadPoolExecutor(){
        init();
    }

    public static LocalThreadPoolExecutor getInstance(){
        return LOCAL_THREAD_POOL_EXECUTOR;
    }

    private void init(){
        //@TODO extract this variables to config properties values
        int corePoolSize = 3;
        int maxPoolSize = 3;
        long timeout = 1000000000;

        // create a new instance for ThreadPoolExecutor with a pre-determined thread pool size along a max limit
        // and a queue to hold the tasks for threads
        threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, timeout, TimeUnit.MILLISECONDS, queue);

        // set the ThreadFactory to ThreadPoolExecutor and assign new threads to it
        threadPoolExecutor.setThreadFactory(new ThreadFactory() {
            AtomicInteger intForThreadCountNo = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable runnable) {
                return new Thread(runnable, "Thread-" + intForThreadCountNo.getAndIncrement());
            }
        });

        // always try to set a rejection policy for the Thread Pool Executor to avoid undesired exception if a task if
        // rejected while adding to the Queue
        threadPoolExecutor.setRejectedExecutionHandler(new RejectedExecutionHandler() {

            @Override
            public void rejectedExecution(Runnable recordTask, ThreadPoolExecutor executor) {
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException exception){
                    System.out.println("Error while waiting for Task Queue to get empty");
                }
                threadPoolExecutor.execute(recordTask);
            }
        });
    }

}
