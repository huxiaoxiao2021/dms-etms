package com.jd.bluedragon.distribution.reverse.service;

import com.jd.bluedragon.distribution.reverse.domain.ReverseReject;
import com.jd.bluedragon.distribution.task.domain.Task;

public interface ReverseRejectService {
    
    Integer add(ReverseReject reverseReject);
    
    Integer update(ReverseReject reverseReject);
    
    ReverseReject get(Integer businessType, String orderId, String packageCode);
    
    void rejectInspect(Task task);
    
    void reject(ReverseReject reverseReject);
}
