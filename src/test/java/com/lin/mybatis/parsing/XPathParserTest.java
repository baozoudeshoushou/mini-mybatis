package com.lin.mybatis.parsing;

import com.lin.mybatis.io.Resources;
import com.lin.mybatis.util.Assert;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

/**
 * @Author linjiayi5
 * @Date 2023/4/27 16:07:58
 */
public class XPathParserTest {

    @Test
    void constructorWithReader() throws IOException {
        try (Reader reader = Resources.getResourceAsReader("mybatis-config.xml")) {
            XPathParser parser = new XPathParser(reader);
            XNode xNode = parser.evalNode("/configuration");
            Assert.notNull(xNode, "");
        }
    }

}
