package com.jd.bluedragon.distribution.reverse.part.dao;

import com.jd.bluedragon.distribution.reverse.part.domain.ReversePartDetail;
import com.jd.bluedragon.distribution.reverse.part.domain.ReversePartDetailCondition;
import com.jd.ql.dms.common.web.mvc.api.Dao;

import java.util.List;

/**
 *
 * @ClassName: ReversePartDetailDao
 * @Description: 半退明细表--Dao接口
 * @author wuyoude
 * @date 2019年02月12日 11:40:45
 *
 */
public interface ReversePartDetailDao extends Dao<ReversePartDetail> {


    List<ReversePartDetail> queryByParam (ReversePartDetailCondition reversePartDetailCondition);

    int queryByParamCount(ReversePartDetailCondition reversePartDetailCondition);

    int updateReceiveTime(ReversePartDetail reversePartDetail);

    int updateForCancelSend(ReversePartDetail reversePartDetail);
}
