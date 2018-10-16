package de.pho.descent.shared.dto;

/**
 *
 * @author pho
 */
public class WsMap {
    private long map;
    private long source;
    private long target;

    public WsMap() {
    }

    public WsMap(long map, long source, long target) {
        this.map = map;
        this.source = source;
        this.target = target;
    }

    public long getMap() {
        return map;
    }

    public void setMap(long map) {
        this.map = map;
    }



    public long getSource() {
        return source;
    }

    public void setSource(long source) {
        this.source = source;
    }

    public long getTarget() {
        return target;
    }

    public void setTarget(long target) {
        this.target = target;
    }
    
    
}
