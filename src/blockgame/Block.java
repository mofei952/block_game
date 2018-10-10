package blockgame;

/**
 * 方块类
 */
public class Block {
    /**
     * 方块7种类型和各自的几种状态
     */
    static int[][][] data = {
            {{1, 5, 9, 13}, {4, 5, 6, 7}},    //|
            {{1, 4, 5, 8}, {1, 2, 3, 4}},    //反z
            {{1, 3, 4, 6}, {0, 1, 4, 5}},    //z
            {{1, 4, 7, 8}, {3, 4, 5, 6}, {0, 1, 4, 7}, {2, 3, 4, 5}},//L
            {{1, 4, 6, 7}, {3, 6, 7, 8}, {1, 2, 4, 7}, {3, 4, 5, 8}},//反L
            {{0, 1, 3, 4}},        // 田
            {{4, 6, 7, 8}, {0, 3, 4, 6}, {3, 4, 5, 7}, {2, 4, 5, 8}}//T
    };
    /**
     * 方块二维数组
     */
    private int[][] arr;
    /**
     * 方块类型
     */
    private int type;
    /**
     * 方块状态
     */
    private int state;

    /**
     * 构造方法
     *
     * @param a
     * @param type
     * @param state
     */
    public Block(int[] a, int type, int state) {
        int n = type == 0 ? 4 : 3;
        arr = new int[n][n];
        for (int i = 0; i < a.length; i++) {
            int x = a[i] / n;
            int y = a[i] % n;
            arr[x][y] = 1;
        }
        this.type = type;
        this.state = state;
    }

    /**
     * 静态方法，获取一个随机类型随机状态的方块
     *
     * @return
     */
    public static Block getBlock() {
        int t = (int) (Math.random() * 7);
        int s = (int) (Math.random() * data[t].length);
        Block block = new Block(data[t][s], t, s);
        return block;
    }

    /**
     * 变换为下一个状态
     */
    public void turn() {
        state = (state + 1) % data[type].length;
        arr = turn(type == 0 ? 4 : 3, data[type][state]);
    }

    /**
     * 反转，变换回上一个状态
     */
    public void reverse() {
        state = (state - 1 + data[type].length) % data[type].length;
        arr = turn(type == 0 ? 4 : 3, data[type][state]);
    }

    /**
     * 状态变换为a
     *
     * @param n
     * @param a
     * @return
     */
    public int[][] turn(int n, int[] a) {
        int[][] changedBlock = new int[n][n];
        for (int i = 0; i < a.length; i++) {
            int x = a[i] / n;
            int y = a[i] % n;
            changedBlock[x][y] = 1;
        }
        return changedBlock;
    }

    /**
     * 获取方块二维数组的length
     *
     * @return
     */
    public int length() {
        return arr.length;
    }

    /**
     * 获取方块二维数组第i行第j列的值
     *
     * @param i
     * @param j
     * @return
     */
    public int get(int i, int j) {
        return arr[i][j];
    }

    /**
     * 获取方块二维数组第i行
     *
     * @param i
     * @return
     */
    public int[] get(int i) {
        return arr[i];
    }

}
