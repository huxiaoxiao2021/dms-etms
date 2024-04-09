package com.jd.bluedragon.distribution.jy.service.work;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerData;
import com.jd.bluedragon.common.dto.work.ResponsibleInfo;
import com.jd.bluedragon.distribution.jy.dto.work.JyWorkGridManagerResponsibleInfo;

import java.util.List;

public interface JyWorkGridManagerResponsibleInfoService {
    int add(JyWorkGridManagerResponsibleInfo responsibleInfo);

    JyWorkGridManagerResponsibleInfo queryByBizId(String bizId);

    ResponsibleInfo queryResponsibleInfoByBizId(String bizId);

    void saveTaskResponsibleInfo(JyWorkGridManagerData oldData, ResponsibleInfo responsibleInfo);

    List<JyWorkGridManagerData> workGridManagerExpiredSaveResponsibleInfo(List<String> bizIds);

    void sendViolentSortingResponsibleInfo(JyWorkGridManagerData oldData, ResponsibleInfo responsibleInfo);

    JdCResponse<Boolean> checkResponsibleInfo(JyWorkGridManagerData taskData, JdCResponse<Boolean> result);

    void workGridManagerExpiredSendMq(List<JyWorkGridManagerData> jyWorkGridManagerData);
    
}
