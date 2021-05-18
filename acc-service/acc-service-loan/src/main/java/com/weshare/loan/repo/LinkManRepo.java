package com.weshare.loan.repo;

import com.weshare.loan.entity.LinkMan;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.loan.repo
 * @date: 2021-05-18 19:50:17
 * @describe:
 */

public interface LinkManRepo extends JpaRepository<LinkMan, String> {
    LinkMan findByIdCardNum(String idCardNum);
}
