package com.jd.bluedragon.distribution.merchantWeightAndVolume.service.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.merchantWeightAndVolume.dao.MerchantWeightAndVolumeWhiteListDao;
import com.jd.bluedragon.distribution.merchantWeightAndVolume.domain.MerchantWeightAndVolumeCondition;
import com.jd.bluedragon.distribution.merchantWeightAndVolume.domain.MerchantWeightAndVolumeDetail;
import com.jd.bluedragon.distribution.merchantWeightAndVolume.service.MerchantWeightAndVolumeWhiteListService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ldop.basic.dto.BasicTraderNeccesaryInfoDTO;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2019/11/5 14:10
 */
@Service("merchantWeightAndVolumeWhiteListService")
public class MerchantWeightAndVolumeWhiteListServiceImpl implements MerchantWeightAndVolumeWhiteListService {

    private final Logger logger = Logger.getLogger(this.getClass());

    @Value("${merchant.whiteList.import.maxNum:1000}")
    private Integer importMaxNum;

    @Value("${merchant.whiteList.export.maxNum:5000}")
    private Integer exportMaxNum;

    /**
     * 导入导出最大值
     * */
    private static final Integer MERCHANT_WHITELIST_IMPORT_MAX_NUM = 1000;
    private static final Integer MERCHANT_WHITELIST_EXPORT_MAX_NUM = 5000;

    @Autowired
    private SiteService siteService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private MerchantWeightAndVolumeWhiteListDao merchantWeightAndVolumeWhiteListDao;

    @Override
    public PagerResult<MerchantWeightAndVolumeDetail> queryByCondition(MerchantWeightAndVolumeCondition condition) {

        PagerResult<MerchantWeightAndVolumeDetail> pagerResult = new PagerResult<>();
        try {
            List<MerchantWeightAndVolumeDetail> list = merchantWeightAndVolumeWhiteListDao.queryByCondition(condition);
            Integer count = merchantWeightAndVolumeWhiteListDao.queryCountByCondition(condition);
            pagerResult.setTotal(count);
            pagerResult.setRows(list);
        }catch (Exception e){
            logger.error("查询失败!",e);
        }
        return pagerResult;
    }

    @Override
    public InvokeResult<Integer> delete(MerchantWeightAndVolumeDetail detail) {
        InvokeResult<Integer> result = new InvokeResult<Integer>();
        int delete = -1;
        try {
            delete = merchantWeightAndVolumeWhiteListDao.delete(detail);
        }catch (Exception e){
            logger.error("删除失败"+ JsonHelper.toJson(delete),e);
        }
        result.setData(delete);
        return result;
    }

    /**
     * 校验excel数据并落库
     * @param dataList
     * @param importErpCode
     * @return
     */
    @Override
    public String checkExportData(List<MerchantWeightAndVolumeDetail> dataList, String importErpCode) {
        String errorMessage = null;
        try {
            if(dataList != null && dataList.size() > 0){
                if(dataList.size() >
                        (importMaxNum==null?MERCHANT_WHITELIST_IMPORT_MAX_NUM:importMaxNum)){
                    errorMessage = "导入数据超出"+importMaxNum+"条";
                    return errorMessage;
                }
                String createUserName = null;
                if(!StringUtils.isEmpty(importErpCode)){
                    BaseStaffSiteOrgDto basestaffDto = baseMajorManager.getBaseStaffIgnoreIsResignByErp(importErpCode);
                    if(basestaffDto != null && !StringUtils.isEmpty(basestaffDto.getStaffName())){
                        createUserName = basestaffDto.getStaffName();
                    }
                }
                Map<String,MerchantWeightAndVolumeDetail> map = new HashMap<>();
                int rowIndex = 1;
                for(MerchantWeightAndVolumeDetail detail : dataList){
                    BaseStaffSiteOrgDto site = siteService.getSite(detail.getOperateSiteCode());
                    if(site == null){
                        errorMessage = "第"+ rowIndex +"行场站编码不存在!";
                        return errorMessage;
                    }else{
                        //商家判断
                        if(!merchantCheck(detail)){
                            errorMessage = "第"+ rowIndex +"行商家信息配置不正确!";
                            return errorMessage;
                        }
                        //站点判断
                        if(!site.getSiteName().equals(detail.getOperateSiteName())){
                            errorMessage = "第"+ rowIndex +"行场站不正确!";
                            return errorMessage;
                        }
                        if(!site.getOrgId().equals(detail.getOperateOrgCode())
                                || !site.getOrgName().equals(detail.getOperateOrgName())){
                            errorMessage = "第"+ rowIndex +"行区域不正确!";
                            return errorMessage;
                        }
                        //商家与站点对应关系是否存在
                        if(isExist(detail)){
                            errorMessage = "第"+ rowIndex +"行商家编码对应站点已存在，请勿重复配置!";
                            return errorMessage;
                        }
                    }
                    detail.setCreateErp(importErpCode);
                    detail.setCreateUserName(createUserName);
                    rowIndex ++;
                    String uniqueKey = detail.getMerchantId() + "|" + detail.getOperateSiteCode();
                    map.put(uniqueKey,detail);
                }
                List<MerchantWeightAndVolumeDetail> realList = new ArrayList<>(map.size());
                realList.addAll(map.values());
                merchantWeightAndVolumeWhiteListDao.batchInsert(realList);
            }
        }catch (Exception e){
            logger.error("导入失败!",e);
            errorMessage = "导入失败!";
        }
        return errorMessage;
    }

    @Override
    public List<List<Object>> getExportData(MerchantWeightAndVolumeCondition condition) {
        List<List<Object>> resList = new ArrayList<List<Object>>();
        List<Object> heads = new ArrayList<Object>();
        //添加表头
        heads.add("商家配送编码");
        heads.add("商家名称");
        heads.add("操作区域ID");
        heads.add("操作区域名称");
        heads.add("操作场站ID");
        heads.add("操作场站");
        heads.add("创建人ERP");
        heads.add("创建人");
        heads.add("创建时间");
        resList.add(heads);
        condition.setLimit(exportMaxNum==null?MERCHANT_WHITELIST_EXPORT_MAX_NUM:exportMaxNum);
        List<MerchantWeightAndVolumeDetail> dataList = merchantWeightAndVolumeWhiteListDao.exportByCondition(condition);
        if(dataList != null && dataList.size() > 0){
            //表格信息
            for(MerchantWeightAndVolumeDetail detail : dataList){
                List<Object> body = Lists.newArrayList();
                body.add(detail.getMerchantCode());
                body.add(detail.getMerchantName());
                body.add(detail.getOperateOrgCode());
                body.add(detail.getOperateOrgName());
                body.add(detail.getOperateSiteCode());
                body.add(detail.getOperateSiteName());
                body.add(detail.getCreateErp());
                body.add(detail.getCreateUserName());
                body.add(detail.getCreateTime() == null ? null : DateHelper.formatDate(detail.getCreateTime(), Constants.DATE_FORMAT));
                resList.add(body);
            }
        }
        return  resList;
    }

    /**
     * 根据站点、商家编码判断是否存在
     * @param detail
     * @return
     */
    public Boolean isExist(MerchantWeightAndVolumeDetail detail) {
        Boolean isExist = Boolean.FALSE;
        try {
            MerchantWeightAndVolumeCondition condition = new MerchantWeightAndVolumeCondition();
            condition.setSiteCode(detail.getOperateSiteCode());
            condition.setMerchantId(detail.getMerchantId());
            condition.setMerchantCode(detail.getMerchantCode());
            List<MerchantWeightAndVolumeDetail> list = merchantWeightAndVolumeWhiteListDao.queryByCondition(condition);
            if(!CollectionUtils.isEmpty(list)){
                isExist = Boolean.TRUE;
            }
        }catch (Exception e){
            logger.error("根据站点、商家编码查询异常!",e);
            isExist = Boolean.TRUE;
        }
        return isExist;
    }

    /**
     * 商家校验
     * @param detail
     * @return
     */
    private Boolean merchantCheck(MerchantWeightAndVolumeDetail detail) {
        Boolean merchantCheck = Boolean.FALSE;
        try{
            BasicTraderNeccesaryInfoDTO dto = baseMajorManager.getBaseTraderNeccesaryInfoById(detail.getMerchantId());
            if(dto != null
                    && detail.getMerchantName()!=null&&detail.getMerchantName().equals(dto.getTraderName())
                    && detail.getMerchantCode()!=null&&detail.getMerchantCode().equals(dto.getTraderCode())){
                merchantCheck = Boolean.TRUE;
            }
        }catch (Exception e){
            logger.error("通过商家ID"+detail.getMerchantId()+"查询商家信息异常!",e);
        }
        return merchantCheck;
    }
}
