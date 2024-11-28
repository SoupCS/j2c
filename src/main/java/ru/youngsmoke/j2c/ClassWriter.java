package ru.youngsmoke.j2c;

import org.objectweb.asm.Label;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

public class ClassWriter {
    public final StringBuilder output;
    private final LabelPool labelPool = new LabelPool();
    private final StackPointer stackPointer = new StackPointer();
    public List<Integer> locals = new ArrayList<Integer>();

    public ClassWriter() {
        this.output = new StringBuilder();
    }

    public void writeln(String str) {
        output.append("\n").append(str);
    }

    public LabelPool getLabelPool() {
        return labelPool;
    }

    public StackPointer getStackPointer() {
        return stackPointer;
    }

    public static class StackPointer {
        private int pointer;

        public StackPointer() {
            this.pointer = 0;
        }

        public StackPointer push(int... count) {
            pointer += (count.length > 0) ? count[0] : 1;
            return this;
        }

        public StackPointer pop(int... count) {
            pointer -= (count.length > 0) ? count[0] : 1;
            return this;
        }

        public int peek() {
            return this.pointer;
        }

        public void set(int pointer) {
            this.pointer = pointer;
        }

    }

    public static class LabelPool {

        private final WeakHashMap<Label, Long> labels = new WeakHashMap<>();
        private long currentIndex = 0;

        public String getName(Label label) {
            return "L" + this.labels.computeIfAbsent(label, addedLabel -> ++currentIndex);
        }
    }
}