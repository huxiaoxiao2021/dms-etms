package com.jd.bluedragon.distribution.busineCode;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeFromSourceEnum;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeNodeTypeEnum;
import com.jd.bluedragon.distribution.businessCode.dao.BusinessCodeDao;
import com.jd.bluedragon.distribution.businessCode.domain.BusinessCodeAttributePo;
import com.jd.bluedragon.distribution.businessCode.domain.BusinessCodePo;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;

@Service("businessCodeService")
public class BusinessCodeManagerImpl implements BusinessCodeManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessCodeManagerImpl.class);

    @Autowired
    private BusinessCodeDao businessCodeDao;

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "BusinessCodeService.saveBusinessCodeAndAttribute", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.FunctionError,JProEnum.TP})
    public boolean saveBusinessCodeAndAttribute(String businessCode, BusinessCodeNodeTypeEnum businessCodeNodeTypeEnum,
                                                Map<String, String> attributeMap, String createUser, BusinessCodeFromSourceEnum fromSourceEnum) {
        /* 校验参数有效性 */
        if (StringHelper.isEmpty(businessCode)) {
            LOGGER.error("保存单号失败，单号为空");
            return Boolean.FALSE;
        }
        if (businessCodeNodeTypeEnum == null) {
            LOGGER.error("保存单号失败，单号[{}]类型为空", businessCode);
            return Boolean.FALSE;
        }
        if (fromSourceEnum == null) {
            LOGGER.error("保存单号失败，单号[{}]创建来源为空", businessCode);
            return Boolean.FALSE;
        }

        /* 1. 添加属性副表 */
        if (attributeMap != null && !attributeMap.isEmpty()) {
            List<BusinessCodeAttributePo> businessCodeAttributePos = new ArrayList<>(attributeMap.size());
            for (Map.Entry<String, String> attributeEntry : attributeMap.entrySet()) {
                BusinessCodeAttributePo businessCodeAttributePoItem = new BusinessCodeAttributePo();
                businessCodeAttributePoItem.setCode(businessCode);
                businessCodeAttributePoItem.setAttributeKey(attributeEntry.getKey());
                businessCodeAttributePoItem.setAttributeValue(String.valueOf(attributeEntry.getValue()));
                businessCodeAttributePoItem.setCreateUser(createUser);
                businessCodeAttributePoItem.setUpdateUser(createUser);
                businessCodeAttributePoItem.setFromSource(fromSourceEnum.name());
                businessCodeAttributePos.add(businessCodeAttributePoItem);
            }
            Integer insertNum = businessCodeDao.batchInsertBusinessCodeAttribute(businessCodeAttributePos);
            if (insertNum <= 0) {
                LOGGER.warn("插入业务单号的属性值副表失败，创建批次号失败，批次属性值：{}", JsonHelper.toJson(attributeMap));
                return Boolean.FALSE;
            }
        }

        /* 2. 添加属性主表 */
        BusinessCodePo businessCodePo = new BusinessCodePo();
        businessCodePo.setCode(businessCode);
        businessCodePo.setNodeType(BusinessCodeNodeTypeEnum.send_code.name());
        businessCodePo.setCreateUser(createUser);
        businessCodePo.setUpdateUser(createUser);
        businessCodePo.setFromSource(fromSourceEnum.name());
        Integer businessCodeInsertNum = businessCodeDao.insertBusinessCode(businessCodePo);
        if (businessCodeInsertNum <= 0) {
            LOGGER.warn("插入业务单号的主表失败，创建批次号失败，批次属性值：{}", JsonHelper.toJson(attributeMap));
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "BusinessCodeService.queryBusinessCodeAttributesByCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.FunctionError,JProEnum.TP})
    public Map<String, String> queryBusinessCodeAttributesByCode(String businessCode) {
        List<BusinessCodeAttributePo> businessCodeAttributePos = businessCodeDao.findAllAttributesByCode(businessCode);
        if (CollectionUtils.isEmpty(businessCodeAttributePos)) {
            LOGGER.error("该批次号：{}存在业务单号的主表记录，但不存在业务单号的副表记录，查询失败", businessCode);
            return Collections.emptyMap();
        }
        Map<String,String> attributes = new HashMap<>(businessCodeAttributePos.size());
        for (BusinessCodeAttributePo item : businessCodeAttributePos) {
            attributes.put(item.getAttributeKey(),item.getAttributeValue());
        }
        return attributes;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "BusinessCodeService.queryBusinessCodeAttributeByCodeAndKey", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.FunctionError,JProEnum.TP})
    public String queryBusinessCodeAttributeByCodeAndKey(String businessCode, String attributeKey) {
        if (StringHelper.isEmpty(businessCode) || StringHelper.isEmpty(attributeKey)) {
            throw new RuntimeException(MessageFormat.format("查询该业务单号【{0}】的属性【{1}】无效", businessCode, attributeKey));
        }
        BusinessCodeAttributePo condition = new BusinessCodeAttributePo();
        condition.setCode(businessCode);
        condition.setAttributeKey(attributeKey);
        BusinessCodeAttributePo businessCodeAttributePo = businessCodeDao.findAttributeByCodeAndKey(condition);
        return businessCodeAttributePo == null? StringUtils.EMPTY : businessCodeAttributePo.getAttributeValue();
    }

    @Override
    public BusinessCodePo queryBusinessCodeByCode(String businessCode, BusinessCodeNodeTypeEnum businessCodeNodeTypeEnum) {
        BusinessCodePo businessCodePo = businessCodeDao.findBusinessCodeByCode(businessCode); // 查询businessCode记录
        boolean bool = businessCodePo != null && StringHelper.isNotEmpty(businessCodePo.getNodeType())
                && businessCodePo.getNodeType().equals(businessCodeNodeTypeEnum.name()); // 判断是否有效
        return bool? businessCodePo : null;
    }
}
