import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) throws IOException {
        String path = "C:/Users/Demch/OneDrive/Рабочий стол/English learning/";
        String curDateFormat = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String nameFile = "Favorites_" + curDateFormat + ".csv";
        String newFileName = "reWords_Transfer " + LocalDate.now() + ".csv";
        Table table = new Table(path + nameFile);
        table.writeTable(path + newFileName);
        System.out.println(table.titleKeys);
        table.print(0,1);
        table = table.merridColumns(2,9);
        table.insertColumn(1,"Transcription");
        table.print(0,1);
        table.sortColumns(1,2,3,4,5);
        table.wrap("\"");
        table.print(0,3);
        table.write(path + newFileName,';');


    }
}