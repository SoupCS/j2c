package ru.youngsmoke.j2c.compiler.impl;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import ru.youngsmoke.j2c.ClassWriter;
import ru.youngsmoke.j2c.compiler.Compiler;


public class NewInsnCompiler extends Compiler {

    public NewInsnCompiler() {
        super("NewInsnCompiler", NEW);
    }

    @Override
    public void compile(ClassWriter writer, AbstractInsnNode insn, MethodNode method) {
        if (insn instanceof TypeInsnNode) {
            writer.writeln("        if (jobject obj = env->AllocObject(env->FindClass(\"%s\"))) { cstack%s.l = obj; }".formatted(((TypeInsnNode) insn).desc, writer.getStackPointer().peek()));
            writer.getStackPointer().push();
        }
    }
}
