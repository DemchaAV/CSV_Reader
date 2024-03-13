import java.io.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CSV_Import {
    String path;


    Map<String, List<String>> mapCSV = new LinkedHashMap<>();
    private volatile boolean isReadingFinished = false; // Флаг завершения чтения
    List<String> keys = new ArrayList<>();
    Map<String, Integer> columnWight = new LinkedHashMap<>();
    AtomicInteger counter = new AtomicInteger();
    //    volatile int counter = 0;
    private final ArrayBlockingQueue<String> abq = new ArrayBlockingQueue<>(100);

    public CSV_Import(String path) {
        this.path = path;
        create();
        columnWight = wigthColumns(mapCSV, keys);
    }

    public CSV_Import() {

    }

    /**
     *
     * @param inPath - inPut Path. It is String to our file example "C:/Users/UserName/userFile.csv"
     * @param separator - variable char to set separator between words for our construction of csv file
     * @param wrapper - It`s take String parameter to wrapper columns you can put null if you don`t need to wrap our columns or eny character like " or '  etc
     * @param outputPath - It`s path to save our new File
     * @param connectList - This`s list contain columns which one will be connected together these columns should be in our columnsList below
     * @param columnsList - Thi`s  list of columns witch one we will reformat, and reduce if we need before make sure the input file from inPath contain enough colums
     * @throws RuntimeException En exception shows us if we put connectList which one is not contains in our columns list
     */

    public void multithreading(String inPath, char separator, String wrapper, String outputPath, List<Integer> connectList, List<Integer> columnsList) throws RuntimeException {
        if (columnsList.containsAll(connectList)) {
            Thread reader = new Thread(() -> {
                try (BufferedReader br = new BufferedReader(new FileReader(inPath))) {
                    String line;
                    boolean titleStatus = true;
                    List<String> values;
                    while ((line = br.readLine()) != null) {
                        if (titleStatus) {
                            titleStatus = false;
                            values = parseCsvLine(line);
                            for (int i = 0; i < values.size(); i++) {
                                System.out.print(i + 1 + ". (" + values.get(i) + ") ");
                            }
                            System.out.println();

                        } else {
                            System.out.println(line);
                            values = parseCsvLine(line);
                            String changedLine = setSeparatorAndConnect(values, separator, wrapper, connectList, columnsList);
                            System.out.println(changedLine);

                            abq.put(changedLine);
                            counter.incrementAndGet();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    isReadingFinished = true; //make sure it is done
                }
            });

            Thread writer = new Thread(() -> {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath))) {
                    while (!isReadingFinished || !abq.isEmpty()) {
                        if (!abq.isEmpty()) {
                            bw.write(abq.take());
                            bw.newLine();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            reader.start();
            writer.start();

            try {
                reader.join();
                writer.join();
                System.out.println("\nWords have done: " + counter);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        } else {
            throw new RuntimeException("The  connecting number is not on your processing list "+ columnsList);
        }
    }

    public void
    multithreading(String inPath, char separator, String wrapper, String outputPath, int[] connect, int... columns) {
        List<Integer> connectList = Arrays.stream(connect).boxed().collect(Collectors.toList());
        List<Integer> columnsList = Arrays.stream(columns).boxed().collect(Collectors.toList());
        multithreading(inPath, separator, wrapper, outputPath, connectList, columnsList);
    }

    public void multithreading(String inPath, char separator, String wrapper, String outputPath, List<Integer> connectList, int... columns) {
        List<Integer> columnsList = Arrays.stream(columns).boxed().collect(Collectors.toList());
        multithreading(inPath, separator, wrapper, outputPath, connectList, columnsList);
    }

    public <K, V> Map<K, V> transferColomn(Map<K, V> mapIn, int... columns) {
        Map<K, V> mapOut = new LinkedHashMap<>();
        for (int i = 0; i < columns.length; i++) {
            mapOut.put((K) keys.get(columns[i] - 1), (V) mapIn.get(keys.get(columns[i] - 1)));

        }
        return mapOut;

    }

    public <K extends String, V extends List> void print() {
        print(this.mapCSV);
    }

    public <K extends String, V extends List> void print(Map<K, V> mapCSV) {
        Set<K> set = new LinkedHashSet<>(mapCSV.keySet());
        List<K> keys = new ArrayList<>(set.stream().toList());
        if (mapCSV != null) {
            for (int i = 0; i < mapCSV.get(keys.get(0)).size(); i++) {
                StringBuilder line = new StringBuilder();
                for (int j = 0; j < mapCSV.size(); j++) {
                    String word;
                    word = (String) mapCSV.get(keys.get(j)).get(i);
                    line.append(word);
                    line.append(printSpace(columnWight.get(keys.get(j)) - word.length()));
                }
                System.out.println(line);
                System.out.println(line(sum()));

            }
        }
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

    public void create() {

        String line = "";
        boolean firstLine = false;

        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            boolean tittelStatus = true;
            int colomns = 0;
            while (true) {
                try {
                    if (((line = br.readLine()) == null)) break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    List<String> values = parseCsvLine(line);
                    //create a colomn for aur Map
                    if (tittelStatus) {
                        keys = new ArrayList<>(values);
                        tittelStatus = false;
                    } else {
                        values = parseCsvLine(line);
                        for (int i = 0; i < keys.size() - 1; i++) {
                            if (mapCSV.get(keys.get(i)) != null) {
                                mapCSV.get(keys.get(i)).add(values.get(i));
                            } else {
                                mapCSV.put((keys.get(i)), new ArrayList<>());

                            }
                        }


                    }

                } catch (ArrayIndexOutOfBoundsException e) {
                    continue;
                }

            }
            br.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static List<String> parseCsvLine(String line) {
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

    private static String printSpace(int spaces) {
        StringBuilder s = new StringBuilder();
        if (spaces > 0) {
            for (int i = 0; i < spaces; i++) {
                s.append(" ");
            }
        }
        return s.toString() + "││ ";
    }

    private static String line(int spaces) {
        StringBuilder s = new StringBuilder();
        if (spaces > 0) {
            for (int i = 0; i < spaces; i++) {
                s.append("_");
            }
        }
        return s.toString() + "___";
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

    private <T extends String> T setSeparator(List<T> list, char separator) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            line.append(list.get(i) + String.valueOf(separator));
        }
        return (T) line.toString();
    }

    private <T extends String> T setSeparator(List<T> list, char separator, int... coloms) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < coloms.length; i++) {
            line.append(list.get(coloms[i] - 1) + String.valueOf(separator));
        }
        return (T) line.toString();
    }

    /**
     *This method take String wordsList connect some columns together and set separators for our csv file
     * @param wordsList - input wordsList previous separated words
     * @param separator - separator for building new string output
     * @param wrapper - set null if we don`t need to wrapping our columns
     * @param connectList - these are wordsList of columns which one will be connected together all columns
     * @param columnsList - these are wordsList of columns which wi will transfer for our new file
     * @return new String with our changes
     */
    private String setSeparatorAndConnect(List<String> wordsList, char separator, String wrapper, List<Integer> connectList, List<Integer> columnsList) {
        StringBuilder line = new StringBuilder();
        List<Integer> doneList = new ArrayList<>();
        Conector conector = new Conector(wordsList, connectList);
        wrapper = wrapper == null ? "" : wrapper;
        if (wordsList.get(4).equals("en")) {
            for (int i = 0; i < columnsList.size(); i++) {
                if (doneList.isEmpty() && connectList.contains(columnsList.get(i)) && i < columnsList.size()) {
                    line.append(wrapper + separator);
                    doneList.addAll(conector.getDoneList());
                    line.append(wrapper + conector.getValue().toLowerCase() + wrapper);
                    line.append(separator);
                } else if (!doneList.contains(columnsList.get(i))) {
                    //setLowerCase if is just target word or translation
                    if (columnsList.get(i) != 3 && columnsList.get(i) != 4) {
                        line.append(wrapper + wordsList.get(columnsList.get(i) - 1).toLowerCase() + wrapper + separator);
                    } else {
                        //set as original file character Case
                        line.append(wrapper + wordsList.get(columnsList.get(i) - 1) + wrapper + separator);
                    }
                }
            }
            line.append(wrapper + wrapper + separator);
        } else {
            //reverse translation
            line.append(wrapper + wordsList.get(1).toLowerCase() + wrapper + separator);
            line.append(wrapper + separator);
            line.append(wrapper + wordsList.get(0).toLowerCase() + wrapper + separator);
            for (int i = 2; i < columnsList.size(); i++) {
                line.append(wrapper + wordsList.get(columnsList.get(i) - 1) + wrapper + separator);
            }


        }
        return line.toString();
    }

    private String setSeparatorAndConnect(List<String> list, char separator, String wrapper, List<Integer> connectList, int... columns) {
        List<Integer> columnsList = Arrays.stream(columns).boxed().collect(Collectors.toList());
        return setSeparatorAndConnect(list, separator, wrapper, connectList, columnsList);
    }

    private  <K extends String, V extends Integer> Map<K, V> wigthColumns(Map<?, List<String>> map, List<?> keys) {
        Map<K, V> leght = new LinkedHashMap<>();
        for (int i = 0; i < map.size(); i++) {
            leght.put((K) keys.get(i), (V) sizeColomn((K) keys.get(i), map.get(keys.get(i))));
        }
        return leght;
    }

    private List<Integer> connect() {
        List<Integer> doneList = new ArrayList<>();

        return doneList;
    }

    private  <T extends String> Integer sizeColomn(String key, List<T> list) {
        int max = key.length();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).length() > max) {
                max = list.get(i).length();
            }
        }
        return max;
    }
}

