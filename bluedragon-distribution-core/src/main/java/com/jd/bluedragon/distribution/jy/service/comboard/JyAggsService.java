package com.jd.bluedragon.distribution.jy.service.comboard;

import com.jd.bluedragon.distribution.jy.dto.comboard.JyAggsDto;
import com.jd.jmq.common.message.Message;

public interface JyAggsService {

    boolean saveAggs(Message message);

}
