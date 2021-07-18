package com.weshare.adapter.repo;

import com.weshare.adapter.entity.MsgLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.adapter.repo
 * @date: 2021-07-17 19:08:00
 * @describe:
 */
public interface MsgLogRepo extends JpaRepository<MsgLog, Integer> {

    MsgLog findByOriginalDataLogId(String dataLogId);

    @Query("delete from #{#entityName} where originalDataLogId=:dataLogId")
    @Modifying
    @Transactional
    void deleteMsgLogByOriginalDataLogId(@Param("dataLogId") String dataLogId);
}
