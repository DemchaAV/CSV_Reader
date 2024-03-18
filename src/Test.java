import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {

        // Sample list of strings
        List<String> arl = new ArrayList<>();
        arl.add("hello");
        arl.add("ok");
        arl.add("Poka");
        arl.add("good bay");
        arl.add("weather");
        arl.add("chip");

        // Sample list of integers
        List<Integer> listInt = new ArrayList<>();
        listInt.add(2);
        listInt.add(6);
        listInt.add(3);
        listInt.add(1);

        List<Integer> listInt2 = new ArrayList<>();
        listInt2.add(9);
        listInt2.add(1);

        // Sample path and file names
        String path = "C:/Users/Demch/OneDrive/Рабочий стол/English learning/";
        String curDateFormat = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String nameFile = "Favorites_" + curDateFormat + ".csv";
        String newFileName = "reWords_Transfer " + LocalDate.now() + ".csv";

        // Sample list of columns
        List<Integer> columns = new ArrayList<>();
        columns.add(2);
        columns.add(1);

        // Test list for Connector class
        List<String> testList = new ArrayList<>();
        testList.add("One");
        testList.add("Two");
        testList.add("Three");
        testList.add("Four");
        testList.add("Five");

        // Create a Connector object
        Connector connector = new Connector(testList, columns, null);

        // Transform the list using the Connector object
        List<String> out = connector.transformList(testList, columns, "\"");

        // Output the results
        System.out.println("Original List: " + testList);
        System.out.println("Transformed List: " + out);
        System.out.println("Done List: " + connector.getDoneList());
        System.out.println("Key Set: " + connector.getKeySet());
    }
}
