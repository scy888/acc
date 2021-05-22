package com.weshare.batch.test;

import com.weshare.batch.controller.AsyncController;
import jodd.io.ZipUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weahare.batch.test
 * @date: 2021-04-30 12:29:57
 * @describe:
 */

@SpringBootTest
public class BatchTest {
    @Autowired
    @Qualifier("secondJdbcTemplate")
    //private JdbcTemplate secondJdbcTemplate;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AsyncController asyncController;

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
    public void test02(){
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
}
