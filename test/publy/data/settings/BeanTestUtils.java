/*
 * Copyright 2013-2015 Sander Verdonschot <sander.verdonschot at gmail.com>.
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

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import static org.junit.Assert.*;
import static org.hamcrest.core.Is.is;
import org.xml.sax.SAXException;
import publy.data.Pair;
import publy.data.PublicationStatus;
import publy.data.category.OutputCategory;
import publy.data.category.conditions.TypeCondition;
import publy.io.ResourceLocator;
import publy.io.settings.SettingsReaderCurrent;
import publy.io.settings.SettingsWriter;

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
            Pair<Object, Object> exampleValues = getExampleValues(field);
            testField(field, declaringClass, exampleValues.getFirst(), exampleValues.getSecond());
        } catch (IllegalAccessException | InvocationTargetException ex) {
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
        }
    }

    private static Pair<Object, Object> getExampleValues(Field field) {
        if (field.getType().equals(boolean.class)) {
            return new Pair<Object, Object>(true, false);
        } else if (field.getType().equals(Path.class)) {
            return new Pair<Object, Object>(ResourceLocator.getFullPath("tmp/foo"), ResourceLocator.getFullPath("foo/tmp"));
        } else if (field.getType().equals(HTMLSettings.NavigationPlacement.class)) {
            return new Pair<Object, Object>(HTMLSettings.NavigationPlacement.NO_NAVIGATION, HTMLSettings.NavigationPlacement.BEFORE_SECTION_AND_BOTTOM);
        } else if (field.getType().equals(HTMLSettings.TitleLinkTarget.class)) {
            return new Pair<Object, Object>(HTMLSettings.TitleLinkTarget.ABSTRACT, HTMLSettings.TitleLinkTarget.PAPER);
        } else if (field.getType().equals(PublicationStatus.class)) {
            return new Pair<Object, Object>(PublicationStatus.ACCEPTED, PublicationStatus.ARXIV);
        } else if (field.getType().equals(String.class)) {
            return new Pair<Object, Object>("a", "b");
        } else if (field.getType().equals(List.class)) {
            Type[] parameters = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();

            if (parameters.length == 0) {
                fail("Field \"" + field + "\" uses raw type \"" + field.getGenericType() + "\".");
            }

            Type parameter = parameters[0];

            if (parameter.equals(String.class)) {
                return new Pair<Object, Object>(Arrays.asList("b"), Arrays.asList("a")); // Needs to be Arrays.asList(...) for the types to match, as SettingsReader returns an Arrays.asList(...).
            } else if (parameter.equals(OutputCategory.class)) {
                // The second list should be a superset of the first, that way allCategories will be set to the second, when activeCategories is test with the first
                // CategorySettings expects its lists to be modifiable, so we need to wrap them in new ArrayLists
                return new Pair<Object, Object>(Arrays.asList(new OutputCategory("a", "A", new TypeCondition(true, "ta"))), Arrays.asList(new OutputCategory("a", "A", new TypeCondition(true, "ta")), new OutputCategory("b", "B", new TypeCondition(false, "tb"))));
            } else {
                fail("Unknown parameter type: " + field.getGenericType());
            }
        } else if (field.getType().equals(GeneralSettings.FirstNameDisplay.class)) {
            return new Pair<Object, Object>(GeneralSettings.FirstNameDisplay.INITIAL, GeneralSettings.FirstNameDisplay.FULL);
        } else if (field.getType().equals(GeneralSettings.Numbering.class)) {
            return new Pair<Object, Object>(GeneralSettings.Numbering.GLOBAL, GeneralSettings.Numbering.WITHIN_CATEGORIES);
        } else if (field.getType().equals(PublicationStatus.class)) {
            return new Pair<Object, Object>(PublicationStatus.ACCEPTED, PublicationStatus.ARXIV);
        } else {
            fail("Unknown field type: " + field.getGenericType());
        }

        return null; // Unreachable, but necessary to prevent a compile error
    }

    private static void testField(Field field, Object declaringClass, Object val1, Object val2) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        // Check getter and setter signatures
        Method get = declaringClass.getClass().getMethod(makeGetter(field));
        assertTrue("Getter does not have the correct return type.", get.getReturnType().equals(field.getType()));
        
        Method set = declaringClass.getClass().getMethod(makeSetter(field), field.getType());
        assertTrue("Setter does not have the correct return type.", set.getReturnType().equals(void.class));

        // Set the field to val1 and check that get returns val1
        set.invoke(declaringClass, val1);
        Object result = get.invoke(declaringClass);
        assertTrue(get + " returned null.", result != null);
        assertEquals("Getter returned an incorrect value.", val1, result);

        // Set the field to val2 and check that get returns val2
        set.invoke(declaringClass, val2);
        result = get.invoke(declaringClass);
        assertTrue(get + " returned null.", result != null);
        assertEquals("Getter returned an incorrect value.", val2, result);
    }

    public static void testSettingsIO(Settings settings, Object partialSettings) throws IOException, SAXException, ParserConfigurationException {
        // Change the location the settings are read from
        Path tempSettingsFile = Files.createTempFile("PublyIOTestSettings", ".xml");
        tempSettingsFile.toFile().deleteOnExit();
        SettingsReaderCurrent.setSettingsFile(tempSettingsFile);

        for (Field field : partialSettings.getClass().getDeclaredFields()) {
            try {
                Pair<Object, Object> exampleValues = getExampleValues(field);
                testFieldIO(settings, field, partialSettings, exampleValues.getFirst());
                testFieldIO(settings, field, partialSettings, exampleValues.getSecond());
            } catch (IllegalAccessException | ParserConfigurationException | SAXException ex) {
                ex.printStackTrace();
                fail(ex.toString());
            }
        }
    }

    private static void testFieldIO(Settings settings, Field field, Object declaringClass, Object val) throws IllegalAccessException, IOException, ParserConfigurationException, SAXException {
        // Set the field to val
        field.setAccessible(true);
        field.set(declaringClass, val);

        // Store and read the settings
        SettingsWriter.writeSettings(settings);
        Settings readSettings = SettingsReaderCurrent.parseSettings();

        // Check that the field has value val
        Object declaringClassInstance = getSettingsPart(readSettings, declaringClass);
        Object result = field.get(declaringClassInstance);
        assertThat("Field " + field, result, is(val));
    }

    private static Object getSettingsPart(Settings settings, Object declaringClass) {
        if (declaringClass.getClass().equals(FileSettings.class)) {
            return settings.getFileSettings();
        } else if (declaringClass.getClass().equals(CategorySettings.class)) {
            return settings.getCategorySettings();
        } else if (declaringClass.getClass().equals(GeneralSettings.class)) {
            return settings.getGeneralSettings();
        } else if (declaringClass.getClass().equals(HTMLSettings.class)) {
            return settings.getHtmlSettings();
        } else if (declaringClass.getClass().equals(ConsoleSettings.class)) {
            return settings.getConsoleSettings();
        } else {
            fail("Unknown settings type.");
            return null; // Unreachable, but necessary to prevent a compile error
        }
    }
}
