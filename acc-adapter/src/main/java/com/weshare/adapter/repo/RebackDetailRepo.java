package com.weshare.adapter.repo;

import com.weshare.adapter.entity.RebackDetail;
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
 * @package: com.weshare.adapter.repo
 * @date: 2021-06-01 11:29:45
 * @describe:
 */
public interface RebackDetailRepo extends JpaRepository<RebackDetail,String> {

    @Transactional
    @Modifying
    @Query(value = "delete from reback_detail where batch_date=:batchDate and due_bill_no in :dueBillNoList",nativeQuery = true)
    void deleteByBatchDateAndDueBillNoIn( LocalDate batchDate, List<String> dueBillNoList);
}
