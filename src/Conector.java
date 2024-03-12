import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Conector {
    private String value;
    private List<Integer> doneList = new ArrayList<>();
    private List<String> inList;
    List<Integer> conectColumns;

    public Conector(List<String> inList, List<Integer> connectColumns) {
        this.inList = inList;
        this.conectColumns = connectColumns;
        connect();
    }

    public Conector(List<String> inList, int[] connectColumns) {
        this.inList = inList;
        this.conectColumns = Arrays.stream(connectColumns).boxed().collect(Collectors.toList());
        connect();
    }

    public String getValue() {
        return value;
    }

    private void connect() {
        StringBuilder conectedLine = new StringBuilder();
        for (int j = 0; j < conectColumns.size(); j++) {
            conectedLine.append(inList.get(conectColumns.get(j) - 1));
            doneList.add(conectColumns.get(j));
            if (j != conectColumns.size() - 1)
                conectedLine.append(", ");
        }
        value = conectedLine.toString();
    }

    public List<Integer> getDoneList() {
        return doneList;
    }
}
