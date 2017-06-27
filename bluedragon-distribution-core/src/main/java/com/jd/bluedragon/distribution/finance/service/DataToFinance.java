package com.jd.bluedragon.distribution.finance.service;

import com.jd.bluedragon.distribution.task.domain.Task;

/**
 * Created by xumei3 on 2017/6/12.
 */
public interface DataToFinance {
    boolean departure2Finance(Task departuerTask);
    boolean delivery2Finance(Task deliverytask);
}
