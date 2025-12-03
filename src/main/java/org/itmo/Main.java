package org.itmo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import org.itmo.statistic.AggregatedStatistic;

public class Main {
    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.US);

        String[] jarNames = {
                "fescar.zip",
                "guava.zip",
                "sample.jar"
        };

        for (String jarName : jarNames) {
            String path = "src/main/resources/" + jarName;
            AggregatedStatistic statistic = JarAnalyzer.analyze(path);
            statistic.makeStatistic();
            saveResults(statistic, jarName);
        }
    }

    private static void saveResults(AggregatedStatistic statistic, String jarName) throws IOException {
        int maxInheritanceDepth = statistic.getMaxInheritanceDepth();
        double avgInheritanceDepth = statistic.getAvgInheritanceDepth();
        double abcMetric = statistic.getAbcMetric();
        double avgOverriddenMethods = statistic.getAvgOverriddenMethods();
        double avgFields = statistic.getAvgFields();

        System.out.println(
                "Jar: " + jarName + "\n"
                        + "Max class inheritance depth: %d\n".formatted(maxInheritanceDepth)
                        + "Avg class inheritance depth: %.4f\n".formatted(avgInheritanceDepth)
                        + "ABC metric: %.4f\n".formatted(abcMetric)
                        + "Avg overridden methods per class: %.4f\n".formatted(avgOverriddenMethods)
                        + "Avg fields per class: %.4f\n".formatted(avgFields)
        );

        String json = """
                {
                    "max_inheritance_depth": %d,
                    "avg_inheritance_depth": %.4f,
                    "abc_metric": %.4f,
                    "avg_overridden_methods": %.4f,
                    "avg_fields": %.4f
                }
                """.formatted(maxInheritanceDepth, avgInheritanceDepth, abcMetric, avgOverriddenMethods, avgFields);
        Path out = Paths.get("out/" + jarName + "_results.txt");
        Files.writeString(out, json);
    }
}
