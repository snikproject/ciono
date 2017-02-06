package org.cytoscape.cionw.internal.Algorithms.DataStructures;

/**
 * Class for storing 3 Values as a triple
 * <p>
 * Created by A. Zeiser on 29.11.2016.
 */
public class Triple {
    private String key;
    private String value1;
    private String value2;

    /**
     * Create a new Triple with (key, value1, value2)
     *
     * @param key    key value
     * @param value1 value 1
     * @param value2 value 2
     */
    public Triple(String key, String value1, String value2) {
        this.key = key;
        this.value1 = value1;
        this.value2 = value2;
    }

    /**
     * get the Key of the Triple
     *
     * @return Key
     */
    public String getKey() {
        return key;
    }

    /**
     * get the first Value of the Triple (not the Key!)
     * @return first Value
     */
    public String getValue1() {
        return value1;
    }

    /**
     * get the sec. Value of the Triple (the last Value)
     * @return sec. Value
     */
    public String getValue2() {
        return value2;
    }
}
