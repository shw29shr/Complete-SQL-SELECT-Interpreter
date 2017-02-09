package bplustree;

import bplustreecomponents.*;
import fileformats.BinaryTupleReader;
import models.Tuple;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Given a sorted input file, we build a B+ tree.
 *
 * @author Saarthak Chandra     sc2776
 *         Shweta Shrivastava   ss3646
 *         Vikas P Nelamangala	vpn6
 */

public class BPlusTree {

    private File file; // input file
    private BinaryTupleReader btr;
    private int bplustreeOrder; // bplustreeOrder of the tree
    private int bplustreeCapacity;// entries in each node
    private int position;
    private File indexFile;
    private List<DataEntry> dataEntriesList; // data entries for creating leaf nodes
    private List<BPlusTreeNode> leafLayerList; // leaflayer that stores all the leaf nodes
    private BPlusTreeNode root;
    private BPlusTreeSerializer bptSerializer;
    private Logger logger = Logger.getLogger(BPlusTree.class);

    /**
     * constructor
     *
     * @param file:the        input file used to construct the tree
     * @param position:       the index
     * @param bplustreeOrder: Order of the B+ tree
     * @throws IOException
     */
    public BPlusTree(File file, int position, int bplustreeOrder, File indexFile) throws IOException {
        this.file = file;
        btr = new BinaryTupleReader(file);
        leafLayerList = new ArrayList<>();
        this.position = position;
        this.indexFile = indexFile;
        this.bplustreeOrder = bplustreeOrder;
        this.bplustreeCapacity = 2 * bplustreeOrder;
        this.bptSerializer = new BPlusTreeSerializer(indexFile);
        genUnclusteredDataEntries();
        createLeafLayer();
        List<BPlusTreeNode> layer = new ArrayList<>(leafLayerList);
        while (layer.size() != 1) {
            layer = createIndexLayer(layer, bptSerializer);
        }
        root = layer.get(0);
        bptSerializer.serialize(root);
        bptSerializer.wrapUpSerialization(bplustreeOrder);
        bptSerializer.close();
        btr.close();
    }



    /**
     * generate the index layer according to the previous layer
     *
     * @throws IOException
     */
    private List<BPlusTreeNode> createIndexLayer(List<BPlusTreeNode> previousLayer, BPlusTreeSerializer bptSerializer) throws IOException {
        List<BPlusTreeNode> newLayer = new ArrayList<>();
        int countSoFar = 0;
        List<Integer> keysList = new ArrayList<>();
        List<BPlusTreeNode> childrenList = new ArrayList<>();
        List<Integer> addressList = new ArrayList<>();
        if (previousLayer.size() <= bplustreeCapacity) {
            for(int i = 0; i < previousLayer.size(); i++) {
                int ads = bptSerializer.serialize(previousLayer.get(i));
                addressList.add(ads);
            }
            childrenList.addAll(previousLayer);
            for(int i = 1; i < previousLayer.size(); i++) {
                keysList.add(previousLayer.get(i).rightSubtreeMinValue());
            }
            newLayer.add(new IndexNode(bplustreeOrder, keysList, childrenList, addressList));

        } else {

            for(int i = 0; i < previousLayer.size(); i++) {

                if(countSoFar == bplustreeCapacity) {
                    childrenList.add(previousLayer.get(i));
                    int ads = bptSerializer.serialize(previousLayer.get(i));
                    addressList.add(ads);
                    keysList.add(previousLayer.get(i).rightSubtreeMinValue());
                    IndexNode node = new IndexNode(bplustreeOrder, keysList, childrenList, addressList);
                    newLayer.add(node);
                    countSoFar = 0;
                    keysList.clear();
                    childrenList.clear();
                    addressList.clear();

                    int remainNum = previousLayer.size() - i - 1;
                    if(remainNum > (2 * bplustreeOrder + 1) && remainNum < (3 * bplustreeOrder + 2)) {
                        int lastButOneNode = remainNum / 2;

                        for (int j = i + 1; j < i + 1 + lastButOneNode; j++) {
                            ads = bptSerializer.serialize(previousLayer.get(j));
                            addressList.add(ads);
                        }

                        childrenList.addAll(previousLayer.subList(i + 1, i + 1 + lastButOneNode));
                        for (int j = i + 2; j < i + 1 + lastButOneNode; j++) {
                            keysList.add(previousLayer.get(j).rightSubtreeMinValue());
                        }

                        newLayer.add(new IndexNode(bplustreeOrder, keysList, childrenList, addressList));
                        keysList.clear();
                        childrenList.clear();
                        addressList.clear();

                        for (int j = i + 1 + lastButOneNode; j < previousLayer.size(); j++) {
                            ads = bptSerializer.serialize(previousLayer.get(j));
                            addressList.add(ads);
                        }
                        childrenList.addAll(previousLayer.subList(i + 1 + lastButOneNode, previousLayer.size()));
                        for (int j = i + 2 + lastButOneNode; j < previousLayer.size(); j++) {
                            keysList.add(previousLayer.get(j).rightSubtreeMinValue());
                        }
                        newLayer.add(new IndexNode(bplustreeOrder, keysList, childrenList, addressList));
                        keysList.clear();
                        childrenList.clear();
                        addressList.clear();
                        break;
                    }
                    continue;
                }

                if(countSoFar == 0) {
                    childrenList.add(previousLayer.get(i));
                    int ads = bptSerializer.serialize(previousLayer.get(i));
                    addressList.add(ads);
                    countSoFar++;
                } else if(countSoFar < bplustreeCapacity) {
                    keysList.add(previousLayer.get(i).rightSubtreeMinValue());
                    childrenList.add(previousLayer.get(i));
                    int ads = bptSerializer.serialize(previousLayer.get(i));
                    addressList.add(ads);
                    countSoFar++;
                }

            }
            if(keysList.size() != 0) {
                newLayer.add(new IndexNode(bplustreeOrder, keysList, childrenList, addressList));
            }
        }
        return newLayer;
    }

    /**
     * Make a list of data entries from an unclustered relation.
     */
    private void genUnclusteredDataEntries() {
        SortedMap<Integer, DataEntry> entryMap = new TreeMap<>();
        try {
            ArrayList<Tuple> tuples;
            int currentPageId = 0;
            while((tuples = btr.getNextPage()) != null) {
                for(int currTupleId = 0; currTupleId < tuples.size(); currTupleId++) {
                    Tuple currTuple = tuples.get(currTupleId);
                    int key = currTuple.values[position];
                    if(entryMap.containsKey(key)) {
                        DataEntry target = entryMap.get(key);
                        target.recordIDList.add(new RecordId(currentPageId, currTupleId));
                    } else {
                        DataEntry newEntry = new DataEntry(key, new ArrayList<>());
                        newEntry.recordIDList.add(new RecordId(currentPageId, currTupleId));
                        entryMap.put(key, newEntry);
                    }
                }
                currentPageId++;
            }
        } catch (IOException e) {
            if(logger.isDebugEnabled())
                e.printStackTrace();
        }

        //TreeMap has the entries sorted in bplustreeOrder,so we create an ArrayList
        dataEntriesList = new ArrayList<>(entryMap.values());
    }

    private void createLeafLayer() {
        if(dataEntriesList == null) {
            throw new NullPointerException();
        }
        int count = 0;
        // each sub data entry list in each node
        List<DataEntry> nodeEntries = new ArrayList<>();

        for(int i = 0; i < dataEntriesList.size(); i++) {
            if (count == bplustreeCapacity) {
                LeafNode node = new LeafNode(bplustreeOrder, nodeEntries);
                leafLayerList.add(node);
                nodeEntries.clear();
                count = 0;
            }
            nodeEntries.add(dataEntriesList.get(i));
            count++;
        }

        if(nodeEntries.size() != 0) {
            if(nodeEntries.size() >= bplustreeOrder) {
                LeafNode node = new LeafNode(bplustreeOrder, nodeEntries);
                leafLayerList.add(node);
            } else {
                //Case of an underflow case
                if(leafLayerList.size() == 0) {
                    leafLayerList.add(new LeafNode(bplustreeOrder, nodeEntries));
                } else {
                    LeafNode secondLast = (LeafNode) leafLayerList.remove(leafLayerList.size() - 1);
                    int numOfEntry = (2 * bplustreeOrder + nodeEntries.size()) / 2;
                    List<DataEntry> secondNodeEntries = secondLast.leafDataEntries;
                    List<DataEntry> lastNodeEntries =secondNodeEntries.subList(numOfEntry, secondNodeEntries.size());
                    lastNodeEntries.addAll(nodeEntries);
                    secondNodeEntries = secondNodeEntries.subList(0, numOfEntry);
                    secondLast = new LeafNode(bplustreeOrder, secondNodeEntries);
                    //add the second last
                    leafLayerList.add(secondLast);
                    // add the last node
                    leafLayerList.add(new LeafNode(bplustreeOrder, lastNodeEntries));
                }
            }
        }
    }


}
