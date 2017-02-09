package catalog;

import catalog.Enums.JoinType;
import catalog.Enums.SortType;
import org.apache.log4j.Logger;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * This handles the plan builder file - plan_builder_config.txt
 * for reading and parsing the values provided to us
 *
 * @author Saarthak Chandra - sc2776
 * Shweta Shrivastava - ss3646
 * Vikas P Nelamangala - vpn6
 */


public class ConfigHandler {

    public static JoinType joinType = JoinType.TupleNestedLoopJoin;
    public static Integer joinBufferPagesNumber = null;

    public static SortType sortType = SortType.InMemorySort;
    public static Integer sortBufferPagesNumber = null;
    public static boolean useIndexForSelect;
    Logger logger = Logger.getLogger(ConfigHandler.class);

    /**
     * Private constructor - Singleton
     */
    private ConfigHandler() {
        setDefaultConfigParameters();
    }

    /**
     * Setting default parameters for the join type,sort type and the buffer pages for each
     */
    public static void setDefaultConfigParameters() {
        joinType = JoinType.TupleNestedLoopJoin;
        joinBufferPagesNumber = 1;
        sortType = SortType.InMemorySort;
        sortBufferPagesNumber = 3;
        useIndexForSelect = true;
    }

    /**
     * Read the plan_builder_config.txt  file and set values, else set default values
     */
    public static void setConfigParameters() {
        setDefaultConfigParameters();
//        try {
//            BufferedReader br = new BufferedReader(new FileReader(DBCatalog.configurationPath));
//            int[] joinParameters = new int[2];
//            int[] sortParameters = new int[2];
//            int i = 0;
//            for(String str : br.readLine().split(" ")) {
//                joinParameters[i] = Integer.parseInt(str);
//                i++;
//            }
//
//            i = 0;
//            for(String str : br.readLine().split(" ")) {
//                sortParameters[i] = Integer.parseInt(str);
//                i++;
//            }
//            if(joinParameters[0] == 0){
//                System.out.println("Join Type --> TNLJ");
//            }
//            if(joinParameters[0] == 1) {
//                joinType = JoinType.BlockNestedLoopJoin;
//                joinBufferPagesNumber = Math.max(1, joinParameters[1]);
//                System.out.println("Join Type --> BNLJ with "+joinBufferPagesNumber+" pages");
//            } else if(joinParameters[0] == 2) {
//                joinType = JoinType.SortMergeJoin;
//                System.out.println("Join Type --> SortMergeJoin");
//            }
//
//            if(sortParameters[0] == 0) {
//                System.out.println("Sort Method --> In-Memory Sort");
//            }
//            else if(sortParameters[0] == 1) {
//                sortType = SortType.ExternalSort;
//                sortBufferPagesNumber = Math.max(3, sortParameters[1]);
//                System.out.println("Sort Method --> External Sort with "+sortBufferPagesNumber+" pages");
//            }
//            else {
//                System.out.println("Invalid Sort Option");
//                return;
//            }
//
//            String a = br.readLine();
//            if(a.equals("1")) {
//                useIndexForSelect = true;
//                System.out.println("Using Index for Select");
//            } else {
//                useIndexForSelect = false;
//                System.out.println("Using Full Scan Implementation for Select");
//            }
//            br.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            setDefaultConfigParameters();
//        }
    }

}
//package catalog;
//
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.SortedSet;
//
//public class ConfigHandler {
//    private File configFile;
//    private int joinMethod = 0;
//    private int joinBufferSize = 0;
//    private int sortMethod = 0;
//    private int sortBufferSize = 0;
//    private boolean joinSeted = false;
//    private boolean sortSeted = false;
//
//    /**
//     * The name of the configuration.
//     */
//    public static final String CONFIG_NAME = "plan_builder_config.txt";
//
//    /**
//     * Tuple nested loop join.
//     */
//    public static final int TNLJ = 0;
//
//    /**
//     * Block nested loop join.
//     */
//    public static final int BNLJ = 1;
//
//    /**
//     * Sort merge join.
//     */
//    public static final int SMJ = 2;
//
//    /**
//     * In memory sort.
//     */
//    public static final int MEM_SORT = 0;
//
//    /**
//     * External Merge Sort.
//     */
//    public static final int EM_SORT = 1;
//
//    /**
//     * Construct the configuration generator.
//     * @param configDir the directory/folder the config is located.
//     */
//    public ConfigHandler(String configDir) {
//        configFile = new File(configDir + File.separator + CONFIG_NAME);
//    }
//
//    /**
//     * Sets the Join method.
//     * @param method the method number.
//     * @param bufferSize the size of the buffer.
//     */
//    public void setJoinMethod(int method, int bufferSize) {
//        if (method == 1 && bufferSize <= 0) {
//            throw new IndexOutOfBoundsException("The size is too small");
//        }
//        joinSeted = true;
//        this.joinMethod = method;
//        if (method == 1) {
//            joinBufferSize = bufferSize;
//        } else {
//            joinBufferSize = 0;
//        }
//    }
//
//    /**
//     * Sets the sort method.
//     * @param method the method number.
//     * @param bufferSize the size of the buffer.
//     */
//    public void setSortMethod(int method, int bufferSize) {
//        if (method != 0 && bufferSize <= 0) {
//            throw new IndexOutOfBoundsException("The size is too small");
//        }
//        sortSeted = true;
//        this.sortMethod = method;
//        if (method != 0) {
//            sortBufferSize = bufferSize;
//        } else {
//            sortBufferSize = 0;
//        }
//    }
//
//    /**
//     * Generate the configuration file.
//     */
//    public void gen() {
//        try {
//            BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));
//            writer.write(String.valueOf(joinMethod));
//            if (joinBufferSize > 0) {
//                writer.write(" " + String.valueOf(joinBufferSize));
//            }
//
//            writer.newLine();
//            writer.write(String.valueOf(sortMethod));
//            if (sortBufferSize > 0) {
//                writer.write(" " + String.valueOf(sortBufferSize));
//            }
//            writer.flush();
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//
//
//}