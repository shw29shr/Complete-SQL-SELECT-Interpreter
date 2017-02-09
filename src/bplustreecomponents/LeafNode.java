package bplustreecomponents;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the LeafNode which are the DataEntry.java objects
 * @author Saarthak Chandra     sc2776
 *         Shweta Shrivastava   ss3646
 *         Vikas P Nelamangala	vpn6
 *
 */
public class LeafNode extends BPlusTreeNode {

    public List<DataEntry> leafDataEntries;
    private int minValue;

    public LeafNode(int order, List<DataEntry> leafDataEntries) {
        super(order);
        this.leafDataEntries = new ArrayList<>(leafDataEntries);
        minValue = leafDataEntries.get(0).indexKey;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LeafNode[\n");
        for(DataEntry data : leafDataEntries) {
            sb.append(data.toString() + "\n");
        }
        sb.append("]\n");
        return sb.toString();
    }

    public int rightSubtreeMinValue() {
        return minValue;
    }


}
