package com.microink.clcamerax.camera;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

/**
 * @author Cass
 * @version v1.0
 * @Date 2020/12/21 3:26 PM
 *
 * CameraX生命周期绑定类
 */
public class CustomLifecycle implements LifecycleOwner {

    private LifecycleRegistry lifecycleRegistry;

    public CustomLifecycle() {
        lifecycleRegistry = new LifecycleRegistry(this);
        lifecycleRegistry.setCurrentState(Lifecycle.State.CREATED);
    }

    public void doOnStart() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
    }

    public void doOnResume() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
    }

    public void doOnPause() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
    }

    public void doOnStop() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
    }

    public void doOnDestory() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
    }

    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }
}
