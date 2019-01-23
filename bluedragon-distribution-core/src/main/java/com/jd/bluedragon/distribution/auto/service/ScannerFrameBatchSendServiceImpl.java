package com.jd.bluedragon.distribution.auto.service;

import com.alibaba.fastjson.TypeReference;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.request.BatchSendPrintImageRequest;
import com.jd.bluedragon.distribution.api.request.BatchSummaryPrintImageRequest;
import com.jd.bluedragon.distribution.api.response.BatchSendPrintImageResponse;
import com.jd.bluedragon.distribution.auto.dao.ScannerFrameBatchSendDao;
import com.jd.bluedragon.distribution.auto.domain.ScannerFrameBatchSend;
import com.jd.bluedragon.distribution.auto.domain.ScannerFrameBatchSendSearchArgument;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;
import com.jd.bluedragon.distribution.gantry.service.GantryDeviceService;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.sendprint.domain.PrintQueryCriteria;
import com.jd.bluedragon.distribution.sendprint.domain.SummaryPrintBoxEntity;
import com.jd.bluedragon.distribution.sendprint.domain.SummaryPrintResult;
import com.jd.bluedragon.distribution.sendprint.domain.SummaryPrintResultResponse;
import com.jd.bluedragon.distribution.sendprint.service.SendPrintService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.RestHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.common.util.StringUtils;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;

/**
 * Created by wangtingwei on 2016/12/8.
 */
@Service("scannerFrameBatchSendService")
public class ScannerFrameBatchSendServiceImpl implements ScannerFrameBatchSendService {

    private static final Log LOGGER = LogFactory.getLog(SimpleScannerFrameDispatchServiceImpl.class);

    private static final byte YN_DEFAULT = 1;

    //批次打印
    private final static int SENDCODE_PRINT_TYPE = 1;

    //汇总单打印
    private final static int SUMMARY_PRINT_TYPE = 2;

    @Autowired
    private ScannerFrameBatchSendDao scannerFrameBatchSendDao;

    @Autowired
    private SiteService siteService;

    @Autowired
    private SendPrintService sendPrintService;

    @Autowired
    private NewSealVehicleService newSealVehicleService;

    @Autowired
    GantryDeviceService gantryDeviceService;

    @Override
    public ScannerFrameBatchSend getAndGenerate(Date operateTime, Integer receiveSiteCode, GantryDeviceConfig config) {
        if (null == config) {
            throw new RuntimeException("the parameter of config can not be null");
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(MessageFormat.format("parameters is opeateTime:{0} receiveSiteCode:{1} config:{2}", operateTime, receiveSiteCode, config.toString()));
        }
        ScannerFrameBatchSend batchSend = scannerFrameBatchSendDao.selectCurrentBatchSend(config.getMachineId(), receiveSiteCode, operateTime);
        if (null == batchSend) {
            batchSend = genarateBatchSend(operateTime,receiveSiteCode,config);
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(MessageFormat.format("result:{0}", batchSend.toString()));
        }
        return batchSend;
    }

    @Override
    public ScannerFrameBatchSend getOrGenerate(Date operateTime, Integer receiveSiteCode, GantryDeviceConfig config, String packageCode) {
        if (null == config) {
            throw new RuntimeException("the parameter of config can not be null");
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(MessageFormat.format("parameters is opeateTime:{0} receiveSiteCode:{1} config:{2}", operateTime, receiveSiteCode, config.toString()));
        }
        ScannerFrameBatchSend batchSend = scannerFrameBatchSendDao.selectCurrentBatchSend(config.getMachineId(), receiveSiteCode, operateTime);

        if (null == batchSend) {
            batchSend = genarateBatchSend(operateTime,receiveSiteCode,config);
        }else{
            String send_code = batchSend.getSendCode();
            if (newSealVehicleService.checkSendCodeIsSealed(send_code)) {
                batchSend = genarateBatchSend(operateTime, receiveSiteCode, config);
                LOGGER.warn(MessageFormat.format("Current batchSend {0} already sealed，将生成新批次{1},操作时间为{2},包裹号为{3}",
                        send_code, batchSend.getSendCode(), DateHelper.formatDateTimeMs(operateTime), packageCode));
            }
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(MessageFormat.format("result:{0}", batchSend.toString()));
        }
        return batchSend;
    }

    @Override
    public boolean generateSend(ScannerFrameBatchSend domain) {
        return scannerFrameBatchSendDao.add(domain) > 0;
    }

    @Override
    public boolean submitPrint(long id, Integer operateUserId, String operateUserName) {
        return scannerFrameBatchSendDao.updatePrintTimes(id) > 0;
    }

    private ScannerFrameBatchSend genarateBatchSend(Date operateTime, Integer receiveSiteCode,GantryDeviceConfig config){
        ScannerFrameBatchSend batchSend = new ScannerFrameBatchSend();
        batchSend.setMachineId(config.getMachineId());
        batchSend.setCreateSiteCode(config.getCreateSiteCode());
        batchSend.setCreateSiteName(config.getCreateSiteName());
        batchSend.setReceiveSiteCode(receiveSiteCode);
        BaseStaffSiteOrgDto site = siteService.getSite(receiveSiteCode);
        if (null != site) {
            batchSend.setReceiveSiteName(site.getSiteName());
        }
        batchSend.setCreateTime(operateTime);
        batchSend.setCreateUserCode(config.getOperateUserId());
        batchSend.setCreateUserName(config.getOperateUserName());
        batchSend.setYn(YN_DEFAULT);
        batchSend.setUpdateTime(batchSend.getCreateTime());
        batchSend.setSendCode(SerialRuleUtil.generateSendCode(batchSend.getCreateSiteCode(), batchSend.getReceiveSiteCode(), batchSend.getCreateTime()));
        generateSend(batchSend);
        return batchSend;
    }

    /**
     * 查询历史记录
     *
     * @param argumentPager 分页查询对象
     * @return
     */
    @Override
    public Pager<List<ScannerFrameBatchSend>> getSplitPageList(Pager<ScannerFrameBatchSendSearchArgument> argumentPager) {
        argumentPager.init();
        long count = scannerFrameBatchSendDao.getSplitPageListCount(argumentPager);
        Pager<List<ScannerFrameBatchSend>> result = new Pager<List<ScannerFrameBatchSend>>(argumentPager.getPageNo(), argumentPager.getPageSize());
        result.setTotalSize((int) count);
        result.setData(scannerFrameBatchSendDao.getSplitPageList(argumentPager));
        return result;
    }

    @Override
    public Pager<List<ScannerFrameBatchSend>> getCurrentSplitPageList(Pager<ScannerFrameBatchSendSearchArgument> argumentPager) {
        argumentPager.init();
        long count = scannerFrameBatchSendDao.getCurrentSplitPageListCount(argumentPager);
        Pager<List<ScannerFrameBatchSend>> result = new Pager<List<ScannerFrameBatchSend>>(argumentPager.getPageNo(), argumentPager.getPageSize());
        result.setTotalSize((int) count);
        result.setData(scannerFrameBatchSendDao.getCurrentSplitPageList(argumentPager));
        return result;
    }

    /**
     * 通过ID获取其domain的方法
     */
    private List<ScannerFrameBatchSend> queryByIds(List<Long> ids) {
        List<ScannerFrameBatchSend> result = new ArrayList<ScannerFrameBatchSend>();
        result = scannerFrameBatchSendDao.queryByIds(ids);
        return result;
    }

    @Override
    public List<ScannerFrameBatchSend> queryByMachineIdAndTime(ScannerFrameBatchSendSearchArgument request) {
        List<ScannerFrameBatchSend> result = new ArrayList<ScannerFrameBatchSend>();
        result = scannerFrameBatchSendDao.queryByMachineIdAndTime(request);
        return result;
    }

    @Override
    public ScannerFrameBatchSend selectCurrentBatchSend(String machineId, long receiveSiteCode, Date operateTime) {
        ScannerFrameBatchSend result = new ScannerFrameBatchSend();
        result = scannerFrameBatchSendDao.selectCurrentBatchSend(machineId, receiveSiteCode, operateTime);
        return result;
    }

    @Override
    public BatchSendPrintImageResponse batchPrint(String url, ScannerFrameBatchSend scannerFrameBatchSend, Integer userId, String userName) {
        BatchSendPrintImageRequest itemRequest = new BatchSendPrintImageRequest();
        itemRequest.setSendCode(scannerFrameBatchSend.getSendCode());
        itemRequest.setCreateSiteCode((int)scannerFrameBatchSend.getCreateSiteCode());
        itemRequest.setCreateSiteName(scannerFrameBatchSend.getCreateSiteName());
        itemRequest.setReceiveSiteCode((int)scannerFrameBatchSend.getReceiveSiteCode());
        itemRequest.setReceiveSiteName(scannerFrameBatchSend.getReceiveSiteName());
        Integer packageSum = 0;
        /** 获取包裹的数据量 **/
        List<SendDetail> sendDetailList = gantryDeviceService.queryWaybillsBySendCode(scannerFrameBatchSend.getSendCode());
        if(sendDetailList != null && sendDetailList.size() > 0){
            packageSum = sendDetailList.size();//获取包裹的数量
        }
        itemRequest.setPackageNum(packageSum);
        BatchSendPrintImageResponse itemResponse = RestHelper.jsonPostForEntity(url,itemRequest,new TypeReference<BatchSendPrintImageResponse>(){});
        itemResponse.setPrintType(SENDCODE_PRINT_TYPE);//批次打印单
        /** ===================获取批次打印图片base64码结束================= **/
        /** =======================3.更新scanner_frame_batch_send表打印时间，打印次数开始================== **/
        this.submitPrint(scannerFrameBatchSend.getId(),userId,userName);
        return itemResponse;
    }

    @Override
    public BatchSendPrintImageResponse summaryPrint(String url,ScannerFrameBatchSend scannerFrameBatchSend,Integer userId,String userName) {
        PrintQueryCriteria criteria = new PrintQueryCriteria();
        criteria.setSendCode(scannerFrameBatchSend.getSendCode());
        criteria.setReceiveSiteCode((int)scannerFrameBatchSend.getReceiveSiteCode());
        criteria.setSiteCode((int)scannerFrameBatchSend.getCreateSiteCode());
        SummaryPrintResultResponse summaryPrintResultResponse =  sendPrintService.batchSummaryPrintQuery(criteria);
        if(summaryPrintResultResponse.getCode() != 200 ){
            return null;
        }
        List<SummaryPrintResult> summaryPrintResults = summaryPrintResultResponse.getData();//根据一个目的地一个批次号取出来的汇总合集 ，这个list应该只有一条数据
        SummaryPrintResult itemResult = new SummaryPrintResult();
        if(summaryPrintResults.size() == 1){
            itemResult = summaryPrintResults.get(0);
        }
        BatchSummaryPrintImageRequest summaryRequest = new BatchSummaryPrintImageRequest();//请求的打印数据类
        summaryRequest.setCreateSiteNo((int)scannerFrameBatchSend.getCreateSiteCode());
        summaryRequest.setCreateSiteName(scannerFrameBatchSend.getCreateSiteName());
        summaryRequest.setReceiveSiteNo((int)scannerFrameBatchSend.getReceiveSiteCode());
        summaryRequest.setReceiveSiteName(scannerFrameBatchSend.getReceiveSiteName());
        summaryRequest.setPrintTime(DateHelper.formatDateTime(new Date()));
        summaryRequest.setSendCode(scannerFrameBatchSend.getSendCode());
        summaryRequest.setSendTime(itemResult.getSendTime());
        summaryRequest.setTotalBoxNum(itemResult.getTotalBoxNum());//周转箱
        summaryRequest.setTotalPackageBarNum(itemResult.getTotalShouldSendPackageNum());//原包个数
        summaryRequest.setTotalNum(itemResult.getTotalBoxNum() + itemResult.getTotalShouldSendPackageNum());//合计 fixme
        List<SummaryPrintBoxEntity> itemBoxEntitys = new ArrayList<SummaryPrintBoxEntity>();
        itemBoxEntitys = itemResult.getDetails();
        int packageBarRecNum = 0;//应发
        int packageBarNum = 0;  //实发
        double volume = 0.00;  //体积
        if(itemBoxEntitys != null){
            for(int i=0 ;i< itemBoxEntitys.size();i++){
                packageBarRecNum += itemBoxEntitys.get(i).getPackageBarRecNum();//应发
                packageBarNum += itemBoxEntitys.get(i).getPackageBarNum();//实发
                volume += itemBoxEntitys.get(i).getVolume();//体积
            }
        }
        summaryRequest.setPackageBarRecNum(packageBarRecNum);
        summaryRequest.setPackageBarNum(packageBarNum);
        summaryRequest.setVolume(volume);
        BatchSendPrintImageResponse result = RestHelper.jsonPostForEntity(url,summaryRequest,new TypeReference<BatchSendPrintImageResponse>(){});
        result.setPrintType(SUMMARY_PRINT_TYPE);
        this.submitPrint(scannerFrameBatchSend.getId(),userId,userName);
        return result;

    }

    @Override
    public List<ScannerFrameBatchSend> queryAllReceiveSites(String createSiteCode, String machineId) {
        List<ScannerFrameBatchSend> results = new ArrayList<ScannerFrameBatchSend>();
        if(StringUtils.isNotBlank(machineId)){
            Map<String ,String> params = new HashMap<String, String>();
            params.put("createSiteCode",createSiteCode);
            params.put("machineId",machineId);
            results = scannerFrameBatchSendDao.queryAllReceiveSites(params);
        }
        return results;
    }

    @Override
    public Pager<List<ScannerFrameBatchSend>> queryAllHistoryBatchSend(Pager<ScannerFrameBatchSendSearchArgument> request) {
        request.init();
        long count = scannerFrameBatchSendDao.queryAllUnPrintCount(request);
        Pager<List<ScannerFrameBatchSend>> result = new Pager<List<ScannerFrameBatchSend>>(request.getPageNo(), request.getPageSize());
        result.setTotalSize((int) count);
        result.setData(scannerFrameBatchSendDao.queryAllUnPrint(request));
        return result;
    }

    @Override
    public long updateYnByMachineId(String machineId) {
        if (StringHelper.isEmpty(machineId)) {
            return 0;
        }
        return scannerFrameBatchSendDao.updateYnByMachineId(machineId);
    }
}
