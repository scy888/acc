package com.weshare.batch.task.repo;

import com.weshare.batch.task.entity.BatchJobControl;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.task.repo
 * @date: 2021-06-17 09:51:28
 * @describe:
 */
public interface BatchJobControlRepo extends JpaRepository<BatchJobControl, String> {
    BatchJobControl findByjobName(String jobName);
}
