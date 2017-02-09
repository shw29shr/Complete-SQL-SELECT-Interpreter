package utils;

import error.SQLCustomErrorHandler;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * We use this class to read from the conf.properties file ... That property
 * file has some of the paths/sub-directories of our project
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */
public class PropertyFileReader extends Properties {
    private static String FILE_NAME = "conf.properties"; // Our property file
    // tableName
    private static String ROOT_PATH = "./src/";

    private static PropertyFileReader instance = null;

    /**
     * Private constructor to prevent objects of this class from being made
     */
    private PropertyFileReader() {
    }

    /**
     * This function returns the propertyFile singleton object
     *
     * @return Singleton instance of the PropertyFileReader class
     */
    public static PropertyFileReader getInstance() {
        if (instance == null) {
            try {
                instance = new PropertyFileReader();
                setPropertiesFile();
                if (checkFile(ROOT_PATH + FILE_NAME)) {
                    FileInputStream in = new FileInputStream(ROOT_PATH + FILE_NAME);
                    instance.load(in);
                    in.close();
                } else
                    setPropertiesFile();
            } catch (Exception ex) {
                SQLCustomErrorHandler handler = new SQLCustomErrorHandler(ex.getMessage());
                return null;
            }
        }
        return instance;
    }

    /**
     * Return the status if property file exists or not
     *
     * @param filePathString File path of the properties file we check
     * @return true if file exists, else false
     */
    private static boolean checkFile(String filePathString) {
        File f = new File(filePathString);
        return f.exists();
    }

    /**
     * Fallback if we are unable to read the property file, we set the
     * properties here
     */
    private static void setPropertiesFile() {
        instance.setProperty("schemaFileName", "schema.txt");
        instance.setProperty("queriesFileName", "queries.sql");
        instance.setProperty("configFileName", "plan_builder_config.txt");
        instance.setProperty("dataSubDirectory", "data");
        instance.setProperty("dbSubDirectory", "db");
        instance.setProperty("outputFilePrefix", "query");
        instance.setProperty("bufferSize", "4096");
        instance.setProperty("variableSize", "4");
        instance.setProperty("isBinary", "true");
        instance.setProperty("attributeSize", "4");
        instance.setProperty("pageSize", "4096");
        instance.setProperty("leafNodeIndicator", "0");
        instance.setProperty("indexNodeIndicator", "1");
        instance.setProperty("headerPageID", "0");
        instance.setProperty("leafPageID", "1");
        instance.setProperty("leafPageID", "1");
        instance.setProperty("indexesSubDirectory", "indexes");
        instance.setProperty("indexInfoPath", "index_info.txt");
    }
}