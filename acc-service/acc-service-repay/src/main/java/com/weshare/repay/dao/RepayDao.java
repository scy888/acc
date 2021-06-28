package com.weshare.repay.dao;

import com.weshare.repay.entity.RepayPlan;
import com.weshare.service.api.enums.AssetStatusEnum;
import com.weshare.service.api.enums.TermStatusEnum;
import com.weshare.service.api.result.DataCheckResult;
import com.weshare.service.api.vo.Tuple3;
import com.weshare.service.api.vo.Tuple4;
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
import java.util.Optional;

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

    public List<Tuple3<String, BigDecimal, BigDecimal>> getBoth(String projectNo) {
        String sql = "select m.due_bill_no,m.sum_repay_amount,n.sum_amount from \n" +
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

    /**
     * 校验用户还款主信息表放款金额是否等于还款计划表应还本金之和
     * first:借据号,second:应还本金之和,third:放款金额
     *
     * @param projectNo
     * @return
     */
    public List<Tuple3<String, BigDecimal, BigDecimal>> checkLoanAmount(String projectNo) {
        String sql = "select m.due_bill_no,m.sum_term_prin,n.contract_amount from\n" +
                "(select b.due_bill_no,sum(b.term_prin) sum_term_prin from acc_repay.repay_plan b where b.project_no=? group by b.due_bill_no) m\n" +
                "left join\n" +
                "(select a.due_bill_no,a.contract_amount from acc_repay.repay_summary a where a.project_no=?) n\n" +
                "on m.due_bill_no=n.due_bill_no where m.sum_term_prin!=ifnull(n.contract_amount,0)";

        return jdbcTemplate.query(sql, new RowMapper<>() {
            @Override
            public Tuple3<String, BigDecimal, BigDecimal> mapRow(ResultSet rs, int i) throws SQLException {
                return Tuple3.of(rs.getString(1), rs.getBigDecimal(2), rs.getBigDecimal(3) == null ? BigDecimal.ZERO : rs.getBigDecimal(3));
            }
        }, projectNo, projectNo);
    }

    /**
     * 校验用户还款主信息表剩余本金是否等于还款计划表剩余本金之和
     * first:借据号,second:还款计划剩余本金之和,third:剩余本金
     *
     * @param projectNo
     * @return
     */
    public List<Tuple3<String, BigDecimal, BigDecimal>> checkRemainPrin(String projectNo) {
        String sql = "select m.due_bill_no,m.sum_remain_prin,n.remain_principal from\n" +
                "(select b.due_bill_no,sum(b.term_prin - b.term_repay_prin) sum_remain_prin from acc_repay.repay_plan b where b.project_no=? group by b.due_bill_no) m\n" +
                "left join\n" +
                "(select a.due_bill_no,a.remain_principal from acc_repay.repay_summary a where a.project_no=?) n\n" +
                "on m.due_bill_no=n.due_bill_no where m.sum_remain_prin!=n.remain_principal or n.due_bill_no is null";
        return jdbcTemplate.query(sql, new RowMapper<Tuple3<String, BigDecimal, BigDecimal>>() {
            @Override
            public Tuple3<String, BigDecimal, BigDecimal> mapRow(ResultSet rs, int i) throws SQLException {
                return Tuple3.of(rs.getString(1), rs.getBigDecimal(2), Optional.ofNullable(rs.getBigDecimal(3)).orElse(BigDecimal.ZERO));
            }
        }, projectNo, projectNo);
    }

    /**
     * 校验还款计划的已还金额是否等于实还和还款流水的扣款金额
     * first:借据号,second:还款计划已还金额,third:还款流水金额,fourth实还金额
     *
     * @param projectNo
     * @return
     */
    public List<Tuple4<String, BigDecimal, BigDecimal, BigDecimal>> checkActualAmount(String projectNo) {
        String sql = "select k.due_bill_no,k.sum_actual_repay,m.sum_trans_amount,n.sum_amount from\n" +
                "(select a.due_bill_no,sum(a.term_repay_prin+a.term_repay_int+a.term_repay_penalty+a.term_reduce_int) sum_actual_repay from acc_repay.repay_plan a where a.project_no=? group by a.due_bill_no) k\n" +
                "left join\n" +
                "(select b.due_bill_no,sum(b.trans_amount) sum_trans_amount from acc_repay.repay_trans_flow b where b.project_no=? group by b.due_bill_no) m\n" +
                "on k.due_bill_no=m.due_bill_no\n" +
                "left join\n" +
                "(select c.due_bill_no,sum(c.amount) sum_amount from acc_repay.receipt_detail c where c.project_no=? group by c.due_bill_no) n\n" +
                "on k.due_bill_no=n.due_bill_no\n" +
                "where (k.sum_actual_repay!=ifnull(m.sum_trans_amount,0) or k.sum_actual_repay!=ifnull(n.sum_amount,0));";
        return jdbcTemplate.query(sql, new RowMapper<Tuple4<String, BigDecimal, BigDecimal, BigDecimal>>() {
            @Override
            public Tuple4<String, BigDecimal, BigDecimal, BigDecimal> mapRow(ResultSet rs, int i) throws SQLException {
                return Tuple4.of(rs.getString(1),
                        rs.getBigDecimal(2),
                        Optional.ofNullable(rs.getBigDecimal(3)).orElse(BigDecimal.ZERO),
                        Optional.ofNullable(rs.getBigDecimal(4)).orElse(BigDecimal.ZERO));
            }
        }, projectNo, projectNo, projectNo);
    }

    /**
     * 校验还款流水表和实还表的流水号是否一致
     * first:借据号,second:流水号,third:流水金额,fourth实还金额
     *
     * @param projectNo
     * @return
     */
    public List<Tuple4<String, String, BigDecimal, BigDecimal>> checkFlowSn(String projectNo) {
        String sql = "select m.due_bill_no,m.flow_sn,m.trans_amount,n.sum_amount from\n" +
                "(select a.due_bill_no,a.flow_sn,a.trans_amount from acc_repay.repay_trans_flow a where a.project_no=? group by a.due_bill_no,a.flow_sn) m,\n" +
                "(select b.due_bill_no,b.flow_sn,sum(b.amount) sum_amount from acc_repay.receipt_detail b where b.project_no=? group by b.due_bill_no,b.flow_sn) n\n" +
                "where m.due_bill_no=n.due_bill_no and m.flow_sn=n.flow_sn and m.trans_amount!=n.sum_amount";
        return jdbcTemplate.query(sql, new RowMapper<Tuple4<String, String, BigDecimal, BigDecimal>>() {
            @Override
            public Tuple4<String, String, BigDecimal, BigDecimal> mapRow(ResultSet rs, int i) throws SQLException {
                return Tuple4.of(rs.getString(1), rs.getString(2), rs.getBigDecimal(3), rs.getBigDecimal(4));
            }
        }, projectNo, projectNo);
    }

    /**
     * 校验还款主信息表的期次是否等于还款计划的的总期次
     * first:借据号,second:还款计划总期次,third:还款主信息总期次
     *
     * @param projectNo
     * @return
     */
    public List<Tuple3<String, Integer, Integer>> checkTotalTerm(String projectNo) {
        String sql = "select m.due_bill_no,m.count_count,a.total_term from acc_repay.repay_summary a\n" +
                " right join\n" +
                " (select b.due_bill_no,count(*) count_count from acc_repay.repay_plan b where b.project_no=? group by b.due_bill_no) m\n" +
                " on a.project_no=? and m.due_bill_no=a.due_bill_no where m.count_count!=ifnull(a.total_term,0)";
        return jdbcTemplate.query(sql, new RowMapper<Tuple3<String, Integer, Integer>>() {
            @Override
            public Tuple3<String, Integer, Integer> mapRow(ResultSet rs, int i) throws SQLException {
                return Tuple3.of(rs.getString(1), rs.getInt(2), Optional.ofNullable(rs.getInt(3)).orElse(0));
            }
        }, projectNo, projectNo);
    }

    /**
     * 校验用户还款主信息表资产状态为NORMAL时,还款计划表期次状态不能有OVERDUE或全为REPAID
     * first:借据号,second:期次状态,third:资产状态
     *
     * @param projectNo
     * @return
     */
    public List<Tuple3<String, TermStatusEnum, AssetStatusEnum>> checkNoamal(String projectNo) {
        String sql = "select m.due_bill_no,m.term_status,n.asset_status from\n" +
                "(select b.due_bill_no,b.term_status from acc_repay.repay_plan b where b.term_status='OVERDUE' and b.project_no=? group by b.due_bill_no) m\n" +
                "left join\n" +
                "(select a.due_bill_no,a.asset_status from acc_repay.repay_summary a where a.asset_status='NORMAL' and project_no=?) n\n" +
                "on m.due_bill_no=n.due_bill_no\n" +
                "UNION\n" +
                "select m.due_bill_no,m.term_status,n.asset_status from\n" +
                "(select b.due_bill_no,b.term_status,count(*) count_count from acc_repay.repay_plan b where b.term_status='REPAID'and b.project_no=? group by b.due_bill_no) m\n" +
                "left join\n" +
                "(select a.due_bill_no,a.asset_status,a.total_term from acc_repay.repay_summary a where a.asset_status='NORMAL' and a.project_no=?) n\n" +
                "on m.due_bill_no=n.due_bill_no where m.count_count=n.total_term";
        return jdbcTemplate.query(sql, new RowMapper<Tuple3<String, TermStatusEnum, AssetStatusEnum>>() {
            @Override
            public Tuple3<String, TermStatusEnum, AssetStatusEnum> mapRow(ResultSet rs, int i) throws SQLException {
                return Tuple3.of(rs.getString(1), TermStatusEnum.valueOf(rs.getString(2)), rs.getString(3) == null ? null : AssetStatusEnum.valueOf(rs.getString(3)));
            }
        }, projectNo, projectNo, projectNo, projectNo);
    }

    /**
     * 校验还款主信息表资产状态为OVERDUE时,还款计划表期次状态至少有一个为OVERDUE
     * first:借据号,second:期次状态为逾期的总期数,third:期次状态不为为逾期的总期数
     *
     * @param projectNo
     * @return
     */
    public List<Tuple3<String, Integer, Integer>> checkOverdue(String projectNo) {
        String sql = "select m.due_bill_no,m.total_term,n.count_count from\n" +
                "(select a.due_bill_no,a.asset_status,a.total_term from acc_repay.repay_summary a where a.asset_status='OVERDUE' and a.project_no=?) m,\n" +
                "(select b.due_bill_no,b.term_status,count(*) count_count from acc_repay.repay_plan b where b.term_status!='OVERDUE' and b.project_no=? group by b.due_bill_no) n\n" +
                " where m.due_bill_no=n.due_bill_no and m.total_term=n.count_count";
        return jdbcTemplate.query(sql, new RowMapper<Tuple3<String, Integer, Integer>>() {
            @Override
            public Tuple3<String, Integer, Integer> mapRow(ResultSet rs, int i) throws SQLException {
                return Tuple3.of(rs.getString(1), rs.getInt(2), rs.getInt(3));
            }
        }, projectNo, projectNo);
    }

    /**
     * 校验还款主信息资产状态为SETTLED时,还款主信息表的期次状态全为REPAID
     * first:借据号,second:期次状态为已结清的总期数,third:期次状态为已还的总期数
     *
     * @param projectNo
     * @return
     */
    public List<Tuple3<String, Integer, Integer>> checkSettled(String projectNo) {
        String sql = "select m.due_bill_no,m.total_term,n.count_count from\n" +
                "(select a.due_bill_no,a.asset_status,a.total_term from acc_repay.repay_summary a where a.asset_status='SETTLED' and a.project_no=?) m,\n" +
                "(select b.due_bill_no,b.term_status,count(*) count_count from acc_repay.repay_plan b where b.term_status='REPAID' and b.project_no=? group by b.due_bill_no) n \n" +
                "where m.due_bill_no=n.due_bill_no and m.total_term!=n.count_count";
        return jdbcTemplate.query(sql, new RowMapper<Tuple3<String, Integer, Integer>>() {
            @Override
            public Tuple3<String, Integer, Integer> mapRow(ResultSet rs, int i) throws SQLException {
                return Tuple3.of(rs.getString(1), rs.getInt(2), rs.getInt(3));
            }
        }, projectNo, projectNo);
    }

    /**
     * 校验还款计划表的min(overdue_term)!=max(repaid_term)+1
     *
     * @param projectNo
     * @return
     */
    public List<Tuple3<String, Integer, Integer>> checkOverdueSkip(String projectNo) {
        String sql = "select m.due_bill_no,m.overdue_min_term,n.repaid_max_term from\n" +
                "(select a.due_bill_no,a.term_status,min(a.term) overdue_min_term from acc_repay.repay_plan a where a.term_status='OVERDUE'and a.project_no=? group by a.due_bill_no) m,\n" +
                "(select b.due_bill_no,b.term_status,max(b.term) repaid_max_term from acc_repay.repay_plan b where b.term_status='REPAID' and b.project_no=? group by b.due_bill_no) n\n" +
                "where m.due_bill_no=n.due_bill_no and m.overdue_min_term!=n.repaid_max_term+1";
        return jdbcTemplate.query(sql, new RowMapper<Tuple3<String, Integer, Integer>>() {
            @Override
            public Tuple3<String, Integer, Integer> mapRow(ResultSet rs, int i) throws SQLException {
                return Tuple3.of(rs.getString(1), rs.getInt(2), rs.getInt(3));
            }
        }, projectNo, projectNo);
    }

    /**
     * 校验还款计划表的min(undue_term)!=max(max(repaid_term),max(overdue_term))+1
     *
     * @param projectNo
     * @return
     */
    public List<Tuple4<String, Integer, Integer, Integer>> checkUndueSkip(String projectNo) {
        String sql="select k.due_bill_no,k.undue_min_term,m.repaid_max_term,n.overdue_max_term from\n" +
                "(select a.due_bill_no,a.term_status,min(a.term) undue_min_term from acc_repay.repay_plan a where a.term_status='UNDUE' and a.project_no=? group by a.due_bill_no) k\n" +
                "left join\n" +
                "(select a.due_bill_no,a.term_status,max(a.term) repaid_max_term from acc_repay.repay_plan a where a.term_status='REPAID' and a.project_no=? group by a.due_bill_no) m\n" +
                "on k.due_bill_no=m.due_bill_no\n" +
                "left join\n" +
                "(select a.due_bill_no,a.term_status,max(a.term) overdue_max_term from acc_repay.repay_plan a where a.term_status='OVERDUE' and a.project_no=? group by a.due_bill_no) n\n" +
                " on k.due_bill_no=n.due_bill_no where k.undue_min_term!=greatest(ifnull(m.repaid_max_term,0),ifnull(n.overdue_max_term,0))+1";
        return jdbcTemplate.query(sql, new RowMapper<Tuple4<String, Integer, Integer, Integer>>() {
            @Override
            public Tuple4<String, Integer, Integer, Integer> mapRow(ResultSet rs, int i) throws SQLException {
                return Tuple4.of(rs.getString(1),rs.getInt(2),rs.getInt(3),rs.getInt(4));
            }
        },projectNo,projectNo,projectNo);
    }
}
