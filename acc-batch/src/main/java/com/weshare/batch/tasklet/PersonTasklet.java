package com.weshare.batch.tasklet;

import com.weshare.batch.Person;
import com.weshare.batch.config.CsvBeanWrapperFieldSetMapper;
import common.ReflectUtils;
import common.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.tasklet
 * @date: 2021-05-14 16:08:46
 * @describe:
 */
@Component
@Slf4j
public class PersonTasklet {

    public Tasklet createCsvTasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();
                String pathStr = (String) jobParameters.get("pathStr");
                log.info("从参数列表中获取的生成的csv路径:{}", pathStr);
                List<Person> personList = List.of(
                        new Person().setId(SnowFlake.getInstance().nextId() + "").setName("赵敏").setAddress("蒙古").setAge(20).setBirthday(LocalDate.parse("1992-06-18")).setSalary(new BigDecimal("1992.0618")).setStatus(Person.Status.F),
                        new Person().setId(SnowFlake.getInstance().nextId() + "").setName("周芷若").setAddress("峨嵋").setAge(19).setBirthday(LocalDate.parse("1992-05-12")).setSalary(new BigDecimal("1992.0512")).setStatus(Person.Status.M),
                        new Person().setId(SnowFlake.getInstance().nextId() + "").setName("小昭").setAddress("波斯").setAge(18).setBirthday(LocalDate.parse("1994-10-10")).setSalary(new BigDecimal("1994.1010")).setStatus(Person.Status.O),
                        new Person().setId(SnowFlake.getInstance().nextId() + "").setName("阿离").setAddress("灵蛇岛").setAge(17).setBirthday(LocalDate.parse("1995-12-16")).setSalary(new BigDecimal("1995.1216")).setStatus(Person.Status.N));

                Path path = Paths.get(pathStr, "create");
                if (Files.notExists(path)) {
                    Files.createDirectories(path);
                }
                List<String> list = personList.stream().map(e -> ReflectUtils.getFieldValues(e, "batchDate"))
                        .collect(Collectors.toList());
                String fieldNames = ReflectUtils.getFieldNames(Person.class, "batchDate");
                list.add(0, fieldNames);
                path = Paths.get(String.valueOf(path), "person.csv");
                log.info("createCsvTasklet=>" + path);
                Files.write(path, list, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
                return RepeatStatus.FINISHED;
            }
        };
    }

    public FlatFileItemReader<Person> personReader() {

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setNames(ReflectUtils.getFieldNames(Person.class, "batchDate").split(","));
        lineTokenizer.setStrict(false);

        DefaultLineMapper<Person> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(lineTokenizer);
        CsvBeanWrapperFieldSetMapper<Person> csvBeanWrapperFieldSetMapper = new CsvBeanWrapperFieldSetMapper<>(Person.class);
        csvBeanWrapperFieldSetMapper.setTargetType(Person.class);
        lineMapper.setFieldSetMapper(csvBeanWrapperFieldSetMapper);


        return new FlatFileItemReaderBuilder<Person>()
                .resource(new FileSystemResource("/batch/create/person.csv"))
                .name("读取person.csv文件")
                .addComment("读取person.csv文件")
                .linesToSkip(1)
                .lineMapper(lineMapper)
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<Person, Person> personProcessor(@Value("#{jobParameters[batchDate]}") String batchDate) {
        log.info("personProcessor=>:" + batchDate);
        return new ItemProcessor<Person, Person>() {
            @Override
            public Person process(Person person) throws Exception {
                person.setBatchDate(LocalDate.parse(batchDate));
                return person;
            }
        };
    }

    public ItemWriter<Person> personWriter() {
        return new ItemWriter<Person>() {
            @Override
            public void write(List<? extends Person> personList) throws Exception {
                log.info("personWriter:{}条", personList.size());
                List<String> list = personList.stream().map(e -> ReflectUtils.getFieldValues(e))
                        .collect(Collectors.toList());

                Path path = Paths.get("/batch", "write");
                if (Files.notExists(path)) {
                    Files.createDirectories(path);
                }
                path = Paths.get(String.valueOf(path), "person.csv");
                Files.write(path, list, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
            }
        };
    }
}
