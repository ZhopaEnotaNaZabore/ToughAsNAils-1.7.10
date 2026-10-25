package toughasnails;

import java.io.File;
import java.net.URL;
import net.minecraft.launchwrapper.LaunchClassLoader;

public final class RegressionLauncher {
    public static void main(String[] args) throws Exception {
        String[] entries = System.getProperty("java.class.path").split(File.pathSeparator);
        URL[] urls = new URL[entries.length];
        for (int i = 0; i < entries.length; i++) urls[i] = new File(entries[i]).toURI().toURL();
        LaunchClassLoader loader = new LaunchClassLoader(urls);
        Thread.currentThread().setContextClassLoader(loader);
        loader.loadClass("toughasnails.PortRegression").getMethod("main", String[].class)
                .invoke(null, (Object)args);
    }
}
