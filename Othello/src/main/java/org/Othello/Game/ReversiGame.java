package org.Othello.Game;
import java.util.ArrayList;

public class ReversiGame {
    private int[][] board;
    private int currentPlayer;
    private final int EMPTY = 0;
    private final int BLACK = 1;
    private final int WHITE = 2;
    private final int RED = 3;
    private final int s;
    private int step=0;
    private ArrayList<Object> flippedPiecesList;

    public ReversiGame(int size) {
        this.board = new int[size][size];
        this.s = size;
        initializeBoard();
        currentPlayer = BLACK;
    }

    //棋盘初始化（摆放最开始的2黑2白）
    private void initializeBoard() {
        int mid = s / 2;
        board[mid - 1][mid - 1] = WHITE;
        board[mid - 1][mid] = BLACK;
        board[mid][mid - 1] = BLACK;
        board[mid][mid] = WHITE;
    }
    //判断有效移动
    public boolean makeMove(int row, int col) {
        // 判断是否超出边界
        if (row < 0 || col < 0 || row >= s || col >= s) {
            return false;
        }
        if (isValidMove(row, col, currentPlayer)||board[row][col] == RED) {
            board[row][col] = currentPlayer;
            flipDiscs(row, col, currentPlayer);
            changePlayer();
            document(row, col);
            step++;
            return true;
        }
        return false;
    }
    //切换当前玩家
    public void changePlayer() {
        currentPlayer = (currentPlayer == BLACK) ? WHITE : BLACK;
    }

    // 判断有效移动
    private boolean isValidMove(int row, int col, int player) {
        // 判断是否超出边界或目标位置是否为空
        if (row < 0 || col < 0 || row >= s || col >= s || board[row][col] != EMPTY) {
            if(board[row][col] == 3) {
                return true;
            }
            return false;
        }

        int count = 0;
        // 检查八个方向的可行性
        count += stereotype(row, col, 0, 1, player);   // 行右
        count += stereotype(row, col, 0, -1, player);  // 行左
        count += stereotype(row, col, 1, 0, player);   // 列下
        count += stereotype(row, col, -1, 0, player);  // 列上
        count += stereotype(row, col, 1, 1, player);   // 右下
        count += stereotype(row, col, -1, -1, player); // 左上
        count += stereotype(row, col, -1, 1, player);  // 右上
        count += stereotype(row, col, 1, -1, player);  // 左下
        // 只要有一个方向可行即合法
        return count > 0;
    }

    // 判断是否有效移动逻辑，p, q 是方向向量
    private int stereotype(int row, int col, int p, int q, int player) {
        boolean opponentFound = false; // 是否找到对手的棋子
        int x = row + p, y = col + q;  // 下一个要检查的位置
        while (x >= 0 && y >= 0 && x < s && y < s) {
            if (board[x][y] == EMPTY) { // 遇到空格，结束检查
                break;
            }
            if (board[x][y] == (player == BLACK ? WHITE : BLACK)) {
                opponentFound = true; // 发现对手棋子
            } else if (opponentFound && board[x][y] == player) {
                return 1; // 有对手棋子包围并回到己方棋子，合法
            }
            else {
                break; // 遇到其他情况，终止
            }
            // 移动到下一个位置
            x += p;
            y += q;
        }
        return 0; // 该方向无效
    }

    //标记有效行棋位置
    public void hint() {
        back();
        for (int i = 0; i < s; i++) {
            for (int j = 0; j < s; j++) {
                // 只检查当前玩家的合法落子位置
                if (isValidMove(i, j, currentPlayer)) {
                    board[i][j] = 3;
                }
            }
        }
    }
    //清除当前棋盘上的提示有效行棋位置
    public void back() {
        for (int i = 0; i < s; i++) {
            for (int j = 0; j < s; j++) {
                if (board[i][j] == RED) {
                    board[i][j] = EMPTY; // 清除标记的棋盘位置
                }
            }
        }
    }


    //翻转的逻辑判断
    private void stereotype2(int row, int col, int player, int m, int n) {
        for (int i = 1; row + m * i < s && col + n * i < s && row + m * i >= 0 && col + n * i >= 0; i++) {
            if (board[row + m * i][col + n * i] == EMPTY) {
                break;
            }
            if (board[row + m * i][col + n * i] == player) {
                for (int j = 1; j < i; j++) {
                    board[row + m * j][col + n * j] = player;
                }
                break; // 确保翻转只在找到对方棋子后进行
            }
        }
    }
    //翻转指定棋子
    private void flipDiscs(int row, int col, int player) {
        //行向右
        stereotype2(row, col, player, 0, 1);
        //行向左
        stereotype2(row, col, player, 0, -1);
        //列向下
        stereotype2(row, col, player, 1, 0);
        //列向上
        stereotype2(row, col, player, -1, 0);
        //右下角
        stereotype2(row, col, player, 1, 1);
        //左上角
        stereotype2(row, col, player, -1, -1);
        //右上角
        stereotype2(row, col, player, -1, 1);
        //左下角
        stereotype2(row, col, player, 1, -1);
    }

    //判断游戏是否结束
    public boolean gameOver() {
        //黑白棋都没有合法位置则游戏结束
        boolean blackHasMoves = !noValidMoves(BLACK);
        boolean whiteHasMoves = !noValidMoves(WHITE);
        return !blackHasMoves && !whiteHasMoves;
    }

    //判别不合法操作
   public boolean noValidMoves(int color) {
        for (int i = 0; i < s; i++) {
            for (int j = 0; j < s; j++) {
                if (isValidMove(i, j, color)) {
                    return false;
                }
            }
        }
        return true;
    }
    //获取获胜方及游戏结束时黑白棋子数量
    public String winner() {
        int black = 0;
        int white = 0;
        for (int i = 0; i < s; i++) {
            for (int j = 0; j < s; j++) {
                if (board[i][j] == BLACK) {
                    black++;
                }
                if (board[i][j] == WHITE) {
                    white++;
                }
            }
        }
        if (black > white) {
            return "黑棋获胜!\n 黑棋: " + black + " 白棋: " + white;
        } else if (black < white) {
            return "白棋获胜!\n 黑棋: " + black + " 白棋: " + white;
        } else {
            return "打平力！\n 黑棋: " + black + " 白棋:" + white;
        }
    }
    //获得棋盘上黑棋数量
    public int getBLACK() {
        int black = 0;
        for (int i = 0; i < s; i++) {
            for (int j = 0; j < s; j++) {
                if (board[i][j] == BLACK) {
                    black++;
                }
            }
        }
        return black;
    }
    //获得棋盘上白棋数量
    public int getWHITE(){
        int white=0;
        for (int i = 0; i < s; i++) {
            for (int j = 0; j < s; j++) {
                if (board[i][j] == WHITE) {
                    white++;
                }
            }
        }
        return white;
    }


    //记录走过的步骤
    private ArrayList<Integer> rowList = new ArrayList<>();
    private ArrayList<Integer> colList = new ArrayList<>();
    private ArrayList<Integer> colorList = new ArrayList<>();
    private void document(int row, int col) {
        rowList.add(step,row) ;
        colList.add(step,col);
        colorList.add(step,currentPlayer);

    }
    //返回board的值
    public int[][] getBoardState() {
        return board;
    }
    //悔棋方法函数
    public int[][] getPreciousBoardState() {
        if (step > 0) {
            // 清除记录的最后一步
            rowList.remove(step);
            colList.remove(step);
            colorList.remove(step);
        }
        return getBoardState();
    }
    public void replayFromStart() {
        // Step 1: 重置棋盘状态到初始状态
        resetGame();

        // Step 2: 遍历所有已记录的操作并重新执行
        for (int i = 0; i < step; i++) {
            int row = rowList.get(i);
            int col = colList.get(i);
            //int color = colorList.get(i);

            // 使用直接的棋盘操作逻辑进行回放
            makeMove(row, col);
            getBoardState(); // 更新 GUI
        }

        // Step 3: 设置当前玩家为下一步的玩家
        currentPlayer = (colorList.get(step - 1) == BLACK) ? WHITE : BLACK;
    }


    public int getCurrentPlayer() {
    return currentPlayer;}
    public void resetGame() {
        for (int i = 0; i < s; i++) {
            for (int j = 0; j < s; j++) {
                board[i][j] = EMPTY;
            }
        }
        initializeBoard(); // 重新初始化棋盘
        currentPlayer = BLACK; // 重置当前玩家为黑棋
        step = 0; // 重置步数
    }

    public int[][][] documentation=new int[64][8][8];
    //悔棋三维数组法
    public void previous(int[][][] doc,int step,int[][] b) {
        this.step = step;
        for (int i = 0; i < 64; i++) {
            int mid = s / 2;
            doc[i][mid - 1][mid - 1] = WHITE;
            doc[i][mid - 1][mid] = BLACK;
            doc[i][mid][mid - 1] = BLACK;
            doc[i][mid][mid] = WHITE;
        }
        doc[step]=b;
    }
    public  int[][] getPrevious(){
        board=documentation[step-1];
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                documentation[step][i][j]=0;
            }
        }
        return board;
    }

}
