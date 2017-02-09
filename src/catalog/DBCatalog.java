package catalog;

import error.SQLCustomErrorHandler;
import fileformats.BinaryTupleReader;
import fileformats.FileTupleReader;
import fileformats.TupleReader;
import indexhelpers.BPlusTreeIndexBuilder;
import models.Table;
import org.apache.log4j.Logger;
import utils.PropertyFileReader;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The DBCatalog class implements a singleton pattern and provides an instance
 * of the Catalog consisting of schema, directories etc..
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */
public class DBCatalog {

    public static String outputDirectory = "samples" + File.separator + "output" + File.separator;
    public static String dataDirectory = "";
    public static String queryPath = "";
    public static String tempDirectory = "temp" + File.separator;
    public static String indexDirectory = "";
    public static HashMap<String, List<String>> schemas = new HashMap<>();
    public static HashMap<String, String> aliases = new HashMap<>();
    public static BPlusTreeIndexBuilder bPlusTreeIndexManager;
    public static HashMap<String, TableStatsStructure> tableStatsStructure = null;
    public static String dbDirectory = "";
    public static String schemaPath = "";
    public static String configurationPath = "";
    private static String inputDirectory = "samples" + File.separator + "input" + File.separator;
    private static String indexConfigPath = "";
    private static PropertyFileReader reader = PropertyFileReader.getInstance();
    private static DBCatalog catalogInstance;
    private static Logger logger = Logger.getLogger(DBCatalog.class);
    private static boolean isBinary = true;
    SQLCustomErrorHandler handler = SQLCustomErrorHandler.getCatalogInstance();

    /**
     * Instantiate the constructor.
     */
    private DBCatalog() {
        createDirectories(inputDirectory, outputDirectory, tempDirectory);
    }

    /**
     * Create the input and output directories.
     *
     * @param newInputDirectory  takes input directory
     * @param newOutputDirectory takes output directory
     * @param newTempDirectory takes temp directory
     */
    public static void createDirectories(String newInputDirectory, String newOutputDirectory, String newTempDirectory) {
        inputDirectory = newInputDirectory + File.separator;
        outputDirectory = newOutputDirectory + File.separator;
        dbDirectory = inputDirectory + reader.getProperty("dbSubDirectory") + File.separator;
        dataDirectory = dbDirectory + reader.getProperty("dataSubDirectory") + File.separator;
        schemaPath = dbDirectory + reader.getProperty("schemaFileName");
        queryPath = inputDirectory + reader.getProperty("queriesFileName");
        tempDirectory = newTempDirectory + File.separator;
        configurationPath = inputDirectory + reader.getProperty("configFileName");
        indexDirectory = dbDirectory + reader.getProperty("indexesSubDirectory") + File.separator;
        indexConfigPath = dbDirectory + reader.getProperty("indexInfoPath");
        ConfigHandler.setConfigParameters();
        DBCatalog.createSchema();
        setIndexData();
        tableStatsStructure = null;
        GenerateStats stats;
        try {
            stats = new GenerateStats();
            stats.collectTableStatistics();
            tableStatsStructure = stats.getTableStatistics();
        } catch (IOException e) {
            if (logger.isDebugEnabled())
                e.printStackTrace();
        }
    }

    /**
     * Returns DBCatalog instance. The Singleton implementation ensures that
     * there exists only 1 version of catalog at any point of time.
     *
     * @return The instance of the class
     */
    public static DBCatalog getCatalogInstance() {
        // System.out.println("get catalog called");
        if(catalogInstance == null) {
            catalogInstance = new DBCatalog();
        }
        return catalogInstance;
    }

    /**
     * Read form the given schema file and generate schema for each table
     * present The generated set of schemas is stored as a Map<String,
     * ArrayList<String>> where the indexKey of the map is the table name and the
     * value is an array of column names
     */
    private static void createSchema() {
        // System.out.println("Create Schema Called....");
        BufferedReader br;
        schemas.clear();
        try {
            br = new BufferedReader(new FileReader(schemaPath));
            String row;
            while((row = br.readLine()) != null){
                String[] tokens = row.split(" ");
                if (tokens.length < 2)
                    continue;
                String key = tokens[0];
                ArrayList<String> columnNames = new ArrayList<>();
                for (int i = 1; i < tokens.length; i++) {
                    columnNames.add(tokens[i]);
                }
                schemas.put(key, columnNames);
            }
            br.close();
        } catch (IOException ex) {
            SQLCustomErrorHandler handler = SQLCustomErrorHandler.getCatalogInstance();
            handler.printCustomError(ex, "DBCatalog");
            if (logger.isDebugEnabled())
                ex.printStackTrace();
        }
    }

    /**
     * Get the schema for a given tablename/alias
     *
     * @param tableName Name of the table/alias for the table
     * @return Corresponding schema object (represented as List<String>)
     */
    private static List<String> getSchema(String tableName){
        String actualTableName = tableName;
        if(aliases.containsKey(tableName))
            actualTableName = aliases.get(tableName);
        return schemas.get(actualTableName);
    }

    /**
     * Creates a table object with given table name and schema details.
     * getTableObject
     * @param tableName Name of the table
     * @return Table object comprising of table name, table schema and a buffer
     * to read from the corresponding data file
     *
     */
    public static Table getTableObject(String tableName) {
        TupleReader tr = getTupleReaderType(tableName);
        if(tr == null) return null;
        return (new Table(tableName, getSchema(tableName), tr));
    }

    /**
     * This function returns a tuple reader for the given table
     * @param tableName the file tableName
     * @return the buffered reader
     */
    private static TupleReader getTupleReaderType(String tableName) {
        String actualTableName = tableName;
        if(aliases.containsKey(tableName))
            actualTableName = aliases.get(tableName);
        tableName = dataDirectory + actualTableName;
        String isBinary = reader.getProperty("isBinary");
        try {
            return (isBinary.equals("true")) ? new BinaryTupleReader(tableName) : new FileTupleReader(tableName);
        } catch (FileNotFoundException e) {
            if(logger.isDebugEnabled())
                e.printStackTrace();
        }
        return null;
    }

    /**
     * Initializes the index information
     */
    private static void setIndexData() {
        bPlusTreeIndexManager = new BPlusTreeIndexBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(indexConfigPath))) {
            String text;
            while((text = br.readLine()) != null) {
                String[] words = text.split(" ");
                String relationName = words[0];
                String relationColumnName = words[1];
                boolean isIndexClustered = (words[2].equals("1"));
                int bTreeOrder = Integer.parseInt(words[3]);
                bPlusTreeIndexManager.addIndexInformation(relationName, relationColumnName, isIndexClustered, bTreeOrder);
            }
        } catch (IOException e) {
            if (logger.isDebugEnabled())
                e.printStackTrace();
        }
    }

    /**
     * Returns the stats for the given table
     * @param tableName Name of the table
     * @return TableStatsStructure
     */
    public static TableStatsStructure getTableStatsStructure(String tableName) {
        String actualTableName = tableName;
        if(aliases.containsKey(tableName))
            actualTableName = aliases.get(tableName);
        return tableStatsStructure.get(actualTableName);
    }

     /**
     * Returns the actual ful name of the table if the alias is given
     * @param tableName  table name
     * @return table actual name
     */
    public static String getActualTableName(String tableName) {
        if(aliases.containsKey(tableName))
            return aliases.get(tableName);
        return tableName;
    }
}
