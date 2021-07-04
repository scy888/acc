package com.weshare.adapter.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author: scyang
 * @program: tensquare_parent
 * @date: 2019-09-20 22:51:18
 */
@Entity
@Data
@Accessors(chain = true)
public class SysLog implements Serializable {
    @Id
    private String id;

    private String ip;

    private String uri;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private BigDecimal lostTime;

    private String className;

    private String methodName;

    private String paramsType;

    private String paramsName;

    private String paramsValue;

    private String returnClassName;

    private String returnValue;

    private String methodDesc;

}
