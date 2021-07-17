package com.weshare.adapter.migration;

import com.google.common.base.CaseFormat;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.adapter.migration
 * @date: 2021-07-16 16:11:26
 * @describe 数据迁移
 */
public abstract class Migration {
    public abstract String dataMigration(String msgJson, String batchDate, String dataLogId);

    public String getClassName() {
        Class<?> clazz = this.getClass();
        String name = clazz.getSimpleName();
        String superName = clazz.getSuperclass().getSimpleName();
        name = name.substring(superName.length());
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, name);
    }
}
