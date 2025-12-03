package org.itmo.statistic;

import java.util.LinkedList;
import java.util.List;

public class ClassStatistic {
    private String className;
    private String superClassName;
    private long fields;
    private final List<String> methodSignatures;

    public ClassStatistic() {
        className = null;
        fields = 0;
        methodSignatures = new LinkedList<>();
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setSuperClassName(String superClassName) {
        this.superClassName = superClassName;
    }

    public void incrementFields() {
        ++fields;
    }

    public void addMethod(String methodSignature) {
        methodSignatures.add(methodSignature);
    }

    public String getClassName() {
        return className;
    }

    public String getSuperClassName() {
        return superClassName;
    }

    public long getFields() {
        return fields;
    }

    public List<String> getMethodSignatures() {
        return methodSignatures;
    }
}
