package com.weshare.adapter.repo;

import com.weshare.adapter.entity.InterfaceLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.adapter.repo
 * @date: 2021-07-17 11:07:24
 * @describe:
 */
public interface InterfaceLogRepo extends JpaRepository<InterfaceLog, String> {
}
