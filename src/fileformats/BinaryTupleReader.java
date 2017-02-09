package fileformats;

import bplustreecomponents.RecordId;
import models.Tuple;
import utils.PropertyFileReader;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The BinaryTupleReader class for reading
 * tuples from a file of binary format representing the tuples.
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */
public final class BinaryTupleReader implements TupleReader {
    PropertyFileReader reader = PropertyFileReader.getInstance();
    private int B_SIZE = Integer.parseInt(reader.getProperty("bufferSize"));
    private int INT_LENGTH = Integer.parseInt(reader.getProperty("variableSize"));
    private File file;
    private FileChannel fc;
    private ByteBuffer buffer;
    private int attributeCount;
    private int totalTuples;
    private long currentTuplePosition;
    private boolean requireNewPage;
    private boolean fileEnding;
    private List<Long> offsets;

    /**
     * Constructor
     *
     * @param file File to read
     */
    public BinaryTupleReader(File file) throws FileNotFoundException {
        this.file = file;
        fc = new FileInputStream(file).getChannel();
        buffer = ByteBuffer.allocate(B_SIZE);
        fileEnding = false;
        requireNewPage = true;
        offsets = new ArrayList<Long>();
        offsets.add(new Long(0));
        currentTuplePosition = 0;
    }

    /**
     * Creates a new BinaryTupleReader, given the file name
     *
     * @param fileName the name of the file to read
     */
    public BinaryTupleReader(String fileName) throws FileNotFoundException {
        this(new File(fileName));
    }

    /**
     * read the next tuple from the table.
     *
     * @return Tuple nextTuple to be returned.
     */
    @Override
    public Tuple read() throws IOException {
        while(!fileEnding) {
            if(requireNewPage) {
                try {
                    retreivePage();
                } catch (EOFException e) {
                    break;
                }
            }

            if(buffer.hasRemaining()) {
                int[] columns = new int[attributeCount];
                for (int i = 0; i < attributeCount; i++) {
                    columns[i] = buffer.getInt();
                }
                currentTuplePosition++;
                return new Tuple(columns);
            }

            eraseBuffer();
            requireNewPage = true;
        }

        return null;
    }

    @Override
    public Tuple read(RecordId recordID) throws IOException {

        if(recordID == null) return null;
        int pageId = recordID.getPageId();
        int tupleId = recordID.getTupleId();

        if(pageId < 0 || tupleId < 0) {
            throw new IndexOutOfBoundsException("Index out of bound");
        }
        eraseBuffer();
        requireNewPage = true;
        fileEnding = false;
        try {
            retreivePage(pageId);
        } catch (EOFException e) {
            e.printStackTrace();
        }
        int newPosition = (tupleId * attributeCount + 2) * INT_LENGTH;
        buffer.position(newPosition);
        return read();
    }


    /**
     * return a list of tuples contained in the current page of a file
     *
     * @return
     */
    public ArrayList<Tuple> getNextPage() throws IOException {
        ArrayList<Tuple> tuplesArray = new ArrayList<Tuple>();
        while(!fileEnding) {
            if(requireNewPage) {
                try {
                    retreivePage();
                } catch (EOFException e) {
                    break;
                }
            }
            while(buffer.hasRemaining()) {
                int[] cols = new int[attributeCount];
                for (int i = 0; i < attributeCount; i++) {
                    cols[i] = buffer.getInt();
                }
                currentTuplePosition++;
                tuplesArray.add(new Tuple(cols));
            }
            eraseBuffer();
            requireNewPage = true;
            return tuplesArray;
        }
        return null;
    }

    /**
     * Resets the reader to the specified tuple index.
     *
     * @param index the tuple index.
     */
    @Override
    public void reset(long index) throws IOException, IndexOutOfBoundsException {

        if(index >= currentTuplePosition || index < 0) {
            throw new IndexOutOfBoundsException("The index is too large");
        }
        int pageIndex = Collections.binarySearch(offsets, new Long(index + 1));
        pageIndex = pageIndex >= 0 ? pageIndex : -(pageIndex + 1);

        fc.position((long) (pageIndex - 1) * B_SIZE);


        eraseBuffer();
        requireNewPage = true;
        fileEnding = false;
        offsets = offsets.subList(0, pageIndex);
        currentTuplePosition = index;
        long numTuplesBuffered = offsets.get(offsets.size() - 1);


        int newTupleOffset = (int) (index - numTuplesBuffered);
        int newPosition = (newTupleOffset * attributeCount + 2) * INT_LENGTH;


        try{
            retreivePage();
        } catch (EOFException e) {
            e.printStackTrace();
        }

        buffer.position(newPosition);
    }


    /**
     * Resets the reader to the beginning of the file.
     *
     * @throws IOException If an I/O error occurs while calling the underlying
     *                     reader's read method
     */
    @Override
    public void reset() throws IOException {
        close();
        fc = new FileInputStream(file).getChannel();
        buffer = ByteBuffer.allocate(B_SIZE);
        fileEnding = false;
        requireNewPage = true;
        offsets = new ArrayList<Long>();
        offsets.add(new Long(0));
        currentTuplePosition = 0;
    }

    /**
     * closes the target
     *
     * @throws IOException If an I/O error occurs while calling the underlying
     *                     reader's close method
     */
    @Override
    public void close() throws IOException {
        fc.close();
    }


    private void retreivePage(int pageId) throws IOException {
        long position = B_SIZE * (long) pageId;
        fc.position(position);
        retreivePage();
    }

    private void retreivePage() throws IOException {
        fileEnding = (fc.read(buffer) < 0);
        requireNewPage = false;

        if(fileEnding) throw new EOFException();

        buffer.flip();
        attributeCount = buffer.getInt();
        totalTuples = buffer.getInt();
        offsets.add(offsets.get(offsets.size() - 1) + totalTuples);
        buffer.limit((attributeCount * totalTuples + 2) * INT_LENGTH);
    }


    private void eraseBuffer() {
        buffer.clear();
        buffer.put(new byte[B_SIZE]);
        buffer.clear();
    }

    // Helper method that dumps the file to the System.out for ease of debugging
    private void dump() throws IOException {
        while (fc.read(buffer) > 0) {
            buffer.flip();

            int attribute = buffer.getInt();
            int tuples = buffer.getInt();
            int column = 0;
            buffer.limit((attribute * tuples + 2) * INT_LENGTH);
            while (buffer.hasRemaining()) {
                column++;
                if (column == attribute) {
                    System.out.println();
                    column = 0;
                } else {
                    System.out.print(",");
                }
            }

            buffer.clear();
            buffer.put(new byte[B_SIZE]);
            buffer.clear();
            System.out.println();

        }

    }

    @Override
    public Long getIndex() throws IOException {
        return currentTuplePosition;
    }


}
