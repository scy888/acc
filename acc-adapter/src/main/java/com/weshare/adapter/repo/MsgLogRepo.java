package com.weshare.adapter.repo;

import com.weshare.adapter.entity.MsgLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.adapter.repo
 * @date: 2021-07-17 19:08:00
 * @describe:
 */
public interface MsgLogRepo extends JpaRepository<MsgLog,Integer> {
    MsgLog findByOriginalDataLogId(String dataLogId);
}
