package common;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author: scyang
 * @program: tensquare_parent
 * @package: common
 * @date: 2020-02-13 17:43:37
 * @describe: 出现原因
 */
public enum CaseReasonEnum {
    CASE_REASON_ONE(new String[]{"CCB","中国建设银行"}, Arrays.asList(new String[]{"建设银风险危机","台风","海啸"})),
    CASE_REASON_TWO(new String[]{"ABC","中国农业银行"}, Arrays.asList(new String[]{"农业银风险危机","泥石流","坠落"})),
    CASE_REASON_THREE(new String[]{"ICBC","中国工商银行"}, Arrays.asList(new String[]{"工商银风险危机","车祸","踩踏事故"})),
    CASE_REASON_FOUR(new String[]{"CMB","中国招商银行"}, Arrays.asList(new String[]{"招商银风险危机","干旱","水灾"}));

   private String[] statusCodeArray;
   private List<String> nameDescList;


    CaseReasonEnum(String[] statusCodeArray, List<String> nameDescList) {
        this.statusCodeArray = statusCodeArray;
        this.nameDescList = nameDescList;
    }

    public String[] getStatusCodeArray() {
        return statusCodeArray;
    }

    public void setStatusCodeArray(String[] statusCodeArray) {
        this.statusCodeArray = statusCodeArray;
    }

    public List<String> getNameDescList() {
        return nameDescList;
    }

    public void setNameDescList(List<String> nameDescList) {
        this.nameDescList = nameDescList;
    }

    public static CaseReasonEnum getEnumByStatusCode(String statusCode){
        /**
         * @Description: 根据状态码获取枚举
         * @methodName: getEnumByStatusCode
         * @Param: [statusCode]
         * @return: common.CaseReasonEnum
         * @Author: scyang
         * @Date: 2020/2/13 18:00
         */
        CaseReasonEnum[] values = CaseReasonEnum.values();
        for (CaseReasonEnum caseReasonEnum : values) {
            if (Arrays.asList(caseReasonEnum.statusCodeArray).contains(statusCode)){
                return caseReasonEnum;
            }
        }
        return null;
    }
    public static CaseReasonEnum getEnumByNameDesc(String nameDesc){
        /**
         * @Description: 根据名称获取枚举
         * @methodName: getEnumByNameDesc
         * @Param: [nameDesc]
         * @return: common.CaseReasonEnum
         * @Author: scyang
         * @Date: 2020/2/13 18:16
         */
        CaseReasonEnum reasonEnum=null;
        for (CaseReasonEnum caseReasonEnum : CaseReasonEnum.values()) {
            if (caseReasonEnum.nameDescList.contains(nameDesc)){
                reasonEnum=caseReasonEnum;
                break;
            }
        }
        return reasonEnum;
    }

}
