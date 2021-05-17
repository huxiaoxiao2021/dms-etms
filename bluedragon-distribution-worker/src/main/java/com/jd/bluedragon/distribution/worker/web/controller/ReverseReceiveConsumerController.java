package com.jd.bluedragon.distribution.worker.web.controller;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.consumer.reverse.ReverseReceiveConsumer;
import com.jd.jmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-05-17 18:39:52 周一
 */
@Controller("/reverseReceiveConsumer")
public class ReverseReceiveConsumerController extends DmsBaseWorkerController {

    @Autowired
    @Qualifier("reverseReceiveConsumer")
    private ReverseReceiveConsumer reverseReceiveConsumer;

    @RequestMapping(value = "/consume", method = RequestMethod.GET)
    @ResponseBody
    public JdResult<Boolean> checkResult(@RequestBody String content) throws Exception {
        JdResult<Boolean> result = new JdResult<>();
        result.toSuccess();
        result.setData(true);
        Message msg = new Message();
        msg.setText(content);
        reverseReceiveConsumer.consume(msg);
        return result;
    }
}
