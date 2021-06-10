package com.weshare.repay.repo;

import com.weshare.repay.entity.PictureFile;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.repay.repo
 * @date: 2021-06-10 11:35:31
 * @describe:
 */
public interface PictureFileRepo extends JpaRepository<PictureFile, String> {
}
