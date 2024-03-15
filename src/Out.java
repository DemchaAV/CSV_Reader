import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class Out {
    Map<String, Integer> columnWidth = new LinkedHashMap<>();
    List<String> keySet;


    /**
     * Calculation size of columns
     *
     * @param map  mapTable
     * @param keys order of columns
     */
    public Out(Map<String, List<String>> map, List<String> keys) {
        keySet = keys;
        //computation width columns
        columnWidth = autoWidthColumns(map, keys);
    }

    public void setKeySet(List<String> keySet) {
        this.keySet = keySet;
    }

    public void print(List<String> line, boolean numeric) {
        if (line == null) {
            return; // Предотвращаем NullPointerException
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

    public void print(List<String> line) {
        print(line, false);

    }

    private Map<String, Integer> autoWidthColumns(Map<String, List<String>> map, List<String> keys) {
        Map<String, Integer> length = new LinkedHashMap<>();
        for (String key : keys) {
            length.put(key, sizeColumn(key, map.get(key)));
        }
        return length;
    }

    private int sizeColumn(String key, List<String> list) {
        int max = key.length();
        for (String item : list) {
            if (item.length() > max) {
                max = item.length();
            }
        }
        return max;
    }

    // Методы printSpace и line могут быть адаптированы аналогичным образом
    private String line(int spaces) {
        StringBuilder s = new StringBuilder();
        if (spaces > 0) {
            for (int i = 0; i < spaces; i++) {
                s.append("_");
            }
        }
        return s.toString() + "___";
    }

    private String printSpace(int spaces) {
        StringBuilder s = new StringBuilder();
        if (spaces > 0) {
            for (int i = 0; i < spaces; i++) {
                s.append(" ");
            }
        }
        return s.toString() + "││ ";
    }

    /**
     * this method is providing of the lengthens ours columns to create a line under each print string
     *
     * @return int the length ours line
     */
    private int sum() {
        int sum = 0;
        for (Map.Entry<String, Integer> entery : columnWidth.entrySet()) {
            sum += entery.getValue();
        }
        return sum;
    }

}
