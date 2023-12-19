package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.jy.dao.pickinggood.JyPickingSendRecordDao;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodTaskStatisticsDto;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingSendRecordEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 空铁提发记录服务
 * @Author zhengchengfa
 * @Date 2023/12/6 20:29
 * @Description
 */
@Service
public class JyPickingSendRecordServiceImpl implements JyPickingSendRecordService{

    private static final Logger log = LoggerFactory.getLogger(JyPickingSendRecordServiceImpl.class);

    @Autowired
    private JyPickingSendRecordDao jyPickingSendRecordDao;


    public String fetchWaitPickingBizIdByBarCode(Long siteId, String barCode) {
        JyPickingSendRecordEntity recordEntity = new JyPickingSendRecordEntity(siteId);
        recordEntity.setWaitScanCode(barCode);
        return jyPickingSendRecordDao.fetchWaitPickingBizIdByBarCode(recordEntity);
    }

    @Override
    public JyPickingSendRecordEntity fetchRealPickingRecordByBarCodeAndBizId(Long curSiteId, String barCode, String bizId) {
        JyPickingSendRecordEntity recordEntity = new JyPickingSendRecordEntity(curSiteId);
        recordEntity.setScanCode(barCode);
        recordEntity.setBizId(bizId);
        return jyPickingSendRecordDao.fetchRealPickingRecordByBarCodeAndBizId(recordEntity);
    }

    @Override
    public JyPickingSendRecordEntity latestPickingRecord(Long curSiteId, String bizId, String barCode) {
        JyPickingSendRecordEntity recordEntity = new JyPickingSendRecordEntity(curSiteId);
        recordEntity.setScanCode(barCode);
        recordEntity.setBizId(bizId);
        return jyPickingSendRecordDao.latestPickingRecord(recordEntity);
    }

    @Override
    public PickingGoodTaskStatisticsDto statisticsByBizId(Long siteId, String bizId, Long nextSiteId) {
        //待提
        Integer waitPickingTotalNum = this.countTaskWaitScanItemNum(bizId, siteId);

        //已提
        Integer realPickingTotalNum = this.countTaskRealScanItemNum(bizId, siteId);

        //多提
        JyPickingSendRecordEntity morePicking = new JyPickingSendRecordEntity(siteId);
        morePicking.setScanCode(bizId);
        morePicking.setMoreScanFlag(Constants.NUMBER_ONE);
        Integer morePickingTotalNum = jyPickingSendRecordDao.countTaskRealScanItemNum(morePicking);

        PickingGoodTaskStatisticsDto res = new PickingGoodTaskStatisticsDto();
        res.setWaitPickingTotalNum(waitPickingTotalNum);
        res.setRealPickingTotalNum(realPickingTotalNum);
        res.setMorePickingTotalNum(morePickingTotalNum);

        if(!Objects.isNull(nextSiteId)) {
            //待发
            JyPickingSendRecordEntity waitSendEntity = new JyPickingSendRecordEntity(siteId);
            waitSendEntity.setScanCode(bizId);
            waitSendEntity.setInitNextSiteId(nextSiteId);
            Integer waitSendTotalNum = jyPickingSendRecordDao.countTaskWaitScanItemNum(waitSendEntity);

            //已发
            JyPickingSendRecordEntity realSendEntity = new JyPickingSendRecordEntity(siteId);
            realSendEntity.setScanCode(bizId);
            realSendEntity.setInitNextSiteId(nextSiteId);
            Integer realSendTotalNum = jyPickingSendRecordDao.countTaskRealScanItemNum(realSendEntity);

            //多发
            JyPickingSendRecordEntity moreSendEntity = new JyPickingSendRecordEntity(siteId);
            moreSendEntity.setScanCode(bizId);
            moreSendEntity.setMoreScanFlag(Constants.NUMBER_ONE);
            moreSendEntity.setInitNextSiteId(nextSiteId);
            Integer moreSendTotalNum = jyPickingSendRecordDao.countTaskRealScanItemNum(moreSendEntity);

            res.setWaitSendTotalNum(waitSendTotalNum);
            res.setRealSendTotalNum(realSendTotalNum);
            res.setMoreSendTotalNum(moreSendTotalNum);
        }
        return res;
    }

    @Override
    public Integer countTaskRealScanItemNum (String bizId, Long siteId) {
        JyPickingSendRecordEntity realPicking = new JyPickingSendRecordEntity(siteId);
        realPicking.setScanCode(bizId);
        return jyPickingSendRecordDao.countTaskRealScanItemNum(realPicking);
    }

    @Override
    public Integer countTaskWaitScanItemNum(String bizId, Long siteId) {
        JyPickingSendRecordEntity waitPickingEntity = new JyPickingSendRecordEntity(siteId);
        waitPickingEntity.setScanCode(bizId);
        return jyPickingSendRecordDao.countTaskWaitScanItemNum(waitPickingEntity);
    }

    @Override
    public void savePickingRecord(JyPickingSendRecordEntity recordEntity) {
        jyPickingSendRecordDao.insertSelective(recordEntity);
    }

    @Override
    public void updatePickingGoodRecordByWaitScanCode(JyPickingSendRecordEntity updateEntity) {
        jyPickingSendRecordDao.updatePickingGoodRecordByWaitScanCode(updateEntity);
    }


}
