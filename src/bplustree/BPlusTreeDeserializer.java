package bplustree;

import bplustreecomponents.*;
import org.apache.log4j.Logger;
import utils.PropertyFileReader;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;


/**
 * This class is used for deserializing the given index tree
 * within a file
 *
 * @author Saarthak Chandra     sc2776
 *         Shweta Shrivastava   ss3646
 *         Vikas P Nelamangala	vpn6
 */

public class BPlusTreeDeserializer {

    private PropertyFileReader reader = PropertyFileReader.getInstance();
    private final int B_SIZE = Integer.valueOf(reader.getProperty("bufferSize"));    // the size of the byteBuffer page
    private Logger logger = Logger.getLogger(BPlusTreeDeserializer.class);
    private File file;
    private FileChannel fileChannel;
    private ByteBuffer byteBuffer;
    private int rootNodeAddr;
    private int leafNodeCount;
    private int order;
    private Integer lowKey;
    private Integer highKey;
    private LeafNode currentLeafNode;
    private int currentLeafPageAddr;
    private int currentDataEntry;
    private int currentRecordID;
    private boolean isDone;

    /**
     * Constructor for binary tree deserializer.
     * Uses the indexDataFile to open a filechannel
     * Reads the full tree
     *
     * @param indexDataFile File which has the index tree data.
     * @throws FileNotFoundException
     */
    private BPlusTreeDeserializer(File indexDataFile) throws FileNotFoundException {
        this.file = indexDataFile;
        fileChannel = new FileInputStream(file).getChannel();
        byteBuffer = ByteBuffer.allocate(B_SIZE);
        try {
            readHeaderPage();
        } catch (IOException e) {
            if (logger.isDebugEnabled())
                e.printStackTrace();
        }
    }

    /**
     * Constructs the the deserializer using the given indexFile and the given
     * range specified by the lowKey and highKey.
     *
     * @param indexFile which stores the serialized tree index.
     * @param lowKey    the lower bound (inclusive), no limit if set to null.
     * @param highKey   the higher bound (exclusive), no limit if set to null.
     * @throws FileNotFoundException
     */
    public BPlusTreeDeserializer(File indexFile, Integer lowKey, Integer highKey) throws FileNotFoundException {
        this(indexFile);
        this.lowKey = lowKey;
        this.highKey = highKey;
        currentDataEntry = 0;
        currentRecordID = 0;
        currentLeafPageAddr = 1;
        isDone = false;
        locateLeafPageThroughLowkey();
    }


    /**
     * This function is used to go to the first leaf page
     * based on the value of the lowkey
     */
    private void locateLeafPageThroughLowkey() {
        try {
            if(lowKey == null) {
                currentLeafNode = (LeafNode) generateNodeThroughAddress(currentLeafPageAddr);
            } else {
                currentLeafNode = createFirstLeafNode();
                while (currentLeafNode != null && currentLeafNode.leafDataEntries.get(currentDataEntry).indexKey < lowKey) {
                    currentDataEntry++;
                    if(currentDataEntry >= currentLeafNode.leafDataEntries.size()) {
                        currentLeafNode = gotoNextLeafNode();
                        currentDataEntry = 0;
                    }
                }

            }

        } catch (IOException e) {
            if (logger.isDebugEnabled())
                e.printStackTrace();
        }
    }

    /**
     * This function is used to traverse to the starting leaf page
     * and returns the generated LeafNode
     *
     * @return LeafNode node which is read
     * @throws IOException
     */
    private LeafNode createFirstLeafNode() throws IOException {
        if(lowKey == null) {
            throw new IllegalArgumentException();
        }
        retrievePageforPageID(rootNodeAddr);
        BPlusTreeNode curr = deserializeIndexNode();
        int pageAddr = 1;
        while(curr instanceof IndexNode) {
            IndexNode currIndexNode = (IndexNode) curr;
            int pageIdx = Collections.binarySearch(currIndexNode.indexkeys, lowKey + 1);
            pageIdx = pageIdx >= 0 ? pageIdx : -(pageIdx + 1);
            pageAddr = currIndexNode.childNodeAddressList.get(pageIdx);
            curr = generateNodeThroughAddress(pageAddr);
        }
        currentLeafPageAddr = pageAddr;
        return (LeafNode) curr;
    }

    /**
     * Returns the next recordID retrieved
     * If the end of the page is reached null is returned
     *
     * @return RecordID the RecordID.
     * @throws IOException
     */
    public RecordId getNextRecordID() throws IOException {
        if(isDone || currentLeafNode == null) return null;
        if(currentRecordID >= currentLeafNode.leafDataEntries.get(currentDataEntry).recordIDList.size()) {
            currentDataEntry++;
            currentRecordID = 0;
        }
        if(currentDataEntry >= currentLeafNode.leafDataEntries.size()) {
            currentLeafNode = gotoNextLeafNode();
            if(currentLeafNode == null) {
                isDone = true;
                return null;
            }
            currentDataEntry = 0;
        }

        if(highKey != null && currentLeafNode.leafDataEntries.get(currentDataEntry).indexKey >= highKey) {
            isDone = true;
            return null;
        }
        return currentLeafNode.leafDataEntries.get(currentDataEntry).recordIDList.get(currentRecordID++);
    }


    /**
     * Returns the next leaf node read
     *
     * @return the next leaf node
     * @throws IOException
     */

    private LeafNode gotoNextLeafNode() throws IOException {
        currentLeafPageAddr++;
        if(currentLeafPageAddr > 0 && currentLeafPageAddr <= leafNodeCount) {
            return (LeafNode) generateNodeThroughAddress(currentLeafPageAddr);
        } else {
            return null;
        }
    }

    /**
     * Returns the number of leaf nodes.
     *
     * @return number of leaf nodes.
     */
    public int getLeafNodeCount() {
        return leafNodeCount;
    }

    /**
     * Dump the index file into human readable format.
     *
     * @param printer The printstream specified.
     */
    public void dump(PrintStream printer) {
        Queue<BPlusTreeNode> nodeQueue = new LinkedList<>();
        try {
            retrievePageforPageID(rootNodeAddr);
            IndexNode root = deserializeIndexNode();
            printer.println("Header Page info: tree has bTreeOrder " + order + ", a root at childNodeAddressList " + rootNodeAddr + " and " + leafNodeCount + " leaf nodes ");
            printer.println();
            printer.print("Root node is: ");
            printer.println(root);

            for (Integer addr : root.childNodeAddressList) {
                nodeQueue.offer(generateNodeThroughAddress(addr));
            }

            while (!nodeQueue.isEmpty()) {
                int size = nodeQueue.size();
                if (nodeQueue.peek() instanceof IndexNode) {
                    printer.println("---------Next layer is index nodes---------");
                } else {
                    printer.println("---------Next layer is leaf nodes---------");
                }
                for(int i = 0; i < size; i++) {
                    BPlusTreeNode curr = nodeQueue.poll();
                    printer.println(curr);
                    if (curr instanceof IndexNode) {
                        for (Integer addr : ((IndexNode) curr).childNodeAddressList) {
                            nodeQueue.offer(generateNodeThroughAddress(addr));
                        }
                    }
                }
            }

        } catch (IOException e) {
            if (logger.isDebugEnabled())
                e.printStackTrace();
        }

    }

    /**
     * This function deserializes the actual node at the given address.
     * and return a BPlusTreeNode
     *
     * @param nodeAddress the address for the that needs to be read
     * @return TreeNode based on the data read
     * @throws IOException
     */
    private BPlusTreeNode generateNodeThroughAddress(int nodeAddress) throws IOException {
        retrievePageforPageID(nodeAddress);
        // condition of leaf node.
        if(nodeAddress > 0 && nodeAddress <= leafNodeCount) {
            return deserializeLeafNode();
        } else {
            return deserializeIndexNode();
        }
    }

    /**
     * Deserialize the leaf node and return the generated LeafNode
     *
     * @return the LeafNode after deserialization
     */
    private LeafNode deserializeLeafNode() {
        int flag = byteBuffer.getInt();
        int numOfDEntries = byteBuffer.getInt();
        List<DataEntry> dataEntries = new ArrayList<>();
        for (int i = 0; i < numOfDEntries; i++) {
            int key = byteBuffer.getInt();
            int ridSize = byteBuffer.getInt();
            List<RecordId> rids = new ArrayList<>();
            for (int j = 0; j < ridSize; j++) {
                int pageId = byteBuffer.getInt();
                int tupleId = byteBuffer.getInt();
                rids.add(new RecordId(pageId, tupleId));
            }
            dataEntries.add(new DataEntry(key, rids));
        }
        return new LeafNode(order, dataEntries);
    }

    // deserialize the index node.
    private IndexNode deserializeIndexNode() {
        int flag = byteBuffer.getInt();
        int numOfKeys = byteBuffer.getInt();
        List<Integer> keys = new ArrayList<>();
        for(int i = 0; i < numOfKeys; i++) {
            keys.add(byteBuffer.getInt());
        }
        List<Integer> addresses = new ArrayList<>();
        for(int i = 0; i < numOfKeys + 1; i++) {
            addresses.add(byteBuffer.getInt());
        }
        return new IndexNode(order, keys, addresses);
    }

    /**
     * This function is used to retrieve the page specified at the given ID
     *
     * @param pageId PageID
     * @throws IOException
     */
    private void retrievePageforPageID(int pageId) throws IOException {
        eraseBuffer();
        long position = B_SIZE * (long) pageId;
        fileChannel.position(position);
        fileChannel.read(byteBuffer);
        byteBuffer.flip();
    }


    // read and store information in the head.
    private void readHeaderPage() throws IOException {
        retrievePageforPageID(0);
        rootNodeAddr = byteBuffer.getInt();
        leafNodeCount = byteBuffer.getInt();
        order = byteBuffer.getInt();
        eraseBuffer();
    }

    // Helper method that erases the byteBuffer by filling zeros.
    private void eraseBuffer() {
        byteBuffer.clear();
        byteBuffer.put(new byte[B_SIZE]);
        byteBuffer.clear();
    }


}
