import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {

        List<String> arl = new ArrayList<>();
        arl.add("hello");
        arl.add("ok");
        arl.add("Poka");
        arl.add("good bay");
        arl.add("wether");
        arl.add("chip");
List<Integer> listInt = new ArrayList<>();
listInt.add(2);
        listInt.add(5);
        listInt.add(9);
        listInt.add(1);
        List<Integer> listInt2 = new ArrayList<>();
listInt2.add(9);
listInt2.add(1);
//        System.out.println(listInt.co);


        Conector conector = new Conector(arl, new int[]{2,5});
        System.out.println(conector.getValue());
        System.out.println(conector.getDoneList());

    }
}
