package com.jd.bluedragon.distribution.test.print;

import com.jd.bluedragon.distribution.command.JsonCommandServiceImpl;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;
import com.jd.bluedragon.distribution.test.utils.UtilsForTestCase;
import com.jd.bluedragon.distribution.testCore.base.EntityUtil;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * @author zhangzhongkai8
 * @date 2020/11/24  19:43
 */
@RunWith(MockitoJUnitRunner.class)
public class AddSecurityLogTestCase {

    @InjectMocks
    JsonCommandServiceImpl jsonCommandService;



    @Test
    public void testPreSell() throws Exception{
        String jsonCommand = "{\"programType\":40,\"versionCode\":\"201801128WM\",\"businessType\":1001,\"operateType\":100102,\"data\":\"{\\\"programType\\\":0,\\\"versionCode\\\":null,\\\"operateType\\\":100102,\\\"dmsSiteCode\\\":910,\\\"barCode\\\":\\\"JDX000155107572\\\",\\\"reBoxCode\\\":null,\\\"targetSiteCode\\\":0,\\\"nopaperFlg\\\":false,\\\"paperSizeCode\\\":\\\"1010\\\",\\\"trustBusinessFlag\\\":false,\\\"weightOperType\\\":1,\\\"startSiteType\\\":64,\\\"packOpeFlowFlg\\\":0,\\\"weightOperFlow\\\":{\\\"workModeCode\\\":0,\\\"barcode\\\":null,\\\"machineCode\\\":null,\\\"weight\\\":13.0,\\\"length\\\":0.0,\\\"width\\\":0.0,\\\"high\\\":0.0,\\\"volume\\\":0.0,\\\"scannerTime\\\":0},\\\"userCode\\\":10053,\\\"userName\\\":\\\"刑松\\\",\\\"userERP\\\":null,\\\"siteCode\\\":910,\\\"siteName\\\":\\\"北京马驹桥分拣中心\\\",\\\"operateTime\\\":null,\\\"cancelFeatherLetter\\\":false,\\\"featherLetterDeviceNo\\\":null,\\\"discernFlag\\\":false,\\\"businessId\\\":0,\\\"barCodeType\\\":5}\"}";
        String result = jsonCommandService.execute(jsonCommand);
    }
}
