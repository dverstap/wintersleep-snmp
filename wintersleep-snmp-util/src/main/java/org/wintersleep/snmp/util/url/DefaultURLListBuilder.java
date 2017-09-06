package org.wintersleep.snmp.util.url;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DefaultURLListBuilder implements URLListFactory {

    private final List<URL> urls = new ArrayList<>();

    @Override
    public List<URL> create() throws Exception {
        return new ArrayList<>(urls);
    }

    public DefaultURLListBuilder addUrl(URL url) {
        urls.add(url);
        return this;
    }

    public DefaultURLListBuilder addUrl(String url) {
        try {
            return addUrl(new URL(url));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public DefaultURLListBuilder addUrls(URL... urls) {
        for (URL url : urls) {
            addUrl(url);
        }
        return this;
    }

    public DefaultURLListBuilder addUrls(String... urls) {
        try {
            for (String url : urls) {
                addUrl(new URL(url));
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public DefaultURLListBuilder addUri(URI uri) {
        try {
            return addUrl(uri.toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public DefaultURLListBuilder addUri(String uri) {
        try {
            return addUri(new URI(uri));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public DefaultURLListBuilder addUris(URI... uris) {
        for (URI uri : uris) {
            addUri(uri);
        }
        return this;
    }

    public DefaultURLListBuilder addUris(String... uris) {
        for (String uri : uris) {
            addUri(uri);
        }
        return this;
    }

    public DefaultURLListBuilder addFile(File file) {
        return addUri(file.toURI());
    }


    public DefaultURLListBuilder addFile(String file) {
        return addFile(new File(file));
    }

    public DefaultURLListBuilder addFiles(File... files) {
        for (File file : files) {
            addFile(file);
        }
        return this;
    }

    public DefaultURLListBuilder addDir(File dir, String... children) {
//        if (!dir.exists()) {
//            throw new IllegalArgumentException("Directory does not exist: " + dir);
//        }
        for (String child : children) {
            addFile(new File(dir, child));
        }
        return this;
    }

    public DefaultURLListBuilder addDir(String dir, String... children) {
        for (String child : children) {
            addFile(new File(dir, child));
        }
        return this;
    }

    public DefaultURLListBuilder addResource(String resource) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        return addResource(contextClassLoader, resource);
    }

    public DefaultURLListBuilder addResource(ClassLoader classLoader, String resource) {
        URL url = classLoader.getResource(resource);
        if (url == null) {
            throw new IllegalArgumentException("Cannot find resource: " + resource);
        }
        return addUrl(url);
    }

    public DefaultURLListBuilder addResourceDir(String parent, String... children) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        return addResourceDir(contextClassLoader, parent, children);
    }

    public DefaultURLListBuilder addResourceDir(ClassLoader classLoader, String parent, String... children) {
        for (String child : children) {
            addResource(classLoader, parent + "/" + child);
        }
        return this;
    }

}
