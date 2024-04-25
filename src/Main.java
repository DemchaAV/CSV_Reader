import table.Table;

import java.io.IOException;
import java.time.LocalDate;
import static  table.Table.*;
git
public class Main {
    public static void main(String[] args) throws IOException {
        String path = "C:/Users/Demch/OneDrive/Рабочий стол/English learning/";
        String newFileName = "reWords_Transfer " + LocalDate.now() + ".csv";
        Table table = new Table(path + Table.currentReversoDateFile);
        table.writeTable(path + Table.currentReversoDateFile);
        table.print(0,1);
        table = table.merridColumns(2,9);
        table.insertColumn(1,"Transcription");
        table.print(0,1);
        table = table.sortColumns(1, 2, 3, 4, 5);
        table.wrap("\"");
        table.print(0,3);
        table.write(path + newFileName,';');


    }
}