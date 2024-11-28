package ru.youngsmoke.j2c.compiler.impl;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;
import ru.youngsmoke.j2c.ClassWriter;
import ru.youngsmoke.j2c.compiler.Compiler;
import ru.youngsmoke.j2c.compiler.MethodCompiler;

public class FieldInsnCompiler extends Compiler {
    public static String[] TYPES = new String[]{"Void", "Boolean", "Char", "Byte", "Short", "Int", "Float", "Long", "Double", "Array", "Object", "Object"};

    public FieldInsnCompiler() {
        super("FieldInsnCompiler", GETFIELD, PUTFIELD, GETSTATIC, PUTSTATIC, NOP);
    }

    @Override
    public void compile(ClassWriter classWriter, AbstractInsnNode insnNode, MethodNode method) {
        if (insnNode instanceof FieldInsnNode) {
            boolean isStatic = insnNode.getOpcode() == GETSTATIC || insnNode.getOpcode() == PUTSTATIC;

            String class_ptr = "env->FindClass(\"%s\")".formatted(((FieldInsnNode) insnNode).owner);

            classWriter.writeln("       jfieldID fieldid = env->Get%sFieldID(%s, \"%s\", \"%s\");".formatted(
                    isStatic ? "Static" : "",
                    class_ptr,
                    ((FieldInsnNode) insnNode).name,
                    ((FieldInsnNode) insnNode).desc));

            switch (insnNode.getOpcode()) {
                case GETSTATIC -> {
                    switch (Type.getType(((FieldInsnNode) insnNode).desc).getSort()) {
                        case 1, 2, 4, 5 -> {
                            classWriter.writeln("       cstack%s.i = %s env->GetStatic%sField(%s, fieldid);".formatted(
                                    classWriter.getStackPointer().peek(),
                                    Type.getType(((FieldInsnNode) insnNode).desc).getSort() == 5 ? "(jint)" : "",
                                    TYPES[Type.getType(((FieldInsnNode) insnNode).desc).getSort()],
                                    class_ptr
                            ));
                            classWriter.writeln("// " + Type.getType(((FieldInsnNode) insnNode).desc).getSort());
                        }
                        case 6 ->
                                classWriter.writeln("       cstack%s.f = env->GetStatic%sField(%s, fieldid);".formatted(
                                        classWriter.getStackPointer().peek(),
                                        TYPES[Type.getType(((FieldInsnNode) insnNode).desc).getSort()],
                                        class_ptr
                                ));
                        case 7 ->
                                classWriter.writeln("       cstack%s.j = env->GetStatic%sField(%s, fieldid);".formatted(
                                        class_ptr,
                                        classWriter.getStackPointer().peek(),
                                        TYPES[Type.getType(((FieldInsnNode) insnNode).desc).getSort()]
                                ));
                        case 8 ->
                                classWriter.writeln("       cstack%s.d = env->GetStatic%sField(%s, fieldid);".formatted(
                                        classWriter.getStackPointer().peek(),
                                        TYPES[Type.getType(((FieldInsnNode) insnNode).desc).getSort()],
                                        class_ptr
                                ));
                        case 9, 10, 11 ->
                                classWriter.writeln("       cstack%s.l = env->GetStatic%sField(%s, fieldid);".formatted(
                                        classWriter.getStackPointer().peek(),
                                        TYPES[Type.getType(((FieldInsnNode) insnNode).desc).getSort()],
                                        class_ptr
                                ));
                    }

                }
                case GETFIELD -> {
                    switch (Type.getType(((FieldInsnNode) insnNode).desc).getSort()) {
                        case 1, 2, 4, 5 -> {
                            classWriter.writeln("       cstack%s.i = %s env->Get%sField(cstack%s.l, fieldid);".formatted(
                                    classWriter.getStackPointer().peek() - 1,
                                    Type.getType(((FieldInsnNode) insnNode).desc).getSort() == 5 ? "(jint)" : "",
                                    TYPES[Type.getType(((FieldInsnNode) insnNode).desc).getSort()],
                                    classWriter.getStackPointer().peek() - 1
                            ));
                            classWriter.writeln("// " + Type.getType(((FieldInsnNode) insnNode).desc).getSort());

                        }
                        case 6 ->
                                classWriter.writeln("       cstack%s.f = env->Get%sField(cstack%s.l, fieldid);".formatted(
                                        classWriter.getStackPointer().peek() - 1,
                                        TYPES[Type.getType(((FieldInsnNode) insnNode).desc).getSort()],
                                        classWriter.getStackPointer().peek() - 1
                                ));
                        case 7 ->
                                classWriter.writeln("       cstack%s.j = env->Get%sField(cstack%s.l, fieldid);".formatted(
                                        classWriter.getStackPointer().peek() - 1,
                                        TYPES[Type.getType(((FieldInsnNode) insnNode).desc).getSort()],
                                        classWriter.getStackPointer().peek() - 1
                                ));
                        case 8 ->
                                classWriter.writeln("       cstack%s.d = env->Get%sField(cstack%s.l, fieldid);".formatted(
                                        classWriter.getStackPointer().peek() - 1,
                                        TYPES[Type.getType(((FieldInsnNode) insnNode).desc).getSort()],
                                        classWriter.getStackPointer().peek() - 1
                                ));
                        case 9, 10, 11 ->
                                classWriter.writeln("       cstack%s.l = env->Get%sField(cstack%s.l, fieldid);".formatted(
                                        classWriter.getStackPointer().peek() - 1,
                                        TYPES[Type.getType(((FieldInsnNode) insnNode).desc).getSort()],
                                        classWriter.getStackPointer().peek() - 1
                                ));
                    }

                }
                case PUTSTATIC -> {
                    switch (Type.getType(((FieldInsnNode) insnNode).desc).getSort()) {
                        case 1, 2, 4, 5 -> {
                            classWriter.writeln("       env->SetStatic%sField(%s, fieldid, %s cstack%s.i);".formatted(
                                    TYPES[Type.getType(((FieldInsnNode) insnNode).desc).getSort()],
                                    class_ptr,
                                    Type.getType(((FieldInsnNode) insnNode).desc).getSort() == 5 ? "(%s)".formatted(MethodCompiler.CPP_TYPES[Type.getType(((FieldInsnNode) insnNode).desc).getSort()]) : "",
                                    classWriter.getStackPointer().peek() - 1
                            ));
                            classWriter.writeln("// " + Type.getType(((FieldInsnNode) insnNode).desc).getSort());

                        }
                        case 6 ->
                                classWriter.writeln("       env->SetStatic%sField(%s, fieldid, cstack%s.f);".formatted(
                                        TYPES[Type.getType(((FieldInsnNode) insnNode).desc).getSort()],
                                        class_ptr,
                                        classWriter.getStackPointer().peek() - 1
                                ));
                        case 7 ->
                                classWriter.writeln("       env->SetStatic%sField(%s, fieldid, cstack%s.j);".formatted(
                                        TYPES[Type.getType(((FieldInsnNode) insnNode).desc).getSort()],
                                        class_ptr,
                                        classWriter.getStackPointer().peek() - 2
                                ));
                        case 8 ->
                                classWriter.writeln("       env->SetStatic%sField(%s, fieldid, cstack%s.d);".formatted(
                                        TYPES[Type.getType(((FieldInsnNode) insnNode).desc).getSort()],
                                        class_ptr,
                                        classWriter.getStackPointer().peek() - 2
                                ));
                        case 9, 10, 11 ->
                                classWriter.writeln("       env->SetStatic%sField(%s, fieldid, cstack%s.l);".formatted(
                                        TYPES[Type.getType(((FieldInsnNode) insnNode).desc).getSort()],
                                        class_ptr,
                                        classWriter.getStackPointer().peek() - 1
                                ));
                    }

                }
                case PUTFIELD -> {
                    switch (Type.getType(((FieldInsnNode) insnNode).desc).getSort()) {
                        case 1, 2, 4, 5 -> {
                            classWriter.writeln("       env->Set%sField(cstack%s.l, fieldid, %s cstack%s.i);".formatted(
                                    TYPES[Type.getType(((FieldInsnNode) insnNode).desc).getSort()],
                                    classWriter.getStackPointer().peek() - 2,
                                    Type.getType(((FieldInsnNode) insnNode).desc).getSort() == 5 ? "(%s)".formatted(MethodCompiler.CPP_TYPES[Type.getType(((FieldInsnNode) insnNode).desc).getSort()]) : "",
                                    classWriter.getStackPointer().peek() - 1
                            ));
                            classWriter.writeln("// " + Type.getType(((FieldInsnNode) insnNode).desc).getSort());

                        }
                        case 6 ->
                                classWriter.writeln("       env->Set%sField(cstack%s.l, fieldid, cstack%s.f);".formatted(
                                        TYPES[Type.getType(((FieldInsnNode) insnNode).desc).getSort()],
                                        classWriter.getStackPointer().peek() - 2,
                                        classWriter.getStackPointer().peek() - 1
                                ));
                        case 7 ->
                                classWriter.writeln("       env->Set%sField(cstack%s.l, fieldid, cstack%s.j);".formatted(
                                        TYPES[Type.getType(((FieldInsnNode) insnNode).desc).getSort()],
                                        classWriter.getStackPointer().peek() - 3,
                                        classWriter.getStackPointer().peek() - 2
                                ));
                        case 8 ->
                                classWriter.writeln("       env->Set%sField(cstack%s.l, fieldid, cstack%s.d);".formatted(
                                        TYPES[Type.getType(((FieldInsnNode) insnNode).desc).getSort()],
                                        classWriter.getStackPointer().peek() - 3,
                                        classWriter.getStackPointer().peek() - 2
                                ));
                        case 9, 10, 11 ->
                                classWriter.writeln("       env->Set%sField(cstack%s.l, fieldid, cstack%s.l);".formatted(
                                        TYPES[Type.getType(((FieldInsnNode) insnNode).desc).getSort()],
                                        classWriter.getStackPointer().peek() - 2,
                                        classWriter.getStackPointer().peek() - 1
                                ));
                    }

                }
            }


            if (insnNode.getOpcode() == GETFIELD || insnNode.getOpcode() == PUTFIELD) {
                classWriter.getStackPointer().pop();
            }
            if (insnNode.getOpcode() == GETSTATIC || insnNode.getOpcode() == GETFIELD) {
                classWriter.getStackPointer().push(Type.getType(((FieldInsnNode) insnNode).desc).getSize());
            }
            if (insnNode.getOpcode() == PUTSTATIC || insnNode.getOpcode() == PUTFIELD) {
                classWriter.getStackPointer().pop(Type.getType(((FieldInsnNode) insnNode).desc).getSize());
            }

            //TODO: field manipulations
            //  classWriter.writeln("       cstack%s.l".formatted(classWriter.stackPointer - 1));

        }
    }
}