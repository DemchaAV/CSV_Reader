import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Table extends CSV_Import implements Printable {
    ConcurrentHashMap<String, List<String>> mapTable = new ConcurrentHashMap<>();
    private volatile boolean isReadingFinished = false; // Флаг завершения чтения
    private boolean autoWightStatus = false;
    private String currentCell;
    static String currentReversoDateFile = "Favorites_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv";

    private int amountLines;

    public void updatePrintData() {
        this.OUT = new Out(this);
        setHeight();
    }

    private Out OUT;

    public Table(String inPath) {
        reader(inPath);
        OUT = new Out(this);
    }

    private Table() {

    }
    private void reader(String inPath) {
        FileReader file;
        try {
            file = new FileReader(inPath);
        } catch (FileNotFoundException e) {
            String nameFile = inPath.substring(inPath.lastIndexOf('/') + 1);
            isReadingFinished = true;
            throw new RuntimeException(e + "\nYour file " + nameFile + " is not exist, or not current date" +
                    "\nDownload yor new file favorite from: " + "https://www.reverso.net/favorites/  or check your inPath and fileName");
        }
        try (
                BufferedReader br = new BufferedReader(file)) {
            String line;
            boolean titleStatus = true;
            while ((line = br.readLine()) != null) {
                if (titleStatus) {
                    titleStatus = isTitleStatus(line);
                    setKeys();
                } else {
                    putInMap(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            setHeight();
            isReadingFinished = true; //make sure it is done
        }
    }

    private void setKeys() {
        if (titleKeys != null) {
            for (int i = 0; i < titleKeys.size(); i++) {
                mapTable.put(titleKeys.get(i), new ArrayList<>());
            }
        }
    }
    public List<String> getColumns(int columnNumber) {
        return mapTable.get(titleKeys.get(columnNumber));

    }
    public List<String> getLine(int numberLine) {
        List<String> line = new ArrayList<>();
        for (int i = 0; i < titleKeys.size(); i++) {
            if(mapTable.get(titleKeys.get(i)).get(numberLine)==null) {
                line.add("");
            }
            line.add(mapTable.get(titleKeys.get(i)).get(numberLine));
        }
        return line;
    }

    private String getLineAsString(int numberLine, char separator) {
        if (numberLine >= amountLines || numberLine < 0) {
            return "";
        }

        StringBuilder lineBuilder = new StringBuilder();
        for (int i = 0; i < titleKeys.size(); i++) {
            List<String> column = mapTable.get(titleKeys.get(i));
            if (column != null && numberLine < column.size()) {
                lineBuilder.append(column.get(numberLine));
            }
            if (i < titleKeys.size() - 1) {
                lineBuilder.append(separator);
            }
        }

        return lineBuilder.toString();
    }

    public Table reduceColumns(int... columns) {
        return reduceColumns(Arrays.stream(columns).boxed().collect(Collectors.toList()));

    }

    public Table reduceColumns(List<Integer> columns) {
        columns = adjustIndexes(columns, -1);
        List<String> newOrderList = new ArrayList<>();
        Table newTable = new Table();
        newTable.titleKeys = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) {
            newTable.titleKeys.add(this.titleKeys.get(columns.get(i)));
        }
        newTable.setKeys();
        String kurkey;
        for (int i = 0; i < newTable.titleKeys.size(); i++) {
            kurkey = newTable.titleKeys.get(i);
            newTable.mapTable.put(kurkey, new ArrayList<>(this.mapTable.get(kurkey)));
        }
        newTable.updatePrintData();
        return newTable;
    }

    public boolean deleteColumn(int number) {
        //adjust index supposed to start from 1
        --number;
        if (number >= 0 && number < titleKeys.size()) {
            mapTable.remove(titleKeys.remove(number)); // Удалить ключ и соответствующий столбец
            updatePrintData();
            return true;
        }
        return false;
    }

    /**
     * This method provides capability to insert new column for our document
     *
     * @param position this value should be positive the inserting position after the current number
     * @param titleKey - the unique name for tittle
     * @return statust of insert may be more than 0 and not out of bound of table
     */
    public boolean insertColumn(int position, String titleKey) {
        if (position < 0 || position > titleKeys.size()) {
            return false;
        }
        titleKeys.add(position, titleKey);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < amountLines; i++) {
            list.add("");
        }
        mapTable.put(titleKey, list);
        updatePrintData();
        return true;
    }

    public boolean sortColumns(int... order) {
        return sortColumns(Arrays.stream(order).boxed().toList());
    }
    public boolean sortColumns(Comparator<String> comparator){
        titleKeys = titleKeys.stream().sorted(comparator).collect(Collectors.toList());
        return true;
    }
    public boolean sortColumns(){
        titleKeys = titleKeys.stream().sorted().collect(Collectors.toList());
        updatePrintData();
        return true;
    }

    public boolean sortColumns(List<Integer> order) {
        List<String> newTittleList = new ArrayList<>();
        order = adjustIndexes(order, -1);
        if (!checkKeys(order)) {
            return false;
        }
        for (int i = 0; i < order.size(); i++) {
            newTittleList.add(titleKeys.get(order.get(i)));
        }
        titleKeys = newTittleList;
        updatePrintData();
        return true;
    }

    private boolean checkKeys(int... keys) {
        return checkKeys(Arrays.stream(keys).boxed().toList());
    }

    private boolean checkKeys(List<Integer> keys) {
        for (int i = 0; i < keys.size(); i++) {
            int key = keys.get(i);
            if (key > titleKeys.size() || key < 0) {
                return false;
            }
        }
        return true;
    }
    public boolean deleteLine(int number) {
        --number;
        if (number < amountLines && number >= 0) {
        for (int i = 0; i < titleKeys.size(); i++) {
            mapTable.get(titleKeys.get(i)).remove(number);
        }
            amountLines--;
            updatePrintData();
            return true;
        } else {
            return false;
        }
    }

    private void putInMap(String line) {
        List<String> values;
        values = parseCsvLine(line);
        putInMap(values);
    }

    private void putInMap(List<String> values) {
        for (int i = 0; i < titleKeys.size(); i++) {
            mapTable.get(titleKeys.get(i)).add(values.get(i));

        }
    }

    public void print() {
        print(0, amountLines);
    }

    public void print(int from, int till) {
        if (from < 0 || till > amountLines) {
            if (from < 0) {
                System.out.println("Мalue must be greater than zero!" + "The current start line is: " + from);
            }
            if (till > amountLines) {
                System.out.println("The value must be lower then amount of lines! Your current till value is: " + till);
            }
            return;
        }
        boolean titleStatus = true;
        List<String> list;
        if (titleStatus) {
            System.out.print(OUT.print(titleKeys, true));
        }
        for (int i = from; i < till; i++) {
            list = getLine(i);
            System.out.print(OUT.print(list));
        }
        System.out.println();
    }

    /**
     * This method provides a way to write your table with as a table view
     *
     * @param pathOut - pathOut for our document to save
     * @throws IOException
     */
    public void writeTable(String pathOut) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(pathOut));
        boolean titleStatus = true;
        List<String> list;
        if (titleStatus) {
            writer.write(OUT.print(titleKeys, true));
            writer.newLine();
        }
        for (int i = 0; i < amountLines; i++) {
            list = getLine(i);
            writer.write(OUT.print(list));
            writer.newLine();
        }
    }

    private void write() {

    }

    public Table merridColumns(String wrapper, int... connect) {
        List<Integer> connectList = Arrays.stream(connect).boxed().collect(Collectors.toList());
        connectList = adjustIndexes(connectList,-1);
        List<String> line;
        Table outTable = new Table();
        boolean titleStatus = true;
        Connector connector;
        for (int i = 0; i < this.amountLines; i++) {
            connector = new Connector(this.getLine(i), connectList);
            line = connector.connectAsList(this.getLine(i), connectList);
            if (titleStatus) {
                titleStatus = outTable.isTitleStatus(connector.connectAsList(this.titleKeys, connectList));
                outTable.setKeys();
                outTable.putInMap(wrapper(line, wrapper));

            } else {
                outTable.putInMap(wrapper(line, wrapper));
            }
        }

        outTable.updatePrintData();
        return outTable;
    }

    private void setHeight() {
        if (!mapTable.isEmpty()) {
            int firstKeyListSize = mapTable.entrySet().iterator().next().getValue().size();
            amountLines = firstKeyListSize;
        }
    }

    public Table merridColumns(int... connect) {
        return merridColumns(null, connect);
    }

    public void wrap(String symbol) {
        List<String> newList;
        for (Map.Entry<String, List<String>> entry : mapTable.entrySet()) {
            newList = entry.getValue().stream().map((x -> wrapper(x, symbol))).collect(Collectors.toList());
            mapTable.put(entry.getKey(), newList);
        }
    }

    private List<String> wrapper(List<String> inList, String symbol) {
        if (symbol == null || symbol.length() == 0) {
            return inList;
        }
        List<String> outList = new ArrayList<>();
        for (String cell : inList) {
            outList.add(wrapper(cell, symbol));
        }
        return outList;
    }

    private String wrapper(String cell, String symbol) {
        if (cell == null || symbol == null) {
            return cell;
        }
        return symbol + cell + symbol;

    }


    public void write(String outputPath, char separator) {
        try (FileWriter fileWriter = new FileWriter(outputPath);
             BufferedWriter bw = new BufferedWriter(fileWriter)) {
            if (this.mapTable != null) {
                String line;
                for (int i = 0; i < amountLines; i++) {
                    line = getLineAsString(i, separator);
                    bw.write(line);
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error with file operations", e);
        }
    }

    private List<Integer> adjustIndexes(List<Integer> connectColumns, int index) {
        return connectColumns.stream().map(x -> x + index).collect(Collectors.toList());
    }
}
