package mr.wruczek.supercensor3.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import mr.wruczek.supercensor3.SCMain;
import mr.wruczek.supercensor3.utils.classes.SCLogger;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0
 * International License. http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class IOUtils {

    public static String getContentFromURL(String url) {
        try {
            return getContentFromURL(new URL(url));
        } catch (Exception e) {
        }
        return null;
    }

    public static String getContentFromURL(URL url) {
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);

            Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            StringBuilder sb = new StringBuilder();
            for (int c; (c = in.read()) >= 0;)
                sb.append((char) c);

            return sb.toString().trim();
        } catch (IOException e) {

        }
        return null;
    }

    /**
     * List directory contents for a resource folder. Not recursive. This is
     * basically a brute-force implementation.
     *
     * @author Greg Briggs
     * @param clazz
     *            Any java class that lives in the same place as the resources
     *            you want.
     * @param path
     *            Should end with "/", but not start with one.
     * @return Just the name of each member item, not the full paths.
     * @throws URISyntaxException
     * @throws IOException
     */
    public static String[] getResourceListing(Class<?> clazz, String path) throws URISyntaxException, IOException {
        URL dirURL = clazz.getClassLoader().getResource(path);

        if (dirURL == null) {
            String me = clazz.getName().replace(".", "/") + ".class";
            dirURL = clazz.getClassLoader().getResource(me);
        }

        if (dirURL.getProtocol().equals("jar")) {
            String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!"));
            JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
            Enumeration<JarEntry> entries = jar.entries();
            Set<String> result = new HashSet<String>();
            while (entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (name.startsWith(path)) {
                    String entry = name.substring(path.length());
                    int checkSubdir = entry.indexOf("/");
                    if (checkSubdir >= 0) {
                        entry = entry.substring(0, checkSubdir);
                    }
                    result.add(entry);
                }
            }
            jar.close();
            return result.toArray(new String[result.size()]);
        }

        throw new UnsupportedOperationException("Cannot list files for URL " + dirURL);
    }

    public static void copyResource(String resourcePath, File saveTo, boolean force) throws IOException {
        InputStream in = SCMain.getInstance().getResource(resourcePath);

        if (in == null) {
            throw new IllegalArgumentException("Resource \"" + resourcePath + "\" not found!");
        }

        if (saveTo.exists() && !force)
            return;

        File parent = saveTo.getParentFile();
        if (!(parent.exists())) {
            parent.mkdirs();
        }

        OutputStream out = new FileOutputStream(saveTo);
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
        in.close();

        SCLogger.logInfo("Created new file from resource: " + saveTo.getName());
    }

}
