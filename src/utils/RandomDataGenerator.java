package utils;

import fileformats.BinaryTupleWriter;
import fileformats.FileTupleWriter;
import fileformats.TupleWriter;
import models.Tuple;
import java.io.IOException;
import java.util.Random;

/**
 * This class is used for generating randoms tuples with values
 * in specific range
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */
public class RandomDataGenerator {
    /**
     * @param filePath:           the path where file will be generated
     * @param numberOfTuples:     number of tuples need to be generated
     * @param valueRange:         the maximum tuple value
     * @param numberOfAttributes: number of attributes
     * @param isBinary:           true for binary, false for human readable format
     */
    private static void dataGenerator(String filePath, int numberOfAttributes, int valueRange, int numberOfTuples, Boolean isBinary) throws IOException {
        TupleWriter tw;
        if (isBinary) {
            tw = new BinaryTupleWriter(filePath);
        } else {
            tw = new FileTupleWriter(filePath);
        }
        Random r = new Random();
        int rowcounter = 0;
        while (rowcounter < numberOfTuples) {
            int counter = 0;
            int[] columnValueHolder = new int[numberOfAttributes];
            while (counter < numberOfAttributes) {
                columnValueHolder[counter] = r.nextInt(valueRange);
                counter++;
            }
            Tuple t = new Tuple(columnValueHolder);
            tw.dump(t);
            rowcounter++;
        }
        tw.close();

    }

//    public static void main(String[] args) throws IOException {
//        // Binary or Human Readable
//        boolean isBinary = true;
//        int valueRange = 10000;
//        int numberOfTuples = 10000;
//        RandomDataGenerator.dataGenerator("samples_perf_testing/input/db/data/Boats", 3, valueRange, numberOfTuples, isBinary);
//        RandomDataGenerator.dataGenerator("samples_perf_testing/input/db/data/Sailors", 3, valueRange, numberOfTuples, isBinary);
//        RandomDataGenerator.dataGenerator("samples_perf_testing/input/db/data/Reserves", 2, valueRange, numberOfTuples, isBinary);
//    }
}

