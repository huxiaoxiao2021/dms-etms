package com.jd.bluedragon.distribution.popPrint.service.impl;

import com.jd.bluedragon.distribution.inspection.dao.InspectionDao;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.popPrint.dao.PopPrintDao;
import com.jd.bluedragon.distribution.popPrint.domain.PopPrint;
import com.jd.bluedragon.distribution.popPrint.service.PopPrintService;
import com.jd.bluedragon.utils.CollectionHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-14 下午07:50:33
 *
 * 类说明
 */
@Service("popPrintService")
public class PopPrintServiceImpl implements PopPrintService {
	
	private final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	private PopPrintDao popPrintDao;
	
	@Autowired
	private InspectionDao inspectionDao;

	@Override
	public PopPrint findByWaybillCode(String waybillCode) {
		if (StringUtils.isBlank(waybillCode)) {
			logger.info("传入运单号 waybillCode 为空");
		}
		return popPrintDao.findByWaybillCode(waybillCode);
	}

	@Override
	public List<PopPrint> findSitePrintDetail(Map<String,Object> map){
		return  popPrintDao.findSitePrintDetail(map);
	}

	@Override
	public Integer findSitePrintDetailCount(Map<String,Object> map){
		return  popPrintDao.findSitePrintDetailCount(map);
	}



	

	@Override
	public List<PopPrint> findAllByWaybillCode(String waybillCode) {
		if (StringUtils.isBlank(waybillCode)) {
			logger.info("传入运单号 waybillCode 为空");
		}
		return this.popPrintDao.findAllByWaybillCode(waybillCode);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int add(PopPrint popPrint) {
		if (popPrint == null) {
			logger.info("传入popPrint 为空");
			return 0;
		}
		return popPrintDao.add(popPrint);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int updateByWaybillCode(PopPrint popPrint) {
		return popPrintDao.updateByWaybillCode(popPrint);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int updateByWaybillOrPack(PopPrint popPrint) {
		return this.popPrintDao.updateByWaybillOrPack(popPrint);
	}

	@Override
    public List<PopPrint> findLimitListNoReceive(List<PopPrint> popList, Map<String, Object> paramMap) {
        List<PopPrint> target=new LinkedList<PopPrint>();
        if (popList != null && !popList.isEmpty()) {
            for (PopPrint popPrint : popList) {
                // 优化拆分表和非拆分表查询语句加入的代码
                String ownSign = (String) paramMap.get("ownSign");
                Inspection inspection = new Inspection();
                inspection.setCreateSiteCode(popPrint.getCreateSiteCode());
                inspection.setWaybillCode(popPrint.getWaybillCode());
                inspection.setPackageBarcode(popPrint.getPackageBarcode());
                if("PRE".equals(ownSign)) {
                    inspection.setInspectionType(60);
                } else {
                    inspection.setInspectionType(40);
                }
                Integer inspectionList = inspectionDao.queryCountByCondition(inspection);
                if(null != inspectionList && inspectionList > 0) {
                    //popList.remove(popPrint);
                    continue;
                }


                //原来的代码
                inspection = new Inspection();
                inspection.setCreateSiteCode(popPrint.getCreateSiteCode());
                inspection.setWaybillCode(popPrint.getWaybillCode());
                inspection.setPackageBarcode(popPrint.getPackageBarcode());
                inspection.setInspectionType(popPrint.getPopReceiveType());
                if (inspectionDao.havePOPInspection(inspection)) {
                    //popList.remove(popPrint);///DEBUG TO DO
                    continue;
                }

                target.add(popPrint);
            }
        }
        return target;
    }

}
