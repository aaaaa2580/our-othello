package org.Othello;

import org.Othello.Game.ReversiGame;
import org.Othello.GUI.ReversiGUI;
public class Main {
    public static void main(String[] args) {
        int boardSize = 8;
        ReversiGame game = new ReversiGame(boardSize);
        ReversiGUI gui = new ReversiGUI(boardSize, game);
        gui.setVisible(true);
    }
}
