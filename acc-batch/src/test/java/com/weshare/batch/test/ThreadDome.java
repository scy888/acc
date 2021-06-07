package com.weshare.batch.test;

import lombok.Data;
import org.junit.Test;

import java.util.Random;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.test
 * @date: 2021-06-06 22:08:27
 * @describe:
 */
public class ThreadDome {

    private static ThreadLocal<Student> threadLocal = new ThreadLocal<>();
    private static Student student;

    // @Test
    public static void main(String[] args) {
        for (int i = 1; i <= 4; i++) {
            new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            Student student = new ThreadDome().getStudent(5);
                            System.out.println("当前线程名:" + Thread.currentThread().getName() + " get() age:" + student.getAge());

                        }
                    }, "thread-" + i
            ).start();
        }
    }

    private Student getStudent(Integer num) {
        student = threadLocal.get();
        if (student == null) {
            student = new Student();
        }
        student.setAge(new Random().nextInt(num));
        threadLocal.set(student);

        // student = new Student();
        // student.setAge(new Random().nextInt(num));

        System.out.println("当前线程名:" + Thread.currentThread().getName() + " set() age:" + threadLocal.get().getAge());
        return threadLocal.get();
    }

    //@Data
    class Student {
        private Integer age;

        private Student() {
        }

        private Student getInstance() {
            return new Student();
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "Student{" +
                    "age=" + age +
                    '}';
        }
    }

    @Test
    public void test() {
        System.out.println(new Student().getInstance() == new Student().getInstance());
    }
}
