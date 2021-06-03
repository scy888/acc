package com.weshare.repay.repo;

import com.weshare.repay.entity.ReceiptDetail;
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
 * @date: 2021-05-31 00:04:26
 * @describe:
 */
public interface ReceiptDetailRepo extends JpaRepository<ReceiptDetail, String> {

    @Transactional
    @Modifying
    @Query("delete from #{#entityName} where batchDate=:batchDate and dueBillNo in :dueBillNoList")
    void deleteByBatchDateAndDueBillNoIn(@Param("batchDate") LocalDate batchDate,@Param("dueBillNoList") List<String> dueBillNoList);

    List<ReceiptDetail> findByDueBillNoAndTerm(String dueBillNo, Integer term);
}
