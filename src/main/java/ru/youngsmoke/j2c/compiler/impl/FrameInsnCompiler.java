package ru.youngsmoke.j2c.compiler.impl;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import ru.youngsmoke.j2c.ClassWriter;
import ru.youngsmoke.j2c.compiler.Compiler;
import ru.youngsmoke.j2c.compiler.MethodCompiler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public class FrameInsnCompiler extends Compiler {

    public List<Integer> stacks = new ArrayList<>();

    public FrameInsnCompiler() {
        super("FrameInsnCompiler", -1);
    }

    @Override
    public void compile(ClassWriter writer, AbstractInsnNode insn, MethodNode method) {
        if (insn instanceof FrameNode frameNode) {
          /*  Consumer<Object> appendLocal = local -> {
                if (local instanceof String) {
                    locals.add(MethodCompiler.TYPE_TO_STACK[Type.OBJECT]);
                } else if (local instanceof LabelNode) {
                    locals.add(MethodCompiler.TYPE_TO_STACK[Type.OBJECT]);
                } else {
                    locals.add(MethodCompiler.STACK_TO_STACK[(int) local]);
                }
            };
            Consumer<Object> appendStack = stack -> {
                if (stack instanceof String) {
                    stacks.add(MethodCompiler.TYPE_TO_STACK[Type.OBJECT]);
                } else if (stack instanceof LabelNode) {
                    stacks.add(MethodCompiler.TYPE_TO_STACK[Type.OBJECT]);
                } else {
                    stacks.add(MethodCompiler.STACK_TO_STACK[(int) stack]);
                }
            };

            switch (((FrameNode) insn).type) {
                case F_APPEND:
                    ((FrameNode) insn).local.forEach(appendLocal);
                    stacks.clear();
                    break;
                case F_CHOP:
                    ((FrameNode) insn).local.forEach(item -> locals.remove(locals.size() - 1));
                    stacks.clear();
                    break;
                case F_NEW:
                case F_FULL:
                    locals.clear();
                    stacks.clear();
                    ((FrameNode) insn).local.forEach(appendLocal);
                    ((FrameNode) insn).stack.forEach(appendStack);
                    break;
                case F_SAME:
                    stacks.clear();
                    break;
                case F_SAME1:
                    stacks.clear();
                    appendStack.accept(((FrameNode) insn).stack.get(0));
                    break;
            }
            switch (((FrameNode) insn).type) {
                case F_APPEND:
                case F_SAME:
                case F_CHOP:
                    writer.stackPointer = 0;
                    break;
                case F_NEW:
                case F_FULL:
                    writer.stackPointer = (((FrameNode) insn).stack.stream().mapToInt(argument -> Math.max(1, argument instanceof Integer ?
                            MethodCompiler.STACK_TO_STACK[(int) argument] : MethodCompiler.TYPE_TO_STACK[Type.OBJECT])).sum());
                    break;
                case F_SAME1:
                    writer.stackPointer = (((FrameNode) insn).stack.stream().limit(1).mapToInt(argument -> Math.max(1, argument instanceof Integer ?
                            MethodCompiler.STACK_TO_STACK[(int) argument] : MethodCompiler.TYPE_TO_STACK[Type.OBJECT])).sum());
                    break;
            }*/

            Consumer<Object> appendLocal = local -> writer.locals.add(
                    local instanceof String || local instanceof LabelNode
                            ? MethodCompiler.TYPE_TO_STACK[Type.OBJECT]
                            : MethodCompiler.STACK_TO_STACK[(int) local]
            );

            Consumer<Object> appendStack = stack -> stacks.add(
                    stack instanceof String || stack instanceof LabelNode
                            ? MethodCompiler.TYPE_TO_STACK[Type.OBJECT]
                            : MethodCompiler.STACK_TO_STACK[(int) stack]
            );

            int frameType = frameNode.type;

            switch (frameType) {
                case F_APPEND:
                    frameNode.local.forEach(appendLocal);
                    stacks.clear();
                    break;

                case F_CHOP:
                    frameNode.local.forEach(_ -> writer.locals.removeLast());
                    stacks.clear();
                    break;

                case F_NEW:
                case F_FULL:
                    writer.locals.clear();
                    stacks.clear();
                    frameNode.local.forEach(appendLocal);
                    frameNode.stack.forEach(appendStack);
                    break;

                case F_SAME:
                    stacks.clear();
                    break;

                case F_SAME1:
                    stacks.clear();
                    appendStack.accept(frameNode.stack.getFirst());
                    break;
            }

            int newStackPointer = 0;
            if (frameType == F_NEW || frameType == F_FULL) {
                newStackPointer = frameNode.stack.stream()
                        .mapToInt(argument -> Math.max(1, argument instanceof Integer
                                ? MethodCompiler.STACK_TO_STACK[(int) argument]
                                : MethodCompiler.TYPE_TO_STACK[Type.OBJECT])).sum();
            } else if (frameType == F_SAME1) {
                newStackPointer = frameNode.stack.stream().limit(1)
                        .mapToInt(argument -> Math.max(1, argument instanceof Integer
                                ? MethodCompiler.STACK_TO_STACK[(int) argument]
                                : MethodCompiler.TYPE_TO_STACK[Type.OBJECT])).sum();
            }
            writer.getStackPointer().set(newStackPointer);

            //  method.tryCatchBlocks.forEach(tryCatchBlockNode -> System.out.println(writer.getLabelPool().getName(tryCatchBlockNode.end.getLabel())));
            //   System.out.println(((FrameNode) insn).type);
        }
    }
}
