package ru.youngsmoke.j2c.compiler.impl;


import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodNode;
import ru.youngsmoke.j2c.ClassWriter;
import ru.youngsmoke.j2c.compiler.Compiler;

public class IfNullInsnCompiler extends Compiler {
    public IfNullInsnCompiler() {
        super("IfNullInsnCompiler", IFNULL, IFNONNULL);
    }

    @Override
    public void compile(ClassWriter classWriter, AbstractInsnNode insnNode, MethodNode method) {
        if (insnNode instanceof JumpInsnNode) {
            classWriter.writeln("       if (" + (insnNode.getOpcode() == IFNULL ? "" : "!") + "env->IsSameObject(cstack%s.l, nullptr)) goto %s;".formatted(classWriter.getStackPointer().pop().peek(), classWriter.getLabelPool().getName(((JumpInsnNode) insnNode).label.getLabel())));

        }
    }

}
