import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) throws IOException {
//        CSV_run csvRun = new CSV_run();
//        csvRun.run();

        // Define file paths and names
        String path = "C:/Users/Demch/OneDrive/Рабочий стол/English learning/";
        String curDateFormat = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String nameFile = "Favorites_" + curDateFormat + ".csv";
        String newFileName = "reWords_Transfer " + LocalDate.now() + ".csv";
        Table table = new Table(path + nameFile);
        table.writeTable(path + newFileName);
        System.out.println(table.titleKeys);
        table.print(0,3);
        table.insertColumn(2,"Noun");
        table.print(0,3);
        table.sortColumns();
        table.print(0,3);
        table.sortColumns(2,3,8);
        table.print(0,3);
        table.wrap("\"");
        table.write(path + newFileName,';');


    }
}