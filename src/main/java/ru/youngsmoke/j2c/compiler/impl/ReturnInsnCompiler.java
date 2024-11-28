package ru.youngsmoke.j2c.compiler.impl;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import ru.youngsmoke.j2c.ClassWriter;
import ru.youngsmoke.j2c.compiler.Compiler;
import ru.youngsmoke.j2c.compiler.MethodCompiler;

public class ReturnInsnCompiler extends Compiler {
    public ReturnInsnCompiler() {
        super("ReturnInsnCompiler", RETURN, ARETURN, DRETURN, FRETURN, LRETURN, IRETURN);
    }

    @Override
    public void compile(ClassWriter classWriter, AbstractInsnNode insnNode, MethodNode method) {
        if (insnNode instanceof InsnNode) {
            Type ret = Type.getReturnType(method.desc);
            switch (insnNode.getOpcode()) {
                case IRETURN ->
                        classWriter.writeln("       return (%s) cstack%s.i;".formatted(MethodCompiler.CPP_TYPES[ret.getSort()], (classWriter.getStackPointer().pop().peek())));
                case DRETURN ->
                        classWriter.writeln("       return (%s) cstack%s.d;".formatted(MethodCompiler.CPP_TYPES[ret.getSort()], (classWriter.getStackPointer().pop().pop().peek())));
                case ARETURN ->
                        classWriter.writeln("       return (%s) cstack%s.l;".formatted(MethodCompiler.CPP_TYPES[ret.getSort()], (classWriter.getStackPointer().pop().peek())));
                case FRETURN ->
                        classWriter.writeln("       return (%s) cstack%s.f;".formatted(MethodCompiler.CPP_TYPES[ret.getSort()], (classWriter.getStackPointer().pop().peek())));
                case LRETURN ->
                        classWriter.writeln("       return (%s) cstack%s.j;".formatted(MethodCompiler.CPP_TYPES[ret.getSort()], (classWriter.getStackPointer().pop().pop().peek())));
                case RETURN -> classWriter.writeln("       return;");
            }


        }
    }
}
