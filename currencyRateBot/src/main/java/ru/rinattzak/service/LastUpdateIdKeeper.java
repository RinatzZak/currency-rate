package ru.rinattzak.service;

public interface LastUpdateIdKeeper {
    long get();
    void set(long lastUpdateId);
}
