package com.weshare.batch.job;

import com.weshare.batch.Person;
import com.weshare.batch.enums.BatchJobEnum;
import com.weshare.batch.enums.JobStepName;
import com.weshare.batch.tasklet.PersonTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.job
 * @date: 2021-05-15 13:48:19
 * @describe:
 */
@Configuration
public class BatchPersonJob {
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private PersonTasklet personTasklet;

    public Step createCsvStep() {
        return stepBuilderFactory.get(JobStepName.PersonTestJob.创建personCsv步骤)
                .tasklet(personTasklet.createCsvTasklet())
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step readAndWriteCsvStep(ItemProcessor<Person, Person> personProcessor,
                                    TaskExecutor batchTaskExecutor) {
        return stepBuilderFactory.get(JobStepName.PersonTestJob.读取personCsv步骤)
                .<Person, Person>chunk(10)
                .reader(personTasklet.personReader())
                .processor(personProcessor)
                .writer(personTasklet.personWriter())
                .taskExecutor(batchTaskExecutor)
                .throttleLimit(4)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Job personJob(Step readAndWriteCsvStep) {
        return jobBuilderFactory.get(BatchJobEnum.personJob.name())
                .incrementer(new RunIdIncrementer())
                .start(createCsvStep())
                .next(readAndWriteCsvStep)
                .build();
    }
}
