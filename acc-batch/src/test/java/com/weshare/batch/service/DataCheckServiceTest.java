package com.weshare.batch.service;

import com.weshare.batch.task.repo.TaskConfigDao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.stream.Collectors;

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
    @Autowired
    private TaskConfigDao taskConfigDao;

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
}