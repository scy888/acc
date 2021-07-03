package com.weshare.service.api.result;

import com.weshare.service.api.enums.DataCheckType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.service.api.result
 * @date: 2021-06-28 11:09:40
 * @describe:
 */
@Data
@Accessors(chain = true)
public class DataCheckResult implements Serializable {

    private DataCheckType name;
    private String desc;
    private Boolean isPass;
    private Integer errorCount;
    private List<String> dueBillNoList;
    private String remark;

    public DataCheckResult(DataCheckType name, Integer errorCount) {
        this.name = name;
        this.desc = name.getDesc();
        this.errorCount = errorCount;
        this.isPass = errorCount == 0;
        this.remark = isPass ? "校验通过" : "校验不通过,异常数据量:" + this.errorCount + "条";
    }

    public static DataCheckResult dataCheckResult(DataCheckType name, Integer errorCount, List<String> dueBillNoList, String remark) {
        DataCheckResult dataCheckResult = new DataCheckResult(name, errorCount);
        dataCheckResult.setDueBillNoList(dueBillNoList);
        dataCheckResult.setRemark(dataCheckResult.isPass ? dataCheckResult.getRemark() : dataCheckResult.getRemark() + "," + remark);
        return dataCheckResult;
    }
}
