package com.weshare.repay.dao;

import com.weshare.repay.entity.RepayPlan;
import com.weshare.service.api.enums.TermStatusEnum;
import com.weshare.service.api.vo.Tuple3;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.repay.dao
 * @date: 2021-06-07 20:40:03
 * @describe:
 */
@Repository
public class RepayDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public RepayPlan getRepayPlan(String dueBillNo, Integer term) {

        String sql = "select * from acc_repay.repay_plan where due_bill_no=:dueBillNo and term=:term";
        //RepayPlan repayPlan = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(RepayPlan.class), dueBillNo, term);
        RepayPlan repayPlan = namedParameterJdbcTemplate.queryForObject(sql, Map.ofEntries(Map.entry("dueBillNo", dueBillNo), Map.entry("term", term)), new BeanPropertyRowMapper<>(RepayPlan.class));
        return repayPlan;
    }

    public Map<String, Object> getRepayPlan(Integer term, String dueBillNo) {
        String sql = "select * from acc_repay.repay_plan where due_bill_no=:dueBillNo and term=:term";
        // Map<String, Object> map = jdbcTemplate.queryForMap(sql, dueBillNo, term);
        Map<String, Object> map = namedParameterJdbcTemplate.queryForMap(sql, Map.of("term", term, "dueBillNo", dueBillNo));
        return map;
    }

    public List<RepayPlan> getRepayPlanList(String dueBillNo, List<Integer> terms) {
        String sql = "select * from acc_repay.repay_plan where due_bill_no=:dueBillNo and term in (:terms)";
        List<RepayPlan> list = namedParameterJdbcTemplate.query(sql, Map.of("terms", terms, "dueBillNo", dueBillNo), new BeanPropertyRowMapper<>(RepayPlan.class));
        return list;
    }

    public List<Map<String, Object>> getRepayPlanList(List<Integer> terms, String dueBillNo) {
        String sql = "select * from acc_repay.repay_plan where due_bill_no=:dueBillNo and term in (:terms)";
        List<Map<String, Object>> mapList = namedParameterJdbcTemplate.queryForList(sql, Map.of("terms", terms, "dueBillNo", dueBillNo));
        return mapList;
    }

    public Tuple3<LocalDate, BigDecimal, TermStatusEnum> getTuple3(String dueBillNo, Integer term) {
        String sql = "select repay_date,term_bill_amount,term_status from acc_repay.repay_plan where due_bill_no=? and term=?";
        Tuple3<LocalDate, BigDecimal, TermStatusEnum> tuple3 = jdbcTemplate.queryForObject(sql, new RowMapper<>() {
            @Override
            public Tuple3<LocalDate, BigDecimal, TermStatusEnum> mapRow(ResultSet rs, int i) throws SQLException {
                return Tuple3.of(rs.getObject(1, LocalDate.class), rs.getBigDecimal(2), Enum.valueOf(TermStatusEnum.class, rs.getString(3)));
            }
        }, dueBillNo, term);
        return tuple3;
    }

    public List<Tuple3<LocalDate, BigDecimal, TermStatusEnum>> getTuple3(String dueBillNo, List<Integer> terms) {
        String sql = "select repay_date,term_bill_amount,term_status from acc_repay.repay_plan where due_bill_no=:dueBillNo and term in (:terms)";
        List<Tuple3<LocalDate, BigDecimal, TermStatusEnum>> tuple3s = namedParameterJdbcTemplate.query(sql, Map.of("dueBillNo", dueBillNo, "terms", terms), new RowMapper<>() {
            @Override
            public Tuple3<LocalDate, BigDecimal, TermStatusEnum> mapRow(ResultSet rs, int i) throws SQLException {

                return Tuple3.of(rs.getObject(1, LocalDate.class), rs.getBigDecimal(2), TermStatusEnum.valueOf(rs.getString(3)));
            }
        });
        return tuple3s;
    }

    public List<Repay> getRepayList(String dueBillNo, List<Integer> terms) {
        String sql = "select repay_date repay_one,term_bill_amount repayTwo,term_status repay_three from acc_repay.repay_plan where due_bill_no=:dueBillNo and term in (:terms)";
        //List<Repay> list = namedParameterJdbcTemplate.query(sql, Map.of("dueBillNo", dueBillNo, "terms", terms), new BeanPropertyRowMapper<>(Repay.class));
        List<Repay> list = namedParameterJdbcTemplate.query(sql, Map.of("dueBillNo", dueBillNo, "terms", terms), new RowMapper<Repay>() {
            @Override
            public Repay mapRow(ResultSet rs, int i) throws SQLException {
                return new Repay(LocalDate.parse(rs.getString(1)), new BigDecimal(rs.getString(2)), TermStatusEnum.valueOf(rs.getString(3)));
            }
        });
        return list;
    }

    public List<Map<String, Object>> getMapList(String dueBillNo, List<Integer> terms) {
        String sql = "select repay_date,term_bill_amount,term_status from acc_repay.repay_plan where due_bill_no=:dueBillNo and term in (:terms)";
        List<Map<String, Object>> mapList = namedParameterJdbcTemplate.query(sql, Map.of("dueBillNo", dueBillNo, "terms", terms), new RowMapper<>() {
            @Override
            public Map<String, Object> mapRow(ResultSet rs, int i) throws SQLException {
                Map<String, Object> map = Map.of("a", rs.getObject(1, LocalDate.class), "b", rs.getBigDecimal(2), "c", TermStatusEnum.valueOf(rs.getString(3)));
                return map;
            }
        });
        return mapList;
    }
    @Value
    public static class Repay {
        private LocalDate repayOne;
        private BigDecimal repayTwo;
        private TermStatusEnum repayThree;
    }

    public List<Tuple3<String,BigDecimal,BigDecimal>> getBoth(String projectNo){
        String sql="select m.due_bill_no,m.sum_repay_amount,n.sum_amount from \n" +
                "(select due_bill_no, sum(term_repay_int+term_repay_prin+term_repay_penalty+term_reduce_int) sum_repay_amount from acc_repay.repay_plan where project_no=:projectNo group by due_bill_no) m left join\n" +
                "(select due_bill_no,sum(amount) sum_amount from acc_repay.receipt_detail where project_no=:projectNo group by due_bill_no) n on m.due_bill_no=n.due_bill_no where m.sum_repay_amount!=n.sum_amount or n.due_bill_no is null\n" +
                "UNION \n" +
                "select n.due_bill_no,m.sum_repay_amount,n.sum_amount from \n" +
                "(select due_bill_no, sum(term_repay_int+term_repay_prin+term_repay_penalty+term_reduce_int) sum_repay_amount from acc_repay.repay_plan where project_no=:projectNo group by due_bill_no) m right join\n" +
                "(select due_bill_no,sum(amount) sum_amount from acc_repay.receipt_detail where project_no=:projectNo group by due_bill_no) n on m.due_bill_no=n.due_bill_no where m.sum_repay_amount!=n.sum_amount or m.due_bill_no is null";

        List<Tuple3<String, BigDecimal, BigDecimal>> tuple3List = namedParameterJdbcTemplate.query(sql, Map.of("projectNo", projectNo), new RowMapper<>() {
            @Override
            public Tuple3<String, BigDecimal, BigDecimal> mapRow(ResultSet rs, int i) throws SQLException {
                return Tuple3.of(rs.getString(1), rs.getBigDecimal(2), rs.getBigDecimal(3));
            }
        });
        return tuple3List;
    }
}
