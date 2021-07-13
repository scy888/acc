package com.weshare.adapter.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

/**
 * @author xiewei
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    /**
     * 批量插入
     * @param list
     * @return
     */
    Integer batchInsert(List<T> list);

    /**
     * 批量更新
     * @param list
     * @return
     */
    Integer batchUpdate(List<T> list);

}
