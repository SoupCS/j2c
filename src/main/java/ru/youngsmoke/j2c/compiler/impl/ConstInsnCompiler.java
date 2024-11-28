package ru.youngsmoke.j2c.compiler.impl;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import ru.youngsmoke.j2c.ClassWriter;
import ru.youngsmoke.j2c.compiler.Compiler;


public class ConstInsnCompiler extends Compiler {

    public ConstInsnCompiler() {
        super("ConstInsnCompiler", ACONST_NULL, ICONST_M1,
                ICONST_0, ICONST_1, ICONST_2, ICONST_3, ICONST_4, ICONST_5,
                LCONST_0, LCONST_1,
                FCONST_0, FCONST_1, FCONST_2,
                DCONST_0, DCONST_1);
    }

    @Override
    public void compile(ClassWriter writer, AbstractInsnNode insn, MethodNode method) {
        if (insn instanceof InsnNode) {
            switch (insn.getOpcode()) {
                case ACONST_NULL -> {
                    writer.writeln("        cstack%s.l = nullptr;".formatted(writer.getStackPointer().peek()));
                    writer.getStackPointer().push();
                }
                case ICONST_M1 -> {
                    writer.writeln("        cstack%s.i = -1;".formatted(writer.getStackPointer().peek()));
                    writer.getStackPointer().push();
                }
                case ICONST_0 -> {
                    writer.writeln("        cstack%s.i = 0;".formatted(writer.getStackPointer().peek()));
                    writer.getStackPointer().push();
                }
                case ICONST_1 -> {
                    writer.writeln("        cstack%s.i = 1;".formatted(writer.getStackPointer().peek()));
                    writer.getStackPointer().push();
                }
                case ICONST_2 -> {
                    writer.writeln("        cstack%s.i = 2;".formatted(writer.getStackPointer().peek()));
                    writer.getStackPointer().push();
                }
                case ICONST_3 -> {
                    writer.writeln("        cstack%s.i = 3;".formatted(writer.getStackPointer().peek()));
                    writer.getStackPointer().push();
                }
                case ICONST_4 -> {
                    writer.writeln("        cstack%s.i = 4;".formatted(writer.getStackPointer().peek()));
                    writer.getStackPointer().push();
                }
                case ICONST_5 -> {
                    writer.writeln("        cstack%s.i = 5;".formatted(writer.getStackPointer().peek()));
                    writer.getStackPointer().push();
                }
                case LCONST_0 -> {
                    writer.writeln("        cstack%s.j = 0;".formatted(writer.getStackPointer().peek()));
                    writer.getStackPointer().push().push();
                }
                case LCONST_1 -> {
                    writer.writeln("        cstack%s.j = 1;".formatted(writer.getStackPointer().peek()));
                    writer.getStackPointer().push().push();
                }
                case FCONST_0 -> {
                    writer.writeln("        cstack%s.i = 0.0f;".formatted(writer.getStackPointer().peek()));
                    writer.getStackPointer().push();
                }
                case FCONST_1 -> {
                    writer.writeln("        cstack%s.i = 1.0f;".formatted(writer.getStackPointer().peek()));
                    writer.getStackPointer().push();
                }
                case FCONST_2 -> {
                    writer.writeln("        cstack%s.i = 2.0f;".formatted(writer.getStackPointer().peek()));
                    writer.getStackPointer().push();
                }
                case DCONST_0 -> {
                    writer.writeln("        cstack%s.d = 0.0;".formatted(writer.getStackPointer().peek()));
                    writer.getStackPointer().push().push();
                }
                case DCONST_1 -> {
                    writer.writeln("        cstack%s.d = 1.0;".formatted(writer.getStackPointer().peek()));
                    writer.getStackPointer().push().push();
                }
            }
        }
    }
}
