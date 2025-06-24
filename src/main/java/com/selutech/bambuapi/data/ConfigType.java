package com.selutech.bambuapi.data;

public enum ConfigType {
    FILAMENT("filament"),
    MACHINE("machine"),
    PROCESS("process"),;

    private final String folderName;

    ConfigType(String type) {
        this.folderName = type;
    }

    public String getFolderName() {
        return folderName;
    }

    @Override
    public String toString() {
        return folderName;
    }
}
