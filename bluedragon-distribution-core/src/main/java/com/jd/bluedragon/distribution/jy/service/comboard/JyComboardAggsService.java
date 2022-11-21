package com.jd.bluedragon.distribution.jy.service.comboard;

import com.jd.bluedragon.distribution.abnormal.domain.ReportTypeEnum;
import com.jd.bluedragon.distribution.jy.comboard.JyComboardAggsEntity;
import com.jd.bluedragon.distribution.jy.enums.UnloadProductTypeEnum;

import java.util.List;

public interface JyComboardAggsService extends JyAggsService {

    public JyComboardAggsEntity queryComboardAggs(String operateSiteId,String receiveSiteId) throws Exception;

    public JyComboardAggsEntity queryComboardAggs(String operateSiteId,String receiveSiteId,String boardCode) throws Exception;

    public List<JyComboardAggsEntity> queryComboardAggs(String operateSiteId, String receiveSiteId, ReportTypeEnum ... reportTypeEnums) throws Exception;

    public List<JyComboardAggsEntity> queryComboardAggs(String operateSiteId, String receiveSiteId, UnloadProductTypeEnum... productTypeEnums) throws Exception;


}
