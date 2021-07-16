package com.weshare.adapter.migration;

import com.weshare.adapter.entity.DataMigrationReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.adapter.migration
 * @date: 2021-07-16 16:14:32
 * @describe:
 */
@RestController
@Slf4j
public class DataMigrationController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public String dataMigrationQuery(@RequestBody @Validated DataMigrationReq dataMigrationReq) {

        return "";
    }
}
