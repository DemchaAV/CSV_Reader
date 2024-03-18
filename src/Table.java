import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Table extends CSV_Import {
    ConcurrentHashMap<String, List<String>> mapTable = new ConcurrentHashMap<>();
    private volatile boolean isReadingFinished = false; // Flag indicating whether reading is finished
    private boolean autoWightStatus = false; // Status of automatic width calculation
    private int amountLines; // Number of lines in the table
    private  Out OUT; // Output formatter

    // Constructor with input file path
    public Table(String inPath) {
        reader(inPath); // Read the CSV file
        OUT = new Out(mapTable, titleKeys); // Initialize output formatter
    }

    // Default constructor
    public Table() {
    }

    // Method to read CSV file
    private void reader(String inPath) {
        FileReader file;
        try {
            file = new FileReader(inPath);
        } catch (FileNotFoundException e) {
            String nameFile = inPath.substring(inPath.lastIndexOf('/') + 1);
            isReadingFinished = true;
            // Throw RuntimeException if file not found
            throw new RuntimeException(e + "\nYour file " + nameFile + " does not exist or is not current." +
                    "\nDownload your new favorite file from: https://www.reverso.net/favorites/ or check your inPath and fileName");
        }
        try (BufferedReader br = new BufferedReader(file)) {
            String line;
            boolean titleStatus = true;
            while ((line = br.readLine()) != null) {
                if (titleStatus) {
                    titleStatus = isTitleStatus(line); // Check if line contains titles
                    setKeys(); // Set column keys
                } else {
                    putInMap(line); // Add data to the map
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Get the number of lines in the table
            for (Map.Entry<String, List<String>> entry : mapTable.entrySet()) {
                amountLines = entry.getValue().size();
                break;
            }
            isReadingFinished = true; // Indicate reading is finished
        }
    }

    // Method to set column keys
    private void setKeys() {
        if (titleKeys != null) {
            for (int i = 0; i < titleKeys.size(); i++) {
                mapTable.put(titleKeys.get(i), new ArrayList<>()); // Initialize columns in the map
            }
        }
    }

    // Method to get data from a specific column
    public List<String> getColumns(int columnNumber) {
        return mapTable.get(titleKeys.get(columnNumber));
    }

    // Method to get data from a specific line
    public List<String> getLine(int lineNumber) {
        List<String> line = new ArrayList<>();
        for (int i = 0; i < titleKeys.size(); i++) {
            if(mapTable.get(titleKeys.get(i)).get(lineNumber) == null) {
                line.add("");
            }
            line.add(mapTable.get(titleKeys.get(i)).get(lineNumber));
        }
        return line;
    }

    // Method to delete a column
    public void deleteColumn(int number){
        mapTable.remove(titleKeys.get(number));
    }

    // Method to delete a line
    public void deleteLine(int number){
        for (int i = 0; i < titleKeys.size(); i++) {
            mapTable.get(titleKeys.get(i)).remove(number);
            amountLines = mapTable.get(titleKeys.get(i)).size();
        }
    }

    // Method to put data into the map
    private void putInMap(String line) {
        List<String> values = parseCsvLine(line); // Parse the CSV line
        for (int i = 0; i < titleKeys.size(); i++) {
            mapTable.get(titleKeys.get(i)).add(values.get(i)); // Add values to the map
        }
    }

    // Method to print the table
    public <K extends String, V extends List> void print() {
        boolean titleStatus = true;
        List<String> list;
        if (titleStatus){
            OUT.print(titleKeys,true); // Print column titles
        }
        // Print each line of the table
        for (int i = 0; i < amountLines; i++) {
            list = getLine(i);
            OUT.print(list); // Print the line
        }
    }

    // Method to transfer columns to a new table
    public Table transferColumn(List<Integer> connectList) {
        Table table = new Table(); // Create a new table
        List<String> currentLine;
        table.setTitleKeys(titleKeys);
        // Remove columns specified in connectList
        for (int j = 1; j < connectList.size(); j++) {
            table.titleKeys.remove(connectList.get(j));
        }
        table.setKeys(); // Set keys for the new table
        // Transform each line in the current table and add to the new table
        for (int i = 0; i < amountLines; i++) {
            currentLine = getLine(i);
            currentLine = new Connector(currentLine, connectList, null).transformList(); // Transform the line
            table.setLine(currentLine); // Add the line to the new table
        }
        table.OUT = new Out(mapTable, titleKeys); // Initialize output formatter
        return table;
    }

    // Method to set a line in the table
    void setLine(List<String> line){
        for (int i = 0; i < line.size(); i++) {
            mapTable.get(titleKeys.get(i)).add(line.get(i)); // Add each value to the respective column
        }
    }

    // Method to write data to a file
    public <K extends String, V extends List> void write(Map<K, V> mapCSV, String outputPath, char separator) {
        BufferedWriter bw;
        Set<K> set = new LinkedHashSet<>(mapCSV.keySet());
        List<K> keys = set.stream().toList();
        try {
            bw = new BufferedWriter(new FileWriter(outputPath)); // Open file for writing
            if (mapCSV != null) {
                // Write each line of data to the file
                for (int i = 0; i < mapCSV.get(keys.get(0)).size(); i++) {
                    StringBuilder line = new StringBuilder();
                    for (int j = 0; j < mapCSV.size(); j++) {
                        String word = (String) mapCSV.get(keys.get(j)).get(i);
                        line.append(word);
                        if (j < mapCSV.size() - 1) {
                            line.append(separator); // Add separator between values
                        }
                    }
                    bw.write(line.toString() + separator + "\n"); // Write line to file
                }
                bw.close(); // Close the file
            }
        } catch (IOException e) {
            throw new RuntimeException(e); // Throw RuntimeException if an error occurs
        }
    }
}
