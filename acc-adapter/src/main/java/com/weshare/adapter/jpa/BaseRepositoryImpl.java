package com.weshare.adapter.jpa;

import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;

/**
 * @author xiewei
 */
@Log4j2
@NoRepositoryBean
public class BaseRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseRepository<T, ID> {

    private EntityManager entityManager;

    private EntityInformation<T, ?> entityInformation;

    private static final Integer BATCH_SIZE = 1000;

    public BaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
        this.entityInformation = entityInformation;
    }

    public BaseRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Integer batchInsert(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        Session session = this.entityManager.unwrap(Session.class);
        session.setJdbcBatchSize(BATCH_SIZE);
        for (T t : list) {
            session.persist(t);
        }
        return list.size();
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Integer batchUpdate(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        Session session = this.entityManager.unwrap(Session.class);
        session.setJdbcBatchSize(BATCH_SIZE);
        for (T t : list) {
            session.update(t);
        }
        return list.size();
    }
}