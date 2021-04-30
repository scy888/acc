package common;

import entity.Constant;
import entity.Guarantee;
import entity.GuaranteeExt;
import utils.IdWorker;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: scyang
 * @program: ssm_super
 * @package: com.itheima.validator
 * @date: 2019-10-20 17:19:43
 * @describe: 担保函校验规则
 */
public class GuaranteeUtils {

   private IdWorker idWorker=new IdWorker();
    /**
     * 船舶险SS个性化字段:shipName 船名
     */
    public static final String PRODUCT_CLASS_SS_SHIPNAME = "shipName";
    /**
     * 船舶险SS个性化字段:shipReason 出现事由
     */
    public static final String PRODUCT_CLASS_SS_SHIPREASON = "shipReason";
    /**
     * 船舶险SS个性化字段:shipWeight 船的载重量
     */
    public static final String PRODUCT_CLASS_SS_SHIPWEIGHT = "shipWeight";
    /**
     * 车险SB个性化字段:carName 车名
     */
    public static final String PRODUCT_CLASS_SB_CARNAME = "carName";
    /**
     * 货运险SB个性化字段:carReason 出险是由
     */
    public static final String PRODUCT_CLASS_SB_CARREASON = "carReason";
    /**
     * 货运险SB个性化字段:carNo 车次号
     */
    public static final String PRODUCT_CLASS_SB_CARNO= "carNo";

    public static void validatorGuarantee(Guarantee guarantee) {
        /**
         * @Description: 担保函保存时部分字段的非空校验
         * @methodName: validatorGuarantee
         * @Param: [guaranteeDTO]
         * @return: void
         * @Author: scyang
         * @Date: 2019/10/20 17:46
         */
        if (guarantee == null) {
            throw new RuntimeException("担保函信息保存时,担保信息不能为空...");
        }
        /** 担保函信息扩展字段非空校验 */
        else if (null == guarantee.getGuaranteeExt()) {
            throw new RuntimeException("担保函信息保存时,担保信息扩展字段不能为空...");
        }
        /** 保单号,赔案号,报案号非空校验 */
        else if (null == guarantee.getPolicyNo()) {
            throw new RuntimeException("担保函信息保存时,担保信息扩展字段保单号不能为空...");
        }
        /** 担保函发起时间校验 */
        else if (null == guarantee.getAccidentDate()) {
            throw new RuntimeException("担保函信息保存时,出险时间不能为空...");
        } else if (StringUtils.isEmpyStr(guarantee.getProductClass())) {
            throw new RuntimeException("担保函信息保存时,担保函产品大类不能为空...");
        }
    }

    public static GuaranteeExt buillGuaranteeExt(List<GuaranteeExt> extDTOList, String productClass) {
        /**
         * @Description: 根据数据库获取担保函信息扩展DTO的list(key - value)和产品大类编码组装成该担保函信息扩展GuaranteeExtDTO
         * @methodName: buillGuaranteeExtDTO
         * @Param: [extDTOList, productClassCode]
         * @return: com.itheima.pian.dto.GuaranteeExtDTO
         * @Author: scyang
         * @Date: 2019/10/20 18:39
         */
        GuaranteeExt guaranteeExt = new GuaranteeExt();
        if (extDTOList.isEmpty()) {
            return guaranteeExt;
        }
        for (GuaranteeExt extDTO : extDTOList) {
            if (Constant.PRODUCT_TYPE_SS.equals(productClass)) {
               if (PRODUCT_CLASS_SS_SHIPNAME.equals(extDTO.getExtKey())) {
                    guaranteeExt.setGuaranteeExtId(extDTO.getGuaranteeExtId());
                    guaranteeExt.setGuaranteeId(extDTO.getGuaranteeId());
                    guaranteeExt.setExtKey(extDTO.getExtKey());
                    guaranteeExt.setExtValue(extDTO.getExtValue());
                } else if (PRODUCT_CLASS_SS_SHIPREASON.equals(extDTO.getExtKey())) {
                    guaranteeExt.setGuaranteeExtId(extDTO.getGuaranteeExtId());
                    guaranteeExt.setGuaranteeId(extDTO.getGuaranteeId());
                    guaranteeExt.setExtKey(extDTO.getExtKey());
                    guaranteeExt.setExtValue(extDTO.getExtValue());
                } else if (PRODUCT_CLASS_SS_SHIPWEIGHT.equals(extDTO.getExtKey())) {
                    guaranteeExt.setGuaranteeExtId(extDTO.getGuaranteeExtId());
                    guaranteeExt.setGuaranteeId(extDTO.getGuaranteeId());
                    guaranteeExt.setExtKey(extDTO.getExtKey());
                    guaranteeExt.setExtValue(extDTO.getExtValue());
                }

            } else if (Constant.PRODUCT_TYPE_SB.equals(productClass)) {
               if (PRODUCT_CLASS_SB_CARNAME.equals(extDTO.getExtKey())) {
                    guaranteeExt.setGuaranteeExtId(extDTO.getGuaranteeExtId());
                    guaranteeExt.setGuaranteeId(extDTO.getGuaranteeId());
                    guaranteeExt.setExtKey(extDTO.getExtKey());
                    guaranteeExt.setExtValue(extDTO.getExtValue());
                } else if (PRODUCT_CLASS_SB_CARREASON.equals(extDTO.getExtKey())) {
                    guaranteeExt.setGuaranteeExtId(extDTO.getGuaranteeExtId());
                    guaranteeExt.setGuaranteeId(extDTO.getGuaranteeId());
                    guaranteeExt.setExtKey(extDTO.getExtKey());
                    guaranteeExt.setExtValue(extDTO.getExtValue());
                } else if (PRODUCT_CLASS_SB_CARNO.equals(extDTO.getExtKey())) {
                    guaranteeExt.setGuaranteeExtId(extDTO.getGuaranteeExtId());
                    guaranteeExt.setGuaranteeId(extDTO.getGuaranteeId());
                    guaranteeExt.setExtKey(extDTO.getExtKey());
                    guaranteeExt.setExtValue(extDTO.getExtValue());
                }
            }
        }
        return guaranteeExt;
    }

    public static List<GuaranteeExt> buillGuaranteeExtList(Guarantee guarantee) {
        /**
         * @Description: 根据担保函信息组装担保函信息扩展字表集合
         * @methodName: buillGuaranteeExtDTOList
         * @Param: [guaranteeDTO]
         * @return: java.util.List<com.itheima.pian.dto.GuaranteeExtDTO>
         * @Author: scyang
         * @Date: 2019/10/20 19:03
         */
        List<GuaranteeExt> guaranteeExtList = new ArrayList<>();
        if (null == guarantee) {
            return guaranteeExtList;
        }
        /** 船舶险 */
        if (Constant.PRODUCT_TYPE_SS.equals(guarantee.getProductClass())) {
            /** 船舶名 */
            GuaranteeExt extShipName = new GuaranteeExt(new GuaranteeUtils().idWorker.nextId()+"", guarantee.getGuaranteeId(), PRODUCT_CLASS_SS_SHIPNAME, guarantee.getGuaranteeExt().getShipName());
            guaranteeExtList.add(extShipName);
            /** 出险事由 */
            GuaranteeExt extShipReason = new GuaranteeExt(new GuaranteeUtils().idWorker.nextId()+"", guarantee.getGuaranteeId(), PRODUCT_CLASS_SS_SHIPREASON, guarantee.getGuaranteeExt().getShipReason());
            guaranteeExtList.add(extShipReason);
            /** 船的承载量 */
            GuaranteeExt extShipWeight = new GuaranteeExt(new GuaranteeUtils().idWorker.nextId()+"", guarantee.getGuaranteeId(), PRODUCT_CLASS_SS_SHIPWEIGHT, guarantee.getGuaranteeExt().getShipWeight());
            guaranteeExtList.add(extShipWeight);
        }
          /** 车险*/
        if (Constant.PRODUCT_TYPE_SB.equals(guarantee.getProductClass())) {
            /** 车名*/
            GuaranteeExt extCarName = new GuaranteeExt(new GuaranteeUtils().idWorker.nextId()+"", guarantee.getGuaranteeId(), PRODUCT_CLASS_SB_CARNAME, guarantee.getGuaranteeExt().getCarName());
            guaranteeExtList.add(extCarName);
            /** 出险原因*/
            GuaranteeExt extCarReason = new GuaranteeExt(new GuaranteeUtils().idWorker.nextId()+"", guarantee.getGuaranteeId(), PRODUCT_CLASS_SB_CARREASON, guarantee.getGuaranteeExt().getCarReason());
            guaranteeExtList.add(extCarReason);
            /** 车次号 */
            GuaranteeExt extCarNo = new GuaranteeExt(new GuaranteeUtils().idWorker.nextId()+"", guarantee.getGuaranteeId(), PRODUCT_CLASS_SB_CARNO, guarantee.getGuaranteeExt().getCarNo());
            guaranteeExtList.add(extCarNo);
        }
        return guaranteeExtList;
    }
}
