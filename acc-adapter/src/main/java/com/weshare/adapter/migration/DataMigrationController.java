package com.weshare.adapter.migration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.weshare.adapter.entity.DataMigrationReq;
import com.weshare.adapter.entity.InterfaceLog;
import com.weshare.adapter.repo.InterfaceLogRepo;
import com.weshare.service.api.vo.Tuple2;
import com.weshare.service.api.vo.Tuple3;
import common.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    private InterfaceLogRepo interfaceLogRepo;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/dataMigrationQuery")
    public String dataMigrationQuery(@RequestBody @Validated DataMigrationReq dataMigrationReq) {

        @NotBlank String serviceId = dataMigrationReq.getServiceId();
        @NotNull String projectNo = dataMigrationReq.getProjectNo();
        @NotNull LocalDate batchDate = dataMigrationReq.getBatchDate();
        String originalDataLogId = dataMigrationReq.getOriginalDataLogId();

        log.info("请求数据如下:serviceId={},projectNo={},batchDate={},originalDataLogId={}",
                serviceId, projectNo, batchDate, originalDataLogId);
        String sql = "select id,original_req_msg from interface_log where service_id=? and batch_date=?";
        Tuple2<String, String> tuple2 = jdbcTemplate.queryForObject(sql, new RowMapper<Tuple2<String, String>>() {
            @Override
            public Tuple2<String, String> mapRow(ResultSet rs, int i) throws SQLException {
                return Tuple2.of(rs.getString(1), rs.getString(2));
            }
        }, serviceId, batchDate);
        //获取对应的serviceId接口数据
        originalDataLogId = tuple2.getFirst();
        String originalReqMsg = tuple2.getSecond();
        String contentJson = JsonUtil.toJsonNode(originalReqMsg, "content");
        if (contentJson != null && !"".equals(contentJson)) {
            Migration dataMigration = DataMigrationFactory.getDataMigration(serviceId);
            dataMigration.dataMigration(originalReqMsg, batchDate.toString(), originalDataLogId);
            return "success";
        }
        log.info("originalReqMsg json串上的节点content数据为空...");
        return "success";
    }

    @GetMapping("/initInterfaceData")
    @Deprecated(forRemoval = true)
    public String initInterfaceData(@RequestParam String path) throws IOException {
        /**
         * @Description: 落库接口数据
         * @methodName: initInterfaceData
         * @Param: [path]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2021/7/17 19:13
         */
        File[] files = new File(path).listFiles(pathname -> pathname.getName().endsWith(".json"));
        log.info("路径下：{},符合条件的文件个数为：{}个", path, files.length);
        List<String> readAllLines = Files.readAllLines(Paths.get(files[0].getAbsolutePath()));
        List<InterfaceLog> list = JsonUtil.fromJson(String.join(System.lineSeparator(), readAllLines), new TypeReference<List<InterfaceLog>>() {
        });
        interfaceLogRepo.saveAll(
                list.stream().peek(e -> e.setModifyDate(LocalDateTime.now())).collect(Collectors.toList())
        );
        return "接口数据导入成功...";
    }
}
