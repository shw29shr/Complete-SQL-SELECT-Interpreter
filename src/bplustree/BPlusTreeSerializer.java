package bplustree;

import bplustreecomponents.*;
import org.apache.log4j.Logger;
import utils.PropertyFileReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * This class serializes B+ tree into a file.
 *
 * @author Saarthak Chandra     sc2776
 *         Shweta Shrivastava   ss3646
 *         Vikas P Nelamangala	vpn6
 */
public class BPlusTreeSerializer {


    private static PropertyFileReader reader = PropertyFileReader.getInstance();
    private static final int B_SIZE = Integer.valueOf(reader.getProperty("bufferSize"));
    private static Logger logger = Logger.getLogger(BPlusTreeSerializer.class);
    private File indexFile;
    private FileChannel fileChannel;
    private ByteBuffer byteBuffer;
    private int pageNumber;
    private int leafNodesCounter;

    /**
     * Constructor of serializer
     *
     * @param indexFile file which has index data
     * @throws FileNotFoundException
     */

    protected BPlusTreeSerializer(File indexFile) throws FileNotFoundException {
        this.indexFile = indexFile;
        fileChannel = new FileOutputStream(indexFile).getChannel();
        byteBuffer = ByteBuffer.allocate(B_SIZE);
        pageNumber = 1;
        leafNodesCounter = 0;
    }

    /**
     * Serializes the current treeNode and returns the page number of the serialized file
     *
     * @param treeNode Node that needs to be serialized
     * @return the address of the treeNode in the index file .
     * @throws IOException
     */
    protected int serialize(BPlusTreeNode treeNode) throws IOException {
        long position = B_SIZE * (long) pageNumber;
        fileChannel.position(position);
        eraseBuffer();
        if(treeNode instanceof LeafNode) {
            leafNodesCounter++;
            LeafNode curr = (LeafNode) treeNode;
            int numOfEntries = curr.leafDataEntries.size();
            byteBuffer.putInt(0);
            byteBuffer.putInt(numOfEntries);
            for(DataEntry data : curr.leafDataEntries) {
                byteBuffer.putInt(data.indexKey);
                byteBuffer.putInt(data.recordIDList.size());
                for(RecordId rid : data.recordIDList) {
                    byteBuffer.putInt(rid.pageId);
                    byteBuffer.putInt(rid.tupleId);
                }
            }
        } else if(treeNode instanceof IndexNode) {
            IndexNode curr = (IndexNode) treeNode;
            int numOfKeys = curr.indexkeys.size();
            byteBuffer.putInt(1);
            byteBuffer.putInt(numOfKeys);
            for(Integer key : curr.indexkeys) {
                byteBuffer.putInt(key);
            }
            for(Integer addr : curr.childNodeAddressList) {
                byteBuffer.putInt(addr);
            }
        }
        while(byteBuffer.hasRemaining()) {
            byteBuffer.putInt(0);
        }
        byteBuffer.flip();
        fileChannel.write(byteBuffer);
        return pageNumber++;
    }

    /**
     * This function wraps up serialization by writing the header page
     *
     * @param bPlusTreeOrder Write the order fo the BPlus Tree
     */
    protected void wrapUpSerialization(int bPlusTreeOrder) {
        long position = 0L;

        try {
            fileChannel.position(position);
            eraseBuffer();
            byteBuffer.putInt(pageNumber - 1);
            byteBuffer.putInt(leafNodesCounter);
            byteBuffer.putInt(bPlusTreeOrder);
            while (byteBuffer.hasRemaining()) {
                byteBuffer.putInt(0);
            }
            byteBuffer.flip();
            fileChannel.write(byteBuffer);
        } catch (IOException e) {
            if(logger.isDebugEnabled())
                e.printStackTrace();
        }
    }

    /**
     * Close file channel
     *
     * @throws IOException
     */
    public void close() throws IOException {
        fileChannel.close();
    }

    /**
     * Clear the buffer
     */
    private void eraseBuffer() {
        byteBuffer.clear();
        byteBuffer.put(new byte[B_SIZE]);
        byteBuffer.clear();
    }
}
