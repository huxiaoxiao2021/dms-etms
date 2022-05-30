package com.jd.bluedragon.distribution.jy.service.seal;

import java.util.List;

public interface JySendSealCodeService {
    List<String> selectSealCodeByBizId(String bizId);
}
