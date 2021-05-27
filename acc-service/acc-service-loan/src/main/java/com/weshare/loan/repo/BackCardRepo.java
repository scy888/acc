package com.weshare.loan.repo;

import com.weshare.loan.entity.BackCard;
import org.apache.commons.compress.archivers.ar.ArArchiveEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.loan.repo
 * @date: 2021-05-18 19:54:01
 * @describe:
 */
public interface BackCardRepo extends JpaRepository<BackCard,String> {
    BackCard findByBackNum(String backNum);

    List<BackCard> findByDueBillNo(String dueBillNo);
}
