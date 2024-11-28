package ru.youngsmoke.j2c.preprocessors.impl;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import ru.youngsmoke.j2c.NativeProcessor;
import ru.youngsmoke.j2c.utils.Util;

import java.util.Objects;

public class NativePreprocessor implements Preprocessor {

    private MethodNode proxyMethod;

    private void processclinit(MethodNode method, ClassNode clazz) {
        proxyMethod = new MethodNode(
                Opcodes.ACC_STATIC /*| Opcodes.ACC_NATIVE*/,
                "$Clinit",
                method.desc,
                method.signature,
                new String[0]);
        proxyMethod.instructions.add(method.instructions);
        method.instructions.clear();

    }

    @Override
    public void process(ClassNode clazz) {
        if (!Util.hasFlag(clazz.access, Opcodes.ACC_INTERFACE)) {
            clazz.methods.stream()
                    .filter(methodNode -> methodNode.name.equals("<clinit>"))
                    .filter(methodNode -> methodNode.instructions.size() > 0)
                    // .filter(NativeProcessor::shouldProcess)
                    //  .filter(methodNode -> NativeProcessor.classMethodFilter.shouldProcess(clazz, methodNode))
                    .forEach(methodNode -> processclinit(methodNode, clazz));

            InsnList insns = new InsnList();
            insns.add(new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "dev/hypestudio/Protection",
                    "init",
                    "()V",
                    false));
            if (proxyMethod != null) {
                insns.add(new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        clazz.name,
                        "$Clinit",
                        "()V",
                        false));
                clazz.methods.add(proxyMethod);
            }
            insns.add(new InsnNode(Opcodes.RETURN));

            clazz.methods.stream()
                    .filter(methodNode -> methodNode.name.equals("<clinit>"))
                    .forEach(methodNode -> methodNode.instructions = insns);
        }
    }
}
