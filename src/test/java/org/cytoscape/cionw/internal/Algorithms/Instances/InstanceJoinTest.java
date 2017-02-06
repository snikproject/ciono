package org.cytoscape.cionw.internal.Algorithms.Instances;

import org.cytoscape.cionw.internal.Algorithms.DataStructures.Triple;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Test Class for Instance Join
 *
 * Created by A. Zeiser on 29.11.2016.
 */
public class InstanceJoinTest {
    @Test
    public void join() throws Exception {
        // create InstanceTripleLists
        List<List<Triple>> instanceTripleList = new ArrayList<>();
        // first Relation table
        List<Triple> firstRelationList = new ArrayList<>();
        firstRelationList.add(new Triple("hasA0", "P0", "PL0"));
        firstRelationList.add(new Triple("hasA1", "P1", "PL0"));
        firstRelationList.add(new Triple("hasA2", "P2", "PL1"));
        firstRelationList.add(new Triple("hasA3", "P3", "PL1"));
        instanceTripleList.add(firstRelationList);
        // second Relation table
        List<Triple> secRelationList = new ArrayList<>();
        secRelationList.add(new Triple("useA0", "PL0", "A0"));
        secRelationList.add(new Triple("useA0", "PL0", "A1"));
        secRelationList.add(new Triple("useA0", "PL0", "A2"));
        secRelationList.add(new Triple("useA0", "PL0", "A3"));
        secRelationList.add(new Triple("useA0", "PL0", "A4"));
        secRelationList.add(new Triple("useA0", "PL0", "A5"));
        secRelationList.add(new Triple("useA0", "PL1", "A6"));
        secRelationList.add(new Triple("useA0", "PL1", "A0"));
        instanceTripleList.add(secRelationList);

        // join all Instances in instanceTripleList
        InstanceJoin iJ = new InstanceJoin();
        List<List<String>> joinedList = iJ.join(instanceTripleList);

        // create reference joinedList
        List<List<String>> expectedJoinedList = new ArrayList<>();
        // join over P0
        expectedJoinedList.add(new ArrayList<>(Arrays.asList("P0", "PL0", "A0")));
        expectedJoinedList.add(new ArrayList<>(Arrays.asList("P0", "PL0", "A1")));
        expectedJoinedList.add(new ArrayList<>(Arrays.asList("P0", "PL0", "A2")));
        expectedJoinedList.add(new ArrayList<>(Arrays.asList("P0", "PL0", "A3")));
        expectedJoinedList.add(new ArrayList<>(Arrays.asList("P0", "PL0", "A4")));
        expectedJoinedList.add(new ArrayList<>(Arrays.asList("P0", "PL0", "A5")));
        // join over P1
        expectedJoinedList.add(new ArrayList<>(Arrays.asList("P1", "PL0", "A0")));
        expectedJoinedList.add(new ArrayList<>(Arrays.asList("P1", "PL0", "A1")));
        expectedJoinedList.add(new ArrayList<>(Arrays.asList("P1", "PL0", "A2")));
        expectedJoinedList.add(new ArrayList<>(Arrays.asList("P1", "PL0", "A3")));
        expectedJoinedList.add(new ArrayList<>(Arrays.asList("P1", "PL0", "A4")));
        expectedJoinedList.add(new ArrayList<>(Arrays.asList("P1", "PL0", "A5")));
        // join over P2
        expectedJoinedList.add(new ArrayList<>(Arrays.asList("P2", "PL1", "A6")));
        expectedJoinedList.add(new ArrayList<>(Arrays.asList("P2", "PL1", "A0")));
        // join over P3
        expectedJoinedList.add(new ArrayList<>(Arrays.asList("P3", "PL1", "A6")));
        expectedJoinedList.add(new ArrayList<>(Arrays.asList("P3", "PL1", "A0")));
        // compare
        Assert.assertTrue("InstanceJoin is wrong", joinedList.containsAll(expectedJoinedList) && expectedJoinedList.containsAll(joinedList));
    }

}