package ru.youngsmoke.j2c.compiler.impl;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import ru.youngsmoke.j2c.ClassWriter;
import ru.youngsmoke.j2c.compiler.Compiler;


public class MathInsnCompiler extends Compiler {

    public MathInsnCompiler() {
        super("MathInsnCompiler", IADD, LADD, FADD, DADD,
                ISUB, LSUB, FSUB, DSUB,
                IMUL, LMUL, FMUL, DMUL,
                INEG, LNEG, FNEG, DNEG,
                ISHL, LSHL, ISHR, LSHR,
                IUSHR, LUSHR,
                IAND, LAND,
                IOR, LOR,
                IXOR, LXOR,
                LCMP,
                FREM, DREM,
                FDIV, DDIV
        );
    }

    @Override
    public void compile(ClassWriter writer, AbstractInsnNode insn, MethodNode method) {
        if (insn instanceof InsnNode) {

            // IADD=cstack$stackindexm2.i = cstack$stackindexm2.i + cstack$stackindexm1.i;
            //  LADD=cstack$stackindexm4.j = cstack$stackindexm4.j + cstack$stackindexm2.j;
            //  FADD=cstack$stackindexm2.f = cstack$stackindexm2.f + cstack$stackindexm1.f;
            //  DADD=cstack$stackindexm4.d = cstack$stackindexm4.d + cstack$stackindexm2.d;
            switch (insn.getOpcode()) {
                case IADD -> {
                    writer.writeln("        cstack%s.i = cstack%s.i + cstack%s.i;"
                            .formatted(
                                    writer.getStackPointer().peek() - 2,
                                    writer.getStackPointer().peek() - 2,
                                    writer.getStackPointer().peek() - 1
                            )
                    );
                    writer.getStackPointer().pop();
                }
                case LADD -> {
                    writer.writeln("        cstack%s.j = cstack%s.j + cstack%s.j;"
                            .formatted(
                                    writer.getStackPointer().peek() - 4,
                                    writer.getStackPointer().peek() - 4,
                                    writer.getStackPointer().peek() - 2
                            )
                    );
                    writer.getStackPointer().pop(2);

                }
                case FADD -> {
                    writer.writeln("        cstack%s.f = cstack%s.f + cstack%s.f;"
                            .formatted(
                                    writer.getStackPointer().peek() - 2,
                                    writer.getStackPointer().peek() - 2,
                                    writer.getStackPointer().peek() - 1
                            )
                    );
                    writer.getStackPointer().pop();
                }
                case DADD -> {
                    writer.writeln("        cstack%s.d = cstack%s.d + cstack%s.d;"
                            .formatted(
                                    writer.getStackPointer().peek() - 4,
                                    writer.getStackPointer().peek() - 4,
                                    writer.getStackPointer().peek() - 2
                            )
                    );
                    writer.getStackPointer().pop(2);
                }
                /*
ISUB=cstack$stackindexm2.i = cstack$stackindexm2.i - cstack$stackindexm1.i;
LSUB=cstack$stackindexm4.j = cstack$stackindexm4.j - cstack$stackindexm2.j;
FSUB=cstack$stackindexm2.f = cstack$stackindexm2.f - cstack$stackindexm1.f;
DSUB=cstack$stackindexm4.d = cstack$stackindexm4.d - cstack$stackindexm2.d;
*/
                case ISUB -> {
                    writer.writeln("        cstack%s.i = cstack%s.i - cstack%s.i;"
                            .formatted(
                                    writer.getStackPointer().peek() - 2,
                                    writer.getStackPointer().peek() - 2,
                                    writer.getStackPointer().peek() - 1
                            )
                    );
                    writer.getStackPointer().pop();
                }
                case LSUB -> {
                    writer.writeln("        cstack%s.j = cstack%s.j - cstack%s.j;"
                            .formatted(
                                    writer.getStackPointer().peek() - 4,
                                    writer.getStackPointer().peek() - 4,
                                    writer.getStackPointer().peek() - 2
                            )
                    );
                    writer.getStackPointer().pop(2);

                }
                case FSUB -> {
                    writer.writeln("        cstack%s.f = cstack%s.f - cstack%s.f;"
                            .formatted(
                                    writer.getStackPointer().peek() - 2,
                                    writer.getStackPointer().peek() - 2,
                                    writer.getStackPointer().peek() - 1
                            )
                    );
                    writer.getStackPointer().pop();
                }
                case DSUB -> {
                    writer.writeln("        cstack%s.d = cstack%s.d - cstack%s.d;"
                            .formatted(
                                    writer.getStackPointer().peek() - 4,
                                    writer.getStackPointer().peek() - 4,
                                    writer.getStackPointer().peek() - 2
                            )
                    );
                    writer.getStackPointer().pop(2);
                }

/*
IMUL=cstack$stackindexm2.i = cstack$stackindexm2.i * cstack$stackindexm1.i;
LMUL=cstack$stackindexm4.j = cstack$stackindexm4.j * cstack$stackindexm2.j;
FMUL=cstack$stackindexm2.f = cstack$stackindexm2.f * cstack$stackindexm1.f;
DMUL=cstack$stackindexm4.d = cstack$stackindexm4.d * cstack$stackindexm2.d;
*/
                case IMUL -> {
                    writer.writeln("        cstack%s.i = cstack%s.i * cstack%s.i;"
                            .formatted(
                                    writer.getStackPointer().peek() - 2,
                                    writer.getStackPointer().peek() - 2,
                                    writer.getStackPointer().peek() - 1
                            )
                    );
                    writer.getStackPointer().pop();
                }
                case LMUL -> {
                    writer.writeln("        cstack%s.j = cstack%s.j * cstack%s.j;"
                            .formatted(
                                    writer.getStackPointer().peek() - 4,
                                    writer.getStackPointer().peek() - 4,
                                    writer.getStackPointer().peek() - 2
                            )
                    );
                    writer.getStackPointer().pop(2);

                }
                case FMUL -> {
                    writer.writeln("        cstack%s.f = cstack%s.f * cstack%s.f;"
                            .formatted(
                                    writer.getStackPointer().peek() - 2,
                                    writer.getStackPointer().peek() - 2,
                                    writer.getStackPointer().peek() - 1
                            )
                    );
                    writer.getStackPointer().pop();
                }
                case DMUL -> {
                    writer.writeln("        cstack%s.d = cstack%s.d * cstack%s.d;"
                            .formatted(
                                    writer.getStackPointer().peek() - 4,
                                    writer.getStackPointer().peek() - 4,
                                    writer.getStackPointer().peek() - 2
                            )
                    );
                    writer.getStackPointer().pop(2);
                }

                /*
INEG=cstack$stackindexm1.i = -cstack$stackindexm1.i;
LNEG=cstack$stackindexm2.j = -cstack$stackindexm2.j;
FNEG=cstack$stackindexm1.f = -cstack$stackindexm1.f;
DNEG=cstack$stackindexm2.d = -cstack$stackindexm2.d;
*/

                case INEG -> {
                    writer.writeln("        cstack%s.i = -cstack%s.i;"
                            .formatted(
                                    writer.getStackPointer().peek() - 1,
                                    writer.getStackPointer().peek() - 1
                            )
                    );
                }
                case LNEG -> {
                    writer.writeln("        cstack%s.j = -cstack%s.j;"
                            .formatted(
                                    writer.getStackPointer().peek() - 2,
                                    writer.getStackPointer().peek() - 2
                            )
                    );
                }
                case FNEG -> {
                    writer.writeln("        cstack%s.f = -cstack%s.f;"
                            .formatted(
                                    writer.getStackPointer().peek() - 1,
                                    writer.getStackPointer().peek() - 1
                            )
                    );
                }
                case DNEG -> {
                    writer.writeln("        cstack%s.d = -cstack%s.d;"
                            .formatted(
                                    writer.getStackPointer().peek() - 2,
                                    writer.getStackPointer().peek() - 2
                            )
                    );
                }

                /*
ISHL=cstack$stackindexm2.i = cstack$stackindexm2.i << (0x1f & cstack$stackindexm1.i);
LSHL=cstack$stackindexm3.j = cstack$stackindexm3.j << (0x3f & cstack$stackindexm1.i);
ISHR=cstack$stackindexm2.i = cstack$stackindexm2.i >> (0x1f & cstack$stackindexm1.i);
LSHR=cstack$stackindexm3.j = cstack$stackindexm3.j >> (0x3f & cstack$stackindexm1.i);
*/
                case ISHL -> {
                    writer.writeln("        cstack%s.i = cstack%s.i << (0x1f & cstack%s.i);"
                            .formatted(
                                    writer.getStackPointer().peek() - 2,
                                    writer.getStackPointer().peek() - 2,
                                    writer.getStackPointer().peek() - 1
                            )
                    );
                    writer.getStackPointer().pop();
                }
                case LSHL -> {
                    writer.writeln("        cstack%s.j = cstack%s.j << (0x3f & cstack%s.j);"
                            .formatted(
                                    writer.getStackPointer().peek() - 3,
                                    writer.getStackPointer().peek() - 3,
                                    writer.getStackPointer().peek() - 1
                            )
                    );
                    writer.getStackPointer().pop();
                }
                case ISHR -> {
                    writer.writeln("        cstack%s.i = cstack%s.i >> (0x1f & cstack%s.i);"
                            .formatted(
                                    writer.getStackPointer().peek() - 2,
                                    writer.getStackPointer().peek() - 2,
                                    writer.getStackPointer().peek() - 1
                            )
                    );
                    writer.getStackPointer().pop();
                }
                case LSHR -> {
                    writer.writeln("        cstack%s.j = cstack%s.j >> (0x3f & cstack%s.j);"
                            .formatted(
                                    writer.getStackPointer().peek() - 3,
                                    writer.getStackPointer().peek() - 3,
                                    writer.getStackPointer().peek() - 1
                            )
                    );
                    writer.getStackPointer().pop();
                }

                /*
IUSHR=cstack$stackindexm2.i = (jint) (((uint32_t) cstack$stackindexm2.i) >> (((uint32_t) cstack$stackindexm1.i) & 0x1f));
LUSHR=cstack$stackindexm3.j = (jlong) (((uint64_t) cstack$stackindexm3.j) >> (((uint64_t) cstack$stackindexm1.i) & 0x3f));
*/

                case IUSHR -> {
                    writer.writeln("        cstack%s.i = (jint) (((uint32_t) cstack%s.i) >> (((uint32_t) cstack%s.i) & 0x1f));"
                            .formatted(
                                    writer.getStackPointer().peek() - 2,
                                    writer.getStackPointer().peek() - 2,
                                    writer.getStackPointer().peek() - 1
                            )
                    );
                    writer.getStackPointer().pop();
                }
                case LUSHR -> {
                    writer.writeln("        cstack%s.j = (jlong) (((uint64_t) cstack%s.j) >> (((uint64_t) cstack%s.j) & 0x3f));"
                            .formatted(
                                    writer.getStackPointer().peek() - 3,
                                    writer.getStackPointer().peek() - 3,
                                    writer.getStackPointer().peek() - 1
                            )
                    );
                    writer.getStackPointer().pop();
                }

/*
IAND=cstack$stackindexm2.i = cstack$stackindexm2.i & cstack$stackindexm1.i;
LAND=cstack$stackindexm4.j = cstack$stackindexm4.j & cstack$stackindexm2.j;
*/
                case IAND -> {
                    writer.writeln("        cstack%s.i = cstack%s.i & cstack%s.i;"
                            .formatted(
                                    writer.getStackPointer().peek() - 2,
                                    writer.getStackPointer().peek() - 2,
                                    writer.getStackPointer().peek() - 1
                            )
                    );
                    writer.getStackPointer().pop();
                }
                case LAND -> {
                    writer.writeln("        cstack%s.j = cstack%s.j & cstack%s.j;"
                            .formatted(
                                    writer.getStackPointer().peek() - 4,
                                    writer.getStackPointer().peek() - 4,
                                    writer.getStackPointer().peek() - 2
                            )
                    );
                    writer.getStackPointer().pop().pop();
                }
/*
IOR=cstack$stackindexm2.i = cstack$stackindexm2.i | cstack$stackindexm1.i;
LOR=cstack$stackindexm4.j = cstack$stackindexm4.j | cstack$stackindexm2.j;
*/
                case IOR -> {
                    writer.writeln("        cstack%s.i = cstack%s.i | cstack%s.i;"
                            .formatted(
                                    writer.getStackPointer().peek() - 2,
                                    writer.getStackPointer().peek() - 2,
                                    writer.getStackPointer().peek() - 1
                            )
                    );
                    writer.getStackPointer().pop();
                }
                case LOR -> {
                    writer.writeln("        cstack%s.j = cstack%s.j | cstack%s.j;"
                            .formatted(
                                    writer.getStackPointer().peek() - 4,
                                    writer.getStackPointer().peek() - 4,
                                    writer.getStackPointer().peek() - 2
                            )
                    );
                    writer.getStackPointer().pop().pop();
                }
/*
IXOR=cstack$stackindexm2.i = cstack$stackindexm2.i ^ cstack$stackindexm1.i;
LXOR=cstack$stackindexm4.j = cstack$stackindexm4.j ^ cstack$stackindexm2.j;
*/
                case IXOR -> {
                    writer.writeln("        cstack%s.i = cstack%s.i ^ cstack%s.i;"
                            .formatted(
                                    writer.getStackPointer().peek() - 2,
                                    writer.getStackPointer().peek() - 2,
                                    writer.getStackPointer().peek() - 1
                            )
                    );
                    writer.getStackPointer().pop();
                }
                case LXOR -> {
                    writer.writeln("        cstack%s.j = cstack%s.j ^ cstack%s.j;"
                            .formatted(
                                    writer.getStackPointer().peek() - 4,
                                    writer.getStackPointer().peek() - 4,
                                    writer.getStackPointer().peek() - 2
                            )
                    );
                    writer.getStackPointer().pop().pop();
                }

/*
LCMP=cstack$stackindexm4.i = (cstack$stackindexm4.j == cstack$stackindexm2.j) ? 0 : (cstack$stackindexm4.j > cstack$stackindexm2.j ? 1 : -1);
*/
                case LCMP -> {
                    writer.writeln("        cstack%s.i = (cstack%s.j == cstack%s.j) ? 0 : (cstack%s.j > cstack%s.j ? 1 : -1);"
                            .formatted(
                                    writer.getStackPointer().peek() - 4,
                                    writer.getStackPointer().peek() - 4,
                                    writer.getStackPointer().peek() - 2,
                                    writer.getStackPointer().peek() - 4,
                                    writer.getStackPointer().peek() - 2
                            )
                    );
                    writer.getStackPointer().pop(3);
                }

/*
FREM=cstack$stackindexm2.f = std::fmod(cstack$stackindexm2.f, cstack$stackindexm1.f);
DREM=cstack$stackindexm4.d = std::fmod(cstack$stackindexm4.d, cstack$stackindexm2.d);
*/

                case FREM -> {
                    writer.writeln("        cstack%s.f = std::fmod(cstack%s.f, cstack%s.f);"
                            .formatted(
                                    writer.getStackPointer().peek() - 2,
                                    writer.getStackPointer().peek() - 2,
                                    writer.getStackPointer().peek() - 1
                            )
                    );
                    writer.getStackPointer().pop();
                }
                case DREM -> {
                    writer.writeln("        cstack%s.d = std::fmod(cstack%s.d, cstack%s.d);"
                            .formatted(
                                    writer.getStackPointer().peek() - 4,
                                    writer.getStackPointer().peek() - 4,
                                    writer.getStackPointer().peek() - 2
                            )
                    );
                    writer.getStackPointer().pop(2);
                }

/*
FDIV=cstack$stackindexm2.f = cstack$stackindexm2.f / cstack$stackindexm1.f;
DDIV=cstack$stackindexm4.d = cstack$stackindexm4.d / cstack$stackindexm2.d;
*/
                case FDIV -> {
                    writer.writeln("        cstack%s.f = cstack%s.f / cstack%s.f;"
                            .formatted(
                                    writer.getStackPointer().peek() - 2,
                                    writer.getStackPointer().peek() - 2,
                                    writer.getStackPointer().peek() - 1
                            )
                    );
                    writer.getStackPointer().pop();
                }
                case DDIV -> {
                    writer.writeln("        cstack%s.d = cstack%s.d / cstack%s.d;"
                            .formatted(
                                    writer.getStackPointer().peek() - 4,
                                    writer.getStackPointer().peek() - 4,
                                    writer.getStackPointer().peek() - 2
                            )
                    );
                    writer.getStackPointer().pop(2);
                }
            }
        }
    }
}
