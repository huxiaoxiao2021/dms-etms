package com.jd.bluedragon.distribution.material.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.material.dao.MaterialReceiveDao;
import com.jd.bluedragon.distribution.material.dao.MaterialRelationDao;
import com.jd.bluedragon.distribution.material.dao.MaterialSendDao;
import com.jd.bluedragon.distribution.material.domain.*;
import com.jd.bluedragon.distribution.material.dto.BoxInOutMessage;
import com.jd.bluedragon.distribution.material.enums.MaterialOperationStatusEnum;
import com.jd.bluedragon.distribution.material.enums.MaterialReceiveTypeEnum;
import com.jd.bluedragon.distribution.material.enums.MaterialScanTypeEnum;
import com.jd.bluedragon.distribution.material.enums.MaterialSendTypeEnum;
import com.jd.bluedragon.distribution.material.service.WarmBoxInOutOperationService;
import com.jd.bluedragon.distribution.material.service.impl.base.AbstractMaterialBaseServiceImpl;
import com.jd.bluedragon.distribution.material.vo.RecycleMaterialScanQuery;
import com.jd.bluedragon.distribution.material.vo.RecycleMaterialScanVO;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName WarmBoxInOutOperationServiceImpl
 * @Description
 * @Author wyh
 * @Date 2020/3/13 18:20
 **/
@Service("warmBoxInOutOperationService")
public class WarmBoxInOutOperationServiceImpl extends AbstractMaterialBaseServiceImpl implements WarmBoxInOutOperationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WarmBoxInOutOperationServiceImpl.class);

    @Qualifier("boxInOutProducer")
    @Autowired
    private DefaultJMQProducer boxInOutProducer;

    @Autowired
    private MaterialRelationDao materialRelationDao;

    @Autowired
    private MaterialSendDao materialSendDao;

    @Autowired
    private MaterialReceiveDao materialReceiveDao;

    @Autowired
    private SiteService siteService;

    @Override
    protected Boolean checkReceiveParam(List<DmsMaterialReceive> materialReceives) {
        return !CollectionUtils.isEmpty(materialReceives);
    }

    @Override
    protected Boolean receiveBeforeOperation(List<DmsMaterialReceive> materialReceives) {
        String receiveCode = materialReceives.get(0).getReceiveCode();
        Long createSiteCode = materialReceives.get(0).getCreateSiteCode();

        List<DmsMaterialRelation> relations = new ArrayList<>();
        for (DmsMaterialReceive materialReceive : materialReceives) {
            relations.add(materialReceive.convert2Relation());
        }

        // 按容器收货重新绑定收货关系
        if (MaterialReceiveTypeEnum.RECEIVE_BY_CONTAINER.getCode() == materialReceives.get(0).getReceiveType()) {
            materialReceiveDao.deleteByReceiveCode(receiveCode, createSiteCode);
            materialRelationDao.deleteByReceiveCode(receiveCode);
        }

        if (!CollectionUtils.isEmpty(relations)) {
            saveMaterialRelations(relations);
        }

        return true;
    }

    private void saveMaterialRelations(List<DmsMaterialRelation> relations) {
        if (relations.size() <= INSERT_NUMBER_UPPER_LIMIT) {
            materialRelationDao.batchInsertOnDuplicate(relations);
        }
        else {
            for (int fromIndex = 0; fromIndex < relations.size(); fromIndex = fromIndex + INSERT_NUMBER_UPPER_LIMIT) {
                int toIndex = fromIndex + INSERT_NUMBER_UPPER_LIMIT;
                if (toIndex > relations.size()) {
                    toIndex = relations.size();
                }
                List<DmsMaterialRelation> subList = relations.subList(fromIndex, toIndex);
                materialRelationDao.batchInsertOnDuplicate(subList);
            }
        }
    }

    private BoxInOutMessage convertMaterialReceive2Message(DmsMaterialReceive receive) {
        BoxInOutMessage boxInOutMessage = setBoxInOutDto(receive.getMaterialCode(), receive.getCreateSiteCode(), receive.getCreateUserErp(), receive.getCreateUserName());
        boxInOutMessage.setInOutType(1);
        return boxInOutMessage;
    }

    private BoxInOutMessage convertMaterialSend2Message(DmsMaterialSend send) {
        BoxInOutMessage boxInOutMessage = setBoxInOutDto(send.getMaterialCode(), send.getCreateSiteCode(), send.getCreateUserErp(), send.getCreateUserName());
        boxInOutMessage.setInOutType(2);
        return boxInOutMessage;
    }

    private BoxInOutMessage setBoxInOutDto(String materialCode, Long createSiteCode, String createUserErp, String createUserName) {
        BoxInOutMessage boxInOutMessage = new BoxInOutMessage();
        boxInOutMessage.setBoxNo(materialCode);
        boxInOutMessage.setDmsId(createSiteCode.intValue());
        boxInOutMessage.setDmsName(this.getSiteName(createSiteCode));
        boxInOutMessage.setErpUserCode(createUserErp);
        boxInOutMessage.setErpUserName(createUserName);
        boxInOutMessage.setOperateTime(new Date());
        return boxInOutMessage;
    }

    @Override
    protected Boolean receiveAfterOperation(List<DmsMaterialReceive> materialReceives) {
        List<BoxInOutMessage> msgDtos = new ArrayList<>(materialReceives.size());
        for (DmsMaterialReceive materialReceive : materialReceives) {
            msgDtos.add(this.convertMaterialReceive2Message(materialReceive));
        }

        sendInOutMQ(msgDtos);

        return true;
    }

    @Override
    protected Boolean checkSendParam(List<DmsMaterialSend> materialSends) {
        return !CollectionUtils.isEmpty(materialSends);
    }

    @Override
    protected Boolean sendBeforeOperation(List<DmsMaterialSend> materialSends) {
        String sendCode = materialSends.get(0).getSendCode();
        Long createSiteCode = materialSends.get(0).getCreateSiteCode();

        List<DmsMaterialRelation> relations = new ArrayList<>();
        for (DmsMaterialSend materialSend : materialSends) {
            relations.add(materialSend.convert2Relation());
        }

        // 按容器收货重新绑定发货关系
        if (MaterialSendTypeEnum.SEND_BY_CONTAINER.getCode() == materialSends.get(0).getSendType()) {
            materialSendDao.deleteBySendCode(sendCode, createSiteCode);
            materialRelationDao.deleteByReceiveCode(sendCode);
        }

        if (!CollectionUtils.isEmpty(relations)) {
            saveMaterialRelations(relations);
        }

        return true;
    }

    @Override
    protected Boolean sendAfterOperation(List<DmsMaterialSend> materialSends) {
        List<BoxInOutMessage> messages = new ArrayList<>(materialSends.size());
        for (DmsMaterialSend materialSend : materialSends) {
            messages.add(this.convertMaterialSend2Message(materialSend));
        }

        sendInOutMQ(messages);

        return true;
    }

    private void sendInOutMQ(List<BoxInOutMessage> boxInOutMessages) {
        List<Message> messages = new ArrayList<>(boxInOutMessages.size());
        for (BoxInOutMessage boxInOutMessage : boxInOutMessages) {
            Message message = new Message();
            message.setTopic(boxInOutProducer.getTopic());
            message.setText(JsonHelper.toJson(boxInOutMessage));
            message.setBusinessId(boxInOutMessage.getBoxNo());
            messages.add(message);
        }
        boxInOutProducer.batchSendOnFailPersistent(messages);
    }

    @Override
    @JProfiler(jKey = "DMS.WEB.WarmBoxInOutOperationServiceImpl.listMaterialRelations", jAppName= Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdResult<List<DmsMaterialRelation>> listMaterialRelations(String receiveCode) {
        JdResult<List<DmsMaterialRelation>> result = new JdResult<>();
        result.toSuccess();

        if (StringUtils.isBlank(receiveCode)) {
            result.toError("请求参数为空！");
            return result;
        }
        if (!BusinessUtil.isBoardCode(receiveCode)) {
            result.toError("板号输入错误！");
            return result;
        }

        try {
            result.setData(materialRelationDao.listRelationsByReceiveCode(receiveCode));
        }
        catch (Exception ex) {
            result.toError();
            LOGGER.error("Failed to get material relations. receiveCode:[{}]", receiveCode, ex);
        }
        return result;
    }

    @Override
    public PagerResult<RecycleMaterialScanVO> queryByPagerCondition(RecycleMaterialScanQuery query) {
        PagerResult<RecycleMaterialScanVO> result = new PagerResult<>();

        // 只查收货
        if ((null != query.getMaterialStatus() && MaterialOperationStatusEnum.INBOUND.getCode() == query.getMaterialStatus()) ||
                (null != query.getScanType() && MaterialScanTypeEnum.INBOUND.getCode() == query.getScanType())) {

            this.createReceiveScanVO(result, query);
        }
        // 只查发货
        else if ((null != query.getMaterialStatus() && MaterialOperationStatusEnum.OUTBOUND.getCode() == query.getMaterialStatus()) ||
                (null != query.getScanType() && MaterialScanTypeEnum.OUTBOUND.getCode() == query.getScanType())){

            this.createSendScanVO(result, query);

        }
        // 发货收货Union查询
        else {

            this.createReceiveAndSendVO(result, query);
        }

        return result;
    }

    private void createReceiveScanVO(PagerResult<RecycleMaterialScanVO> result, RecycleMaterialScanQuery query) {
        List<RecycleMaterialScanVO> scanVOS = new ArrayList<>();
        PagerResult<DmsMaterialReceive> pageResult = materialReceiveDao.queryByPagerCondition(query);
        if (!CollectionUtils.isEmpty(pageResult.getRows())) {
            for (DmsMaterialReceive row : pageResult.getRows()) {
                RecycleMaterialScanVO vo = new RecycleMaterialScanVO();
                vo.setBoardCode(row.getReceiveCode());
                vo.setMaterialCode(row.getMaterialCode());
                vo.setCreateSiteName(this.getSiteName(row.getCreateSiteCode()));
                vo.setMaterialStatus(MaterialOperationStatusEnum.INBOUND.getCode());
                vo.setMaterialType(row.getMaterialType());
                vo.setOperateTime(row.getUpdateTime());
                vo.setScanType(MaterialScanTypeEnum.INBOUND.getCode());
                vo.setUserErp(row.getUpdateUserErp());
                scanVOS.add(vo);
            }
        }
        result.setRows(scanVOS);
        result.setTotal(pageResult.getTotal());
    }

    private void createSendScanVO(PagerResult<RecycleMaterialScanVO> result, RecycleMaterialScanQuery query) {
        List<RecycleMaterialScanVO> scanVOS = new ArrayList<>();
        PagerResult<DmsMaterialSend> pagerResult = materialSendDao.queryByPagerCondition(query);
        if (!CollectionUtils.isEmpty(pagerResult.getRows())) {
            for (DmsMaterialSend row : pagerResult.getRows()) {
                RecycleMaterialScanVO vo = new RecycleMaterialScanVO();
                vo.setBoardCode(row.getSendCode());
                vo.setMaterialCode(row.getMaterialCode());
                vo.setCreateSiteName(this.getSiteName(row.getCreateSiteCode()));
                vo.setMaterialStatus(MaterialOperationStatusEnum.OUTBOUND.getCode());
                vo.setMaterialType(row.getMaterialType());
                vo.setOperateTime(row.getUpdateTime());
                vo.setScanType(MaterialScanTypeEnum.OUTBOUND.getCode());
                vo.setUserErp(row.getUpdateUserErp());
                scanVOS.add(vo);
            }
        }
        result.setRows(scanVOS);
        result.setTotal(pagerResult.getTotal());
    }

    private void createReceiveAndSendVO(PagerResult<RecycleMaterialScanVO> result, RecycleMaterialScanQuery query) {
        List<RecycleMaterialScanVO> scanVOS = new ArrayList<>();
        PagerResult<RecycleMaterialScanVO> pagerResult = materialRelationDao.queryReceiveAndSend(query);
        if (!CollectionUtils.isEmpty(pagerResult.getRows())) {
            for (RecycleMaterialScanVO row : pagerResult.getRows()) {
                RecycleMaterialScanVO vo = new RecycleMaterialScanVO();
                vo.setBoardCode(row.getBoardCode());
                vo.setMaterialCode(row.getMaterialCode());
                vo.setCreateSiteName(this.getSiteName(row.getCreateSiteCode()));
                vo.setMaterialStatus(row.getMaterialStatus());
                vo.setMaterialType(row.getMaterialType());
                vo.setOperateTime(row.getOperateTime());
                vo.setScanType(row.getScanType());
                vo.setUserErp(row.getUserErp());
                scanVOS.add(vo);
            }
        }
        result.setRows(scanVOS);
        result.setTotal(pagerResult.getTotal());
    }

    private String getSiteName(Long createSiteCode) {
        BaseStaffSiteOrgDto baseStaffSiteOrgDto = siteService.getSite(createSiteCode.intValue());
        return null == baseStaffSiteOrgDto ? StringUtils.EMPTY : baseStaffSiteOrgDto.getSiteName();
    }
}
