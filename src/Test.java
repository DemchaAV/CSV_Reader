import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {
    public static void main(String[] args) throws InterruptedException {
//        String path = "C:/Users/Demch/OneDrive/Рабочий стол/English learning/";
//        String curDateFormat = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
//        String nameFile = "Favorites_" + curDateFormat + ".csv";
//        String newFileName = "reWords_Transfer " + LocalDate.now() + ".csv";
//        Table table = new Table(path+nameFile);
////        table.print();
//       Table table1 =  table.merridColumns(1,8);
////        System.out.println(table1.mapTable);
////        table1.print();
//        table1.wrap("\"");
////        table1.print();
//        table1 = table1.reduceColumns(2,1,5);
//        Printable print = table1;
//        print.print();
//        System.out.println("                                         **************************************************************************************");
//        Thread.sleep(1000);
//        System.out.println(table1.deleteLine(1));
//        table1.print();
//        System.out.println(table1.mapTable.entrySet().iterator().next().getValue().size());
//        Cell cell = new Cell("Привет",1,2);
//        System.out.println(cell);
//        System.out.println(cell.toUpperCase());
//        System.out.println(cell.setCapitalLater());
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(4);
        list.add(5);
        System.out.println(list);
        list.add(2,3);
        System.out.println(list);
    }
}
