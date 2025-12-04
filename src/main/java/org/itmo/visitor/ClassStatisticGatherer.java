package org.itmo.visitor;

import org.itmo.statistic.AggregatedStatistic;
import org.itmo.statistic.ClassStatistic;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ClassStatisticGatherer extends ClassVisitor {
    private final AggregatedStatistic aggregatedStatistic;
    private final ClassStatistic statistic;

    public ClassStatisticGatherer(AggregatedStatistic aggregatedStatistic) {
        super(Opcodes.ASM9);
        this.aggregatedStatistic = aggregatedStatistic;
        statistic = new ClassStatistic();
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        statistic.setClassName(name);
        statistic.setSuperClassName(superName);
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        statistic.incrementFields();
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (name.equals("<init>") || name.equals("<clinit>")) {
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }
        statistic.addMethod(signature);
        return new MethodStatisticGatherer(aggregatedStatistic);
    }

    @Override
    public void visitEnd() {
        aggregatedStatistic.merge(statistic);
    }
}
