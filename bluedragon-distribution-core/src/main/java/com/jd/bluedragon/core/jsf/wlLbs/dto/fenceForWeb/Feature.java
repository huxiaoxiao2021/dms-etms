
package com.jd.bluedragon.core.jsf.wlLbs.dto.fenceForWeb;

import lombok.Data;

import java.io.Serializable;

@Data
public class Feature implements Serializable {

    private static final long serialVersionUID = -4061936520974752074L;

    private Geometry geometry;
    
    private Properties properties;
    
    private String type;

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
