package ru.youngsmoke.j2c.compiler.impl;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import ru.youngsmoke.j2c.ClassWriter;
import ru.youngsmoke.j2c.compiler.Compiler;
import ru.youngsmoke.j2c.utils.Util;


public class LoadStoreInsnCompiler extends Compiler {
    public LoadStoreInsnCompiler() {
        super("LoadStoreInsnCompiler", ILOAD, FLOAD, ALOAD, LLOAD, DLOAD, ISTORE, FSTORE, ASTORE, LSTORE, DSTORE);
    }

    @Override
    public void compile(ClassWriter classWriter, AbstractInsnNode insnNode, MethodNode method) {
        if (insnNode instanceof VarInsnNode) {
            switch (insnNode.getOpcode()) {
                case ILOAD -> {
                    classWriter.writeln("       cstack%s.i = clocal%s.i;".formatted(classWriter.getStackPointer().peek(), ((VarInsnNode) insnNode).var));
                    classWriter.getStackPointer().push();
                }
                case FLOAD -> {
                    classWriter.writeln("       cstack%s.f = clocal%s.f;".formatted(classWriter.getStackPointer().peek(), ((VarInsnNode) insnNode).var));
                    classWriter.getStackPointer().push();
                }
                case ALOAD -> {
                    classWriter.writeln("       cstack%s.l = clocal%s.l;".formatted(classWriter.getStackPointer().peek(), ((VarInsnNode) insnNode).var));
                    classWriter.getStackPointer().push();
                }
                case LLOAD -> {
                    classWriter.writeln("       cstack%s.j = clocal%s.j;".formatted(classWriter.getStackPointer().peek(), ((VarInsnNode) insnNode).var));
                    classWriter.getStackPointer().push().push();
                }
                case DLOAD -> {
                    classWriter.writeln("       cstack%s.d = clocal%s.d;".formatted(classWriter.getStackPointer().peek(), ((VarInsnNode) insnNode).var));
                    classWriter.getStackPointer().push().push();
                }
                case ISTORE -> {
                    classWriter.getStackPointer().pop();
                    classWriter.writeln("       clocal%s.i = cstack%s.i;".formatted(((VarInsnNode) insnNode).var, classWriter.getStackPointer().peek()));
                }
                case FSTORE -> {
                    classWriter.getStackPointer().pop();
                    classWriter.writeln("       clocal%s.f = cstack%s.f;".formatted(((VarInsnNode) insnNode).var, classWriter.getStackPointer().peek()));
                }
                case ASTORE -> {
                    classWriter.getStackPointer().pop();
                    classWriter.writeln("       clocal%s.l = cstack%s.l;".formatted(((VarInsnNode) insnNode).var, classWriter.getStackPointer().peek()));
                }
                case LSTORE -> {
                    classWriter.getStackPointer().pop().pop();
                    classWriter.writeln("       clocal%s.j = cstack%s.j;".formatted(((VarInsnNode) insnNode).var, classWriter.getStackPointer().peek()));
                }
                case DSTORE -> {
                    classWriter.getStackPointer().pop().pop();
                    classWriter.writeln("       clocal%s.d = cstack%s.d;".formatted(((VarInsnNode) insnNode).var, classWriter.getStackPointer().peek()));
                }
            }
        }
    }
}
