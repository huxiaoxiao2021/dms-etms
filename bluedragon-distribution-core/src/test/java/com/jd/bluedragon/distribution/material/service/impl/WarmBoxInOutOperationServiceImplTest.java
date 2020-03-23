package com.jd.bluedragon.distribution.material.service.impl;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationH2Test;
import com.jd.bluedragon.distribution.material.dao.*;
import com.jd.bluedragon.distribution.material.domain.*;
import com.jd.bluedragon.distribution.material.enums.MaterialOperationStatusEnum;
import com.jd.bluedragon.distribution.material.enums.MaterialReceiveTypeEnum;
import com.jd.bluedragon.distribution.material.enums.MaterialScanTypeEnum;
import com.jd.bluedragon.distribution.material.enums.MaterialSendTypeEnum;
import com.jd.bluedragon.distribution.material.vo.RecycleMaterialScanQuery;
import com.jd.bluedragon.distribution.material.vo.RecycleMaterialScanVO;
import com.jd.ql.dms.common.web.mvc.api.PagerCondition;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

public class WarmBoxInOutOperationServiceImplTest extends AbstractDaoIntegrationH2Test {
    @Mock
    Logger LOGGER;
    @Mock
    DefaultJMQProducer boxInOutProducer;
    @Mock
    MaterialRelationDao materialRelationDao;
    @Mock
    MaterialSendDao materialSendDao;
    @Mock
    MaterialReceiveDao materialReceiveDao;
    @Mock
    SiteService siteService;
    @Mock
    Logger logger;
    @Mock
    MaterialSendFlowDao materialSendFlowDao;
    @Mock
    MaterialReceiveFlowDao materialReceiveFlowDao;
    @InjectMocks
    WarmBoxInOutOperationServiceImpl warmBoxInOutOperationServiceImpl;

    private static final String BOARD_CODE = "B11111111111111";
    private static final String MATERIAL_CODE = "MZ111111111111";
    private static final List<DmsMaterialSend> SENDS = new ArrayList<>();
    private static final List<DmsMaterialReceive> RECEIVES = new ArrayList<>();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        DmsMaterialReceive receive = new DmsMaterialReceive();
        receive.setReceiveCode(BOARD_CODE);
        receive.setCreateSiteCode(910L);
        receive.setMaterialCode(MATERIAL_CODE);
        receive.setReceiveType(MaterialReceiveTypeEnum.RECEIVE_BY_CONTAINER.getCode());
        RECEIVES.add(receive);


        DmsMaterialSend send = new DmsMaterialSend();
        send.setSendCode(BOARD_CODE);
        send.setReceiveSiteCode(910L);
        send.setCreateSiteCode(910L);
        send.setMaterialCode(MATERIAL_CODE);
        send.setSendType(MaterialSendTypeEnum.SEND_BY_CONTAINER.getCode());
        SENDS.add(send);
    }

    @Test
    public void testCheckReceiveParam() {
        Boolean result = warmBoxInOutOperationServiceImpl.checkReceiveParam(RECEIVES);
        Assert.assertEquals(Boolean.TRUE, result);
    }

    @Test
    public void testReceiveBeforeOperation() {
        when(materialRelationDao.batchInsertOnDuplicate(ArgumentMatchers.<List<DmsMaterialRelation>>any())).thenReturn(0);
        when(materialRelationDao.deleteByReceiveCode(anyString())).thenReturn(0);
        when(materialReceiveDao.deleteByReceiveCode(anyString(), anyLong())).thenReturn(0);

        Boolean result = warmBoxInOutOperationServiceImpl.receiveBeforeOperation(RECEIVES);
        Assert.assertEquals(Boolean.TRUE, result);
    }

    @Test
    public void testReceiveAfterOperation() {
        when(boxInOutProducer.getTopic()).thenReturn("getTopicResponse");
        when(siteService.getSite(anyInt())).thenReturn(null);

        Boolean result = warmBoxInOutOperationServiceImpl.receiveAfterOperation(RECEIVES);
        Assert.assertEquals(Boolean.TRUE, result);
    }

    @Test
    public void testCheckSendParam() {
        Boolean result = warmBoxInOutOperationServiceImpl.checkSendParam(SENDS);
        Assert.assertEquals(Boolean.TRUE, result);
    }

    @Test
    public void testSendBeforeOperation() {
        when(materialRelationDao.batchInsertOnDuplicate(ArgumentMatchers.<List<DmsMaterialRelation>>any())).thenReturn(0);
        when(materialRelationDao.deleteByReceiveCode(anyString())).thenReturn(0);
        when(materialSendDao.deleteBySendCode(anyString(), anyLong())).thenReturn(0);

        Boolean result = warmBoxInOutOperationServiceImpl.sendBeforeOperation(SENDS);
        Assert.assertEquals(Boolean.TRUE, result);
    }

    @Test
    public void testSendAfterOperation() {
        when(boxInOutProducer.getTopic()).thenReturn("getTopicResponse");
        when(siteService.getSite(anyInt())).thenReturn(null);

        Boolean result = warmBoxInOutOperationServiceImpl.sendAfterOperation(SENDS);
        Assert.assertEquals(Boolean.TRUE, result);
    }

    @Test
    public void testListMaterialRelations() {
        when(materialRelationDao.listRelationsByReceiveCode(anyString())).thenReturn(Arrays.<DmsMaterialRelation>asList(new DmsMaterialRelation()));
        String receiveCode = "B11111111111111";
        JdResult<List<DmsMaterialRelation>> result = warmBoxInOutOperationServiceImpl.listMaterialRelations(receiveCode);
        Assert.assertEquals(JdResult.CODE_SUC, result.getCode());
    }

    @Test
    public void testQueryByPagerCondition() {
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

        PagerResult<RecycleMaterialScanVO> result = warmBoxInOutOperationServiceImpl.queryByPagerCondition(new RecycleMaterialScanQuery());
        Assert.assertNotNull(result);
    }

    @Test
    public void testSaveMaterialReceive() {
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

        JdResult<Boolean> result = warmBoxInOutOperationServiceImpl.saveMaterialReceive(RECEIVES, Boolean.TRUE);
        Assert.assertEquals(JdResult.CODE_SUC, result.getCode());
    }

    @Test
    public void testSaveMaterialSend() {
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

        JdResult<Boolean> result = warmBoxInOutOperationServiceImpl.saveMaterialSend(SENDS, Boolean.TRUE);
        Assert.assertEquals(JdResult.CODE_SUC, result.getCode());
    }

    @Test
    public void testListMaterialSendBySendCode() {
        when(materialSendDao.listBySendCode(anyString(), anyLong())).thenReturn(Arrays.<DmsMaterialSend>asList(new DmsMaterialSend()));

        JdResult<List<DmsMaterialSend>> result = warmBoxInOutOperationServiceImpl.listMaterialSendBySendCode("910-39-20191125184252014", Long.valueOf(1));
        Assert.assertTrue(true);
    }
}
