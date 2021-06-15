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

    public static class YsmsJob {
        public static final String 创建文件步骤 = "创建文件步骤";
        public static final String 压缩文件步骤 = "压缩文件步骤";
        public static final String 解压文件步骤 = "解压文件步骤";
        public static final String 清除相关表的数据步骤 = "清除相关表的数据步骤";
        public static final String 读写放款明细步骤 = "读写放款明细步骤";
        public static final String 读写还款计划步骤 = "读写还款计划步骤";
        public static final String 读写退票步骤 = "读写退票步骤";
        public static final String 读写扣款明细步骤 = "读写扣款明细步骤";
        public static final String 读写实还明细步骤 = "读写实还明细步骤";
        public static final String 更新repay_summary表的当前期次 = "更新repay_summary表的当前期次";
    }
}
