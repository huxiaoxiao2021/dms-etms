package com.jd.bluedragon.distribution.reverse.service;

import com.jd.bluedragon.distribution.reverse.domain.ReverseReceive;
import com.jd.bluedragon.distribution.task.domain.Task;

public interface ReverseReceiveService {
    
    Integer add(ReverseReceive reverseReceive);
    
    Integer update(ReverseReceive reverseReceive);
    
    ReverseReceive findByPackageCode(String packageCode);
    
    void aftersaleReceiveInspect(Task task);
    
    void aftersaleReceive(ReverseReceive reverseReceive);
}
