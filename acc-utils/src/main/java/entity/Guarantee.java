package entity;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.StringJoiner;

/**
 * @author: scyang
 * @program: ssm_super
 * @package: com.itheima.pian.dto
 * @date: 2019-10-20 11:01:27
 * @describe: 担保函信息表DTO
 */
public class Guarantee implements Serializable {
    private static final long serialVersionUID = -637390711487088007L;
    private String guaranteeId;//担保函信息主键
    private String policyNo;//保单号
    private String insuredName;//被保险人名字
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date accidentDate;//出险时间
    private String accidentDateStr;//出险时间字符串
    private String productClass;//产品大类（船舶险SS,货运险SB）
    private String guaranteeSeriaNo;//保单序列号
    private GuaranteeExt guaranteeExt;//担保函信息扩展DTO

    public String getGuaranteeId() {
        return guaranteeId;
    }

    public void setGuaranteeId(String guaranteeId) {
        this.guaranteeId = guaranteeId;
    }

    public String getPolicyNo() {
        return policyNo;
    }

    public void setPolicyNo(String policyNo) {
        this.policyNo = policyNo;
    }

    public String getInsuredName() {
        return insuredName;
    }

    public void setInsuredName(String insuredName) {
        this.insuredName = insuredName;
    }

    public Date getAccidentDate() {
        return accidentDate;
    }

    public void setAccidentDate(Date accidentDate) {
        this.accidentDate = accidentDate;
    }

    public String getAccidentDateStr() {
        return accidentDateStr;
    }

    public void setAccidentDateStr(String accidentDateStr) {
        this.accidentDateStr = accidentDateStr;
    }

    public String getProductClass() {
        return productClass;
    }

    public void setProductClass(String productClass) {
        this.productClass = productClass;
    }

    public String getGuaranteeSeriaNo() {
        return guaranteeSeriaNo;
    }

    public void setGuaranteeSeriaNo(String guaranteeSeriaNo) {
        this.guaranteeSeriaNo = guaranteeSeriaNo;
    }

    public GuaranteeExt getGuaranteeExt() {
        return guaranteeExt;
    }

    public void setGuaranteeExt(GuaranteeExt guaranteeExt) {
        this.guaranteeExt = guaranteeExt;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Guarantee.class.getSimpleName() + "[", "]")
                .add("guaranteeId='" + guaranteeId + "'")
                .add("policyNo='" + policyNo + "'")
                .add("insuredName='" + insuredName + "'")
                .add("accidentDate=" + accidentDate)
                .add("accidentDateStr='" + accidentDateStr + "'")
                .add("productClass='" + productClass + "'")
                .add("guaranteeSeriaNo='" + guaranteeSeriaNo + "'")
                .add("guaranteeExt=" + guaranteeExt)
                .toString();
    }
}
