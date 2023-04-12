package com.wjl.gulimall.search;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/4/11
 */
@Slf4j
public class ThreadTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        AtomicLong aLong = new AtomicLong(0);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                5,
                8,
                5,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10),
                r -> new Thread(r, "wjl-"+aLong.getAndAdd(1)),
                new ThreadPoolExecutor.AbortPolicy()
        );

        Future<String> future = executor.submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(2L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("处理完成");
            return "OK";
        });


        executor.submit(()->{
            log.info("处理任务2");
        });
        System.out.println(future.get());

        executor.shutdown();


    }
}
