import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {

        List<String> arl = new ArrayList<>();
        arl.add("Odin");//1
        arl.add("Dwa");//2
        arl.add("Tri");//3
        arl.add("Chetyre");//4
        arl.add("Pyat");//5
        arl.add("Shest");//6
List<Integer> listInt = new ArrayList<>();
listInt.add(2);
        listInt.add(6);
        listInt.add(3);
        listInt.add(1);
        List<Integer> listInt2 = new ArrayList<>();
listInt2.add(9);
listInt2.add(1);
        arl.forEach(System.out::println);
        Connector conector = new Connector(arl, new int[]{2,1});
        conector.connectAsList(arl,2,1).forEach(System.out::println);


        System.out.println(conector.getDoneList());
        String path = "C:/Users/Demch/OneDrive/Рабочий стол/English learning/";
        String curDateFormat = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String nameFile = "Favorites_" + curDateFormat + ".csv";
        String newFileName = "reWords_Transfer " + LocalDate.now() + ".csv";
        Table table = new Table(path+nameFile);
//        table.print();
       Table table1 =  table.merridColumns(1,8);
//        System.out.println(table1.mapTable);
        table1.print();

    }
}
