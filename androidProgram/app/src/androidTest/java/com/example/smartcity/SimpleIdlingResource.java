package com.example.smartcity;

import androidx.test.espresso.IdlingResource;

public class SimpleIdlingResource implements IdlingResource {
    private volatile boolean isIdleNow = true;
    private ResourceCallback resourceCallback;

    @Override
    public String getName() {
        return SimpleIdlingResource.class.getName();
    }

    @Override
    public boolean isIdleNow() {
        return isIdleNow;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.resourceCallback = callback;
    }

    public void setIdleState(boolean isIdleNow) {
        this.isIdleNow = isIdleNow;
        if (isIdleNow && resourceCallback != null) {
            resourceCallback.onTransitionToIdle();
        }
    }
}