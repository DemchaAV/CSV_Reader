package test.java;

import main.table.Connector;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConnectorTest {

    @Test
    void connectAsList() {
        Connector connector = new Connector();
        List<String> testList = new ArrayList<>();
        testList.add("odin");
        testList.add("dwa");
        testList.add("trie");
        testList.add("chetire");
        assertEquals(new ArrayList<String>( Arrays.asList("odin","dwa, chetire","trie")),connector.connectAsList(testList, 1,3) );

    }

    @Test
    void testConnectAsList() {
    }
}