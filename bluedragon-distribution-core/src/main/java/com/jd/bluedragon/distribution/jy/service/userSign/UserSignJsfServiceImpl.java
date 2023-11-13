package com.jd.bluedragon.distribution.jy.service.userSign;

import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.api.userSign.UserSignJsfService;
import com.jd.bluedragon.distribution.jy.dto.userSign.UserJobType;
import com.jd.bluedragon.distribution.station.enums.JobTypeEnum;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("userSignJsfServiceImpl")
public class UserSignJsfServiceImpl implements UserSignJsfService {
    @Override
    public InvokeResult<List<UserJobType>> getJobTypes() {
        InvokeResult<List<UserJobType>> result = new InvokeResult<>();
        List<UserJobType> jobTypeList = new ArrayList<>();
        for (JobTypeEnum jobTypeEnum : JobTypeEnum.values()) {
            UserJobType jobType = new UserJobType();
            jobType.setCode(jobTypeEnum.getCode());
            jobType.setName(jobTypeEnum.getName());
            jobTypeList.add(jobType);
        }
        result.setData(jobTypeList);
        return result;
    }
}
