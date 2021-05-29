package com.weshare.repay.repo;

import com.weshare.repay.entity.RepayPlan;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.repay.repo
 * @date: 2021-05-29 02:26:43
 * @describe:
 */
public interface RepayPlanRepo extends JpaRepository<RepayPlan,String> {
    RepayPlan findByDueBillNoAndTerm(String dueBillNo, Integer term);
}
