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
import jodd.io.ZipUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weahare.batch.test
 * @date: 2021-04-30 12:29:57
 * @describe:
 */

@SpringBootTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("模拟放款数据")
public class BatchTest {
    @Autowired
    //@Qualifier("secondJdbcTemplate")
    private JdbcTemplate secondJdbcTemplate;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private AsyncController asyncController;
    @Autowired
    private AdapterFeignClient adapterFeignClient;
    @Autowired
    private LoanFeignClient loanFeignClient;

    @Test
    public void test0() {
        String username = secondJdbcTemplate.queryForObject("select username from user limit 1", String.class);
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
    @Order(0)
    @DisplayName("执行清除脚本")
    public void testdelete() throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("delete.sql");
        //byte[] bytes = inputStream.readAllBytes();
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        inputStream.close();
        String string = new String(bytes);
        jdbcTemplate.batchUpdate(string.split(";"));
    }

    @Test
    @Order(5)
    @DisplayName("5月15日放款两笔数据")
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

        try {
            createFile(dateStr, loanDetailReqList, repaymentPlanReqList, refundTicketReqList, rebackDetailReqList, repaymentDetailReqList);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
    @Order(6)
    @DisplayName("5月30日借据号YX-101退票")
    public void test0530RefundTicket() throws Exception {
        LocalDate batchDate = LocalDate.parse("2020-05-30");
        String dateStr = batchDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        //原始放款明细
        List<LoanDetailReq> loanDetailReqs = new ArrayList<>();
        //原始还款计划
        List<RepaymentPlanReq> repaymentPlanReqs = new ArrayList<>();
        //原始退票
        List<RefundTicketReq> refundTicketReqs = List.of(
                new RefundTicketReq("YX-101", new BigDecimal(1200), LocalDate.parse("2020-05-15"), "02", "6217 0028 7001 5622 705", LocalDateTime.now().withYear(batchDate.getYear()).withMonth(batchDate.getMonthValue()).withDayOfMonth(batchDate.getDayOfMonth()), batchDate),
                new RefundTicketReq("YX-101", new BigDecimal(1200), LocalDate.parse("2020-05-15"), "01", "6217 0028 7001 5622 705", LocalDateTime.now().withYear(batchDate.getYear()).withMonth(batchDate.getMonthValue()).withDayOfMonth(batchDate.getDayOfMonth()), batchDate)
        );
        //原始扣款明细
        List<RebackDetailReq> rebackDetailReqs = new ArrayList<>();
        //原始实还明细
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

        try {
            createFile(dateStr, loanDetailReqList, repaymentPlanReqList, refundTicketReqList, rebackDetailReqList, repaymentDetailReqList);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
    @Order(7)
    @DisplayName("6月15日借据号YX-102第一期正常还款")
    public void test0615Repayment() throws Exception {
        LocalDate batchDate = LocalDate.parse("2020-06-15");
        String dateStr = batchDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        //原始放款明细
        List<LoanDetailReq> loanDetailReqs = new ArrayList<>();
        //原始还款计划
        List<RepaymentPlanReq> repaymentPlanReqs = new ArrayList<>();
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
        List<RepaymentDetailReq> repaymentDetailReqs = List.of(
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

        try {
            createFile(dateStr, loanDetailReqList, repaymentPlanReqList, refundTicketReqList, rebackDetailReqList, repaymentDetailReqList);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
    @Order(8)
    @DisplayName("7月20日借据号YX-102第二期逾期正常还款")
    public void test0720Repayment() throws Exception {
        LocalDate batchDate = LocalDate.parse("2020-07-20");
        String dateStr = batchDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        //原始放款明细
        List<LoanDetailReq> loanDetailReqs = new ArrayList<>();
        //原始还款计划
        List<RepaymentPlanReq> repaymentPlanReqs = new ArrayList<>();
        //原始退票
        List<RefundTicketReq> refundTicketReqs = new ArrayList<>();
        //原始扣款明细
        List<RebackDetailReq> rebackDetailReqs = List.of(
                new RebackDetailReq("YX-102", 2, new BigDecimal(180), new BigDecimal(80), new BigDecimal(100), BigDecimal.ZERO, BigDecimal.ZERO, "02", TransFlowTypeEnum.逾期还款, RebackDetailReq.FailReasonEnum.手机号错误, DateUtils.getLocalDateTime(batchDate), batchDate),
                new RebackDetailReq("YX-102", 2, new BigDecimal(180), new BigDecimal(80), new BigDecimal(100), BigDecimal.ZERO, BigDecimal.ZERO, "02", TransFlowTypeEnum.逾期还款, RebackDetailReq.FailReasonEnum.身份证号错误, DateUtils.getLocalDateTime(batchDate), batchDate),
                new RebackDetailReq("YX-102", 2, new BigDecimal(180), new BigDecimal(80), new BigDecimal(100), BigDecimal.ZERO, BigDecimal.ZERO, "02", TransFlowTypeEnum.逾期还款, RebackDetailReq.FailReasonEnum.银行卡号错误, DateUtils.getLocalDateTime(batchDate), batchDate),
                new RebackDetailReq("YX-102", 2, new BigDecimal(180), new BigDecimal(80), new BigDecimal(100), BigDecimal.ZERO, BigDecimal.ZERO, "01", TransFlowTypeEnum.逾期还款, null, DateUtils.getLocalDateTime(batchDate), batchDate),
                new RebackDetailReq("YX-102", 2, new BigDecimal(120), new BigDecimal(100), new BigDecimal(20), BigDecimal.ZERO, BigDecimal.ZERO, "02", TransFlowTypeEnum.逾期还款, RebackDetailReq.FailReasonEnum.手机号错误, DateUtils.getLocalDateTime(batchDate), batchDate),
                new RebackDetailReq("YX-102", 2, new BigDecimal(120), new BigDecimal(100), new BigDecimal(20), BigDecimal.ZERO, BigDecimal.ZERO, "02", TransFlowTypeEnum.逾期还款, RebackDetailReq.FailReasonEnum.身份证号错误, DateUtils.getLocalDateTime(batchDate), batchDate),
                new RebackDetailReq("YX-102", 2, new BigDecimal(120), new BigDecimal(100), new BigDecimal(20), BigDecimal.ZERO, BigDecimal.ZERO, "02", TransFlowTypeEnum.逾期还款, RebackDetailReq.FailReasonEnum.银行卡号错误, DateUtils.getLocalDateTime(batchDate), batchDate),
                new RebackDetailReq("YX-102", 2, new BigDecimal(120), new BigDecimal(100), new BigDecimal(20), BigDecimal.ZERO, BigDecimal.ZERO, "01", TransFlowTypeEnum.逾期还款, null, DateUtils.getLocalDateTime(batchDate), batchDate)
        );
        //原始实还明细
        List<RepaymentDetailReq> repaymentDetailReqs = List.of(
                new RepaymentDetailReq("YX-102", "03", DateUtils.getLocalDateTime(batchDate), 2, new BigDecimal(180), new BigDecimal(80), new BigDecimal(100), BigDecimal.ZERO, BigDecimal.ZERO, batchDate),
                new RepaymentDetailReq("YX-102", "03", DateUtils.getLocalDateTime(batchDate), 2, new BigDecimal(120), new BigDecimal(100), new BigDecimal(20), BigDecimal.ZERO, BigDecimal.ZERO, batchDate)
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

        try {
            createFile(dateStr, loanDetailReqList, repaymentPlanReqList, refundTicketReqList, rebackDetailReqList, repaymentDetailReqList);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
    @Order(9)
    @DisplayName("8月20日借据号YX-102第三期期逾期减免正常还款")
    public void test0820Repayment() throws Exception {
        LocalDate batchDate = LocalDate.parse("2020-08-20");
        String dateStr = batchDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        //原始放款明细
        List<LoanDetailReq> loanDetailReqs = new ArrayList<>();
        //原始还款计划
        List<RepaymentPlanReq> repaymentPlanReqs = new ArrayList<>();
        //原始退票
        List<RefundTicketReq> refundTicketReqs = new ArrayList<>();
        //原始扣款明细
        List<RebackDetailReq> rebackDetailReqs = List.of(
                new RebackDetailReq("YX-102", 3, new BigDecimal(215), new BigDecimal(150), new BigDecimal(30), new BigDecimal(20), new BigDecimal(15), "01", TransFlowTypeEnum.减免, null, DateUtils.getLocalDateTime(batchDate), batchDate),
                new RebackDetailReq("YX-102", 3, new BigDecimal(105), new BigDecimal(40), new BigDecimal(50), new BigDecimal(10), new BigDecimal(5), "01", TransFlowTypeEnum.减免, null, DateUtils.getLocalDateTime(batchDate), batchDate)
        );
        //原始实还明细
        List<RepaymentDetailReq> repaymentDetailReqs = List.of(
                new RepaymentDetailReq("YX-102", "04", DateUtils.getLocalDateTime(batchDate), 3, new BigDecimal(215), new BigDecimal(150), new BigDecimal(30), new BigDecimal(20), new BigDecimal(15), batchDate),
                new RepaymentDetailReq("YX-102", "04", DateUtils.getLocalDateTime(batchDate), 3, new BigDecimal(105), new BigDecimal(40), new BigDecimal(50), new BigDecimal(10), new BigDecimal(5), batchDate)
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

        try {
            createFile(dateStr, loanDetailReqList, repaymentPlanReqList, refundTicketReqList, rebackDetailReqList, repaymentDetailReqList);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
    @Order(10)
    @DisplayName("8月20日借据号YX-102第四.五.六期提前还款")
    public void test1015Repayment() throws Exception {
        LocalDate batchDate = LocalDate.parse("2020-10-15");
        String dateStr = batchDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        //原始放款明细
        List<LoanDetailReq> loanDetailReqs = new ArrayList<>();
        //原始还款计划
        List<RepaymentPlanReq> repaymentPlanReqs = new ArrayList<>();
        //原始退票
        List<RefundTicketReq> refundTicketReqs = new ArrayList<>();
        //原始扣款明细
        List<RebackDetailReq> rebackDetailReqs = List.of(
                new RebackDetailReq("YX-102", 4, new BigDecimal(880), new BigDecimal(660), new BigDecimal(150), new BigDecimal(40), new BigDecimal(30), "01", TransFlowTypeEnum.提前还款, null, DateUtils.getLocalDateTime(batchDate), batchDate)
        );
        //原始实还明细
        List<RepaymentDetailReq> repaymentDetailReqs = List.of(
                new RepaymentDetailReq("YX-102", "02", DateUtils.getLocalDateTime(batchDate), 4, new BigDecimal(880), new BigDecimal(660), new BigDecimal(150), new BigDecimal(40), new BigDecimal(30), batchDate)
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

        try {
            createFile(dateStr, loanDetailReqList, repaymentPlanReqList, refundTicketReqList, rebackDetailReqList, repaymentDetailReqList);
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    private void createFile(String dateStr, List<String> loanDetailReqList, List<String> repaymentPlanReqList, List<String> refundTicketReqList, List<String> rebackDetailReqList, List<String> repaymentDetailReqList) throws IOException {
        //写入
        Path writePath = Paths.get("/yxms", dateStr, "write");
        if (Files.notExists(writePath)) {
            Files.createDirectories(writePath);
        }
        for (File file : new File(writePath.toUri()).listFiles()) {
            file.delete();
        }
        Files.write(Paths.get(String.valueOf(writePath), "loan_detail_" + dateStr + ".csv"), loanDetailReqList);
        Files.write(Paths.get(String.valueOf(writePath), "repayment_plan_" + dateStr + ".csv"), repaymentPlanReqList);
        Files.write(Paths.get(String.valueOf(writePath), "refund_ticket_" + dateStr + ".csv"), refundTicketReqList);
        Files.write(Paths.get(String.valueOf(writePath), "reback_detail" + dateStr + ".csv"), rebackDetailReqList);
        Files.write(Paths.get(String.valueOf(writePath), "repayment_detail_" + dateStr + ".csv"), repaymentDetailReqList);
        //压缩
        Path zipPath = Paths.get("/yxms", dateStr, "zip");
        if (Files.notExists(zipPath)) {
            Files.createDirectories(zipPath);
        }
        for (File file : Objects.requireNonNull(new File(zipPath.toUri()).listFiles())) {
            file.delete();
        }
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(new File(String.valueOf(zipPath), "yxms.zip")));
        for (File file : Objects.requireNonNull(new File(writePath.toUri()).listFiles())) {
            //ZipUtil.addToZip(zipOutputStream, file, file.getName(), "zip", false);
            //ZipUtil.addToZip(zipOutputStream, Files.readAllBytes(Paths.get(file.getAbsolutePath())), file.getName(), "zip");
            FileInputStream fileInputStream = new FileInputStream(new File(file.getAbsolutePath()));
            byte[] bytes = new byte[fileInputStream.available()];
            fileInputStream.read(bytes);
            fileInputStream.close();
            ZipUtil.addToZip(zipOutputStream,bytes,file.getName(),"");
        }
        zipOutputStream.close();
        //解压
        Path unzipPath = Paths.get("/yxms", dateStr, "unzip");
        if (Files.notExists(unzipPath)) {
            Files.createDirectories(unzipPath);
        }
        for (File file : Objects.requireNonNull(new File(unzipPath.toUri()).listFiles())) {
            file.delete();
        }
        for (File file : Objects.requireNonNull(new File(zipPath.toUri()).listFiles())) {
            //ZipUtil.unzip(file, new File(unzipPath.toUri()));
            ZipUtil.unzip(file.getAbsoluteFile().toString(),unzipPath.toString());
        }
    }
}
