// Volkan Bora Seki 2021400156 10.05.2023
/*
table class is the matrix shows the current situation.
 */
public class Table {
    public int row,col;
    public int[][] matris = new int[row][col];

    public void setTable(int[][] table) {this.matris = table;}
    public Table(int row,int col){
        this.row = row;
        this.col = col;
    }

}
