package com.weshare.adapter.repo;

import com.weshare.adapter.entity.RefundTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.adapter.repo
 * @date: 2021-05-30 17:31:12
 * @describe:
 */
public interface RefundTicketRepo extends JpaRepository<RefundTicket, String> {
    RefundTicket findByDueBillNoAndRefundStatus(String dueBillNo, RefundTicket.RefundStatusEnum refundStatus);

    @Query("delete from #{#entityName} where dueBillNo in :dueBillNoList")
    @Modifying
    @Transactional
    void deleteByDueBillNoList(@Param("dueBillNoList") List<String> dueBillNoList);
}
