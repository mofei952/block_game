package blockgame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * 主函数运行游戏
 */
public class BlockGame {
    public static void main(String[] args) {
        JFrame w = new JFrame();
        BlockPanel mp = new BlockPanel();

        Thread t = new Thread(mp);
        t.start();

        w.addKeyListener(mp);
        mp.addKeyListener(mp);

        w.add(mp);

        w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		w.setUndecorated(true);
        w.setBounds(400, 150, 450, 488);
        w.setVisible(true);
    }
}

/**
 * 自定义画板类
 */
class BlockPanel extends JPanel implements Runnable, KeyListener {
    /**
     * 背景二维数组
     */
    int[][] back = new int[20][12];
    /**
     * 当前方块，下一个方块
     */
    Block nowBlock, nextBlock;
    /**
     * 当前方块位置
     */
    int x, y;
    /**
     * 是否开始，是否存活，是否暂停
     */
    boolean isStart, isLive, isPause;
    /**
     * 分数
     */
    int score;

    /**
     * 构造函数
     */
    public BlockPanel() {
        init();
        isStart = false;
    }

    /**
     * 初始化参数
     */
    public void init() {
        isPause = false;
        isLive = true;
        isStart = true;
        score = 0;
        for (int i = 0; i < back.length; i++) {
            for (int j = 0; j < back[i].length; j++) {
                if (i == back.length - 1 || j == 0 || j == back[i].length - 1) {
                    back[i][j] = 1;
                } else {
                    back[i][j] = 0;
                }
            }
        }
        nowBlock = Block.getBlock();
        nextBlock = Block.getBlock();
        x = (int) (Math.random() * 6 + 1);
        y = 1;
    }

    /**
     * run
     */
    @Override
    public void run() {
        while (true) {
            if (isLive && !isPause && isStart) {
                if (isCollision()) {
                    handleCollision();
                } else {
                    y++;
                }
            }
            repaint();
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 绘制界面
     *
     * @param g
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        g.setColor(new Color(0x00C5CD));
        for (int i = 4; i < back.length - 1; i++) {
            for (int j = 1; j < back[i].length - 1; j++) {
                if (back[i][j] == 1) {
                    g.fillRect((j - 1) * 30, (i - 4) * 30, 29, 29);
                }
            }
        }
        if (isStart) {
            for (int i = 0; i < nowBlock.length(); i++) {
                for (int j = 0; j < nowBlock.get(i).length; j++) {
                    if (nowBlock.get(i, j) == 1) {
                        g.fillRect(j * 30 + (x - 1) * 30, i * 30 + (y - 4) * 30, 29, 29);
                    }
                }
            }
            for (int i = 0; i < nextBlock.length(); i++) {
                for (int j = 0; j < nextBlock.get(i).length; j++) {
                    if (nextBlock.get(i, j) == 1) {
                        g.fillRect(j * 30 + 315, i * 30 + 50, 29, 29);
                    }
                }
            }
        }

        g.setColor(new Color(0x999999));
        g.drawLine(300, 0, 300, 450);
        g.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        g.drawString("暂停(继续)", 310, 200);
        g.drawString("[Enter]", 310, 225);
        g.drawString("开始", 310, 270);
        g.drawString("[F5]", 310, 295);
        g.drawString("退出", 310, 340);
        g.drawString("[Esc]", 310, 365);
        g.drawString("分数：" + score, 310, 410);

        if (isStart && !isLive) {
            g.setColor(Color.GREEN);
            g.setFont(new Font("微软雅黑", Font.PLAIN, 20));
            g.drawString("游戏结束", 150, 200);
        }
    }

    /**
     * 键盘按下事件
     *
     * @param e
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        } else if (key == KeyEvent.VK_ENTER) {
            if (!isPause) {
                isPause = true;
            } else {
                isPause = false;
            }
        } else if (key == KeyEvent.VK_F5) {
            init();
        }

        if (!isStart || !isLive || isPause) {
            return;
        }

        if (key == KeyEvent.VK_UP) {
            nowBlock.turn();
            if (!isLegalLocation()) {
                nowBlock.reverse();
            } else {
                repaint();
            }
        } else if (key == KeyEvent.VK_LEFT) {
            if (canLeft()) {
                x--;
                repaint();
            }
        } else if (key == KeyEvent.VK_RIGHT) {
            if (canRight()) {
                x++;
                repaint();
            }
        } else if (key == KeyEvent.VK_DOWN) {
            if (isCollision()) {
                handleCollision();
            } else {
                y++;
            }
            repaint();
        } else if (key == KeyEvent.VK_SPACE) {
            while (!isCollision()) {
                y++;
            }
            handleCollision();
            repaint();
        }
    }

    /**
     * 返回是否碰撞
     *
     * @return
     */
    public boolean isCollision() {
        for (int i = 0; i < nowBlock.length(); i++) {
            for (int j = 0; j < nowBlock.get(i).length; j++) {
                if (nowBlock.get(i, j) == 1 && back[i + y + 1][j + x] == 1) {
                    if (i + y <= 3) {
                        isLive = false;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 处理碰撞
     */
    public void handleCollision() {
        addNowBlockToBack();
        clearFillLine();
        switchToNext();
    }

    /**
     * 将当前方块添加到back中
     */
    public void addNowBlockToBack() {
        for (int i = 0; i < nowBlock.length(); i++) {
            for (int j = 0; j < nowBlock.get(i).length; j++) {
                if (nowBlock.get(i, j) == 1) {
                    back[i + y][j + x] = 1;
                }
            }
        }
    }

    /**
     * 消除填满的行
     */
    public void clearFillLine() {
        int line = 0;
        for (int i = 0; i < back.length - 1; i++) {
            int sum = 0;
            for (int j = 1; j < back[i].length - 1; j++) {
                sum += back[i][j];
            }
            if (sum == back[i].length - 2) {
                line++;
                for (int j = i; j > 0; j--) {
                    for (int k = 1; k < back[i].length - 1; k++) {
                        back[j][k] = back[j - 1][k];
                    }
                }
//                for (int j = 0; j < back[i].length; j++) {
//                    if (j != 0 && j != back[i].length - 1) {
//                        back[0][j] = 0;
//                    }
//                }
            }
        }
        addScore(line);
    }

    /**
     * 切换到下一个方块
     */
    public void switchToNext() {
        nowBlock = nextBlock;
        nextBlock = Block.getBlock();
        x = (int) (Math.random() * 6 + 1);
        y = 1;
    }

    /**
     * 添加分数
     *
     * @param num
     */
    public void addScore(int num) {
        switch (num) {
            case 1:
                score += 10;
                break;
            case 2:
                score += 25;
                break;
            case 3:
                score += 50;
                break;
            case 4:
                score += 100;
                break;
        }
    }

    /**
     * 当前方块位置是否正常
     *
     * @return
     */
    public boolean isLegalLocation() {
        for (int i = 0; i < nowBlock.length(); i++) {
            for (int j = 0; j < nowBlock.get(i).length; j++) {
                if (nowBlock.get(i, j) == 1 && back[i + y][j + x] == 1) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 返回是否能够左移
     *
     * @return
     */
    public boolean canLeft() {
        for (int i = 0; i < nowBlock.length(); i++) {
            for (int j = 0; j < nowBlock.get(i).length; j++) {
                if (nowBlock.get(i, j) == 1 && back[i + y][j + x - 1] == 1) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 返回是否能够右移
     *
     * @return
     */
    public boolean canRight() {
        for (int i = 0; i < nowBlock.length(); i++) {
            for (int j = 0; j < nowBlock.get(i).length; j++) {
                if (nowBlock.get(i, j) == 1 && back[i + y][j + x + 1] == 1) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}



