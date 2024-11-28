package ru.youngsmoke.j2c.preprocessors;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import ru.youngsmoke.j2c.preprocessors.impl.IndyPreprocessor;
import ru.youngsmoke.j2c.preprocessors.impl.LdcPreprocessor;
import ru.youngsmoke.j2c.preprocessors.impl.NativePreprocessor;
import ru.youngsmoke.j2c.preprocessors.impl.Preprocessor;

import java.util.ArrayList;
import java.util.List;

public class PreprocessorRunner {

    private final static List<Preprocessor> PREPROCESSORS = new ArrayList<>();

    static {
        PREPROCESSORS.add(new IndyPreprocessor());
        PREPROCESSORS.add(new LdcPreprocessor());
        PREPROCESSORS.add(new NativePreprocessor());
    }

    public static void preprocess(ClassNode classNode) {
        for (Preprocessor preprocessor : PREPROCESSORS) {
            preprocessor.process(classNode);
        }
    }
}
