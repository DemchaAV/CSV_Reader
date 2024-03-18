import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The Connector class provides functionality to connect and transform lists of strings and integers.
 */
public class Connector implements Combinable {
    private List<Integer> doneList = new ArrayList<>(); // List to store indices of processed elements
    private List<String> inList; // Input list of strings
    private String value; // Combined string value
    private List<Integer> connectColumns; // Columns to connect

    private List<Integer> keySet; // Keys used during transformation

    private List<String> newLine = new ArrayList<>(); // Transformed lines
    private String wrapper; // Wrapper for transformation

    /**
     * Constructs a Connector object with the given input list and connect columns.
     *
     * @param inList         The input list of strings.
     * @param connectColumns The columns to connect.
     */
    public Connector(List<String> inList, List<Integer> connectColumns) {
        this.inList = inList;
        this.connectColumns = connectColumns;
    }

    /**
     * Constructs a Connector object with the given input list, connect columns, and wrapper.
     *
     * @param inList         The input list of strings.
     * @param connectColumns The columns to connect.
     * @param wrapper        The wrapper for transformation.
     */
    public Connector(List<String> inList, List<Integer> connectColumns, String wrapper) {
        this(inList, connectColumns);
        this.wrapper = wrapper;
    }

    /**
     * Constructs a Connector object with the given input list and connect columns.
     * @param inList The input list of strings.
     * @param connectColumns The columns to connect.
     */
    public Connector(List<String> inList, int[] connectColumns) {
        this.inList = inList;
        this.connectColumns = Arrays.stream(connectColumns).boxed().collect(Collectors.toList());
    }

    /**
     * Connects the elements of the input list based on the specified connect columns.
     *
     * @return The combined string value.
     */
    public String connect() {
        return connect(this.inList, this.connectColumns);
    }

    /**
     * Connects the elements of the input list based on the specified connect columns.
     * @param inList The input list of strings.
     * @param connectColumns The columns to connect.
     * @return The combined string value.
     */
    public String connect(List<String> inList, List<Integer> connectColumns) {
        StringBuilder connectedLine = new StringBuilder();
        for (int i = 0; i < connectColumns.size(); i++) {
            if (i > 0) {
                connectedLine.append(", ");
            }
            connectedLine.append(inList.get(connectColumns.get(i)));
            doneList.add(connectColumns.get(i));
        }
        value = connectedLine.toString().toLowerCase();
        return value;
    }

    /**
     * Transforms the input list based on the connect columns and the wrapper.
     *
     * @return The transformed list.
     */
    public List<String> transformList() {
        return transformList(inList, connectColumns, wrapper);
    }

    /**
     * Transforms the input list based on the specified connect columns and wrapper.
     *
     * @param inList         The input list of strings.
     * @param connectColumns The columns to connect.
     * @param wrapper        The wrapper for transformation.
     * @return The transformed list.
     */
    public List<String> transformList(List<String> inList, List<Integer> connectColumns, String wrapper) {
        this.wrapper = (wrapper == null) ? "" : wrapper;
        List<String> outList = new ArrayList<>();
        keySet = new ArrayList<>();
        for (int i = 0; i < inList.size(); i++) {
            if (doneList.isEmpty()) {
                if (connectColumns.contains(i)) {
                    keySet.add(connectColumns.get(0));
                    outList.add(wrapper + connect(inList, connectColumns) + wrapper);
                } else {
                    keySet.add(i);
                    outList.add(wrapper + inList.get(i) + wrapper);
                }
            } else {
                if (!doneList.contains(i)) {
                    keySet.add(i);
                    outList.add(wrapper + inList.get(i) + wrapper);
                }
            }
        }
        newLine = outList;
        return outList;
    }

    /**
     * Gets the list of indices that have been processed.
     * @return The list of indices.
     */
    public List<Integer> getDoneList() {
        return doneList;
    }

    /**
     * Gets the combined string value.
     *
     * @return The combined string value.
     */
    public String getValue() {
        return value;
    }

    public List<Integer> getKeySet() {
        return keySet;
    }
}
