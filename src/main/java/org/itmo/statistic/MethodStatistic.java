package org.itmo.statistic;

public class MethodStatistic {
    private long assignments;
    private long branches;
    private long conditions;

    public MethodStatistic() {
        assignments = 0;
        branches = 0;
        conditions = 0;
    }

    public void incrementAssignments() {
        ++assignments;
    }

    public void incrementBranches() {
        ++branches;
    }

    public void incrementConditions() {
        ++conditions;
    }

    public void incrementConditions(int value) {
        conditions += value;
    }

    public long getAssignments() {
        return assignments;
    }

    public long getBranches() {
        return branches;
    }

    public long getConditions() {
        return conditions;
    }
}
