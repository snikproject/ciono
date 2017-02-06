package org.cytoscape.cionw.internal.Algorithms.Instances;

import com.opencsv.CSVReader;
import javafx.util.Pair;
import org.cytoscape.cionw.internal.Algorithms.DataStructures.Triple;
import org.slf4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for reading instance tables. If switched to a other saving system only this class have to be reprogrammed.
 * <p>
 * Created by A. Zeiser on 21.11.2016.
 */
public class InstanceReader {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(InstanceReader.class);
    private static CSVReader csvReader;

    /**
     * Default Constructor
     */
    private InstanceReader() {
        csvReader = null;
    }

    /**
     * Read all Instances of a given File as a Key Value Pair.
     *
     * @param filePath Path to the CSV file
     * @return List of K-V Pairs
     */
    public static List<Pair> getInstancesPair(String filePath) {
        List<Pair> result = new ArrayList<>();
        try {
            FileReader fReader = new FileReader(filePath);
            csvReader = new CSVReader(fReader);
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                //noinspection unchecked
                result.add(new Pair(line[0], line[1]));
            }
        } catch (IOException e) {
            LOGGER.info("Can't find File: " + filePath + " while try to read instance table");
            return new ArrayList<>();
        }
        return result;
    }

    /**
     * Read all Instances of a given File with Key, First Value, Sec. Value
     *
     * @param filePath Path to the CSV file
     * @param inverse  Switch firstValue with secValue if set to true
     * @return List of Triples: iD, firstValue, secValue if inverse = false, else iD, secValue, firstValue
     */
    public static List<Triple> getInstancesTriple(String filePath, boolean inverse) {
        List<Triple> result = new ArrayList<>();
        try {
            FileReader fReader = new FileReader(filePath);
            csvReader = new CSVReader(fReader);
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                if (!inverse) {
                    //noinspection unchecked
                    result.add(new Triple(line[0], line[1], line[2]));
                } else {
                    //noinspection unchecked
                    result.add(new Triple(line[0], line[2], line[1]));
                }
            }
        } catch (IOException e) {
            LOGGER.info("Can't find File: " + filePath + " while try to read instance table");
            return new ArrayList<>();
        }
        return result;
    }
}
