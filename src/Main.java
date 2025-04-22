import java.io.*;
import java.util.*;

public class Main {
    static Graph graph = null;

    public static void main(String[] args) {
        // 检查命令行参数是否为空
        if (args.length == 0) {
            System.out.println("请提供文件路径作为参数！");
            return;
        }

        // 获取文件路径
        String filePath = args[0];

        try {
            // 创建图
            graph = createGraphFromTextFile(filePath);
            // 打印图的所有边
        } catch (IOException e) {
            System.err.println("读取文件时发生错误: " + e.getMessage());
        }
        while (true) {
            System.out.println("输入需求：");
            System.out.println("---------------");
            System.out.println("1.展示有向图");
            System.out.println("2.查询桥接词");
            System.out.println("3.生成新文本");
            System.out.println("4.最短路径");
            System.out.println("5.计算PR");
            System.out.println("6.随机游走");
            System.out.println("7.退出");
            Scanner sc = new Scanner(System.in);
            int choice = sc.nextInt();
            sc.nextLine();
            switch (choice) {
                case 1:
                    showDirectedGraph(graph);
                    break;
                case 2:
                    System.out.println("请输入要查询的两个单词");
                    String word1 = sc.nextLine();
                    String word2 = sc.nextLine();
                    System.out.println(queryBridgeWords(word1, word2));
                    break;
                case 3:
                    System.out.println("请输入新的文本");
                    String inputText = sc.nextLine();
                    System.out.println(generateNewText(inputText));
                    break;
                case 4:
                    System.out.println("请输入起始节点和目标节点");
                    String start = sc.nextLine();
                    String end = sc.nextLine();
                    System.out.println(calcShortestPath(start, end));
                    break;
                case 5:
                    System.out.println("请输入要计算PR的节点");
                    String node = sc.nextLine();
                    System.out.println(calPageRank(node));
                    break;
                case 6:
                    String walkText = randomWalk();
                    System.out.println(walkText);
                    try (PrintWriter writer = new PrintWriter(new File("random_walk_output.txt"))) {
                        writer.println(walkText);
                        System.out.println("路径已保存至 random_walk_output.txt");
                    } catch (IOException e) {
                        System.out.println("写入文件失败: " + e.getMessage());
                    }
                default:
                    System.exit(0);
            }
        }
    }

    public static Graph createGraphFromTextFile(String filePath) throws IOException {
        Graph graph = new Graph();
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        String[] temp;
        List<String> words = new ArrayList<>();
        // 用于处理每一行文本
        while ((line = br.readLine()) != null) {
            temp = line.split("\\W+");  // 按照空格分割单词
            for (int i = 0; i < temp.length - 1; i++) {
                String wordA = temp[i].toLowerCase();  // 转为小写
                String wordB = temp[i + 1].toLowerCase();  // 转为小写
                words.add(wordA);
                words.add(wordB);
            }
        }
        for (int i = 0; i < words.size() - 1; i++) {
            if (!words.get(i).equals(words.get(i + 1))) {
                graph.addEdge(words.get(i), words.get(i + 1));
            }
        }
        // 添加边
        br.close();
        return graph;
    }

    // 打印图中的节点和边
    public static void showDirectedGraph(Graph graph) {
        for (String node : graph.adjList.keySet()) {
            for (Map.Entry<String, Integer> entry : graph.adjList.get(node).entrySet()) {
                System.out.println("Edge: " + node + " -> " + entry.getKey() + " with weight: " + entry.getValue());
            }
        }
    }

    public static String queryBridgeWords(String word1, String word2) {
        word1 = word1.toLowerCase();
        word2 = word2.toLowerCase();

        if (!graph.adjList.containsKey(word1) || !graph.adjList.containsKey(word2)) {
            return "No " + word1 + " or " + word2 + " in the graph!";
        }

        List<String> bridgeWords = new ArrayList<>();
        Map<String, Integer> word1Neighbors = graph.adjList.get(word1);

        for (String mid : word1Neighbors.keySet()) {
            Map<String, Integer> midNeighbors = graph.adjList.get(mid);
            if (midNeighbors != null && midNeighbors.containsKey(word2)) {
                bridgeWords.add(mid);
            }
        }

        if (bridgeWords.isEmpty()) {
            return "No bridge words from " + word1 + " to " + word2 + "!";
        } else {
            StringBuilder result = new StringBuilder("The bridge words from " + word1 + " to " + word2 + " are: ");
            for (int i = 0; i < bridgeWords.size(); i++) {
                result.append(bridgeWords.get(i));
                if (i < bridgeWords.size() - 2) {
                    result.append(", ");
                } else if (i == bridgeWords.size() - 2) {
                    result.append(", and ");
                }
            }
            result.append(".");
            return result.toString();
        }
    }

    public static String generateNewText(String inputText) {
        String[] words = inputText.trim().split("\\W+");
        List<String> result = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < words.length - 1; i++) {
            String word1 = words[i];
            String word2 = words[i + 1];
            result.add(word1);

            List<String> bridgeWords = new ArrayList<>();

            if (graph.adjList.containsKey(word1.toLowerCase())) {
                for (String mid : graph.adjList.get(word1).keySet()) {
                    if (graph.adjList.containsKey(mid) && graph.adjList.get(mid).containsKey(word2)) {
                        bridgeWords.add(mid);
                    }
                }
            }

            if (!bridgeWords.isEmpty()) {
                String chosen = bridgeWords.get(random.nextInt(bridgeWords.size()));
                result.add(chosen);
            }
        }

        // 添加最后一个单词
        result.add(words[words.length - 1]);

        // 返回结果
        return "生成的新文本：\n" + String.join(" ", result);
    }

    public static String calcShortestPath(String word1, String word2) {
        // 记录每个节点到起始点的最短距离
        Map<String, Integer> dist = new HashMap<>();
        // 记录每个节点的前驱节点，以便回溯路径
        Map<String, String> prev = new HashMap<>();
        // 最小优先队列，用于选择当前最短路径的节点
        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingInt(dist::get));

        // 初始化
        for (String node : graph.adjList.keySet()) {
            dist.put(node, Integer.MAX_VALUE);  // 初始距离为无穷大
            prev.put(node, null);  // 没有前驱
        }
        dist.put(word1, 0);  // 起点到起点的距离为 0
        pq.add(word1);

        while (!pq.isEmpty()) {
            String current = pq.poll();

            // 如果当前节点是目标节点，则可以返回路径
            if (current.equals(word2)) {
                return reconstructPath(prev, word1, word2);
            }

            // 更新邻接节点的距离
            for (Map.Entry<String, Integer> entry : graph.adjList.getOrDefault(current, new HashMap<>()).entrySet()) {
                String neighbor = entry.getKey();
                int weight = entry.getValue();
                int newDist = dist.get(current) + weight;

                if (newDist < dist.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    dist.put(neighbor, newDist);
                    prev.put(neighbor, current);
                    pq.add(neighbor);
                }
            }
        }

        return "No path exists between " + word1 + " and " + word2;  // 如果没有找到路径
    }

    // 通过 prev 路径回溯，生成最短路径
    private static String reconstructPath(Map<String, String> prev, String word1, String word2) {
        List<String> path = new ArrayList<>();
        for (String at = word2; at != null; at = prev.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        if (path.size() == 1 || !path.get(0).equals(word1)) {
            return "No path exists between " + word1 + " and " + word2;
        }

        // 将路径格式化为字符串并返回
        return String.join(" -> ", path);
    }

    public static Double calPageRank(String word) {
        // 阻尼系数 d，迭代次数与容差设置
        double d = 0.85;
        int maxIterations = 100;
        double tolerance = 1.0e-6;

        Map<String, Double> pr = new HashMap<>();
        Map<String, Set<String>> inLinks = new HashMap<>();
        Set<String> nodes = graph.adjList.keySet();

        // 初始化 PR 值
        for (String node : nodes) {
            pr.put(node, 1.0);
            for (String neighbor : graph.adjList.get(node).keySet()) {
                inLinks.computeIfAbsent(neighbor, k -> new HashSet<>()).add(node);
            }
            inLinks.putIfAbsent(node, new HashSet<>());
        }

        int N = pr.size();
        boolean converged = false;

        for (int iter = 0; iter < maxIterations && !converged; iter++) {
            Map<String, Double> newPr = new HashMap<>();
            converged = true;

            for (String node : nodes) {
                double sum = 0.0;
                for (String inNode : inLinks.get(node)) {
                    int outDegree = graph.adjList.get(inNode).size();
                    if (outDegree > 0) {
                        sum += pr.get(inNode) / outDegree;
                    }
                }

                double updatedPr = (1 - d) / N + d * sum;
                newPr.put(node, updatedPr);

                // 判断是否收敛
                if (Math.abs(updatedPr - pr.get(node)) > tolerance) {
                    converged = false;
                }
            }

            pr = newPr;
        }

        word = word.toLowerCase(); // 保证大小写不敏感

        if (!pr.containsKey(word)) {
            System.out.println("单词 " + word + " 不在图中！");
            return null;
        }

        return pr.get(word);
    }

    public static String randomWalk() {
        Scanner sc = new Scanner(System.in);
        Random rand = new Random();
        Set<String> visitedEdges = new HashSet<>();
        List<String> walkPath = new ArrayList<>();
        // 从图中随机选择一个起始节点
        List<String> nodes = new ArrayList<>(graph.adjList.keySet());
        if (nodes.isEmpty()) {
            return null;
        }
        String current = nodes.get(rand.nextInt(nodes.size()));
        walkPath.add(current);
        System.out.println("开始随机游走，起点: " + current);

        while (true) {
            Map<String, Integer> neighbors = graph.adjList.getOrDefault(current, new HashMap<>());
            if (neighbors.isEmpty()) {
                System.out.println("当前节点无出边，游走结束。");
                break;
            }

            // 随机选择一个出边
            List<String> neighborList = new ArrayList<>(neighbors.keySet());
            String next = neighborList.get(rand.nextInt(neighborList.size()));

            String edgeKey = current + "->" + next;
            if (visitedEdges.contains(edgeKey)) {
                System.out.println("检测到重复边 " + edgeKey + "，游走结束。");
                break;
            }

            visitedEdges.add(edgeKey);
            walkPath.add(next);
            current = next;


        }

        // 输出并保存到文件
        String walkText = String.join(" ", walkPath);
        return ("最终游走路径: " + walkText);


    }

}
