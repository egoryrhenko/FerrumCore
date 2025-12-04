package org.ferrum.ferrumCore.pricol.portal;

import java.util.HashMap;
import java.util.Map;

public enum PortalState {
    OPEN((byte) 0),
    WORK((byte) 1),
    CLOSE((byte) 2);

    private final byte state;
    private static final Map<Byte, PortalState> lookup = new HashMap<>();

    PortalState(byte state) {
        this.state = state;
    }

    public byte getId() {
        return state;
    }

    public static PortalState forId(byte id) {
        return lookup.get(id);
    }

    static {
        for(PortalState pState : values()) {
            lookup.put(pState.getId(), pState);
        }
    }
}
