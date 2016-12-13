package com.jd.bluedragon.distribution.auto.service;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.auto.dao.ScannerFrameBatchSendDao;
import com.jd.bluedragon.distribution.auto.domain.ScannerFrameBatchSend;
import com.jd.bluedragon.distribution.auto.domain.ScannerFrameBatchSendSearchArgument;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by wangtingwei on 2016/12/8.
 */
@Service("scannerFrameBatchSendService")
public class ScannerFrameBatchSendServiceImpl implements ScannerFrameBatchSendService {

    private static final Log LOGGER= LogFactory.getLog(SimpleScannerFrameDispatchServiceImpl.class);

    private static final byte YN_DEFAULT=1;
    @Autowired
    private ScannerFrameBatchSendDao scannerFrameBatchSendDao;

    @Autowired
    private SiteService siteService;

    @Override
    public ScannerFrameBatchSend getAndGenerate(Date operateTime, Integer receiveSiteCode, GantryDeviceConfig config) {
        if(null==config){
            throw new RuntimeException("the parameter of config can not be null");
        }
        if(LOGGER.isInfoEnabled()){
            LOGGER.info(MessageFormat.format("parameters is opeateTime:{0} receiveSiteCode:{1} config:{2}",operateTime,receiveSiteCode, config.toString()));
        }
        ScannerFrameBatchSend batchSend= scannerFrameBatchSendDao.selectCurrentBatchSend(config.getMachineId(),receiveSiteCode,operateTime);
        if(null==batchSend){
            batchSend=new ScannerFrameBatchSend();
            batchSend.setCreateSiteCode(config.getCreateSiteCode());
            batchSend.setCreateSiteName(config.getCreateSiteName());
            batchSend.setReceiveSiteCode(receiveSiteCode);
            BaseStaffSiteOrgDto site=siteService.getSite(receiveSiteCode);
            if(null!=site) {
                batchSend.setReceiveSiteName(site.getSiteName());
            }
            batchSend.setCreateTime(operateTime);
            batchSend.setCreateUserCode(config.getOperateUserId());
            batchSend.setCreateUserName(config.getOperateUserName());
            batchSend.setYn(YN_DEFAULT);
            batchSend.setUpdateTime(batchSend.getCreateTime());
            batchSend.setSendCode(SerialRuleUtil.generateSendCode(batchSend.getCreateSiteCode(),batchSend.getReceiveSiteCode(),batchSend.getCreateTime()));
            generateSend(batchSend);
        }
        if(LOGGER.isInfoEnabled()){
            LOGGER.info(MessageFormat.format("result:{0}",batchSend.toString()));
        }
        return batchSend;
    }

    @Override
    public boolean generateSend(ScannerFrameBatchSend domain) {
        return scannerFrameBatchSendDao.add(domain)>0;
    }

    @Override
    public boolean submitPrint(long id, Integer operateUserId, String operateUserName) {
        return scannerFrameBatchSendDao.updatePrintTimes(id)>0;
    }

    @Override
    public Pager<List<ScannerFrameBatchSend>> getSplitPageList(Pager<ScannerFrameBatchSendSearchArgument> argumentPager) {
        //scannerFrameBatchSendDao.getSplitPageListCount()
        return null;
    }

    @Override
    public Pager<List<ScannerFrameBatchSend>> getCurrentSplitPageList(Pager<ScannerFrameBatchSendSearchArgument> argumentPager) {
        return null;
    }
}
