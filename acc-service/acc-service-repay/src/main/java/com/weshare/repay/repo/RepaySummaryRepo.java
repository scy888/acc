package com.weshare.repay.repo;

import com.weshare.repay.entity.RepaySummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.repay.repo
 * @date: 2021-05-29 12:47:38
 * @describe:
 */

public interface RepaySummaryRepo extends JpaRepository<RepaySummary, String> {
    List<RepaySummary> findByDueBillNoIn(List<String> dueBillNoList);

    int countByProjectNo(String projectNo);

    @Query("select t.dueBillNo from #{#entityName} t where t.projectNo=projectNo")
    Page<String> findByProjectNo(String projectNo, PageRequest pageRequest);

    @Query("select t.userId from #{#entityName} t where t.dueBillNo=:dueBillNo")
    String findByDueBillNo_(@Param("dueBillNo") String dueBillNo);

    RepaySummary findByDueBillNo(String dueBillNo);

    @Query("select e.totalTerm from #{#entityName} e where e.dueBillNo=:dueBillNo and e.projectNo=:projectNo")
    Integer findByDueBillNoAndProjectNo(String dueBillNo, String projectNo);
}
