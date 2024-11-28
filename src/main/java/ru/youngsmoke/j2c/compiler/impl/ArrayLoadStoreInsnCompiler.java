package ru.youngsmoke.j2c.compiler.impl;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import ru.youngsmoke.j2c.ClassWriter;
import ru.youngsmoke.j2c.compiler.Compiler;


public class ArrayLoadStoreInsnCompiler extends Compiler {

    public ArrayLoadStoreInsnCompiler() {
        super("ArrayLoadStoreInsnCompiler");
     /*   super(  IALOAD,
                LALOAD,
                FALOAD,
                DALOAD,
                AALOAD,
                BALOAD,
                CALOAD,
                SALOAD,
                IASTORE,
                LASTORE,
                FASTORE,
                DASTORE,
                AASTORE,
                BASTORE,
                CASTORE,
                SASTORE
        );*/
    }

    @Override
    public void compile(ClassWriter writer, AbstractInsnNode insn, MethodNode method) {
        if (insn instanceof InsnNode) {
            // TODO: сделать обработку массивов
        }
    }
}
