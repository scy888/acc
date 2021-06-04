package com.weshare.repay.repo;

import com.weshare.repay.entity.RepayPlan;
import com.weshare.service.api.vo.DueBillNoAndTermDueDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.repay.repo
 * @date: 2021-05-29 02:26:43
 * @describe:
 */
public interface RepayPlanRepo extends JpaRepository<RepayPlan, String> {
    RepayPlan findByDueBillNoAndTerm(String dueBillNo, Integer term);

    @Query("select new com.weshare.service.api.vo.DueBillNoAndTermDueDate(t.dueBillNo,t.term,t.termDueDate) from #{#entityName} t where t.dueBillNo in :dueBillNoList")
    List<DueBillNoAndTermDueDate> findByDueBillNoIn(@Param("dueBillNoList") List<String> dueBillNoList);

    List<RepayPlan> findByDueBillNo(String dueBillNo);
}