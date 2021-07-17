package com.weshare.adapter.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * @Description
 * @Author chong.xie
 * @Date 2021/5/21 12:08
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class DataMigrationReq {
    /**
     * 接口名
     */
    @NotBlank
    private String serviceId;
    /**
     * 请求发起日期
     */
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
    private LocalDate batchDate;
    /**
     * 项目编号
     */
    @NotNull
    private String projectNo;

    /**
     * 原始数据ID
     */
    private String originalDataLogId;
}
