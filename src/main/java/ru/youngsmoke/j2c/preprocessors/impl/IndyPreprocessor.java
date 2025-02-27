package ru.youngsmoke.j2c.preprocessors.impl;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import ru.youngsmoke.j2c.NativeProcessor;
import ru.youngsmoke.j2c.preprocessors.utils.MethodHandleUtils;
import ru.youngsmoke.j2c.preprocessors.utils.PreprocessorUtils;
import ru.youngsmoke.j2c.utils.Util;

import java.util.Arrays;

public class IndyPreprocessor implements Preprocessor {

    private static void processIndy(ClassNode classNode, MethodNode methodNode,
                                    InvokeDynamicInsnNode invokeDynamicInsnNode) {
        try {
            LabelNode bootstrapStart = new LabelNode(new Label());
            LabelNode bootstrapEnd = new LabelNode(new Label());
            LabelNode invokeStart = new LabelNode(new Label());

            InsnList bootstrapInstructions = new InsnList();
            bootstrapInstructions.add(bootstrapStart); // 0

            Type[] bsmArguments = Type.getArgumentTypes(invokeDynamicInsnNode.bsm.getDesc());
            int targetArgLength = bsmArguments.length - 3;
            int originArgLength = invokeDynamicInsnNode.bsmArgs.length;

            // process variable arguments for bsm like StringConcatFactory.makeConcatWithConstants(Lookup, String, MethodType, String, Object...)
            // jvm will process variable argument automatically when using linkCallSite
            // but if we want to use invokeWithArguments, we need to process variable argument manually
            if (originArgLength < targetArgLength) {
                Object[] newArgs = new Object[targetArgLength];
                System.arraycopy(invokeDynamicInsnNode.bsmArgs, 0, newArgs, 0, originArgLength);

                if (targetArgLength - originArgLength != 1) {
                    throw new RuntimeException("Impossible BSM arguments length");
                }

                if (bsmArguments[originArgLength + 3].getSort() == Type.ARRAY) {
                    newArgs[originArgLength] = new Object[0];
                } else {
                    throw new RuntimeException("Last argument of BSM is not a vararg array");
                }

                invokeDynamicInsnNode.bsmArgs = newArgs;
            } else if (originArgLength > targetArgLength || (bsmArguments[bsmArguments.length - 1].getSort() == Type.ARRAY && Type.getType(invokeDynamicInsnNode.bsmArgs[invokeDynamicInsnNode.bsmArgs.length - 1].getClass()).getSort() != Type.ARRAY)) {
                Object[] newArgs = new Object[targetArgLength];
                System.arraycopy(invokeDynamicInsnNode.bsmArgs, 0, newArgs, 0, targetArgLength - 1);

                Object[] varArgs = new Object[originArgLength - targetArgLength + 1];
                System.arraycopy(invokeDynamicInsnNode.bsmArgs, targetArgLength - 1, varArgs, 0, originArgLength - targetArgLength + 1);

                newArgs[targetArgLength - 1] = varArgs;
                invokeDynamicInsnNode.bsmArgs = newArgs;
            }


            if (bsmArguments.length < 3 || !bsmArguments[0].getDescriptor().equals("Ljava/lang/invoke/MethodHandles$Lookup;") ||
                    !bsmArguments[1].getDescriptor().equals("Ljava/lang/String;") ||
                    !bsmArguments[2].getDescriptor().equals("Ljava/lang/invoke/MethodType;")) {
                InsnList resultInstructions = new InsnList();
                resultInstructions.add(new TypeInsnNode(Opcodes.NEW, "java/lang/BootstrapMethodError")); // 1
                resultInstructions.add(new InsnNode(Opcodes.DUP)); // 2
                resultInstructions.add(new LdcInsnNode("Wrong 3 first arguments in bsm")); // 3
                resultInstructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/BootstrapMethodError",
                        "<init>", "(Ljava/lang/String;)V")); // 1
                resultInstructions.add(new InsnNode(Opcodes.ATHROW)); // 0
                methodNode.instructions.insert(invokeDynamicInsnNode, resultInstructions);
                methodNode.instructions.remove(invokeDynamicInsnNode);
                return;
            }

            Type[] arguments = Type.getArgumentTypes(invokeDynamicInsnNode.desc);

            bootstrapInstructions.add(new LdcInsnNode(arguments.length)); // 1
            bootstrapInstructions.add(new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/Object")); // 1
            {
                int index = arguments.length;
                for (Type argument : Util.reverse(Arrays.stream(arguments)).toList()) {
                    index--;
                    if (argument.getSize() == 1) {
                        if (argument.getSort() != Type.ARRAY && argument.getSort() != Type.OBJECT) {
                            bootstrapInstructions.add(new InsnNode(Opcodes.SWAP)); // 2
                            bootstrapInstructions.add(getBoxingInsnNode(argument)); // 2
                            bootstrapInstructions.add(new InsnNode(Opcodes.SWAP)); // 2
                        }
                    } else if (argument.getSize() == 2) {
                        bootstrapInstructions.add(new InsnNode(Opcodes.DUP_X2)); // 3
                        bootstrapInstructions.add(new InsnNode(Opcodes.POP)); // 2
                        bootstrapInstructions.add(getBoxingInsnNode(argument)); // 2
                        bootstrapInstructions.add(new InsnNode(Opcodes.SWAP)); // 2
                    }
                    bootstrapInstructions.add(new InsnNode(Opcodes.DUP)); // 3
                    bootstrapInstructions.add(new InsnNode(Opcodes.DUP2_X1)); // 5
                    bootstrapInstructions.add(new InsnNode(Opcodes.POP2)); // 3
                    bootstrapInstructions.add(new LdcInsnNode(index)); // 4
                    bootstrapInstructions.add(new InsnNode(Opcodes.SWAP)); // 4
                    bootstrapInstructions.add(new InsnNode(Opcodes.AASTORE)); // 1
                }
            }

            bootstrapInstructions.add(PreprocessorUtils.LOOKUP_LOCAL.get()); // 2

            bootstrapInstructions.add(new LdcInsnNode(invokeDynamicInsnNode.name)); // 3
            bootstrapInstructions.add(MethodHandleUtils.generateMethodTypeLdcInsn(Type.getMethodType(invokeDynamicInsnNode.desc), classNode));

            for (Object bsmArgument : invokeDynamicInsnNode.bsmArgs) {
                if (bsmArgument instanceof String) {
                    bootstrapInstructions.add(new LdcInsnNode(bsmArgument)); // 5
                } else if (bsmArgument instanceof Type) {
                    if (((Type) bsmArgument).getSort() == Type.METHOD) {
                        bootstrapInstructions.add(MethodHandleUtils.generateMethodTypeLdcInsn((Type) bsmArgument, classNode));
                    } else {
                        bootstrapInstructions.add(new LdcInsnNode(bsmArgument)); // 5
                    }
                } else if (bsmArgument instanceof Integer) {
                    bootstrapInstructions.add(new LdcInsnNode(bsmArgument)); // 5
                } else if (bsmArgument instanceof Long) {
                    bootstrapInstructions.add(new LdcInsnNode(bsmArgument)); // 6
                } else if (bsmArgument instanceof Float) {
                    bootstrapInstructions.add(new LdcInsnNode(bsmArgument)); // 5
                } else if (bsmArgument instanceof Double) {
                    bootstrapInstructions.add(new LdcInsnNode(bsmArgument)); // 6
                } else if (bsmArgument instanceof Handle) {
                    bootstrapInstructions.add(MethodHandleUtils.generateMethodHandleLdcInsn((Handle) bsmArgument));
                } else if (bsmArgument instanceof Object[]) {
                    Object[] objects = (Object[]) bsmArgument;
                    bootstrapInstructions.add(new LdcInsnNode(objects.length));
                    bootstrapInstructions.add(new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/Object"));

                    int index = 0;
                    for (Object object : objects) {
                        bootstrapInstructions.add(new InsnNode(Opcodes.DUP));
                        bootstrapInstructions.add(new LdcInsnNode(index));
                        if (object instanceof String) {
                            bootstrapInstructions.add(new LdcInsnNode(object));
                        } else if (object instanceof Type) {
                            if (((Type) object).getSort() == Type.METHOD) {
                                bootstrapInstructions.add(MethodHandleUtils.generateMethodTypeLdcInsn((Type) object, classNode));
                            } else {
                                bootstrapInstructions.add(new LdcInsnNode(object));
                            }
                        } else if (object instanceof Integer) {
                            bootstrapInstructions.add(new LdcInsnNode(object));
                            bootstrapInstructions.add(getBoxingInsnNode(Type.INT_TYPE));
                        } else if (object instanceof Long) {
                            bootstrapInstructions.add(new LdcInsnNode(object));
                            bootstrapInstructions.add(getBoxingInsnNode(Type.LONG_TYPE));
                        } else if (object instanceof Float) {
                            bootstrapInstructions.add(new LdcInsnNode(object));
                            bootstrapInstructions.add(getBoxingInsnNode(Type.FLOAT_TYPE));
                        } else if (object instanceof Double) {
                            bootstrapInstructions.add(new LdcInsnNode(object));
                            bootstrapInstructions.add(getBoxingInsnNode(Type.DOUBLE_TYPE));
                        } else if (object instanceof Handle) {
                            bootstrapInstructions.add(MethodHandleUtils.generateMethodHandleLdcInsn((Handle) object));
                        } else {
                            throw new RuntimeException("Wrong argument type: " + object.getClass());
                        }
                        bootstrapInstructions.add(new InsnNode(Opcodes.AASTORE));
                        index++;
                    }

                } else {
                    throw new RuntimeException("Wrong argument type: " + bsmArgument.getClass());
                }
            }
            bootstrapInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, invokeDynamicInsnNode.bsm.getOwner(),
                    invokeDynamicInsnNode.bsm.getName(), invokeDynamicInsnNode.bsm.getDesc())); // 2
            //bootstrapInstructions.add(new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/invoke/CallSite")); // 2
            bootstrapInstructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/invoke/CallSite",
                    "getTarget", "()Ljava/lang/invoke/MethodHandle;")); // 2
            bootstrapInstructions.add(new JumpInsnNode(Opcodes.GOTO, invokeStart)); // 2


            bootstrapInstructions.add(bootstrapEnd);

            InsnList invokeInstructions = new InsnList();
            invokeInstructions.add(invokeStart);

            invokeInstructions.add(new InsnNode(Opcodes.SWAP)); // 2
            invokeInstructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/invoke/MethodHandle",
                    "invokeWithArguments", "([Ljava/lang/Object;)Ljava/lang/Object;")); // 1
            Type returnType = Type.getReturnType(invokeDynamicInsnNode.desc);
            if (returnType.getSort() == Type.OBJECT) {
                invokeInstructions.add(new TypeInsnNode(Opcodes.CHECKCAST, returnType.getInternalName())); // 1
            } else if (returnType.getSort() == Type.ARRAY) {
                invokeInstructions.add(new TypeInsnNode(Opcodes.CHECKCAST, returnType.getDescriptor())); // 1
            } else {
                invokeInstructions.add(getUnboxingTypeInsn(returnType));
            }


            InsnList resultInstructions = new InsnList();
            resultInstructions.add(bootstrapInstructions);
            resultInstructions.add(invokeInstructions);

            methodNode.instructions.insert(invokeDynamicInsnNode, resultInstructions);
            methodNode.instructions.remove(invokeDynamicInsnNode);
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }


    private static AbstractInsnNode getBoxingInsnNode(Type argument) {
        return switch (argument.getSort()) {
            case Type.BOOLEAN ->
                    new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
            case Type.BYTE ->
                    new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
            case Type.CHAR ->
                    new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
            case Type.DOUBLE ->
                    new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
            case Type.FLOAT ->
                    new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
            case Type.INT ->
                    new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
            case Type.LONG ->
                    new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
            case Type.SHORT ->
                    new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
            default -> throw new RuntimeException(String.format("Failed to box %s", argument));
        };
    }

    private static InsnList getUnboxingTypeInsn(Type argument) {
        InsnList result = new InsnList();
        switch (argument.getSort()) {
            case Type.BOOLEAN:
                result.add(new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/Boolean"));
                result.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z"));
                break;
            case Type.BYTE:
                result.add(new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/Byte"));
                result.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B"));
                break;
            case Type.CHAR:
                result.add(new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/Character"));
                result.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C"));
                break;
            case Type.DOUBLE:
                result.add(new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/Double"));
                result.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D"));
                break;
            case Type.FLOAT:
                result.add(new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/Float"));
                result.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F"));
                break;
            case Type.INT:
                result.add(new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/Integer"));
                result.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I"));
                break;
            case Type.LONG:
                result.add(new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/Long"));
                result.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J"));
                break;
            case Type.SHORT:
                result.add(new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/Short"));
                result.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S"));
                break;
            case Type.VOID:
                result.add(new InsnNode(Opcodes.POP));
                break;
            default:
                throw new RuntimeException(String.format("Failed to unbox %s", argument));
        }
        return result;
    }

    @Override
    public void process(ClassNode classNode) {
        if (!Util.hasFlag(classNode.access, Opcodes.ACC_INTERFACE)) {
            classNode.methods.stream()
                    .filter(NativeProcessor::shouldProcess)
                    .filter(methodNode -> NativeProcessor.classMethodFilter.shouldProcess(classNode, methodNode))
                    .forEach(methodNode -> {
                        Arrays.stream(methodNode.instructions.toArray())
                                .filter(abstractInsnNode -> abstractInsnNode instanceof InvokeDynamicInsnNode)
                                .forEach(abstractInsnNode -> processIndy(classNode, methodNode, (InvokeDynamicInsnNode) abstractInsnNode));
                    });
        }
    }
}
