package com.jd.bluedragon.distribution.spare.service;

import java.util.List;

import com.jd.bluedragon.distribution.spare.domain.Spare;

public interface SpareService {
    
    Integer update(Spare spare);
    
    List<Spare> print(Spare spare);
    
    Integer reprint(Spare spare);
    
    List<Spare> findSpares(Spare spare);
    
    Spare findBySpareCode(String spareCode);
    
}
