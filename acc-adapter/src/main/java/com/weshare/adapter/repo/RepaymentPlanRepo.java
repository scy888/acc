package com.weshare.adapter.repo;

import com.weshare.adapter.entity.RepaymentPlan;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.adapter.repo
 * @date: 2021-07-18 01:17:50
 * @describe:
 */
public interface RepaymentPlanRepo extends JpaRepository<RepaymentPlan, String> {
}
