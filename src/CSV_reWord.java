import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CSV_reWord extends CSV_Import {
    /**
     *
     * @param inPath  inPut Path. It is String to our file example "C:/Users/UserName/userFile.csv"
     * @param separator  variable char to set separator between words for our construction of csv file
     * @param wrapper  It`s take String parameter to wrapper columns you can put null if you don`t need to wrap our columns or eny character like " or '  etc
     * @param outputPath  It`s path to save our new File
     * @param connectList  This`s list contain columns which one will be connected together these columns should be in our columnsList below
     * @param columnsList  Thi`s  list of columns witch one we will reformat, and reduce if we need before make sure the input file from inPath contain enough colums
     * @throws RuntimeException En exception shows us if we put connectList which one is not contains in our columns list
     */
    public void multithreading(
            String inPath, char separator, String wrapper, String outputPath, List<Integer> connectList, List<Integer> columnsList)
            throws RuntimeException {
        Thread reader = new Thread(() -> {
            threadReader(inPath, separator, wrapper, connectList, columnsList);
        });
        reader.setName("Thread - Reader");

        Thread writer = new Thread(() ->
                threadWriter(outputPath)
        );
        writer.setName("Thread - Writer");

        reader.start();
        writer.start();

        try {
            reader.join();
            writer.join();
            fileInfo(outputPath);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    public void multithreading(String inPath, char separator, String wrapper, String outputPath, int[] connect, int... columns) {
        List<Integer> connectList = Arrays.stream(connect).boxed().collect(Collectors.toList());
        List<Integer> columnsList = Arrays.stream(columns).boxed().collect(Collectors.toList());
        multithreading(inPath, separator, wrapper, outputPath, connectList, columnsList);
    }

    public void multithreading(String inPath, char separator, String wrapper, String outputPath, List<Integer> connectList, int... columns) {
        List<Integer> columnsList = Arrays.stream(columns).boxed().collect(Collectors.toList());
        multithreading(inPath, separator, wrapper, outputPath, connectList, columnsList);
    }

    public void multithreading(String inPath, char separator, String wrapper, String outputPath, String[] connectList, int... columns) {
        List<Integer> connect = Arrays.stream(connectList)
                .map(Integer::valueOf) // Преобразует каждую строку в Integer
                .collect(Collectors.toList());
        multithreading(inPath, separator, wrapper, outputPath, connect, columns);
    }

    private void threadReader(String inPath, char separator, String wrapper, List<Integer> connectList, List<Integer> columnsList) {
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
                    System.out.println("\n"+titleNotify+"\n");
                } else {
                    lineCreator(separator, wrapper, connectList, columnsList, line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            isReadingFinished = true; //make sure it is done
        }
    }

    private void threadWriter(String outputPath) {
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
    }
}
