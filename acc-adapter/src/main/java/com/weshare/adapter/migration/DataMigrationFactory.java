package com.weshare.adapter.migration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.adapter.migration
 * @date: 2021-07-16 16:59:29
 * @describe:
 */
@Service
@Slf4j
public class DataMigrationFactory {

    @Autowired
    private Migration migrationLoanDetail;
    @Autowired
    private Migration migrationRepayPlan;

    private static final Map<String, Migration> map = new HashMap<>();


    @PostConstruct
    public void init() {
        System.out.println("初始化了....");
        map.put(migrationLoanDetail.getClassName(), migrationLoanDetail);
        map.put(migrationRepayPlan.getClassName(), migrationRepayPlan);
        //map.put("repayPlan", new MigrationRepayPlan());
    }

    public Migration getDataMigration(String serviceId) {

        return map.get(serviceId);
    }

    @Service("migrationLoanDetail")
    public static class MigrationLoanDetail extends Migration {

        @Override
        public String dataMigration(String contextJson, String batchDate, String dataLogId) {
            log.info("MigrationLoanDetail....");
            return null;
        }
    }

    @Service("migrationRepayPlan")
    public static class MigrationRepayPlan extends Migration {

        @Override
        public String dataMigration(String contextJson, String batchDate, String dataLogId) {
            log.info("MingrationRepayPlan....");

            return null;
        }
    }
}
