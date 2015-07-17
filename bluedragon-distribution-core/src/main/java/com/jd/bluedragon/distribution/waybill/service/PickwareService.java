package com.jd.bluedragon.distribution.waybill.service;

import com.jd.bluedragon.distribution.waybill.domain.Pickware;

public interface PickwareService {
    
    Pickware get(String code);
    
}
