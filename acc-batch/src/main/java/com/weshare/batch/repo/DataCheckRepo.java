package com.weshare.batch.repo;

import com.weshare.batch.entity.DataCheck;
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
 * @package: com.weshare.batch.repo
 * @date: 2021-06-28 13:07:03
 * @describe:
 */
public interface DataCheckRepo extends JpaRepository<DataCheck, String> {

    @Modifying
    @Transactional
    @Query("delete from #{#entityName} e where e.projectNo=:projectNo and e.batchDate=:batchDate and e.checkName=:checkName")
    void deleteByProjectNoAndBatchDateAndCheckName(@Param("projectNo") String projectNo, @Param("batchDate") LocalDate batchDate, @Param("checkName") String checkName);

    List<DataCheck> findByBatchDateAndProjectNo(LocalDate batchDate, String projectNo);
}
