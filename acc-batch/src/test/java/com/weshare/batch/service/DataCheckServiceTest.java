package com.weshare.batch.service;

import com.weshare.batch.entity.SysLog;
import com.weshare.batch.task.repo.TaskConfigDao;

import com.weshare.service.api.vo.Tuple2;
import common.SnowFlake;
import common.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.service
 * @date: 2021-06-28 14:19:20
 * @describe:
 */
@SpringBootTest
@Slf4j
class DataCheckServiceTest {
    @Autowired
    private DataCheckService dataCheckService;
    @Autowired
    private TaskConfigDao taskConfigDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void checkDataResult() {
        dataCheckService.checkDataResult("WS121212", "2020-06-26");
    }

    @Test
    public void testUpdate() throws IOException, URISyntaxException {
        System.out.println(dataCheckService.batchUpdate());
    }

    @Test
    public void test() {
        taskConfigDao.lockTask("yxmsTask");
    }

    @Test
    public void testReflect() {
        Class<DataCheckService> clazz = DataCheckService.class;
        try {
            Method method = clazz.getMethod("getDataCheckList", String.class, String.class);
            String name = method.getName();
            Class<?>[] parameterTypes = method.getParameterTypes();
            Parameter[] parameters = method.getParameters();
            Class<?> returnType = method.getReturnType();
            String[] objects = new String[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                objects[i] = parameterTypes[i].getSimpleName() + " " + parameters[i].getName();
            }
            System.out.println(Modifier.toString(method.getModifiers()));
            System.out.println(clazz.getName() + "." + method.getName() + "()");
            System.out.println(Arrays.asList(objects));
            System.out.println(returnType.getName());
            String s = Modifier.toString(method.getModifiers()) + " " + method.getReturnType().getSimpleName() + " " + method.getName() + "(" + Arrays.stream(objects).collect(Collectors.joining(",")) + ")";
            System.out.println(s);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInsertSql() {
        SysLog sysLog = new SysLog().setId(SnowFlake.getInstance().nextId() + "")
                .setClassName("ClassName")
                .setMethodName("MethodName")
                .setParamsName("ParamsName")
                .setParamsType("ParamsType")
                .setReturnClassName("ReturnClassName")
                .setReturnValue("ReturnValue")
                .setLostTime(new BigDecimal("12.03"));

        int update = jdbcTemplate.update(StringUtils.getInsertSql(sysLog));
        int update1 = jdbcTemplate.update(StringUtils.getUpdateSql(sysLog, "id", "class_name", "return_value"));

        LocalDateTime start = LocalDateTime.of(2020, 5, 12, 12, 12, 12);
        LocalDateTime end = LocalDateTime.of(2020, 5, 12, 13, 14, 12);
        System.out.println(end.getSecond() - start.getSecond());
        System.out.println(Duration.between(start, end).getSeconds());
        System.out.println(Duration.between(start, end).toMillis());
        System.out.println(Duration.between(start, end).toMinutes());
    }

    @Test
    public void testMap() {
        Map<String, Object> ofEntries = Map.ofEntries(
                Map.entry("jobName", "yxmsJob"),
                Map.entry("batchDate", "2020-05-15"),
                Map.entry("endDate", "2020-10-15"),
                Map.entry("projectNo", "WS121212"),
                Map.entry("remark", System.currentTimeMillis()+"")
        );
        ofEntries.forEach((k, v) -> {
            System.out.println(k + ":" + v);
        });
    }

    @Test
    @DisplayName("更新329640条还款计划耗时耗时:13.254 秒")
    public void updateRepaymentIsnull01() {
        long start = System.currentTimeMillis();
        String sqlCount = "select a.due_bill_no,a.product_no from acc_repay.repayment_summary a where a.project_no='WS0010200001' and a.product_no is not null";
        List<Tuple2<String, String>> tuple2s = jdbcTemplate.query(sqlCount, new RowMapper<Tuple2<String, String>>() {
            @Override
            public Tuple2<String, String> mapRow(ResultSet rs, int rowNum) throws SQLException {
                return Tuple2.of(rs.getString(1), rs.getString(2));
            }
        });
        int num = (int) Math.ceil(tuple2s.size() * 1.0 / 1000);
        String sqlUpdate = "update acc_repay.repayment_plan a set a.product_no=?,a.last_modified_date = now() where a.due_bill_no=? and a.project_no='WS0010200001' and a.product_no is null";
        for (int i = 1; i <= num; i++) {
            List<Tuple2<String, String>> list = tuple2s.subList((i - 1) * 1000, Math.min(i * 1000, tuple2s.size()));
            jdbcTemplate.batchUpdate(sqlUpdate, list.stream().map(e -> new Object[]{e.getSecond(), e.getFirst()}).collect(Collectors.toList()));
        }
        long end = System.currentTimeMillis();
        log.info("耗时:{} 秒", (end - start) / 1000.0);
    }

    @Test
    @DisplayName("更新329640条还款计划耗时15.543 秒")
    public void updateRepaymentIsnull02() {
        long start = System.currentTimeMillis();
        String sqlCount = "select count(*) from acc_repay.repayment_summary a where a.project_no='WS0010200001' and a.product_no is not null";
        Integer count = jdbcTemplate.queryForObject(sqlCount, Integer.class);
        int num = (int) Math.ceil(count * 1.0 / 1000);
        for (int i = 1; i <= num; i++) {
            String sqlUpdate = "select a.due_bill_no,a.product_no from acc_repay.repayment_summary a where a.project_no='WS0010200001' and a.product_no is not null limit ?,?";
            List<Tuple2<String, String>> tuple2s = jdbcTemplate.query(sqlUpdate, new RowMapper<Tuple2<String, String>>() {
                @Override
                public Tuple2<String, String> mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return Tuple2.of(rs.getString(1), rs.getString(2));
                }
            }, (i - 1) * 1000, 1000);
            String sqlUpdate_ = "update acc_repay.repayment_plan a set a.product_no=?,a.last_modified_date = now() where a.due_bill_no=? and a.project_no='WS0010200001' and a.product_no is null";
            jdbcTemplate.batchUpdate(sqlUpdate_, tuple2s.stream().map(e -> new Object[]{e.getSecond(), e.getFirst()}).collect(Collectors.toList()));
        }
        long end = System.currentTimeMillis();
        log.info("耗时:{} 秒", (end - start) / 1000.0);
    }

    @Test
    @DisplayName("更新329640条还款计划耗时9.918 秒")
    public void updateRepaymentIsnull03() {
        long start = System.currentTimeMillis();
        String sqlCount = "update acc_repay.repayment_plan a,acc_repay.repayment_summary b set a.product_no=b.product_no where a.project_no=b.project_no and a.due_bill_no=b.due_bill_no and a.project_no='WS0010200001' and a.product_no is null";
        jdbcTemplate.update(sqlCount);
        long end = System.currentTimeMillis();
        log.info("耗时:{} 秒", (end - start) / 1000.0);
    }

    @Test
    @DisplayName("更新329640条还款计划耗时42.806 秒")
    public void updateRepaymentIsnull04() {
        long start = System.currentTimeMillis();
        String sqlCount = "select count(*) from acc_repay.repayment_plan a where a.project_no='WS0010200001' and a.product_no is null";
        Integer count = jdbcTemplate.queryForObject(sqlCount, Integer.class);
        int num = (int) Math.ceil(count * 1.0 / 5000);
        String updateSQL = "update acc_repay.repayment_plan a set a.product_no=(select b.product_no from acc_repay.repayment_summary b where b.project_no=a.project_no and b.due_bill_no=a.due_bill_no) where a.project_no='WS0010200001' and a.product_no is null limit 5000";
        for (int i = 1; i <= num; i++) {
            jdbcTemplate.update(updateSQL);
        }
        long end = System.currentTimeMillis();
        log.info("耗时:{} 秒", (end - start) / 1000.0);
    }
}