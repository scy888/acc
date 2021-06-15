package com.weshare.batch.job;

import com.weshare.batch.Listener.JobListener;
import com.weshare.batch.enums.BatchJobEnum;
import com.weshare.batch.enums.JobStepName;
import com.weshare.batch.feignClient.LoanFeignClient;
import com.weshare.batch.tasklet.YxmsTasklet;
import com.weshare.service.api.entity.*;
import com.weshare.service.api.enums.ProjectEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.job
 * @date: 2021-06-15 18:23:39
 * @describe:
 */
@Component
@Slf4j
public class YxmsJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private JobListener jobListener;
    @Autowired
    private YxmsTasklet yxmsTasklet;
    @Autowired
    private LoanFeignClient loanFeignClient;

    public Step createCsvStep() {
        return stepBuilderFactory.get(JobStepName.YsmsJob.创建文件步骤)
                .tasklet(yxmsTasklet.createCsvTasklet())
                .allowStartIfComplete(true)
                .build();
    }

    public Step zipCsvStep() {
        return stepBuilderFactory.get(JobStepName.YsmsJob.压缩文件步骤)
                .tasklet(yxmsTasklet.zipCsvTasklet())
                .allowStartIfComplete(true)
                .build();
    }

    public Step unzipCsvStep() {
        return stepBuilderFactory.get(JobStepName.YsmsJob.解压文件步骤)
                .tasklet(yxmsTasklet.unzipCsvTasklet())
                .allowStartIfComplete(true)
                .build();
    }

    public Step clearAllStep() {
        return stepBuilderFactory.get(JobStepName.YsmsJob.清除相关表的数据步骤)
                .tasklet(yxmsTasklet.clearAllTasklet())
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step loanDetailCsvStep(FlatFileItemReader<LoanDetailReq> getLoanDetailRead,
                                  ItemWriter<LoanDetailReq> getLoanDetailWrite,
                                  TaskExecutor batchTaskExecutor) {
        return stepBuilderFactory.get(JobStepName.YsmsJob.读写放款明细步骤)
                .<LoanDetailReq, LoanDetailReq>chunk(100)
                .reader(getLoanDetailRead)
                .writer(getLoanDetailWrite)
                .allowStartIfComplete(true)
                .taskExecutor(batchTaskExecutor)
                .throttleLimit(1)
                .build();
    }

    @Bean
    public Step repaymentPlanCsvStep(FlatFileItemReader<RepaymentPlanReq> getRepaymentPlanRead,
                                     ItemWriter<RepaymentPlanReq> getRepaymentPlanWrite,
                                     TaskExecutor batchTaskExecutor) {
        return stepBuilderFactory.get(JobStepName.YsmsJob.读写还款计划步骤)
                .<RepaymentPlanReq, RepaymentPlanReq>chunk(100)
                .reader(getRepaymentPlanRead)
                .writer(getRepaymentPlanWrite)
                .allowStartIfComplete(true)
                .taskExecutor(batchTaskExecutor)
                .throttleLimit(1)
                .build();
    }

    @Bean
    public Step refundTicketCsvStep(FlatFileItemReader<RefundTicketReq> getRefundTicketRead,
                                    ItemWriter<RefundTicketReq> getRefundTicketWrite,
                                    TaskExecutor batchTaskExecutor) {
        return stepBuilderFactory.get(JobStepName.YsmsJob.读写退票步骤)
                .<RefundTicketReq, RefundTicketReq>chunk(100)
                .reader(getRefundTicketRead)
                .writer(getRefundTicketWrite)
                .allowStartIfComplete(true)
                .taskExecutor(batchTaskExecutor)
                .throttleLimit(1)
                .build();
    }

    @Bean
    public Step rebackDetailCsvStep(FlatFileItemReader<RebackDetailReq> getRebackDetailRead,
                                    ItemWriter<RebackDetailReq> getRebackDetailWrite,
                                    TaskExecutor batchTaskExecutor) {
        return stepBuilderFactory.get(JobStepName.YsmsJob.读写扣款明细步骤)
                .<RebackDetailReq, RebackDetailReq>chunk(100)
                .reader(getRebackDetailRead)
                .writer(getRebackDetailWrite)
                .allowStartIfComplete(true)
                .taskExecutor(batchTaskExecutor)
                .throttleLimit(1)
                .build();
    }

    @Bean
    public Step repaymentDetailCsvStep(FlatFileItemReader<RepaymentDetailReq> getRepaymentDetailRead,
                                       ItemWriter<RepaymentDetailReq> getRepaymentDetailWrite,
                                       TaskExecutor batchTaskExecutor) {
        return stepBuilderFactory.get(JobStepName.YsmsJob.读写实还明细步骤)
                .<RepaymentDetailReq, RepaymentDetailReq>chunk(100)
                .reader(getRepaymentDetailRead)
                .writer(getRepaymentDetailWrite)
                .allowStartIfComplete(true)
                .taskExecutor(batchTaskExecutor)
                .throttleLimit(1)
                .build();
    }

    @Bean(name = "yxmsJob_")
    public Job yxmsJob(Step loanDetailCsvStep, Step repaymentPlanCsvStep,
                       Step refundTicketCsvStep, Step rebackDetailCsvStep,
                       Step repaymentDetailCsvStep) {
        return jobBuilderFactory.get(BatchJobEnum.yxmsJob.name())
                .incrementer(new RunIdIncrementer())
                .listener(jobListener)
                .start(createCsvStep())
                .next(zipCsvStep())
                .next(unzipCsvStep())
                .next(clearAllStep())
                .next(loanDetailCsvStep)
                .next(repaymentPlanCsvStep)
                .next(refundTicketCsvStep)
                .next(rebackDetailCsvStep)
                .next(repaymentDetailCsvStep)
                .next(stepBuilderFactory.get(JobStepName.YsmsJob.更新repay_summary表的当前期次)
                        .tasklet((contribution, chunkContext) -> {
                            String batchDate = (String) chunkContext.getStepContext().getJobParameters().get("batchDate");
                            loanFeignClient.UpdateRepaySummaryCurrentTerm(ProjectEnum.YXMS.getProjectNo(), batchDate);
                            return RepeatStatus.FINISHED;
                        }).build())
                .build();
    }
}
