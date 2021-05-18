package com.weshare.loan.repo;

import com.weshare.loan.entity.BackCard;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.loan.repo
 * @date: 2021-05-18 19:54:01
 * @describe:
 */
public interface BackCardRepo extends JpaRepository<BackCard,String> {
    BackCard findByBackNum(String backNum);
}
