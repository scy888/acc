package com.weshare.repay.repo;

import com.weshare.repay.entity.RepayTransFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.repay.repo
 * @date: 2021-05-30 18:22:20
 * @describe:
 */
public interface RepayTransFlowRepo extends JpaRepository<RepayTransFlow, String> {

    @Transactional
    @Modifying
    @Query(value = "delete from #{#entityName} e where e.batchDate=:batchDate and e.dueBillNo in :dueBillNoList")
    void deleteByBatchDateAndDueBillNoIn(@Param("batchDate") LocalDate batchDate, @Param("dueBillNoList") List<String> dueBillNoList);

}
