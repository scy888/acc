package com.weshare.batch.tasklet;

import com.weshare.batch.config.AppConfig;
import com.weshare.batch.config.CsvBeanWrapperFieldSetMapper;
import com.weshare.batch.entity.Person;
import com.weshare.batch.feignClient.AdapterFeignClient;
import com.weshare.batch.feignClient.LoanFeignClient;
import com.weshare.batch.service.DataCheckService;
import com.weshare.service.api.entity.*;
import com.weshare.service.api.enums.TransFlowTypeEnum;
import common.DateUtils;
import common.ReflectUtils;
import common.SnowFlake;
import jodd.io.ZipUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private AdapterFeignClient adapterFeignClient;
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DataCheckService dataCheckService;

    public Tasklet clearAllTasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                String batchDate = (String) chunkContext.getStepContext().getJobParameters().get("batchDate");
                if ("2020-05-15".equals(batchDate)) {
                    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("delete.sql");
                    byte[] bytes = inputStream.readAllBytes();
                    String[] split = new String(bytes).split(";");
                    jdbcTemplate.batchUpdate(split);
                }
                return RepeatStatus.FINISHED;
            }
        };
    }

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

    public Tasklet dataCheckTasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();
                String projectNo = (String) jobParameters.get("projectNo");
                String batchDate = (String) jobParameters.get("batchDate");
                dataCheckService.checkDataResult(projectNo, batchDate);
                return RepeatStatus.FINISHED;
            }
        };
    }

    public Tasklet batchUpdate() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();
                String batchDate = (String) jobParameters.get("batchDate");
                if ("2020-10-15".equals(batchDate)) {
                    dataCheckService.batchUpdate();
                }
                return RepeatStatus.FINISHED;
            }
        };
    }

    @Bean
    @StepScope
    public FlatFileItemReader<LoanDetailReq> getLoanDetailRead(@Value("#{jobParameters[batchDate]}") String batchDate) {
        String dateStr = LocalDate.parse(batchDate).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setNames(ReflectUtils.getFieldNames(LoanDetailReq.class, "batchDate").split(","));
        lineTokenizer.setStrict(false);

        DefaultLineMapper<LoanDetailReq> detailReqDefaultLineMapper = new DefaultLineMapper<>();
        detailReqDefaultLineMapper.setLineTokenizer(lineTokenizer);
        detailReqDefaultLineMapper.setFieldSetMapper(new CsvBeanWrapperFieldSetMapper<>(LoanDetailReq.class));

        return new FlatFileItemReaderBuilder<LoanDetailReq>()
                .resource(new FileSystemResource(appConfig.getUnzip() + "/" + dateStr + "/loan_detail_" + dateStr + ".csv"))
                .name("放款明细.csv")
                .addComment("放款明细.csv")
                .linesToSkip(1)
                .lineMapper(detailReqDefaultLineMapper)
                .build();
    }

    @Bean
    @StepScope
    public ItemWriter<LoanDetailReq> getLoanDetailWrite(@Value("#{jobParameters[batchDate]}") String batchDate) {

        return new ItemWriter<LoanDetailReq>() {
            @Override
            public void write(List<? extends LoanDetailReq> items) throws Exception {
                items = items.stream().peek(e -> e.setBatchDate(LocalDate.parse(batchDate))).collect(Collectors.toList());
                log.info("getLoanDetailWrite:{}条", items.size());
                adapterFeignClient.saveAllLoanDetail(items);//保存adapter库的放款明细
                adapterFeignClient.saveAllLoanContractAndLoanTransFlowAndRepaySummary(items, batchDate.toString());//保存loan库的放款明细和放款流水,repay库的用户主信息
            }
        };
    }

    @Bean
    @StepScope
    public FlatFileItemReader<RepaymentPlanReq> getRepaymentPlanRead(@Value("#{jobParameters[batchDate]}") String batchDate) {
        String dateStr = LocalDate.parse(batchDate).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setNames(ReflectUtils.getFieldNames(RepaymentPlanReq.class, "batchDate").split(","));
        lineTokenizer.setStrict(false);

        DefaultLineMapper<RepaymentPlanReq> detailReqDefaultLineMapper = new DefaultLineMapper<>();
        detailReqDefaultLineMapper.setLineTokenizer(lineTokenizer);
        detailReqDefaultLineMapper.setFieldSetMapper(new CsvBeanWrapperFieldSetMapper<>(RepaymentPlanReq.class));

        return new FlatFileItemReaderBuilder<RepaymentPlanReq>()
                .resource(new FileSystemResource(appConfig.getUnzip() + "/" + dateStr + "/repayment_plan_" + dateStr + ".csv"))
                .name("还款计划.csv")
                .addComment("还款计划.csv")
                .linesToSkip(1)
                .lineMapper(detailReqDefaultLineMapper)
                .build();
    }

    @Bean
    @StepScope
    public ItemWriter<RepaymentPlanReq> getRepaymentPlanWrite(@Value("#{jobParameters[batchDate]}") String batchDate) {

        return new ItemWriter<RepaymentPlanReq>() {
            @Override
            public void write(List<? extends RepaymentPlanReq> items) throws Exception {
                items = items.stream().peek(e -> e.setBatchDate(LocalDate.parse(batchDate))).collect(Collectors.toList());
                log.info("getRepaymentPlanWrite:{}条", items.size());
                adapterFeignClient.saveAllRepaymentPlan(items);//保存adapter库的还款计划
                adapterFeignClient.saveAllRepayPlanUpdateLoanContractAndRepaySummary(items);//更新loan库的放款明细,repay库的用户主信息
            }
        };
    }

    @Bean
    @StepScope
    public FlatFileItemReader<RefundTicketReq> getRefundTicketRead(@Value("#{jobParameters[batchDate]}") String batchDate) {
        String dateStr = LocalDate.parse(batchDate).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setNames(ReflectUtils.getFieldNames(RefundTicketReq.class, "batchDate").split(","));
        lineTokenizer.setStrict(false);

        DefaultLineMapper<RefundTicketReq> detailReqDefaultLineMapper = new DefaultLineMapper<>();
        detailReqDefaultLineMapper.setLineTokenizer(lineTokenizer);
        detailReqDefaultLineMapper.setFieldSetMapper(new CsvBeanWrapperFieldSetMapper<>(RefundTicketReq.class));

        return new FlatFileItemReaderBuilder<RefundTicketReq>()
                .resource(new FileSystemResource(appConfig.getUnzip() + "/" + dateStr + "/refund_ticket_" + dateStr + ".csv"))
                .name("退票文件.csv")
                .addComment("退票文件.csv")
                .linesToSkip(1)
                .lineMapper(detailReqDefaultLineMapper)
                .build();
    }

    @Bean
    @StepScope
    public ItemWriter<RefundTicketReq> getRefundTicketWrite(@Value("#{jobParameters[batchDate]}") String batchDate) {

        return new ItemWriter<RefundTicketReq>() {
            @Override
            public void write(List<? extends RefundTicketReq> items) throws Exception {
                items = items.stream().peek(e -> e.setBatchDate(LocalDate.parse(batchDate))).collect(Collectors.toList());
                log.info("getRefundTicketWrite:{}条", items.size());
                adapterFeignClient.saveAllRefundTicket(items);//报存adapter库的退票文件
                adapterFeignClient.saveRefundDownRepayTransFlowAndReceiptDetail(items, batchDate.toString());//保存实还更新还款计划(用户还款主信息,放款主信息,新增放款流水)
            }
        };
    }

    @Bean
    @StepScope
    public FlatFileItemReader<RebackDetailReq> getRebackDetailRead(@Value("#{jobParameters[batchDate]}") String batchDate) {
        String dateStr = LocalDate.parse(batchDate).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setNames(ReflectUtils.getFieldNames(RebackDetailReq.class, "batchDate").split(","));
        lineTokenizer.setStrict(false);

        DefaultLineMapper<RebackDetailReq> detailReqDefaultLineMapper = new DefaultLineMapper<>();
        detailReqDefaultLineMapper.setLineTokenizer(lineTokenizer);
        detailReqDefaultLineMapper.setFieldSetMapper(new CsvBeanWrapperFieldSetMapper<>(RebackDetailReq.class));

        return new FlatFileItemReaderBuilder<RebackDetailReq>()
                .resource(new FileSystemResource(appConfig.getUnzip() + "/" + dateStr + "/reback_detail_" + dateStr + ".csv"))
                .name("扣款明细文件.csv")
                .addComment("扣款明细文件.csv")
                .linesToSkip(1)
                .lineMapper(detailReqDefaultLineMapper)
                .build();
    }

    @Bean
    @StepScope
    public ItemWriter<RebackDetailReq> getRebackDetailWrite(@Value("#{jobParameters[batchDate]}") String batchDate) {

        return new ItemWriter<RebackDetailReq>() {
            @Override
            public void write(List<? extends RebackDetailReq> items) throws Exception {
                items = items.stream().peek(e -> e.setBatchDate(LocalDate.parse(batchDate))).collect(Collectors.toList());
                log.info("getRebackDetailWrite:{}条", items.size());
                adapterFeignClient.saveAllRebackDetal(items);//保存adapter库的reback_detail表（扣款明细表）
                adapterFeignClient.createAllRepayTransFlow(items, batchDate.toString());//保存repay库的repay_trans_flow表（还款流水表）
            }
        };
    }

    @Bean
    @StepScope
    public FlatFileItemReader<RepaymentDetailReq> getRepaymentDetailRead(@Value("#{jobParameters[batchDate]}") String batchDate) {
        String dateStr = LocalDate.parse(batchDate).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setNames(ReflectUtils.getFieldNames(RepaymentDetailReq.class, "batchDate").split(","));
        lineTokenizer.setStrict(false);

        DefaultLineMapper<RepaymentDetailReq> detailReqDefaultLineMapper = new DefaultLineMapper<>();
        detailReqDefaultLineMapper.setLineTokenizer(lineTokenizer);
        detailReqDefaultLineMapper.setFieldSetMapper(new CsvBeanWrapperFieldSetMapper<>(RepaymentDetailReq.class));

        return new FlatFileItemReaderBuilder<RepaymentDetailReq>()
                .resource(new FileSystemResource(appConfig.getUnzip() + "/" + dateStr + "/repayment_detail_" + dateStr + ".csv"))
                .name("还款明细文件.csv")
                .addComment("还款明细文件.csv")
                .linesToSkip(1)
                .lineMapper(detailReqDefaultLineMapper)
                .build();
    }

    @Bean
    @StepScope
    public ItemWriter<RepaymentDetailReq> getRepaymentDetailWrite(@Value("#{jobParameters[batchDate]}") String batchDate) {

        return new ItemWriter<RepaymentDetailReq>() {
            @Override
            public void write(List<? extends RepaymentDetailReq> items) throws Exception {
                items = items.stream().peek(e -> e.setBatchDate(LocalDate.parse(batchDate))).collect(Collectors.toList());
                log.info("getRepaymentDetailWrite:{}条", items.size());
                adapterFeignClient.saveAllRepaymentDetail(items);//保存adapter库的repayment_detail表（还款明细表）
                adapterFeignClient.createAllReceiptDetail(items, batchDate.toString());//保存repay的库receipt_detail表(实还记录，更新还款计划和用户还款主信息表)
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
        Files.write(Paths.get(String.valueOf(path), "reback_detail_" + dateStr + ".csv"), rebackDetailReqList, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        Files.write(Paths.get(String.valueOf(path), "repayment_detail_" + dateStr + ".csv"), repaymentDetailReqList, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
    }
}
