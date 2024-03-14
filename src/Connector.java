import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Connector implements Combinable {
    private String value;
    private List<Integer> doneList = new ArrayList<>();
    private List<String> inList;
    List<Integer> connectColumns;

    public Connector(List<String> inList, List<Integer> connectColumns) {
        this.inList = inList;
        this.connectColumns = connectColumns;
        connect();
    }

    public Connector(List<String> inList, int[] connectColumns) {
        this.inList = inList;
        this.connectColumns = Arrays.stream(connectColumns).boxed().collect(Collectors.toList());
        connect();
    }

    public String getValue() {
        return value;
    }

     public String connect() {
       return connect(this.inList, this.connectColumns);
    }
    public String connect(List<String> inList,int[] connectColumns) {
        this.connectColumns = Arrays.stream(connectColumns).boxed().collect(Collectors.toList());
        return connect(inList, this.connectColumns);
    }
    public String connect(List<String> inList, List<Integer> connectColumns) {
        StringBuilder connectedLine = new StringBuilder();
        for (int i = 0; i < connectColumns.size(); i++) {
            if (i > 0) {
                connectedLine.append(", ");
            }
            connectedLine.append(inList.get(connectColumns.get(i) - 1));
            doneList.add(connectColumns.get(i));
        }
        this.value = connectedLine.toString();
        return connectedLine.toString();
    }


    public List<Integer> getDoneList() {
        return doneList;
    }
}
