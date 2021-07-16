package com.weshare.adapter.migration;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.adapter.migration
 * @date: 2021-07-16 16:11:26
 * @describe 数据迁移
 */
public interface DataMigration {
    String dataMigration(String contextJson, String batchDate, String dataLogId);
}
