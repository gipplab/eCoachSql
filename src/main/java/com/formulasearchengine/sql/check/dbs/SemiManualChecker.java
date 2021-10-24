package com.formulasearchengine.sql.check.dbs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public abstract class SemiManualChecker extends BaseChecker {
    double maybePoints = 0;
    double totalMaybePoints = 0;


    public void filenameExists(String name) {
        filenameExists(name, 1.0);
    }

    public void filenameExists(String name, Double points) {
        totalMaybePoints+=points;
        try {
            final List<Path> fileList = Files.walk(testFolder)
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
            for (Path path : fileList) {
                if (path.getFileName().toString().startsWith(name)) {
                    feedback(path.getFileName() + " will be checked by a human for task " + name);
                    feedback("You can gain up to "+ points +" points for this upload.");
                    maybePoints+=points;
                    return;
                }
            }
            feedback("No solution for task " + name + " submitted.");
        } catch (IOException e) {
            printException(e);
        }
    }

    @Override
    public void writeOutput() {
        super.writeOutput();
        feedback("You might obtain " + maybePoints + " of "+ totalMaybePoints + " additional points that will be granted by humans.");

    }
}
