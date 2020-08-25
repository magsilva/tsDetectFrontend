package edu.rit.se.testsmells;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVWriter;

public class Main {
	
	public static void main(String[] args) throws IOException {
    	if (args == null || args.length != 3) {
    		throw new IllegalArgumentException("Please provide the name of the project, the path to the project directory, and the name for the CSV file which will store the results");
        }
    	
    	String projectName = args[0];
    	
        File inputFile = new File(args[1]);
        if (! inputFile.exists() || ! inputFile.isDirectory()) {
        	throw new IllegalArgumentException("Invalid project directory");
        }
        
        String rootDirectory = args[1];
        String outputCsvFile = args[2];
        TestFileDetector testFileDetector = new TestFileDetector();
        
        //recursively identify all 'java' files in the specified directory
        FileWalker fw = new FileWalker();
        List<Path> testFiles = fw.getJavaTestFiles(rootDirectory, true);
        List<ClassEntity> testClasses = new ArrayList<ClassEntity>();
        for (Path file : testFiles) {
          	ClassEntity testClass = testFileDetector.runAnalysis(file);
           	testClasses.add(testClass);
        }

        MappingDetector mappingDetector;
        try (
        	Writer writer = Files.newBufferedWriter(Paths.get(outputCsvFile));
        	CSVWriter csvWriter = new CSVWriter(writer,
                        CSVWriter.DEFAULT_SEPARATOR,
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END);
        ) {
        	for (ClassEntity testClass : testClasses) {
        		mappingDetector = new MappingDetector();
                TestFile testFile = mappingDetector.detectMapping(testClass.getFilePath(), rootDirectory);
                if (testFile.getProductionFilePath() != null && testFile.getProductionFilePath().length() > 0) {
                	csvWriter.writeNext(new String[] { projectName, testFile.getFilePath(), testFile.getProductionFilePath() });
                }
            }	
        }
    }
}
