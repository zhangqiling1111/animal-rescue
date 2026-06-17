import java.util.*;

public class Main {
    // 定义图的顶点，按题目中的 A、B、C、D、E、F、G 顺序
    private static final char[] VERTICES = {'A', 'B', 'C', 'D', 'E', 'F', 'G'};
    // 用于快速根据边的字符串标识（如 "AB"）获取其在输入数组中的索引
    private static final Map<String, Integer> EDGE_INDEX_MAP = new HashMap<>();

    static {
        // 初始化边与输入数组索引的映射关系，对应题目中的 AB、AC、AF、BC、CD、CG、DE、EF、EG 顺序
        EDGE_INDEX_MAP.put("AB", 0);
        EDGE_INDEX_MAP.put("AC", 1);
        EDGE_INDEX_MAP.put("AF", 2);
        EDGE_INDEX_MAP.put("BC", 3);
        EDGE_INDEX_MAP.put("CD", 4);
        EDGE_INDEX_MAP.put("CG", 5);
        EDGE_INDEX_MAP.put("DE", 6);
        EDGE_INDEX_MAP.put("EF", 7);
        EDGE_INDEX_MAP.put("EG", 8);
    }

    // 边的内部类，用于存储邻接顶点和边的权重
    static class Edge {
        char to;
        int weight;

        public Edge(char to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入线路长度 (d1,d2,d3,d4,d5,d6,d7,d8,d9)：");
        String input = scanner.nextLine().trim();
        // 处理输入，去除括号并按逗号分割
        input = input.replace("(", "").replace(")", "");
        int[] d = Arrays.stream(input.split(",")).mapToInt(Integer::parseInt).toArray();

        // 构建图的邻接表表示
        Map<Character, List<Edge>> adjacencyList = buildGraph(d);

        // 计算所有边的权重总和
        int totalEdgesWeight = calculateTotalEdgesWeight(d);

        // 找出所有奇度数顶点
        List<Character> oddDegreeVertices = findOddDegreeVertices(adjacencyList);

        // 计算奇度数顶点之间的最短路径，本题中主要是处理 A 和 E 两个奇度数顶点的情况
        int shortestPathBetweenOdds = 0;
        if (oddDegreeVertices.size() == 2) {
            char start = oddDegreeVertices.get(0);
            char end = oddDegreeVertices.get(1);
            shortestPathBetweenOdds = dijkstraShortestPath(adjacencyList, start, end);
        }

        // 计算最短巡检距离 = 所有边权重和 + 奇度数顶点间最短路径长度
        int shortestInspectionDistance = totalEdgesWeight + shortestPathBetweenOdds;
        System.out.println("巡检员的最短行走距离：" + shortestInspectionDistance);
    }

    // 构建图的邻接表，用于存储每个顶点的邻接边信息
    private static Map<Character, List<Edge>> buildGraph(int[] d) {
        Map<Character, List<Edge>> adjacencyList = new HashMap<>();
        // 初始化每个顶点的邻接边列表
        for (char vertex : VERTICES) {
            adjacencyList.put(vertex, new ArrayList<>());
        }

        // 添加边到邻接表，无向图所以双向添加边
        addEdge(adjacencyList, 'A', 'B', d[EDGE_INDEX_MAP.get("AB")]);
        addEdge(adjacencyList, 'A', 'C', d[EDGE_INDEX_MAP.get("AC")]);
        addEdge(adjacencyList, 'A', 'F', d[EDGE_INDEX_MAP.get("AF")]);
        addEdge(adjacencyList, 'B', 'C', d[EDGE_INDEX_MAP.get("BC")]);
        addEdge(adjacencyList, 'C', 'D', d[EDGE_INDEX_MAP.get("CD")]);
        addEdge(adjacencyList, 'C', 'G', d[EDGE_INDEX_MAP.get("CG")]);
        addEdge(adjacencyList, 'D', 'E', d[EDGE_INDEX_MAP.get("DE")]);
        addEdge(adjacencyList, 'E', 'F', d[EDGE_INDEX_MAP.get("EF")]);
        addEdge(adjacencyList, 'E', 'G', d[EDGE_INDEX_MAP.get("EG")]);

        return adjacencyList;
    }

    // 向邻接表中添加边，无向图需添加双向边
    private static void addEdge(Map<Character, List<Edge>> adjacencyList, char from, char to, int weight) {
        adjacencyList.get(from).add(new Edge(to, weight));
        adjacencyList.get(to).add(new Edge(from, weight));
    }

    // 计算所有边的权重总和
    private static int calculateTotalEdgesWeight(int[] d) {
        return Arrays.stream(d).sum();
    }

    // 找出图中所有度数为奇数的顶点
    private static List<Character> findOddDegreeVertices(Map<Character, List<Edge>> adjacencyList) {
        List<Character> oddDegreeVertices = new ArrayList<>();
        for (char vertex : VERTICES) {
            // 顶点的度数等于其邻接边列表的大小
            if (adjacencyList.get(vertex).size() % 2 != 0) {
                oddDegreeVertices.add(vertex);
            }
        }
        return oddDegreeVertices;
    }

    // 使用 Dijkstra 算法计算从 start 顶点到 end 顶点的最短路径长度
    private static int dijkstraShortestPath(Map<Character, List<Edge>> adjacencyList, char start, char end) {
        // 用于存储每个顶点到 start 顶点的最短距离，初始化为无穷大
        Map<Character, Integer> shortestDistances = new HashMap<>();
        for (char vertex : VERTICES) {
            shortestDistances.put(vertex, Integer.MAX_VALUE);
        }
        shortestDistances.put(start, 0);

        // 优先队列，按照距离从小到大排序，存储的是（距离，顶点）对
        PriorityQueue<Map.Entry<Character, Integer>> priorityQueue = new PriorityQueue<>(
                Comparator.comparingInt(Map.Entry::getValue)
        );
        priorityQueue.offer(new AbstractMap.SimpleEntry<>(start, 0));

        // 记录已经确定最短路径的顶点
        Set<Character> visited = new HashSet<>();

        while (!priorityQueue.isEmpty()) {
            Map.Entry<Character, Integer> currentEntry = priorityQueue.poll();
            char currentVertex = currentEntry.getKey();
            int currentDistance = currentEntry.getValue();

            // 如果已经处理过该顶点，跳过
            if (visited.contains(currentVertex)) {
                continue;
            }
            // 标记为已访问
            visited.add(currentVertex);

            // 如果到达目标顶点，提前结束
            if (currentVertex == end) {
                break;
            }

            // 遍历当前顶点的邻接边
            for (Edge edge : adjacencyList.get(currentVertex)) {
                char neighbor = edge.to;
                int newDistance = currentDistance + edge.weight;

                // 如果经过当前顶点到邻接顶点的距离更短，更新最短距离并加入队列
                if (newDistance < shortestDistances.get(neighbor)) {
                    shortestDistances.put(neighbor, newDistance);
                    priorityQueue.offer(new AbstractMap.SimpleEntry<>(neighbor, newDistance));
                }
            }
        }

        // 返回 start 到 end 的最短路径长度
        return shortestDistances.get(end);
    }
}