package ru.rinattzak.service.impl;

import org.springframework.stereotype.Component;
import ru.rinattzak.service.LastUpdateIdKeeper;

@Component
public class LastUpdateIdKeeperImpl implements LastUpdateIdKeeper {
    private long lastUpdateId = 0;

    @Override
    public long get() {
        return lastUpdateId;
    }

    @Override
    public void set(long lastUpdateId) {
        this.lastUpdateId = lastUpdateId;
    }
}
