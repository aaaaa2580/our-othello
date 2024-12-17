
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
    public int step=0;
    public int[][][] documentation=new int[64][8][8];
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
            previous(documentation,step,board);
            board[row][col] = currentPlayer;

            flipDiscs(row, col, currentPlayer);
            changePlayer();

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
            if (board[row + m * i][col + n * i] == EMPTY||board[row + m * i][col + n * i] == RED) {
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
    //返回board的值
    public int[][] getBoardState() {
        return board;
    }
    //返回当前棋子颜色
    public int getCurrentPlayer() {
        return currentPlayer;}
    //重启一局
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
    //悔棋三维数组法，存储每一步操作过后的棋盘状态
    public void previous(int[][][] doc,int step,int[][] b) {
        this.step = step;
        for (int i = 0; i < s; i++) {
            for (int j = 0; j < s; j++) {
                doc[step][i][j] = b[i][j];
            }
        }
    }
    //用于清除悔棋后的最后一步状态
    public int[][] getPrevious() {
        if (step > 0) { // 检查是否还有可悔棋的步数
            // 恢复棋盘状态为上一状态
            for (int i = 0; i < s; i++) {
                for (int j = 0; j < s; j++) {
                    board[i][j] = documentation[step-1][i][j];
                    documentation[step][i][j] = 0; // 清除当前步的存储数据
                }
            }
            step--; // 回退一步
            changePlayer();
        }
        return board; // 返回更新后的棋盘状态
    }
    public int[][][] getDocumentation(){
        return documentation;
    }
    public int[][] setDocumentation(Object a){
        int[][][] doc=(int[][][])a;
        documentation=doc.clone();
       for(int i=63;i>0;i--){
           if(doc[i][4][4]!=EMPTY){
               step=i;
               break;
           }
       }
        board=doc[step];
        return board;
    }
    public int getStep(Object a){
        int s=0;
        int[][][] doc=(int[][][])a;
        documentation=doc;
        for(int i=63;i>0;i--){
            if(doc[i][4][4]!=EMPTY){
                s=i;
                break;
            }
        }
        return s;
    }
}
