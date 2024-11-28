package ru.youngsmoke.j2c.compiler.impl;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import ru.youngsmoke.j2c.ClassWriter;
import ru.youngsmoke.j2c.compiler.Compiler;


public class InstanceOfInsnCompiler extends Compiler {

    public InstanceOfInsnCompiler() {
        super("InstanceOfInsnCompiler", INSTANCEOF);
    }

    @Override
    public void compile(ClassWriter writer, AbstractInsnNode insn, MethodNode method) {
        //INSTANCEOF=cstack$stackindexm1.i = cstack$stackindexm1.l == nullptr ? false : env->IsInstanceOf(cstack$stackindexm1.l, $desc_ptr);
        if (insn instanceof TypeInsnNode) {
            writer.writeln("        cstack%s.i = cstack%s.l == nullptr ? false : env->IsInstanceOf(cstack%s.l, %s);"
                    .formatted(writer.getStackPointer().peek() - 1, writer.getStackPointer().peek() - 1, writer.getStackPointer().peek() - 1, ((TypeInsnNode) insn).desc)
            );
        }
    }
}
