import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class PolynomialConstantTerm {

    public static class Point {
        int x;
        int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    // Convert a value from a given base to base-10
    public static int decodeValue(int base, String value) {
        return Integer.parseInt(value, base);
    }

    // Parse the JSON data and retrieve the (x, y) points
    public static List<Point> parseRoots(JSONObject jsonData) {
        List<Point> roots = new ArrayList<>();
        JSONObject keys = jsonData.getJSONObject("keys");

        for (String key : jsonData.keySet()) {
            if (!key.equals("keys")) {
                int x = Integer.parseInt(key);  // x is the key of the object
                JSONObject details = jsonData.getJSONObject(key);
                int base = Integer.parseInt(details.getString("base"));
                String value = details.getString("value");
                int y = decodeValue(base, value);  // y is the decoded value
                roots.add(new Point(x, y));
            }
        }
        return roots;
    }

    // Apply Lagrange interpolation to find the polynomial value at x_value
    public static int lagrangeInterpolation(List<Point> points, int xValue) {
        double result = 0;
        int n = points.size();

        for (int i = 0; i < n; i++) {
            double term = points.get(i).y;
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    term *= (xValue - points.get(j).x) / (double)(points.get(i).x - points.get(j).x);
                }
            }
            result += term;
        }
        return (int) Math.round(result);
    }

    // Main function to find the constant term
    public static int findConstantTerm(JSONObject jsonData) {
        JSONObject keys = jsonData.getJSONObject("keys");
        int n = keys.getInt("n");
        int k = keys.getInt("k");

        List<Point> roots = parseRoots(jsonData);

        if (roots.size() < k) {
            throw new IllegalArgumentException("Not enough roots to solve for the polynomial coefficients");
        }

        // Solve for the constant term using Lagrange interpolation at x = 0
        return lagrangeInterpolation(roots, 0);
    }

    public static void main(String[] args) {
        String jsonString = "{\n" +
                "    \"keys\": {\n" +
                "        \"n\": 4,\n" +
                "        \"k\": 3\n" +
                "    },\n" +
                "    \"1\": {\n" +
                "        \"base\": \"10\",\n" +
                "        \"value\": \"4\"\n" +
                "    },\n" +
                "    \"2\": {\n" +
                "        \"base\": \"2\",\n" +
                "        \"value\": \"111\"\n" +
                "    },\n" +
                "    \"3\": {\n" +
                "        \"base\": \"10\",\n" +
                "        \"value\": \"12\"\n" +
                "    },\n" +
                "    \"6\": {\n" +
                "        \"base\": \"4\",\n" +
                "        \"value\": \"213\"\n" +
                "    }\n" +
                "}";

        JSONObject jsonData = new JSONObject(jsonString);
        int constantTerm = findConstantTerm(jsonData);
        System.out.println("Constant term (c): " + constantTerm);
    }
}
