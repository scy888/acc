package com.weshare;

import com.weshare.eureka.EurekaApplication;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare
 * @date: 2021-04-25 23:35:36
 * @describe:
 */
@SpringBootTest(classes = EurekaApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@RunWith(SpringRunner.class)
public class test {

    @Autowired
    private HttpServletRequest request;

    @Test
    @DisplayName("haha")
    public void test() {
        System.out.println(request);
    }
}
