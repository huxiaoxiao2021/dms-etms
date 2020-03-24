package com.jd.bluedragon.distribution.material.service.impl;

import com.google.common.collect.ImmutableList;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.request.material.batch.MaterialBatchSendRequest;
import com.jd.bluedragon.distribution.api.response.material.batch.MaterialTypeResponse;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.consumable.domain.PackingConsumableInfo;
import com.jd.bluedragon.distribution.consumable.service.PackingConsumableInfoService;
import com.jd.bluedragon.distribution.material.dao.MaterialReceiveDao;
import com.jd.bluedragon.distribution.material.dao.MaterialReceiveFlowDao;
import com.jd.bluedragon.distribution.material.dao.MaterialSendDao;
import com.jd.bluedragon.distribution.material.dao.MaterialSendFlowDao;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialReceive;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialReceiveFlow;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialSend;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialSendFlow;
import com.jd.bluedragon.distribution.material.enums.MaterialSendTypeEnum;
import com.jd.bluedragon.distribution.material.service.impl.SortingMaterialSendServiceImpl;
import com.jd.bluedragon.distribution.material.util.MaterialServiceFactory;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class SortingMaterialSendServiceImplTest {
    @Mock
    DefaultJMQProducer materialSendMQProducer;
    @Mock
    DefaultJMQProducer cancelMaterialSendMQProducer;
    @Mock
    MaterialSendDao materialSendDao;
    @Mock
    MaterialSendFlowDao materialSendFlowDao;
    @Mock
    PackingConsumableInfoService packingConsumableInfoService;
    @Mock
    SiteService siteService;
    @Mock
    MaterialReceiveDao materialReceiveDao;
    @Mock
    MaterialReceiveFlowDao materialReceiveFlowDao;
    @InjectMocks
    SortingMaterialSendServiceImpl sortingMaterialSendServiceImpl;

    private static List<DmsMaterialSend> dmsMaterialSends = new ArrayList<>();

    private static MaterialBatchSendRequest request = new MaterialBatchSendRequest(MaterialServiceFactory.MaterialSendModeEnum.MATERIAL_TYPE_BATCH_SEND.getCode());

    static {
        DmsMaterialSend dmsMaterialSend = new DmsMaterialSend();
        dmsMaterialSend.setCreateSiteCode(910L);
        dmsMaterialSend.setMaterialCode("MZ0000000000001");
        dmsMaterialSend.setSendType(MaterialSendTypeEnum.SEND_BY_BATCH.getCode());
        dmsMaterialSend.setSendCode("910-39-20191125184252014");
        dmsMaterialSends.add(dmsMaterialSend);

        request.setSendCode("910-39-20191125184252014");
        request.setSendDetails(new ArrayList<MaterialBatchSendRequest.MaterialSendByTypeDetail>());
        request.setUserErp("bjxings");
        request.setSiteCode(910);
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCheckSendParam() {
        Boolean result = sortingMaterialSendServiceImpl.checkSendParam(dmsMaterialSends);
        Assert.assertEquals(Boolean.TRUE, result);
    }

    @Test
    public void testSendBeforeOperation() {
        when(materialSendDao.deleteBySendCode(anyString(), anyLong())).thenReturn(0);

        Boolean result = sortingMaterialSendServiceImpl.sendBeforeOperation(dmsMaterialSends);
        Assert.assertEquals(Boolean.TRUE, result);
    }

    @Test
    public void testSendAfterOperation() {
        when(materialSendMQProducer.getTopic()).thenReturn("getTopicResponse");
        when(cancelMaterialSendMQProducer.getTopic()).thenReturn("getTopicResponse");
        when(packingConsumableInfoService.listPackingConsumableInfoByCodes((List<String>) any())).thenReturn(ImmutableList.of(new PackingConsumableInfo()));
        BaseStaffSiteOrgDto baseStaffSiteOrgDto = new BaseStaffSiteOrgDto();
        baseStaffSiteOrgDto.setSiteCode(910);
        baseStaffSiteOrgDto.setSiteName("马驹桥");
        when(siteService.getSite(anyInt())).thenReturn(baseStaffSiteOrgDto);

        Boolean result = sortingMaterialSendServiceImpl.sendAfterOperation(dmsMaterialSends);
        Assert.assertEquals(Boolean.TRUE, result);
    }

    @Test
    public void cancelMaterialSendBySendCodeTest() {
        when(materialSendDao.logicalDeleteBatchSendBySendCode(anyString(), anyLong(), anyString(), anyString())).thenReturn(0);
        when(materialSendDao.listBySendCode(anyString(), anyLong())).thenReturn(dmsMaterialSends);
        when(materialSendDao.batchInsert(ArgumentMatchers.<List<DmsMaterialSend>>any())).thenReturn(true);
        when(materialSendFlowDao.batchInsert(ArgumentMatchers.<List<DmsMaterialSendFlow>>any())).thenReturn(true);
        when(siteService.getSite(anyInt())).thenReturn(null);
        when(materialReceiveDao.batchInsert(ArgumentMatchers.<List<DmsMaterialReceive>>any())).thenReturn(true);
        when(materialReceiveFlowDao.batchInsert(ArgumentMatchers.<List<DmsMaterialReceiveFlow>>any())).thenReturn(true);

        JdResult<Boolean> result = sortingMaterialSendServiceImpl.cancelMaterialSendBySendCode(request);
        Assert.assertEquals(JdResult.CODE_SUC, result.getCode());
    }

    @Test
    public void testListSortingMaterialType() {
        when(packingConsumableInfoService.listByTypeCode(anyString())).thenReturn(ImmutableList.of(new PackingConsumableInfo()));

        JdResult<List<MaterialTypeResponse>> result = sortingMaterialSendServiceImpl.listSortingMaterialType(request);
        Assert.assertEquals(JdResult.CODE_SUC, result.getCode());
    }

    @Test
    public void testSaveMaterialReceive() {
        when(materialSendDao.batchInsert(ArgumentMatchers.<List<DmsMaterialSend>>any())).thenReturn(true);
        when(materialSendFlowDao.batchInsert(ArgumentMatchers.<List<DmsMaterialSendFlow>>any())).thenReturn(true);
        when(materialReceiveDao.batchInsertOnDuplicate(ArgumentMatchers.<List<DmsMaterialReceive>>any())).thenReturn(0);
        when(materialReceiveDao.batchInsert(ArgumentMatchers.<List<DmsMaterialReceive>>any())).thenReturn(true);
        when(materialReceiveFlowDao.batchInsert(ArgumentMatchers.<List<DmsMaterialReceiveFlow>>any())).thenReturn(true);

        JdResult<Boolean> result = sortingMaterialSendServiceImpl.saveMaterialReceive(ImmutableList.of(new DmsMaterialReceive()), Boolean.TRUE);
        Assert.assertEquals(JdResult.CODE_SUC, result.getCode());
    }

    @Test
    public void testSaveMaterialSend() {
        when(materialSendDao.batchInsertOnDuplicate(ArgumentMatchers.<List<DmsMaterialSend>>any())).thenReturn(0);
        when(materialSendDao.batchInsert(ArgumentMatchers.<List<DmsMaterialSend>>any())).thenReturn(true);
        when(materialSendFlowDao.batchInsert(ArgumentMatchers.<List<DmsMaterialSendFlow>>any())).thenReturn(true);
        when(materialReceiveDao.batchInsert(ArgumentMatchers.<List<DmsMaterialReceive>>any())).thenReturn(true);
        when(materialReceiveFlowDao.batchInsert(ArgumentMatchers.<List<DmsMaterialReceiveFlow>>any())).thenReturn(true);

        JdResult<Boolean> result = sortingMaterialSendServiceImpl.saveMaterialSend(dmsMaterialSends, Boolean.TRUE);
        Assert.assertEquals(JdResult.CODE_SUC, result.getCode());
    }

    @Test
    public void testListMaterialSendBySendCode() {
        when(materialSendDao.listBySendCode(anyString(), anyLong())).thenReturn(dmsMaterialSends);

        JdResult<List<DmsMaterialSend>> result = sortingMaterialSendServiceImpl.listMaterialSendBySendCode("910-39-20191125184252014", 1L);
        Assert.assertEquals(JdResult.CODE_SUC, result.getCode());
    }
}