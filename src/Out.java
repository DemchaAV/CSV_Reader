import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class Out {
    Map<String, Integer> columnWidth = new LinkedHashMap<>(); // Width of each column
    List<String> keySet; // List of column keys


    public Out(Table table) {
        keySet = table.titleKeys;
        //computation width columns
        columnWidth = autoWidthColumns(table.mapTable, table.titleKeys);
    }
    /**
     * Prints a data row in the table.
     *
     * @param line    Data row to print
     * @param numeric Flag indicating if the data is numeric
     */
    public void print(List<String> line, boolean numeric) {
        if (line == null) {
            return; // Prevent NullPointerException
        }
        StringBuilder stringLine = new StringBuilder();
        for (int i = 0; i < keySet.size(); i++) {
            String word = line.get(i);
            String num = "";
            if (numeric) {
                num = "(" + String.valueOf(i + 1) + "): ";
                stringLine.append(num + word);
            } else {
                stringLine.append(word);
            }
            stringLine.append(printSpace(columnWidth.get(keySet.get(i)) - word.length() - num.length()));

        }
        if (numeric) {
            System.out.println(line(sum()));
            System.out.println(line(sum()));
        }
        System.out.println(stringLine);
        if (numeric) {
            System.out.println(line(sum()));
        }
        System.out.println(line(sum()));
    }
    /**
     * Prints a data row in the table without number formatting.
     *
     * @param line Data row to print
     */
    public void print(List<String> line) {
        print(line, false);

    }
    /**
     * Automatically computes the width of columns based on data.
     *
     * @param map  Data map
     * @param keys Order of columns
     * @return Map of column widths
     */
    private Map<String, Integer> autoWidthColumns(Map<String, List<String>> map, List<String> keys) {
        Map<String, Integer> length = new LinkedHashMap<>();
        for (String key : keys) {
            length.put(key, sizeColumn(key, map.get(key)));
        }
        return length;
    }
    /**
     * Computes the maximum length of elements in a column.
     *
     * @param key  Column key
     * @param list Data list in the column
     * @return Maximum length of an element in the column
     */
    private int sizeColumn(String key, List<String> list) {
        int max = key.length()+6;
        for (String item : list) {
            if (item.length() > max) {
                max = item.length();
            }
        }
        return max;
    }

    // Additional methods (printSpace, line) have identical structure
    private String line(int spaces) {
        StringBuilder s = new StringBuilder();
        if (spaces > 0) {
            for (int i = 0; i < spaces; i++) {
                s.append("_");
            }
        }
        return s + "___";
    }

    private String printSpace(int spaces) {
        StringBuilder s = new StringBuilder();
        if (spaces > 0) {
            for (int i = 0; i < spaces; i++) {
                s.append(" ");
            }
        }
        return s + "││ ";
    }

    /**
     * Computes the total width of a data row.
     *
     * @return Total width of the row
     */
    private int sum() {
        int sum = 0;
        for (Map.Entry<String, Integer> entery : columnWidth.entrySet()) {
            sum += entery.getValue();
        }
        return sum+((keySet.size()+2)*2);
    }

}
