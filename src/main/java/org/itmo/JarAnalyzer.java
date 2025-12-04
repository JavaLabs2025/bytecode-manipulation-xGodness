package org.itmo;

import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.itmo.statistic.AggregatedStatistic;
import org.itmo.visitor.ClassStatisticGatherer;
import org.objectweb.asm.ClassReader;

public class JarAnalyzer {

    public static AggregatedStatistic analyze(String path) throws IOException {
        AggregatedStatistic aggregatedStatistic = new AggregatedStatistic();

        try (JarFile jar = new JarFile(path)) {
            Enumeration<JarEntry> enumeration = jar.entries();

            while (enumeration.hasMoreElements()) {
                JarEntry entry = enumeration.nextElement();
                if (entry.getName().endsWith(".class")) {
                    ClassReader reader = new ClassReader(jar.getInputStream(entry));
                    ClassStatisticGatherer gatherer = new ClassStatisticGatherer(aggregatedStatistic);
                    reader.accept(gatherer, 0);
                }
            }
        }

        return aggregatedStatistic;
    }
}
