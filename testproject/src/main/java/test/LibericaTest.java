package test;

import java.lang.reflect.ParameterizedType;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

@Testable
public class LibericaTest {
    @Test
    void checkVersion() throws Throwable {
        ClassReader reader = new ClassReader(LibericaTest.class.getResourceAsStream("Java8.class"));
        ClassNode node = new ClassNode();
        reader.accept(node, 0);

        assert node.version == Opcodes.V1_8 : String.format("bad version %s for class Java8", node.version);
    }

    @Test
    void diamond() {
        assert ((ParameterizedType) Java8.diamond().getClass().getGenericSuperclass()).getActualTypeArguments()[0] == Java8.class;
    }
}
