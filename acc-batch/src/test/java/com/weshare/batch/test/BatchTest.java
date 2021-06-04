package com.weshare.batch.test;

import com.weshare.batch.controller.AsyncController;
import com.weshare.batch.feignClient.AdapterFeignClient;
import com.weshare.batch.feignClient.LoanFeignClient;
import com.weshare.service.api.entity.*;
import com.weshare.service.api.enums.ProjectEnum;
import com.weshare.service.api.enums.TransFlowTypeEnum;
import common.DateUtils;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractList;
import java.util.ArrayList;
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
    public void test0515LoanContract() throws Exception {
        LocalDate batchDate = LocalDate.parse("2020-05-15");
        String dateStr = batchDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        //原始放款明细
        List<LoanDetailReq> loanDetailReqs = List.of(
                new LoanDetailReq("YX-101", batchDate, new BigDecimal(1200), SnowFlake.getInstance().nextId() + "", 6, "6217 0028 7001 5622 705", "02", batchDate),
                new LoanDetailReq("YX-101", batchDate, new BigDecimal(1200), SnowFlake.getInstance().nextId() + "", 6, "6214 8312 7106 8212 236", "01", batchDate),
                new LoanDetailReq("YX-102", batchDate, new BigDecimal(1200), SnowFlake.getInstance().nextId() + "", 6, "6228 4800 5864 3078 676", "02", batchDate),
                new LoanDetailReq("YX-102", batchDate, new BigDecimal(1200), SnowFlake.getInstance().nextId() + "", 6, "6217 8576 0000 7092 823", "01", batchDate)
        );
        //原始还款计划
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
        //原始退票文件
        List<RefundTicketReq> refundTicketReqs = new ArrayList<>();
        //原始扣款明细文件
        List<RebackDetailReq> rebackDetailReqs = new ArrayList<>();
        //原始实还文件
        List<RepaymentDetailReq> repaymentDetailReqs = new ArrayList<>();

        loanDetailReqs = new ArrayList<>(loanDetailReqs);
        //loanDetailReqs.clear();
        repaymentPlanReqs = new ArrayList<>(repaymentPlanReqs);
        //repaymentPlanReqs.clear();
        refundTicketReqs = new ArrayList<>(refundTicketReqs);
        //refundTicketReqs.clear();
        rebackDetailReqs = new ArrayList<>(rebackDetailReqs);
        //rebackDetailReqs.clear();
        repaymentDetailReqs = new ArrayList<>(repaymentDetailReqs);
        //repaymentDetailReqs.clear();


        List<String> loanDetailReqList = loanDetailReqs.stream().map(e -> ReflectUtils.getFieldValues(e, "batchDate")).collect(Collectors.toList());
        loanDetailReqList.add(0, ReflectUtils.getFieldNames(LoanDetailReq.class, "batchDate"));

        List<String> repaymentPlanReqList = repaymentPlanReqs.stream().map(e -> ReflectUtils.getFieldValues(e, "batchDate")).collect(Collectors.toList());
        repaymentPlanReqList.add(0, ReflectUtils.getFieldNames(RepaymentPlanReq.class, "batchDate"));

        List<String> refundTicketReqList = refundTicketReqs.stream().map(e -> ReflectUtils.getFieldValues(e, "batchDate")).collect(Collectors.toList());
        refundTicketReqList.add(0, ReflectUtils.getFieldNames(RefundTicketReq.class, "batchDate"));

        List<String> rebackDetailReqList = rebackDetailReqs.stream().map(e -> ReflectUtils.getFieldValues(e, "batchDate")).collect(Collectors.toList());
        rebackDetailReqList.add(0, ReflectUtils.getFieldNames(RebackDetailReq.class, "batchDate"));

        List<String> repaymentDetailReqList = repaymentDetailReqs.stream().map(e -> ReflectUtils.getFieldValues(e, "batchDate")).collect(Collectors.toList());
        repaymentDetailReqList.add(0, ReflectUtils.getFieldNames(RepaymentDetailReq.class, "batchDate"));

        Path path = Paths.get("/yxms", dateStr, "create");
        if (Files.notExists(path)) {
            Files.createDirectories(path);
        }
        File file = new File(path.toUri());
        for (File listFile : Objects.requireNonNull(file.listFiles())) {
            listFile.delete();
        }

        Files.write(Paths.get(String.valueOf(path), "loan_detail_" + dateStr + ".csv"), loanDetailReqList);
        Files.write(Paths.get(String.valueOf(path), "repayment_plan_" + dateStr + ".csv"), repaymentPlanReqList);
        Files.write(Paths.get(String.valueOf(path), "refund_ticket_" + dateStr + ".csv"), refundTicketReqList);
        Files.write(Paths.get(String.valueOf(path), "reback_detail" + dateStr + ".csv"), rebackDetailReqList);
        Files.write(Paths.get(String.valueOf(path), "repayment_detail_" + dateStr + ".csv"), repaymentDetailReqList);

        adapterFeignClient.saveAllLoanDetail(loanDetailReqs);//保存adapter库的放款明细
        adapterFeignClient.saveAllLoanContractAndLoanTransFlowAndRepaySummary(loanDetailReqs, batchDate.toString());//保存loan库的放款明细和放款流水,repay库的用户主信息

        adapterFeignClient.saveAllRepaymentPlan(repaymentPlanReqs);//保存adapter库的还款计划
        adapterFeignClient.saveAllRepayPlanUpdateLoanContractAndRepaySummary(repaymentPlanReqs);//更新loan库的放款明细,repay库的用户主信息

        adapterFeignClient.saveAllRefundTicket(refundTicketReqs);//报存adapter库的退票文件
        adapterFeignClient.saveRefundDownRepayTransFlowAndReceiptDetail(refundTicketReqs, batchDate.toString());//保存实还更新还款计划(用户还款主信息,放款主信息,新增放款流水)

        adapterFeignClient.saveAllRebackDetal(rebackDetailReqs);//保存adapter库的reback_detail表（扣款明细表）
        adapterFeignClient.createAllRepayTransFlow(rebackDetailReqs, batchDate.toString());//保存repay库的repay_trans_flow表（还款流水表）

        adapterFeignClient.saveAllRepaymentDetail(repaymentDetailReqs);//保存adapter库的repayment_detail表（还款明细表）
        adapterFeignClient.createAllReceiptDetail(repaymentDetailReqs, batchDate.toString());//保存repay的库receipt_detail表(实还记录，更新还款计划和用户还款主信息表)

        loanFeignClient.UpdateRepaySummaryCurrentTerm(ProjectEnum.YXMS.getProjectNo(), batchDate.toString());//最后刷新repay_summary当前期数
    }

    @Test
    public void test0530RefundTicket() throws Exception {
        LocalDate batchDate = LocalDate.parse("2020-05-30");
        String dateStr = batchDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        //原始放款明细
        List<LoanDetailReq> loanDetailReqs = new ArrayList<>();
        //原始还款计划
        List<RepaymentPlanReq> repaymentPlanReqs=new ArrayList<>();
        //原始退票
        List<RefundTicketReq> refundTicketReqs = List.of(
                new RefundTicketReq("YX-101", new BigDecimal(1200), LocalDate.parse("2020-05-15"), "02", "6217 0028 7001 5622 705", LocalDateTime.now().withYear(batchDate.getYear()).withMonth(batchDate.getMonthValue()).withDayOfMonth(batchDate.getDayOfMonth()), batchDate),
                new RefundTicketReq("YX-101", new BigDecimal(1200), LocalDate.parse("2020-05-15"), "01", "6217 0028 7001 5622 705", LocalDateTime.now().withYear(batchDate.getYear()).withMonth(batchDate.getMonthValue()).withDayOfMonth(batchDate.getDayOfMonth()), batchDate)
        );
        //原始扣款明细
        List<RebackDetailReq> rebackDetailReqs=new ArrayList<>();
        //原始实还明细
        List<RepaymentDetailReq> repaymentDetailReqs=new ArrayList<>();

        loanDetailReqs = new ArrayList<>(loanDetailReqs);
        //loanDetailReqs.clear();
        repaymentPlanReqs = new ArrayList<>(repaymentPlanReqs);
        //repaymentPlanReqs.clear();
        refundTicketReqs = new ArrayList<>(refundTicketReqs);
        //refundTicketReqs.clear();
        rebackDetailReqs = new ArrayList<>(rebackDetailReqs);
        //rebackDetailReqs.clear();
        repaymentDetailReqs = new ArrayList<>(repaymentDetailReqs);
        //repaymentDetailReqs.clear();


        List<String> loanDetailReqList = loanDetailReqs.stream().map(e -> ReflectUtils.getFieldValues(e, "batchDate")).collect(Collectors.toList());
        loanDetailReqList.add(0, ReflectUtils.getFieldNames(LoanDetailReq.class, "batchDate"));

        List<String> repaymentPlanReqList = repaymentPlanReqs.stream().map(e -> ReflectUtils.getFieldValues(e, "batchDate")).collect(Collectors.toList());
        repaymentPlanReqList.add(0, ReflectUtils.getFieldNames(RepaymentPlanReq.class, "batchDate"));

        List<String> refundTicketReqList = refundTicketReqs.stream().map(e -> ReflectUtils.getFieldValues(e, "batchDate")).collect(Collectors.toList());
        refundTicketReqList.add(0, ReflectUtils.getFieldNames(RefundTicketReq.class, "batchDate"));

        List<String> rebackDetailReqList = rebackDetailReqs.stream().map(e -> ReflectUtils.getFieldValues(e, "batchDate")).collect(Collectors.toList());
        rebackDetailReqList.add(0, ReflectUtils.getFieldNames(RebackDetailReq.class, "batchDate"));

        List<String> repaymentDetailReqList = repaymentDetailReqs.stream().map(e -> ReflectUtils.getFieldValues(e, "batchDate")).collect(Collectors.toList());
        repaymentDetailReqList.add(0, ReflectUtils.getFieldNames(RepaymentDetailReq.class, "batchDate"));

        Path path = Paths.get("/yxms", dateStr, "create");
        if (Files.notExists(path)) {
            Files.createDirectories(path);
        }
        File file = new File(path.toUri());
        for (File listFile : Objects.requireNonNull(file.listFiles())) {
            listFile.delete();
        }

        Files.write(Paths.get(String.valueOf(path), "loan_detail_" + dateStr + ".csv"), loanDetailReqList);
        Files.write(Paths.get(String.valueOf(path), "repayment_plan_" + dateStr + ".csv"), repaymentPlanReqList);
        Files.write(Paths.get(String.valueOf(path), "refund_ticket_" + dateStr + ".csv"), refundTicketReqList);
        Files.write(Paths.get(String.valueOf(path), "reback_detail" + dateStr + ".csv"), rebackDetailReqList);
        Files.write(Paths.get(String.valueOf(path), "repayment_detail_" + dateStr + ".csv"), repaymentDetailReqList);

        adapterFeignClient.saveAllLoanDetail(loanDetailReqs);//保存adapter库的放款明细
        adapterFeignClient.saveAllLoanContractAndLoanTransFlowAndRepaySummary(loanDetailReqs, batchDate.toString());//保存loan库的放款明细和放款流水,repay库的用户主信息

        adapterFeignClient.saveAllRepaymentPlan(repaymentPlanReqs);//保存adapter库的还款计划
        adapterFeignClient.saveAllRepayPlanUpdateLoanContractAndRepaySummary(repaymentPlanReqs);//更新loan库的放款明细,repay库的用户主信息

        adapterFeignClient.saveAllRefundTicket(refundTicketReqs);//报存adapter库的退票文件
        adapterFeignClient.saveRefundDownRepayTransFlowAndReceiptDetail(refundTicketReqs, batchDate.toString());//保存实还更新还款计划(用户还款主信息,放款主信息,新增放款流水)

        adapterFeignClient.saveAllRebackDetal(rebackDetailReqs);//保存adapter库的reback_detail表（扣款明细表）
        adapterFeignClient.createAllRepayTransFlow(rebackDetailReqs, batchDate.toString());//保存repay库的repay_trans_flow表（还款流水表）

        adapterFeignClient.saveAllRepaymentDetail(repaymentDetailReqs);//保存adapter库的repayment_detail表（还款明细表）
        adapterFeignClient.createAllReceiptDetail(repaymentDetailReqs, batchDate.toString());//保存repay的库receipt_detail表(实还记录，更新还款计划和用户还款主信息表)

        loanFeignClient.UpdateRepaySummaryCurrentTerm(ProjectEnum.YXMS.getProjectNo(), batchDate.toString());//最后刷新repay_summary当前期数
    }

    @Test
    public void test0615Repayment() throws Exception {
        LocalDate batchDate = LocalDate.parse("2020-06-15");
        String dateStr = batchDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        //原始放款明细
        List<LoanDetailReq> loanDetailReqs = new ArrayList<>();
        //原始还款计划
        List<RepaymentPlanReq> repaymentPlanReqs=new ArrayList<>();
        //原始退票
        List<RefundTicketReq> refundTicketReqs = new ArrayList<>();
        //原始扣款明细
        List<RebackDetailReq> rebackDetailReqs = List.of(
                new RebackDetailReq("YX-102", 1, new BigDecimal(150), new BigDecimal(80), new BigDecimal(70), BigDecimal.ZERO, BigDecimal.ZERO, "02", TransFlowTypeEnum.正常还款, RebackDetailReq.FailReasonEnum.手机号错误, DateUtils.getLocalDateTime(batchDate), batchDate),
                new RebackDetailReq("YX-102", 1, new BigDecimal(150), new BigDecimal(80), new BigDecimal(70), BigDecimal.ZERO, BigDecimal.ZERO, "02", TransFlowTypeEnum.正常还款, RebackDetailReq.FailReasonEnum.身份证号错误, DateUtils.getLocalDateTime(batchDate), batchDate),
                new RebackDetailReq("YX-102", 1, new BigDecimal(150), new BigDecimal(80), new BigDecimal(70), BigDecimal.ZERO, BigDecimal.ZERO, "02", TransFlowTypeEnum.正常还款, RebackDetailReq.FailReasonEnum.银行卡号错误, DateUtils.getLocalDateTime(batchDate), batchDate),
                new RebackDetailReq("YX-102", 1, new BigDecimal(150), new BigDecimal(80), new BigDecimal(70), BigDecimal.ZERO, BigDecimal.ZERO, "01", TransFlowTypeEnum.正常还款, null, DateUtils.getLocalDateTime(batchDate), batchDate),
                new RebackDetailReq("YX-102", 1, new BigDecimal(150), new BigDecimal(90), new BigDecimal(60), BigDecimal.ZERO, BigDecimal.ZERO, "02", TransFlowTypeEnum.正常还款, RebackDetailReq.FailReasonEnum.手机号错误, DateUtils.getLocalDateTime(batchDate), batchDate),
                new RebackDetailReq("YX-102", 1, new BigDecimal(150), new BigDecimal(90), new BigDecimal(60), BigDecimal.ZERO, BigDecimal.ZERO, "02", TransFlowTypeEnum.正常还款, RebackDetailReq.FailReasonEnum.身份证号错误, DateUtils.getLocalDateTime(batchDate), batchDate),
                new RebackDetailReq("YX-102", 1, new BigDecimal(150), new BigDecimal(90), new BigDecimal(60), BigDecimal.ZERO, BigDecimal.ZERO, "02", TransFlowTypeEnum.正常还款, RebackDetailReq.FailReasonEnum.银行卡号错误, DateUtils.getLocalDateTime(batchDate), batchDate),
                new RebackDetailReq("YX-102", 1, new BigDecimal(150), new BigDecimal(90), new BigDecimal(60), BigDecimal.ZERO, BigDecimal.ZERO, "01", TransFlowTypeEnum.正常还款, null, DateUtils.getLocalDateTime(batchDate), batchDate)
        );
        //原始实还明细
        List<RepaymentDetailReq> repaymentDetailReqs=List.of(
                new RepaymentDetailReq("YX-102", "01", DateUtils.getLocalDateTime(batchDate), 1, new BigDecimal(150), new BigDecimal(80), new BigDecimal(70), BigDecimal.ZERO, BigDecimal.ZERO, batchDate),
                new RepaymentDetailReq("YX-102", "01", DateUtils.getLocalDateTime(batchDate), 1, new BigDecimal(150), new BigDecimal(90), new BigDecimal(60), BigDecimal.ZERO, BigDecimal.ZERO, batchDate)
        );

        loanDetailReqs = new ArrayList<>(loanDetailReqs);
        //loanDetailReqs.clear();
        repaymentPlanReqs = new ArrayList<>(repaymentPlanReqs);
        //repaymentPlanReqs.clear();
        refundTicketReqs = new ArrayList<>(refundTicketReqs);
        //refundTicketReqs.clear();
        rebackDetailReqs = new ArrayList<>(rebackDetailReqs);
        //rebackDetailReqs.clear();
        repaymentDetailReqs = new ArrayList<>(repaymentDetailReqs);
        //repaymentDetailReqs.clear();


        List<String> loanDetailReqList = loanDetailReqs.stream().map(e -> ReflectUtils.getFieldValues(e, "batchDate")).collect(Collectors.toList());
        loanDetailReqList.add(0, ReflectUtils.getFieldNames(LoanDetailReq.class, "batchDate"));

        List<String> repaymentPlanReqList = repaymentPlanReqs.stream().map(e -> ReflectUtils.getFieldValues(e, "batchDate")).collect(Collectors.toList());
        repaymentPlanReqList.add(0, ReflectUtils.getFieldNames(RepaymentPlanReq.class, "batchDate"));

        List<String> refundTicketReqList = refundTicketReqs.stream().map(e -> ReflectUtils.getFieldValues(e, "batchDate")).collect(Collectors.toList());
        refundTicketReqList.add(0, ReflectUtils.getFieldNames(RefundTicketReq.class, "batchDate"));

        List<String> rebackDetailReqList = rebackDetailReqs.stream().map(e -> ReflectUtils.getFieldValues(e, "batchDate")).collect(Collectors.toList());
        rebackDetailReqList.add(0, ReflectUtils.getFieldNames(RebackDetailReq.class, "batchDate"));

        List<String> repaymentDetailReqList = repaymentDetailReqs.stream().map(e -> ReflectUtils.getFieldValues(e, "batchDate")).collect(Collectors.toList());
        repaymentDetailReqList.add(0, ReflectUtils.getFieldNames(RepaymentDetailReq.class, "batchDate"));

        Path path = Paths.get("/yxms", dateStr, "create");
        if (Files.notExists(path)) {
            Files.createDirectories(path);
        }
        File file = new File(path.toUri());
        for (File listFile : Objects.requireNonNull(file.listFiles())) {
            listFile.delete();
        }

        Files.write(Paths.get(String.valueOf(path), "loan_detail_" + dateStr + ".csv"), loanDetailReqList);
        Files.write(Paths.get(String.valueOf(path), "repayment_plan_" + dateStr + ".csv"), repaymentPlanReqList);
        Files.write(Paths.get(String.valueOf(path), "refund_ticket_" + dateStr + ".csv"), refundTicketReqList);
        Files.write(Paths.get(String.valueOf(path), "reback_detail" + dateStr + ".csv"), rebackDetailReqList);
        Files.write(Paths.get(String.valueOf(path), "repayment_detail_" + dateStr + ".csv"), repaymentDetailReqList);

        adapterFeignClient.saveAllLoanDetail(loanDetailReqs);//保存adapter库的放款明细
        adapterFeignClient.saveAllLoanContractAndLoanTransFlowAndRepaySummary(loanDetailReqs, batchDate.toString());//保存loan库的放款明细和放款流水,repay库的用户主信息

        adapterFeignClient.saveAllRepaymentPlan(repaymentPlanReqs);//保存adapter库的还款计划
        adapterFeignClient.saveAllRepayPlanUpdateLoanContractAndRepaySummary(repaymentPlanReqs);//更新loan库的放款明细,repay库的用户主信息

        adapterFeignClient.saveAllRefundTicket(refundTicketReqs);//报存adapter库的退票文件
        adapterFeignClient.saveRefundDownRepayTransFlowAndReceiptDetail(refundTicketReqs, batchDate.toString());//保存实还更新还款计划(用户还款主信息,放款主信息,新增放款流水)

        adapterFeignClient.saveAllRebackDetal(rebackDetailReqs);//保存adapter库的reback_detail表（扣款明细表）
        adapterFeignClient.createAllRepayTransFlow(rebackDetailReqs, batchDate.toString());//保存repay库的repay_trans_flow表（还款流水表）

        adapterFeignClient.saveAllRepaymentDetail(repaymentDetailReqs);//保存adapter库的repayment_detail表（还款明细表）
        adapterFeignClient.createAllReceiptDetail(repaymentDetailReqs, batchDate.toString());//保存repay的库receipt_detail表(实还记录，更新还款计划和用户还款主信息表)

        loanFeignClient.UpdateRepaySummaryCurrentTerm(ProjectEnum.YXMS.getProjectNo(), batchDate.toString());//最后刷新repay_summary当前期数
    }
}
