package com.jd.bluedragon.distribution.material.dao;

import com.jd.bluedragon.distribution.material.domain.DmsMaterialSend;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MaterialSendDao extends Dao<DmsMaterialSend> {

    boolean insert(DmsMaterialSend record);

    int batchInsertOnDuplicate(@Param("list") List<DmsMaterialSend> list);

    int deleteBySendCode(@Param("sendCode") String sendCode, @Param("createSiteCode") Long createSiteCode);
}