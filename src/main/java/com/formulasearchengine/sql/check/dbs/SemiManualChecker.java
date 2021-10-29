package com.formulasearchengine.sql.check.dbs;

import com.opencsv.bean.CsvToBeanBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
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
        totalMaybePoints += points;
        try {
            final List<Path> fileList = Files.walk(testFolder)
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
            for (Path path : fileList) {
                if (path.getFileName().toString().startsWith(name)) {
                    feedback(path.getFileName() + " will be checked by a human for task " + name);
                    feedback("You can gain up to " + points + " points for this upload.");
                    maybePoints += points;
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
        feedback("You might obtain " + maybePoints + " of " + totalMaybePoints + " additional points that will be granted by humans.");

    }

    public void compareWithResource(InputStream refStream, Double maxPoints, Class clazz) {
        compareWithResource(refStream, maxPoints, clazz, false);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void compareWithResource(InputStream refStream, Double maxPoints, Class clazz, Boolean detailedFeedback) {
        try {
            feedback("check that the file is a valid csv file");
            List list = new CsvToBeanBuilder(new StringReader(currentFileContent))
                    .withIgnoreLeadingWhiteSpace(true)
                    .withType(clazz)
                    .build()
                    .parse();
            addCheckMark();
            List refList = new CsvToBeanBuilder(new InputStreamReader(refStream))
                    .withType(clazz)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();
            if (refList.size() == list.size()
                    && list.containsAll(refList)) {
                points += maxPoints;
                addCheckMark();
                feedback("+");
            } else {
                feedback("Solution does not match reference solution.");
                if (detailedFeedback) {
                    StringBuilder sb = new StringBuilder("Hints to differences with reference solution.");
                    for (Object o : list) {
                        sb.append("\n  ");
                        sb.append("Element \"");
                        sb.append(o.toString());
                        sb.append("\" is ");
                        if (!refList.contains(o)) {
                            sb.append("NOT ");
                        }
                        sb.append("part of the reference solution.");
                    }
                    feedback(sb.toString());

                }
            }
        } catch (AssertionError | Exception e) {
            feedback("During evaluation the following error occurred");
            feedback(e.getLocalizedMessage());

        }
    }
}
