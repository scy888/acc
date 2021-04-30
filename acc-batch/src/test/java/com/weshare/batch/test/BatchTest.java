package com.weshare.batch.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

    @Test
    public void test(@Autowired HttpServletRequest request) {
        System.out.println(request);
    }

    @Test
    public void test() throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get("E:\\image", "aa.jpg"));
        Files.write(Paths.get("E:\\ideaws\\acc\\acc-batch\\src\\test\\resources","bb.jpg"), bytes, StandardOpenOption.CREATE);
    }
}
