package ld;

import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.recyclematerial.request.BoxMaterialRelationJSFRequest;
import com.jd.bluedragon.external.gateway.service.RecycleMaterialGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2023/7/26
 * @Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
public class RecycleMaterialGatewayServiceTest {

    @Autowired
    RecycleMaterialGatewayService recycleMaterialGatewayService;

    @Test
    public void boxMaterialRelationAlter(){
        String jsonStr = "{\"bindFlag\":1,\"boxCode\":\"BC1001230726220000300529\",\"currentOperate\":{\"siteCode\":910,\"siteName\":\"北京马驹桥分拣中心\"},\"groupSearch\":false,\"materialCode\":\"AD20230726000002\",\"user\":{\"userCode\":17331,\"userErp\":\"wuyoude\",\"userName\":\"吴有德\"}}";
        BoxMaterialRelationJSFRequest request = JsonHelper.fromJsonUseGson(jsonStr,BoxMaterialRelationJSFRequest.class);
        JdVerifyResponse<Boolean> response = recycleMaterialGatewayService.boxMaterialRelationAlter(request);
        System.out.println(JsonHelper.toJson(response));
        //Assert.assertTrue(response.getData());
    }

    @Test
    public void boxMaterialRelationAlterForEach(){
        for (int i = 0; i < 10; i++) {
            try{
                boxMaterialRelationAlter();
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }

}
