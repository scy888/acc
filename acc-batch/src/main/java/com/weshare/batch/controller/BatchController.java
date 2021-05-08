package com.weshare.batch.controller;

import com.alibaba.fastjson.JSON;
import common.FreemarkerUtil;
import common.JsonUtil;
import entity.Result;
import entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.controller
 * @date: 2021-05-01 17:20:44
 * @describe:
 */
@RestController
@Slf4j
public class BatchController {
    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${sendMessage.send-from}")
    private String sendFrom;
    @Value("${sendMessage.send-to}")
    private String[] sendTos;

    @GetMapping("/sendMsg/{message}")
    public String sendMsg(@PathVariable String message) throws MessagingException {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();//创建模拟量
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setText(message);//设置内容
        mimeMessageHelper.setSubject("【邮件主题】");//设置主题
        mimeMessageHelper.setFrom(sendFrom);//设置邮箱发送方
        mimeMessageHelper.setTo(sendTos);//设置邮件接收方
        javaMailSender.send(mimeMessage);//发送邮件
        return "success";
    }

    @GetMapping("/sendMessage")
    public String sendMessage() throws MessagingException {
        List<User> userList = List.of(new User("赵敏", new Date(2020 - 1900, 7 - 1, 6), 20, "女", "蒙古", "123", "123", BigDecimal.ONE, User.Status.F),
                new User("周芷若", new Date(2020 - 1900, 8 - 1, 9), 19, "女", "峨嵋", "123", "123", BigDecimal.ONE, User.Status.F),
                new User("殷离", new Date(2020 - 1900, 6 - 1, 5), 18, "女", "灵蛇岛", "123", "123", BigDecimal.ONE, User.Status.F),
                new User("小昭", new Date(2020 - 1900, 5 - 1, 5), 17, "女", "波斯", "123", "123", BigDecimal.ONE, User.Status.F));

        String templateContent = FreemarkerUtil.getTemplateContent("/ftl/userList.ftl");
        Map<String, Object> map = new HashMap<>();
        map.put("content", "【花心大萝卜张无忌身边的女人校验如下】:");//内容
        map.put("userList", userList);//内容
        String text = FreemarkerUtil.parse(templateContent, map);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setFrom(sendFrom);
        mimeMessageHelper.setTo(sendTos);
        mimeMessageHelper.setSubject("倚天屠龙记四美人物邮件校验模板");
        mimeMessageHelper.setText(text, true);

        javaMailSender.send(mimeMessage);

        return "success";
    }
}
