import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {

        List<String> arl = new ArrayList<>();
        arl.add("hello");//1
        arl.add("ok");//2
        arl.add("Poka");//3
        arl.add("good bay");//4
        arl.add("wether");//5
        arl.add("chip");//6
List<Integer> listInt = new ArrayList<>();
listInt.add(2);
        listInt.add(6);
        listInt.add(3);
        listInt.add(1);
        List<Integer> listInt2 = new ArrayList<>();
listInt2.add(9);
listInt2.add(1);
//        System.out.println(listInt.co);


//        Conector conector = new Conector(arl, new int[]{2,5});
//        System.out.println(conector.getValue());
//        System.out.println(conector.getDoneList());
        String path = "C:/Users/Demch/OneDrive/Рабочий стол/English learning/";
        String curDateFormat = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String nameFile = "Favorites_" + curDateFormat + ".csv";
        String newFileName = "reWords_Transfer " + LocalDate.now() + ".csv";
        Table table = new Table(path+nameFile);
//        System.out.println(table.mapTable.keySet());
//        System.out.println(table.mapTable);
//        System.out.println(table.getLine(54));
        table.getColumns(0).forEach(System.out::println);
        System.out.println(table.getLine(7));

    }
}
