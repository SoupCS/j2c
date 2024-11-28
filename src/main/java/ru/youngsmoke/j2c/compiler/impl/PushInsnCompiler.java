package ru.youngsmoke.j2c.compiler.impl;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodNode;
import ru.youngsmoke.j2c.ClassWriter;
import ru.youngsmoke.j2c.compiler.Compiler;

public class PushInsnCompiler extends Compiler {
    public PushInsnCompiler() {
        super("PushInsnCompiler", BIPUSH, SIPUSH);
    }

    @Override
    public void compile(ClassWriter classWriter, AbstractInsnNode insnNode, MethodNode method) {
        if (insnNode instanceof IntInsnNode) {
            String val = String.valueOf(((IntInsnNode) insnNode).operand);
            classWriter.writeln("       cstack%d.i = (jint) %s;".formatted(classWriter.getStackPointer().peek(), val));
            classWriter.getStackPointer().push();
        }
    }
}
