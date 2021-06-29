package com.weshare.batch.service;

import com.weshare.batch.entity.DataCheck;
import com.weshare.batch.entity.DataCheckDetail;
import com.weshare.batch.repo.DataCheckDetailRepo;
import com.weshare.batch.repo.DataCheckRepo;
import com.weshare.service.api.client.RepayClient;
import com.weshare.service.api.enums.ProjectEnum;
import com.weshare.service.api.result.DataCheckResult;
import common.FreemarkerUtil;
import common.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.service
 * @date: 2021-06-28 12:31:20
 * @describe:
 */
@Service
@Slf4j
public class DataCheckService {
    @Autowired
    private RepayClient repayClient;
    @Autowired
    private DataCheckRepo dataCheckRepo;
    @Autowired
    private DataCheckDetailRepo dataCheckDetailRepo;
    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${sendMessage.send-from}")
    private String sendFrom;
    @Value("${sendMessage.send-to}")
    private String[] sendTos;
    @Value("${spring.profiles.active}")
    private String springActive;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void checkDataResult(String projectNo, String batchDate) {
        List<DataCheckResult> checkResults = repayClient.checkDataResult(projectNo).getData();

        for (DataCheckResult checkResult : checkResults) {
            //保存项目校验
            dataCheckRepo.deleteByProjectNoAndBatchDateAndCheckName(projectNo, LocalDate.parse(batchDate), checkResult.getName().name());
            dataCheckRepo.save(
                    new DataCheck()
                            .setId(SnowFlake.getInstance().nextId() + "")
                            .setCheckName(checkResult.getName().name())
                            .setCheckDesc(checkResult.getDesc())
                            .setCheckResult(checkResult.getIsPass())
                            .setProjectNo(projectNo)
                            .setBatchDate(LocalDate.parse(batchDate))
                            .setErrorCount(checkResult.getErrorCount())
                            .setRemark(checkResult.getRemark())
                            .setCreatedDate(LocalDateTime.now())
                            .setLastModifiedDate(LocalDateTime.now())
            );
            //保存校验明细
            if (!checkResult.getIsPass()) {
                dataCheckDetailRepo.deleteByProjectNoAndBatchDateAndCheckType(projectNo, LocalDate.parse(batchDate), checkResult.getName().name());
                for (String dueBillNo : checkResult.getDueBillNoList()) {
                    dataCheckDetailRepo.save(
                            new DataCheckDetail()
                                    .setId(SnowFlake.getInstance().nextId() + "")
                                    .setBatchDate(LocalDate.parse(batchDate))
                                    .setCheckType(checkResult.getName().name())
                                    .setDueBillNo(dueBillNo)
                                    .setDescription(checkResult.getDesc())
                                    .setProjectNo(projectNo)
                                    .setCreatedDate(LocalDateTime.now())
                                    .setLastModifiedDate(LocalDateTime.now())
                    );
                }
            }
        }
    }

    public int batchUpdate() throws URISyntaxException, IOException {
        List<String> readAllLines = Files.readAllLines(Paths.get(this.getClass().getClassLoader().getResource("update.sql").toURI()));
        String[] split = String.join(System.lineSeparator(), readAllLines).split(";");
        return jdbcTemplate.batchUpdate(split).length;
    }

    public void sendStartEmail(String batchDate) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        try {
            mimeMessageHelper.setSubject("【" + batchDate + "】" + "跑批情况如下:");
            mimeMessageHelper.setText("跑批正常,哈哈哈哈");
            mimeMessageHelper.setFrom(sendFrom);
            mimeMessageHelper.setTo(sendTos);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendCheckDataEmail(String batchDate, List<DataCheck> list) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String templateContent = FreemarkerUtil.getTemplateContent("/ftl/data-check-result.ftl");
        Map<String, Object> map = new HashMap<>();
        String checkResult = list.stream().mapToInt(DataCheck::getErrorCount).sum() < 1 ? "通过" : "不通过";
        checkResult = list.stream().map(DataCheck::getCheckResult).noneMatch(e -> e.equals(false)) ? "通过" : "不通过";
        map.put("content", "批量日期:" + batchDate + ",数据校验结果:" + checkResult + ",明细如下");
        map.put("dataCheckList", list);
        String outputContent = FreemarkerUtil.parse(templateContent, map);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        try {
            mimeMessageHelper.setFrom(sendFrom);
            mimeMessageHelper.setTo(sendTos);
            mimeMessageHelper.setSubject("【" + springActive + "】" + ProjectEnum.YXMS.getProjectName() + batchDate + "数据校验问题");
            mimeMessageHelper.setText(outputContent, true);
            javaMailSender.send(mimeMessage);
            stopWatch.stop();
            log.info("发送邮件耗时:{}毫秒",stopWatch.getTotalTimeMillis());
        } catch (MessagingException e) {
            e.printStackTrace();
            log.error("e", e);
        }
    }

    public List<DataCheck> getDataCheckList(String batchDate, String projectNo) {
        return dataCheckRepo.findByBatchDateAndProjectNo(LocalDate.parse(batchDate), projectNo);
    }
}
