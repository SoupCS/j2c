package ru.youngsmoke.j2c.compiler.impl;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import ru.youngsmoke.j2c.ClassWriter;
import ru.youngsmoke.j2c.compiler.Compiler;

public class PopInsnCompiler extends Compiler {
    public PopInsnCompiler() {
        super("PopInsnCompiler", POP, POP2);
    }

    @Override
    public void compile(ClassWriter writer, AbstractInsnNode insn, MethodNode method) {
        if (insn instanceof InsnNode) {
            switch (insn.getOpcode()) {
                case POP -> {
                    writer.getStackPointer().pop();
                }
                case POP2 -> {
                    writer.getStackPointer().pop().pop();
                }
            }
            //   System.out.println(Util.getOpcodeString(insn.getOpcode()));
        }
    }
}
