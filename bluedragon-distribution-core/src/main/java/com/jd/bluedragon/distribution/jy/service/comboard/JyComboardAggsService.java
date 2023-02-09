package com.jd.bluedragon.distribution.jy.service.comboard;

import com.jd.bluedragon.distribution.abnormal.domain.ReportTypeEnum;
import com.jd.bluedragon.distribution.jy.comboard.JyComboardAggsEntity;
import com.jd.bluedragon.distribution.jy.enums.UnloadProductTypeEnum;

import java.util.List;

public interface JyComboardAggsService extends JyAggsService {

    public JyComboardAggsEntity queryComboardAggs(Integer operateSiteId,Integer receiveSiteId) throws Exception;

    public List<JyComboardAggsEntity> queryComboardAggs(Integer operateSiteId,List<Integer> receiveSiteId) throws Exception;

    public JyComboardAggsEntity queryComboardAggs(String boardCode) throws Exception;

    public List<JyComboardAggsEntity> queryComboardAggs(List<String> boardCode) throws Exception;

    public List<JyComboardAggsEntity> queryComboardAggs(Integer operateSiteId, Integer receiveSiteId, ReportTypeEnum ... reportTypeEnums) throws Exception;

    public List<JyComboardAggsEntity> queryComboardAggs(Integer operateSiteId, Integer receiveSiteId, UnloadProductTypeEnum... productTypeEnums) throws Exception;

    public List<JyComboardAggsEntity> queryComboardAggs(String boardCode,UnloadProductTypeEnum... productTypeEnums) throws Exception;


}
