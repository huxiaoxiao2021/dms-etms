package ld;

import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.domain.SortingVO;
import com.jd.bluedragon.distribution.sorting.service.SortingFactory;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.worker.sorting.SortingSplitTask;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context.xml")
public class SortingTest {


    @Autowired
    private SortingFactory sortingFactory;

    @Test
    public void test(){

        String json = "{\"body\":\"[{\\n  \\\"receiveSiteCode\\\" : 39,\\n  \\\"receiveSiteName\\\" : \\\"石景山营业部\\\",\\n  \\\"boxCode\\\" : \\\"BC1001190403130000000202\\\",\\n  " +
                "\\\"packageCode\\\" : \\\"JDVA00001832840\\\",\\n  \\\"isCancel\\\" : 0,\\n  \\\"isLoss\\\" : 0,\\n  \\\"featureType\\\" : 0,\\n  " +
                "\\\"bsendCode\\\" : \\\"\\\",\\n  \\\"id\\\" : 186398,\\n  \\\"businessType\\\" : 10,\\n  \\\"userCode\\\" : 10053,\\n  \\\"userName\\\" : \\\"邢松\\\",\\n  " +
                "\\\"siteCode\\\" : 910,\\n  \\\"siteName\\\" : \\\"北京马驹桥分拣中心\\\",\\n  \\\"operateTime\\\" : \\\"2019-04-03 14:57:41.000\\\"\\n}]\"," +
                "\"boxCode\":\"BC1001190403130000000202\",\"businessType\":10,\"createSiteCode\":910,\"executeTime\":1554271173141,\"fingerprint\":\"DC6681359314AA6945671401440963XC\"," +
                "\"keyword1\":\"910\",\"keyword2\":\"JDVA00001832840\",\"operateTime\":1554274661000,\"ownSign\":\"DMS\",\"receiveSiteCode\":39,\"sequenceName\":\"SEQ_TASK_SORTING\"," +
                "\"tableName\":\"task_sorting\",\"type\":1200}";

        String body = "[{\n" +
                "  \"receiveSiteCode\" : 39,\n" +
                "  \"receiveSiteName\" : \"石景山营业部\",\n" +
                "  \"boxCode\" : \"JDVA00001549808\",\n" +
                "  \"packageCode\" : \"JDVA00001549808\",\n" +
                "  \"isCancel\" : 0,\n" +
                "  \"isLoss\" : 0,\n" +
                "  \"featureType\" : 0,\n" +
                "  \"bsendCode\" : \"\",\n" +
                "  \"id\" : 5498,\n" +
                "  \"businessType\" : 10,\n" +
                "  \"userCode\" : 10053,\n" +
                "  \"userName\" : \"邢松B网\",\n" +
                "  \"siteCode\" : 10036,\n" +
                "  \"siteName\" : \"北京马驹桥城配中心\",\n" +
                "  \"operateTime\" : \"2019-04-19 11:59:51.307\"\n" +
                "}]";
        Task task = new Task();
        task.setBody(body);
        //Task task = JsonHelper.fromJson(json,Task.class);
        SortingVO sortingVO = new SortingVO(task);
        sortingFactory.bulid(sortingVO).execute(sortingVO);
        while (true){
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Autowired
    private SortingSplitTask sortingSplitTask;
    @Test
    public void sortingSplit(){
        String body = "{\n" +
                "  \"boxCode\" : \"JDVA00003252788\",\n" +
                "  \"bsendCode\" : \"\",\n" +
                "  \"waybillCode\" : \"JDVA00003252788\",\n" +
                "  \"createSiteCode\" : 10036,\n" +
                "  \"createSiteName\" : \"北京马驹桥城配中心\",\n" +
                "  \"receiveSiteCode\" : 39,\n" +
                "  \"receiveSiteName\" : \"石景山营业部\",\n" +
                "  \"createUserCode\" : 10053,\n" +
                "  \"createUser\" : \"邢松B网\",\n" +
                "  \"createTime\" : 1554638391307,\n" +
                "  \"operateTime\" : 1554638391307,\n" +
                "  \"updateUserCode\" : 10053,\n" +
                "  \"updateUser\" : \"邢松B网\",\n" +
                "  \"status\" : 1,\n" +
                "  \"type\" : 10,\n" +
                "  \"isCancel\" : 0,\n" +
                "  \"isLoss\" : 0,\n" +
                "  \"featureType\" : 0,\n" +
                "  \"sortingType\" : 3,\n" +
                "  \"pageNo\" : 1,\n" +
                "  \"pageSize\" : 10,\n" +
                "  \"needInspection\" : true,\n" +
                "  \"lastTurn\" : true,\n" +
                "  \"packageListSize\" : 0,\n" +
                "  \"packSorting\" : false,\n" +
                "  \"waybillSorting\" : false,\n" +
                "  \"waybillSplitSorting\" : true,\n" +
                "  \"reverse\" : false,\n" +
                "  \"forward\" : true,\n" +
                "  \"cancel\" : false\n" +
                "}";
        Task tTask = new Task();
        tTask.setBody(body);
        try {
            SortingVO sortingVO = new SortingVO(tTask);
            sortingFactory.bulid(sortingVO).execute(sortingVO);

            //sortingSplitTask.executeSingleTask(tTask,"DMS");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
