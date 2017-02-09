package catalog;
//A
import java.util.HashMap;
import java.util.Set;

/**
 * Here we define a data structure to hold the values of the
 * statistics of a table
 * The GenerateStats file uses this structure to store
 * the collected information on every table
 */
/**
 * @author Saarthak Chandra     sc2776
 *         Shweta Shrivastava   ss3646
 *         Vikas P Nelamangala	vpn6
 */
public class TableStatsStructure {
    String tableName;
    int columnCount;
    int rowCount;

    //Yet another structure to hold min and max values for every column present in the table
    HashMap<String, int[]> columnAttributeMap;

    /**
     * Constructor for TableStatsStructure
     * @param tableName Name of the table
     * @param columnNames A string array equal to the number of columns int the table
     * @param min an integer array to hold the min value of each column
     * @param max an integer array to hold the max value of each column
     */
    public TableStatsStructure(String tableName, String[] columnNames, int[] min, int[] max) {
        this.columnCount = -1;
        this.tableName = tableName;
        columnAttributeMap = new HashMap<>();
        for (int i = 0; i < columnNames.length; i++) {
            int[] range = {min[i], max[i]};
            if(columnNames[i] != null) {
                columnAttributeMap.put(columnNames[i], range);
            }
        }
        columnCount = columnNames.length;
    }

    /**
     * This function adds a new index in the column info map
     * @param columnName Name of the column
     * @param min minimum value
     * @param max maximum value
     */
    public void addNewColumnToColumnInfo(String columnName, int min, int max) {
        if(columnAttributeMap.containsKey(columnName)) {
            throw new IllegalArgumentException();
        } else {
            int[] range = {min, max};
            columnAttributeMap.put(columnName, range);
        }
    }

    //Below we have all the functions which return the information on the table
    //These functions will be used for cost calculations in various places

    /**
     * @return number of rows in the table
     */
    public int getRowCount() {
        return rowCount;
    }

    /**
     * Given a column name, this function returns an array
     * containing the structure range which holds the min and max
     * values of each column
     * @return int array containing range
     */
    public int[] getRange(String attrName) {
        if(columnAttributeMap.containsKey(attrName)) {
            return columnAttributeMap.get(attrName);
        } else {
            return null;
        }
    }
    /**
     * Returns the total number of columns of the table
     * @return column count
     */
    public int getColumnCount() {
        if(columnCount == -1) {
            throw new IllegalArgumentException();
        }
        return columnCount;
    }

    /**
     * This function returns a list of column Names
     * present in the table
     * @return string array containing column names
     */
    public String[] getColumnNames() {
        Set<String> columnNames = columnAttributeMap.keySet();
        return (String[]) columnNames.toArray();
    }

    /**
     * Used by GenerateStats to set the number of rows for the table
     */
    public void setRowCount(int count) {
        rowCount = count;
    }


}

