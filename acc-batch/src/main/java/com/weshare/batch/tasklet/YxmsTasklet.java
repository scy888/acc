package com.weshare.batch.tasklet;

import com.weshare.batch.config.AppConfig;
import com.weshare.service.api.entity.*;
import com.weshare.service.api.enums.TransFlowTypeEnum;
import common.DateUtils;
import common.ReflectUtils;
import common.SnowFlake;
import jodd.io.ZipUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.tasklet
 * @date: 2021-06-15 10:24:46
 * @describe:
 */
@Component
@Slf4j
public class YxmsTasklet {

    @Autowired
    private AppConfig appConfig;

    public Tasklet createCsvTasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();
                String batchDate = (String) jobParameters.get("batchDate");
                String dateStr = LocalDate.parse(batchDate).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                Path path = Paths.get(appConfig.getCreate(), dateStr);
                if (Files.notExists(path)) {
                    Files.createDirectories(path);
                }
                for (File file : Objects.requireNonNull(new File(path.toUri()).listFiles())) {
                    file.delete();
                }
                createFile(batchDate, path);
                return RepeatStatus.FINISHED;
            }
        };
    }

    public Tasklet zipCsvTasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();
                String batchDate = (String) jobParameters.get("batchDate");
                String dateStr = LocalDate.parse(batchDate).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                Path path = Paths.get(appConfig.getZip(), dateStr);
                if (Files.notExists(path)) {
                    Files.createDirectories(path);
                }
                for (File file : Objects.requireNonNull(new File(path.toUri()).listFiles())) {
                    file.delete();
                }
                try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(new File(path.toString(), "yxms.zip")))) {
                    for (File file : new File(appConfig.getCreate(), dateStr).listFiles()) {
                        ZipUtil.addToZip(zipOutputStream, file, file.getName(), "zip", false);
                    }
                }
                return RepeatStatus.FINISHED;
            }
        };
    }

    public Tasklet unzipCsvTasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();
                String batchDate = (String) jobParameters.get("batchDate");
                String dateStr = LocalDate.parse(batchDate).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                Path path = Paths.get(appConfig.getUnzip(), dateStr);
                if (Files.notExists(path)) {
                    Files.createDirectories(path);
                }
                for (File file : Objects.requireNonNull(new File(path.toUri()).listFiles())) {
                    file.delete();
                }
                for (File file : new File(appConfig.getZip(), dateStr).listFiles()) {
                    ZipUtil.unzip(file, new File(path.toUri()));
                }
                return RepeatStatus.FINISHED;
            }
        };
    }

    private void createFile(String strDate, Path path) throws IOException {
        LocalDate batchDate = LocalDate.parse(strDate);
        String dateStr = batchDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        List<LoanDetailReq> loanDetailReqs = null;
        List<RepaymentPlanReq> repaymentPlanReqs = null;
        List<RefundTicketReq> refundTicketReqs = null;
        List<RebackDetailReq> rebackDetailReqs = null;
        List<RepaymentDetailReq> repaymentDetailReqs = null;
        switch (strDate) {
            case "2020-05-15":
                //原始放款明细
                loanDetailReqs = List.of(
                        new LoanDetailReq("YX-101", batchDate, new BigDecimal(1200), SnowFlake.getInstance().nextId() + "", 6, "6217 0028 7001 5622 705", "02", batchDate),
                        new LoanDetailReq("YX-101", batchDate, new BigDecimal(1200), SnowFlake.getInstance().nextId() + "", 6, "6214 8312 7106 8212 236", "01", batchDate),
                        new LoanDetailReq("YX-102", batchDate, new BigDecimal(1200), SnowFlake.getInstance().nextId() + "", 6, "6228 4800 5864 3078 676", "02", batchDate),
                        new LoanDetailReq("YX-102", batchDate, new BigDecimal(1200), SnowFlake.getInstance().nextId() + "", 6, "6217 8576 0000 7092 823", "01", batchDate)
                );
                //原始还款计划
                repaymentPlanReqs = List.of(
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
                refundTicketReqs = new ArrayList<>();
                //原始扣款明细文件
                rebackDetailReqs = new ArrayList<>();
                //原始实还文件
                repaymentDetailReqs = new ArrayList<>();
                createFile(path, dateStr, loanDetailReqs, repaymentPlanReqs, refundTicketReqs, rebackDetailReqs, repaymentDetailReqs);
                break;
            case "2020-05-30":
                //原始放款明细
                loanDetailReqs = new ArrayList<>();
                //原始还款计划
                repaymentPlanReqs = new ArrayList<>();
                //原始退票
                refundTicketReqs = List.of(
                        new RefundTicketReq("YX-101", new BigDecimal(1200), LocalDate.parse("2020-05-15"), "02", "6217 0028 7001 5622 705", LocalDateTime.now().withYear(batchDate.getYear()).withMonth(batchDate.getMonthValue()).withDayOfMonth(batchDate.getDayOfMonth()), batchDate),
                        new RefundTicketReq("YX-101", new BigDecimal(1200), LocalDate.parse("2020-05-15"), "01", "6217 0028 7001 5622 705", LocalDateTime.now().withYear(batchDate.getYear()).withMonth(batchDate.getMonthValue()).withDayOfMonth(batchDate.getDayOfMonth()), batchDate)
                );
                //原始扣款明细
                rebackDetailReqs = new ArrayList<>();
                //原始实还明细
                repaymentDetailReqs = new ArrayList<>();
                createFile(path, dateStr, loanDetailReqs, repaymentPlanReqs, refundTicketReqs, rebackDetailReqs, repaymentDetailReqs);
                break;
            case "2020-06-15":
                //原始放款明细
                loanDetailReqs = new ArrayList<>();
                //原始还款计划
                repaymentPlanReqs = new ArrayList<>();
                //原始退票
                refundTicketReqs = new ArrayList<>();
                //原始扣款明细
                rebackDetailReqs = List.of(
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
                repaymentDetailReqs = List.of(
                        new RepaymentDetailReq("YX-102", "01", DateUtils.getLocalDateTime(batchDate), 1, new BigDecimal(150), new BigDecimal(80), new BigDecimal(70), BigDecimal.ZERO, BigDecimal.ZERO, batchDate),
                        new RepaymentDetailReq("YX-102", "01", DateUtils.getLocalDateTime(batchDate), 1, new BigDecimal(150), new BigDecimal(90), new BigDecimal(60), BigDecimal.ZERO, BigDecimal.ZERO, batchDate)
                );
                createFile(path, dateStr, loanDetailReqs, repaymentPlanReqs, refundTicketReqs, rebackDetailReqs, repaymentDetailReqs);
                break;
            case "2020-07-20":
                //原始放款明细
                loanDetailReqs = new ArrayList<>();
                //原始还款计划
                repaymentPlanReqs = new ArrayList<>();
                //原始退票
                refundTicketReqs = new ArrayList<>();
                //原始扣款明细
                rebackDetailReqs = List.of(
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
                repaymentDetailReqs = List.of(
                        new RepaymentDetailReq("YX-102", "03", DateUtils.getLocalDateTime(batchDate), 2, new BigDecimal(180), new BigDecimal(80), new BigDecimal(100), BigDecimal.ZERO, BigDecimal.ZERO, batchDate),
                        new RepaymentDetailReq("YX-102", "03", DateUtils.getLocalDateTime(batchDate), 2, new BigDecimal(120), new BigDecimal(100), new BigDecimal(20), BigDecimal.ZERO, BigDecimal.ZERO, batchDate)
                );
                createFile(path, dateStr, loanDetailReqs, repaymentPlanReqs, refundTicketReqs, rebackDetailReqs, repaymentDetailReqs);
                break;
            case "2020-08-20":
                //原始放款明细
                loanDetailReqs = new ArrayList<>();
                //原始还款计划
                repaymentPlanReqs = new ArrayList<>();
                //原始退票
                refundTicketReqs = new ArrayList<>();
                //原始扣款明细
                rebackDetailReqs = List.of(
                        new RebackDetailReq("YX-102", 3, new BigDecimal(215), new BigDecimal(150), new BigDecimal(30), new BigDecimal(20), new BigDecimal(15), "01", TransFlowTypeEnum.减免, null, DateUtils.getLocalDateTime(batchDate), batchDate),
                        new RebackDetailReq("YX-102", 3, new BigDecimal(105), new BigDecimal(40), new BigDecimal(50), new BigDecimal(10), new BigDecimal(5), "01", TransFlowTypeEnum.减免, null, DateUtils.getLocalDateTime(batchDate), batchDate)
                );
                //原始实还明细
                repaymentDetailReqs = List.of(
                        new RepaymentDetailReq("YX-102", "04", DateUtils.getLocalDateTime(batchDate), 3, new BigDecimal(215), new BigDecimal(150), new BigDecimal(30), new BigDecimal(20), new BigDecimal(15), batchDate),
                        new RepaymentDetailReq("YX-102", "04", DateUtils.getLocalDateTime(batchDate), 3, new BigDecimal(105), new BigDecimal(40), new BigDecimal(50), new BigDecimal(10), new BigDecimal(5), batchDate)
                );
                createFile(path, dateStr, loanDetailReqs, repaymentPlanReqs, refundTicketReqs, rebackDetailReqs, repaymentDetailReqs);
                break;
            case "2020-10-15":
                //原始放款明细
                loanDetailReqs = new ArrayList<>();
                //原始还款计划
                repaymentPlanReqs = new ArrayList<>();
                //原始退票
                refundTicketReqs = new ArrayList<>();
                //原始扣款明细
                rebackDetailReqs = List.of(
                        new RebackDetailReq("YX-102", 4, new BigDecimal(880), new BigDecimal(660), new BigDecimal(150), new BigDecimal(40), new BigDecimal(30), "01", TransFlowTypeEnum.提前还款, null, DateUtils.getLocalDateTime(batchDate), batchDate)
                );
                //原始实还明细
                repaymentDetailReqs = List.of(
                        new RepaymentDetailReq("YX-102", "02", DateUtils.getLocalDateTime(batchDate), 4, new BigDecimal(880), new BigDecimal(660), new BigDecimal(150), new BigDecimal(40), new BigDecimal(30), batchDate)
                );
                createFile(path, dateStr, loanDetailReqs, repaymentPlanReqs, refundTicketReqs, rebackDetailReqs, repaymentDetailReqs);
                break;
            default:
                return;
        }
    }

    private void createFile(Path path, String dateStr, List<LoanDetailReq> loanDetailReqs, List<RepaymentPlanReq> repaymentPlanReqs, List<RefundTicketReq> refundTicketReqs, List<RebackDetailReq> rebackDetailReqs, List<RepaymentDetailReq> repaymentDetailReqs) throws IOException {

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

        Files.write(Paths.get(String.valueOf(path), "loan_detail_" + dateStr + ".csv"), loanDetailReqList, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        Files.write(Paths.get(String.valueOf(path), "repayment_plan_" + dateStr + ".csv"), repaymentPlanReqList, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        Files.write(Paths.get(String.valueOf(path), "refund_ticket_" + dateStr + ".csv"), refundTicketReqList, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        Files.write(Paths.get(String.valueOf(path), "reback_detail" + dateStr + ".csv"), rebackDetailReqList, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        Files.write(Paths.get(String.valueOf(path), "repayment_detail_" + dateStr + ".csv"), repaymentDetailReqList, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
    }
}
