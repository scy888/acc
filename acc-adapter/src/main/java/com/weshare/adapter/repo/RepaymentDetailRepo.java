package com.weshare.adapter.repo;

import com.weshare.adapter.entity.RepaymentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.adapter.repo
 * @date: 2021-06-01 20:21:53
 * @describe:
 */
public interface RepaymentDetailRepo extends JpaRepository<RepaymentDetail,String> {

    @Transactional
    @Modifying
    @Query("delete from #{#entityName} e where e.batchDate=:batchDate and e.dueBillNo in :dueBillNoList")
    void deleteByBatchDateAndDueBillNoIn(LocalDate batchDate, List<String> dueBillNoList);
}
