/*
 * Copyright 2013 Sander Verdonschot <sander.verdonschot at gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package publy.data.settings;

import java.lang.String;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;
import publy.data.PublicationType;
import publy.data.category.OutputCategory;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class BeanTestUtils {

    public static String makeSetter(Field field) {
        String name = field.getName();
        String capitalizedName = Character.toUpperCase(name.charAt(0)) + name.substring(1);
        return "set" + capitalizedName;
    }

    public static String makeGetter(Field field) {
        String setter = makeSetter(field);

        if (field.getType().equals(boolean.class)) {
            return "is" + setter.substring(3);
        } else {
            return "get" + setter.substring(3);
        }
    }

    public static void testBean(Object bean) {
        for (Field field : bean.getClass().getDeclaredFields()) {
            try {
                testField(field, bean.getClass().newInstance());
            } catch (InstantiationException ex) {
                fail("Class could not be instantiated.");
            } catch (IllegalAccessException ex) {
                fail(ex.toString());
            }
        }
    }

    public static void testField(Field field, Object declaringClass) {
        try {
            if (field.getType().equals(boolean.class)) {
                testField(field, declaringClass, true, false);
            } else if (field.getType().equals(Path.class)) {
                testField(field, declaringClass, Paths.get("/tmp/foo"), Paths.get("/foo/tmp"));
            } else if (field.getType().equals(HTMLSettings.NavigationPlacement.class)) {
                testField(field, declaringClass, HTMLSettings.NavigationPlacement.NO_NAVIGATION, HTMLSettings.NavigationPlacement.BEFORE_SECTION_AND_BOTTOM);
            } else if (field.getType().equals(HTMLSettings.TitleLinkTarget.class)) {
                testField(field, declaringClass, HTMLSettings.TitleLinkTarget.ABSTRACT, HTMLSettings.TitleLinkTarget.PAPER);
            } else if (field.getType().equals(PublicationType.class)) {
                testField(field, declaringClass, PublicationType.ACCEPTED, PublicationType.ARXIV);
            } else if (field.getType().equals(String.class)) {
                testField(field, declaringClass, "a", "b");
            } else if (field.getType().equals(List.class)) {
                Type[] parameters = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();

                if (parameters.length == 0) {
                    fail("Field \"" + field + "\" uses raw type \"" + field.getGenericType() + "\".");
                }

                Type parameter = parameters[0];

                if (parameter.equals(String.class)) {
                    testField(field, declaringClass, new ArrayList<String>(), Arrays.asList("a"));
                } else if (parameter.equals(OutputCategory.class)) {
                    testField(field, declaringClass, new ArrayList<OutputCategory>(), Arrays.asList(new OutputCategory("b", "B", null)));
                } else {
                    fail("Unknown parameter type: " + field.getGenericType());
                }
            } else if (field.getType().equals(GeneralSettings.NameDisplay.class)) {
                testField(field, declaringClass, GeneralSettings.NameDisplay.ABBREVIATED, GeneralSettings.NameDisplay.FULL);
            } else if (field.getType().equals(GeneralSettings.Numbering.class)) {
                testField(field, declaringClass, GeneralSettings.Numbering.GLOBAL, GeneralSettings.Numbering.LOCAL);
            } else if (field.getType().equals(PublicationType.class)) {
                testField(field, declaringClass, PublicationType.ACCEPTED, PublicationType.ARXIV);
            } else {
                fail("Unknown field type: " + field.getGenericType());
            }
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            fail(ex.toString());
        } catch (NoSuchMethodException ex) {
            if (ex.toString().contains(makeGetter(field))) {
                fail("Getter \"" + makeGetter(field) + "()\" for field " + field + " does not exist.");
            } else if (ex.toString().contains(makeSetter(field))) {
                fail("Setter \"" + makeSetter(field) + "(" + field.getGenericType() + ")\" for field " + field + " does not exist.");
            } else {
                ex.printStackTrace();
                fail(ex.toString());
            }
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
            fail(ex.toString());
        }
    }

    private static void testField(Field field, Object declaringClass, Object val1, Object val2) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        // Set the field to val1
        field.setAccessible(true);
        field.set(declaringClass, val1);

        // Check that get returns val1
        Method get = declaringClass.getClass().getMethod(makeGetter(field));
        assertTrue("Getter does not have the correct return type.", get.getReturnType().equals(field.getType()));

        Object result = get.invoke(declaringClass);
        assertTrue(get + " returned null.", result != null);
        assertEquals("Getter returned an incorrect value.", val1, result);

        // Set the field to val2 and check that get returns val2
        field.set(declaringClass, val2);
        result = get.invoke(declaringClass);
        assertTrue(get + " returned null.", result != null);
        assertEquals("Getter returned an incorrect value.", val2, result);

        // Test set (field is val2)
        Method set = declaringClass.getClass().getMethod(makeSetter(field), field.getType());
        assertTrue("Setter does not have the correct return type.", set.getReturnType().equals(void.class));

        set.invoke(declaringClass, val1);
        assertEquals("Field \"" + field.getName() + "\" does not have the right value after invoking setter \"" + set.getName() + "\".", val1, field.get(declaringClass));

        set.invoke(declaringClass, val2);
        assertEquals("Field \"" + field.getName() + "\" does not have the right value after invoking setter \"" + set.getName() + "\".", val2, field.get(declaringClass));
    }
}
