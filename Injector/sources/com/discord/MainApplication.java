package com.discord;

import android.app.Application;
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
public final class MainApplication extends Application implements ReactApplication {
    private final DCDReactNativeHost host = new DCDReactNativeHost(this);

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        BundleUpdater.Companion companion = BundleUpdater.Companion;
        Bootstrapper.INSTANCE.init(this);
        String[] split = BuildConfig.VERSION_NAME.split(" ");
        String str = split.length > 0 ? split[0] : "";
        ClientInfo.INSTANCE.init(this, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE, BuildConfig.FLAVOR, BuildConfig.BUILD_TYPE);
        companion.init(this, str);
        CacheDataSourceFactory.Companion.init(this);
        TTIManagerModule.Companion.logAppOpenedTimestamp();
        SoLoaderUtils.INSTANCE.init(this, false);
        FlipperUtils.INSTANCE.init(this);
        RLottieUtils.INSTANCE.init();
        FontScaleUtilsKt.setFontScaleDeprecated(this);
        ReactScrollViewFlingLimitKt.setFlingVelocityLimit(10000);
    }

    public DCDReactNativeHost getReactNativeHost() {
        return this.host;
    }
}
