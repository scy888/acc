package com.weshare.batch.enums;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.enums
 * @date: 2021-05-11 17:01:41
 * @describe:
 */
public class JobStepName {

    public static class BatchTestJob {
        public static final String BATCH测试 = "BATCH测试";
        public static final String BATCHONE = "BATCHONE";
        public static final String BATCHTWO = "BATCHTWO";
    }

    public static class PersonTestJob {
        public static final String 创建personCsv步骤 = "创建personCsv步骤";
        public static final String 读取personCsv步骤 = "读取personCsv步骤";
    }
}
