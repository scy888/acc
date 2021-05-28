package com.weshare.adapter.dao;

import com.weshare.adapter.entity.RepaymentPlan;
import com.weshare.service.api.entity.RepaymentPlanReq;
import common.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.adapter.dao
 * @date: 2021-05-28 22:37:14
 * @describe:
 */
@Repository
@Slf4j
public class AdapterDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<RepaymentPlan> findByDueBillNoAndTerm(List<? extends RepaymentPlanReq> list) {
        if (!list.isEmpty()) {
            String sql = "select * from acc_adapter.repayment_plan where (due_bill_no,term) in " +
                    list.stream().map(e -> "(" + "?" + "," + "?" + ")").collect(Collectors.joining(",", "(", ")"));

            List<Object> objectList = new ArrayList<>();
            for (RepaymentPlanReq req : list) {
                objectList.add(req.getDueBillNo());
                objectList.add(req.getTerm());
            }
            List<RepaymentPlan> planList = jdbcTemplate.query(sql,
                    new BeanPropertyRowMapper<>(RepaymentPlan.class),
                    objectList.toArray(new Object[objectList.size()]));
            log.info("findByDueBillNoAndTerm=>sql:{}", sql);
            return planList;
        }
        return new ArrayList<RepaymentPlan>();
    }

    public List<RepaymentPlan> findByDueBillNoAndTerm_(List<? extends RepaymentPlanReq> list) {
        if (!list.isEmpty()) {
            String sql = "select * from acc_adapter.repayment_plan where (due_bill_no,term) in " +
                    list.stream().map(e -> "('" + e.getDueBillNo() + "','" + e.getTerm() + "')").collect(Collectors.joining(",", "(", ")"));

            List<RepaymentPlan> planList = jdbcTemplate.query(sql,
                    new BeanPropertyRowMapper<>(RepaymentPlan.class)
            );
            log.info("findByDueBillNoAndTerm=>sql:{}", sql);
            return planList;
        }
        return new ArrayList<RepaymentPlan>();
    }

    public void updateRepaymentPlan(RepaymentPlan repaymentPlan) {
        String sql = "update acc_adapter.repayment_plan set repayment_date=?," +
                "should_month_money=?," +
                "should_capital_money=?," +
                "should_interest_money=?," +
                "batch_date=?," +
                "last_modified_date=? " +
                "where (due_bill_no,term) in ((?,?))";
        jdbcTemplate.update(sql, repaymentPlan.getRepaymentDate(), repaymentPlan.getShouldMonthMoney(), repaymentPlan.getShouldCapitalMoney(),
                repaymentPlan.getShouldInterestMoney(), repaymentPlan.getBatchDate(), repaymentPlan.getLastModifiedDate(),
                repaymentPlan.getDueBillNo(), repaymentPlan.getTerm());
    }

    public void insertRepaymentPlan(RepaymentPlan repaymentPlan) {
        String sql = StringUtils.getInsertSql("acc_adapter.repayment_plan", RepaymentPlan.class);
        try {
            Object[] objects = StringUtils.getFieldValue(repaymentPlan);
            jdbcTemplate.update(sql, objects);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
