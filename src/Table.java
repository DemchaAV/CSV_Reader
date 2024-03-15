import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Table {
    ConcurrentHashMap<String, List<String>> mapTable = new ConcurrentHashMap<>();
    private volatile boolean isReadingFinished = false; // Флаг завершения чтения
    List<String> titleKeys;
    private boolean autoWightStatus = false;
    Map<String, Integer> columnWight = new LinkedHashMap<>();

    public Table(String inPath) {
        reader(inPath);
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

                } else {
                    putInMap(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            isReadingFinished = true; //make sure it is done
        }
    }

    private boolean isTitleStatus(String line) {
        if (line == null) {
            return true;
        }
        List<String> values;
        values = parseCsvLine(line);
        this.titleKeys = new ArrayList<>(values);
        for (int i = 0; i < titleKeys.size(); i++) {
            mapTable.put(titleKeys.get(i), new ArrayList<>());
        }
        return false;
    }

    public List<String> getColumns(int columnNumber) {
        return mapTable.get(titleKeys.get(columnNumber));

    }

    private void putInMap(String line) {
        List<String> values;
        values = parseCsvLine(line);
        for (int i = 0; i < titleKeys.size(); i++) {
            mapTable.get(titleKeys.get(i)).add(values.get(i));

        }
    }

    public List<String> getLine(int numberLine) {
        List<String> line = new ArrayList<>();
        for (int i = 0; i < titleKeys.size(); i++) {
            line.add(mapTable.get(titleKeys.get(i)).get(numberLine));
        }
        return line;
    }

    private List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>(4);
        String regex = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"; // Регулярное выражение для разделения, игнорируя запятые внутри кавычек
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);

        int start = 0;
        while (matcher.find()) {
            String match = line.substring(start, matcher.start()).trim();
            values.add(match.replaceAll("\"", "")); // Убираем кавычки, если они есть
            start = matcher.end();
        }

        // Добавляем последний элемент
        String lastValue = line.substring(start).trim();
        values.add(lastValue.replaceAll("\"", ""));

        return values;
    }

    public <K, V> Map<K, V> transferColomn(Map<K, V> mapIn, int... columns) {
        Map<K, V> mapOut = new LinkedHashMap<>();
        for (int i = 0; i < columns.length; i++) {
            mapOut.put((K) titleKeys.get(columns[i] - 1), (V) mapIn.get(titleKeys.get(columns[i] - 1)));

        }
        return mapOut;

    }

    private <K extends String, V extends Integer> Map<K, V> autoWigthColumns(Map<?, List<String>> map, List<?> keys) {
        Map<K, V> leght = new LinkedHashMap<>();
        for (int i = 0; i < map.size(); i++) {
            leght.put((K) keys.get(i), (V) sizeColomn((K) keys.get(i), map.get(keys.get(i))));
        }
        return leght;
    }

    private <T extends String> Integer sizeColomn(String key, List<T> list) {
        int max = key.length();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).length() > max) {
                max = list.get(i).length();
            }
        }
        return max;
    }

    public <K extends String, V extends List> void print() {
        print(this.mapTable);
    }

    public <K extends String, V extends List> void print(Map<K, V> mapTable) {
        Set<K> set = new LinkedHashSet<>(mapTable.keySet());
        List<K> keys = new ArrayList<>(set.stream().toList());
        if (mapTable != null) {
            for (int i = 0; i < mapTable.get(keys.get(0)).size(); i++) {
                StringBuilder stringLine = new StringBuilder();
                for (int j = 0; j < mapTable.size(); j++) {
                    String word;
                    word = (String) mapTable.get(keys.get(j)).get(i);
                    stringLine.append(word);
                    stringLine.append(printSpace(columnWight.get(keys.get(j)) - word.length()));
                }
                System.out.println(stringLine);
                System.out.println(line(sum()));

            }
        }
    }

    private String line(int spaces) {
        StringBuilder s = new StringBuilder();
        if (spaces > 0) {
            for (int i = 0; i < spaces; i++) {
                s.append("_");
            }
        }
        return s.toString() + "___";
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


    private String printSpace(int spaces) {
        StringBuilder s = new StringBuilder();
        if (spaces > 0) {
            for (int i = 0; i < spaces; i++) {
                s.append(" ");
            }
        }
        return s.toString() + "││ ";
    }

    /**
     * this method is providing of the lengthens ours columns to create a line under each print string
     *
     * @return int the length ours line
     */
    private int sum() {
        int sum = 0;
        for (Map.Entry<String, Integer> entery : columnWight.entrySet()) {
            sum += entery.getValue();
        }
        return sum;
    }

}
