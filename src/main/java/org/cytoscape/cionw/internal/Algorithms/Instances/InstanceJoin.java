package org.cytoscape.cionw.internal.Algorithms.Instances;

import org.cytoscape.cionw.internal.Algorithms.DataStructures.Triple;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * Class for Joining Instances from a List of InstanceTriple
 * <p>
 * Created by A. Zeiser on 29.11.2016.
 */
public class InstanceJoin {
    /**
     * Perform natural join on instanceTripleList
     *
     * @param instanceTripleList list of list of Triples (from the relation instance tables)
     * @return list of all ID in a row if join was possible. List is empty if a Join was not possible
     */
    public List<List<String>> join(List<List<Triple>> instanceTripleList) {
        List<List<String>> joinedList = new ArrayList<>();
        // iterate over every List<Triple>
        for (int i = 0; i < instanceTripleList.size(); i++) {
            if (i != 0) {
                List<List<String>> newJoinedList = new ArrayList<>();
                // check which List is bigger (instanceTripleList vs joinedList and iterate over the bigger one
                if (instanceTripleList.get(i).size() > joinedList.size()) {
                    // iterate over every Triple
                    for (Triple triple : instanceTripleList.get(i)) {
                        String firstKeyId = triple.getValue1();
                        String secKeyId = triple.getValue2();
                        // if triple Value1 occurs in any entry of currentJoinList[last element]
                        // then add new list with currentJoinList[lastElement] + Value1
                        for (List<String> currentJoin : joinedList) {
                            List<String> temps = tryJoinPair(currentJoin, firstKeyId, secKeyId);
                            if (temps != null && !temps.contains(null)) {
                                newJoinedList.add(tryJoinPair(currentJoin, firstKeyId, secKeyId));
                            }
                        }
                    }
                    if (!newJoinedList.isEmpty()) {
                        joinedList = newJoinedList;
                        joinedList = removeDuplicates(joinedList);
                    }
                } else {
                    // iterate over joinedList
                    for (List<String> list : joinedList) {
                        for (Triple triple : instanceTripleList.get(i)) {
                            String firstKeyId = triple.getValue1();
                            String secKeyId = triple.getValue2();
                            List<String> temps = tryJoinPair(list, firstKeyId, secKeyId);
                            if (temps != null && !temps.contains(null)) {
                                newJoinedList.add(tryJoinPair(list, firstKeyId, secKeyId));
                            }
                        }
                    }
                    if (!newJoinedList.isEmpty()) {
                        joinedList = newJoinedList;
                        joinedList = removeDuplicates(joinedList);
                    }
                }
            }
            // first iteration case (just perform a copy)
            else {
                for (Triple triple : instanceTripleList.get(i)) {
                    String firstKeyId = triple.getValue1();
                    String secKeyId = triple.getValue2();
                    List<String> tempList = new ArrayList<>();
                    tempList.add(firstKeyId);
                    tempList.add(secKeyId);
                    joinedList.add(tempList);
                }
            }
        }
        return joinedList;
    }

    /**
     * Remove duplicate entries from List of Lists
     *
     * @param joinedList List where duplicates gets removed
     * @return List of List without duplicates
     */
    private List<List<String>> removeDuplicates(List<List<String>> joinedList) {
        HashSet<List<String>> tempHash = new HashSet<>();
        tempHash.addAll(joinedList);
        joinedList.clear();
        joinedList.addAll(tempHash);
        return joinedList;
    }

    /**
     * Joins two Lists depending on there matching values (last or sec. last value from first list and either firstKeyId or secKeyId)
     *
     * @param currentJoin List of String, where the last or sec. last value will be matched with firstKeyId or secKeyId
     * @param firstKeyId  Matching String
     * @param secKeyId    Matching String
     * @return List of Strings: currentJoin extended with either first or sec KeyId
     */
    private List<String> tryJoinPair(List<String> currentJoin, String firstKeyId, String secKeyId) {
        List<String> tempList = new ArrayList<>();
        if (Objects.equals(currentJoin.get(currentJoin.size() - 1), firstKeyId)) {
            tempList.addAll(currentJoin);
            tempList.add(secKeyId);
            return tempList;
        } else if (Objects.equals(currentJoin.get(currentJoin.size() - 2), secKeyId)) {
            tempList.addAll(currentJoin);
            tempList.add(firstKeyId);
            return tempList;
        }
        return null;
    }
}
