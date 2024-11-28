package ru.youngsmoke.j2c.preprocessors.utils;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import java.util.function.Supplier;

public class PreprocessorUtils {

    public static final Supplier<AbstractInsnNode> LOOKUP_LOCAL = () -> new MethodInsnNode(Opcodes.INVOKESTATIC,
            "java/lang/invoke/MethodHandles", "lookup",
            "()Ljava/lang/invoke/MethodHandles$Lookup;");
    public static final Supplier<AbstractInsnNode> CLASSLOADER_LOCAL = () -> new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "java/lang/Class", "getClassLoader",
            "()Ljava/lang/ClassLoader;");
}
