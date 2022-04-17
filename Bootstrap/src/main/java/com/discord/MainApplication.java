package com.discord;

import android.app.Application;
import android.util.Log;

import com.aliucord.Bootstrapper;
import com.discord.bridge.DCDReactNativeHost;
import com.discord.bundle_updater.BundleUpdater;
import com.discord.client_info.ClientInfo;
import com.discord.flipper.FlipperUtils;
import com.discord.image.rlottie_utilities.RLottieUtils;
import com.discord.media_player.CacheDataSourceFactory;
import com.discord.scale.FontScaleUtilsKt;
import com.discord.tti_manager.TTIManagerModule;
import com.discord.utils.SoLoaderUtils;
import com.facebook.react.ReactApplication;
import com.facebook.react.views.scroll.ReactScrollViewFlingLimitKt;

// Copy pasted from jadx decompile with slight modifications, see comments
public final class MainApplication extends Application implements ReactApplication {
    private final DCDReactNativeHost host = new DCDReactNativeHost(this);

    @Override
    public void onCreate() {
        super.onCreate();
        BundleUpdater.Companion companion = BundleUpdater.Companion;

        // BEGIN ALIUCORD CHANGED
        try {
            Bootstrapper.init(this);
        } catch (Throwable th) {
            Log.e(Bootstrapper.LOG_TAG, "Failed to bootstrap AliucordRN", th);
        }
        var split = BuildConfig.VERSION_NAME.split(" ");
        var str = split.length > 0 ? split[0] : "";
        ClientInfo.INSTANCE.init(
            this,
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE,
            BuildConfig.FLAVOR,
            BuildConfig.BUILD_TYPE
        );
        // END ALIUCORD CHANGED

        companion.init(this, str);
        CacheDataSourceFactory.Companion.init(this);
        // CrashReporting.INSTANCE.init(this);
        TTIManagerModule.Companion.logAppOpenedTimestamp();
        // Dropped $default
        SoLoaderUtils.INSTANCE.init(this, false);
        FlipperUtils.INSTANCE.init(this);
        RLottieUtils.INSTANCE.init();
        FontScaleUtilsKt.setFontScaleDeprecated(this);
        // Dropped $default
        ReactScrollViewFlingLimitKt.setFlingVelocityLimit(10000);
    }

    @Override
    public DCDReactNativeHost getReactNativeHost() {
        return this.host;
    }
}
