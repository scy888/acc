package com.weshare.batch.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
}
