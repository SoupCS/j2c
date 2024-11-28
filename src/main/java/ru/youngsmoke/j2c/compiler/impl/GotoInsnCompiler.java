package ru.youngsmoke.j2c.compiler.impl;

import org.objectweb.asm.tree.*;
import ru.youngsmoke.j2c.ClassWriter;
import ru.youngsmoke.j2c.compiler.Compiler;


public class GotoInsnCompiler extends Compiler {

    public GotoInsnCompiler() {
        super("GotoInsnCompiler", GOTO);
    }

    @Override
    public void compile(ClassWriter writer, AbstractInsnNode insn, MethodNode method) {
        if (insn instanceof JumpInsnNode) {
            writer.writeln("        goto %s;".formatted(writer.getLabelPool().getName(((JumpInsnNode) insn).label.getLabel())));
        }
    }
}