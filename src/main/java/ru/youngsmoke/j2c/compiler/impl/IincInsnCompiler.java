package ru.youngsmoke.j2c.compiler.impl;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.MethodNode;
import ru.youngsmoke.j2c.ClassWriter;
import ru.youngsmoke.j2c.compiler.Compiler;


public class IincInsnCompiler extends Compiler {

    public IincInsnCompiler() {
        super("IincInsnCompiler", IINC);
    }

    @Override
    public void compile(ClassWriter writer, AbstractInsnNode insn, MethodNode method) {
        if (insn instanceof IincInsnNode) {
            writer.writeln("        clocal%s.i += %s;".formatted(((IincInsnNode) insn).var, ((IincInsnNode) insn).incr));
        }
    }
}
