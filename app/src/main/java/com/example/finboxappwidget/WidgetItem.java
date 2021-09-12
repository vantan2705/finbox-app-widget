package com.example.finboxappwidget;

import android.content.ComponentName;

public class WidgetItem {
    private String name;
    private int imageId;
    private ComponentName componentName;

    public WidgetItem(String name, int imageId, ComponentName componentName) {
        this.name = name;
        this.imageId = imageId;
        this.componentName = componentName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getImageId() {
        return this.imageId;
    }

    public void setComponentName(ComponentName componentName) {
        this.componentName = componentName;
    }

    public ComponentName getComponentName() {
        return this.componentName;
    }
}
