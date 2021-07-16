package com.weshare.adapter.migration;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
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
    private DataMigration migrationLoanDetail;
    @Autowired
    private DataMigration migrationRepayPlan;

    public Map<String, DataMigration> map = new HashMap<>();


    @PostConstruct
    public void init() {
        System.out.println("初始化了....");
        map.put("loanDetail", migrationLoanDetail);
        map.put("repayPlan", migrationRepayPlan);
        //map.put("repayPlan", new MigrationRepayPlan());
    }


    public DataMigration getDataMigration(String serviceId) {
        return map.get(serviceId);
    }

    @Service("migrationLoanDetail")
    public class MigrationLoanDetail implements DataMigration {

        @Override
        public String dataMigration(String contextJson, String batchDate, String dataLogId) {
            log.info("MigrationLoanDetail....");
            return null;
        }
    }

    @Service("migrationRepayPlan")
    public class MigrationRepayPlan implements DataMigration {

        @Override
        public String dataMigration(String contextJson, String batchDate, String dataLogId) {
            log.info("MingrationRepayPlan....");

            return null;
        }
    }
}
