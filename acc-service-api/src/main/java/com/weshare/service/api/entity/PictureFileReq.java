package com.weshare.service.api.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author v_tianwenkai
 * @since 2020/12/29 17:56
 */
@Data
@Accessors(chain = true)

public class PictureFileReq {

    private String dueBillNo;

    private byte[] byteArray;

    private String fileName;

    private LocalDateTime createTime;
}
