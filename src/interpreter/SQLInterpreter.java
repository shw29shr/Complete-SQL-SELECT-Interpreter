package interpreter;

import catalog.DBCatalog;
import error.SQLCustomErrorHandler;
import fileformats.BinaryTupleWriter;
import fileformats.FileTupleWriter;
import fileformats.TupleWriter;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.statement.Statement;
import org.apache.log4j.Logger;
import utils.PropertyFileReader;
import utils.SelectExecutor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

/**
 * The SqlInterpreter class in the entry point for the SQLInterpreter. The
 * main() function takes in as arguments the location of the sample inputs It
 * reads the queries one by one and generates output.
 *
 * @author Saarthak Chandra     sc2776
 *         Shweta Shrivastava   ss3646
 *         Vikas P Nelamangala	vpn6
 */
public class SQLInterpreter {


    private PropertyFileReader reader = PropertyFileReader.getInstance();
    private Logger logger = Logger.getLogger(SQLInterpreter.class);
    private SQLCustomErrorHandler handler = SQLCustomErrorHandler.getCatalogInstance();

    /**
     * @param args argument list given when we run the jar
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Incorrect input format, only " + args.length + " provided");
        }

        SQLInterpreter interpreter = new SQLInterpreter();
        try {
            interpreter.execute(args[0]);
        } catch (IOException e) {
            System.out.println("Failed to Initialize --> sqlInterpret.execute(args[0])");
        }
    }

    /**
     * Based on the config file, we run this
     *
     * @param configPath the path of the configuration file.
     * @throws IOException If an I/O error occurs.
     */
    protected void execute(String configPath) throws IOException {

        InterpreterConfig config = new InterpreterConfig(configPath);
        DBCatalog.createDirectories(config.inPath, config.outPath, config.tempPath);
        DBCatalog.getCatalogInstance();
        this.createOutputDirectoryIfNotExists(config.outPath);

        if (config.shouldGenerateStats) {
            System.out.println("\n\n###################### Gathering the statistics ###############################");
        }

        if (config.shouldBuildIndex) {
            System.out.println("\n\n###################### Start - BUILDING INDEX ###############################");
            //System.out.println("Building the index");
            boolean withHumanReadable = true;
            DBCatalog.bPlusTreeIndexManager.createBPlusIndex();
            System.out.println("###################### Done - BUILDING INDEX ###############################");
        }

        if (config.shouldEvaluate) {
            System.out.println("Evaluating the query");
            evaluateQuery(config.inPath, config.outPath, config.tempPath);
        }
    }

    /**
     * Output of the parser with the given input / output directory.
     *
     * @param inPath   input directory
     * @param outPath  output directory
     * @param tempPath output directory
     */
    private void evaluateQuery(String inPath, String outPath, String tempPath) {
        DBCatalog.createDirectories(inPath, outPath, tempPath);
        DBCatalog.getCatalogInstance();
        try {

            CCJSqlParser sqlparser = new CCJSqlParser(new FileReader(DBCatalog.queryPath));
            Statement querystatement;
            int querycounter = 1;
            while ((querystatement = sqlparser.Statement()) != null) {
                try {
                    String fileName = DBCatalog.outputDirectory + File.separator + "query" + querycounter;
                    File file = new File(fileName);
                    File logicalPlan = new File(DBCatalog.outputDirectory + File.separator + "query" + querycounter + "_logicalplan");
                    File physicalPlan = new File(DBCatalog.outputDirectory + File.separator + "query" + querycounter + "_physicalplan");
                    PrintStream logicalPlanStream = new PrintStream(logicalPlan);
                    PrintStream physicalPlanStream = new PrintStream(physicalPlan);

                    if (logger.isDebugEnabled())
                        System.out.println("Parsing: " + querystatement);

                    if (logger.isDebugEnabled()) {
                        System.out.println("\n\n ----------------------- QUERY " + querycounter + "-----------------------------------------------");
                        System.out.println("Evaluating Query ---> " + querystatement);
                        //System.out.println("Output FileName ---> " + fileName);
                    }
                    SelectExecutor selectExecutor = new SelectExecutor(querystatement);

                    // Print the  logical and physical plans
                    System.out.println("\n************ Logical Plan ************");
                    selectExecutor.logicRoot.printQueryPlan(System.out, 0);
                    selectExecutor.logicRoot.printQueryPlan(logicalPlanStream, 0);
                    logicalPlanStream.close();

                    System.out.println("\n************ Physical Plan ************");
                    selectExecutor.root.printQueryPlan(System.out, 0);
                    selectExecutor.root.printQueryPlan(physicalPlanStream, 0);
                    physicalPlanStream.close();

                    TupleWriter writer;
                    if (reader.getProperty("isBinary").equalsIgnoreCase("true"))
                        writer = new BinaryTupleWriter(fileName);
                    else
                        writer = new FileTupleWriter(fileName);
                    selectExecutor.root.dump(writer);

                    // begin time
                    long beginTime = System.currentTimeMillis();
                    selectExecutor.root.dump(writer);
                    // end time
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - beginTime;
                    //System.out.println("The running time for query " + querycounter + " is " + duration + " milliseconds");
                    System.out.println("-------------------- END --------------------------------------------------");
                    writer.close();
                    querycounter++;

                } catch (Exception ex) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(ex);
                        ex.printStackTrace();
                    }querycounter++;
                    handler.printCustomError(ex, this.getClass().getSimpleName());
                }
            }
        } catch (Exception ex) {
            if (logger.isDebugEnabled()) {
                logger.debug(ex);
            }
        }
    }

    /**
     * Create output directory folder if it does not exist
     *
     * @param outputDirectory Directory provided as input from command line
     */
    private void createOutputDirectoryIfNotExists(String outputDirectory) {
        new File(outputDirectory).mkdirs();
    }

}
