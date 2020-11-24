package com.jd.bluedragon.distribution.busineCode;

import com.jd.bluedragon.distribution.businessCode.BusinessCodeFromSourceEnum;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeNodeTypeEnum;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeAttributeKey;
import com.jd.bluedragon.distribution.businessCode.domain.BusinessCodePo;

import java.util.Map;

/**
 * 业务单号表相关的服务
 * business_code 创建为了后续业务系统中保存各种生成单号的记录
 */
public interface BusinessCodeManager {

    /**
     * 持久化businessCode 业务单号到表中，并保存相关的业务单号的属性
     * @param businessCode 业务单号
     * @param businessCodeNodeTypeEnum 业务单号类型
     * @param attribute 属性值 该服务接受任何类型的KEY-VALUE持久化，但是为了保持代码的整洁，请使用<@code>BusinessCodeAttributeKey</@code>中的枚举作为KEY值
     * @param createUser 创建人
     * @param fromSourceEnum 创建来源
     * @see BusinessCodeNodeTypeEnum 业务单号类型 「支持扩展，如果需要新的单号，请添加单号类型」
     * @see BusinessCodeFromSourceEnum 业务单号创建来源 「支持扩展，如果需要新的来源，请添加来源枚举」
     * @see BusinessCodeAttributeKey 业务单号中的属性值枚举 「支持扩展，如果需要新的单号类型，请添加相关的内部枚举类，如果需要添加属性，请在内部枚举类中添加相关的枚举值」
     * @return 返回是否创建成功
     */
    boolean saveBusinessCodeAndAttribute(String businessCode, BusinessCodeNodeTypeEnum businessCodeNodeTypeEnum,
                                         Map<String, String> attribute, String createUser, BusinessCodeFromSourceEnum fromSourceEnum);

    /**
     * 根据单号查询businessCode的属性，以KEY-VALUE的形式展开
     * @param businessCode 业务单号
     * @return 返回业务单号的所有属性key-value
     * @see BusinessCodeAttributeKey 业务单号中的属性值枚举
     */
    Map<String, String> queryBusinessCodeAttributesByCode(String businessCode);

    /**
     * 查询业务单号相应的属性 查询该单号对应属性的值
     * @param businessCode 业务单号
     * @param attributeKey 业务单号对应的属性KEY
     * @return 返回该业务单号属性KEY对应的值
     * @see BusinessCodeAttributeKey 业务单号中的属性值枚举
     */
    String queryBusinessCodeAttributeByCodeAndKey(String businessCode, String attributeKey);

    /**
     * 查询业务单号主表中的记录根据单号和单号类型查询
     * @param businessCode 单号
     * @param businessCodeNodeTypeEnum 单号类型
     * @return 返回业务单号主表记录
     */
    BusinessCodePo queryBusinessCodeByCode(String businessCode, BusinessCodeNodeTypeEnum businessCodeNodeTypeEnum);
}
