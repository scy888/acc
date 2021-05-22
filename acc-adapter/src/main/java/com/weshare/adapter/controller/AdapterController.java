package com.weshare.adapter.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.weshare.adapter.feignCilent.LoanFeignClient;
import com.weshare.service.api.entity.User;
import com.weshare.service.api.entity.UserBaseReq;
import com.weshare.service.api.result.Result;
import common.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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

        restTemplate = new RestTemplate();
        String url = "http://localhost:9003/loan/client/tesGetUrl?name=" + name + "&age=" + age;
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

        restTemplate = new RestTemplate();
        String url = "http://localhost:9003/loan/client/tesPostUrl";
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
        //String s="{\"name\":\"scy\",\"age\":25}";
        User user = new User(name, age);
        // MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("name",name);
//        map.put("age",age);
        Map<String, Object> map = Map.ofEntries(
                Map.entry("name", name),
                Map.entry("age", age)
        );
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(map, headers);
        Result result = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Result.class).getBody();
        System.out.println(result);
        return result;
    }
}
