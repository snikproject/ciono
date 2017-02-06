package org.cytoscape.cionw.internal.Utils;

import org.cytoscape.property.AbstractConfigDirPropsReader;

/**
 * Simple class for a property reader.
 * <p>
 * Created by A. Zeiser on 03.12.2016.
 */
public class PropertyReader extends AbstractConfigDirPropsReader {
    /**
     * Default Constructor
     *
     * @param name         Name of the Properties (e.g. cionw)
     * @param propFileName name of the Property File
     */
    public PropertyReader(String name, String propFileName) {
        super(name, propFileName, SavePolicy.SESSION_FILE_AND_CONFIG_DIR);
    }
}
