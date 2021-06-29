package com.weshare.batch.service;

import com.weshare.batch.entity.DataCheck;
import com.weshare.batch.entity.DataCheckDetail;
import com.weshare.batch.repo.DataCheckDetailRepo;
import com.weshare.batch.repo.DataCheckRepo;
import com.weshare.service.api.client.RepayClient;
import com.weshare.service.api.result.DataCheckResult;
import common.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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
}
