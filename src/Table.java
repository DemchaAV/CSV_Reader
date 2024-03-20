import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Table extends CSV_Import {
    ConcurrentHashMap<String, List<String>> mapTable = new ConcurrentHashMap<>();
    private volatile boolean isReadingFinished = false; // Флаг завершения чтения
    private boolean autoWightStatus = false;
    private int amountLines;
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
            for (Map.Entry<String, List<String>> entery : mapTable.entrySet()) {
                amountLines = entery.getValue().size();
                break;
            }
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
    public void deleteColumn(int number){
        mapTable.remove(titleKeys.get(number));

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
    }

    public Table merridColumns(int... connect) {
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
                outTable.putInMap(line);
            }
        }

        for (Map.Entry<String, List<String>> entery : outTable.mapTable.entrySet()) {
            outTable.amountLines = entery.getValue().size();
            break;
        }

        outTable.OUT = new Out(outTable);
        return outTable;
    }


    public <K extends String, V extends List> void write(Map<K, V> mapCSV, String outputPath, char separator) {
        BufferedWriter bw;
        Set<K> set = new LinkedHashSet<>(mapCSV.keySet());
        List<K> keys = set.stream().toList();
        try {
            bw = new BufferedWriter(new FileWriter(outputPath));
            if (mapCSV != null) {
                for (int i = 0; i < mapCSV.get(keys.get(0)).size(); i++) {
                    StringBuilder line = new StringBuilder();
                    for (int j = 0; j < mapCSV.size(); j++) {
                        String word;
                        word = (String) mapCSV.get(keys.get(j)).get(i);
                        line.append(word);
                        if (j < mapCSV.size() - 1) {
                            if (j == mapCSV.size()) {

                                line.append(separator);
                            }
                            line.append(separator);
                        }
                    }
                    bw.write(line.toString() + separator + "\n");

                }
                bw.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
