package com.afollestad.impression.api;

public class VideoEntry extends LocalMediaEntry {

    public VideoEntry() {
    }

    @Override
    public boolean isVideo() {
        return true;
    }
}
