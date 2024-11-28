package ru.youngsmoke.j2c.compiler.impl;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodNode;
import ru.youngsmoke.j2c.ClassWriter;
import ru.youngsmoke.j2c.compiler.Compiler;
import ru.youngsmoke.j2c.utils.Util;

public class IfInsnCompiler extends Compiler {
    public IfInsnCompiler() {
        super("IfInsnCompiler", IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE, IF_ICMPEQ, IF_ICMPNE, IF_ICMPLT, IF_ICMPGE, IF_ICMPGT, IF_ICMPLE, IF_ACMPEQ, IF_ACMPNE);
    }

    private static String insToFrmtExpr(String postfix, int opcode) {
        if (opcode == IF_ACMPEQ || opcode == IF_ACMPNE) {
            return "%senv->IsSameObject(%s, %s)".formatted(opcode == IF_ACMPEQ ? "" : "!", "%s", "%s");
        }

        String operator = switch (postfix) {
            case "EQ" -> "==";
            case "NE" -> "!=";
            case "LT" -> "<";
            case "GE" -> ">=";
            case "GT" -> ">";
            case "LE" -> "<=";
            default -> throw new IllegalStateException("Unexpected value: " + postfix);
        };

        return "%s %s %s".formatted("%s", operator, "%s");
    }

    @Override
    public void compile(ClassWriter classWriter, AbstractInsnNode insnNode, MethodNode method) {
        if (insnNode instanceof JumpInsnNode) {
            boolean unary = Util.getOpcodeString(insnNode.getOpcode()).length() == 4;
            String expr = insToFrmtExpr(Util.getOpcodeString(insnNode.getOpcode()).substring(Util.getOpcodeString(insnNode.getOpcode()).length() - 2), insnNode.getOpcode());
            String firstOp = unary ? "0" : "cstack%s.i".formatted(classWriter.getStackPointer().pop().peek()); //pop
            String secondOp = "cstack%s.i".formatted(classWriter.getStackPointer().pop().peek()); //pop

            classWriter.writeln("       if (%s) {".formatted(expr.formatted(secondOp, firstOp)));
            classWriter.writeln("           goto %s;".formatted(classWriter.getLabelPool().getName(((JumpInsnNode) insnNode).label.getLabel())));
            classWriter.writeln("       }");
            //System.out.println(Util.getOpcodeString(insnNode.getOpcode()));
        }
    }

}
