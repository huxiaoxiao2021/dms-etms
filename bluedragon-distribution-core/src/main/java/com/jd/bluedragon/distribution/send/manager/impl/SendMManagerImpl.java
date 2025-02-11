package com.jd.bluedragon.distribution.send.manager.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.distribution.api.request.box.BoxReq;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.external.constants.BoxStatusEnum;
import com.jd.bluedragon.distribution.external.constants.OpBoxNodeEnum;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.manager.SendMManager;
import com.jd.bluedragon.utils.CollectionHelper;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by hanjiaxing1 on 2018/10/16.
 */
@Component
public class SendMManagerImpl implements SendMManager {

    private static final String DB_TABLE_NAME = "send_m";

    @Autowired
    private SequenceGenAdaptor sequenceGenAdaptor;

    @Autowired
    private SendMDao sendMDao;

    @Autowired
    private BoxService boxService;

    @Autowired
    private DmsConfigManager dmsConfigManager;

    @Override
    @JProfiler(jKey = "DMSWEB.SendMManagerImpl.add", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public Integer add(String namespace, SendM sendM) {
        // 生成主键ID
        sendM.setSendMId(sequenceGenAdaptor.newId(DB_TABLE_NAME));
        Integer result = sendMDao.add(namespace, sendM);
        if (result > 0) {
            //关闭箱
            updateBoxStatus(sendM);
        }
        return result;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.SendMManagerImpl.insertSendM", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public boolean insertSendM(SendM sendM) {
        // 生成主键ID
        sendM.setSendMId(sequenceGenAdaptor.newId(DB_TABLE_NAME));
        boolean result = sendMDao.insertSendM(sendM);
        if (result) {
            //关闭箱
            updateBoxStatus(sendM);
        }
        return result;
    }

    /**
     * 通过箱号和始发地获取发货数据
     * @param sendM
     * @return
     */
    @Override
    public List<SendM> findSendMByBoxCode(SendM sendM){
        return sendMDao.findSendMByBoxCode(sendM);
    }


    /**
     * 关闭箱
     * @param sendM
     */
    private void updateBoxStatus(SendM sendM){
        //构造参数
        BoxReq boxReq = new BoxReq();
        boxReq.setBoxCode(sendM.getBoxCode());
        boxReq.setBoxStatus(BoxStatusEnum.CLOSE.getStatus());
        boxReq.setOpNodeCode(OpBoxNodeEnum.SEND.getNodeCode());
        boxReq.setOpNodeName(OpBoxNodeEnum.SEND.getNodeName());
        boxReq.setOpSiteCode(sendM.getCreateSiteCode());
        boxReq.setOpSiteName("");
        boxReq.setOpErp(sendM.getCreateUser());
        boxReq.setOpTime(sendM.getOperateTime());
        boxReq.setOpDescription(String.format("%s操作发货，关闭此箱号%s的箱子", sendM.getCreateUser(),sendM.getBoxCode()));
        //更新逻辑
        boxService.updateBoxStatus(boxReq);
    }

    /**
     * <ul>
     *     <li>批量保存sendM</li>
     *     <li>箱号状态置为关闭</li>
     * </ul>
     *
     * <p>需要手动生成主键ID</p>
     *
     * @param sendMList
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.SendMManagerImpl.batchSaveSendM", mState = {JProEnum.TP}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public int batchSaveSendM(List<SendM> sendMList) {

        // 生成主键ID
        for (SendM sendM : sendMList) {
            sendM.setSendMId(sequenceGenAdaptor.newId(DB_TABLE_NAME));
        }

        int insertDbRows = dmsConfigManager.getPropertyConfig().getInsertDbRowsOneTime();
        List<List<SendM>> splitList = CollectionHelper.splitList(sendMList, insertDbRows);
        int affectedRows = 0;
        for (List<SendM> subList : splitList) {

            affectedRows += sendMDao.addBatch(subList);

            for (SendM sendM : subList) {
                updateBoxStatus(sendM);
            }
        }

        return affectedRows;
    }

    /**
     * 批量获取发货数据
     * 超过最大时不返回数据
     * @param sendM
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.SendMManagerImpl.batchQuerySendMList", mState = {JProEnum.TP}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public List<SendM> batchQuerySendMListBySiteAndBoxes(SendM sendM) {
        if(sendM.getBoxCodeList() != null && Constants.DB_IN_MAX_SIZE > sendM.getBoxCodeList().size()) {
            return sendMDao.batchQuerySendMListBySiteAndBoxes(sendM);
        }
        return new ArrayList<>();
    }
}
