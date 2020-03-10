package com.jd.bluedragon.distribution.material.service;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.material.dao.*;
import com.jd.bluedragon.distribution.material.domain.*;
import com.jd.bluedragon.distribution.material.enums.MaterialOperationStatusEnum;
import com.jd.bluedragon.distribution.material.enums.MaterialReceiveTypeEnum;
import com.jd.bluedragon.distribution.material.enums.MaterialScanTypeEnum;
import com.jd.bluedragon.distribution.material.enums.MaterialSendTypeEnum;
import com.jd.bluedragon.distribution.material.service.impl.MaterialOperationServiceImpl;
import com.jd.bluedragon.distribution.material.vo.RecycleMaterialScanQuery;
import com.jd.bluedragon.distribution.material.vo.RecycleMaterialScanVO;
import com.jd.ql.dms.common.web.mvc.api.PagerCondition;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

public class MaterialOperationServiceImplTest {

    private static final String BOARD_CODE = "B11111111111111";
    private static final String MATERIAL_CODE = "MZ111111111111";

    @Mock
    Logger LOGGER;
    @Mock
    DefaultJMQProducer boxInOutProducer;
    @Mock
    MaterialRelationDao materialRelationDao;
    @Mock
    MaterialReceiveDao materialReceiveDao;
    @Mock
    MaterialSendDao materialSendDao;
    @Mock
    MaterialReceiveFlowDao materialReceiveFlowDao;
    @Mock
    MaterialSendFlowDao materialSendFlowDao;
    @Mock
    SiteService siteService;
    @InjectMocks
    MaterialOperationServiceImpl materialOperationServiceImpl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testListMaterialRelations() throws Exception {
        when(materialRelationDao.listRelationsByReceiveCode(anyString())).thenReturn(Arrays.<DmsMaterialRelation>asList(new DmsMaterialRelation()));
        String receiveCode = "B11111111111111";
        JdResult<List<DmsMaterialRelation>> result = materialOperationServiceImpl.listMaterialRelations(receiveCode);
        Assert.assertEquals(JdResult.CODE_SUC, result.getCode());
    }

    @Test
    public void testSaveMaterialSend() throws Exception {

        List<DmsMaterialSend> sends = new ArrayList<>();
        DmsMaterialSend send = new DmsMaterialSend();
        send.setSendCode(BOARD_CODE);
        send.setReceiveSiteCode(910L);
        send.setCreateSiteCode(910L);
        send.setMaterialCode(MATERIAL_CODE);
        send.setSendType(MaterialSendTypeEnum.SEND_BY_CONTAINER.getCode());
        sends.add(send);

        when(boxInOutProducer.getTopic()).thenReturn("getTopicResponse");
        when(materialRelationDao.batchInsertOnDuplicate((List<DmsMaterialRelation>) any())).thenReturn(0);
        when(materialRelationDao.deleteByReceiveCode(anyString())).thenReturn(0);
        when(materialRelationDao.batchInsert((List<DmsMaterialRelation>) any())).thenReturn(true);
        when(materialReceiveDao.batchInsert((List<DmsMaterialReceive>) any())).thenReturn(true);
        when(materialSendDao.batchInsertOnDuplicate((List<DmsMaterialSend>) any())).thenReturn(0);
        when(materialSendDao.deleteBySendCode(anyString(), anyLong())).thenReturn(0);
        when(materialSendDao.batchInsert((List<DmsMaterialSend>) any())).thenReturn(true);
        when(materialReceiveFlowDao.batchInsert((List<DmsMaterialReceiveFlow>) any())).thenReturn(true);
        when(materialSendFlowDao.batchInsert((List<DmsMaterialSendFlow>) any())).thenReturn(true);
        when(siteService.getSite(anyInt())).thenReturn(null);

        JdResult<Boolean> result = materialOperationServiceImpl.saveMaterialSend(sends);
        Assert.assertEquals(JdResult.CODE_SUC, result.getCode());
    }

    @Test
    public void testSaveMaterialReceive() throws Exception {
        when(boxInOutProducer.getTopic()).thenReturn("getTopicResponse");
        when(materialRelationDao.batchInsertOnDuplicate((List<DmsMaterialRelation>) any())).thenReturn(0);
        when(materialRelationDao.deleteByReceiveCode(anyString())).thenReturn(0);
        when(materialRelationDao.batchInsert((List<DmsMaterialRelation>) any())).thenReturn(true);
        when(materialReceiveDao.batchInsertOnDuplicate((List<DmsMaterialReceive>) any())).thenReturn(0);
        when(materialReceiveDao.deleteByReceiveCode(anyString(), anyLong())).thenReturn(0);
        when(materialReceiveDao.batchInsert((List<DmsMaterialReceive>) any())).thenReturn(true);
        when(materialSendDao.batchInsert((List<DmsMaterialSend>) any())).thenReturn(true);
        when(materialReceiveFlowDao.batchInsert((List<DmsMaterialReceiveFlow>) any())).thenReturn(true);
        when(materialSendFlowDao.batchInsert((List<DmsMaterialSendFlow>) any())).thenReturn(true);
        when(siteService.getSite(anyInt())).thenReturn(null);

        List<DmsMaterialReceive> receives = new ArrayList<>();
        DmsMaterialReceive receive = new DmsMaterialReceive();
        receive.setReceiveCode(BOARD_CODE);
        receive.setCreateSiteCode(910L);
        receive.setMaterialCode(MATERIAL_CODE);
        receive.setReceiveType(MaterialReceiveTypeEnum.RECEIVE_BY_CONTAINER.getCode());
        receives.add(receive);

        JdResult<Boolean> result = materialOperationServiceImpl.saveMaterialReceive(receives);
        Assert.assertEquals(JdResult.CODE_SUC, result.getCode());
    }

    @Test
    public void testQueryByPagerCondition() throws Exception {
        when(materialRelationDao.queryReceiveAndSend((RecycleMaterialScanQuery) any())).thenReturn(new PagerResult<RecycleMaterialScanVO>());
        when(materialRelationDao.queryByPagerCondition((PagerCondition) any())).thenReturn(new PagerResult<DmsMaterialRelation>());
        when(materialReceiveDao.queryByPagerCondition((PagerCondition) any())).thenReturn(new PagerResult<DmsMaterialReceive>());
        when(materialSendDao.queryByPagerCondition((PagerCondition) any())).thenReturn(new PagerResult<DmsMaterialSend>());
        when(materialReceiveFlowDao.queryByPagerCondition((PagerCondition) any())).thenReturn(new PagerResult<DmsMaterialReceiveFlow>());
        when(materialSendFlowDao.queryByPagerCondition((PagerCondition) any())).thenReturn(new PagerResult<DmsMaterialSendFlow>());
        when(siteService.getSite(anyInt())).thenReturn(null);

        RecycleMaterialScanQuery query = new RecycleMaterialScanQuery();
        query.setCreateSiteCode(910L);
        query.setBoardCode("1");
        query.setMaterialCode("1");
        query.setMaterialType((byte)1);
        query.setScanType(MaterialScanTypeEnum.INBOUND.getCode());
        query.setMaterialStatus(MaterialOperationStatusEnum.INBOUND.getCode());
        query.setUserErp("bjxings");

        PagerResult<RecycleMaterialScanVO> result = materialOperationServiceImpl.queryByPagerCondition(query);
        Assert.assertNotNull(result);
    }
}
