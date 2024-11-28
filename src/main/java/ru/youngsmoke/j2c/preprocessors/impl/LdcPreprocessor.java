package ru.youngsmoke.j2c.preprocessors.impl;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import ru.youngsmoke.j2c.NativeProcessor;
import ru.youngsmoke.j2c.preprocessors.utils.MethodHandleUtils;
import ru.youngsmoke.j2c.utils.Util;

public class LdcPreprocessor implements Preprocessor {
    @Override
    public void process(ClassNode classNode) {
        if (!Util.hasFlag(classNode.access, Opcodes.ACC_INTERFACE)) {
            classNode.methods.stream()
                    .filter(NativeProcessor::shouldProcess)
                    .filter(methodNode -> NativeProcessor.classMethodFilter.shouldProcess(classNode, methodNode))
                    .forEach(methodNode -> {
                        AbstractInsnNode insnNode = methodNode.instructions.getFirst();
                        while (insnNode != null) {
                            if (insnNode instanceof LdcInsnNode ldcInsnNode) {

                                if (ldcInsnNode.cst instanceof Handle) {
                                    methodNode.instructions.insertBefore(ldcInsnNode,
                                            MethodHandleUtils.generateMethodHandleLdcInsn((Handle) ldcInsnNode.cst));
                                    AbstractInsnNode nextInsnNode = insnNode.getNext();
                                    methodNode.instructions.remove(insnNode);
                                    insnNode = nextInsnNode;
                                    continue;
                                }

                                if (ldcInsnNode.cst instanceof Type type) {

                                    if (type.getSort() == Type.METHOD) {
                                        methodNode.instructions.insertBefore(ldcInsnNode,
                                                MethodHandleUtils.generateMethodTypeLdcInsn(type, classNode));
                                        AbstractInsnNode nextInsnNode = insnNode.getNext();
                                        methodNode.instructions.remove(insnNode);
                                        insnNode = nextInsnNode;
                                    }
                                }
                            }

                            insnNode = insnNode.getNext();
                        }
                    });
        }
    }
}
