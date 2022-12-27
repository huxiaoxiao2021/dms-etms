
package com.jd.bluedragon.core.jsf.wlLbs.dto.fenceForWeb;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Geometry implements Serializable {

    private static final long serialVersionUID = -2680001790896107201L;

    private List<List<List<Double>>> coordinates;
    
    private String type;

    public List<List<List<Double>>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<List<List<Double>>> coordinates) {
        this.coordinates = coordinates;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
