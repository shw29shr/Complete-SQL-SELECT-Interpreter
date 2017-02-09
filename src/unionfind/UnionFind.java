package unionfind;

import java.util.*;

/**
 * This class defines the Union Find data structure
 *
 * @author Saarthak Chandra     sc2776
 *         Shweta Shrivastava   ss3646
 *         Vikas P Nelamangala	vpn6
 */

public class UnionFind {

    public int elementcount;
    public Map<String, UnionFindElement> unionFindElements;
    public Map<UnionFindElement, UnionFindElement> unionFindParent;
    public Map<UnionFindElement, Integer> unionFindElementRank;

    /**
     * Constructor for UnionFind Data Structure
     */
    public UnionFind() {
        this.elementcount = 0;
        this.unionFindElements = new HashMap<>();
        this.unionFindParent = new HashMap<>();
        this.unionFindElementRank = new HashMap<>();
    }

    /**
     * This function searches for the union find element structure
     * corresponding to a particular column of a relation and returns it
     * If no structure exists, create one and return it
     * @return the union find element structure
     */
    public UnionFindElement find(String tableColumn) {
        if(!unionFindElements.containsKey(tableColumn)) {
            UnionFindElement newElement = new UnionFindElement();
            unionFindElements.put(tableColumn, newElement);
            unionFindParent.put(newElement, newElement);
            unionFindElementRank.put(newElement, 0);
            elementcount++;
        }
        UnionFindElement ufe = unionFindElements.get(tableColumn);
        while(ufe != unionFindParent.get(ufe)) {
            unionFindParent.put(ufe, unionFindParent.get(unionFindParent.get(ufe)));
            ufe = unionFindParent.get(ufe);
        }
        return ufe;
    }

    /**
     * Returns the list of all union find elements in the form of string
     * @return String representing hte union find elements
     */
    public List<String> elementListAsString() {
        List<String> finalList = new ArrayList<>();
        Map<UnionFindElement, List<String>> parentInfo = new HashMap<>();
        for(String col : unionFindElements.keySet()) {
            UnionFindElement ufe = find(col);
            if (!parentInfo.containsKey(ufe)) {
                parentInfo.put(ufe, new ArrayList<>());
            }
            parentInfo.get(ufe).add(col);
        }
        for(UnionFindElement ufe : parentInfo.keySet()) {
            StringBuilder sb = new StringBuilder();
            sb.append("[[");
            for (String col : parentInfo.get(ufe)) {
                sb.append(col + ", ");
            }
            sb.setLength(sb.length()-2);
            sb.append("], equals ");
            sb.append(ufe.equality);
            sb.append(", min ");
            sb.append(ufe.lowerbound);
            sb.append(", max ");
            sb.append(ufe.upperbound);
            sb.append("]");
            finalList.add(sb.toString());
        }
        return finalList;
    }

    /**
     * Sets two union find elements to be equal to each other
     * @param ufe1 first element
     * @param ufe2 second element
     */
    public void setElementsEqual(UnionFindElement ufe1, UnionFindElement ufe2) {
        ufe1.setAllConstraints(ufe2);
        ufe2.setAllConstraints(ufe1);
    }

    /**
     * Given two columns, this merges their union find elements
     * @param tableColumn1 first column
     * @param tableColumn2 second column
     */
    public void unionColumnElements(String tableColumn1, String tableColumn2) {
        UnionFindElement ufe1 = find(tableColumn1);
        UnionFindElement ufe2 = find(tableColumn2);
        unionElements(ufe1, ufe2);
    }

    /**
     * Performs the union of two union find elements
     * @param ufe1 first element
     * @param ufe2 second element
     */
    public void unionElements(UnionFindElement ufe1, UnionFindElement ufe2) {
        setElementsEqual(ufe1, ufe2);
        if(unionFindElementRank.get(ufe1) < unionFindElementRank.get(ufe2)) {
            unionFindParent.put(ufe1, ufe2);
        }
        else if(unionFindElementRank.get(ufe1) < unionFindElementRank.get(ufe2)) {
            unionFindParent.put(ufe2, ufe1);
        }
        else {
            unionFindParent.put(ufe2, ufe1);
            int elementRank = unionFindElementRank.get(ufe1);
            unionFindElementRank.put(ufe1, elementRank + 1);
        }
        elementcount--;
    }

}
