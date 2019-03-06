package com.jd.bluedragon.distribution.cross.service;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.cross.dao.CrossSortingDao;
import com.jd.bluedragon.distribution.cross.dao.CrossSortingReadDao;
import com.jd.bluedragon.distribution.cross.domain.CrossSorting;
import com.jd.bluedragon.distribution.cross.domain.CrossSortingDto;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.zip.DataFormatException;

@Service("crossSortingService")
public class CrossSortingImpl implements CrossSortingService {

	private final Log logger = LogFactory.getLog(this.getClass());

    public static final String CREATE_PACKAGE = "建包";
    public static final String CREATE_SEND = "发货";

    public static final Integer CREATE_PACKAGE_CODE = 10;
    public static final Integer CREATE_SEND_CODE = 20;

    public  static final  Integer ERROR_CODE201=201;
    public static  final  String ERROR_MESSAGE201="跨分拣需求1 参数错误 null";

    public  static final  Integer ok=200;

    public  static final  Integer ERROR_CODE202=202;
    public static  final  String ERROR_MESSAGE202="跨分拣需求1 DB无数据";

	@Autowired
	private CrossSortingDao crossSortingDao;

	@Autowired
	private CrossSortingReadDao crossSortingReadDao;

	@Autowired
	private BaseMajorManager baseMajorManager;

	@Override
	public Integer findCountCrossSorting(Map<String, Object> params) {
		logger.info("CrossSortingImpl.findCountCrossSorting begin...");
		return crossSortingReadDao.findCountCrossSorting(params);
	}

	@Override
	public List<CrossSorting> findPageCrossSorting(Map<String, Object> params) {
		logger.info("CrossSortingImpl.findPageCrossSorting begin...");
		return crossSortingReadDao.findPageCrossSorting(params);
	}

	@Override
	public int deleteCrossSorting(Map<String, Object> params) {
		logger.info("CrossSortingImpl.deleteCrossSorting begin...");
		return crossSortingDao.deleteCrossSorting(params);
	}

	@Override
	public int addBatchCrossSorting(List<CrossSorting> csList) {
		logger.info("CrossSortingImpl.addBatchCrossSorting begin...");
		return crossSortingDao.addBatchCrossSorting(csList);
	}

	@Override
	public List<CrossSorting> findMixDms(Map<String, Object> params) {
		logger.info("CrossSortingImpl.getMixDms begin...");
		return crossSortingReadDao.findMixDms(params);
	}

    /**
     * 夸分拣中心规则配置  缓存 读
     * @param createDmsCode 始发地分拣中心id
     * @param destinationDmsCode  目的分拣id
     * @param mixDmsCode 可混装分拣中心id
     * @param dmsType 规则类型 分拣10 发货20
     * @return
     */
    @Override
    @Cache(key = "CrossSortingImpl.getQueryByids@args0@args1@args2@args3", memoryEnable = false, memoryExpiredTime = 10 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
    public List<CrossSortingDto> getQueryByids(Integer createDmsCode ,Integer destinationDmsCode,Integer mixDmsCode){
        List<CrossSortingDto> resultlist = new ArrayList<CrossSortingDto>();
        CrossSortingDto crossSortingDto=new CrossSortingDto();
        if(null==createDmsCode || null==destinationDmsCode){
            crossSortingDto.setCode(ERROR_CODE201);
            crossSortingDto.setMessage(ERROR_MESSAGE201);
            resultlist.add(crossSortingDto);
            logger.info("CrossSortingImpl.getQueryByid params==Null");
            return resultlist;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("createDmsCode",createDmsCode);
        params.put("destinationDmsCode",destinationDmsCode);
        params.put("mixDmsCode",mixDmsCode);

        List<CrossSorting> mixDmsList = crossSortingReadDao.findOne(params);

        if(null!=mixDmsList && mixDmsList.size()>0){
            crossSortingDto.setCode(ok);
            crossSortingDto.setCrossSortingList(mixDmsList);
            resultlist.add(crossSortingDto);
        }else{
            crossSortingDto.setCode(ERROR_CODE202);
            crossSortingDto.setMessage(ERROR_MESSAGE202);
            logger.info("CrossSortingImpl.getQueryByids DB is null");
        }
        return resultlist;
    }


    /**
     * 导入跨分拣信息服务
     * @param sheet0 导入的文件生成的sheet
     * @param userName 操作人
     * @param userCode 操作人code
     * @throws Exception
     */
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Deprecated
	public void importCrossSortingRules(Sheet sheet0,String userName, String userCode) throws Exception{
		logger.info("start import crossing sorting rule file...");
		logger.info("total " + sheet0.getLastRowNum() + " row rules");
		List<BaseStaffSiteOrgDto> siteCodes = getAllDistributionCenter();
		List<CrossSorting> needInsertRules = new ArrayList<CrossSorting>();
        List<CrossSorting> needUpdateRules = new ArrayList<CrossSorting>();
        List<CrossSorting> allValidRules = new ArrayList<CrossSorting>();
		for (int rowIndex = 1; rowIndex <= sheet0.getLastRowNum(); rowIndex++) {
			Row row = sheet0.getRow(rowIndex);
			CrossSorting curRules = new CrossSorting();
			checkCellFormatValid(row);  // 检查单元格是否符合列对应的要求
			checkSiteCodeValid(row, siteCodes, curRules);   // 检查输入的站点code是否存在，源分拣中心和混装分拣中心不能相同
			curRules.setCreateTime(new Date());
			curRules.setCreateUserCode(Integer.valueOf(userCode));
			curRules.setCreateUserName(userName);
			curRules.setYn(1);
            allValidRules.add(curRules);
			CrossSorting existRules = crossSortingDao.findCrossSorting(curRules);
			if(existRules != null){
				needUpdateRules.add(curRules);
			}else{
				needInsertRules.add(curRules);
			}
		}
        checkMixRulesRepeat(allValidRules); // 检查导入的excel文件里面有没有重复的行
        for(CrossSorting sorting : needUpdateRules){
            crossSortingDao.updateCrossSorting(sorting);
        }
		if(!needInsertRules.isEmpty()) crossSortingDao.addBatchCrossSorting(needInsertRules);
	}

    /**
     * 检查导入的excel里面有没有重复的行
     * @param crossSortings 导入的数据生成的list
     * @throws DataFormatException
     */
    private void checkMixRulesRepeat(List<CrossSorting> crossSortings) throws DataFormatException{
        if(null == crossSortings || crossSortings.size() <= 0) return;
        for(int i = 0; i < crossSortings.size(); i++){
            CrossSorting cs = crossSortings.get(i);
            for(int j = i + 1; j < crossSortings.size(); j++){
                CrossSorting cs1 = crossSortings.get(j);
                if(cs.getCreateDmsCode().equals(cs1.getCreateDmsCode())
                        && cs.getDestinationDmsCode().equals(cs1.getDestinationDmsCode()) && cs.getMixDmsCode().equals(cs1.getMixDmsCode())){
                    throw new DataFormatException("第" + (i + 2) + "行和第" + (j + 2) + "行数据重复"); //为毛+2，excel从第二行开始读的需+1，遍历从0开始的需+1
                }
            }
        }
    }

    /**
     * 检查站点是否存在，源分拣中心和混装分拣中心不能相同
     * @param row
     * @param siteCodes
     * @param crossSorting
     * @throws DataFormatException
     */
	private void checkSiteCodeValid(Row row, List<BaseStaffSiteOrgDto> siteCodes,CrossSorting crossSorting) throws DataFormatException {
		int rowIndex = row.getRowNum();
//		Cell ruleType = row.getCell(0);
//		if(CREATE_PACKAGE.equals(ruleType.getStringCellValue())){
//			crossSorting.setType(CREATE_PACKAGE_CODE);
//		}else{
//			crossSorting.setType(CREATE_SEND_CODE);
//		}
		Cell sourceCode = row.getCell(0);
		BaseStaffSiteOrgDto siteOrgDto = getSiteByCode(siteCodes, sourceCode.getNumericCellValue());
		if (null == siteOrgDto) {
			throw new DataFormatException("(" + (rowIndex + 1) + "行," + (1) + "列) 没有找到对应分拣中心");
		}
		crossSorting.setCreateDmsCode(siteOrgDto.getSiteCode());
		crossSorting.setCreateDmsName(siteOrgDto.getSiteName());
		crossSorting.setOrgId(siteOrgDto.getOrgId());
		Cell targetCode = row.getCell(2);
		siteOrgDto = getSiteByCode(siteCodes, targetCode.getNumericCellValue());
		if (null == siteOrgDto) {
			throw new DataFormatException("(" + (rowIndex + 1) + "行," + (3) + "列) 没有找到对应分拣中心");
		}
		crossSorting.setDestinationDmsCode(siteOrgDto.getSiteCode());
		crossSorting.setDestinationDmsName(siteOrgDto.getSiteName());
		Cell mixCode = row.getCell(4);
		siteOrgDto = getSiteByCode(siteCodes,mixCode.getNumericCellValue());
		if (null == siteOrgDto) {
			throw new DataFormatException("(" + (rowIndex + 1) + "行," + (5) + "列) 没有找到对应分拣中心");
		}
		crossSorting.setMixDmsCode(siteOrgDto.getSiteCode());
		crossSorting.setMixDmsName(siteOrgDto.getSiteName());
        if(crossSorting.getCreateDmsCode().equals(crossSorting.getMixDmsCode())){
            throw new DataFormatException((rowIndex + 1) + "行 源分拣中心和混装分拣中心不能相同");
        }
	}


    /**
     * 检查指定的单元格格式是否是规定的格式
     * @param row
     * @throws Exception
     */
	private void checkCellFormatValid(Row row) throws Exception{
//		Cell ruleType = row.getCell(0);
		int rowIndex = row.getRowNum();
//		if (null == ruleType || ruleType.getCellType() != Cell.CELL_TYPE_STRING
//				|| StringHelper.isEmpty(ruleType.getStringCellValue())
//				|| !isRuleTypeValid(ruleType.getStringCellValue())) {
//			throw new DataFormatException("(" + (rowIndex + 1) + "行," + (1) + "列) 数据不正确");
//		}
		Cell sourceCode = row.getCell(0);
		if (null == sourceCode || sourceCode.getCellType() != Cell.CELL_TYPE_NUMERIC) {
			throw new DataFormatException("(" + (rowIndex + 1) + "行," + (1) + "列) 数据不正确");
		}
		Cell targetCode = row.getCell(2);
		if (null == targetCode || targetCode.getCellType() != Cell.CELL_TYPE_NUMERIC) {
			throw new DataFormatException("(" + (rowIndex + 1) + "行," + (3) + "列) 数据不正确");
		}
		Cell mixCode = row.getCell(4);
		if (null == mixCode || mixCode.getCellType() != Cell.CELL_TYPE_NUMERIC) {
			throw new DataFormatException("(" + (rowIndex + 1) + "行," + (5) + "列) 数据不正确");
		}
		logger.info("原分拣:" + sourceCode + ",目标分拣:" + targetCode + ",混装分拣:" + mixCode);
	}

    /**
     * 根据配置的站点编码获取完整的站点信息
     * @param siteOrgDtos 所有的分拣中心
     * @param siteCode 配置的站点编码
     * @return
     */
	private BaseStaffSiteOrgDto getSiteByCode(List<BaseStaffSiteOrgDto> siteOrgDtos,double siteCode){
		try {
			for (BaseStaffSiteOrgDto dto : siteOrgDtos) {
				if (dto.getSiteCode().equals(Integer.valueOf((int) siteCode))) {
					return dto;
				}
			}
		}catch (NumberFormatException ex){
		}
		return null;
	}

    /**
     * 获取所有的分拣中心
     * @return
     */
	@Deprecated
	private List<BaseStaffSiteOrgDto> getAllDistributionCenter(){
		CallerInfo info = Profiler.registerInfo("DMS.CrossSortingImpl.getAllDistributionCenter", false, true);
		/*List<BaseStaffSiteOrgDto> baseStaffSiteOrgDtos = baseMajorManager.getDmsSiteAll();
		List<BaseStaffSiteOrgDto> dtos = new ArrayList<BaseStaffSiteOrgDto>();
		for (BaseStaffSiteOrgDto dto : baseStaffSiteOrgDtos) {
			if (64 == dto.getSiteType()){
				dtos.add(dto);
			}
		}*/
        List<BaseStaffSiteOrgDto> baseSiteList = baseMajorManager.getBaseSiteByOrgIdSiteType(null, 64);
        Profiler.registerInfoEnd(info);
		return baseSiteList;
	}

    /**
     * 配置的规则是否是建包和发货
     * @param ruleType
     * @return
     */
	private Boolean isRuleTypeValid(String ruleType){
		if (CREATE_PACKAGE.equals(ruleType) || CREATE_SEND.equals(ruleType)) {
			return true;
		}
		return false;
	}
	@Override
	public int updateCrossSorting(CrossSorting cs) {
		logger.info("CrossSortingImpl.updateCrossSorting begin...");
		return crossSortingDao.updateCrossSorting(cs);
	}

	@Override
	public int updateCrossSortingForDelete(CrossSorting cs) {
		logger.info("CrossSortingImpl.updateCrossSortingForDelete begin...");
		return crossSortingDao.updateCrossSortingForDelete(cs);
	}

}
