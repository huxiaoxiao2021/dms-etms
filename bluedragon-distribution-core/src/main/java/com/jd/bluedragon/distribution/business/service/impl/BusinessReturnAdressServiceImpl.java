package com.jd.bluedragon.distribution.business.service.impl;

import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.core.base.LDOPManager;
import com.jd.bluedragon.distribution.business.dao.BusinessReturnAdressDao;
import com.jd.bluedragon.distribution.business.entity.BusinessReturnAddressExportDto;
import com.jd.bluedragon.distribution.business.entity.BusinessReturnAdress;
import com.jd.bluedragon.distribution.business.entity.BusinessReturnAdressCondition;
import com.jd.bluedragon.distribution.business.entity.BusinessReturnAdressStatusEnum;
import com.jd.bluedragon.distribution.business.service.BusinessReturnAdressService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.utils.CsvExporterUtils;
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

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
		if(result != null && result.getRows() != null){
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

	@Override
	public void export(BusinessReturnAdressCondition businessReturnAdressCondition, BufferedWriter bufferedWriter) {
		try {
			// 写入表头
			Map<String, String> headerMap = getHeaderMap();
			CsvExporterUtils.writeTitleOfCsv(headerMap, bufferedWriter, headerMap.values().size());

			int queryTotal = 0;
			int index = 1;
			businessReturnAdressCondition.setLimit(businessReturnExportMax);
			while (index++ <= 100) {
				PagerResult<BusinessReturnAdress> result = this.queryBusinessReturnAdressListByPagerCondition(businessReturnAdressCondition);
				if(result != null && result.getRows() != null){
					List<BusinessReturnAddressExportDto> dataList =  transForm(result.getRows());
					// 输出至csv文件中
					CsvExporterUtils.writeCsvByPage(bufferedWriter, headerMap, dataList);
					// 限制导出数量
					queryTotal += dataList.size();
					if(queryTotal > businessReturnExportMax){
						break;
					}
				}
			}
		}catch (Exception e){
			log.error("商家退货地址 export error",e);
		}
	}

	/**
	 * 转化导出对象
	 * @param rows
	 * @return
	 */
	private List<BusinessReturnAddressExportDto> transForm(List<BusinessReturnAdress> rows) {
		List<BusinessReturnAddressExportDto> dataList = new ArrayList<>();
		for(BusinessReturnAdress row : rows){
			BusinessReturnAddressExportDto body = new BusinessReturnAddressExportDto();
			body.setDmsSiteCode(row.getDmsSiteCode());
			body.setDmsSiteName(row.getDmsSiteName());
			body.setLastOperateTime(DateHelper.formatDateTime(row.getLastOperateTime()));
			body.setBusinessId(row.getBusinessId());
			body.setBusinessName(row.getBusinessName());
			body.setDeptNo(row.getDeptNo());
			body.setReturnAdressStatusDesc(row.getReturnAdressStatusDesc());
			body.setReturnQuantity(row.getReturnQuantity());
			dataList.add(body);
		}
		return dataList;
	}

	private Map<String, String> getHeaderMap() {
		Map<String, String> headerMap = new LinkedHashMap<>();
		headerMap.put("dmsSiteCode","场地ID");
		headerMap.put("dmsSiteName","场地名称");
		headerMap.put("lastOperateTime","最新换单时间");
		headerMap.put("businessId","商家ID");
		headerMap.put("businessName","商家名称");
		headerMap.put("deptNo","签约区域");
		headerMap.put("returnAdressStatusDesc","此时是否已维护退货信息");
		headerMap.put("returnQuantity","退货量");
		return  headerMap;
	}
}
