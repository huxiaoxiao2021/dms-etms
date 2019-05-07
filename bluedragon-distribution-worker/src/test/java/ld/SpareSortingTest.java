package ld;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.reverse.service.ReverseSpareService;
import com.jd.bluedragon.distribution.sorting.domain.SortingVO;
import com.jd.bluedragon.distribution.sorting.service.SortingFactory;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.worker.sorting.SortingSplitTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context.xml")
public class SpareSortingTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ReverseSpareService reverseSpareService;

    @Test
    public void test(){

        String s = "{\"type\":3300,\"siteCode\":910,\"keyword1\":\"JD0000000044615\",\"keyword2\":\"JD0000000044615\",\"body\":\"[{\\\"boxCode\\\":\\\"TC1001190417190000000303\\\",\\\"receiveSiteCode\\\":25016,\\\"receiveSiteName\\\":\\\"北京3C备件库\\\",\\\"waybillCode\\\":\\\"JD0000000044615\\\",\\\"isCancel\\\":0,\\\"spareReason\\\":\\\"站点环节破损\\\",\\\"trackContent\\\":\\\"订单扫描异常【站点环节破损】，责任主体【终端】\\\",\\\"spareCode\\\":1,\\\"data\\\":[{\\\"productId\\\":\\\"107810\\\",\\\"productName\\\":\\\"索爱（sonyericsson ）K310C 手机(蓝色)\\\",\\\"spareCode\\\":\\\"PB2019041700001003\\\",\\\"productCode\\\":\\\"107810\\\",\\\"productPrice\\\":600.0,\\\"arrtCode1\\\":101,\\\"arrtCode2\\\":201,\\\"arrtCode3\\\":0,\\\"arrtCode4\\\":0,\\\"arrtDesc1\\\":\\\"商品外包装：新\\\",\\\"arrtDesc2\\\":\\\"主商品外观：新\\\",\\\"arrtDesc3\\\":\\\"\\\",\\\"arrtDesc4\\\":\\\"\\\"}],\\\"dutyCode\\\":151,\\\"dutyName\\\":\\\"终端\\\",\\\"id\\\":0,\\\"businessType\\\":20,\\\"userCode\\\":10053,\\\"userName\\\":\\\"邢松\\\",\\\"siteCode\\\":910,\\\"siteName\\\":\\\"北京马驹桥分拣中心\\\",\\\"operateTime\\\":\\\"2019-04-18 10:05:01\\\"}]\",\"boxCode\":\"TC1001190417190000000303\",\"receiveSiteCode\":25016}";
        TaskRequest request = JsonHelper.fromJson(s, TaskRequest.class);

        String json = request.getBody();
        Object[] array = JsonHelper.jsonToArray(json, Object[].class);


        for (Object element : array) {

                Map<String, Object> reverseSpareMap = (Map<String, Object>) element;
                List<Map<String, Object>> reverseSpareDtos = (List<Map<String, Object>>) reverseSpareMap
                        .get("data");
                for (Map<String, Object> dto : reverseSpareDtos) {
                    List<Map<String, Object>> tempReverseSpareDtos = Arrays
                            .asList(dto);
                    reverseSpareMap.put("data", tempReverseSpareDtos);
                    request.setKeyword2(String.valueOf(dto.get("spareCode")));
                    String eachJson = Constants.PUNCTUATION_OPEN_BRACKET
                            + JsonHelper.toJson(reverseSpareMap)
                            + Constants.PUNCTUATION_CLOSE_BRACKET;
                    Task task = this.taskService.toTask(request, eachJson);

                    try {
                        reverseSpareService.doReverseSpareTask(task);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

        }


    }

}
