package edu.rit.se.testsmells;

import org.apache.commons.lang3.StringUtils;
import java.io.File;

public class TestFile {
    private String filePath, productionFilePath;
    String[] data;

    public String getFileName() {
        return data[data.length - 1];
    }

    public String getFilePath() {
        return filePath;
    }

    public String getProductionFilePath() {
        return productionFilePath;
    }

    public void setProductionFilePath(String productionFilePath) {
        this.productionFilePath = productionFilePath;
    }

    public String getProjectRootFolder() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            stringBuilder.append(data[i] + File.separator);
        }
        return stringBuilder.toString();
    }

    public String getAppName() {
        return data[3];
    }

    public String getTagName() {
        return data[4];
    }

    public TestFile(String filePath) {
        this.filePath = filePath;
        data = filePath.split("\\\\");
    }

    public String getRelativeTestFilePath(){
        String[] splitString = filePath.split("\\\\");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            stringBuilder.append(splitString[i] + "\\");
        }
        return filePath.substring(stringBuilder.toString().length()).replace("\\","/");
    }

    public String getRelativeProductionFilePath(){
        if (!StringUtils.isEmpty(productionFilePath)){
            String[] splitString = productionFilePath.split("\\\\");
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                stringBuilder.append(splitString[i] + "\\");
            }
            return productionFilePath.substring(stringBuilder.toString().length()).replace("\\","/");
        }
        else{
            return "";
        }

    }
}
