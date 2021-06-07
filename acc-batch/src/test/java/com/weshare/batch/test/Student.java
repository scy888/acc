package com.weshare.batch.test;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.test
 * @date: 2021-06-06 23:24:32
 * @describe:
 */
public class Student {

    private String name;
    private int age;

    private static Student instance = null;

    public static Student getInstance(String name, Integer age) {
        if (instance == null) {
            instance = new Student(name, age);
        }
        return instance;
    }

    private Student() {
    }

    private Student(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
