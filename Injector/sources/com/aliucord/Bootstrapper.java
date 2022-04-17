package com.aliucord;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import com.discord.bridge.DCDReactNativeHost;
import com.facebook.react.bridge.CatalystInstanceImpl;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.Charset;
import kotlin.Metadata;
import kotlin.io.FilesKt;
import kotlin.io.TextStreamsKt;
import org.jetbrains.annotations.NotNull;
/* compiled from: Bootstrapper.kt */
@Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006¨\u0006\u0007"}, d2 = {"Lcom/aliucord/Bootstrapper;", "", "()V", "init", "", "ctx", "Landroid/content/Context;", "Bootstrap_debug"}, k = 1, mv = {1, 5, 1}, xi = 48)
public final class Bootstrapper {
    @NotNull
    public static final Bootstrapper INSTANCE = new Bootstrapper();

    private Bootstrapper() {
    }

    /* JADX INFO: Multiple debug info for r2v3 java.lang.reflect.Method: [D('loadScriptFromFile' java.lang.reflect.Method), D('$this$init_u24lambda_u2d0' java.lang.reflect.Method)] */
    public final void init(@NotNull Context ctx) {
        File cache = ctx.getCacheDir();
        File modulesFile = new File(cache, "modulesPatch.js");
        if (!modulesFile.exists()) {
            File parentFile = modulesFile.getParentFile();
            if (parentFile != null) {
                parentFile.mkdirs();
            }
            FilesKt.writeText$default(modulesFile, "const oldObjectCreate = this.Object.create;\nconst _window = this;\n_window.Object.create = (...args) => {\n    const obj = oldObjectCreate.apply(_window.Object, args);\n    if (args[0] === null) {\n        _window.modules = obj;\n        _window.Object.create = oldObjectCreate;\n    }\n    return obj;\n};", (Charset) null, 2, (Object) null);
        }
        Method $this$init_u24lambda_u2d0 = CatalystInstanceImpl.class.getDeclaredMethod("jniLoadScriptFromFile", String.class, String.class, Boolean.TYPE);
        $this$init_u24lambda_u2d0.setAccessible(true);
        XposedBridge.hookMethod(CatalystInstanceImpl.class.getDeclaredMethod("loadScriptFromAssets", AssetManager.class, String.class, Boolean.TYPE), new XC_MethodHook($this$init_u24lambda_u2d0, modulesFile, cache) { // from class: com.aliucord.Bootstrapper$init$1
            final /* synthetic */ File $cache;
            final /* synthetic */ Method $loadScriptFromFile;
            final /* synthetic */ File $modulesFile;

            {
                this.$loadScriptFromFile = $loadScriptFromFile;
                this.$modulesFile = $modulesFile;
                this.$cache = $cache;
            }

            /* access modifiers changed from: protected */
            public void beforeHookedMethod(@NotNull XC_MethodHook.MethodHookParam param) {
                Log.i(BootstrapperKt.LOG_TAG, "Loading window.modules patch");
                this.$loadScriptFromFile.invoke(param.thisObject, this.$modulesFile.getAbsolutePath(), this.$modulesFile.getAbsolutePath(), param.args[2]);
            }

            /* access modifiers changed from: protected */
            public void afterHookedMethod(@NotNull XC_MethodHook.MethodHookParam param) {
                Log.i(BootstrapperKt.LOG_TAG, "Loading Aliucord.js");
                File aliucordFile = new File(this.$cache, "Aliucord.js");
                FilesKt.writeBytes(aliucordFile, TextStreamsKt.readBytes(new URL("http://localhost:3000/Aliucord.js")));
                this.$loadScriptFromFile.invoke(param.thisObject, aliucordFile.getAbsolutePath(), aliucordFile.getAbsolutePath(), param.args[2]);
            }
        });
        XposedBridge.hookMethod(DCDReactNativeHost.class.getDeclaredMethod("getUseDeveloperSupport", new Class[0]), XC_MethodReplacement.returnConstant(true));
    }
}
