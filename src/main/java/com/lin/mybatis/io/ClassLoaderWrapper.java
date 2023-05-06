package com.lin.mybatis.io;

import java.io.InputStream;

/**
 * @Author linjiayi5
 * @Date 2023/4/26 16:01:03
 */
public class ClassLoaderWrapper {

    ClassLoader defaultClassLoader;

    ClassLoader systemClassLoader;

    ClassLoaderWrapper() {
        systemClassLoader = ClassLoader.getSystemClassLoader();
    }

    /**
     * Get a resource from the classpath
     *
     * @param resource the resource to find
     * @return the stream or null
     */
    public InputStream getResourceAsStream(String resource, ClassLoader classLoader) {
        return getResourceAsStream(resource, getClassLoaders(classLoader));
    }

    /**
     * Try to get a resource from a group of classloaders
     *
     * @param resource the resource to get
     * @param classLoader the classloaders to examine
     * @return the resource or null
     */
    InputStream getResourceAsStream(String resource, ClassLoader[] classLoader) {
        for (ClassLoader loader : classLoader) {
            if (loader != null) {
                InputStream inputStream = loader.getResourceAsStream(resource);

                // now, some class loaders want this leading "/", so we'll add it and try again if we didn't find the resource
                if (inputStream == null) {
                    inputStream = loader.getResourceAsStream("/" + resource);
                }

                if (inputStream != null) {
                    return inputStream;
                }
            }
        }

        return null;
    }

    /**
     * Find a class on the classpath (or die trying)
     *
     * @param name the class to look for
     * @return the class
     * @throws ClassNotFoundException if class not found
     */
    public Class<?> classForName(String name) throws ClassNotFoundException {
        ClassLoader[] classLoaders = getClassLoaders(null);
        for (ClassLoader classLoader : classLoaders) {
            if (classLoader != null) {
                try {
                    return Class.forName(name, true, classLoader);
                }
                catch (ClassNotFoundException e) {
                    // we'll ignore this until all classloaders fail to locate the class
                }
            }
        }

        throw new ClassNotFoundException("Cannot find class: " + name);
    }

    ClassLoader[] getClassLoaders(ClassLoader classLoader) {
        return new ClassLoader[] { classLoader, defaultClassLoader, Thread.currentThread().getContextClassLoader(),
                getClass().getClassLoader(), systemClassLoader };
    }

}
