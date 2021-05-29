package com.weshare.batch.test;

import com.weshare.batch.controller.AsyncController;
import com.weshare.batch.feignClient.AdapterFeignClient;
import com.weshare.batch.feignClient.LoanFeignClient;
import com.weshare.service.api.entity.LoanDetailReq;
import com.weshare.service.api.entity.RepaymentPlanReq;
import com.weshare.service.api.enums.ProjectEnum;
import common.ReflectUtils;
import common.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weahare.batch.test
 * @date: 2021-04-30 12:29:57
 * @describe:
 */

@SpringBootTest
@Slf4j
public class BatchTest {
    @Autowired
    @Qualifier("secondJdbcTemplate")
    //private JdbcTemplate secondJdbcTemplate;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private AsyncController asyncController;
    @Autowired
    private AdapterFeignClient adapterFeignClient;
    @Autowired
    private LoanFeignClient loanFeignClient;

    @Test
    public void test0() {
        String username = jdbcTemplate.queryForObject("select username from user limit 1", String.class);
        System.out.println("username:" + username);
    }

    @Test
    public void test() throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get("E:\\image", "盛重阳.pdf"));
        Files.write(Paths.get("E:\\ideaws\\acc\\acc-batch\\src\\test\\resources", "盛重阳.pdf"), bytes, StandardOpenOption.CREATE);
        Files.write(Paths.get("E:\\image\\pdf", "盛重阳.pdf"), bytes, StandardOpenOption.CREATE);
    }

    @Test
    public void test02() {
        asyncController.asyncController();
    }

    @Test
    public void test03() throws Exception {
        asyncController.asyncControllerValue();
    }

    @Test
    public void test04() throws Exception {
        asyncController.asyncControlleTest();
    }

    @Test
    public void test0515() throws Exception {
        LocalDate batchDate = LocalDate.parse("2020-05-15");
        String dateStr = batchDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        List<LoanDetailReq> loanDetailReqs = List.of(
                new LoanDetailReq("YX-101", batchDate, new BigDecimal(1200), SnowFlake.getInstance().nextId() + "", 6, "6217 0028 7001 5622 705", "02", batchDate),
                new LoanDetailReq("YX-101", batchDate, new BigDecimal(1200), SnowFlake.getInstance().nextId() + "", 6, "6214 8312 7106 8212 236", "01", batchDate),
                new LoanDetailReq("YX-102", batchDate, new BigDecimal(1200), SnowFlake.getInstance().nextId() + "", 6, "6228 4800 5864 3078 676", "02", batchDate),
                new LoanDetailReq("YX-102", batchDate, new BigDecimal(1200), SnowFlake.getInstance().nextId() + "", 6, "6217 8576 0000 7092 823", "01", batchDate)
        );
        List<RepaymentPlanReq> repaymentPlanReqs = List.of(
                new RepaymentPlanReq("YX-101", 1, batchDate.plusMonths(1), new BigDecimal(300), new BigDecimal(170), new BigDecimal(130), batchDate),
                new RepaymentPlanReq("YX-101", 2, batchDate.plusMonths(2), new BigDecimal(300), new BigDecimal(180), new BigDecimal(120), batchDate),
                new RepaymentPlanReq("YX-101", 3, batchDate.plusMonths(3), new BigDecimal(300), new BigDecimal(190), new BigDecimal(110), batchDate),
                new RepaymentPlanReq("YX-101", 4, batchDate.plusMonths(4), new BigDecimal(300), new BigDecimal(200), new BigDecimal(100), batchDate),
                new RepaymentPlanReq("YX-101", 5, batchDate.plusMonths(5), new BigDecimal(300), new BigDecimal(210), new BigDecimal(90), batchDate),
                new RepaymentPlanReq("YX-101", 6, batchDate.plusMonths(6), new BigDecimal(300), new BigDecimal(250), new BigDecimal(50), batchDate),

                new RepaymentPlanReq("YX-102", 1, batchDate.plusMonths(1), new BigDecimal(300), new BigDecimal(170), new BigDecimal(130), batchDate),
                new RepaymentPlanReq("YX-102", 2, batchDate.plusMonths(2), new BigDecimal(300), new BigDecimal(180), new BigDecimal(120), batchDate),
                new RepaymentPlanReq("YX-102", 3, batchDate.plusMonths(3), new BigDecimal(300), new BigDecimal(190), new BigDecimal(110), batchDate),
                new RepaymentPlanReq("YX-102", 4, batchDate.plusMonths(4), new BigDecimal(300), new BigDecimal(200), new BigDecimal(100), batchDate),
                new RepaymentPlanReq("YX-102", 5, batchDate.plusMonths(5), new BigDecimal(300), new BigDecimal(210), new BigDecimal(90), batchDate),
                new RepaymentPlanReq("YX-102", 6, batchDate.plusMonths(6), new BigDecimal(300), new BigDecimal(250), new BigDecimal(50), batchDate)
        );
        log.info("test0515()的方法的主线程名:{}", Thread.currentThread().getName());

        List<String> loanList = loanDetailReqs.stream().map(e -> ReflectUtils.getFieldValues(e, "batchDate")).collect(Collectors.toList());
        loanList.add(0, ReflectUtils.getFieldNames(LoanDetailReq.class, "batchDate"));

        List<String> repaymentList = repaymentPlanReqs.stream().map(e -> ReflectUtils.getFieldValues(e, "batchDate")).collect(Collectors.toList());
        repaymentList.add(0, ReflectUtils.getFieldNames(RepaymentPlanReq.class, "batchDate"));

        Path path = Paths.get("/yxms", dateStr, "create");
        if (Files.notExists(path)) {
            Files.createDirectories(path);
        }
        File file = new File(path.toUri());
        for (File listFile : Objects.requireNonNull(file.listFiles())) {
            listFile.delete();
        }
        Files.write(Paths.get(String.valueOf(path), "loan_detail_" + dateStr + ".csv"), loanList);
        Files.write(Paths.get(String.valueOf(path), "repayment_plan_" + dateStr + ".csv"), repaymentList);

        adapterFeignClient.saveAllLoanDetail(loanDetailReqs);//保存adapter库的放款明细
        adapterFeignClient.saveAllLoanContractAndLoanTransFlowAndRepaySummary(loanDetailReqs, batchDate.toString());//保存loan库的放款明细和放款流水,repay库的用户主信息

        adapterFeignClient.saveAllRepaymentPlan(repaymentPlanReqs);//保存adapter库的还款计划
        adapterFeignClient.saveAllRepayPlanUpdateLoanContractAndRepaySummary(repaymentPlanReqs);//更新loan库的放款明细,repay库的用户主信息
        loanFeignClient.UpdateRepaySummaryCurrentTerm(ProjectEnum.YXMS.getProjectNo(),batchDate.toString());//最后刷新repay_summary当前期数
    }
}
