package com.weshare.adapter.repo;

import com.weshare.adapter.entity.RefundTicket;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.adapter.repo
 * @date: 2021-05-30 17:31:12
 * @describe:
 */
public interface RefundTicketRepo extends JpaRepository<RefundTicket, String> {
    RefundTicket findByDueBillNoAndRefundStatus(String dueBillNo, RefundTicket.RefundStatusEnum refundStatus);
}
