package com.weshare.batch.service;

import com.weshare.batch.entity.Person;
import com.weshare.batch.mapper.PersonMapper;
import com.weshare.service.api.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.service
 * @date: 2021-05-20 18:46:15
 * @describe:
 */
@Service
@Slf4j
public class AsyncService {
    @Autowired
    private PersonMapper personMapper;

    @Async
    public void asyncServiceOne() {
        try {
            long start = System.currentTimeMillis();
            Thread.sleep(new Random().nextInt(5 * 1000));
            log.info("异步方法asyncServiceOne...执行了...,当前线程名:{},当前耗时:{}毫秒", Thread.currentThread().getName(), System.currentTimeMillis() - start);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Async
    public void asyncServiceTwo() {
        try {
            long start = System.currentTimeMillis();
            Thread.sleep(new Random().nextInt(5 * 1000));
            log.info("异步方法asyncServiceTwo...执行了...,当前线程名:{},当前耗时:{}毫秒", Thread.currentThread().getName(), System.currentTimeMillis() - start);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Async
    public Future<Result> asyncServiceValueOne() {
        long start = System.currentTimeMillis();

        try {
            //int a = 5 / 0;
            TimeUnit.MILLISECONDS.sleep(new Random().nextInt(5 * 1000));
            log.info("异步方法asyncServiceValueOne...执行了...,当前线程名:{},当前耗时:{}毫秒", Thread.currentThread().getName(), System.currentTimeMillis() - start);
            //int a = 5 / 0;
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("e:" + e.getMessage());
        }
        Future<Result> future = new AsyncResult<>(Result.result(true, "successOne"));
        return future;
    }

    @Async
    public Future<Result> asyncServiceValueTwo() {
        long start = System.currentTimeMillis();
        try {
            TimeUnit.MILLISECONDS.sleep(new Random().nextInt(5 * 1000));
            log.info("异步方法asyncServiceValueTwo...执行了...,当前线程名:{},当前耗时:{}毫秒", Thread.currentThread().getName(), System.currentTimeMillis() - start);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("e:" + e.getMessage());
        }
        Future<Result> future = new AsyncResult<>(Result.result(true, "successTwo"));
        return future;
    }

    public String asyncServiceTestOne() {
        long start = System.currentTimeMillis();
        try {
            TimeUnit.MILLISECONDS.sleep(new Random().nextInt(5 * 1000));
            log.info("asyncServiceTestOne...执行了...,当前线程名:{},当前耗时:{}毫秒", Thread.currentThread().getName(), System.currentTimeMillis() - start);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("e:" + e.getMessage());
        }
        return "successOne";
    }

    public String asyncServiceTesTwo() {
        long start = System.currentTimeMillis();
        try {
            TimeUnit.MILLISECONDS.sleep(new Random().nextInt(5 * 1000));
            log.info("异步方法asyncServiceTesTwo...执行了...,当前线程名:{},当前耗时:{}毫秒", Thread.currentThread().getName(), System.currentTimeMillis() - start);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("e:" + e.getMessage());
        }
        return "successTwo";
    }

    public void addPerson(Person person) {
        personMapper.addPerson(person);
    }
}
