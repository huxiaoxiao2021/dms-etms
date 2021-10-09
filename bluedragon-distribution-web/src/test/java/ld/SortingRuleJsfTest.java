package ld;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.rule.dto.SortingRuleDto;
import com.jd.bluedragon.distribution.rule.dto.SortingRuleTypeEnum;
import com.jd.bluedragon.distribution.rule.service.SortingRuleJsfService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2021/2/22
 * @Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
public class SortingRuleJsfTest {

    @Autowired
    private SortingRuleJsfService sortingRuleJsfService;

    @Test
    public void test(){
        List<String> ruleTypes = new ArrayList<>();
        ruleTypes.add(SortingRuleTypeEnum.RULE_1120.getCode());
        ruleTypes.add(SortingRuleTypeEnum.RULE_1121.getCode());
        ruleTypes.add(SortingRuleTypeEnum.RULE_1122.getCode());
        ruleTypes.add(SortingRuleTypeEnum.RULE_1125.getCode());


        Response<List<SortingRuleDto>> response = sortingRuleJsfService.findRules(910,ruleTypes);

        Response<Boolean> response1 = sortingRuleJsfService.changeRules(910,"liuduo8",response.getData());

        Assert.assertTrue(response1.getData());

        Response<List<SortingRuleDto>> response2 = sortingRuleJsfService.findRules(910,ruleTypes);

    }
}
