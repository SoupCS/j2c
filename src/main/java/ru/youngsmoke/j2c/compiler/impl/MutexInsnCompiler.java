package ru.youngsmoke.j2c.compiler.impl;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import ru.youngsmoke.j2c.ClassWriter;
import ru.youngsmoke.j2c.compiler.Compiler;


public class MutexInsnCompiler extends Compiler {

    public MutexInsnCompiler() {
        super("MutexInsnCompiler", MONITOREXIT, MONITORENTER);
    }

    @Override
    public void compile(ClassWriter writer, AbstractInsnNode insn, MethodNode method) {
        if (insn instanceof InsnNode) {
            switch (insn.getOpcode()) {
                case MONITORENTER -> {
                    writer.writeln("        env->MonitorEnter(cstack%s.l);".formatted(writer.getStackPointer().peek()));
                    writer.getStackPointer().pop();
                }
                case MONITOREXIT -> {
                    writer.writeln("        env->MonitorExit(cstack%s.l);".formatted(writer.getStackPointer().peek()));
                    writer.getStackPointer().pop();
                }
            }
        }
    }
}
