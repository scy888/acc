package com.weshare.loan.repo;

import com.weshare.loan.entity.CriticalDataHash;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.loan.repo
 * @date: 2021-05-19 10:37:56
 * @describe:
 */
public interface CriticalDataHashRepo extends JpaRepository<CriticalDataHash,String> {
    CriticalDataHash findByUserId(String userId);

    CriticalDataHash findByDueBillNo(String dueBillNo);
}
