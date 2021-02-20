package com.jd.bluedragon.distribution.material.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.material.dao.MaterialReceiveDao;
import com.jd.bluedragon.distribution.material.dao.MaterialRelationDao;
import com.jd.bluedragon.distribution.material.dao.MaterialSendDao;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialReceive;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialRelation;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialSend;
import com.jd.bluedragon.distribution.material.dto.BoxInOutMessage;
import com.jd.bluedragon.distribution.material.enums.MaterialReceiveTypeEnum;
import com.jd.bluedragon.distribution.material.enums.MaterialSendTypeEnum;
import com.jd.bluedragon.distribution.material.service.WarmBoxInOutOperationService;
import com.jd.bluedragon.distribution.material.service.impl.base.AbstractMaterialBaseServiceImpl;
import com.jd.bluedragon.distribution.material.vo.RecycleMaterialScanQuery;
import com.jd.bluedragon.distribution.material.vo.RecycleMaterialScanVO;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.util.DateUtil;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ql.dms.report.api.material.IMaterialFlowJsfService;
import com.jd.ql.dms.report.domain.Pager;
import com.jd.ql.dms.report.domain.material.DmsMaterialFlowDto;
import com.jd.ql.dms.report.domain.material.MaterialFlowCondition;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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

    @Autowired
    @Qualifier("materialFlowJsfService")
    private IMaterialFlowJsfService materialFlowJsfService;

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
        List<RecycleMaterialScanVO> scanVOS = new ArrayList<>();
        result.setRows(scanVOS);
        MaterialFlowCondition condition = new MaterialFlowCondition();
        BeanUtils.copyProperties(query, condition);
        condition.setStartTime(DateUtil.format(query.getStartTime(), DateUtil.FORMAT_DATE_TIME));
        condition.setEndTime(DateUtil.format(query.getEndTime(), DateUtil.FORMAT_DATE_TIME));
        Pager<DmsMaterialFlowDto> pager = materialFlowJsfService.queryByPage(condition);
        if (null != pager && CollectionUtils.isNotEmpty(pager.getData())) {
            for (DmsMaterialFlowDto item : pager.getData()) {
                RecycleMaterialScanVO scanVO = new RecycleMaterialScanVO();
                scanVO.setMaterialType(item.getMaterialType().byteValue());
                scanVO.setMaterialCode(item.getMaterialCode());
                scanVO.setBoardCode(item.getReceiveCode());
                scanVO.setScanType(item.getScanType().byteValue());
                scanVO.setCreateSiteCode(item.getCreateSiteCode());
                scanVO.setCreateSiteName(this.getSiteName(item.getCreateSiteCode()));
                scanVO.setUserErp(item.getUpdateUserErp());
                scanVO.setOperateTime(DateUtil.parse(item.getUpdateTime(), DateUtil.FORMAT_DATE_TIME));
                scanVO.setMaterialStatus(item.getMaterialStatus().byteValue());
                scanVOS.add(scanVO);
            }
            result.setRows(scanVOS);
            result.setTotal(pager.getTotal().intValue());
        }
        
        return result;
    }

    private String getSiteName(Long createSiteCode) {
        BaseStaffSiteOrgDto baseStaffSiteOrgDto = siteService.getSite(createSiteCode.intValue());
        return null == baseStaffSiteOrgDto ? StringUtils.EMPTY : baseStaffSiteOrgDto.getSiteName();
    }
}
