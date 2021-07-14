package com.jd.bluedragon.distribution.web.jddl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.manager.SendMManager;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.sqlkit.domain.DataCompareDto;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.web.sqlkit.SqlkitController;
import com.jd.bluedragon.utils.MysqlHelper;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;
import com.jd.uim.annotation.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @ClassName JddlController
 * @Description
 * @Author wyh
 * @Date 2021/7/14 13:56
 **/
@Controller
@RequestMapping("/jddl")
public class JddlController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String QUERY_USERS = "queryUsers";
    private static final List<String> queryUsers;

    static {
        queryUsers = Arrays.asList(PropertiesHelper.newInstance()
                .getValue(QUERY_USERS).split(Constants.SEPARATOR_COMMA));
    }

    private static final String DB_TABLE_NAME = "send_m";

    @Autowired
    private SequenceGenAdaptor sequenceGenAdaptor;

    @Autowired
    private SendMDao sendMDao;


    @Authorization(Constants.DMS_WEB_DEVELOP_SQLKIT_R)
    @RequestMapping(value = "/testSplitDatabase", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<Integer> testSplitDatabase(@RequestBody List<SendM> sendMList) {
        InvokeResult<Integer> result = new InvokeResult<>();
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        if (!queryUsers.contains(erpUser.getUserCode().toLowerCase())) {
            log.info("用户erp账号：{}不在查询用户列表中", erpUser.getUserCode());
            return result;
        }

        for (SendM sendM : sendMList) {
            sendM.setBizSource(99);
            sendM.setCreateUserCode(999);
            sendM.setSendType(Constants.BUSSINESS_TYPE_POSITIVE);
            sendM.setCreateUser("bjxings");
            sendM.setCreateTime(new Date());
            sendM.setOperateTime(new Date());
            sendM.setYn(0);
            sendM.setSendMId(sequenceGenAdaptor.newId(DB_TABLE_NAME));
        }

        result.setData(sendMDao.addBatch(sendMList));

        return result;
    }
}
