package bplustreecomponents;

/**
 * An abstract class for B PLus Tree nodes
 * IndexNode and LeafNode extend from this class
 *
 * @author Saarthak Chandra     sc2776
 *         Shweta Shrivastava   ss3646
 *         Vikas P Nelamangala	vpn6
 */
public abstract class BPlusTreeNode {

    private int treeOrder;

    /**
     * Constructor - it only initializes the order of the tree
     * since that will be common for both Index and LeafNodes
     *
     * @param treeOrder
     */
    protected BPlusTreeNode(int treeOrder) {
        this.treeOrder = treeOrder;
    }

    /**
     * In order to initialize the keys, the min value of the right subtree is needed
     * This function does that.
     */
    abstract public int rightSubtreeMinValue();
}
