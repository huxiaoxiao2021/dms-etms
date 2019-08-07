package com.jd.bluedragon.core.base;

import com.jd.spm.bwcompany.request.BwCompanyCondition;
import com.jd.spm.bwcompany.response.BwCompanyInfo;
import com.jd.spm.bwcompany.service.BWCompanyService;
import com.jd.spm.common.CommonDto;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lixin39
 * @Description B网商家查询接口
 * @ClassName SpmBWCompanyManagerImpl
 * @date 2019/8/7
 */
@Service
public class SpmBWCompanyManagerImpl implements SpmBWCompanyManager {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private BWCompanyService bwCompanyService;

    @Override
    public List<BwCompanyInfo> getCompanyListByPin(String pin) {
        if (StringUtils.isBlank(pin)) {
            return null;
        }
        BwCompanyCondition condition = new BwCompanyCondition();
        condition.setJdPin(pin);
        CommonDto<List<BwCompanyInfo>> commonDto = bwCompanyService.getCompanyListByCondition(condition);
        if (commonDto.getCode() == CommonDto.CODE_SUCCESS) {
            return commonDto.getData();
        }

        if (commonDto.getCode() != CommonDto.CODE_SUCCESS) {
            logger.error(String.format("B网商家查询接口查询失败，状态码：%s，信息：%s", commonDto.getCode(), commonDto.getMessage()));
        }
        return null;
    }

}
