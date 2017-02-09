package bplustreecomponents;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the IndexNode object
 * It has a list of childrenNodes which are pointers to other BPlusTree Nodes (Leaf/Index),
 * a list of addresses of all its childrenNodes and the list of actual indexKey values
 *
 * @author Saarthak Chandra     sc2776
 *         Shweta Shrivastava   ss3646
 *         Vikas P Nelamangala	vpn6
 */

public class IndexNode extends BPlusTreeNode {

    public List<Integer> indexkeys;
    public List<Integer> childNodeAddressList; // the childNodeAddressList the childrenNodes
    private List<BPlusTreeNode> childrenNodes;
    private int minValue; //the minimum indexKey value for its parent

    /**
     * Constructor for IndexNode
     *
     * @param treeOrder         the treeOrder of the bplus tree
     * @param indexKeys         the list of indexKeys in indexnode
     * @param childrenNodes     the list of child nodes(could be index/leaf)
     * @param childNodeAddressList list of addresses of child nodes
     */

    public IndexNode(int treeOrder, List<Integer> indexKeys, List<BPlusTreeNode> childrenNodes, List<Integer> childNodeAddressList) {
        super(treeOrder);
        this.childrenNodes = new ArrayList<>(childrenNodes);
        this.indexkeys = new ArrayList<>(indexKeys);
        this.childNodeAddressList = new ArrayList<>(childNodeAddressList);
        minValue = childrenNodes.get(0).rightSubtreeMinValue();
    }

    /**
     * Constructor for IndexNode called when childNodes themselves are not given, their addresses are given
     *
     * @param treeOrder         the treeOrder of the bplus tree
     * @param indexKeys         the list of indexKeys in indexnode
     * @param childNodeAddressList list of addresses of child nodes
     */

    public IndexNode(int treeOrder, List<Integer> indexKeys, List<Integer> childNodeAddressList) {
        super(treeOrder);
        this.childrenNodes = null;
        this.indexkeys = new ArrayList<>(indexKeys);
        this.childNodeAddressList = new ArrayList<>(childNodeAddressList);
        minValue = 0;
    }

    /**
     * We override the toString function so that
     * it returns the string representation of each IndexNode
     * to match with the input index text files as ..
     * IndexNode with indexkeys [692, 727, 758, 790, ...
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("IndexNode with index keys [");
        for(Integer key : indexkeys) {
            sb.append(key + ", ");
        }
        sb.setLength(sb.length() - 2);
        sb.append("] and child addresses [");
        for(Integer addr : childNodeAddressList) {
            sb.append(addr + ", ");
        }
        sb.setLength(sb.length() - 2);
        sb.append("]\n");
        return sb.toString();
    }

    @Override
    public int rightSubtreeMinValue() {
        return minValue;
    }

}
