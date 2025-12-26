// checkers/json/JsonHelper.java
package checkers.json;

import java.util.List;
import java.util.Map;

public class JsonHelper {

    // Сериализация объекта в JSON строку
    public static String toJson(Object obj) {
        if (obj == null) return "null";
        if (obj instanceof String) return "\"" + escapeJson((String) obj) + "\"";
        if (obj instanceof Number) return obj.toString();
        if (obj instanceof Boolean) return obj.toString();
        if (obj instanceof List) return listToJson((List<?>) obj);
        if (obj instanceof Map) return mapToJson((Map<?, ?>) obj);
        return "\"" + escapeJson(obj.toString()) + "\"";
    }

    private static String listToJson(List<?> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(toJson(list.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }

    private static String mapToJson(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) sb.append(",");
            first = false;
            sb.append("\"").append(escapeJson(entry.getKey().toString())).append("\":");
            sb.append(toJson(entry.getValue()));
        }
        sb.append("}");
        return sb.toString();
    }

    private static String escapeJson(String str) {
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
