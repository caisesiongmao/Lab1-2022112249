import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTestWhite {
    private Main main;

    @BeforeEach
    void setUp() {
        main = new Main();
        // 初始化测试数据或环境
        try {
            // 创建图
            main.graph = Main.createGraphFromTextFile("C:\\Users\\ASUS\\Desktop\\楞次都没有我次\\软工\\Lab1\\Easy_Test.txt");
            // 打印图的所有边
            //Main.showDirectedGraph(main.graph);
        } catch (IOException e) {
            System.err.println("读取文件时发生错误: " + e.getMessage());
        }
    }
    @Test
    void testCalcShortestPath1() {
        String result =Main.calcShortestPath("","");
        assertTrue(result.contains("null"),
                "结果应为 '" + "Input is null" + " ', 但实际结果是: " + result);
    }
    @Test
    void testCalcShortestPath2() {
        String result =Main.calcShortestPath("data","data");
        assertTrue(result.contains("same"),
                "结构应为 '" + "The start and end words are the same!"+ " ', 但实际结果是: " + result);
    }
    @Test
    void testCalcShortestPath3() {
        String result =Main.calcShortestPath("again","data");
        assertTrue(result.contains("No"),
                "结果应为 '" + "No path exists between again and data"+ " ', 但实际结果是: " + result);
    }
    @Test
    void testCalcShortestPath4() {
        String result =Main.calcShortestPath("data","detailed");
        assertTrue(result.equals("data -> wrote -> a -> detailed"),
                "结果应为 '" + "data -> wrote -> a -> detailed"+ " ', 但实际结果是: " + result);
    }
}