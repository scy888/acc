package com.weshare.batch.tasklet;

import com.jcraft.jsch.ChannelSftp;
import com.weshare.batch.config.AppConfig;
import com.weshare.batch.config.CsvBeanWrapperFieldSetMapper;
import com.weshare.batch.feignClient.AdapterFeignClient;
import com.weshare.batch.service.DataCheckService;
import com.weshare.service.api.entity.*;
import com.weshare.service.api.enums.TransFlowTypeEnum;
import common.DateUtils;
import common.ReflectUtil;
import common.SftpUtils;
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
import java.util.*;
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

    public Tasklet sendStartEmailTasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();
                String batchDate = (String) jobParameters.get("batchDate");
                if ("2020-05-15".equals(batchDate)) {
                    dataCheckService.sendStartEmail(batchDate);
                }
                return RepeatStatus.FINISHED;
            }
        };
    }

    public Tasklet sendDataCheckEmailTasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();
                String batchDate = (String) jobParameters.get("batchDate");
                String projectNo = (String) jobParameters.get("projectNo");
                if ("2020-10-15".equals(batchDate)) {
                    dataCheckService.sendCheckDataEmail(batchDate, dataCheckService.getDataCheckList(batchDate, projectNo));
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
        lineTokenizer.setNames(ReflectUtil.getFieldNames(LoanDetailReq.class, "batchDate").split(","));
        lineTokenizer.setStrict(false);

        DefaultLineMapper<LoanDetailReq> detailReqDefaultLineMapper = new DefaultLineMapper<>();
        detailReqDefaultLineMapper.setLineTokenizer(lineTokenizer);
        detailReqDefaultLineMapper.setFieldSetMapper(new CsvBeanWrapperFieldSetMapper<>(LoanDetailReq.class));

        return new FlatFileItemReaderBuilder<LoanDetailReq>()
                .resource(new FileSystemResource(appConfig.getUnzip() + "/" + dateStr + "/loan_detail_" + dateStr + ".csv"))
                .strict(false)
                .name("????????????.csv")
                .addComment("????????????.csv")
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
                log.info("getLoanDetailWrite:{}???", items.size());
                adapterFeignClient.saveAllLoanDetail(items);//??????adapter??????????????????
                adapterFeignClient.saveAllLoanContractAndLoanTransFlowAndRepaySummary(items, batchDate.toString());//??????loan?????????????????????????????????,repay?????????????????????
            }
        };
    }

    @Bean
    @StepScope
    public FlatFileItemReader<RepaymentPlanReq> getRepaymentPlanRead(@Value("#{jobParameters[batchDate]}") String batchDate) {
        String dateStr = LocalDate.parse(batchDate).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setNames(ReflectUtil.getFieldNames(RepaymentPlanReq.class, "batchDate").split(","));
        lineTokenizer.setStrict(false);

        DefaultLineMapper<RepaymentPlanReq> detailReqDefaultLineMapper = new DefaultLineMapper<>();
        detailReqDefaultLineMapper.setLineTokenizer(lineTokenizer);
        detailReqDefaultLineMapper.setFieldSetMapper(new CsvBeanWrapperFieldSetMapper<>(RepaymentPlanReq.class));

        return new FlatFileItemReaderBuilder<RepaymentPlanReq>()
                .resource(new FileSystemResource(appConfig.getUnzip() + "/" + dateStr + "/repayment_plan_" + dateStr + ".csv"))
                .strict(false)
                .name("????????????.csv")
                .addComment("????????????.csv")
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
                log.info("getRepaymentPlanWrite:{}???", items.size());
                adapterFeignClient.saveAllRepaymentPlan(items);//??????adapter??????????????????
                adapterFeignClient.saveAllRepayPlanUpdateLoanContractAndRepaySummary(items);//??????loan??????????????????,repay?????????????????????
            }
        };
    }

    @Bean
    @StepScope
    public FlatFileItemReader<RefundTicketReq> getRefundTicketRead(@Value("#{jobParameters[batchDate]}") String batchDate) {
        String dateStr = LocalDate.parse(batchDate).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setNames(ReflectUtil.getFieldNames(RefundTicketReq.class, "batchDate").split(","));
        lineTokenizer.setStrict(false);

        DefaultLineMapper<RefundTicketReq> detailReqDefaultLineMapper = new DefaultLineMapper<>();
        detailReqDefaultLineMapper.setLineTokenizer(lineTokenizer);
        detailReqDefaultLineMapper.setFieldSetMapper(new CsvBeanWrapperFieldSetMapper<>(RefundTicketReq.class));

        return new FlatFileItemReaderBuilder<RefundTicketReq>()
                .resource(new FileSystemResource(appConfig.getUnzip() + "/" + dateStr + "/refund_ticket_" + dateStr + ".csv"))
                .strict(false)
                .name("????????????.csv")
                .addComment("????????????.csv")
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
                log.info("getRefundTicketWrite:{}???", items.size());
                adapterFeignClient.saveAllRefundTicket(items);//??????adapter??????????????????
                adapterFeignClient.saveRefundDownRepayTransFlowAndReceiptDetail(items, batchDate.toString());//??????????????????????????????(?????????????????????,???????????????,??????????????????)
            }
        };
    }

    @Bean
    @StepScope
    public FlatFileItemReader<RebackDetailReq> getRebackDetailRead(@Value("#{jobParameters[batchDate]}") String batchDate) {
        String dateStr = LocalDate.parse(batchDate).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setNames(ReflectUtil.getFieldNames(RebackDetailReq.class, "batchDate").split(","));
        lineTokenizer.setStrict(false);

        DefaultLineMapper<RebackDetailReq> detailReqDefaultLineMapper = new DefaultLineMapper<>();
        detailReqDefaultLineMapper.setLineTokenizer(lineTokenizer);
        detailReqDefaultLineMapper.setFieldSetMapper(new CsvBeanWrapperFieldSetMapper<>(RebackDetailReq.class));

        return new FlatFileItemReaderBuilder<RebackDetailReq>()
                .resource(new FileSystemResource(appConfig.getUnzip() + "/" + dateStr + "/reback_detail_" + dateStr + ".csv"))
                .strict(false)
                .name("??????????????????.csv")
                .addComment("??????????????????.csv")
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
                log.info("getRebackDetailWrite:{}???", items.size());
                adapterFeignClient.saveAllRebackDetal(items);//??????adapter??????reback_detail????????????????????????
                adapterFeignClient.createAllRepayTransFlow(items, batchDate.toString());//??????repay??????repay_trans_flow????????????????????????
            }
        };
    }

    @Bean
    @StepScope
    public FlatFileItemReader<RepaymentDetailReq> getRepaymentDetailRead(@Value("#{jobParameters[batchDate]}") String batchDate) {
        String dateStr = LocalDate.parse(batchDate).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setNames(ReflectUtil.getFieldNames(RepaymentDetailReq.class, "batchDate").split(","));
        lineTokenizer.setStrict(false);

        DefaultLineMapper<RepaymentDetailReq> detailReqDefaultLineMapper = new DefaultLineMapper<>();
        detailReqDefaultLineMapper.setLineTokenizer(lineTokenizer);
        detailReqDefaultLineMapper.setFieldSetMapper(new CsvBeanWrapperFieldSetMapper<>(RepaymentDetailReq.class));

        return new FlatFileItemReaderBuilder<RepaymentDetailReq>()
                .resource(new FileSystemResource(appConfig.getUnzip() + "/" + dateStr + "/repayment_detail_" + dateStr + ".csv"))
                .strict(false)
                .name("??????????????????.csv")
                .addComment("??????????????????.csv")
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
                log.info("getRepaymentDetailWrite:{}???", items.size());
                adapterFeignClient.saveAllRepaymentDetail(items);//??????adapter??????repayment_detail????????????????????????
                adapterFeignClient.createAllReceiptDetail(items, batchDate.toString());//??????repay??????receipt_detail???(????????????????????????????????????????????????????????????)
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
                //??????????????????
                loanDetailReqs = List.of(
                        new LoanDetailReq("YX-101", batchDate, new BigDecimal(1200), SnowFlake.getInstance().nextId() + "", 6, "6217 0028 7001 5622 705", "02", batchDate),
                        new LoanDetailReq("YX-101", batchDate, new BigDecimal(1200), SnowFlake.getInstance().nextId() + "", 6, "6214 8312 7106 8212 236", "01", batchDate),
                        new LoanDetailReq("YX-102", batchDate, new BigDecimal(1200), SnowFlake.getInstance().nextId() + "", 6, "6228 4800 5864 3078 676", "02", batchDate),
                        new LoanDetailReq("YX-102", batchDate, new BigDecimal(1200), SnowFlake.getInstance().nextId() + "", 6, "6217 8576 0000 7092 823", "01", batchDate)
                );
                //??????????????????
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
                //??????????????????
                refundTicketReqs = new ArrayList<>();
                //????????????????????????
                rebackDetailReqs = new ArrayList<>();
                //??????????????????
                repaymentDetailReqs = new ArrayList<>();
                createFile(path, dateStr, loanDetailReqs, repaymentPlanReqs, refundTicketReqs, rebackDetailReqs, repaymentDetailReqs);
                break;
            case "2020-05-30":
                //??????????????????
                loanDetailReqs = new ArrayList<>();
                //??????????????????
                repaymentPlanReqs = new ArrayList<>();
                //????????????
                refundTicketReqs = List.of(
                        new RefundTicketReq("YX-101", new BigDecimal(1200), LocalDate.parse("2020-05-15"), "02", "6217 0028 7001 5622 705", LocalDateTime.now().withYear(batchDate.getYear()).withMonth(batchDate.getMonthValue()).withDayOfMonth(batchDate.getDayOfMonth()), batchDate),
                        new RefundTicketReq("YX-101", new BigDecimal(1200), LocalDate.parse("2020-05-15"), "01", "6217 0028 7001 5622 705", LocalDateTime.now().withYear(batchDate.getYear()).withMonth(batchDate.getMonthValue()).withDayOfMonth(batchDate.getDayOfMonth()), batchDate)
                );
                //??????????????????
                rebackDetailReqs = new ArrayList<>();
                //??????????????????
                repaymentDetailReqs = new ArrayList<>();
                createFile(path, dateStr, loanDetailReqs, repaymentPlanReqs, refundTicketReqs, rebackDetailReqs, repaymentDetailReqs);
                break;
            case "2020-06-15":
                //??????????????????
                loanDetailReqs = new ArrayList<>();
                //??????????????????
                repaymentPlanReqs = new ArrayList<>();
                //????????????
                refundTicketReqs = new ArrayList<>();
                //??????????????????
                rebackDetailReqs = List.of(
                        new RebackDetailReq("YX-102", 1, new BigDecimal(150), new BigDecimal(80), new BigDecimal(70), BigDecimal.ZERO, BigDecimal.ZERO, "02", TransFlowTypeEnum.????????????, RebackDetailReq.FailReasonEnum.???????????????, DateUtils.getLocalDateTime(batchDate), batchDate),
                        new RebackDetailReq("YX-102", 1, new BigDecimal(150), new BigDecimal(80), new BigDecimal(70), BigDecimal.ZERO, BigDecimal.ZERO, "02", TransFlowTypeEnum.????????????, RebackDetailReq.FailReasonEnum.??????????????????, DateUtils.getLocalDateTime(batchDate), batchDate),
                        new RebackDetailReq("YX-102", 1, new BigDecimal(150), new BigDecimal(80), new BigDecimal(70), BigDecimal.ZERO, BigDecimal.ZERO, "02", TransFlowTypeEnum.????????????, RebackDetailReq.FailReasonEnum.??????????????????, DateUtils.getLocalDateTime(batchDate), batchDate),
                        new RebackDetailReq("YX-102", 1, new BigDecimal(150), new BigDecimal(80), new BigDecimal(70), BigDecimal.ZERO, BigDecimal.ZERO, "01", TransFlowTypeEnum.????????????, null, DateUtils.getLocalDateTime(batchDate), batchDate),
                        new RebackDetailReq("YX-102", 1, new BigDecimal(150), new BigDecimal(90), new BigDecimal(60), BigDecimal.ZERO, BigDecimal.ZERO, "02", TransFlowTypeEnum.????????????, RebackDetailReq.FailReasonEnum.???????????????, DateUtils.getLocalDateTime(batchDate), batchDate),
                        new RebackDetailReq("YX-102", 1, new BigDecimal(150), new BigDecimal(90), new BigDecimal(60), BigDecimal.ZERO, BigDecimal.ZERO, "02", TransFlowTypeEnum.????????????, RebackDetailReq.FailReasonEnum.??????????????????, DateUtils.getLocalDateTime(batchDate), batchDate),
                        new RebackDetailReq("YX-102", 1, new BigDecimal(150), new BigDecimal(90), new BigDecimal(60), BigDecimal.ZERO, BigDecimal.ZERO, "02", TransFlowTypeEnum.????????????, RebackDetailReq.FailReasonEnum.??????????????????, DateUtils.getLocalDateTime(batchDate), batchDate),
                        new RebackDetailReq("YX-102", 1, new BigDecimal(150), new BigDecimal(90), new BigDecimal(60), BigDecimal.ZERO, BigDecimal.ZERO, "01", TransFlowTypeEnum.????????????, null, DateUtils.getLocalDateTime(batchDate), batchDate)
                );
                //??????????????????
                repaymentDetailReqs = List.of(
                        new RepaymentDetailReq("YX-102", "01", DateUtils.getLocalDateTime(batchDate), 1, new BigDecimal(150), new BigDecimal(80), new BigDecimal(70), BigDecimal.ZERO, BigDecimal.ZERO, batchDate),
                        new RepaymentDetailReq("YX-102", "01", DateUtils.getLocalDateTime(batchDate), 1, new BigDecimal(150), new BigDecimal(90), new BigDecimal(60), BigDecimal.ZERO, BigDecimal.ZERO, batchDate)
                );
                createFile(path, dateStr, loanDetailReqs, repaymentPlanReqs, refundTicketReqs, rebackDetailReqs, repaymentDetailReqs);
                break;
            case "2020-07-20":
                //??????????????????
                loanDetailReqs = new ArrayList<>();
                //??????????????????
                repaymentPlanReqs = new ArrayList<>();
                //????????????
                refundTicketReqs = new ArrayList<>();
                //??????????????????
                rebackDetailReqs = List.of(
                        new RebackDetailReq("YX-102", 2, new BigDecimal(180), new BigDecimal(80), new BigDecimal(100), BigDecimal.ZERO, BigDecimal.ZERO, "02", TransFlowTypeEnum.????????????, RebackDetailReq.FailReasonEnum.???????????????, DateUtils.getLocalDateTime(batchDate), batchDate),
                        new RebackDetailReq("YX-102", 2, new BigDecimal(180), new BigDecimal(80), new BigDecimal(100), BigDecimal.ZERO, BigDecimal.ZERO, "02", TransFlowTypeEnum.????????????, RebackDetailReq.FailReasonEnum.??????????????????, DateUtils.getLocalDateTime(batchDate), batchDate),
                        new RebackDetailReq("YX-102", 2, new BigDecimal(180), new BigDecimal(80), new BigDecimal(100), BigDecimal.ZERO, BigDecimal.ZERO, "02", TransFlowTypeEnum.????????????, RebackDetailReq.FailReasonEnum.??????????????????, DateUtils.getLocalDateTime(batchDate), batchDate),
                        new RebackDetailReq("YX-102", 2, new BigDecimal(180), new BigDecimal(80), new BigDecimal(100), BigDecimal.ZERO, BigDecimal.ZERO, "01", TransFlowTypeEnum.????????????, null, DateUtils.getLocalDateTime(batchDate), batchDate),
                        new RebackDetailReq("YX-102", 2, new BigDecimal(120), new BigDecimal(100), new BigDecimal(20), BigDecimal.ZERO, BigDecimal.ZERO, "02", TransFlowTypeEnum.????????????, RebackDetailReq.FailReasonEnum.???????????????, DateUtils.getLocalDateTime(batchDate), batchDate),
                        new RebackDetailReq("YX-102", 2, new BigDecimal(120), new BigDecimal(100), new BigDecimal(20), BigDecimal.ZERO, BigDecimal.ZERO, "02", TransFlowTypeEnum.????????????, RebackDetailReq.FailReasonEnum.??????????????????, DateUtils.getLocalDateTime(batchDate), batchDate),
                        new RebackDetailReq("YX-102", 2, new BigDecimal(120), new BigDecimal(100), new BigDecimal(20), BigDecimal.ZERO, BigDecimal.ZERO, "02", TransFlowTypeEnum.????????????, RebackDetailReq.FailReasonEnum.??????????????????, DateUtils.getLocalDateTime(batchDate), batchDate),
                        new RebackDetailReq("YX-102", 2, new BigDecimal(120), new BigDecimal(100), new BigDecimal(20), BigDecimal.ZERO, BigDecimal.ZERO, "01", TransFlowTypeEnum.????????????, null, DateUtils.getLocalDateTime(batchDate), batchDate)
                );
                //??????????????????
                repaymentDetailReqs = List.of(
                        new RepaymentDetailReq("YX-102", "03", DateUtils.getLocalDateTime(batchDate), 2, new BigDecimal(180), new BigDecimal(80), new BigDecimal(100), BigDecimal.ZERO, BigDecimal.ZERO, batchDate),
                        new RepaymentDetailReq("YX-102", "03", DateUtils.getLocalDateTime(batchDate), 2, new BigDecimal(120), new BigDecimal(100), new BigDecimal(20), BigDecimal.ZERO, BigDecimal.ZERO, batchDate)
                );
                createFile(path, dateStr, loanDetailReqs, repaymentPlanReqs, refundTicketReqs, rebackDetailReqs, repaymentDetailReqs);
                break;
            case "2020-08-20":
                //??????????????????
                loanDetailReqs = new ArrayList<>();
                //??????????????????
                repaymentPlanReqs = new ArrayList<>();
                //????????????
                refundTicketReqs = new ArrayList<>();
                //??????????????????
                rebackDetailReqs = List.of(
                        new RebackDetailReq("YX-102", 3, new BigDecimal(215), new BigDecimal(150), new BigDecimal(30), new BigDecimal(20), new BigDecimal(15), "01", TransFlowTypeEnum.??????, null, DateUtils.getLocalDateTime(batchDate), batchDate),
                        new RebackDetailReq("YX-102", 3, new BigDecimal(105), new BigDecimal(40), new BigDecimal(50), new BigDecimal(10), new BigDecimal(5), "01", TransFlowTypeEnum.??????, null, DateUtils.getLocalDateTime(batchDate), batchDate)
                );
                //??????????????????
                repaymentDetailReqs = List.of(
                        new RepaymentDetailReq("YX-102", "04", DateUtils.getLocalDateTime(batchDate), 3, new BigDecimal(215), new BigDecimal(150), new BigDecimal(30), new BigDecimal(20), new BigDecimal(15), batchDate),
                        new RepaymentDetailReq("YX-102", "04", DateUtils.getLocalDateTime(batchDate), 3, new BigDecimal(105), new BigDecimal(40), new BigDecimal(50), new BigDecimal(10), new BigDecimal(5), batchDate)
                );
                createFile(path, dateStr, loanDetailReqs, repaymentPlanReqs, refundTicketReqs, rebackDetailReqs, repaymentDetailReqs);
                break;
            case "2020-10-15":
                //??????????????????
                loanDetailReqs = new ArrayList<>();
                //??????????????????
                repaymentPlanReqs = new ArrayList<>();
                //????????????
                refundTicketReqs = new ArrayList<>();
                //??????????????????
                rebackDetailReqs = List.of(
                        new RebackDetailReq("YX-102", 4, new BigDecimal(880), new BigDecimal(660), new BigDecimal(150), new BigDecimal(40), new BigDecimal(30), "01", TransFlowTypeEnum.????????????, null, DateUtils.getLocalDateTime(batchDate), batchDate)
                );
                //??????????????????
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

        List<String> loanDetailReqList = loanDetailReqs.stream().map(e -> ReflectUtil.getFieldValues(e, "batchDate")).collect(Collectors.toList());
        loanDetailReqList.add(0, ReflectUtil.getFieldNames(LoanDetailReq.class, "batchDate"));

        List<String> repaymentPlanReqList = repaymentPlanReqs.stream().map(e -> ReflectUtil.getFieldValues(e, "batchDate")).collect(Collectors.toList());
        repaymentPlanReqList.add(0, ReflectUtil.getFieldNames(RepaymentPlanReq.class, "batchDate"));

        List<String> refundTicketReqList = refundTicketReqs.stream().map(e -> ReflectUtil.getFieldValues(e, "batchDate")).collect(Collectors.toList());
        refundTicketReqList.add(0, ReflectUtil.getFieldNames(RefundTicketReq.class, "batchDate"));

        List<String> rebackDetailReqList = rebackDetailReqs.stream().map(e -> ReflectUtil.getFieldValues(e, "batchDate")).collect(Collectors.toList());
        rebackDetailReqList.add(0, ReflectUtil.getFieldNames(RebackDetailReq.class, "batchDate"));

        List<String> repaymentDetailReqList = repaymentDetailReqs.stream().map(e -> ReflectUtil.getFieldValues(e, "batchDate")).collect(Collectors.toList());
        repaymentDetailReqList.add(0, ReflectUtil.getFieldNames(RepaymentDetailReq.class, "batchDate"));

        Files.write(Paths.get(String.valueOf(path), "loan_detail_" + dateStr + ".csv"), loanDetailReqList, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        Files.write(Paths.get(String.valueOf(path), "repayment_plan_" + dateStr + ".csv"), repaymentPlanReqList, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        Files.write(Paths.get(String.valueOf(path), "refund_ticket_" + dateStr + ".csv"), refundTicketReqList, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        Files.write(Paths.get(String.valueOf(path), "reback_detail_" + dateStr + ".csv"), rebackDetailReqList, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        Files.write(Paths.get(String.valueOf(path), "repayment_detail_" + dateStr + ".csv"), repaymentDetailReqList, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
    }

    @Value("${spring.downloan.imagePath:true}")
    private String downImagePath;//????????????????????????
    @Value("${spring.downloan.localPath:true}")
    private String downLocalPath;//????????????????????????
    @Value("${spring.uploan.imagePath:true}")
    private String upImagePath;//????????????????????????
    @Value("${spring.uploan.localPath:true}")
    private String upLocalPath;//????????????????????????

    @Value("${spring.sftp.host:true}")
    private String host;//ip??????
    @Value("${spring.sftp.port:2020}")
    private int port;//??????
    @Value("${spring.sftp.username:true}")
    private String username;//?????????
    @Value("${spring.sftp.password:true}")
    private String password;//??????
    @Value("${spring.sftp.timeout:30000}")
    private int timeout;//??????

    public Tasklet downAndUpTask() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                String batchDate = (String) chunkContext.getStepContext().getJobParameters().get("batchDate");
                String dateStr = LocalDate.parse(batchDate).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                downloan(dateStr);
                uploan(dateStr);
                return RepeatStatus.FINISHED;
            }
        };
    }

    private void uploan(String dateStr) throws Exception {
        ChannelSftp channel = null;
        try {
            channel = (ChannelSftp) SftpUtils.connectServer(host, port, username, password, timeout);
            upImagePath = upImagePath + "/" + dateStr;
            upLocalPath = upLocalPath + "/" + dateStr;
            if (SftpUtils.dirExist(channel, upImagePath)) {
                SftpUtils.createDirIfNotExists(channel, upImagePath);
            }
            File file = new File(upLocalPath);
            List<File> files = Arrays.stream(Objects.requireNonNull(file.listFiles())).filter(e -> e.getName().startsWith(ZipUtil.ZIP_EXT)).collect(Collectors.toList());
            for (File file_ : files) {
                SftpUtils.uploadFile(channel, file_.getAbsolutePath(), upImagePath+"/"+file_.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SftpUtils.close(channel);
        }

    }

    private void downloan(String dateStr) throws Exception {
        ChannelSftp channel = null;
        try {
            channel = (ChannelSftp) SftpUtils.connectServer(host, port, username, password, timeout);
            downImagePath = downImagePath + "/" + dateStr;
            downLocalPath = downLocalPath + "/" + dateStr;
            List<ChannelSftp.LsEntry> lsEntryList = SftpUtils.getDirList(channel, downImagePath);
            Path localPath = Paths.get(downLocalPath);
            if (Files.notExists(localPath)) {
                Files.createDirectories(localPath);//?????????????????????
            }
            if (lsEntryList.isEmpty()) {
                for (ChannelSftp.LsEntry file : lsEntryList) {
                    if (file.getFilename().startsWith(ZipUtil.ZIP_EXT)) {
                        //??????????????????
                        SftpUtils.copyFile(channel, downImagePath + "/" + file.getFilename(), downLocalPath);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SftpUtils.close(channel);
        }
    }
}
