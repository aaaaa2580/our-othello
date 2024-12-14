package org.Othello.GUI;
import org.Othello.Game.ReversiGame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

public class ReversiGUI extends JFrame {
    private JButton[][] boardButtons;
    private ReversiGame game;
    private int boardSize;
    int step = 0; // 初始化步数
    String player = "黑棋";
    private JLabel stepCountLabel; // 用于计步的标签
    private JLabel playerLabel;
    private JLabel whiteCount;
    private JLabel blackCount; // 用于显示当前玩家的标签

    private Timer playerTimer; // 用于管理当前玩家的计时器
    private int timeLimit; // 每步限时，单位：秒
    private JLabel timeLabel; // 显示剩余时间
    private int remainingTime; // 当前剩余时间


    public ReversiGUI(int size, ReversiGame game) {
        this.boardSize = size;
        this.game = game;

        // 弹出对话框让用户设置时间
        setTimeLimit();

        boardButtons = new JButton[boardSize][boardSize];
        initializeBoard();
        initJMenuBar();
        updateBoard(game.getBoardState());
        saveAndLoad();

        resetTimer();//启动倒计时

        setResizable(false);
        setVisible(true);
    }


    //初始化棋盘
    private void initializeBoard() {
        setTitle("黑白棋睿智小游戏");
        setSize(800, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // 创建一个面板来放置棋盘按钮
        JPanel boardPanel = new JPanel(new GridLayout(boardSize, boardSize));
        boardPanel.setBackground(new Color(196, 179, 211));

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                boardButtons[i][j] = new JButton();
                boardButtons[i][j].setOpaque(false); // 使按钮透明
                boardButtons[i][j].setContentAreaFilled(false); // 不填充按钮区域
                int row = i;
                int col = j;
                boardButtons[i][j].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        handleBoardClick(row, col);
                    }
                });
                boardPanel.add(boardButtons[i][j]);
            }
        }

        // 将棋盘面板添加到窗口的中央
        add(boardPanel, BorderLayout.CENTER);

        // 创建面板用于放置计步和轮次提醒
        JPanel counterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        stepCountLabel = new JLabel("当前步数：0");
        playerLabel = new JLabel("当前轮到：黑");
        whiteCount = new JLabel("白棋：2");
        blackCount = new JLabel("黑棋：2");
        timeLabel = new JLabel("剩余时间：" + timeLimit + " 秒");
        counterPanel.add(stepCountLabel);
        counterPanel.add(Box.createHorizontalStrut(10));
        counterPanel.add(playerLabel);
        counterPanel.add(Box.createHorizontalStrut(20));
        counterPanel.add(whiteCount);
        counterPanel.add(Box.createHorizontalStrut(10));
        counterPanel.add(blackCount);
        counterPanel.add(Box.createHorizontalStrut(20));
        counterPanel.add(timeLabel);
        add(counterPanel, BorderLayout.NORTH);


    }

    private void saveAndLoad() {
        JButton saveButton = new JButton("存一下（存档）");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(ReversiGUI.this, "已经存好力！！");
            }
        });

        JButton loadButton = new JButton("回溯一下（读档）");
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(ReversiGUI.this, "读完力，，");
            }
        });

        // 创建一个面板来放置存档和读档按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);

        // 将按钮面板添加到窗口的底部
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // 初始化菜单
    private void initJMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu function = new JMenu("功能");
        JMenu introduction = new JMenu("介绍");
        menuBar.add(function);
        menuBar.add(introduction);

        JMenuItem replay = new JMenuItem("重开一局");
        replay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetGameGUI();
                setTimeLimit(); // 重新设置时间限制
            }
        });

        JMenuItem reLogin = new JMenuItem("换个号");
        JMenuItem close = new JMenuItem("不玩了！！");
        function.add(replay);
        function.add(reLogin);
        function.add(close);

        JMenuItem aboutUs = new JMenuItem("纯吐槽");
        introduction.add(aboutUs);

        this.setJMenuBar(menuBar);
    }


    private void resetGameGUI() {
        game.resetGame(); // 重置游戏逻辑状态
        step = 0; // 重置步数
        player = game.getCurrentPlayer() == 1 ? "黑棋" : "白棋"; // 重置当前玩家
        updateBoard(game.getBoardState()); // 更新棋盘显示
        updateLabels(); // 更新标签
    }

    private void handleBoardClick(int row, int col) {
        if (game.makeMove(row, col)) {
            step++;
            player = game.getCurrentPlayer() == 1 ? "黑棋" : "白棋";
            resetTimer(); // 重置计时器

            if (game.noValidMoves(game.getCurrentPlayer())) {
                JOptionPane.showMessageDialog(this, player + "没位置了，下一位！", "提示", JOptionPane.INFORMATION_MESSAGE);
                game.makeMove(-1, -1); // 切换玩家
                updateBoard(game.getBoardState());
                updateLabels();
                resetTimer(); // 为下一位玩家重置计时器
            }

            if (game.gameOver()) {
                JOptionPane.showMessageDialog(this, "游戏结束！" + game.winner(), "游戏结束", JOptionPane.INFORMATION_MESSAGE);
                stopTimer(); // 停止计时器
            }

            updateBoard(game.getBoardState());
            updateLabels();
        } else {
            JOptionPane.showMessageDialog(this, "这里不能放啊(#`O′)！！！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }



    private void resetTimer() {
        // 停止之前的计时器任务
        if (playerTimer != null) {
            playerTimer.cancel();
        }

        playerTimer = new Timer(); // 初始化新的 Timer
        remainingTime = timeLimit; // 重置剩余时间
        updateTimerLabel(); // 初始显示

        // 定义计时器任务
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    if (remainingTime > 0) {
                        remainingTime--; // 减少剩余时间
                        updateTimerLabel(); // 更新显示
                    } else {
                        playerTimer.cancel(); // 停止计时器
                        handleTimeout(); // 触发超时处理
                    }
                });
            }
        };

        // 每秒执行一次任务
        playerTimer.scheduleAtFixedRate(timerTask, 0, 1000);
    }
    private void updateTimerLabel() {
        timeLabel.setText("剩余时间：" + remainingTime + " 秒");
    }
    private void stopTimer() {
        if (playerTimer != null) {
            playerTimer.cancel();
            playerTimer = null;
        }
    }
    private void handleTimeout() {
        JOptionPane.showMessageDialog(this, player + "超时！轮到下一位玩家。", "超时提醒", JOptionPane.WARNING_MESSAGE);

        // 切换到下一位玩家
        game.makeMove(-1, -1);
        player = game.getCurrentPlayer() == 1 ? "黑棋" : "白棋";

        // 检查下一玩家是否有合法行棋位置
        if (game.noValidMoves(game.getCurrentPlayer())) {
            JOptionPane.showMessageDialog(this, player + "没位置啦！！下一个。", "提示", JOptionPane.INFORMATION_MESSAGE);

            // 再次切换到另一位玩家
            game.makeMove(-1, -1);
            player = game.getCurrentPlayer() == 1 ? "黑棋" : "白棋";

            // 如果双方都无合法行棋位置，结束游戏
            if (game.gameOver()) {
                JOptionPane.showMessageDialog(this, "游戏结束！" + game.winner(), "游戏结束", JOptionPane.INFORMATION_MESSAGE);
                stopTimer(); // 停止计时器
                return;
            }
        }

        // 更新棋盘和计时器
        updateBoard(game.getBoardState());
        updateLabels();
        resetTimer(); // 为下一位玩家重新启动计时器
    }
    private void updateLabels() {
        stepCountLabel.setText("当前步数：" + step);
        playerLabel.setText("当前轮到：" + player);
        whiteCount.setText("白棋：" + game.getWHITE());
        blackCount.setText("黑棋：" + game.getBLACK());
        updateTimerLabel(); // 确保计时器显示实时更新
    }
    // 更新棋盘显示
    private void updateBoard(int[][] boardState) {
        // 清除所有按钮背景色
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                boardButtons[i][j].setBackground(null);
                boardButtons[i][j].setOpaque(false);
            }
        }

        // 更新棋盘状态

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (boardState[i][j] == 1) {
                    // 黑棋 - 使用自定义按钮背景颜色
                    boardButtons[i][j].setIcon(new CircleIcon(Color.BLACK));
                } else if (boardState[i][j] == 2) {
                    // 白棋 - 使用自定义按钮背景颜色
                    boardButtons[i][j].setIcon(new CircleIcon(Color.WHITE));
                } else if (boardState[i][j] == 3) {
                    // 有效行棋位置标出
                    boardButtons[i][j].setBackground(new Color(212, 147, 183));
                    boardButtons[i][j].setOpaque(true);
                    boardButtons[i][j].setIcon(null);
                } else {
                    // 空格 - 不显示棋子
                    boardButtons[i][j].setIcon(null);
                }
            }
        }
    }


    // 创建一个用于绘制圆形棋子的类
    class CircleIcon implements Icon {
        private Color color;

        public CircleIcon(Color color) {
            this.color = color;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            int buttonWidth = c.getWidth();
            int buttonHeight = c.getHeight();
            int iconSize = Math.min(buttonWidth, buttonHeight) - 10; // 减去一些边距以确保棋子不会超出按钮边界
            int xOffset = (buttonWidth - iconSize) / 2; // 计算x轴偏移量以居中
            int yOffset = (buttonHeight - iconSize) / 2; // 计算y轴偏移量以居中

            // 我也不知道怎么调才能居中，，，只能这样了
            int moveLeft = 20;
            int moveUp = 10;
            xOffset -= moveLeft;
            yOffset -= moveUp;

            g.setColor(color);
            g.fillOval(x + xOffset, y + yOffset, iconSize, iconSize); // 在居中位置绘制圆形
        }

        @Override
        public int getIconWidth() {
            return 60;
        }

        @Override
        public int getIconHeight() {
            return 60;
        }
    }

    //计时器相关
    private void setTimeLimit() {
        String input;
        do {
            input = JOptionPane.showInputDialog(
                    this,
                    "限个时（秒）：",
                    "设置时间限制",
                    JOptionPane.QUESTION_MESSAGE
            );

            // 检查用户是否取消
            if (input == null) {
                // 使用默认时间并退出循环
                timeLimit = 300; // 默认值为300秒
                JOptionPane.showMessageDialog(this, "使用默认时间限制：300秒", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            try {
                timeLimit = Integer.parseInt(input);
                if (timeLimit <= 0) {
                    JOptionPane.showMessageDialog(this, "输点能用的数行吗！", "错误", JOptionPane.ERROR_MESSAGE);
                    input = null; // 强制重新输入
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "帅哥/美女，正整数谢谢！", "错误", JOptionPane.ERROR_MESSAGE);
                input = null; // 强制重新输入
            }
        } while (input == null);

        JOptionPane.showMessageDialog(this, "听你的，每步限时：" + timeLimit + " 秒", "好嘟~", JOptionPane.INFORMATION_MESSAGE);
    }
}

