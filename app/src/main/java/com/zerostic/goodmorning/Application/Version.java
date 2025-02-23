package com.zerostic.goodmorning.Application;

public class Version {
    String current_version;
    String type;

    public Version() {
    }

    public Version(String current_version, String type) {
        this.current_version = current_version;
        this.type = type;
    }

    public String getCurrent_version() {
        return current_version;
    }

    public void setCurrent_version(String current_version) {
        this.current_version = current_version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
