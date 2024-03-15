import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

class CSV_run {
    public void run() {
        long start = System.nanoTime();
        String path = "C:/Users/Demch/OneDrive/Рабочий стол/English learning/";
        String curDateFormat = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String nameFile = "Favorites_" + curDateFormat + ".csv";
        String newFileName = "reWords_Transfer " + LocalDate.now() + ".csv";
        CSV_reWord csv = new CSV_reWord();
        try {
            csv.multithreading(
                    path + nameFile, ';', null, path + newFileName, new int[]{2, 9}, 1, 2, 9, 3, 4);
        } catch (RuntimeException run) {
            run.printStackTrace();
        } catch (Exception e) {
            System.out.println(e.getMessage() );
            System.out.println("Please write down below a list of columns for connect");
            Scanner scanner = new Scanner(System.in);
            String[] stringPackInt = scanner.nextLine().split(",");
            List<Integer> intList = Arrays.stream(stringPackInt)
                    .map(Integer::valueOf) // Преобразует каждую строку в Integer
                    .collect(Collectors.toList()); // Собираем результат в список2
            csv.multithreading(
                    path + nameFile, ';', null, path + newFileName, intList, 1, 2, 9, 3, 4);

        }
        System.out.println("Spent time " + (System.nanoTime() - start) / 1000000 + " milisec");


    }

}
