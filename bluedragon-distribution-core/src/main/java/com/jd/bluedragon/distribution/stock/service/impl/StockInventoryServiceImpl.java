package com.jd.bluedragon.distribution.stock.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.stock.StockInventoryResult;
import com.jd.bluedragon.common.dto.stock.StockInventoryScanDto;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.SortingInspectionSendingManager;
import com.jd.bluedragon.core.base.TransferWaveManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.stock.dao.StockInventoryDao;
import com.jd.bluedragon.distribution.stock.domain.InventoryQuery;
import com.jd.bluedragon.distribution.stock.domain.StockInventory;
import com.jd.bluedragon.distribution.stock.domain.StockInventoryEnum;
import com.jd.bluedragon.distribution.stock.service.StockInventoryService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.wb.report.api.dto.transferWave.TransferWaveDto;
import com.jd.dms.wb.report.api.dto.transferWave.TransferWaveQueryCondition;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.zw.monitor.service.domain.OperateSiteQueryVO;
import com.jd.ql.zw.monitor.service.domain.UnsentPackageDetail;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 库存盘点接口实现
 *
 * @author hujiping
 * @date 2021/6/4 2:14 下午
 */
@Service("stockInventoryService")
public class StockInventoryServiceImpl implements StockInventoryService {

    private static final Logger logger = LoggerFactory.getLogger(StockInventoryServiceImpl.class);

    /**
     * 待盘点数量
     */
    private static final int TODO_INVENTORY_NUM_LIMIT = 1000;
    /**
     * 待盘点数量超过1000则显示 999+
     */
    private static final String TODO_INVENTORY_NUM_LIMIT_DISPLAY = "999+";

    @Autowired
    private TransferWaveManager transferWaveManager;

    @Autowired
    private SortingInspectionSendingManager sortingInspectionSendingManager;

    @Autowired
    private StockInventoryDao stockInventoryDao;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @JProfiler(jKey = "dms.stock.StockInventoryServiceImpl.stockInventoryScan", jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public InvokeResult<StockInventoryResult> stockInventoryScan(StockInventoryScanDto stockInventoryScanDto) {

        // 参数校验
        InvokeResult<StockInventoryResult> result = checkParams(stockInventoryScanDto);
        if(!result.codeSuccess()){
            return result;
        }
        try {
            // 处理盘点数据
            updateInventory(stockInventoryScanDto);

            StockInventoryResult stockInventoryResult = new StockInventoryResult();
            stockInventoryResult.setWaveCode(stockInventoryScanDto.getWaveCode());
            stockInventoryResult.setWaveBeginTime(stockInventoryScanDto.getWaveBeginTime());
            stockInventoryResult.setWaveEndTime(stockInventoryScanDto.getWaveEndTime());
            // 扫描一次就获取一次盘点数量
            int inventoryNum = getInventoryNum(stockInventoryScanDto.getOperateSiteCode(),
                    stockInventoryScanDto.getWaveCode());
            stockInventoryResult.setInventoryNum(inventoryNum >= TODO_INVENTORY_NUM_LIMIT ? TODO_INVENTORY_NUM_LIMIT_DISPLAY : String.valueOf(inventoryNum));
            // 扫描一次就获取一次待盘点数量
            int totalUnSendPackNum = getTotalInventoryNum(stockInventoryScanDto.getOperateSiteCode(),
                    stockInventoryScanDto.getWaveBeginTime().getTime(),
                    stockInventoryScanDto.getWaveEndTime().getTime());
            int todoInventoryNum = Math.max(totalUnSendPackNum - inventoryNum, Constants.NUMBER_ZERO);
            stockInventoryResult.setTodoInventoryNum(todoInventoryNum >= TODO_INVENTORY_NUM_LIMIT ? TODO_INVENTORY_NUM_LIMIT_DISPLAY : String.valueOf(todoInventoryNum));

            result.setData(stockInventoryResult);
        }catch (Exception e){
            logger.error("库存盘点扫描异常!" ,e);
            result.error();
        }
        return result;
    }

    /**
     * 参数校验
     *
     * @param stockInventoryScanDto
     * @return
     */
    private InvokeResult<StockInventoryResult> checkParams(StockInventoryScanDto stockInventoryScanDto) {
        InvokeResult<StockInventoryResult> result = new InvokeResult<StockInventoryResult>();
        if(stockInventoryScanDto == null){
            result.parameterError(InvokeResult.PARAM_ERROR);
            return result;
        }
        if(!WaybillUtil.isPackageCode(stockInventoryScanDto.getPackageCode())){
            result.parameterError("只支持包裹号扫描!");
            return result;
        }
        if(StringUtils.isEmpty(stockInventoryScanDto.getWaveCode())){
            result.parameterError("波次编码不存在!");
            return result;
        }
        if(stockInventoryScanDto.getWaveBeginTime() == null || stockInventoryScanDto.getWaveEndTime() == null){
            result.parameterError("波次的起始时间不存在!");
            return result;
        }
        if(StringUtils.isEmpty(stockInventoryScanDto.getOperateUserErp())){
            result.parameterError("操作人不存在!");
            return result;
        }

        // 校验当前波次是否是最新波次
        checkIsRecentWave(stockInventoryScanDto, result);
        if(!result.codeSuccess()){
            return result;
        }

        // 非本波次包裹校验
        if(!checkIsExist(stockInventoryScanDto)) {
            result.hintMessage("非本波次包裹!");
            return result;
        }

        // 重复扫描
        if(checkIsInventory(stockInventoryScanDto)){
            result.customMessage(600,"该包裹已扫描!");
            return result;
        }

        return result;
    }

    /**
     * 校验包裹是否已盘点
     *
     * @param stockInventoryScanDto
     * @return
     */
    private boolean checkIsInventory(StockInventoryScanDto stockInventoryScanDto) {

        StockInventory stockInventory = new StockInventory();
        stockInventory.setPackageCode(stockInventoryScanDto.getPackageCode());
        stockInventory.setOperateSiteCode(stockInventoryScanDto.getOperateSiteCode());
        stockInventory.setWaveCode(stockInventoryScanDto.getWaveCode());
        stockInventory.setOperateUserErp(stockInventoryScanDto.getOperateUserErp());
        stockInventory.setInventoryStatus(StockInventoryEnum.INVENTORY_ALREADY.getCode());
        List<StockInventory> existInventoryList = stockInventoryDao.queryRecordsByPack(stockInventory);

        return CollectionUtils.isNotEmpty(existInventoryList);
    }

    /**
     * 校验是否最近波次
     *
     * @param stockInventoryScanDto
     * @return
     */
    private void checkIsRecentWave(StockInventoryScanDto stockInventoryScanDto, InvokeResult<StockInventoryResult> result) {

        // 获取最近已结束波次信息
        TransferWaveQueryCondition queryCondition = new TransferWaveQueryCondition();
        queryCondition.setNodeCode(toDmsSiteCode(stockInventoryScanDto.getOperateSiteCode()));
        queryCondition.setQueryTime(new Date());
        TransferWaveDto transferWaveDto = transferWaveManager.queryTransferWaveByQueryCondition(queryCondition);
        if(transferWaveDto == null){
            result.parameterError("不存在最近已完结波次!");
            return;
        }
        if(!Objects.equals(transferWaveDto.getWaveCode(), stockInventoryScanDto.getWaveCode())){
            result.confirmMessage("当前波次非最新波次，是否切换为最新波次？");
        }
    }

    /**
     * 校验包裹是否存在此波次
     *
     * @param stockInventoryScanDto
     * @return
     */
    private boolean checkIsExist(StockInventoryScanDto stockInventoryScanDto) {

        OperateSiteQueryVO queryVO = new OperateSiteQueryVO();
        queryVO.setPackageCode(stockInventoryScanDto.getPackageCode());
        queryVO.setSiteCode(stockInventoryScanDto.getOperateSiteCode());
        queryVO.setStartDate(stockInventoryScanDto.getWaveBeginTime().getTime());
        queryVO.setEndDate(stockInventoryScanDto.getWaveEndTime().getTime());
        queryVO.setPageNo(Constants.CONSTANT_NUMBER_ONE);
        queryVO.setPageSize(Constants.CONSTANT_NUMBER_ONE);
        List<UnsentPackageDetail> unsentPackageDetails = sortingInspectionSendingManager.searchUnsentPackageByPage(queryVO);
        if(CollectionUtils.isNotEmpty(unsentPackageDetails)){
            return true;
        }
        return false;
    }

    /**
     * 更新盘点数据
     *
     * @param stockInventoryScanDto
     */
    @Transactional(value = "main_undiv", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void updateInventory(StockInventoryScanDto stockInventoryScanDto) {
        // 盘点数据落库
        StockInventory queryCondition = new StockInventory();
        queryCondition.setPackageCode(stockInventoryScanDto.getPackageCode());
        queryCondition.setOperateSiteCode(stockInventoryScanDto.getOperateSiteCode());
        queryCondition.setWaveCode(stockInventoryScanDto.getWaveCode());
        queryCondition.setInventoryStatus(StockInventoryEnum.INVENTORY_ALREADY.getCode());
        List<StockInventory> existList = stockInventoryDao.queryRecordsByPack(queryCondition);
        if(CollectionUtils.isNotEmpty(existList)){
            // 更新非当前操作人记录的状态为'已被他人盘点'
            Set<String> waitUpdateErp = new HashSet<>();
            for (StockInventory inventory : existList) {
                if(!Objects.equals(inventory.getOperateUserErp(), stockInventoryScanDto.getOperateUserErp())){
                    waitUpdateErp.add(inventory.getOperateUserErp());
                }
            }
            queryCondition.setOperateUserErp(stockInventoryScanDto.getOperateUserErp());
            stockInventoryDao.updateStatusByErp(queryCondition, new ArrayList<String>(waitUpdateErp));
        }
        stockInventoryDao.add(convertToStockInventory(stockInventoryScanDto));
    }

    @Override
    public InvokeResult<List<StockInventory>> queryInventoryUnSendPacks(InventoryQuery inventoryQuery) {
        InvokeResult<List<StockInventory>> result = new InvokeResult<List<StockInventory>>();
        if(StringUtils.isEmpty(inventoryQuery.getWaveCode()) || inventoryQuery.getOperateSiteCode() == null
                || CollectionUtils.isEmpty(inventoryQuery.getPackList())){
            result.parameterError(InvokeResult.PARAM_ERROR);
            return result;
        }
        StockInventory stockInventory = new StockInventory();
        stockInventory.setWaveCode(inventoryQuery.getWaveCode());
        stockInventory.setOperateSiteCode(inventoryQuery.getOperateSiteCode());
        stockInventory.setInventoryStatus(StockInventoryEnum.INVENTORY_ALREADY.getCode());
        result.setData(stockInventoryDao.queryInventoryUnSendPacks(stockInventory, inventoryQuery.getPackList()));
        return result;
    }

    @Override
    public InvokeResult<Long> queryInventoryUnSendNum(InventoryQuery inventoryQuery) {
        InvokeResult<Long> result = new InvokeResult<Long>();
        InvokeResult<List<StockInventory>> listInvokeResult = queryInventoryUnSendPacks(inventoryQuery);
        if(!listInvokeResult.codeSuccess() || CollectionUtils.isEmpty(listInvokeResult.getData())){
            result.customMessage(InvokeResult.RESULT_PARAMETER_ERROR_CODE, InvokeResult.RESULT_NULL_MESSAGE);
            return result;
        }
        int inventoryNum = listInvokeResult.getData().size();
        result.setData(Long.parseLong(String.valueOf(inventoryNum)));
        return result;
    }

    private StockInventory convertToStockInventory(StockInventoryScanDto stockInventoryScanDto) {
        StockInventory stockInventory = new StockInventory();
        stockInventory.setPackageCode(stockInventoryScanDto.getPackageCode());
        stockInventory.setInventoryStatus(StockInventoryEnum.INVENTORY_ALREADY.getCode());
        stockInventory.setWaveCode(stockInventoryScanDto.getWaveCode());
        stockInventory.setWaveBeginTime(stockInventoryScanDto.getWaveBeginTime());
        stockInventory.setWaveEndTime(stockInventoryScanDto.getWaveEndTime());
        stockInventory.setInventoryTime(stockInventoryScanDto.getScanTime());
        stockInventory.setOperateSiteCode(stockInventoryScanDto.getOperateSiteCode());
        stockInventory.setOperateSiteName(stockInventoryScanDto.getOperateSiteName());
        stockInventory.setOperateUserErp(stockInventoryScanDto.getOperateUserErp());
        stockInventory.setOperateUserName(stockInventoryScanDto.getOperateUserName());
        stockInventory.setUpdateUserErp(stockInventoryScanDto.getOperateUserErp());
        return stockInventory;
    }

    @JProfiler(jKey = "dms.stock.StockInventoryServiceImpl.queryStockInventory", jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public InvokeResult<StockInventoryResult> queryStockInventory(Integer createSiteCode) {
        InvokeResult<StockInventoryResult> result = new InvokeResult<StockInventoryResult>();

        // 已验未发包裹数量
        int totalInventoryNum;
        // 已盘点数量
        int inventoryNum;
        TransferWaveDto transferWaveDto;
        try {
            // 获取最近已结束波次信息
            TransferWaveQueryCondition queryCondition = new TransferWaveQueryCondition();
            queryCondition.setNodeCode(toDmsSiteCode(createSiteCode));
            queryCondition.setQueryTime(new Date());
            transferWaveDto = transferWaveManager.queryTransferWaveByQueryCondition(queryCondition);
            if(transferWaveDto == null){
                logger.warn("根据条件【{}】未查询到波次信息!", JsonHelper.toJson(queryCondition));
                result.customMessage(600, "当前站点暂无最近波次信息!");
                return result;
            }

            // 获取已验未发包裹数量（总盘点数）
            totalInventoryNum = getTotalInventoryNum(createSiteCode, transferWaveDto.getWaveBeginTime(), transferWaveDto.getWaveEndTime());

            // 获取已盘点包裹数据
            inventoryNum = getInventoryNum(createSiteCode, transferWaveDto.getWaveCode());

        }catch (Exception e){
            logger.error("根据站点{}查询库存盘点数据异常!" ,createSiteCode, e);
            result.error();
            return result;
        }
        StockInventoryResult stockInventoryResult = new StockInventoryResult();
        int todoInventoryNum = Math.max(totalInventoryNum - inventoryNum, Constants.NUMBER_ZERO);
        stockInventoryResult.setTodoInventoryNum(todoInventoryNum >= TODO_INVENTORY_NUM_LIMIT ? TODO_INVENTORY_NUM_LIMIT_DISPLAY : String.valueOf(todoInventoryNum));
        stockInventoryResult.setInventoryNum(inventoryNum >= TODO_INVENTORY_NUM_LIMIT ? TODO_INVENTORY_NUM_LIMIT_DISPLAY : String.valueOf(inventoryNum));
        stockInventoryResult.setWaveCode(transferWaveDto.getWaveCode());
        stockInventoryResult.setWaveBeginTime(transferWaveDto.getWaveBeginTime() == null
                ? null : new Date(transferWaveDto.getWaveBeginTime()));
        stockInventoryResult.setWaveEndTime(transferWaveDto.getWaveEndTime() == null
                ? null : new Date(transferWaveDto.getWaveEndTime()));

        result.setData(stockInventoryResult);
        return result;
    }

    /**
     * 根据条件查询已盘点数量
     *
     * @param createSiteCode
     * @param waveCode
     * @return
     */
    private int getInventoryNum(Integer createSiteCode, String waveCode) {
        StockInventory stockInventory = new StockInventory();
        stockInventory.setOperateSiteCode(createSiteCode);
        stockInventory.setWaveCode(waveCode);
        stockInventory.setInventoryStatus(StockInventoryEnum.INVENTORY_ALREADY.getCode());
        return stockInventoryDao.queryInventoryNum(stockInventory);
    }

    /**
     * 根据条件查询已验未发包裹数量
     *
     * @param createSiteCode
     * @param waveBeginTime
     * @param waveEndTime
     * @return
     */
    private int getTotalInventoryNum(Integer createSiteCode, Long waveBeginTime, Long waveEndTime) {
        OperateSiteQueryVO queryVO = new OperateSiteQueryVO();
        queryVO.setSiteCode(createSiteCode);
        queryVO.setStartDate(waveBeginTime);
        queryVO.setEndDate(waveEndTime);
        Long unsentPackageTotal = sortingInspectionSendingManager.getUnsentPackageTotal(queryVO);
        return unsentPackageTotal == null ? Constants.NUMBER_ZERO : Integer.parseInt(String.valueOf(unsentPackageTotal));
    }

    /**
     * 根据站点ID查询站点编码
     *
     * @param siteCode
     * @return
     */
    private String toDmsSiteCode(Integer siteCode) {
        BaseStaffSiteOrgDto bDto = baseMajorManager.getBaseSiteBySiteId(siteCode);
        if (bDto == null) {
            return null;
        }
        return bDto.getDmsSiteCode();
    }
}
