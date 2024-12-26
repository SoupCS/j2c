package ru.youngsmoke.j2c.compiler;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import ru.youngsmoke.j2c.ClassWriter;
import ru.youngsmoke.j2c.utils.Reflection;
import ru.youngsmoke.j2c.utils.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MethodCompiler {

    public static List<Compiler> compilers = new ArrayList<>();
    public static String[] CPP_TYPES = new String[]{"void", "jboolean", "jchar", "jbyte", "jshort", "jint", "jfloat", "jlong", "jdouble", "jarray", "jobject", "jobject"};
    public static String[] ARG_TYPES = new String[]{"i", "i", "i", "i", "i", "f", "j", "d", "l", "l", "l"};
    public static int[] TYPE_TO_STACK = new int[]{1, 1, 1, 1, 1, 1, 1, 2, 2, 0, 0, 0};
    public static int[] STACK_TO_STACK = new int[]{1, 1, 1, 2, 2, 0, 0, 0, 0};

    public MethodCompiler() {
        /*
            compilers.add(new NewInsnCompiler());
            compilers.add(new LdcInsnCompiler());
            compilers.add(new NopInsnCompiler());
            compilers.add(new SwapInsnCompiler());
            compilers.add(new IfInsnCompiler());
            compilers.add(new InvokeInsnCompiler());
            compilers.add(new CheckcastInsnCompiler());
            compilers.add(new ReturnInsnCompiler());
            compilers.add(new FrameInsnCompiler());
            compilers.add(new IfNullInsnCompiler());
            compilers.add(new MutexInsnCompiler());
            compilers.add(new LoadStoreInsnCompiler());
            compilers.add(new DupInsnCompiler());
            compilers.add(new FieldInsnCompiler());
            compilers.add(new GotoInsnCompiler());
            compilers.add(new ArrayLoadStoreInsnCompiler());
            compilers.add(new ConstInsnCompiler());
            compilers.add(new PopInsnCompiler());
            compilers.add(new PushInsnCompiler());
        */
        Reflection.getClasses("ru.youngsmoke.j2c.compiler.impl", Compiler.class).forEach(compiler -> System.out.printf("compilers.add(new %s());%n", compiler.name));
        compilers.addAll(Reflection.getClasses("ru.youngsmoke.j2c.compiler.impl", Compiler.class));
    }

    public void process(MethodNode method, ClassNode clazz, ClassWriter writer) {
        if (method.name.equals("<init>")) return;

        Type[] args = Type.getArgumentTypes(method.desc);

        boolean isStatic = Util.getFlag(method.access, Opcodes.ACC_STATIC);

        ArrayList<Type> argTypes = new ArrayList<>(Arrays.asList(args));
        if (!isStatic) {
            argTypes.addFirst(Type.getType(Object.class));
        }
        String methodName = Util.getJNICompatibleName(method.name);
        writer.output.append("%s JNICALL %s(JNIEnv *env, ".formatted(CPP_TYPES[Type.getReturnType(method.desc).getSort()], clazz.name.replace("/", "_") + "_" + methodName));

        writer.output.append(Util.getFlag(method.access, Opcodes.ACC_STATIC) ? "jclass clazz" : "jobject obj");

        ArrayList<String> argNames = new ArrayList<>();
        if (!isStatic) argNames.add("obj");
        for (int i = 0; i < args.length; ++i) {
            argNames.add("arg" + i);
            writer.output.append(", %s arg%d".formatted(CPP_TYPES[args[i].getSort()], i));
        }

        writer.output.append(" ) {");

        for (int i = 0; i < method.maxLocals; ++i) {
            writer.writeln("    jvalue clocal%d;".formatted(i));
        }

        for (int i = 0; i < method.maxStack; ++i) {
            writer.writeln("    jvalue cstack%d;".formatted(i));
        }

        int localIndex = 0;
        for (int i = 0; i < argTypes.size(); ++i) {
            Type current = argTypes.get(i);
            writer.output.append("\n    ").append(
                            "clocal%s.%s = ".formatted(localIndex, ARG_TYPES[1]))
                    .append(i <= 4 ? "(jint) " : "")
                    .append(argNames.get(i))
                    .append(";\n");
            localIndex += current.getSize();
        }

        argTypes.forEach(t -> writer.locals.add(TYPE_TO_STACK[t.getSort()]));

        Arrays.stream(method.instructions.toArray()).forEach(abstractInsnNode -> {
            //System.out.println("stack = " + writer.stackPointer);

            if (abstractInsnNode instanceof LabelNode) {
                // writer.getLabelPool().getName(((LabelNode) abstractInsnNode).getLabel());
                // System.out.println(writer.getLabelPool().getName(((LabelNode) abstractInsnNode).getLabel()));
                writer.writeln("%s: ".formatted(writer.getLabelPool().getName(((LabelNode) abstractInsnNode).getLabel())));
                return;
            }
            if (abstractInsnNode.getOpcode() != -1) {
                writer.writeln("    { //%s".formatted(Util.getOpcodeString(abstractInsnNode.getOpcode()).equals("UNKNOWN") ? String.valueOf(abstractInsnNode.getOpcode()) : Util.getOpcodeString(abstractInsnNode.getOpcode())));
            }
            compilers.stream()
                    .filter(compiler -> compiler.supports(abstractInsnNode.getOpcode()))
                    .findFirst()
                    .ifPresentOrElse(
                            compiler -> {
                                compiler.compile(writer, abstractInsnNode, method);
                                System.out.printf("Supported insn %s [%s] owned by %s%n",
                                        Util.getOpcodeString(abstractInsnNode.getOpcode()),
                                        abstractInsnNode.getOpcode(),
                                        compiler.getClass()
                                );
                            },
                            () -> System.out.printf("Unsupported insn %s [%s]%n",
                                    Util.getOpcodeString(abstractInsnNode.getOpcode()),
                                    abstractInsnNode.getOpcode()
                            )
                    );

            /*
            switch (node.getOpcode()) {
            case Opcodes.ARRAYLENGTH:
            case Opcodes.DALOAD:
            case Opcodes.LALOAD:
                return currentStackPointer;
            case Opcodes.IALOAD:
            case Opcodes.FCMPG:
            case Opcodes.FCMPL:
            case Opcodes.IREM:
            case Opcodes.IDIV:
            case Opcodes.SALOAD:
            case Opcodes.CALOAD:
            case Opcodes.BALOAD:
            case Opcodes.AALOAD:
            case Opcodes.FALOAD:
            case Opcodes.ATHROW:
                return currentStackPointer - 1;
            case Opcodes.IASTORE:
            case Opcodes.DCMPG:
            case Opcodes.DCMPL:
            case Opcodes.SASTORE:
            case Opcodes.CASTORE:
            case Opcodes.BASTORE:
            case Opcodes.AASTORE:
            case Opcodes.FASTORE:
                return currentStackPointer - 3;
            case Opcodes.LASTORE:
            case Opcodes.DASTORE:
                return currentStackPointer - 4;
            case Opcodes.LREM:
            case Opcodes.LDIV:
                return currentStackPointer - 2;
        }
            * */

            if (abstractInsnNode.getOpcode() != -1) {
                writer.writeln("    } // stack %d".formatted(writer.getStackPointer().peek()));
            }
        });
        if (Type.getReturnType(method.desc).getSort() != 0)
            writer.writeln(String.format("    return (%s) 0;\n", CPP_TYPES[Type.getReturnType(method.desc).getSort()]));
        writer.writeln("}\n");
        writer.getStackPointer().set(0);

        method.instructions.clear();
        method.access = method.access | Opcodes.ACC_NATIVE;
    }

}
