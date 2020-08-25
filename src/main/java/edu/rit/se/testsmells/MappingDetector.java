package edu.rit.se.testsmells;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class MappingDetector {

    TestFile testFile;
    String productionFileName;
    String productionFilePath;
    boolean ignoreFile;

    public MappingDetector() {
        initialize();
        
    }
    
    private void initialize() {
    	productionFileName = "";
        productionFilePath = "";
        ignoreFile = false;
        testFile = null;
    }


    public TestFile detectMapping(String testFilePath, String projectRoot) throws IOException {
    	initialize();
        testFile = new TestFile(testFilePath);
        String testRelativeFilename = new File(testFile.getFileName()).getName(); 
        int index = testRelativeFilename.toLowerCase().lastIndexOf("test");
        if (index == 0) {
            //the name of the test file starts with the name 'test'
            productionFileName = testRelativeFilename.substring("test".length(), testRelativeFilename.length());
        } else {
            productionFileName = testRelativeFilename.substring(0, testRelativeFilename.length() - "test".length() - ".java".length()) + ".java";
        }

        Path startDir = Paths.get(new File(testFile.getFileName()).getParent());
        Files.walkFileTree(startDir, new FindJavaTestFilesVisitor());
        if (isFileSyntacticallyValid(productionFilePath)) {
            testFile.setProductionFilePath(productionFilePath);
        } else {
            startDir = Paths.get(projectRoot);
            Files.walkFileTree(startDir, new FindJavaTestFilesVisitor());
            if (isFileSyntacticallyValid(productionFilePath)) {
                testFile.setProductionFilePath(productionFilePath);
            } else {
            	testFile.setProductionFilePath("");
            }
        }

        return testFile;
    }

    /**
     * Determines if the identified production file is syntactically correct by parsing it and generating its AST
     *
     * @param filePath of the production file
     */
    private boolean isFileSyntacticallyValid(String filePath) {
        boolean valid = false;
        ignoreFile = false;

        if (filePath != null && filePath.length() != 0) {
            try {
                FileInputStream fTemp = new FileInputStream(filePath);
                CompilationUnit compilationUnit = JavaParser.parse(fTemp);
                MappingDetector.ClassVisitor classVisitor;
                classVisitor = new MappingDetector.ClassVisitor();
                classVisitor.visit(compilationUnit, null);
                valid = !ignoreFile;
            } catch (Exception error) {
                valid = false;
            }

        }

        return valid;
    }

    public class FindJavaTestFilesVisitor extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult visitFile(Path file,
                                         BasicFileAttributes attrs)
                throws IOException {
            if (file.getFileName().toString().toLowerCase().equals(productionFileName.toLowerCase())) {
                productionFilePath = file.toAbsolutePath().toString();
                return FileVisitResult.TERMINATE;
            }
            return FileVisitResult.CONTINUE;
        }
    }

    /**
     * Visitor class
     */
    private class ClassVisitor extends VoidVisitorAdapter<Void> {

        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            ignoreFile = n.isInterface();
            super.visit(n, arg);
        }

        @Override
        public void visit(AnnotationDeclaration n, Void arg) {
            ignoreFile = true;
            super.visit(n, arg);
        }
    }

}

