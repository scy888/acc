package com.weshare.loan.repo;

import com.weshare.loan.entity.LoanTransFlow;
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
 * @package: com.weshare.loan.repo
 * @date: 2021-05-27 14:56:05
 * @describe:
 */
public interface LoanTransFlowRepo extends JpaRepository<LoanTransFlow, String> {

    LoanTransFlow findByBatchDateAndDueBillNo(LocalDate batchDate, String dueBillNo);

    @Transactional
    @Modifying
    @Query("delete #{#entityName} a where a.batchDate=:batchDate and a.dueBillNo=:dueBillNo")
    void deleteByBatchDateAndDueBillNo(@Param("batchDate") LocalDate batchDate, @Param("dueBillNo") String dueBillNo);

    List<LoanTransFlow> findByBatchDateAndDueBillNoIn(LocalDate batchDate, List<String> dueBillNoList);
}
