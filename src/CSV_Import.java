import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CSV_Import {
    protected volatile boolean isReadingFinished = false; // Флаг завершения чтения
    List<String> titleKeys;
    Table table;

    public void setTitleKeys(List<String> titleKeys) {
        this.titleKeys = titleKeys;
    }

    volatile String titleNotify;
    AtomicInteger counter = new AtomicInteger();
    protected final ArrayBlockingQueue<String> abq = new ArrayBlockingQueue<>(100);
    public Table importAsTable(String inPath) {
        table = new Table(inPath);

        return table;
    }

    /**
     * method create title of our columns and create an information obout our exists columns in case errors
     *
     * @param line input line from reading file
     * @return boolean to change title status to false
     */
    protected boolean isTitleStatus(String line) {
        if (line == null) {
            return true;
        }
        List<String> values;
        values = parseCsvLine(line);
        this.titleKeys = new ArrayList<>(values);
        StringBuilder titleNotify = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            titleNotify.append(i + 1 + ". (" + values.get(i) + ") ");
        }
        this.titleNotify = titleNotify.toString();
        return false;
    }

    /**
     * Method create a new String line with our changes and reformatted
     *
     * @param separator   how we will separate our columns
     * @param wrapper     if we need to wrap our columns set character as a String if not set null
     * @param connectList connecting List  to combine our columns
     * @param columnsList list of columns for our output file
     * @param line        inputLine from our reading file
     * @return changed String line
     * @throws InterruptedException if our columns oouOfBound
     */
    protected String lineCreator(char separator, String wrapper, List<Integer> connectList, List<Integer> columnsList, String line) throws InterruptedException {
        if (isContainNegative(columnsList) || isOverBound(columnsList, titleKeys)) {
            isReadingFinished = true;
            throw new ArrayIndexOutOfBoundsException("Available columns list are: " + titleNotify);
        }
        if (columnsList.containsAll(connectList)) {

            List<String> values;
            System.out.println(line);
            values = parseCsvLine(line);
            String changedLine = setSeparatorAndConnect(values, separator, wrapper, connectList, columnsList);
            System.out.println(changedLine);

            abq.put(changedLine);
            counter.incrementAndGet();
            return changedLine;
        } else {
            isReadingFinished = true;
            throw new RuntimeException("The  connecting number is not on your processing list " + columnsList);
        }
    }

    protected List<String> parseCsvLine(String line) {
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
        Connector conector = new Connector(wordsList, connectList);
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

    private boolean isContainNegative(List<Integer> checkingList) {
        for (int i = 0; i < checkingList.size(); i++) {
            if (checkingList.get(i) < 1) {
                return true;
            }
        }
        return false;
    }

    private boolean isOverBound(List<Integer> chekingList, List<String> title) {
        for (int i = 0; i < chekingList.size(); i++) {
            if (chekingList.get(i) > title.size()) {
                return true;
            }
        }
        return false;
    }

    protected void fileInfo(String outputPath) {
        File file = new File(outputPath);

        // Получаем размер файла
        long fileSize = file.length();

        // Выводим размер файла
        System.out.println("\nWords processed: " + counter);
        System.out.printf("The file size: %.3f KB%n", (fileSize / 1024.0));
        System.out.println();
    }
}

