package com.weshare.repay.repo;

import com.weshare.repay.entity.RepaySummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.repay.repo
 * @date: 2021-05-29 12:47:38
 * @describe:
 */

public interface RepaySummaryRepo extends JpaRepository<RepaySummary,String> {
    List<RepaySummary> findByDueBillNoIn(List<String> dueBillNoList);
}
