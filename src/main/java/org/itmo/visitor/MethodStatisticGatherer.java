package org.itmo.visitor;

import java.util.Set;

import org.itmo.statistic.AggregatedStatistic;
import org.itmo.statistic.MethodStatistic;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * <a href="https://en.wikipedia.org/wiki/ABC_Software_Metric#ABC_rules_for_Java">ABC rules for java</a>
 */
public class MethodStatisticGatherer extends MethodVisitor {
    private static final Set<Integer> STORE_OPCODES = Set.of(
            Opcodes.ISTORE,
            Opcodes.LSTORE,
            Opcodes.FSTORE,
            Opcodes.DSTORE,
            Opcodes.ASTORE
    );

    private static final Set<Integer> NEW_OPCODES = Set.of(
            Opcodes.NEW,
            Opcodes.ANEWARRAY
    );

    private static final Set<Integer> CONDITION_OPCODES = Set.of(
            Opcodes.IFEQ,
            Opcodes.IFNE,
            Opcodes.IFLT,
            Opcodes.IFGE,
            Opcodes.IFGT,
            Opcodes.IFLE,
            Opcodes.IF_ICMPEQ,
            Opcodes.IF_ICMPNE,
            Opcodes.IF_ICMPLT,
            Opcodes.IF_ICMPGE,
            Opcodes.IF_ICMPGT,
            Opcodes.IF_ICMPLE,
            Opcodes.IF_ACMPEQ,
            Opcodes.IF_ACMPNE,
            Opcodes.IFNULL,
            Opcodes.IFNONNULL
    );

    private final AggregatedStatistic aggregatedStatistic;
    private final MethodStatistic statistic;

    public MethodStatisticGatherer(AggregatedStatistic aggregatedStatistic) {
        super(Opcodes.ASM9);
        this.aggregatedStatistic = aggregatedStatistic;
        statistic = new MethodStatistic();
    }

    @Override
    public void visitVarInsn(int opcode, int varIndex) {
        if (STORE_OPCODES.contains(opcode)) {
            statistic.incrementAssignments();
        }
        super.visitVarInsn(opcode, varIndex);
    }

    @Override
    public void visitIincInsn(int varIndex, int increment) {
        statistic.incrementAssignments();
        super.visitIincInsn(varIndex, increment);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if (!name.equals("<init>") && !name.equals("<clinit>")) {
            statistic.incrementBranches();
        }
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
        statistic.incrementBranches();
        super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        if (NEW_OPCODES.contains(opcode)) {
            statistic.incrementBranches();
        }
        super.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
        statistic.incrementBranches();
        super.visitMultiANewArrayInsn(descriptor, numDimensions);
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        statistic.incrementConditions(labels.length);
        super.visitLookupSwitchInsn(dflt, keys, labels);
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        statistic.incrementConditions(labels.length);
        super.visitTableSwitchInsn(min, max, dflt, labels);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        if (CONDITION_OPCODES.contains(opcode)) {
            statistic.incrementConditions();
        }
        super.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        statistic.incrementConditions();
        super.visitTryCatchBlock(start, end, handler, type);
    }

    @Override
    public void visitEnd() {
        aggregatedStatistic.merge(statistic);
        super.visitEnd();
    }
}
