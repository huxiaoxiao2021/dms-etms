package com.jd.bluedragon.distribution.financialForKA.service.impl;

import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.financialForKA.dao.WaybillCodeCheckDao;
import com.jd.bluedragon.distribution.financialForKA.domain.KaCodeCheckCondition;
import com.jd.bluedragon.distribution.financialForKA.domain.WaybillCodeCheckCondition;
import com.jd.bluedragon.distribution.financialForKA.domain.WaybillCodeCheckDto;
import com.jd.ldop.basic.dto.BasicTraderInfoDTO;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2020/2/27 21:36
 */
@RunWith(MockitoJUnitRunner.class)
public class WaybillCodeCheckServiceImplTest {

    @InjectMocks
    private WaybillCodeCheckServiceImpl waybillCodeCheckService;

    @Mock
    private WaybillQueryManager waybillQueryManager;

    @Mock
    private BaseMinorManager baseMinorManager;

    @Mock
    private WaybillCodeCheckDao waybillCodeCheckDao;

    private KaCodeCheckCondition condition;

    @Before
    public void setUp() throws Exception {
        condition = new KaCodeCheckCondition();
        condition.setWaybillCode("JDVA00049341283");
        condition.setCheckResult(0);
        condition.setBusiCode("010K100125");
        condition.setOperateErp("bjxings");
    }

    @Test
    public void listData() {
        try {
            List<WaybillCodeCheckDto> list = new ArrayList<WaybillCodeCheckDto>();
            WaybillCodeCheckDto dto = new WaybillCodeCheckDto();
            dto.setWaybillCode("JDVA00049341283");
            list.add(dto);
            Mockito.when(waybillCodeCheckDao.queryByCondition(Mockito.any(KaCodeCheckCondition.class)))
                    .thenReturn(list);
            Mockito.when(waybillCodeCheckDao.queryCountByCondition(Mockito.any(KaCodeCheckCondition.class)))
                    .thenReturn(1);
            PagerResult<WaybillCodeCheckDto> pagerResult = waybillCodeCheckService.listData(condition);
            Assert.assertEquals(pagerResult.getRows().get(0).getWaybillCode(),dto.getWaybillCode());
        }catch (Exception e){
            //异常直接断言单测失败
            Assert.assertTrue(Boolean.FALSE);
        }
    }

    @Test
    public void getExportData() {
        try {
            List<WaybillCodeCheckDto> list = new ArrayList<WaybillCodeCheckDto>();
            WaybillCodeCheckDto dto = new WaybillCodeCheckDto();
            dto.setWaybillCode("JDVA00049341283");
            list.add(dto);
            Mockito.when(waybillCodeCheckDao.exportByCondition(Mockito.any(KaCodeCheckCondition.class)))
                    .thenReturn(list);
            List<List<Object>> exportData = waybillCodeCheckService.getExportData(condition);
            String waybillCode = (String)exportData.get(1).get(0);
            Assert.assertEquals(waybillCode,dto.getWaybillCode());
        }catch (Exception e){
            //异常直接断言单测失败
            Assert.assertTrue(Boolean.FALSE);
        }
    }

    @Test
    public void waybillCodeCheck() {
        try {
            Mockito.when(waybillCodeCheckDao.add(Mockito.any(WaybillCodeCheckDto.class)))
                    .thenReturn(1);
            Mockito.when(baseMinorManager.getBaseTraderById(Mockito.anyInt()))
                    .thenReturn(new BasicTraderInfoDTO());
            WaybillCodeCheckCondition condition = new WaybillCodeCheckCondition();
            condition.setBarCodeOfOne("JDVA00049341283");
            condition.setBarCodeOfTwo("JDVA00049341283");
            InvokeResult result = waybillCodeCheckService.waybillCodeCheck(condition);
            Assert.assertTrue(result.getCode()==200);
        }catch (Exception e){
            //异常直接断言单测失败
            Assert.assertTrue(Boolean.FALSE);
        }
    }
}