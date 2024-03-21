import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Test {
    public static void main(String[] args) {
        String path = "C:/Users/Demch/OneDrive/Рабочий стол/English learning/";
        String curDateFormat = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String nameFile = "Favorites_" + curDateFormat + ".csv";
        String newFileName = "reWords_Transfer " + LocalDate.now() + ".csv";
        Table table = new Table(path+nameFile);
//        table.print();
       Table table1 =  table.merridColumns(1,8);
//        System.out.println(table1.mapTable);
//        table1.print();
        table1.wrap("\"");
//        table1.print();
        table1 = table1.reduceColumns(2,1,5);
        table1.print();
    }
}
