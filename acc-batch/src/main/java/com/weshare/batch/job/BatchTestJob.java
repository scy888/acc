package com.weshare.batch.job;

import com.weshare.batch.enums.BatchJobEnum;
import com.weshare.batch.enums.JobStepName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.job
 * @date: 2021-05-11 16:54:04
 * @describe:
 */
@Configuration
@Slf4j
public class BatchTestJob {
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    public Step stepOne() {
        return stepBuilderFactory.get(JobStepName.BatchTestJob.BATCHONE)
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        log.info("JobStepName.BatchTestJob.BATCHONE");
                        return RepeatStatus.FINISHED;
                    }
                })
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step stepTwo() {
        return stepBuilderFactory.get(JobStepName.BatchTestJob.BATCHTWO)
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        log.info("JobStepName.BatchTestJob.BATCHTWO");
                        return RepeatStatus.FINISHED;
                    }
                })
                .allowStartIfComplete(true)
                .build();
    }

    @Bean(value = "batchTestJob_")
    //@Qualifier("batchTestJob")
    public Job batchTestJob( Step stepTwo) {
        return jobBuilderFactory.get(BatchJobEnum.TATCHTESTJOB.name())
                .incrementer(new RunIdIncrementer())
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        log.info("TATCHTESTJOB执行前:" + jobExecution.toString());
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        log.info("TATCHTESTJOB执行后:" + jobExecution.toString());
                    }
                })
                .start(
                        stepBuilderFactory.get(JobStepName.BatchTestJob.BATCH测试)
                                .tasklet(new Tasklet() {
                                    @Override
                                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                                        Map<String, Object> map = chunkContext.getStepContext().getJobParameters();
                                        map.forEach((k, v) -> {
                                            log.info("map=>:{}", k + ":" + v);
                                        });
                                        return RepeatStatus.FINISHED;
                                    }
                                })
                                .allowStartIfComplete(true)
                                .build()
                )
                .next(stepOne())
                .next(stepTwo)
                .build();
    }
}
