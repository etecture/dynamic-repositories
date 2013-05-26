package de.etecture.opensource.neo4j.cypher.responsehandling;

import de.etecture.opensource.neo4j.CypherResult;
import de.etecture.opensource.neo4j.Node;
import de.etecture.opensource.neo4j.Relationship;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Pattern;
import org.json.simple.JSONObject;

/**
 * a default implementation of {@link CypherResult}
 *
 * @author rhk
 */
class DefaultCypherResult implements CypherResult {

    private static final Pattern nodePattern = Pattern.compile("http://.+/db/data/node/(\\d+)");
    private static final Pattern relationshipPattern = Pattern.compile("http://.+/db/data/relationship/(\\d+)");
    private List<String> columns = new ArrayList<>();
    private List<Map<String, Object>> data = new ArrayList<>();

    @Override
    public int getRowCount() {
        return this.data.size();
    }

    @Override
    public int getColumnCount() {
        return this.columns.size();
    }

    @Override
    public String getColumnName(int columnIndex) {
        return this.columns.get(columnIndex);
    }

    @Override
    public List<String> getColumnNames() {
        return Collections.unmodifiableList(this.columns);
    }

    @Override
    public Object getValue(int rowIndex, int columnIndex) {
        return getValue(rowIndex, columns.get(columnIndex));
    }

    @Override
    public Object getValue(int rowIndex, String columnName) {
        Map<String, Object> rowData = getRowData(rowIndex);
        if (rowData.containsKey(columnName)) {
            return rowData.get(columnName);
        } else {
            return null;
        }
    }

    @Override
    public List<Object> getColumnValues(int columnIndex) {
        return getColumnValues(columns.get(columnIndex));
    }

    @Override
    public List<Object> getColumnValues(String columnName) {
        List<Object> result = new ArrayList<>();
        for (Map<String, Object> row : this.data) {
            result.add(row.get(columnName));
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public List<Object> getRowValues(int rowIndex) {
        return Collections.unmodifiableList(new ArrayList(this.data.get(rowIndex).values()));
    }

    @Override
    public Map<String, Object> getRowData(int rowIndex) {
        return Collections.unmodifiableMap(this.data.get(rowIndex));
    }

    @Override
    public List<Map<String, Object>> getAllValues() {
        return Collections.unmodifiableList(this.data);
    }

    @Override
    public boolean isEmpty() {
        return this.data.isEmpty();
    }

    void addColumn(String columnName) {
        this.columns.add(columnName);
    }

    void setValueAt(int rowIndex, int columnIndex, Object value) {
        setValueAt(rowIndex, columns.get(columnIndex), value);
    }

    void setValueAt(int rowIndex, String columnName, Object value) {
        // ensure capacity
        for (int i = data.size(); i <= rowIndex; i++) {
            data.add(new HashMap<String, Object>());
        }
        // get the row for the rowIndex
        Map<String, Object> row = this.data.get(rowIndex);
        // set the field...
        if (value instanceof JSONObject && ((JSONObject) value).containsKey("self")) {
            String selfUrl = (String) ((JSONObject) value).get("self");
            // check if it is a node or a relationship
            if (nodePattern.matcher(selfUrl).matches()) {
                value = new Node((JSONObject) value);
            } else if (relationshipPattern.matcher(selfUrl).matches()) {
                value = new Relationship((JSONObject) value);
            }
        }
        row.put(columnName, value);
    }

    void setRow(int rowIndex, Map<String, Object> row) {
        // ensure capacity
        for (int i = data.size(); i < rowIndex; i++) {
            data.add(new HashMap<String, Object>());
        }
        // add the row
        data.add(row);
    }

    void setRowValues(int rowIndex, List rowValues) {
        for (ListIterator it = rowValues.listIterator(); it.hasNext();) {
            setValueAt(rowIndex, it.nextIndex(), it.next());
        }
    }

    @Override
    public Iterator<Map<String, Object>> iterator() {
        return this.data.iterator();
    }

    @Override
    public String toString() {
        // calculate column-sizes...
        int[] columnSizes = new int[getColumnCount()];
        for (int c = 0; c < getColumnCount(); c++) {
            columnSizes[c] = (columns.get(c).length());
            for (Object rowValue : getColumnValues(c)) {
                columnSizes[c] = Math.max(columnSizes[c], rowValue == null ? 0 : rowValue.toString().length());
            }
        }
        StringBuilder sb = new StringBuilder();
        // print header
        for (int i = 0; i < columnSizes.length; i++) {
            if (i == 0) {
                sb.append("+-");
            } else {
                sb.append("-");
            }
            sb.append(new String(new char[columnSizes[i]]).replaceAll("\0", "-"));
            sb.append("-+");
        }
        sb.append("\n");
        // print column names...
        for (int i = 0; i < columnSizes.length; i++) {
            if (i == 0) {
                sb.append("| ");
            } else {
                sb.append(" ");
            }
            String columnName = columns.get(i);
            sb.append(columnName);
            for (int j = columnName.length(); j < columnSizes[i]; j++) {
                sb.append(" ");
            }
            sb.append(" |");
        }
        sb.append("\n");
        for (int i = 0; i < columnSizes.length; i++) {
            if (i == 0) {
                sb.append("+-");
            } else {
                sb.append("-");
            }
            sb.append(new String(new char[columnSizes[i]]).replaceAll("\0", "-"));
            sb.append("-+");
        }
        sb.append("\n");
        // print row values
        for (Map<String, Object> row : data) {
            for (int i = 0; i < columnSizes.length; i++) {
                if (i == 0) {
                    sb.append("| ");
                } else {
                    sb.append(" ");
                }
                Object rowValue = row.get(columns.get(i));

                String value = "";
                if (rowValue != null) {
                    if (rowValue instanceof JSONObject) {
                        value = ((JSONObject) rowValue).toJSONString();
                    } else {
                        value = rowValue.toString();
                    }
                }
                sb.append(value);
                for (int j = value.length(); j < columnSizes[i]; j++) {
                    sb.append(" ");
                }
                sb.append(" |");
            }
            sb.append("\n");
            for (int i = 0; i < columnSizes.length; i++) {
                if (i == 0) {
                    sb.append("+-");
                } else {
                    sb.append("-");
                }
                sb.append(new String(new char[columnSizes[i]]).replaceAll("\0", "-"));
                sb.append("-+");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
