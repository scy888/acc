package com.weshare.loan.repo;

import com.weshare.loan.entity.SysLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.loan.repo
 * @date: 2021-07-04 23:18:48
 * @describe:
 */
public interface SysLogRepayRepo extends JpaRepository<SysLog, String> {
}
