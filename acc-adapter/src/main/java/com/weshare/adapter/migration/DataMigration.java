package com.weshare.adapter.migration;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.adapter.migration
 * @date: 2021-07-16 16:11:26
 * @describe 数据迁移
 */
public abstract class DataMigration {
    public abstract String dataMigration(String contextJson, String batchDate, String dataLogId);

    public String getClassName() {
        String name = this.getClass().getSimpleName();
        return name.substring(0, 1).toLowerCase().concat(name.substring(1));
    }
}
