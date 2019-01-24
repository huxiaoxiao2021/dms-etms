package com.jd.bluedragon.distribution.saf;

import com.jd.bluedragon.distribution.command.JdCommand;
import com.jd.bluedragon.distribution.command.JdCommandService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.print.domain.PrintPackageImage;
import com.jd.bluedragon.distribution.print.service.PackagePrintService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.Map;

/**
 * B网营业厅打印JSF接口
 * @author shipeilin
 * @Description: 类描述信息
 * @date 2019年01月24日 13时:43分
 */
public class PackagePrintServiceImpl implements PackagePrintService {

    @Autowired
    @Qualifier("jsonCommandService")
    private JdCommandService jdCommandService;

    @Override
    public JdResult<Map<String, Object>> getPrintInfo(JdCommand<String> printRequest) {
        JdResult<Map<String, Object>> result = new JdResult<Map<String, Object>>();
        result.toSuccess();
        //TODO 校验systemCode和secretKey是否匹配
        String commandResult = jdCommandService.execute(JsonHelper.toJson(printRequest));
        JdResult jdResult = JsonHelper.fromJson(commandResult, JdResult.class);
        String data = JSONObject.parseObject(commandResult).getString("data");
        Map map = JsonHelper.json2MapNormal(data);

        result.setCode(jdResult.getCode());
        result.setMessage(jdResult.getMessage());
        result.setMessageCode(jdResult.getMessageCode());
        result.setData(map);

        return result;
    }

    @Override
    public JdResult<List<PrintPackageImage>> generateImage(JdCommand<String> printRequest) {
        return null;
    }

    @Override
    public JdResult<String> generatePdf(JdCommand<String> printRequest) {
        return null;
    }
}
