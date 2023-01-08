package four;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*
Improvements can be made in separating the code in self-contained classes outside the ConnectFour class,
though they would still rely on the global array of cells.
 */

public class ConnectFour extends JFrame {
    public static final int ROWS = 6;
    public static final int COLS = 7;
    public static Cell[][] buttons = new Cell[ROWS][COLS];
    public ConnectFour() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setTitle("Connect Four");

        //create the grid
        JPanel grid = new JPanel();
        grid.setLayout(new GridLayout(ROWS,COLS));
        String name;

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                name = Character.toString(j + 'A') + (ROWS - i );
                buttons[i][j] = new Cell(name);
                grid.add(buttons[i][j]);
            }
        }

        //create reset button
        JPanel reset = new JPanel();
        reset.setLayout(new FlowLayout(FlowLayout.RIGHT));
        ButtonReset resetButton = new ButtonReset();
        reset.add(resetButton);

        //all together
        setLayout(new BorderLayout());
        add(grid, BorderLayout.CENTER);
        add(reset, BorderLayout.SOUTH);
        setVisible(true);
    }

    public static void main(String[] args) {
        ConnectFour frame = new ConnectFour();
    }

    class Cell extends JButton {
        /*
        The Cell class is responsible for modifying the board
        The static variable movesLeft keeps track of whether a new cell can be activated.
        It initially allows for as many moves as cells on the board, but it can be overwritten by the endGame method
        If the parity of movesLeft is the same as the parity of the number of cells, the next piece will be an X,
        otherwise an O
         */
        private final static clickButton buttonListener = new clickButton();
        static int movesLeft = ROWS * COLS;

        public Cell(String name) {
            super();
            this.setText(" ");
            this.setName("Button" + name);
            this.setFocusPainted(false);
            this.addActionListener(buttonListener);
            this.setBackground(Color.lightGray);
        }

        public static void resetPlayOrder() {
            movesLeft = ROWS*COLS;
        }
        private void fillCell () {
            //if (!this.getText().equals(" ")) return;
            if (movesLeft % 2 == ROWS * COLS % 2) {
                this.setText("X");
            }
            else {
                this.setText("O");
            }
            movesLeft--;
        }

        private int getCellColumn () {
            return this.getName().charAt(6) - 'A';
        }

        public static int lastEmptyRowInColumn (int column) {
            for (int i = ROWS - 1; i >= 0; i--) {
                if (buttons[i][column].getText().equals(" ")) {
                    return i;
                }
            }
            return -1;
        }

        //does not need to be a class, if it does not need to be private
        private static class clickButton implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (movesLeft == 0) return;
                Cell cell = ((Cell)e.getSource());
                int col = cell.getCellColumn();
                int row = lastEmptyRowInColumn(col);
                if (row == -1) return;
                buttons[row][col].fillCell();
                CheckWin.checkWin();
            }

        }
    }

    class ButtonReset extends JButton implements ActionListener {
        public ButtonReset() {
            super();
            this.setText("Reset");
            this.setName("ButtonReset");
            this.addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            for (var row : buttons) {
                for (Cell cell : row) {
                    cell.setBackground(Color.lightGray);
                    cell.setText(" ");
                    Cell.resetPlayOrder();
                }
            }
        }
    }

    class CheckWin {
        public static void checkWin() {
            //check all cells if they are the starting point of a winning line
            //return bottem-left-most if two exist
            for (int i = ROWS - 1; i >= 0; i--) {
                for (int j = 0; j < COLS; j++) {
                    //avoid empty cells
                    if (buttons[i][j].getText().equals(" ")) continue;
                    Direction foundDir = findFour(i, j);
                    //Direction(0,0) is return of findFour on unsuccessful find
                    if (!foundDir.equals(new Direction(0,0))) endGame(i, j, foundDir);
                }
            }
        }

        private static Direction findFour(int startRow, int startCol) {
            //all possible lines and diagonals from a given point
            final Direction[] directions = {
                    new Direction(1,0), new Direction(1,1), new Direction (0,1),
                    new Direction(-1,1), new Direction(-1, 0), new Direction(-1,-1),
                    new Direction (0, -1), new Direction(1, -1)
            };

            String checkedValue = buttons[startRow][startCol].getText();

            for (Direction dir : directions) {
                //check boundaries
                if (startRow + 3 * dir.row >= ROWS || startRow + 3 * dir.row < 0) continue;
                if (startCol + 3 * dir.col >= COLS || startCol + 3 * dir.col < 0) continue;

                //check if the next 3 cells in the direction are the same
                boolean win = true;
                for (int i = 1; i < 4; i++) {
                    if (!buttons[startRow + i * dir.row][startCol + i * dir.col].getText().equals(checkedValue)) {
                        win = false;
                        break;
                    }
                }
                if (win) {
                    return dir;
                }
            }
            return new Direction(0,0);
        }

        private static void endGame(int startRow, int startCol, Direction foundDir) {

            for (int i = 0; i < 4; i++) {
                buttons[startRow + i * foundDir.row][startCol + i * foundDir.col].setBackground(Color.GREEN);
            }
            Cell.movesLeft = 0;
        }
    }
}

class Direction {
    int row;
    int col;

    public Direction (int i, int j) {
        row = i;
        col = j;
    }
    public boolean equals (Direction dir) {
        return this.row == dir.row && this.col == dir.col;
    }
}



