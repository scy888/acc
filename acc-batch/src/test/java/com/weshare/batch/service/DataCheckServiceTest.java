package com.weshare.batch.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.service
 * @date: 2021-06-28 14:19:20
 * @describe:
 */
@SpringBootTest
class DataCheckServiceTest {
    @Autowired
    private DataCheckService dataCheckService;

    @Test
    void checkDataResult() {
        dataCheckService.checkDataResult("WS121212","2020-06-26");
    }
}