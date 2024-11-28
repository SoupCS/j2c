package ru.youngsmoke.j2c.compiler.impl;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import ru.youngsmoke.j2c.ClassWriter;
import ru.youngsmoke.j2c.compiler.Compiler;


public class CheckcastInsnCompiler extends Compiler {

    public CheckcastInsnCompiler() {
        super("CheckcastInsnCompiler", CHECKCAST);
    }

    @Override
    public void compile(ClassWriter writer, AbstractInsnNode insn, MethodNode method) {
        if (insn instanceof TypeInsnNode) {
            // TODO: add Throw
            writer.writeln("        if (cstack%s.l != nullptr && !env->IsInstanceOf(cstack%s.l, env->FindClass(\"%s\")) {}".formatted(writer.getStackPointer().peek() - 1, writer.getStackPointer().peek() - 1, ((TypeInsnNode) insn).desc));
        }
    }
}
