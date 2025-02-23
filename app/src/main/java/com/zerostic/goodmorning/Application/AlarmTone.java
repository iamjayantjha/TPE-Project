package com.zerostic.goodmorning.Application;

public class AlarmTone {
    String toneName;
    int toneResId;
    boolean isSelected;

    public AlarmTone(String toneName, int toneResId, boolean isSelected) {
        this.toneName = toneName;
        this.toneResId = toneResId;
        this.isSelected = isSelected;
    }

    public String getToneName() {
        return toneName;
    }

    public void setToneName(String toneName) {
        this.toneName = toneName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getToneResId() {
        return toneResId;
    }

    public void setToneResId(int toneResId) {
        this.toneResId = toneResId;
    }
}
