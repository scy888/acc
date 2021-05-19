package com.weshare.adapter.controller;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.weshare.adapter.feignCilent.LoanFeignClient;
import com.weshare.service.api.entity.User;
import com.weshare.service.api.entity.UserBaseReq;
import com.weshare.service.api.result.Result;
import common.HttpClientUtil;
import common.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.adapter.controller
 * @date: 2021-05-16 18:37:47
 * @describe:
 */
@RestController
@Slf4j
public class AdapterController {

    @Autowired
    private LoanFeignClient loanFeignClient;
    //@Resource
    private RestTemplate restTemplate;

    @GetMapping("/saveUserBase")
    public Result saveUserBase() throws Exception {

        List<String> readAllLines = Files.readAllLines(Paths.get("/incomeApply", "userbase.json"));
        List<UserBaseReq> baseReqList = JsonUtil.fromJson(String.join(System.lineSeparator(), readAllLines), new TypeReference<List<UserBaseReq>>() {
        });
        return loanFeignClient.saveListUserBase(baseReqList);
    }

    @GetMapping("/tesGetUrl")
    Result tesGettUrl(@RequestParam String name, @RequestParam Integer age) {

        restTemplate=new RestTemplate();
       String url= "http://localhost:9003/loan/client/tesGetUrl?name=" + name + "&age=" + age;
       // return loanFeignClient.tesGettUrl(name, age);

//        String msg = HttpClientUtil.get(url);
//        System.out.println(msg);
//        return Result.result(true,msg);


//        Result result = restTemplate.getForObject(url, Result.class);
//        System.out.println(result);
//        return result;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        Result result = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class).getBody();
        System.out.println(result);
        return result;
    }

    @GetMapping("/tesPostUrl")
    Result tesPostUrl(@RequestParam String name, @RequestParam Integer age) {

        restTemplate=new RestTemplate();
        String url= "http://localhost:9003/loan/client/tesPostUrl";
        // return loanFeignClient.tesPostUrl(name, age);

//        User user=new User(name,age);
//        String msg = HttpClientUtil.post(url,JsonUtil.toJson(user));
//        System.out.println(JsonUtil.fromJson(msg,Result.class));
//        return Result.result(true,msg);


//        User user=new User(name,age);
//        Result result = restTemplate.postForObject(url, user, Result.class);
//        System.out.println(result);
//        return result;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        //String json="{\"name\",name,\"age\":age}";
        User user=new User(name,age);
        HttpEntity<Object> httpEntity = new HttpEntity<>(user,headers);
        Result result = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Result.class).getBody();
        System.out.println(result);
        return result;
    }
}
