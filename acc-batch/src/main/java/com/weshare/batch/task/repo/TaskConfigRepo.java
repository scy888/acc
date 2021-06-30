package com.weshare.batch.task.repo;

import com.weshare.batch.task.entity.TaskConfig;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.task.repo
 * @date: 2021-06-12 14:06:07
 * @describe:
 */
public interface TaskConfigRepo extends JpaRepository<TaskConfig, String> {
    TaskConfig findByTaskName(String taskName);
}
