import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CSV_run {
    public static void main(String[] args) throws IOException {
        long start = System.nanoTime();
        String path = "C:/Users/Demch/OneDrive/Рабочий стол/English learning/";
        String nameFile = "Favorites_20240311.csv";
        String newFileName = "reWords_Transfer " + LocalDate.now() + ".csv";
        CSV_Import csv = new CSV_Import();
        try {
            csv.multithreading(
                    path + nameFile, ';', path + newFileName, new int[]{2, 13}, 1, 2, 9, 3, 4);
        } catch (Exception e) {
            System.out.println(e.getMessage() );
            System.out.println("Please write down below a list of columns for connect");
            Scanner scanner = new Scanner(System.in);
            String[] stringPackInt = scanner.nextLine().split(",");
            List<Integer> intList = Arrays.stream(stringPackInt)
                    .map(Integer::valueOf) // Преобразует каждую строку в Integer
                    .collect(Collectors.toList()); // Собираем результат в список2
            csv.multithreading(
                    path + nameFile, ';', path + newFileName, intList, 1, 2, 9, 3, 4);

        }

//
//        CSV_Import csv = new CSV_Import(path + nameFile);
//        System.out.println("Transfer to the map been successful!!");
////        csv.print();
//
//        Map<String, List<String>> newMap = csv.transferColomn(csv.mapCSV, 1, 2, 3);
//        csv.write(newMap, path + newFileName, ';');
//        System.out.println("Writing successful");
//
//        for(Map.Entry<String, List<String>>entry: newMap.entrySet()){
//            System.out.print("In total been writing lines : ");
//            System.out.println(newMap.get(entry.getKey()).size()+1);
//            break;
//        }
        System.out.println("Spent time " + (System.nanoTime() - start) / 1000000 + " milisec");
        System.out.println("In total words: " + csv.counter);

    }

}
