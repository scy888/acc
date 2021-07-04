package com.weshare.loan.dao;

import com.weshare.loan.entity.SysLog;
import com.weshare.loan.entity.UserBase;
import com.weshare.service.api.entity.UserBaseReq;
import common.Md5Utils;
import common.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.loan
 * @date: 2021-05-18 16:12:59
 * @describe:
 */
@Repository
public class LoanDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<UserBase> findUserBaseByDueBillNo(List<String> dueBillNoList) {
        String sql = "select * from user_base where due_bill_no in (:dueBillNoList)";
        List<UserBase> userBases = namedParameterJdbcTemplate.query(sql, Map.of("dueBillNoList", dueBillNoList), new BeanPropertyRowMapper<>(UserBase.class));
        return userBases;
    }

    public void addUserBaseList(List<UserBaseReq> insertList) throws Exception {
        String insertSql = StringUtils.getInsertSql("user_base", UserBaseReq.class, "come_list", "link_man_list", "back_card_list");
        List<Object[]> list = new ArrayList<>();
        for (UserBaseReq userBaseReq : insertList) {
            list.add(StringUtils.getFieldValue(userBaseReq, "comeList", "linkManList", "backCardList"));
        }
        jdbcTemplate.batchUpdate(insertSql, list);
    }

    public void deleteUserBaseByDueBillNoList(List<String> dueBillNoList) {
        String sql = "delete from user_base where due_bill_no in (:dueBillNoList)";
        namedParameterJdbcTemplate.update(sql, Map.of("dueBillNoList", dueBillNoList));
    }

    public void updateUserBaseList(List<UserBaseReq> updateList) {
        String sql = "UPDATE `acc_loan`.`user_base` SET" +
                " `user_id` = ?, `user_name` =?," +
                " `id_card_type` = ?, `id_card_num` = ?," +
                " `iphone` = ?, `car_num` = ?," +
                " `sex` = ?, `project_no` = ?," +
                " `id` = ?, `batch_date` = ?" +
                " where `due_bill_no` = ?";

        jdbcTemplate.batchUpdate(sql, updateList.stream().map(e -> new Object[]{
                e.getUserId(), e.getUserName(),
                e.getIdCardType().name(), e.getIdCardNum(),
                e.getIphone(), e.getCarNum(),
                e.getSex().name(), e.getProjectNo(),
                e.getId(), e.getBatchDate(),
                e.getDueBillNo()
        }).collect(Collectors.toList()));
    }

    public void addSysLog(SysLog sysLog){
        jdbcTemplate.update(StringUtils.getInsertSql(sysLog));
    }
}
