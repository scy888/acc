package com.weshare.batch.task.repo;

import com.weshare.batch.task.entity.TaskRunLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.task.repo
 * @date: 2021-06-12 14:09:01
 * @describe:
 */
public interface TaskRunLogRepo extends JpaRepository<TaskRunLog, Integer> {
}
