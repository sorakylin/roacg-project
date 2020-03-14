package com.roacg.test.model;

import com.roacg.core.model.enums.DeletedStatusEnum;
import com.roacg.core.model.enums.convert.AbstractBaseCodeEnumConverter;
import com.roacg.test.model.compiler.JavaStringCompiler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * https://github.com/michaelliao/compiler/tree/master/src/main/java/com/itranswarp/compiler
 */
@RunWith(JUnit4.class)
public class ClsProxyTest {

    String java = "package com.roacg.core.model.enums.convert;\n" +
            "\n" +
            "import @enumclass@;\n" +
            "import com.roacg.core.model.enums.convert.AbstractBaseCodeEnumConverter;\n" +
            "\n" +
            "import javax.persistence.Converter;\n" +
            "\n" +
            "@Converter(autoApply = true)" +
            "\n" +
            "class @enumname@Convert extends AbstractBaseCodeEnumConverter<@enumname@> {\n" +
            "\n" +
            "    public @enumname@Convert() {\n" +
            "        super(@enumname@.class);\n" +
            "    }\n" +
            "}";

    @Before
    public void before() {
        System.out.println("\r\n\r\n\r\n");
    }

    @After
    public void after() {
        System.out.println("\r\n\r\n\r\n");
    }

    @Test
    public void testClazz() throws IOException, ReflectiveOperationException {

        String resultJavaName = DeletedStatusEnum.class.getSimpleName() + "Convert";

        String javaSourceCode = java.replaceAll("@enumclass@", DeletedStatusEnum.class.getName())
                .replaceAll("@enumname@", DeletedStatusEnum.class.getSimpleName());

        JavaStringCompiler compiler = new JavaStringCompiler();
        Map<String, byte[]> results = compiler.compile(resultJavaName + ".java", javaSourceCode);
        Class<?> clazz = compiler.loadClass("com.roacg.core.model.enums.convert." + resultJavaName, results);


        System.out.println(clazz.getName());
        System.out.println(clazz.getSimpleName());
        for (Annotation declaredAnnotation : clazz.getDeclaredAnnotations()) {
            String annotationName = declaredAnnotation.getClass().getName();
            System.out.println(annotationName);
        }

        System.out.println("------");

        Constructor<?> declaredConstructor = clazz.getDeclaredConstructor();
        declaredConstructor.setAccessible(true);
        AbstractBaseCodeEnumConverter converter = (AbstractBaseCodeEnumConverter) declaredConstructor.newInstance();

        Enum anEnum = converter.convertToEntityAttribute(0);
        System.out.println(anEnum.getClass().getName());
        System.out.println(anEnum.name());
    }

}
