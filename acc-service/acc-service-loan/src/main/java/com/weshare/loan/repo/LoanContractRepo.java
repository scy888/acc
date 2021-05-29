package com.weshare.loan.repo;

import com.weshare.loan.entity.LoanContract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.loan.repo
 * @date: 2021-05-27 14:55:10
 * @describe:
 */
public interface LoanContractRepo extends JpaRepository<LoanContract,String> {
    List<LoanContract> findByDueBillNoIn(List<String> dueBillNoList);

    List<LoanContract> findByProjectNo(String projectNo);
}
