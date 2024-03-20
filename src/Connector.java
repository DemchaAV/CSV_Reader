import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Connector implements Combinable {
    private String value;
    private List<Integer> doneList = new ArrayList<>();
    private List<String> inList;
    private List<String> outList = new ArrayList<>();
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
        connectColumns = connectColumns.stream().map(x -> --x).collect(Collectors.toList());
        StringBuilder connectedLine = new StringBuilder();
        for (int i = 0; i < connectColumns.size(); i++) {
            if (i > 0) {
                connectedLine.append(", ");
            }
            connectedLine.append(inList.get(connectColumns.get(i)));
            doneList.add(connectColumns.get(i));
        }
        this.value = connectedLine.toString();
        return connectedLine.toString();
    }

    public String connectAsList() {
        return connect(this.inList, this.connectColumns);
    }

    public List<String> connectAsList(List<String> inList, int... connectColumns) {
        return connectAsList(inList, Arrays.stream(connectColumns).boxed().collect(Collectors.toList()));
    }

    public List<String> connectAsList(List<String> inList, List<Integer> connectColumns) {
        doneList.clear();
        doneList = new ArrayList<>();
        List<String> newList = new ArrayList<>();
        for (int i = 0; i < inList.size(); i++) {
            if (doneList.isEmpty()) {
                if (connectColumns.contains(i)) {
                    newList.add(connect(inList, connectColumns.stream().map(x -> ++x).collect(Collectors.toList())));
                } else {
                    newList.add(inList.get(i));
                }
            } else {
                if (!doneList.contains(i)) {
                    newList.add(inList.get(i));
                }
            }

        }
        this.outList = new ArrayList<>(newList);
        return newList;
    }


    public List<Integer> getDoneList() {
        return doneList;
    }
}
