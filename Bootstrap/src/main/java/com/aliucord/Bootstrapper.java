package com.aliucord;

import android.content.Context;
import android.util.Log;

import com.facebook.react.bridge.CatalystInstanceImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

public class Bootstrapper {
    private static final AtomicBoolean loadedModulesPatch = new AtomicBoolean(false);
    private static final AtomicBoolean loadedAliucord = new AtomicBoolean(false);
    private static File modulesFile;
    private static File cache;

    public static final String LOG_TAG = "AliucordRN Bootstrapper";
    private static final String modulesPatch = "const oldObjectCreate = this.Object.create;\n" +
        "const _window = this;\n" +
        "_window.Object.create = (...args) => {\n" +
        "  const obj = oldObjectCreate.apply(_window.Object, args);\n" +
        "  if (args[0] === null) {\n" +
        "    _window.modules = obj;\n" +
        "    _window.Object.create = oldObjectCreate;\n" +
        "  }\n" +
        "  return obj;\n" +
        "};";

    // Called from Smali Patch
    public static void loadModulesPatch(Object $this, boolean bVar) {
        if (loadedModulesPatch.getAndSet(true)) return;
        try {
            Log.i(LOG_TAG, "Loading modulesPatch.js");
            ((CatalystInstanceImpl) $this).loadScriptFromFile(modulesFile.getAbsolutePath(), modulesFile.getAbsolutePath(), bVar);
        } catch (Throwable th) {
            Log.e(LOG_TAG, "Failed to write modulesPatch.js", th);
        }
    }

    // Called from Smali Patch
    public static void loadAliucord(Object $this, boolean bVar) {
        if (loadedAliucord.getAndSet(true)) return;
        Log.i(LOG_TAG, "Downloading Aliucord.js");
        try {
            var aliucordFile = new File(cache, "Aliucord.js");
            var conn = new URL("http://localhost:3000/Aliucord.js").openConnection();
            try (var ucis = conn.getInputStream(); var fos = new FileOutputStream(aliucordFile)) {
                int n;
                var buf = new byte[4096];
                while ((n = ucis.read(buf)) != -1) {
                    fos.write(buf, 0, n);
                }
            }
            Log.i(LOG_TAG, "Loading Aliucord.js");
            ((CatalystInstanceImpl) $this).loadScriptFromFile(aliucordFile.getAbsolutePath(), aliucordFile.getAbsolutePath(), bVar);
        } catch (Throwable th) {
            Log.e(LOG_TAG, "Failed to download Aliucord.js", th);
        }
    }

    public static void init(Context context) throws Throwable {
        cache = context.getCacheDir();
        modulesFile = new File(cache, "modulesPatch.js");
        if (!modulesFile.exists()) {
            if (!cache.mkdirs()) {
                throw new IllegalStateException("Failed to create cache dir");
            }
            try (var fos = new FileOutputStream(modulesFile)) {
                fos.write(modulesPatch.getBytes(StandardCharsets.UTF_8));
            }
        }
    }
}
