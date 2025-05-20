import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {
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
    void testQueryBridgeWords1() {
        String result =Main.queryBridgeWords("more","data");
        assertTrue(result.contains("No"),
                "桥接词结果应包含 '" + "No"+ " ', 但实际结果是: " + result);
    }
    @Test
    void testQueryBridgeWords2() {
        String result =Main.queryBridgeWords("analyzed","data");
        assertTrue(result.contains("the")&&result.contains("many"),
                "桥接词结果应包含 '" + "the'"+" 'many"+ " ', 但实际结果是: " + result);
    }
    @Test
    void testQueryBridgeWords3() {
        String result =Main.queryBridgeWords("scientist","analyzed");
        assertTrue(result.contains("carefully"),
                "桥接词结果应包含 '" + "carefully"+ " ', 但实际结果是: " + result);
    }
    @Test
    void testQueryBridgeWords4() {
        String result =Main.queryBridgeWords("team","more");
        assertTrue(result.contains("requested"),
                "桥接词结果应包含 '" + "requested"+ " ', 但实际结果是: " + result);
    }
}