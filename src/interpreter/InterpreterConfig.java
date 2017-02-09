package interpreter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

// The class for configuration of the sql interpreter.
class InterpreterConfig {
    protected String inPath;
    protected String outPath;
    protected String tempPath;
    // Default is true
    protected boolean shouldBuildIndex = true;
    protected boolean shouldEvaluate = true;
    protected boolean shouldGenerateStats = true;


    public InterpreterConfig(String configPath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(configPath));
        inPath = br.readLine();
        outPath = br.readLine();
        tempPath = br.readLine();
        int value = 1;
        String lineRead = br.readLine();
        if (lineRead != null && !lineRead.equals("")) {
            value = Integer.parseInt(lineRead);
            shouldBuildIndex = value != 0;
        }
        lineRead = br.readLine();
        if (lineRead != null && !lineRead.equals("")) {
            value = Integer.parseInt(lineRead);
            shouldEvaluate = value != 0;
        }

        lineRead = br.readLine();
        if (lineRead != null && !lineRead.equals("")) {
            value = Integer.parseInt(lineRead);
            shouldGenerateStats = value != 0;
        }
        print();
        br.close();
    }

    private void print() {
        System.out.println("Input Path -->" + this.inPath);
        System.out.println("Output Path -->" + this.outPath);
        System.out.println("Temp Path -->" + this.tempPath);
        System.out.println("Should Build Index? " + this.shouldBuildIndex);
        System.out.println("Should Evaluate Queries? " + this.shouldEvaluate);
        System.out.println("Should Generate Statistics? " + this.shouldGenerateStats);
    }

}