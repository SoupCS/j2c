package ru.youngsmoke.j2c.compiler.impl;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import ru.youngsmoke.j2c.ClassWriter;
import ru.youngsmoke.j2c.compiler.Compiler;

public class ConvertInsnCompiler extends Compiler {
    public ConvertInsnCompiler() {
        super("ConvertInsnCompiler", I2L, I2F, I2D,
                L2I, L2F, L2D,
                F2I, F2L, F2D,
                D2I, D2L, D2F,
                I2B, I2C, I2S
        );
    }

    @Override
    public void compile(ClassWriter writer, AbstractInsnNode insn, MethodNode method) {
        if (insn instanceof InsnNode) {
            switch (insn.getOpcode()) {
                case I2L -> {
                    //I2L=cstack$stackindexm1.j = cstack$stackindexm1.i;
                    writer.writeln("        cstack%s.j = cstack%s.i;".formatted(writer.getStackPointer().peek() - 1, writer.getStackPointer().peek() - 1));
                    writer.getStackPointer().push();
                }
                case I2F -> {
                    //I2F=cstack$stackindexm1.f = (jfloat) cstack$stackindexm1.i;
                    writer.writeln("        cstack%s.f = (jfloat)  cstack%s.i;".formatted(writer.getStackPointer().peek() - 1, writer.getStackPointer().peek() - 1));

                }
                case I2D -> {
                    //I2D=cstack$stackindexm1.d = (jdouble) cstack$stackindexm1.i;
                    writer.writeln("        cstack%s.d = (jdouble) cstack%s.i;".formatted(writer.getStackPointer().peek() - 1, writer.getStackPointer().peek() - 1));
                    writer.getStackPointer().push();
                }
                case L2I -> {
                    //L2I=cstack$stackindexm2.i = (jint) cstack$stackindexm2.j;
                    writer.writeln("        cstack%s.i = (jint) cstack%s.j;".formatted(writer.getStackPointer().peek() - 2, writer.getStackPointer().peek() - 2));
                    writer.getStackPointer().pop();
                }
                case L2F -> {
                    //L2F=cstack$stackindexm2.f = (jfloat) cstack$stackindexm2.j;
                    writer.writeln("        cstack%s.f = (jfloat) cstack%s.j;".formatted(writer.getStackPointer().peek() - 2, writer.getStackPointer().peek() - 2));
                    writer.getStackPointer().pop();
                }
                case L2D -> {
                    //L2D=cstack$stackindexm2.d = (jdouble) cstack$stackindexm2.j;
                    writer.writeln("        cstack%s.d = (jdouble) cstack%s.j;".formatted(writer.getStackPointer().peek() - 2, writer.getStackPointer().peek() - 2));

                }
                case F2I -> {
                    //F2I=cstack$stackindexm1.i = (jint) cstack$stackindexm1.f;
                    writer.writeln("        cstack%s.i = (jint) cstack%s.f;".formatted(writer.getStackPointer().peek() - 1, writer.getStackPointer().peek() - 2));
                }
                case F2L -> {
                    //F2L=cstack$stackindexm1.j = (jlong) cstack$stackindexm1.f;
                    writer.writeln("        cstack%s.j = (jlong) cstack%s.f;".formatted(writer.getStackPointer().peek() - 1, writer.getStackPointer().peek() - 2));
                }
                case F2D -> {
                    //F2D=cstack$stackindexm1.d = (jdouble) cstack$stackindexm1.f;
                    writer.writeln("        cstack%s.d = (jdouble) cstack%s.f;".formatted(writer.getStackPointer().peek() - 1, writer.getStackPointer().peek() - 2));
                    writer.getStackPointer().push();
                }
                case D2I -> {
                    //D2I=cstack$stackindexm2.i = (jint) cstack$stackindexm2.d;
                    writer.writeln("        cstack%s.i = (jint) cstack%s.d;".formatted(writer.getStackPointer().peek() - 2, writer.getStackPointer().peek() - 2));
                    writer.getStackPointer().pop();
                }
                case D2L -> {
                    //D2L=cstack$stackindexm2.j = (jlong) cstack$stackindexm2.d;
                    writer.writeln("        cstack%s.j = (jlong) cstack%s.d;".formatted(writer.getStackPointer().peek() - 2, writer.getStackPointer().peek() - 2));
                }
                case D2F -> {
                    //D2F=cstack$stackindexm2.f = (jfloat) cstack$stackindexm2.d;
                    writer.writeln("        cstack%s.f = (jfloat) cstack%s.d;".formatted(writer.getStackPointer().peek() - 2, writer.getStackPointer().peek() - 2));
                    writer.getStackPointer().pop();
                }
                case I2B -> {
                    //I2B=cstack$stackindexm1.i = (jint) (jbyte) cstack$stackindexm1.i;
                    writer.writeln("        cstack%s.i = (jint) (jbyte) cstack%s.i;".formatted(writer.getStackPointer().peek() - 1, writer.getStackPointer().peek() - 1));

                }
                case I2C -> {
                    //I2C=cstack$stackindexm1.i = (jint) (jchar) cstack$stackindexm1.i;
                    writer.writeln("        cstack%s.i = (jint) (jchar) cstack%s.i;".formatted(writer.getStackPointer().peek() - 1, writer.getStackPointer().peek() - 1));

                }
                case I2S -> {
                    //I2S=cstack$stackindexm1.i = (jint) (jshort) cstack$stackindexm1.i;
                    writer.writeln("        cstack%s.i = (jint) (jshort) cstack%s.i;".formatted(writer.getStackPointer().peek() - 1, writer.getStackPointer().peek() - 1));
                }
            }
        }
    }
}
