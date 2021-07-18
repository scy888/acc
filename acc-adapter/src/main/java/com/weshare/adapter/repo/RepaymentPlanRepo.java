package com.weshare.adapter.repo;

import com.weshare.adapter.entity.RepaymentPlan;
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
 * @date: 2021-07-18 01:17:50
 * @describe:
 */
public interface RepaymentPlanRepo extends JpaRepository<RepaymentPlan, String> {
    @Query("delete from #{#entityName} where dueBillNo in :dueBillNoList")
    @Modifying
    @Transactional
    void deleteByDueBillNoList(@Param("dueBillNoList") List<String> dueBillNoList);
}
