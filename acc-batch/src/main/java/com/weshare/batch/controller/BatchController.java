package com.weshare.batch.controller;

import com.weshare.batch.feignClient.RepayFeignClient;
import com.weshare.service.api.entity.PictureFileReq;
import common.FreemarkerUtil;
import common.SnowFlake;
import entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

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
    private JobRegistry jobRegistry;
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private RepayFeignClient repayFeignClient;
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

    @GetMapping("/startJob/{jobName}")
    public String startJob(@PathVariable String jobName,
                           @RequestParam String batchDate,
                           @RequestParam String projectNo,
                           @RequestParam String remark) throws Exception {

        Job job = jobRegistry.getJob(jobName);//job名
        JobParameters jobParameters = new JobParametersBuilder()//构建job参数
                .addString("batchDate", batchDate)
                .addString("projectNo", projectNo)
                .addString("remark", remark).toJobParameters();

        JobExecution execution = jobLauncher.run(job, jobParameters);
        return execution.toString();
    }


    @GetMapping("testBatch/{jobName}")
    public String testBatch(@PathVariable String jobName, @RequestParam String remark) throws Exception {
        Job job = jobRegistry.getJob(jobName);//注册job名
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addString("remark", remark);
        JobParameters jobParameters = jobParametersBuilder.toJobParameters();
        JobExecution jobExecution = jobLauncher.run(job, jobParameters);
        return jobExecution.toString();
    }

    @GetMapping("/personBatch/{jobName}")
    public String personBatch(@PathVariable String jobName,
                              @RequestParam String batchDate,
                              @RequestParam String remark,
                              @RequestParam String pathStr) throws Exception {
        Job job = jobRegistry.getJob(jobName);
        JobParametersBuilder parametersBuilder = new JobParametersBuilder()
                .addString("batchDate", batchDate)
                .addString("remark", remark)
                .addString("pathStr", pathStr);
        JobExecution jobExecution = jobLauncher.run(job, parametersBuilder.toJobParameters());
        return jobExecution.toString();
    }

    @PostMapping("/addPictureFile")
    public String addPictureFile(@RequestBody MultipartFile[] multipartFiles) throws IOException {
        log.info("要上传的文件个数:{}", multipartFiles.length);
        for (MultipartFile multipartFile : multipartFiles) {
            PictureFileReq pictureFileReq = new PictureFileReq();
            pictureFileReq.setDueBillNo(SnowFlake.getInstance().nextId() + "")
                    .setByteArray(multipartFile.getBytes())
                    .setFileName(multipartFile.getOriginalFilename())
                    .setCreateTime(LocalDateTime.now());
            repayFeignClient.addPictureFile(pictureFileReq);
        }
        return "success";
    }

    @GetMapping("/viewPictureFile/{id}")
    public String viewPictureFile(@PathVariable String id, HttpServletResponse response) throws IOException {
        PictureFileReq pictureFileReq = repayFeignClient.viewPictureFile(id).getData();
        String fileName = pictureFileReq.getFileName();
        log.info("fileName = " + fileName);
        byte[] byteArray = pictureFileReq.getByteArray();
        ServletOutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            outputStream.write(byteArray);
            response.setHeader("Content-disposition", "attachment; filename=" + fileName);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            outputStream.close();
        }
        return "success";
    }

    @Autowired
    ThreadPoolTaskScheduler threadPoolTaskScheduler;

    //@Scheduled(cron = "0/40 * * * * ?")
    public void test() throws InterruptedException {
        // threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        ScheduledFuture<?> scheduledFuture = threadPoolTaskScheduler.schedule(() -> System.out.println("当前时间：" + LocalDateTime.now() + "执行了..."), new CronTrigger("0/30 * * * * ?"));
        //scheduledFuture.cancel(true);
    }
}
