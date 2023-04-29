package com.mlf.phototest

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import kotlin.properties.Delegates

class App : Application() {

    companion object {
        var act: Activity by Delegates.notNull()
        var ctx: Context by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        ctx = this
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

            override fun onActivityStarted(activity: Activity) {
                act = activity
            }

            override fun onActivityResumed(activity: Activity) {}

            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityStopped(activity: Activity) {}

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {}
        })
    }

}