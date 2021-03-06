package user11681.liberica;

import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.util.Map;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;
import net.gudenau.lib.unsafe.Unsafe;
import user11681.reflect.Accessor;
import user11681.reflect.Classes;
import user11681.reflect.Fields;
import user11681.reflect.Invoker;
import user11681.reflect.Pointer;

@SuppressWarnings("unchecked")
public class LibericaPlugin implements Plugin {
    private boolean initialized;

    @Override
    public String getName() {
        return "liberica";
    }

    @Override
    public void init(JavacTask task, String... args) {
        if (!initialized) try {
            Class<?> Source = Classes.load("com.sun.tools.javac.code.Source");
            Class<?> Source$Feature = Classes.load(Source.getName() + "$Feature");

            Comparable<Object> JDK8 = Accessor.getObject(Source, "JDK8");
            Enum<?> LAMBDA = Accessor.getObject(Source$Feature, "LAMBDA");

            Object context = Accessor.getObject(task, "context");
            Object preview = unlock(context, "com.sun.tools.javac.code.Preview", "previewKey");
            Pointer minLevel = new Pointer().instanceField(Source$Feature, "minLevel");
            Messager messager = this.<ProcessingEnvironment>get(context, "com.sun.tools.javac.processing.JavacProcessingEnvironment").getMessager();
            MethodHandle isPreview = Invoker.bind(preview, "isPreview", boolean.class, Source$Feature);

            for (Field field : Fields.staticFields(Source$Feature)) {
                if (Source$Feature.isAssignableFrom(field.getType())) {
                    Pointer pointer = new Pointer().staticField(field);
                    Enum<?> feature = pointer.getObject();

                    if (JDK8.compareTo(minLevel.getObject(feature)) < 0) {
                        messager.printMessage(Diagnostic.Kind.NOTE, "Enabling feature %s.%n".formatted(feature));
                        minLevel.putObject(feature, JDK8);

                        if (!(boolean) isPreview.invoke(feature)) {
                            pointer.putObject(LAMBDA);
                        }
                    }
                }
            }

            initialized = true;
        } catch (Throwable throwable) {
            throw Unsafe.throwException(throwable);
        }
    }

    @Override
    public boolean autoStart() {
        return true;
    }

    private <T> T unlock(Object context, String keyHolder, String field) {
        return (T) Accessor.<Map<Object, Object>>getObject(context, "ht").get(Accessor.getObject(Classes.load(keyHolder), field));
    }

    private <T> T get(Object context, String type) throws Throwable {
        return (T) Invoker.bind(context, "get", Object.class, Class.class).invoke(Classes.load(type));
    }
}
