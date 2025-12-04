package org.itmo.statistic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AggregatedStatistic {
    private final Map<String, String> classToSuper;
    private final Map<String, Set<String>> classToItsMethodSignatures;

    private long assignments;
    private long branches;
    private long conditions;

    private long fields;

    private int maxInheritanceDepth;
    private double avgInheritanceDepth;
    private double abcMetric;
    private double avgOverriddenMethods;
    private double avgFields;

    public AggregatedStatistic() {
        classToSuper = new HashMap<>();
        classToItsMethodSignatures = new HashMap<>();

        assignments = 0;
        branches = 0;
        conditions = 0;
    }

    public void merge(MethodStatistic statistic) {
        assignments += statistic.getAssignments();
        branches += statistic.getBranches();
        conditions += statistic.getConditions();
    }

    public void merge(ClassStatistic statistic) {
        classToItsMethodSignatures.put(statistic.getClassName(), new HashSet<>(statistic.getMethodSignatures()));
        fields += statistic.getFields();
        classToSuper.put(statistic.getClassName(), statistic.getSuperClassName());
    }

    public void makeStatistic() {
        int[] overridden = {0};
        int[] maxInheritanceDepth = {0};
        List<Integer> inheritanceDepthList = new LinkedList<>();

        classToSuper.keySet().forEach((current) -> {
            Set<String> curMethods = classToItsMethodSignatures.get(current);
            Set<String> overriddenMethods = new HashSet<>();

            int inheritanceDepth = 0;

            String parent = classToSuper.get(current);
            Set<String> parentMethods;
            while (parent != null) {
                ++inheritanceDepth;
                parentMethods = classToItsMethodSignatures.get(parent);
                if (parentMethods != null) {
                    overriddenMethods.addAll(parentMethods);
                }
                parent = classToSuper.get(parent);
            }

            inheritanceDepthList.add(inheritanceDepth);
            maxInheritanceDepth[0] = Math.max(maxInheritanceDepth[0], inheritanceDepth);

            overriddenMethods.retainAll(curMethods);
            overridden[0] += overriddenMethods.size();
        });

        this.maxInheritanceDepth = maxInheritanceDepth[0];
        int classes = classToSuper.size();

        this.abcMetric = Math.sqrt(this.assignments * this.assignments + this.branches * this.branches + this.conditions * this.conditions);
        this.avgOverriddenMethods = (double) overridden[0] / classes;
        this.avgFields = (double) this.fields / classes;
        this.avgInheritanceDepth = inheritanceDepthList.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
    }

    public int getMaxInheritanceDepth() {
        return maxInheritanceDepth;
    }

    public double getAvgInheritanceDepth() {
        return avgInheritanceDepth;
    }

    public double getAbcMetric() {
        return abcMetric;
    }

    public double getAvgOverriddenMethods() {
        return avgOverriddenMethods;
    }

    public double getAvgFields() {
        return avgFields;
    }
}
