package com.lin.mybatis.io;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.Reader;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @Author linjiayi5
 * @Date 2023/4/26 16:13:09
 */
public class ResourcesTest {

    private static final ClassLoader CLASS_LOADER = ResourcesTest.class.getClassLoader();

    @Test
    void shouldGetResourceAsStream() throws Exception {
        try (InputStream in = Resources.getResourceAsStream(CLASS_LOADER, "test.properties")) {
            assertNotNull(in);
        }
    }

    @Test
    void shouldGetResourceAsReader() throws Exception {
        try (Reader in = Resources.getResourceAsReader(CLASS_LOADER, "test.properties")) {
            assertNotNull(in);
        }
    }

}
