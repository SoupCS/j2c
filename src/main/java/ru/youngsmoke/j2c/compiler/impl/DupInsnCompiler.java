package ru.youngsmoke.j2c.compiler.impl;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import ru.youngsmoke.j2c.ClassWriter;
import ru.youngsmoke.j2c.compiler.Compiler;


public class DupInsnCompiler extends Compiler {

    public DupInsnCompiler() {
        super("DupInsnCompiler", DUP, DUP_X1, DUP_X2, DUP2, DUP2_X1, DUP2_X2);
    }

    @Override
    public void compile(ClassWriter writer, AbstractInsnNode insn, MethodNode method) {
        if (insn instanceof InsnNode) {
            switch (insn.getOpcode()) {
                case DUP -> {
                    // DUP=cstack$stackindex0 = cstack$stackindexm1;
                    writer.writeln("        cstack%s = cstack%s;".formatted(writer.getStackPointer().peek(), writer.getStackPointer().peek() - 1));
                    writer.getStackPointer().push(); // stack + 1;
                }
                case DUP_X1 -> {
                    // DUP_X1=cstack$stackindex0 = cstack$stackindexm1;
                    // cstack$stackindexm1 = cstack$stackindexm2;
                    // cstack$stackindexm2 = cstack$stackindex0;
                    writer.writeln("        cstack%s = cstack%s;".formatted(writer.getStackPointer().peek(), writer.getStackPointer().peek() - 1));
                    writer.writeln("        cstack%s = cstack%s;".formatted(writer.getStackPointer().peek() - 1, writer.getStackPointer().peek() - 2));
                    writer.writeln("        cstack%s = cstack%s;".formatted(writer.getStackPointer().peek() - 2, writer.getStackPointer().peek()));
                    writer.getStackPointer().push(); // stack + 1;
                }
                case DUP_X2 -> {
                    // DUP_X2=cstack$stackindex0 = cstack$stackindexm1;
                    // cstack$stackindexm1 = cstack$stackindexm2;
                    // cstack$stackindexm2 = cstack$stackindexm3;
                    // cstack$stackindexm3 = cstack$stackindex0;
                    writer.writeln("        cstack%s = cstack%s;".formatted(writer.getStackPointer().peek(), writer.getStackPointer().peek() - 1));
                    writer.writeln("        cstack%s = cstack%s;".formatted(writer.getStackPointer().peek() - 1, writer.getStackPointer().peek() - 2));
                    writer.writeln("        cstack%s = cstack%s;".formatted(writer.getStackPointer().peek() - 2, writer.getStackPointer().peek() - 3));
                    writer.writeln("        cstack%s = cstack%s;".formatted(writer.getStackPointer().peek() - 3, writer.getStackPointer().peek()));
                    writer.getStackPointer().push(); // stack + 1;
                }

                case DUP2 -> {
                    // DUP2=cstack$stackindex0 = cstack$stackindexm2;
                    // cstack$stackindex1 = cstack$stackindexm1;
                    writer.writeln("        cstack%s = cstack%s;".formatted(writer.getStackPointer().peek(), writer.getStackPointer().peek() - 2));
                    writer.writeln("        cstack%s = cstack%s;".formatted(writer.getStackPointer().peek() + 1, writer.getStackPointer().peek() - 1));
                    writer.getStackPointer().push().push(); // stack + 2;
                }
                case DUP2_X1 -> {
                    // DUP2_X1=cstack$stackindex0 = cstack$stackindexm2;
                    // cstack$stackindex1 = cstack$stackindexm1;
                    // cstack$stackindexm1 = cstack$stackindexm3;
                    // cstack$stackindexm2 = cstack$stackindex1;
                    // cstack$stackindexm3 = cstack$stackindex0;
                    writer.writeln("        cstack%s = cstack%s;".formatted(writer.getStackPointer().peek(), writer.getStackPointer().peek() - 2));
                    writer.writeln("        cstack%s = cstack%s;".formatted(writer.getStackPointer().peek() + 1, writer.getStackPointer().peek() - 1));
                    writer.writeln("        cstack%s = cstack%s;".formatted(writer.getStackPointer().peek() - 1, writer.getStackPointer().peek() - 3));
                    writer.writeln("        cstack%s = cstack%s;".formatted(writer.getStackPointer().peek() - 2, writer.getStackPointer().peek() + 1));
                    writer.writeln("        cstack%s = cstack%s;".formatted(writer.getStackPointer().peek() - 3, writer.getStackPointer().peek()));
                    writer.getStackPointer().push().push(); // stack + 2;
                }
                case DUP2_X2 -> {
                    // DUP2_X2=cstack$stackindex0 = cstack$stackindexm2;
                    // cstack$stackindex1 = cstack$stackindexm1;
                    // cstack$stackindexm1 = cstack$stackindexm3;
                    // cstack$stackindexm2 = cstack$stackindexm4;
                    // cstack$stackindexm3 = cstack$stackindex1;
                    // cstack$stackindexm4 = cstack$stackindex0;
                    writer.writeln("        cstack%s = cstack%s;".formatted(writer.getStackPointer().peek(), writer.getStackPointer().peek() - 2));
                    writer.writeln("        cstack%s = cstack%s;".formatted(writer.getStackPointer().peek() + 1, writer.getStackPointer().peek() - 1));
                    writer.writeln("        cstack%s = cstack%s;".formatted(writer.getStackPointer().peek() - 1, writer.getStackPointer().peek() - 3));
                    writer.writeln("        cstack%s = cstack%s;".formatted(writer.getStackPointer().peek() - 2, writer.getStackPointer().peek() - 4));
                    writer.writeln("        cstack%s = cstack%s;".formatted(writer.getStackPointer().peek() - 3, writer.getStackPointer().peek() + 1));
                    writer.writeln("        cstack%s = cstack%s;".formatted(writer.getStackPointer().peek() - 4, writer.getStackPointer().peek()));
                    writer.getStackPointer().push().push(); // stack + 2;
                }
            }
        }
    }
}
