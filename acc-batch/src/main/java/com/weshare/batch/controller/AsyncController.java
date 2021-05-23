package com.weshare.batch.controller;

import com.weshare.batch.entity.Person;
import com.weshare.batch.mapper.PersonMapper;
import com.weshare.batch.service.AsyncService;
import com.weshare.service.api.result.Result;
import common.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.controller
 * @date: 2021-05-20 18:45:38
 * @describe:
 */
@RestController
@Slf4j
public class AsyncController {
    @Autowired
    private AsyncService asyncService;
    @Autowired
    private PersonMapper personMapper;
    @Autowired
    private ApplicationContext applicationContext;

    @GetMapping("/asyncController")
    public String asyncController() {
        /**
         * 外部方法掉无返回值
         */

        asyncService.asyncServiceOne();
        asyncService.asyncServiceTwo();
        asyncControllerTest();
        applicationContext.getBean(AsyncController.class).asyncControllerTest();//内部调用要动态代理
        log.info("主方法asyncController...执行了...,当前主线程名:{}", Thread.currentThread().getName());
        return "success";
    }

    @Async
    public void asyncControllerTest() {
        try {
            long start = System.currentTimeMillis();
            Thread.sleep(new Random().nextInt(5 * 1000));
            log.info("异步方法asyncControllerTest...执行了...,当前线程名:{},当前耗时:{}毫秒", Thread.currentThread().getName(), System.currentTimeMillis() - start);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void asyncControllerTest_() {
        try {
            long start = System.currentTimeMillis();
            Thread.sleep(new Random().nextInt(5 * 1000));
            log.info("异步方法asyncControllerTest_...执行了...,当前线程名:{},当前耗时:{}毫秒", Thread.currentThread().getName(), System.currentTimeMillis() - start);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/asyncControllerValue")
    public void asyncControllerValue() throws Exception {
        long start = System.currentTimeMillis();
        Future<Result> future1 = asyncService.asyncServiceValueOne();
        Future<Result> future2 = asyncService.asyncServiceValueTwo();
        String msg1 = future1.get().getMsg();
        String msg2 = future2.get().getMsg();
        long end = System.currentTimeMillis();
        log.info("主方法asyncControllerValue...执行了...,当前主线程名:{},耗时:{} 毫秒,msg1:{},msg2:{}", Thread.currentThread().getName(), end - start, msg1, msg2);

    }

    @GetMapping("/asyncControlleTest")
    public void asyncControlleTest() throws Exception {
        long start = System.currentTimeMillis();
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            return asyncService.asyncServiceTestOne();
        }).exceptionally(ex -> {
            throw new RuntimeException("ex:" + ex.getMessage());
        });

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            return asyncService.asyncServiceTesTwo();
        }).exceptionally(ex -> {
            throw new RuntimeException("ex:" + ex.getMessage());
        });
        CompletableFuture future3 = CompletableFuture.runAsync(this::asyncControllerTest_).exceptionally(ex -> {
            throw new RuntimeException("ex:" + ex.getMessage());
        });

        String msg1 = future1.get();
        String msg2 = future2.get();
        future3.get();
        long end = System.currentTimeMillis();
        log.info("主方法asyncControlleTest...执行了...,当前主线程名:{},耗时:{} 毫秒,msg1:{},msg2:{}", Thread.currentThread().getName(), end - start, msg1, msg2);
    }

    @PostMapping("/addPerson")
    @Transactional
    public Result addPerson(@RequestBody Person person) {
        person.setCreateDate(LocalDateTime.now());
        log.info("addPerson:{}", JsonUtil.toJson(person, true));

        personMapper.addPerson(person.setId("controller"));

        asyncService.addPersonInner(person.setId("service"));

        int a = 5 / 0;

        return Result.result(true, "success");
    }

    @GetMapping("/selectAllPerson")
    public Result selectAllPerson() {
        List<Person> personList = asyncService.selectAllPerson();
        System.out.println(Person.Status.N.equals(personList.get(0).getStatus()));
        return Result.result(true, personList);
    }

}
