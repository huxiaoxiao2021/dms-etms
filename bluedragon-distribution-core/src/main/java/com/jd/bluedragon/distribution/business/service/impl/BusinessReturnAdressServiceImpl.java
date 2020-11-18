package com.jd.bluedragon.distribution.business.service.impl;

import com.jd.bluedragon.core.base.LDOPManager;
import com.jd.bluedragon.distribution.business.dao.BusinessReturnAdressDao;
import com.jd.bluedragon.distribution.business.entity.BusinessReturnAdress;
import com.jd.bluedragon.distribution.business.entity.BusinessReturnAdressCondition;
import com.jd.bluedragon.distribution.business.entity.BusinessReturnAdressStatusEnum;
import com.jd.bluedragon.distribution.business.service.BusinessReturnAdressService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ldop.business.api.dto.request.BackAddressDTO;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ql.dms.print.utils.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: BusinessReturnAdressServiceImpl
 * @Description: 商家退货地址信息--Service接口实现
 * @author wuyoude
 * @date 2020年07月28日 16:45:14
 *
 */
@Service("businessReturnAdressService")
public class BusinessReturnAdressServiceImpl extends BaseService<BusinessReturnAdress> implements BusinessReturnAdressService {

	private static final Logger logger = LoggerFactory.getLogger(BusinessReturnAdressServiceImpl.class);

	/**
     * 商家退货地址导出限制条数，默认：5000
     * */
    @Value("${business.return.export.max:5000}")
	private Integer businessReturnExportMax;

	/**
	 * 导出excel头
	 */
	private static final List<Object> EXCELL_HEADS = new ArrayList<Object>();
	static {
		EXCELL_HEADS.add("场地ID");
		EXCELL_HEADS.add("场地名称");
		EXCELL_HEADS.add("最新换单时间");
		EXCELL_HEADS.add("商家ID");
		EXCELL_HEADS.add("商家名称");
		EXCELL_HEADS.add("事业部编码");
		EXCELL_HEADS.add("此时是否已维护退货信息");
		EXCELL_HEADS.add("退货量");
	}
	@Autowired
	@Qualifier("businessReturnAdressDao")
	private BusinessReturnAdressDao businessReturnAdressDao;
    @Autowired
	private LDOPManager lDOPManager;
    
	@Override
	public Dao<BusinessReturnAdress> getDao() {
		return this.businessReturnAdressDao;
	}

	@Override
	public List<List<Object>> queryBusinessReturnAdressExcelData(BusinessReturnAdressCondition businessReturnAdressCondition) {
		List<List<Object>> resList = new ArrayList<List<Object>>();
		resList.add(EXCELL_HEADS);
		businessReturnAdressCondition.setLimit(businessReturnExportMax);
		PagerResult<BusinessReturnAdress> result = this.queryBusinessReturnAdressListByPagerCondition(businessReturnAdressCondition);
		if(result != null
				&& result.getRows() != null){
			for(BusinessReturnAdress row : result.getRows()){
				loadReturnAdressStatusDesc(row);
				List<Object> body = new ArrayList<Object>();
				body.add(row.getDmsSiteCode());
				body.add(row.getDmsSiteName());
				body.add(DateHelper.formatDateTime(row.getLastOperateTime()));
				body.add(row.getBusinessId());
				body.add(row.getBusinessName());
				body.add(row.getDeptNo());
				body.add(row.getReturnAdressStatusDesc());
				body.add(row.getReturnQuantity());
				resList.add(body);
			}
		}
		return resList;
	}

	@Override
	public PagerResult<BusinessReturnAdress> queryBusinessReturnAdressListByPagerCondition(BusinessReturnAdressCondition businessReturnAdressCondition) {
		if(businessReturnAdressCondition != null){
			//设置调度时间条件
			if(StringHelper.isNotEmpty(businessReturnAdressCondition.getLastOperateTimeGteStr())){
				businessReturnAdressCondition.setLastOperateTimeGte(DateHelper.parseAllFormatDateTime(businessReturnAdressCondition.getLastOperateTimeGteStr()));
			}
			if(StringHelper.isNotEmpty(businessReturnAdressCondition.getLastOperateTimeLtStr())){
				businessReturnAdressCondition.setLastOperateTimeLt(DateHelper.parseAllFormatDateTime(businessReturnAdressCondition.getLastOperateTimeLtStr()));
			}
		}
		PagerResult<BusinessReturnAdress> result = this.businessReturnAdressDao.queryListByConditionWithPage(businessReturnAdressCondition);
		if(result != null
				&& result.getRows() != null){
			int rowNum = 1;
			for(BusinessReturnAdress businessReturnAdress : result.getRows()){
				businessReturnAdress.setRowNum(rowNum ++);
				loadReturnAdressStatusDesc(businessReturnAdress);
			}
		}
		return result;
	}
	/**
	 * 加载地址维护状态
	 * @param businessReturnAdress
	 */
	private void loadReturnAdressStatusDesc(BusinessReturnAdress businessReturnAdress){
		//调用jsf获取退货地址
        JdResult<List<BackAddressDTO>> backAddressResult = lDOPManager.queryBackAddressByType(DmsConstants.RETURN_BACK_ADDRESS_TYPE_6, businessReturnAdress.getBusinessCode());
        if(backAddressResult != null 
        		&& backAddressResult.isSucceed()
        		&& backAddressResult.getData() != null
        		&& backAddressResult.getData().size() > 0){
        	businessReturnAdress.setReturnAdressStatusDesc(BusinessReturnAdressStatusEnum.YES.getStatusName());
        }else{
        	businessReturnAdress.setReturnAdressStatusDesc(BusinessReturnAdressStatusEnum.NO.getStatusName());
        }
	}
	@Override
	public BusinessReturnAdress queryBusinessReturnAdressByBusiId(Integer busiId) {
		return this.businessReturnAdressDao.queryBusinessReturnAdressByBusiId(busiId);
	}

	@Override
	public boolean add(BusinessReturnAdress businessReturnAdress) {
		return this.businessReturnAdressDao.insert(businessReturnAdress);
	}

	@Override
	public boolean update(BusinessReturnAdress businessReturnAdress) {
		return this.businessReturnAdressDao.update(businessReturnAdress);
	}

    @Override
    public List<BusinessReturnAdress> queryByBusinessIdWithNoMaintain(Integer businessId) {
        return businessReturnAdressDao.queryByBusinessIdWithNoMaintain(businessId);
    }

    @Override
    public BusinessReturnAdress queryBySiteAndBusinessId(Integer dmsSiteCode, Integer businessId){
        BusinessReturnAdress businessReturnAddress = new BusinessReturnAdress();
        businessReturnAddress.setDmsSiteCode(dmsSiteCode);
        businessReturnAddress.setBusinessId(businessId);
        return businessReturnAdressDao.queryBySiteAndBusinessId(businessReturnAddress);
    }

    @Override
    public int updateReturnQuantity(BusinessReturnAdress businessReturnAddress) {
	    if(businessReturnAddress == null){
	        return 0;
        }
        return businessReturnAdressDao.updateReturnQuantity(businessReturnAddress);
    }

    @Override
    public int updateStatusByBusinessId(Integer businessId) {
        if(businessId == null){
            return 0;
        }
        return businessReturnAdressDao.updateStatusByBusinessId(businessId);
    }

}
