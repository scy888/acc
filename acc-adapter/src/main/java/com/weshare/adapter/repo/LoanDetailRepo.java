package com.weshare.adapter.repo;

import com.weshare.adapter.entity.LoanDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.adapter.repo
 * @date: 2021-05-27 10:34:35
 * @describe:
 */
public interface LoanDetailRepo extends JpaRepository<LoanDetail, String> {

    @Transactional
    @Modifying
    @Query("delete from #{#entityName} e where e.dueBillNo in :dueBillNoList")
    void deleteByDueBillNoList(@Param("dueBillNoList") List<String> dueBillNoList);
}
