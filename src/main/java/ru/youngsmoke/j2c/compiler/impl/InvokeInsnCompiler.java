package ru.youngsmoke.j2c.compiler.impl;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import ru.youngsmoke.j2c.ClassWriter;
import ru.youngsmoke.j2c.compiler.Compiler;

import java.util.Arrays;


public class InvokeInsnCompiler extends Compiler {
    public InvokeInsnCompiler() {
        super("InvokeInsnCompiler", INVOKEINTERFACE, INVOKESPECIAL, INVOKESTATIC, INVOKEVIRTUAL);
    }

    @Override
    public void compile(ClassWriter classWriter, AbstractInsnNode insnNode, MethodNode method) {
        if (insnNode instanceof MethodInsnNode) {
            //TODO: invoke


            //stackpointer manipulations
            if (insnNode.getOpcode() != INVOKESTATIC) {
                classWriter.getStackPointer().pop();
            }
            classWriter.getStackPointer().pop(Arrays.stream(Type.getArgumentTypes(((MethodInsnNode) insnNode).desc)).mapToInt(Type::getSize).sum()).push(Type.getReturnType(((MethodInsnNode) insnNode).desc).getSize());
        }
    }
}
