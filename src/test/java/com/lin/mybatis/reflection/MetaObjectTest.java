package com.lin.mybatis.reflection;

import com.lin.mybatis.entity.Author;
import com.lin.mybatis.entity.RichType;
import com.lin.mybatis.entity.Section;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @Author linjiayi5
 * @Date 2023/5/23 15:09:34
 */
public class MetaObjectTest {

    @Test
    void shouldGetAndSetField() {
        RichType rich = new RichType();
        MetaObject meta = SystemMetaObject.forObject(rich);
        meta.setValue("richField", "foo");
        assertEquals("foo", meta.getValue("richField"));
    }

    @Test
    void shouldGetAndSetNestedField() {
        RichType rich = new RichType();
        MetaObject meta = SystemMetaObject.forObject(rich);
        meta.setValue("richType.richField", "foo");
        assertEquals("foo", meta.getValue("richType.richField"));
    }

    @Test
    void shouldGetAndSetProperty() {
        RichType rich = new RichType();
        MetaObject meta = SystemMetaObject.forObject(rich);
        meta.setValue("richProperty", "foo");
        assertEquals("foo", meta.getValue("richProperty"));
    }

    @Test
    void shouldGetAndSetNestedProperty() {
        RichType rich = new RichType();
        MetaObject meta = SystemMetaObject.forObject(rich);
        meta.setValue("richType.richProperty", "foo");
        assertEquals("foo", meta.getValue("richType.richProperty"));
    }

    @Test
    void shouldGetAndSetMapPair() {
        RichType rich = new RichType();
        MetaObject meta = SystemMetaObject.forObject(rich);
        meta.setValue("richMap.key", "foo");
        assertEquals("foo", meta.getValue("richMap.key"));
    }

    @Test
    void shouldGetAndSetMapPairUsingArraySyntax() {
        RichType rich = new RichType();
        MetaObject meta = SystemMetaObject.forObject(rich);
        meta.setValue("richMap[key]", "foo");
        assertEquals("foo", meta.getValue("richMap[key]"));
    }

    @Test
    void shouldGetAndSetNestedMapPair() {
        RichType rich = new RichType();
        MetaObject meta = SystemMetaObject.forObject(rich);
        meta.setValue("richType.richMap.key", "foo");
        assertEquals("foo", meta.getValue("richType.richMap.key"));
    }

    @Test
    void shouldGetAndSetNestedMapPairUsingArraySyntax() {
        RichType rich = new RichType();
        MetaObject meta = SystemMetaObject.forObject(rich);
        meta.setValue("richType.richMap[key]", "foo");
        assertEquals("foo", meta.getValue("richType.richMap[key]"));
    }

    @Test
    void shouldGetAndSetListItem() {
        RichType rich = new RichType();
        MetaObject meta = SystemMetaObject.forObject(rich);
        meta.setValue("richList[0]", "foo");
        assertEquals("foo", meta.getValue("richList[0]"));
    }

    @Test
    void shouldGetAndSetNestedListItem() {
        RichType rich = new RichType();
        MetaObject meta = SystemMetaObject.forObject(rich);
        meta.setValue("richType.richList[0]", "foo");
        assertEquals("foo", meta.getValue("richType.richList[0]"));
    }

    @Test
    void shouldVerifyHasReadablePropertiesReturnedByGetReadablePropertyNames() {
        MetaObject object = SystemMetaObject.forObject(new Author());
        for (String readable : object.getGetterNames()) {
            assertTrue(object.hasGetter(readable));
        }
    }

    @Test
    void shouldVerifyHasWriteablePropertiesReturnedByGetWriteablePropertyNames() {
        MetaObject object = SystemMetaObject.forObject(new Author());
        for (String writeable : object.getSetterNames()) {
            assertTrue(object.hasSetter(writeable));
        }
    }

    @Test
    void shouldSetAndGetProperties() {
        MetaObject object = SystemMetaObject.forObject(new Author());
        object.setValue("email", "test");
        assertEquals("test", object.getValue("email"));
    }

    @Test
    void shouldVerifyPropertyTypes() {
        MetaObject object = SystemMetaObject.forObject(new Author());
        assertEquals(6, object.getSetterNames().length);
        assertEquals(int.class, object.getGetterType("id"));
        assertEquals(String.class, object.getGetterType("username"));
        assertEquals(String.class, object.getGetterType("password"));
        assertEquals(String.class, object.getGetterType("email"));
        assertEquals(String.class, object.getGetterType("bio"));
        assertEquals(Section.class, object.getGetterType("favouriteSection"));
    }

    @Test
    void shouldDemonstrateDeeplyNestedMapProperties() {
        HashMap<String, String> map = new HashMap<>();
        MetaObject metaMap = SystemMetaObject.forObject(map);

        assertTrue(metaMap.hasSetter("id"));
        assertTrue(metaMap.hasSetter("name.first"));
        assertTrue(metaMap.hasSetter("address.street"));

        assertFalse(metaMap.hasGetter("id"));
        assertFalse(metaMap.hasGetter("name.first"));
        assertFalse(metaMap.hasGetter("address.street"));

        metaMap.setValue("id", "100");
        metaMap.setValue("name.first", "Clinton");
        metaMap.setValue("name.last", "Begin");
        metaMap.setValue("address.street", "1 Some Street");
        metaMap.setValue("address.city", "This City");
        metaMap.setValue("address.province", "A Province");
        metaMap.setValue("address.postal_code", "1A3 4B6");

        assertTrue(metaMap.hasGetter("id"));
        assertTrue(metaMap.hasGetter("name.first"));
        assertTrue(metaMap.hasGetter("address.street"));

        assertEquals(3, metaMap.getGetterNames().length);
        assertEquals(3, metaMap.getSetterNames().length);

        @SuppressWarnings("unchecked")
        Map<String, String> name = (Map<String, String>) metaMap.getValue("name");
        @SuppressWarnings("unchecked")
        Map<String, String> address = (Map<String, String>) metaMap.getValue("address");

        assertEquals("Clinton", name.get("first"));
        assertEquals("1 Some Street", address.get("street"));
    }

    @Test
    void shouldDemonstrateNullValueInMap() {
        HashMap<String, String> map = new HashMap<>();
        MetaObject metaMap = SystemMetaObject.forObject(map);
        assertFalse(metaMap.hasGetter("phone.home"));

        metaMap.setValue("phone", null);
        assertTrue(metaMap.hasGetter("phone"));
        // hasGetter returns true if the parent exists and is null.
        assertTrue(metaMap.hasGetter("phone.home"));
        assertTrue(metaMap.hasGetter("phone.home.ext"));
        assertNull(metaMap.getValue("phone"));
        assertNull(metaMap.getValue("phone.home"));
        assertNull(metaMap.getValue("phone.home.ext"));

        metaMap.setValue("phone.office", "789");
        assertFalse(metaMap.hasGetter("phone.home"));
        assertFalse(metaMap.hasGetter("phone.home.ext"));
        assertEquals("789", metaMap.getValue("phone.office"));
        assertNotNull(metaMap.getValue("phone"));
        assertNull(metaMap.getValue("phone.home"));
    }

    @Test
    void shouldMethodHasGetterReturnTrueWhenListElementSet() {
        List<Object> param1 = new ArrayList<>();
        param1.add("firstParam");
        param1.add(222);
        param1.add(new Date());

        Map<String, Object> parametersEmulation = new HashMap<>();
        parametersEmulation.put("param1", param1);
        parametersEmulation.put("filterParams", param1);

        MetaObject meta = SystemMetaObject.forObject(parametersEmulation);

        assertEquals(param1.get(0), meta.getValue("filterParams[0]"));
        assertEquals(param1.get(1), meta.getValue("filterParams[1]"));
        assertEquals(param1.get(2), meta.getValue("filterParams[2]"));

        assertTrue(meta.hasGetter("filterParams[0]"));
        assertTrue(meta.hasGetter("filterParams[1]"));
        assertTrue(meta.hasGetter("filterParams[2]"));
    }

}
