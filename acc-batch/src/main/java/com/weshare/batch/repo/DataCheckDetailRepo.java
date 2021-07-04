package com.weshare.batch.repo;

import com.weshare.batch.entity.DataCheckDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.repo
 * @date: 2021-06-28 13:07:52
 * @describe:
 */
public interface DataCheckDetailRepo extends JpaRepository<DataCheckDetail, String> {
    @Modifying
    @Transactional
    @Query("delete from DataCheckDetail where projectNo=:projectNo and batchDate=:batchDate and checkType=:checkType")
    void deleteByProjectNoAndBatchDateAndCheckType(@Param("projectNo") String projectNo,@Param("batchDate") LocalDate batchDate,@Param("checkType") String checkType);
}
