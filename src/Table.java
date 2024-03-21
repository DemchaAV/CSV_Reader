import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Table extends CSV_Import {
    ConcurrentHashMap<String, List<String>> mapTable = new ConcurrentHashMap<>();
    private volatile boolean isReadingFinished = false; // Флаг завершения чтения
    private boolean autoWightStatus = false;
    private int amountLines;

    public void setOUT() {
        this.OUT = new Out(this);
        setHight();
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
            setHight();
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
        newTable.setOUT();
        return newTable;
    }

    public void deleteColumn(int number){
        mapTable.remove(titleKeys.get(number));
        OUT = new Out(this);


    }
    public void deleteLine(int number){
        for (int i = 0; i < titleKeys.size(); i++) {
            mapTable.get(titleKeys.get(i)).remove(number);
            amountLines = mapTable.get(titleKeys.get(i)).size();
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

    public <K extends String, V extends List> void print() {
        boolean titleStatus = true;
        List<String> list;
        if (titleStatus){
            OUT.print(titleKeys,true);
        }
        for (int i = 0; i < amountLines; i++) {
            list = getLine(i);
            OUT.print(list);
        }
        System.out.println();
    }

    public Table merridColumns(String wrapper, int... connect) {
        List<Integer> connectList = Arrays.stream(connect).boxed().collect(Collectors.toList());
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

            } else {
                outTable.putInMap(wrapper(line, wrapper));
            }
        }

        outTable.setOUT();
        return outTable;
    }

    private void setHight() {
        int outTable1 = 0;
        for (Map.Entry<String, List<String>> entery : this.mapTable.entrySet()) {
            outTable1 = entery.getValue().size();
            break;
        }
        amountLines = outTable1;

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
