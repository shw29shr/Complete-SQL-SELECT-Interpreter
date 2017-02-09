package fileformats;

import models.Tuple;
import utils.PropertyFileReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Concrete implementation for TupleWriter To write output tuples as binary
 * files
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */

public class BinaryTupleWriter implements TupleWriter {
    PropertyFileReader reader = PropertyFileReader.getInstance();
    private int B_SIZE = Integer.parseInt(reader.getProperty("bufferSize"));
    private int INT_LENGTH = Integer.parseInt(reader.getProperty("variableSize"));
    private FileChannel fc;
    private ByteBuffer buffer;
    private int numberOfAttributes;
    private int numberOfTuples;
    private boolean topWriteCall;
    private int tupleLimit;
    private FileOutputStream fos;

    /**
     * Constructor of BinaryFileWriter
     *
     * @param filePath path where the output should be written to
     */
    public BinaryTupleWriter(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        fos = new FileOutputStream(file);
        fc = fos.getChannel();
        buffer = ByteBuffer.allocate(B_SIZE);
        topWriteCall = true;
        numberOfTuples = 0;
        //Vikas - CHECK WITH OLD

    }

    @Override
    public void dump(Tuple tuple) throws IOException {
        if (topWriteCall) {
            numberOfAttributes = tuple.length();
            buffer.putInt(numberOfAttributes);
            buffer.putInt(0);
            topWriteCall = false;
            tupleLimit = (B_SIZE - 8) / (INT_LENGTH * numberOfAttributes);
        }
        if (numberOfTuples < tupleLimit) {
            for (int i = 0; i < tuple.length(); i++) {
                buffer.putInt(tuple.getValue(i));
            }
            numberOfTuples++;
        } else {

            paddle(buffer);
            buffer.putInt(4, numberOfTuples);
            buffer.clear();
            fc.write(buffer);
            numberOfTuples = 0;
            topWriteCall = true;
            buffer.clear();
            buffer = buffer.put(new byte[B_SIZE]);
            buffer.clear();
            dump(tuple);
        }
    }

    /**
     * paddle zero to the end of the page
     * if there are still some spaces left
     *
     * @param buffer The buffer
     */
    public void paddle(ByteBuffer buffer) {
        while (buffer.hasRemaining()) {
            buffer.putInt(0);
        }
    }

    @Override
    public void close() throws IOException {
        paddle(buffer);
        buffer.putInt(4, numberOfTuples);
        buffer.clear();
        fc.write(buffer);
        fc.close();
        fos.close();


    }

}
