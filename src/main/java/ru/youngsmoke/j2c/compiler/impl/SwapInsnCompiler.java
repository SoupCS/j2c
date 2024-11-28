package ru.youngsmoke.j2c.compiler.impl;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import ru.youngsmoke.j2c.ClassWriter;
import ru.youngsmoke.j2c.compiler.Compiler;

public class SwapInsnCompiler extends Compiler {
    public SwapInsnCompiler() {
        super("SwapInsnCompiler", SWAP);
    }

    @Override
    public void compile(ClassWriter classWriter, AbstractInsnNode insnNode, MethodNode method) {
        if (insnNode instanceof InsnNode) {
            classWriter.writeln("       std::swap(cstack%d, cstack%d);".formatted(classWriter.getStackPointer().peek() - 1, classWriter.getStackPointer().peek() - 2));
        }
    }
}
