package indexhelpers;

/**
 * This class  contains some basic information
 * about the B+- Tree index like the table name and attribute,
 * whether it is clustered or unclustered and what is the order of the tree
 *
 * @author Saarthak Chandra     sc2776
 *         Shweta Shrivastava   ss3646
 *         Vikas P Nelamangala	vpn6
 *
 */


public class BPlusTreeIndexData {

    public String relationName = "";
    public String relationIndexColumn = "";
    public boolean isIndexClustered = false;
    public int bTreeOrder = 1;
    private int numOfLeaveNodes = -1;


    /**
     * Constructor method
     * @param isIndexClustered whether index is clustered/unclustered
     * @param bTreeOrder order of the tree (d)
     * @param relationName name of the table
     * @param relationIndexColumn column name which has index
     */
    protected BPlusTreeIndexData(boolean isIndexClustered, int bTreeOrder, String relationName, String relationIndexColumn) {
        this.isIndexClustered = isIndexClustered;
        this.bTreeOrder = bTreeOrder;
        this.relationName = relationName;
        this.relationIndexColumn = relationIndexColumn;
    }

    /**
     * get the number of leaf nodes.
     *
     * @return num of leaf nodes.
     */
    public int getLeafNodeCount() {
        return numOfLeaveNodes;
    }

    /**
     * set the number of leaf nodes.
     *
     * @param num of leaf nodes.
     */
    protected void setLeafNodeCount(int num) {
        numOfLeaveNodes = num;
    }
}
