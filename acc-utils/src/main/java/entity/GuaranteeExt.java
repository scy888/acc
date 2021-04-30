package entity;
/**
 * @author: scyang
 * @program: ssm_super
 * @package: com.itheima.pian.dto
 * @date: 2019-10-20 15:00:28
 * @describe: 担保函信息扩展表
 */
public class GuaranteeExt {

    private static final long serialVersionUID = -7142972678616909030L;
    private String guaranteeExtId;//担保函信息扩展主键
    private String guaranteeId;//关联担保函信息主键
    private String extKey;//扩展字段key
    private String extValue;//扩展字段对应的值
    private String shipName;//船舶名
    private String shipReason;//出险原因
    private String shipWeight;//船承受的重量
    private String carName;//车名
    private String carReason;//出险原因
    private String carNo;//车次


    public String getGuaranteeExtId() {
        return guaranteeExtId;
    }

    public void setGuaranteeExtId(String guaranteeExtId) {
        this.guaranteeExtId = guaranteeExtId;
    }

    public String getGuaranteeId() {
        return guaranteeId;
    }

    public void setGuaranteeId(String guaranteeId) {
        this.guaranteeId = guaranteeId;
    }

    public String getExtKey() {
        return extKey;
    }

    public void setExtKey(String extKey) {
        this.extKey = extKey;
    }

    public String getExtValue() {
        return extValue;
    }

    public void setExtValue(String extValue) {
        this.extValue = extValue;
    }

    public String getShipName() {
        return shipName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public String getShipReason() {
        return shipReason;
    }

    public void setShipReason(String shipReason) {
        this.shipReason = shipReason;
    }

    public String getShipWeight() {
        return shipWeight;
    }

    public void setShipWeight(String shipWeight) {
        this.shipWeight = shipWeight;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getCarReason() {
        return carReason;
    }

    public void setCarReason(String carReason) {
        this.carReason = carReason;
    }

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    public GuaranteeExt() {
    }

    /** 主键id,关联id,字段key,字段value,创建者(修改者)*/
    public GuaranteeExt(String guaranteeExtId, String guaranteeId, String extKey, String extValue) {
        this.guaranteeExtId = guaranteeExtId;
        this.guaranteeId = guaranteeId;
        this.extKey = extKey;
        this.extValue = extValue;
    }
}
