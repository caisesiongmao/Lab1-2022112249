import java.util.HashMap;
import java.util.Map;

public class Graph {
    Map<String, Map<String, Integer>> adjList = new HashMap<>();

    // 添加边到图中
    public void addEdge(String from, String to) {
        adjList.putIfAbsent(from, new HashMap<>());
        adjList.get(from).put(to, adjList.get(from).getOrDefault(to, 0) + 1);
    }
}
