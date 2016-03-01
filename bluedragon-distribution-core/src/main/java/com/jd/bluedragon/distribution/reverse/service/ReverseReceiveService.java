package com.jd.bluedragon.distribution.reverse.service;

import com.jd.bluedragon.distribution.reverse.domain.ReverseReceive;

public interface ReverseReceiveService {
    
    Integer add(ReverseReceive reverseReceive);
    
    Integer update(ReverseReceive reverseReceive);
    
    ReverseReceive findByPackageCode(String packageCode);
    
    void aftersaleReceive(ReverseReceive reverseReceive);
}
