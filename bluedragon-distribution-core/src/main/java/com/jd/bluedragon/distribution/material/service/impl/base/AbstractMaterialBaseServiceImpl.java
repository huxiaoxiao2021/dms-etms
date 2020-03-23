package com.jd.bluedragon.distribution.material.service.impl.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.material.dao.MaterialReceiveDao;
import com.jd.bluedragon.distribution.material.dao.MaterialReceiveFlowDao;
import com.jd.bluedragon.distribution.material.dao.MaterialSendDao;
import com.jd.bluedragon.distribution.material.dao.MaterialSendFlowDao;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialReceive;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialReceiveFlow;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialSend;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialSendFlow;
import com.jd.bluedragon.distribution.material.service.MaterialOperationService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName AbstractMaterialBaseServiceImpl
 * @Description
 * @Author wyh
 * @Date 2020/3/13 17:37
 **/
@Component
public abstract class AbstractMaterialBaseServiceImpl implements MaterialOperationService {

    protected static final int INSERT_NUMBER_UPPER_LIMIT = 100;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MaterialSendDao materialSendDao;

    @Autowired
    private MaterialSendFlowDao materialSendFlowDao;

    @Autowired
    private MaterialReceiveDao materialReceiveDao;

    @Autowired
    private MaterialReceiveFlowDao materialReceiveFlowDao;

    protected abstract Boolean checkReceiveParam(List<DmsMaterialReceive> materialReceives);

    protected abstract Boolean receiveBeforeOperation(List<DmsMaterialReceive> materialReceives);

    protected abstract Boolean receiveAfterOperation(List<DmsMaterialReceive> materialReceives);

    @Override
    @JProfiler(jKey = "DMS.WEB.AbstractMaterialBaseServiceImpl.saveMaterialReceive", jAppName= Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdResult<Boolean> saveMaterialReceive(List<DmsMaterialReceive> materialReceives, Boolean saveFlow) {
        JdResult<Boolean> result = new JdResult<>();
        result.toSuccess();
        if (!checkReceiveParam(materialReceives)) {
            result.toError("参数校验失败！");
            return result;
        }

        try {
            if (receiveBeforeOperation(materialReceives)) {

                result = saveMaterialReceiveAndFlow(materialReceives, saveFlow);
            }

            receiveAfterOperation(materialReceives);
        }
        catch (Throwable ex) {
            result.toError("服务器异常!");
            logger.error("Failed to save material receive data. body:[{}].", JsonHelper.toJson(materialReceives), ex);
            throw new RuntimeException("物资入库失败");
        }

        return result;
    }

    private JdResult<Boolean> saveMaterialReceiveAndFlow(List<DmsMaterialReceive> materialReceives, Boolean saveFlow) {
        JdResult<Boolean> result = new JdResult<>();
        result.toSuccess();

        List<DmsMaterialReceiveFlow> flows = new ArrayList<>(materialReceives.size());
        for (DmsMaterialReceive materialReceive : materialReceives) {
            flows.add(materialReceive.convert2ReceiveFlow());
        }

        if (materialReceives.size() <= INSERT_NUMBER_UPPER_LIMIT) {
            result.setData(materialReceiveDao.batchInsertOnDuplicate(materialReceives) > 0);
            if (saveFlow) {
                materialReceiveFlowDao.batchInsert(flows);
            }
        }
        else {
            for (int fromIndex = 0; fromIndex < materialReceives.size(); fromIndex = fromIndex + INSERT_NUMBER_UPPER_LIMIT) {
                int toIndex = fromIndex + INSERT_NUMBER_UPPER_LIMIT;
                if (toIndex > materialReceives.size()) {
                    toIndex = materialReceives.size();
                }
                List<DmsMaterialReceive> subList = materialReceives.subList(fromIndex, toIndex);
                List<DmsMaterialReceiveFlow> subFlows = flows.subList(fromIndex, toIndex);
                result.setData(materialReceiveDao.batchInsertOnDuplicate(subList) > 0);
                if (saveFlow) {
                    materialReceiveFlowDao.batchInsert(subFlows);
                }
            }
        }
        return result;
    }


    protected abstract Boolean checkSendParam(List<DmsMaterialSend> materialSends);

    protected abstract Boolean sendBeforeOperation(List<DmsMaterialSend> materialSends);

    protected abstract Boolean sendAfterOperation(List<DmsMaterialSend> materialSends);

    @Override
    @JProfiler(jKey = "DMS.WEB.AbstractMaterialBaseServiceImpl.saveMaterialSend", jAppName= Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdResult<Boolean> saveMaterialSend(List<DmsMaterialSend> materialSends, Boolean saveFlow) {
        JdResult<Boolean> result = new JdResult<>();
        result.toSuccess();
        if (!checkSendParam(materialSends)) {
            result.toError("参数校验失败！");
            return result;
        }

        try {
            if (sendBeforeOperation(materialSends)){

                result = this.saveMaterialSendAndFlow(materialSends, saveFlow);

            }

            sendAfterOperation(materialSends);
        }
        catch (Throwable ex) {
            result.toError("服务器异常!");
            logger.error("Failed to save material send data. body:[{}].", JsonHelper.toJson(materialSends), ex);
            throw new RuntimeException("物资出库失败");
        }

        return result;
    }

    private JdResult<Boolean> saveMaterialSendAndFlow(List<DmsMaterialSend> materialSends, Boolean saveFlow) {
        JdResult<Boolean> result = new JdResult<>();
        result.toSuccess();

        List<DmsMaterialSendFlow> flows = new ArrayList<>(materialSends.size());
        for (DmsMaterialSend materialSend : materialSends) {
            flows.add(materialSend.convert2SendFlow());
        }
        if (materialSends.size() <= INSERT_NUMBER_UPPER_LIMIT) {
            result.setData(materialSendDao.batchInsertOnDuplicate(materialSends) > 0);
            if (saveFlow) {
                materialSendFlowDao.batchInsert(flows);
            }
        }
        else {
            for (int fromIndex = 0; fromIndex < materialSends.size(); fromIndex = fromIndex + INSERT_NUMBER_UPPER_LIMIT) {
                int toIndex = fromIndex + INSERT_NUMBER_UPPER_LIMIT;
                if (toIndex > materialSends.size()) {
                    toIndex = materialSends.size();
                }
                List<DmsMaterialSend> subList = materialSends.subList(fromIndex, toIndex);
                List<DmsMaterialSendFlow> subFlows = flows.subList(fromIndex, toIndex);
                result.setData(materialSendDao.batchInsertOnDuplicate(subList) > 0);
                if (saveFlow) {
                    materialSendFlowDao.batchInsert(subFlows);
                }
            }
        }
        return result;
    }

    @Override
    @JProfiler(jKey = "DMS.WEB.AbstractMaterialBaseServiceImpl.listMaterialSendBySendCode", jAppName= Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdResult<List<DmsMaterialSend>> listMaterialSendBySendCode(String sendCode, Long createSiteCode) {
        JdResult<List<DmsMaterialSend>> result = new JdResult<>();
        result.toSuccess();

        if (StringUtils.isBlank(sendCode) || null == createSiteCode || createSiteCode <= 0) {
            result.toError("请求参数不合法！");
            return result;
        }
        if (!BusinessUtil.isSendCode(sendCode)) {
            result.toError("批次号错误！");
            return result;
        }

        try {
            result.setData(materialSendDao.listBySendCode(sendCode, createSiteCode));
        }
        catch (Exception ex) {
            result.toError("服务器异常!");
            logger.error("Failed to get material send records. sendCode:[{}], createSiteCode:[{}]", sendCode, createSiteCode, ex);
        }
        return result;
    }
}
