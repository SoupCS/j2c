package ru.youngsmoke.j2c.compiler.impl;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import ru.youngsmoke.j2c.ClassWriter;
import ru.youngsmoke.j2c.compiler.Compiler;
import ru.youngsmoke.j2c.utils.Util;

public class LdcInsnCompiler extends Compiler {
    public LdcInsnCompiler() {
        super("LdcInsnCompiler", LDC);
    }

    public static String getIntString(int value) {
        return value == Integer.MIN_VALUE ? "(jint) 2147483648U" : String.valueOf(value);
    }

    public static String getLongValue(long value) {
        return value == Long.MIN_VALUE ? "(jlong) 9223372036854775808ULL" : String.valueOf(value) + "LL";
    }

    public static String getFloatValue(float value) {
        if (Float.isNaN(value)) {
            return "NAN";
        } else if (value == Float.POSITIVE_INFINITY) {
            return "HUGE_VALF";
        } else if (value == Float.NEGATIVE_INFINITY) {
            return "-HUGE_VALF";
        }
        return value + "f";
    }

    public static String getDoubleValue(double value) {
        if (Double.isNaN(value)) {
            return "NAN";
        } else if (value == Double.POSITIVE_INFINITY) {
            return "HUGE_VAL";
        } else if (value == Double.NEGATIVE_INFINITY) {
            return "-HUGE_VAL";
        }
        return String.valueOf(value);
    }

    @Override
    public void compile(ClassWriter classWriter, AbstractInsnNode insnNode, MethodNode method) {
        if (insnNode instanceof LdcInsnNode) {
            Object cst = ((LdcInsnNode) insnNode).cst;
            if (cst instanceof String) {
                classWriter.writeln("       cstack%s.l = env->NewString((unsigned short[]) {%s}, %s);".formatted(classWriter.getStackPointer().peek(), Util.utf82unicode((String) cst.toString()), cst.toString().length()));
            } else if (cst instanceof Integer) {
                classWriter.writeln("       cstack%s.i = %s;".formatted(classWriter.getStackPointer().peek(), getIntString((Integer) cst)));
            } else if (cst instanceof Long) {
                classWriter.writeln("       cstack%s.j = %s;".formatted(classWriter.getStackPointer().peek(), getLongValue((Long) cst)));
            } else if (cst instanceof Float) {
                classWriter.writeln("       cstack%s.f = %s;".formatted(classWriter.getStackPointer().peek(), getFloatValue((Float) cst)));
            } else if (cst instanceof Double) {
                classWriter.writeln("       cstack%s.d = %s;".formatted(classWriter.getStackPointer().peek(), getDoubleValue((Double) cst)));
            } else if (cst instanceof Type) {
                classWriter.writeln("       cstack%s.l = env->FindClass(\"%s\");".formatted(classWriter.getStackPointer().peek(), ((Type) cst).getClassName().replace(".", "/")));
                // System.out.println(((Type) cst).getClassName());
                //System.out.println("    WARNING!! UNSUPPORTED LDC INSTANCEOF TYPE (class)");
            }

            if (((LdcInsnNode) insnNode).cst instanceof Double || ((LdcInsnNode) insnNode).cst instanceof Long) {
                classWriter.getStackPointer().push().push();
            } else {
                classWriter.getStackPointer().push();
            }
        }
    }
}
