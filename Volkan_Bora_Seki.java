// Volkan Bora Seki 2021400156 10.05.2023
/*
Takes moves as input from user
Checks they are convenient
Adds stone or asks decent move from user
calculates volumes determines the islands
 */


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Volkan_Bora_Seki {
    public static String fileName = "input.txt";




    public static void main(String[] args) throws FileNotFoundException {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.printf("%s can not be found.", fileName);
            System.exit(0);
        }
        Scanner inputFile = new Scanner(file);
        String[] colrow = inputFile.nextLine().split(" ");
        int col = Integer.valueOf(colrow[0]);
        int row = Integer.valueOf(colrow[1]);
        String[] colList = colListMaker(col); // the names of cols as array
        int[][] tableTemp = tablemaker(); // takes table from file to 2d array
        Table table = new Table(row, col);
        table.setTable(tableTemp);
        // alphabet Lists to use printing
        String[] alphabetWithSpace = new String[27];
        String[] alphabetUpper = new String[26];
        String[] alphabetUpperWithSpace = new String[27];
        alphabetUpperWithSpace[0] = " ";
        int alphabeIndex = 0;
        int alphabeUpperIndex = 0;
        alphabetWithSpace[0] = " ";
        for (char i = 'a'; i <= 'z'; i++) {
            alphabetWithSpace[alphabeIndex + 1] = Character.toString(i);
            alphabeIndex++;
        }
        for (char i = 'A'; i <= 'Z'; i++) {
            alphabetUpper[alphabeUpperIndex] = Character.toString(i);
            alphabeUpperIndex++;
            alphabetUpperWithSpace[alphabeUpperIndex] = Character.toString(i);
        }
        String[] alphabetLakeNameList = new String[26*27];
        int indexAlphabetLakeNameList = 0;
        for(String letter:alphabetUpperWithSpace){
            for(String letter2:alphabetUpper){
                alphabetLakeNameList[indexAlphabetLakeNameList] = letter + letter2;
                indexAlphabetLakeNameList++;
            }
        }


        tablePrinter(table.matris, row, colList); // printing table
        moveMaker(row,col,colList,tableTemp,table); // Makes move, detailed description in below
        int maxLand = maxValueFinder(table.matris);
        // water table is the table all elements completed to the max table height with water. If max height == 6 and one land 2 height, in water
        // table that land seems 4, this is bigger because beside from map water leaks. I arraged beside 0 by making side coors 0
        int[][] watertableTemp = new int[row + 2][col + 2];
        for (int i = 0; i < row + 2; i++) {
            for (int j = 0; j < col + 2; j++) {
                if (i == 0 || j == 0 || j == col + 1 || i == row + 1) watertableTemp[i][j] = 0;
                else watertableTemp[i][j] = maxLand - table.matris[i - 1][j - 1];
            }
        }
        Table watertable = new Table(row + 2, col + 2);
        watertable.matris = watertableTemp;
        int[][] landAndWaterTableTemp = new int[row + 2][col + 2]; // sum of land and water.
        for (int i = 0; i < row + 2; i++) {
            for (int j = 0; j < col + 2; j++) {
                if (i == 0 || j == 0 || j == col + 1 || i == row + 1) watertable.matris[i][j] = 0;
                else landAndWaterTableTemp[i][j] = watertable.matris[i][j] + table.matris[i - 1][j - 1];
            }
        }
        Table landAndWaterTable = new Table(row + 2, col + 2);
        landAndWaterTable.setTable(landAndWaterTableTemp);
        watertableTemp = allChecker(watertableTemp, landAndWaterTable.matris, row, col, table); // leaks are done.
        watertable.setTable(watertableTemp);
        for (int i = 0; i < row + 2; i++) {
            for (int j = 0; j < col + 2; j++) {
                if (i == 0 || j == 0 || j == col + 1 || i == row + 1) watertable.matris[i][j] = 0;
                else landAndWaterTableTemp[i][j] = watertable.matris[i][j] + table.matris[i - 1][j - 1];
            }
        }
        landAndWaterTable.setTable(landAndWaterTableTemp);
        String[][] tableStringListTemp = new String[row][col]; // to make letter height grading, I copied  int aray to string array.
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                tableStringListTemp[i][j] = Integer.toString(watertable.matris[i+1][j+1]);
            }
        }
        boolean[][] truthTable = new boolean[row][col];
        for(int i1 = 0; i1 < row; i1++){for(int j1= 0; j1 < col; j1++){truthTable[i1][j1] = false;}}
        int indexforLoop = 0;
        double score = 0;
        // Since I wasnt able to do it without recursion. Recursive neighboor land finder.
        for(int i = 0; i < row; i++){
            for(int j= 0; j < col; j++){
                if(!truthTable[i][j]){
                    double a = rec_set_neighboor(watertable.matris,i,j,truthTable,row,col,alphabetLakeNameList[indexforLoop],tableStringListTemp,0);
                    if(watertable.matris[i+1][j+1] == 0)tableStringListTemp[i][j] = Integer.toString(table.matris[i][j]);
                    if(a > 0) {
                        indexforLoop++;
                    }
                }
            }
        }
        ArrayList<String> coorArrayList = new ArrayList<>();
        ArrayList<Double> scoreArrayList = new ArrayList<>();
        // calculating the score
        for(int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if(watertable.matris[i+1][j+1] != 0){
                    if(!coorArrayList.contains(tableStringListTemp[i][j])){
                        coorArrayList.add(tableStringListTemp[i][j]);
                        scoreArrayList.add(watertable.matris[i+1][j+1]+0.0);
                    }
                    else {
                        scoreArrayList.set(coorArrayList.indexOf(tableStringListTemp[i][j]),scoreArrayList.get(coorArrayList.indexOf(tableStringListTemp[i][j]))+watertable.matris[i+1][j+1]);
                    }
                }
            }
        }
        for(double scoresum: scoreArrayList) score+= Math.sqrt(scoresum);
        tableSTRPrinter(tableStringListTemp,row,colList);
        System.out.println("Final score: " + String.format("%.2f", score).replace(',','.'));

    }

    public static String[] colListMaker(int input)
    // makes the col list to print properly
    {
        String[] alphabetWithSpace = new String[27];
        int alphabeIndex = 0;
        for (char i = 'a'; i <= 'z'; i++) {
            alphabetWithSpace[alphabeIndex + 1] = Character.toString(i);
            alphabeIndex++;
        }
        alphabetWithSpace[0] = " ";
        String[] rowlist = new String[input];
        for (int i = 0; i < input; i++) {
            rowlist[i] = " " + alphabetWithSpace[i / 26] + alphabetWithSpace[1 + i % 26];
        }
        return rowlist;
    }

    public static int[][] tablemaker() throws FileNotFoundException
    //reads table from file
    {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.printf("%s can not be found.", fileName);
            System.exit(0);
        }
        Scanner inputFile = new Scanner(file);
        String[] colrow = inputFile.nextLine().split(" ");
        int col = Integer.valueOf(colrow[0]);
        int row = Integer.valueOf(colrow[1]);
        int[][] table = new int[row][col];
        for (int i = 0; i < row; i++) {
            String line = inputFile.nextLine();
            String[] lineEl = line.split(" ");
            for (int index = 0; index < col; index++) {
                table[i][index] = Integer.valueOf(lineEl[index]);
            }
        }
        return table;
    }

    public static String[] moveListMaker(int row, int col)
    // determines decent moves
    {
        String[] movementlist = new String[row * col];
        int moveIndex = 0;
        String[] colList = colListMaker(col);
        for (String letter : colList) {
            for (int i = 0; i < row; i++) {
                String move = (letter + i).strip();
                movementlist[moveIndex] = move;
                moveIndex++;
            }
        }
        return movementlist;
    }

    public static void tablePrinter(int[][] table, int row, String[] colList)
    // Prints tables in int[][] format properly
    {
        for (int i = 0; i <= row; i++) {
            if (i != row) {
                if (i < 10) System.out.print("  " + i + " ");
                if (i < 100 && i >= 10) {
                    System.out.print(" " + i + " ");
                }
                if (i >= 100) System.out.print(i + " ");
                for (int j : table[i]) {
                    System.out.print(" " + j + " ");
                }
                System.out.println();
            }
            if (i == row) {
                System.out.print("   ");
                for (int k = 0; k < colList.length; k++) {
                    System.out.print(colList[k]);
                }
                System.out.println(" ");
            }

        }
    }

    public static void tableSTRPrinter(String[][] table, int row, String[] colList)
    // prints tables in String[][] format properly
    {
        for (int i = 0; i <= row; i++) {
            if (i != row) {
                if (i < 10) System.out.print("  " + i );
                if (i < 100 && i >= 10) {
                    System.out.print(" " + i);
                }
                if (i >= 100) System.out.print(i);
                for (String j : table[i]) {
                    if(j.length() == 2) {
                        System.out.print(" " + j);
                        continue;
                    }
                    System.out.print("  " +j);
                }
                System.out.println();
            }
            if (i == row) {
                System.out.print("   ");
                for (int k = 0; k < colList.length; k++) {
                    System.out.print(colList[k]);
                }
                System.out.println(" ");
            }

        }
    }


    public static String[] stringListMaker(String s)
    // converts charList to String List
    {
        char[] charlist = s.toCharArray();
        String[] sList = new String[charlist.length];
        int index = 0;
        for (char c : charlist) {
            sList[index] = Character.toString(c);
            index++;
        }
        return sList;
    }

    public static void moveMaker(int row, int col, String[] colList, int[][] tableTemp, Table table)
    //Takes move input from user and adds stone if it is a decent move.
    {
        Scanner inputUser = new Scanner(System.in);
        String[] movementList = moveListMaker(row, col);
        for (int i = 1; i <= 10; i++) {
            System.out.print("Add stone " + i + " / 10 to coordinate:");
            String potentialMove = inputUser.next();
            boolean recentMove = false;
            for (String move : movementList) {
                if (move.strip().equals(potentialMove)) recentMove = true;
            }
            String[] numericList = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
            if (!recentMove) {
                i--;
                System.out.println("Not a valid step!");
            }
            if (recentMove) {
                String[] actualMoveList = stringListMaker(potentialMove);
                String alphabeticMove = "";
                String numericMove = "";
                for (String str : actualMoveList) {
                    boolean alphabetic = true;
                    for (String s : numericList) if (s.equals(str)) alphabetic = false;
                    if (alphabetic) alphabeticMove = alphabeticMove + str;
                    if (!alphabetic) numericMove = numericMove + str;
                }
                int colIndex = 0;
                int rowIndex = 0;
                for (int index = 0; index < colList.length; index++) {
                    if (colList[index].strip().equals(alphabeticMove)) colIndex = index;
                }
                for (int index = 0; index < row; index++) {
                    if (Integer.toString(index).equals(numericMove)) rowIndex = index;
                }
                tableTemp[rowIndex][colIndex] += 1;
                table.setTable(tableTemp);
                tablePrinter(table.matris, row, colList);
                System.out.println("---------------");
            }

        }

    }

    public static int maxValueFinder(int[][] table)
    // return the tallest island's height
    {
        int max = 0;
        for (int[] lis : table) {
            for (int number : lis) if (number >= max) max = number;
        }
        return max;
    }

    public static int[][] allChecker(int[][] waterTableTemp, int[][] landAndWaterTableTemp, int row, int col, Table table) {
        // it is a funcion w/8*3 Loop, every loop checks a direction if there is a leak. That makes 8 but I Checked 3 times with changed queue
        // to make sure there wouldnt be any leak.
        for (int colChanger = 0; colChanger < col; colChanger++) {
            for (int rowChanger = row + 1; rowChanger > 1; rowChanger--) {
                // this one checks from right down to up left. Leak direction is always opposit of checking direction
                if (landAndWaterTableTemp[rowChanger][colChanger + 1] < landAndWaterTableTemp[rowChanger - 1][colChanger + 1]) {
                    // if sum of current bigger than sum of next one, current will be rearranging
                    int difference = landAndWaterTableTemp[rowChanger - 1][colChanger + 1] - landAndWaterTableTemp[rowChanger][colChanger + 1];
                    if (difference > landAndWaterTableTemp[rowChanger][colChanger + 1]) {
                        landAndWaterTableTemp[rowChanger - 1][colChanger + 1] = table.matris[rowChanger - 2][colChanger];

                    }
                    if (difference <= landAndWaterTableTemp[rowChanger][colChanger + 1]) {
                        if (rowChanger < row + 1) {
                            if (table.matris[rowChanger - 1 - 1][colChanger] <= landAndWaterTableTemp[rowChanger][colChanger + 1])
                                landAndWaterTableTemp[rowChanger - 1][colChanger + 1] = landAndWaterTableTemp[rowChanger][colChanger + 1];
                            if (table.matris[rowChanger - 1 - 1][colChanger] > landAndWaterTableTemp[rowChanger][colChanger + 1])
                                landAndWaterTableTemp[rowChanger - 1][colChanger + 1] = table.matris[rowChanger - 1 - 1][colChanger];
                        } else
                            landAndWaterTableTemp[rowChanger - 1 - 1][colChanger + 1] = landAndWaterTableTemp[rowChanger][colChanger + 1];
                    }
                }

            }
        }
        for (int colChanger = 0; colChanger < col; colChanger++) {
            for (int rowChanger = 0; rowChanger < row; rowChanger++) {
                if (landAndWaterTableTemp[rowChanger][colChanger + 1] < landAndWaterTableTemp[rowChanger + 1][colChanger + 1]) {
                    int difference = landAndWaterTableTemp[rowChanger + 1][colChanger + 1] - landAndWaterTableTemp[rowChanger][colChanger + 1];
                    if (difference > landAndWaterTableTemp[rowChanger][colChanger + 1]) {
                        landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = table.matris[rowChanger][colChanger];

                    }
                    if (difference <= landAndWaterTableTemp[rowChanger][colChanger + 1]) {
                        if (rowChanger > 0) {
                            if (table.matris[rowChanger][colChanger] <= landAndWaterTableTemp[rowChanger][colChanger + 1])
                                landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = landAndWaterTableTemp[rowChanger][colChanger + 1];
                            if (table.matris[rowChanger][colChanger] > landAndWaterTableTemp[rowChanger][colChanger + 1])
                                landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = table.matris[rowChanger][colChanger];
                        } else
                            landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = landAndWaterTableTemp[rowChanger][colChanger + 1];
                    }
                }

            }
        }
        for (int rowChanger = 0; rowChanger < row; rowChanger++) {
            for (int colChanger = 0; colChanger < col; colChanger++) {
                if (landAndWaterTableTemp[rowChanger + 1][colChanger] < landAndWaterTableTemp[rowChanger + 1][colChanger + 1]) {
                    int difference = landAndWaterTableTemp[rowChanger + 1][colChanger + 1] - landAndWaterTableTemp[rowChanger + 1][colChanger];
                    if (difference > landAndWaterTableTemp[rowChanger + 1][colChanger]) {
                        landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = table.matris[rowChanger][colChanger];

                    }
                    if (difference <= landAndWaterTableTemp[rowChanger + 1][colChanger]) {
                        if (colChanger > 0) {
                            if (table.matris[rowChanger][colChanger] <= landAndWaterTableTemp[rowChanger + 1][colChanger])
                                landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = landAndWaterTableTemp[rowChanger + 1][colChanger];
                            if (table.matris[rowChanger][colChanger] > landAndWaterTableTemp[rowChanger + 1][colChanger])
                                landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = table.matris[rowChanger][colChanger];
                        } else
                            landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = landAndWaterTableTemp[rowChanger + 1][colChanger];
                    }
                }

            }
        }
        for (int rowChanger = 0; rowChanger < row; rowChanger++) {
            for (int colChanger = col + 1; colChanger > 1; colChanger--) {
                if (landAndWaterTableTemp[rowChanger + 1][colChanger] <= landAndWaterTableTemp[rowChanger + 1][colChanger - 1]) {
                    int difference = landAndWaterTableTemp[rowChanger + 1][colChanger - 1] - landAndWaterTableTemp[rowChanger + 1][colChanger];

                    if (difference > landAndWaterTableTemp[rowChanger + 1][colChanger]) {
                        landAndWaterTableTemp[rowChanger + 1][colChanger - 1] = table.matris[rowChanger][colChanger - 2];

                    }
                    if (difference <= landAndWaterTableTemp[rowChanger + 1][colChanger]) {
                        if (colChanger < col + 1) {
                            if (table.matris[rowChanger][colChanger - 2] <= landAndWaterTableTemp[rowChanger + 1][colChanger])
                                landAndWaterTableTemp[rowChanger + 1][colChanger - 1] = landAndWaterTableTemp[rowChanger + 1][colChanger];
                            if (table.matris[rowChanger][colChanger - 2] > landAndWaterTableTemp[rowChanger + 1][colChanger])
                                landAndWaterTableTemp[rowChanger + 1][colChanger - 1] = table.matris[rowChanger][colChanger - 2];
                        } else
                            landAndWaterTableTemp[rowChanger + 1][colChanger - 1] = landAndWaterTableTemp[rowChanger + 1][colChanger];
                    }
                }

            }
        }

        for(int rowChanger = row; rowChanger > 0; rowChanger --){
            for(int colChanger = col; colChanger > 0; colChanger --){
                if(landAndWaterTableTemp[rowChanger][colChanger] > landAndWaterTableTemp[rowChanger+1][colChanger+1]){
                    if(rowChanger == row || colChanger == col) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                    else{
                        if(table.matris[rowChanger-1][colChanger-1] > landAndWaterTableTemp[rowChanger+1][colChanger+1]) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                        if(table.matris[rowChanger-1][colChanger-1] <= landAndWaterTableTemp[rowChanger+1][colChanger+1]){
                            landAndWaterTableTemp[rowChanger][colChanger] = Math.max(landAndWaterTableTemp[rowChanger+1][colChanger+1],table.matris[rowChanger-1][colChanger-1]);
                        }
                    }
                }
            }
        }
        for(int rowChanger = 1; rowChanger <row+1; rowChanger ++){
            for(int colChanger = col; colChanger > 0; colChanger --){
                if(landAndWaterTableTemp[rowChanger][colChanger] > landAndWaterTableTemp[rowChanger-1][colChanger+1]){
                    if(rowChanger == 1 || colChanger == col) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                    else{
                        if(table.matris[rowChanger-1][colChanger-1] > landAndWaterTableTemp[rowChanger-1][colChanger+1]) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                        if(table.matris[rowChanger-1][colChanger-1] <= landAndWaterTableTemp[rowChanger-1][colChanger+1]){
                            landAndWaterTableTemp[rowChanger][colChanger] = Math.max(landAndWaterTableTemp[rowChanger-1][colChanger+1],table.matris[rowChanger-1][colChanger-1]);
                        }
                    }
                }
            }
        }
        for(int rowChanger = 1; rowChanger <row+1; rowChanger ++){
            for(int colChanger = 1; colChanger < col+1; colChanger ++){
                if(landAndWaterTableTemp[rowChanger][colChanger] > landAndWaterTableTemp[rowChanger-1][colChanger-1]){
                    if(rowChanger == 1 || colChanger == 1) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                    else{
                        if(table.matris[rowChanger-1][colChanger-1] > landAndWaterTableTemp[rowChanger-1][colChanger-1]) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                        if(table.matris[rowChanger-1][colChanger-1] <= landAndWaterTableTemp[rowChanger-1][colChanger-1]){
                            landAndWaterTableTemp[rowChanger][colChanger] = Math.max(landAndWaterTableTemp[rowChanger-1][colChanger-1],table.matris[rowChanger-1][colChanger-1]);
                        }
                    }
                }
            }
        }
        for(int rowChanger = row; rowChanger > 0; rowChanger --){
            for(int colChanger = 1; colChanger < col+1; colChanger ++){
                if(landAndWaterTableTemp[rowChanger][colChanger] > landAndWaterTableTemp[rowChanger+1][colChanger-1]){
                    if(rowChanger == row || colChanger == 1)
                        landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                    else{
                        if(table.matris[rowChanger-1][colChanger-1] > landAndWaterTableTemp[rowChanger+1][colChanger-1]) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                        if(table.matris[rowChanger-1][colChanger-1] <= landAndWaterTableTemp[rowChanger+1][colChanger-1]){
                            landAndWaterTableTemp[rowChanger][colChanger] = Math.max(landAndWaterTableTemp[rowChanger+1][colChanger-1],table.matris[rowChanger-1][colChanger-1]);
                        }
                    }
                }
            }
        }

        for (int rowChanger = 0; rowChanger < row; rowChanger++) {
            for (int colChanger = 0; colChanger < col; colChanger++) {
                if (landAndWaterTableTemp[rowChanger + 1][colChanger] < landAndWaterTableTemp[rowChanger + 1][colChanger + 1]) {
                    int difference = landAndWaterTableTemp[rowChanger + 1][colChanger + 1] - landAndWaterTableTemp[rowChanger + 1][colChanger];
                    if (difference > landAndWaterTableTemp[rowChanger + 1][colChanger]) {
                        landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = table.matris[rowChanger][colChanger];

                    }
                    if (difference <= landAndWaterTableTemp[rowChanger + 1][colChanger]) {
                        if (colChanger > 0) {
                            if (table.matris[rowChanger][colChanger] <= landAndWaterTableTemp[rowChanger + 1][colChanger])
                                landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = landAndWaterTableTemp[rowChanger + 1][colChanger];
                            if (table.matris[rowChanger][colChanger] > landAndWaterTableTemp[rowChanger + 1][colChanger])
                                landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = table.matris[rowChanger][colChanger];
                        } else
                            landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = landAndWaterTableTemp[rowChanger + 1][colChanger];
                    }
                }

            }
        }
        for (int rowChanger = 0; rowChanger < row; rowChanger++) {
            for (int colChanger = col + 1; colChanger > 1; colChanger--) {
                if (landAndWaterTableTemp[rowChanger + 1][colChanger] <= landAndWaterTableTemp[rowChanger + 1][colChanger - 1]) {
                    int difference = landAndWaterTableTemp[rowChanger + 1][colChanger - 1] - landAndWaterTableTemp[rowChanger + 1][colChanger];

                    if (difference > landAndWaterTableTemp[rowChanger + 1][colChanger]) {
                        landAndWaterTableTemp[rowChanger + 1][colChanger - 1] = table.matris[rowChanger][colChanger - 2];

                    }
                    if (difference <= landAndWaterTableTemp[rowChanger + 1][colChanger]) {
                        if (colChanger < col + 1) {
                            if (table.matris[rowChanger][colChanger - 2] <= landAndWaterTableTemp[rowChanger + 1][colChanger])
                                landAndWaterTableTemp[rowChanger + 1][colChanger - 1] = landAndWaterTableTemp[rowChanger + 1][colChanger];
                            if (table.matris[rowChanger][colChanger - 2] > landAndWaterTableTemp[rowChanger + 1][colChanger])
                                landAndWaterTableTemp[rowChanger + 1][colChanger - 1] = table.matris[rowChanger][colChanger - 2];
                        } else
                            landAndWaterTableTemp[rowChanger + 1][colChanger - 1] = landAndWaterTableTemp[rowChanger + 1][colChanger];
                    }
                }

            }
        }
        for (int colChanger = 0; colChanger < col; colChanger++) {
            for (int rowChanger = row + 1; rowChanger > 1; rowChanger--) {
                if (landAndWaterTableTemp[rowChanger][colChanger + 1] < landAndWaterTableTemp[rowChanger - 1][colChanger + 1]) {
                    int difference = landAndWaterTableTemp[rowChanger - 1][colChanger + 1] - landAndWaterTableTemp[rowChanger][colChanger + 1];
                    if (difference > landAndWaterTableTemp[rowChanger][colChanger + 1]) {
                        landAndWaterTableTemp[rowChanger - 1][colChanger + 1] = table.matris[rowChanger - 2][colChanger];

                    }
                    if (difference <= landAndWaterTableTemp[rowChanger][colChanger + 1]) {
                        if (rowChanger < row + 1) {
                            if (table.matris[rowChanger - 1 - 1][colChanger] <= landAndWaterTableTemp[rowChanger][colChanger + 1])
                                landAndWaterTableTemp[rowChanger - 1][colChanger + 1] = landAndWaterTableTemp[rowChanger][colChanger + 1];
                            if (table.matris[rowChanger - 1 - 1][colChanger] > landAndWaterTableTemp[rowChanger][colChanger + 1])
                                landAndWaterTableTemp[rowChanger - 1][colChanger + 1] = table.matris[rowChanger - 1 - 1][colChanger];
                        } else
                            landAndWaterTableTemp[rowChanger - 1 - 1][colChanger + 1] = landAndWaterTableTemp[rowChanger][colChanger + 1];
                    }
                }

            }
        }
        for (int colChanger = 0; colChanger < col; colChanger++) {
            for (int rowChanger = 0; rowChanger < row; rowChanger++) {
                if (landAndWaterTableTemp[rowChanger][colChanger + 1] < landAndWaterTableTemp[rowChanger + 1][colChanger + 1]) {
                    int difference = landAndWaterTableTemp[rowChanger + 1][colChanger + 1] - landAndWaterTableTemp[rowChanger][colChanger + 1];
                    if (difference > landAndWaterTableTemp[rowChanger][colChanger + 1]) {
                        landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = table.matris[rowChanger][colChanger];

                    }
                    if (difference <= landAndWaterTableTemp[rowChanger][colChanger + 1]) {
                        if (rowChanger > 0) {
                            if (table.matris[rowChanger][colChanger] <= landAndWaterTableTemp[rowChanger][colChanger + 1])
                                landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = landAndWaterTableTemp[rowChanger][colChanger + 1];
                            if (table.matris[rowChanger][colChanger] > landAndWaterTableTemp[rowChanger][colChanger + 1])
                                landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = table.matris[rowChanger][colChanger];
                        } else
                            landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = landAndWaterTableTemp[rowChanger][colChanger + 1];
                    }
                }

            }
        }

        for(int rowChanger = 1; rowChanger <row+1; rowChanger ++){
            for(int colChanger = 1; colChanger < col+1; colChanger ++){
                if(landAndWaterTableTemp[rowChanger][colChanger] > landAndWaterTableTemp[rowChanger-1][colChanger-1]){
                    if(rowChanger == 1 || colChanger == 1) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                    else{
                        if(table.matris[rowChanger-1][colChanger-1] > landAndWaterTableTemp[rowChanger-1][colChanger-1]) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                        if(table.matris[rowChanger-1][colChanger-1] <= landAndWaterTableTemp[rowChanger-1][colChanger-1]){
                            landAndWaterTableTemp[rowChanger][colChanger] = Math.max(landAndWaterTableTemp[rowChanger-1][colChanger-1],table.matris[rowChanger-1][colChanger-1]);
                        }
                    }
                }
            }
        }
        for(int rowChanger = row; rowChanger > 0; rowChanger --){
            for(int colChanger = 1; colChanger < col+1; colChanger ++){
                if(landAndWaterTableTemp[rowChanger][colChanger] > landAndWaterTableTemp[rowChanger+1][colChanger-1]){
                    if(rowChanger == row || colChanger == 1)
                        landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                    else{
                        if(table.matris[rowChanger-1][colChanger-1] > landAndWaterTableTemp[rowChanger+1][colChanger-1]) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                        if(table.matris[rowChanger-1][colChanger-1] <= landAndWaterTableTemp[rowChanger+1][colChanger-1]){
                            landAndWaterTableTemp[rowChanger][colChanger] = Math.max(landAndWaterTableTemp[rowChanger+1][colChanger-1],table.matris[rowChanger-1][colChanger-1]);
                        }
                    }
                }
            }
        }
        for(int rowChanger = row; rowChanger > 0; rowChanger --){
            for(int colChanger = col; colChanger > 0; colChanger --){
                if(landAndWaterTableTemp[rowChanger][colChanger] > landAndWaterTableTemp[rowChanger+1][colChanger+1]){
                    if(rowChanger == row || colChanger == col) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                    else{
                        if(table.matris[rowChanger-1][colChanger-1] > landAndWaterTableTemp[rowChanger+1][colChanger+1]) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                        if(table.matris[rowChanger-1][colChanger-1] <= landAndWaterTableTemp[rowChanger+1][colChanger+1]){
                            landAndWaterTableTemp[rowChanger][colChanger] = Math.max(landAndWaterTableTemp[rowChanger+1][colChanger+1],table.matris[rowChanger-1][colChanger-1]);
                        }
                    }
                }
            }
        }
        for(int rowChanger = 1; rowChanger <row+1; rowChanger ++){
            for(int colChanger = col; colChanger > 0; colChanger --){
                if(landAndWaterTableTemp[rowChanger][colChanger] > landAndWaterTableTemp[rowChanger-1][colChanger+1]){
                    if(rowChanger == 1 || colChanger == col) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                    else{
                        if(table.matris[rowChanger-1][colChanger-1] > landAndWaterTableTemp[rowChanger-1][colChanger+1]) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                        if(table.matris[rowChanger-1][colChanger-1] <= landAndWaterTableTemp[rowChanger-1][colChanger+1]){
                            landAndWaterTableTemp[rowChanger][colChanger] = Math.max(landAndWaterTableTemp[rowChanger-1][colChanger+1],table.matris[rowChanger-1][colChanger-1]);
                        }
                    }
                }
            }
        }

        for (int rowChanger = 0; rowChanger < row; rowChanger++) {
            for (int colChanger = 0; colChanger < col; colChanger++) {
                if (landAndWaterTableTemp[rowChanger + 1][colChanger] < landAndWaterTableTemp[rowChanger + 1][colChanger + 1]) {
                    int difference = landAndWaterTableTemp[rowChanger + 1][colChanger + 1] - landAndWaterTableTemp[rowChanger + 1][colChanger];
                    if (difference > landAndWaterTableTemp[rowChanger + 1][colChanger]) {
                        landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = table.matris[rowChanger][colChanger];

                    }
                    if (difference <= landAndWaterTableTemp[rowChanger + 1][colChanger]) {
                        if (colChanger > 0) {
                            if (table.matris[rowChanger][colChanger] <= landAndWaterTableTemp[rowChanger + 1][colChanger])
                                landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = landAndWaterTableTemp[rowChanger + 1][colChanger];
                            if (table.matris[rowChanger][colChanger] > landAndWaterTableTemp[rowChanger + 1][colChanger])
                                landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = table.matris[rowChanger][colChanger];
                        } else
                            landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = landAndWaterTableTemp[rowChanger + 1][colChanger];
                    }
                }

            }
        }
        for (int colChanger = 0; colChanger < col; colChanger++) {
            for (int rowChanger = 0; rowChanger < row; rowChanger++) {
                if (landAndWaterTableTemp[rowChanger][colChanger + 1] < landAndWaterTableTemp[rowChanger + 1][colChanger + 1]) {
                    int difference = landAndWaterTableTemp[rowChanger + 1][colChanger + 1] - landAndWaterTableTemp[rowChanger][colChanger + 1];
                    if (difference > landAndWaterTableTemp[rowChanger][colChanger + 1]) {
                        landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = table.matris[rowChanger][colChanger];

                    }
                    if (difference <= landAndWaterTableTemp[rowChanger][colChanger + 1]) {
                        if (rowChanger > 0) {
                            if (table.matris[rowChanger][colChanger] <= landAndWaterTableTemp[rowChanger][colChanger + 1])
                                landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = landAndWaterTableTemp[rowChanger][colChanger + 1];
                            if (table.matris[rowChanger][colChanger] > landAndWaterTableTemp[rowChanger][colChanger + 1])
                                landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = table.matris[rowChanger][colChanger];
                        } else
                            landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = landAndWaterTableTemp[rowChanger][colChanger + 1];
                    }
                }

            }
        }
        for (int colChanger = 0; colChanger < col; colChanger++) {
            for (int rowChanger = row + 1; rowChanger > 1; rowChanger--) {
                if (landAndWaterTableTemp[rowChanger][colChanger + 1] < landAndWaterTableTemp[rowChanger - 1][colChanger + 1]) {
                    int difference = landAndWaterTableTemp[rowChanger - 1][colChanger + 1] - landAndWaterTableTemp[rowChanger][colChanger + 1];
                    if (difference > landAndWaterTableTemp[rowChanger][colChanger + 1]) {
                        landAndWaterTableTemp[rowChanger - 1][colChanger + 1] = table.matris[rowChanger - 2][colChanger];

                    }
                    if (difference <= landAndWaterTableTemp[rowChanger][colChanger + 1]) {
                        if (rowChanger < row + 1) {
                            if (table.matris[rowChanger - 1 - 1][colChanger] <= landAndWaterTableTemp[rowChanger][colChanger + 1])
                                landAndWaterTableTemp[rowChanger - 1][colChanger + 1] = landAndWaterTableTemp[rowChanger][colChanger + 1];
                            if (table.matris[rowChanger - 1 - 1][colChanger] > landAndWaterTableTemp[rowChanger][colChanger + 1])
                                landAndWaterTableTemp[rowChanger - 1][colChanger + 1] = table.matris[rowChanger - 1 - 1][colChanger];
                        } else
                            landAndWaterTableTemp[rowChanger - 1 - 1][colChanger + 1] = landAndWaterTableTemp[rowChanger][colChanger + 1];
                    }
                }

            }
        }
        for (int rowChanger = 0; rowChanger < row; rowChanger++) {
            for (int colChanger = col + 1; colChanger > 1; colChanger--) {
                if (landAndWaterTableTemp[rowChanger + 1][colChanger] <= landAndWaterTableTemp[rowChanger + 1][colChanger - 1]) {
                    int difference = landAndWaterTableTemp[rowChanger + 1][colChanger - 1] - landAndWaterTableTemp[rowChanger + 1][colChanger];

                    if (difference > landAndWaterTableTemp[rowChanger + 1][colChanger]) {
                        landAndWaterTableTemp[rowChanger + 1][colChanger - 1] = table.matris[rowChanger][colChanger - 2];

                    }
                    if (difference <= landAndWaterTableTemp[rowChanger + 1][colChanger]) {
                        if (colChanger < col + 1) {
                            if (table.matris[rowChanger][colChanger - 2] <= landAndWaterTableTemp[rowChanger + 1][colChanger])
                                landAndWaterTableTemp[rowChanger + 1][colChanger - 1] = landAndWaterTableTemp[rowChanger + 1][colChanger];
                            if (table.matris[rowChanger][colChanger - 2] > landAndWaterTableTemp[rowChanger + 1][colChanger])
                                landAndWaterTableTemp[rowChanger + 1][colChanger - 1] = table.matris[rowChanger][colChanger - 2];
                        } else
                            landAndWaterTableTemp[rowChanger + 1][colChanger - 1] = landAndWaterTableTemp[rowChanger + 1][colChanger];
                    }
                }

            }
        }

        for(int rowChanger = 1; rowChanger <row+1; rowChanger ++){
            for(int colChanger = 1; colChanger < col+1; colChanger ++){
                if(landAndWaterTableTemp[rowChanger][colChanger] > landAndWaterTableTemp[rowChanger-1][colChanger-1]){
                    if(rowChanger == 1 || colChanger == 1) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                    else{
                        if(table.matris[rowChanger-1][colChanger-1] > landAndWaterTableTemp[rowChanger-1][colChanger-1]) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                        if(table.matris[rowChanger-1][colChanger-1] <= landAndWaterTableTemp[rowChanger-1][colChanger-1]){
                            landAndWaterTableTemp[rowChanger][colChanger] = Math.max(landAndWaterTableTemp[rowChanger-1][colChanger-1],table.matris[rowChanger-1][colChanger-1]);
                        }
                    }
                }
            }
        }
        for(int rowChanger = 1; rowChanger <row+1; rowChanger ++){
            for(int colChanger = col; colChanger > 0; colChanger --){
                if(landAndWaterTableTemp[rowChanger][colChanger] > landAndWaterTableTemp[rowChanger-1][colChanger+1]){
                    if(rowChanger == 1 || colChanger == col) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                    else{
                        if(table.matris[rowChanger-1][colChanger-1] > landAndWaterTableTemp[rowChanger-1][colChanger+1]) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                        if(table.matris[rowChanger-1][colChanger-1] <= landAndWaterTableTemp[rowChanger-1][colChanger+1]){
                            landAndWaterTableTemp[rowChanger][colChanger] = Math.max(landAndWaterTableTemp[rowChanger-1][colChanger+1],table.matris[rowChanger-1][colChanger-1]);
                        }
                    }
                }
            }
        }
        for(int rowChanger = row; rowChanger > 0; rowChanger --){
            for(int colChanger = col; colChanger > 0; colChanger --){
                if(landAndWaterTableTemp[rowChanger][colChanger] > landAndWaterTableTemp[rowChanger+1][colChanger+1]){
                    if(rowChanger == row || colChanger == col) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                    else{
                        if(table.matris[rowChanger-1][colChanger-1] > landAndWaterTableTemp[rowChanger+1][colChanger+1]) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                        if(table.matris[rowChanger-1][colChanger-1] <= landAndWaterTableTemp[rowChanger+1][colChanger+1]){
                            landAndWaterTableTemp[rowChanger][colChanger] = Math.max(landAndWaterTableTemp[rowChanger+1][colChanger+1],table.matris[rowChanger-1][colChanger-1]);
                        }
                    }
                }
            }
        }
        for(int rowChanger = row; rowChanger > 0; rowChanger --){
            for(int colChanger = 1; colChanger < col+1; colChanger ++){
                if(landAndWaterTableTemp[rowChanger][colChanger] > landAndWaterTableTemp[rowChanger+1][colChanger-1]){
                    if(rowChanger == row || colChanger == 1)
                        landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                    else{
                        if(table.matris[rowChanger-1][colChanger-1] > landAndWaterTableTemp[rowChanger+1][colChanger-1]) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                        if(table.matris[rowChanger-1][colChanger-1] <= landAndWaterTableTemp[rowChanger+1][colChanger-1]){
                            landAndWaterTableTemp[rowChanger][colChanger] = Math.max(landAndWaterTableTemp[rowChanger+1][colChanger-1],table.matris[rowChanger-1][colChanger-1]);
                        }
                    }
                }
            }
        }

        for(int rowChanger = row; rowChanger > 0; rowChanger --){
            for(int colChanger = col; colChanger > 0; colChanger --){
                if(landAndWaterTableTemp[rowChanger][colChanger] > landAndWaterTableTemp[rowChanger+1][colChanger+1]){
                    if(rowChanger == row || colChanger == col) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                    else{
                        if(table.matris[rowChanger-1][colChanger-1] > landAndWaterTableTemp[rowChanger+1][colChanger+1]) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                        if(table.matris[rowChanger-1][colChanger-1] <= landAndWaterTableTemp[rowChanger+1][colChanger+1]){
                            landAndWaterTableTemp[rowChanger][colChanger] = Math.max(landAndWaterTableTemp[rowChanger+1][colChanger+1],table.matris[rowChanger-1][colChanger-1]);
                        }
                    }
                }
            }
        }
        for(int rowChanger = 1; rowChanger <row+1; rowChanger ++){
            for(int colChanger = col; colChanger > 0; colChanger --){
                if(landAndWaterTableTemp[rowChanger][colChanger] > landAndWaterTableTemp[rowChanger-1][colChanger+1]){
                    if(rowChanger == 1 || colChanger == col) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                    else{
                        if(table.matris[rowChanger-1][colChanger-1] > landAndWaterTableTemp[rowChanger-1][colChanger+1]) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                        if(table.matris[rowChanger-1][colChanger-1] <= landAndWaterTableTemp[rowChanger-1][colChanger+1]){
                            landAndWaterTableTemp[rowChanger][colChanger] = Math.max(landAndWaterTableTemp[rowChanger-1][colChanger+1],table.matris[rowChanger-1][colChanger-1]);
                        }
                    }
                }
            }
        }
        for(int rowChanger = 1; rowChanger <row+1; rowChanger ++){
            for(int colChanger = 1; colChanger < col+1; colChanger ++){
                if(landAndWaterTableTemp[rowChanger][colChanger] > landAndWaterTableTemp[rowChanger-1][colChanger-1]){
                    if(rowChanger == 1 || colChanger == 1) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                    else{
                        if(table.matris[rowChanger-1][colChanger-1] > landAndWaterTableTemp[rowChanger-1][colChanger-1]) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                        if(table.matris[rowChanger-1][colChanger-1] <= landAndWaterTableTemp[rowChanger-1][colChanger-1]){
                            landAndWaterTableTemp[rowChanger][colChanger] = Math.max(landAndWaterTableTemp[rowChanger-1][colChanger-1],table.matris[rowChanger-1][colChanger-1]);
                        }
                    }
                }
            }
        }
        for(int rowChanger = row; rowChanger > 0; rowChanger --){
            for(int colChanger = 1; colChanger < col+1; colChanger ++){
                if(landAndWaterTableTemp[rowChanger][colChanger] > landAndWaterTableTemp[rowChanger+1][colChanger-1]){
                    if(rowChanger == row || colChanger == 1)
                        landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                    else{
                        if(table.matris[rowChanger-1][colChanger-1] > landAndWaterTableTemp[rowChanger+1][colChanger-1]) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                        if(table.matris[rowChanger-1][colChanger-1] <= landAndWaterTableTemp[rowChanger+1][colChanger-1]){
                            landAndWaterTableTemp[rowChanger][colChanger] = Math.max(landAndWaterTableTemp[rowChanger+1][colChanger-1],table.matris[rowChanger-1][colChanger-1]);
                        }
                    }
                }
            }
        }

        for (int colChanger = 0; colChanger < col; colChanger++) {
            for (int rowChanger = row + 1; rowChanger > 1; rowChanger--) {
                if (landAndWaterTableTemp[rowChanger][colChanger + 1] < landAndWaterTableTemp[rowChanger - 1][colChanger + 1]) {
                    int difference = landAndWaterTableTemp[rowChanger - 1][colChanger + 1] - landAndWaterTableTemp[rowChanger][colChanger + 1];
                    if (difference > landAndWaterTableTemp[rowChanger][colChanger + 1]) {
                        landAndWaterTableTemp[rowChanger - 1][colChanger + 1] = table.matris[rowChanger - 2][colChanger];

                    }
                    if (difference <= landAndWaterTableTemp[rowChanger][colChanger + 1]) {
                        if (rowChanger < row + 1) {
                            if (table.matris[rowChanger - 1 - 1][colChanger] <= landAndWaterTableTemp[rowChanger][colChanger + 1])
                                landAndWaterTableTemp[rowChanger - 1][colChanger + 1] = landAndWaterTableTemp[rowChanger][colChanger + 1];
                            if (table.matris[rowChanger - 1 - 1][colChanger] > landAndWaterTableTemp[rowChanger][colChanger + 1])
                                landAndWaterTableTemp[rowChanger - 1][colChanger + 1] = table.matris[rowChanger - 1 - 1][colChanger];
                        } else
                            landAndWaterTableTemp[rowChanger - 1 - 1][colChanger + 1] = landAndWaterTableTemp[rowChanger][colChanger + 1];
                    }
                }

            }
        }
        for (int colChanger = 0; colChanger < col; colChanger++) {
            for (int rowChanger = 0; rowChanger < row; rowChanger++) {
                if (landAndWaterTableTemp[rowChanger][colChanger + 1] < landAndWaterTableTemp[rowChanger + 1][colChanger + 1]) {
                    int difference = landAndWaterTableTemp[rowChanger + 1][colChanger + 1] - landAndWaterTableTemp[rowChanger][colChanger + 1];
                    if (difference > landAndWaterTableTemp[rowChanger][colChanger + 1]) {
                        landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = table.matris[rowChanger][colChanger];

                    }
                    if (difference <= landAndWaterTableTemp[rowChanger][colChanger + 1]) {
                        if (rowChanger > 0) {
                            if (table.matris[rowChanger][colChanger] <= landAndWaterTableTemp[rowChanger][colChanger + 1])
                                landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = landAndWaterTableTemp[rowChanger][colChanger + 1];
                            if (table.matris[rowChanger][colChanger] > landAndWaterTableTemp[rowChanger][colChanger + 1])
                                landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = table.matris[rowChanger][colChanger];
                        } else
                            landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = landAndWaterTableTemp[rowChanger][colChanger + 1];
                    }
                }

            }
        }
        for (int rowChanger = 0; rowChanger < row; rowChanger++) {
            for (int colChanger = 0; colChanger < col; colChanger++) {
                if (landAndWaterTableTemp[rowChanger + 1][colChanger] < landAndWaterTableTemp[rowChanger + 1][colChanger + 1]) {
                    int difference = landAndWaterTableTemp[rowChanger + 1][colChanger + 1] - landAndWaterTableTemp[rowChanger + 1][colChanger];
                    if (difference > landAndWaterTableTemp[rowChanger + 1][colChanger]) {
                        landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = table.matris[rowChanger][colChanger];

                    }
                    if (difference <= landAndWaterTableTemp[rowChanger + 1][colChanger]) {
                        if (colChanger > 0) {
                            if (table.matris[rowChanger][colChanger] <= landAndWaterTableTemp[rowChanger + 1][colChanger])
                                landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = landAndWaterTableTemp[rowChanger + 1][colChanger];
                            if (table.matris[rowChanger][colChanger] > landAndWaterTableTemp[rowChanger + 1][colChanger])
                                landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = table.matris[rowChanger][colChanger];
                        } else
                            landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = landAndWaterTableTemp[rowChanger + 1][colChanger];
                    }
                }

            }
        }
        for (int rowChanger = 0; rowChanger < row; rowChanger++) {
            for (int colChanger = col + 1; colChanger > 1; colChanger--) {
                if (landAndWaterTableTemp[rowChanger + 1][colChanger] <= landAndWaterTableTemp[rowChanger + 1][colChanger - 1]) {
                    int difference = landAndWaterTableTemp[rowChanger + 1][colChanger - 1] - landAndWaterTableTemp[rowChanger + 1][colChanger];

                    if (difference > landAndWaterTableTemp[rowChanger + 1][colChanger]) {
                        landAndWaterTableTemp[rowChanger + 1][colChanger - 1] = table.matris[rowChanger][colChanger - 2];

                    }
                    if (difference <= landAndWaterTableTemp[rowChanger + 1][colChanger]) {
                        if (colChanger < col + 1) {
                            if (table.matris[rowChanger][colChanger - 2] <= landAndWaterTableTemp[rowChanger + 1][colChanger])
                                landAndWaterTableTemp[rowChanger + 1][colChanger - 1] = landAndWaterTableTemp[rowChanger + 1][colChanger];
                            if (table.matris[rowChanger][colChanger - 2] > landAndWaterTableTemp[rowChanger + 1][colChanger])
                                landAndWaterTableTemp[rowChanger + 1][colChanger - 1] = table.matris[rowChanger][colChanger - 2];
                        } else
                            landAndWaterTableTemp[rowChanger + 1][colChanger - 1] = landAndWaterTableTemp[rowChanger + 1][colChanger];
                    }
                }

            }
        }


        for(int rowChanger = 1; rowChanger <row+1; rowChanger ++){
            for(int colChanger = 1; colChanger < col+1; colChanger ++){
                if(landAndWaterTableTemp[rowChanger][colChanger] > landAndWaterTableTemp[rowChanger-1][colChanger-1]){
                    if(rowChanger == 1 || colChanger == 1) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                    else{
                        if(table.matris[rowChanger-1][colChanger-1] > landAndWaterTableTemp[rowChanger-1][colChanger-1]) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                        if(table.matris[rowChanger-1][colChanger-1] <= landAndWaterTableTemp[rowChanger-1][colChanger-1]){
                            landAndWaterTableTemp[rowChanger][colChanger] = Math.max(landAndWaterTableTemp[rowChanger-1][colChanger-1],table.matris[rowChanger-1][colChanger-1]);
                        }
                    }
                }
            }
        }
        for(int rowChanger = row; rowChanger > 0; rowChanger --){
            for(int colChanger = 1; colChanger < col+1; colChanger ++){
                if(landAndWaterTableTemp[rowChanger][colChanger] > landAndWaterTableTemp[rowChanger+1][colChanger-1]){
                    if(rowChanger == row || colChanger == 1)
                        landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                    else{
                        if(table.matris[rowChanger-1][colChanger-1] > landAndWaterTableTemp[rowChanger+1][colChanger-1]) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                        if(table.matris[rowChanger-1][colChanger-1] <= landAndWaterTableTemp[rowChanger+1][colChanger-1]){
                            landAndWaterTableTemp[rowChanger][colChanger] = Math.max(landAndWaterTableTemp[rowChanger+1][colChanger-1],table.matris[rowChanger-1][colChanger-1]);
                        }
                    }
                }
            }
        }
        for(int rowChanger = row; rowChanger > 0; rowChanger --){
            for(int colChanger = col; colChanger > 0; colChanger --){
                if(landAndWaterTableTemp[rowChanger][colChanger] > landAndWaterTableTemp[rowChanger+1][colChanger+1]){
                    if(rowChanger == row || colChanger == col) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                    else{
                        if(table.matris[rowChanger-1][colChanger-1] > landAndWaterTableTemp[rowChanger+1][colChanger+1]) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                        if(table.matris[rowChanger-1][colChanger-1] <= landAndWaterTableTemp[rowChanger+1][colChanger+1]){
                            landAndWaterTableTemp[rowChanger][colChanger] = Math.max(landAndWaterTableTemp[rowChanger+1][colChanger+1],table.matris[rowChanger-1][colChanger-1]);
                        }
                    }
                }
            }
        }
        for(int rowChanger = 1; rowChanger <row+1; rowChanger ++){
            for(int colChanger = col; colChanger > 0; colChanger --){
                if(landAndWaterTableTemp[rowChanger][colChanger] > landAndWaterTableTemp[rowChanger-1][colChanger+1]){
                    if(rowChanger == 1 || colChanger == col) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                    else{
                        if(table.matris[rowChanger-1][colChanger-1] > landAndWaterTableTemp[rowChanger-1][colChanger+1]) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                        if(table.matris[rowChanger-1][colChanger-1] <= landAndWaterTableTemp[rowChanger-1][colChanger+1]){
                            landAndWaterTableTemp[rowChanger][colChanger] = Math.max(landAndWaterTableTemp[rowChanger-1][colChanger+1],table.matris[rowChanger-1][colChanger-1]);
                        }
                    }
                }
            }
        }

        for (int rowChanger = 0; rowChanger < row; rowChanger++) {
            for (int colChanger = 0; colChanger < col; colChanger++) {
                if (landAndWaterTableTemp[rowChanger + 1][colChanger] < landAndWaterTableTemp[rowChanger + 1][colChanger + 1]) {
                    int difference = landAndWaterTableTemp[rowChanger + 1][colChanger + 1] - landAndWaterTableTemp[rowChanger + 1][colChanger];
                    if (difference > landAndWaterTableTemp[rowChanger + 1][colChanger]) {
                        landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = table.matris[rowChanger][colChanger];

                    }
                    if (difference <= landAndWaterTableTemp[rowChanger + 1][colChanger]) {
                        if (colChanger > 0) {
                            if (table.matris[rowChanger][colChanger] <= landAndWaterTableTemp[rowChanger + 1][colChanger])
                                landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = landAndWaterTableTemp[rowChanger + 1][colChanger];
                            if (table.matris[rowChanger][colChanger] > landAndWaterTableTemp[rowChanger + 1][colChanger])
                                landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = table.matris[rowChanger][colChanger];
                        } else
                            landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = landAndWaterTableTemp[rowChanger + 1][colChanger];
                    }
                }

            }
        }
        for (int rowChanger = 0; rowChanger < row; rowChanger++) {
            for (int colChanger = col + 1; colChanger > 1; colChanger--) {
                if (landAndWaterTableTemp[rowChanger + 1][colChanger] <= landAndWaterTableTemp[rowChanger + 1][colChanger - 1]) {
                    int difference = landAndWaterTableTemp[rowChanger + 1][colChanger - 1] - landAndWaterTableTemp[rowChanger + 1][colChanger];

                    if (difference > landAndWaterTableTemp[rowChanger + 1][colChanger]) {
                        landAndWaterTableTemp[rowChanger + 1][colChanger - 1] = table.matris[rowChanger][colChanger - 2];

                    }
                    if (difference <= landAndWaterTableTemp[rowChanger + 1][colChanger]) {
                        if (colChanger < col + 1) {
                            if (table.matris[rowChanger][colChanger - 2] <= landAndWaterTableTemp[rowChanger + 1][colChanger])
                                landAndWaterTableTemp[rowChanger + 1][colChanger - 1] = landAndWaterTableTemp[rowChanger + 1][colChanger];
                            if (table.matris[rowChanger][colChanger - 2] > landAndWaterTableTemp[rowChanger + 1][colChanger])
                                landAndWaterTableTemp[rowChanger + 1][colChanger - 1] = table.matris[rowChanger][colChanger - 2];
                        } else
                            landAndWaterTableTemp[rowChanger + 1][colChanger - 1] = landAndWaterTableTemp[rowChanger + 1][colChanger];
                    }
                }

            }
        }
        for (int colChanger = 0; colChanger < col; colChanger++) {
            for (int rowChanger = row + 1; rowChanger > 1; rowChanger--) {
                if (landAndWaterTableTemp[rowChanger][colChanger + 1] < landAndWaterTableTemp[rowChanger - 1][colChanger + 1]) {
                    int difference = landAndWaterTableTemp[rowChanger - 1][colChanger + 1] - landAndWaterTableTemp[rowChanger][colChanger + 1];
                    if (difference > landAndWaterTableTemp[rowChanger][colChanger + 1]) {
                        landAndWaterTableTemp[rowChanger - 1][colChanger + 1] = table.matris[rowChanger - 2][colChanger];

                    }
                    if (difference <= landAndWaterTableTemp[rowChanger][colChanger + 1]) {
                        if (rowChanger < row + 1) {
                            if (table.matris[rowChanger - 1 - 1][colChanger] <= landAndWaterTableTemp[rowChanger][colChanger + 1])
                                landAndWaterTableTemp[rowChanger - 1][colChanger + 1] = landAndWaterTableTemp[rowChanger][colChanger + 1];
                            if (table.matris[rowChanger - 1 - 1][colChanger] > landAndWaterTableTemp[rowChanger][colChanger + 1])
                                landAndWaterTableTemp[rowChanger - 1][colChanger + 1] = table.matris[rowChanger - 1 - 1][colChanger];
                        } else
                            landAndWaterTableTemp[rowChanger - 1 - 1][colChanger + 1] = landAndWaterTableTemp[rowChanger][colChanger + 1];
                    }
                }

            }
        }
        for (int colChanger = 0; colChanger < col; colChanger++) {
            for (int rowChanger = 0; rowChanger < row; rowChanger++) {
                if (landAndWaterTableTemp[rowChanger][colChanger + 1] < landAndWaterTableTemp[rowChanger + 1][colChanger + 1]) {
                    int difference = landAndWaterTableTemp[rowChanger + 1][colChanger + 1] - landAndWaterTableTemp[rowChanger][colChanger + 1];
                    if (difference > landAndWaterTableTemp[rowChanger][colChanger + 1]) {
                        landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = table.matris[rowChanger][colChanger];

                    }
                    if (difference <= landAndWaterTableTemp[rowChanger][colChanger + 1]) {
                        if (rowChanger > 0) {
                            if (table.matris[rowChanger][colChanger] <= landAndWaterTableTemp[rowChanger][colChanger + 1])
                                landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = landAndWaterTableTemp[rowChanger][colChanger + 1];
                            if (table.matris[rowChanger][colChanger] > landAndWaterTableTemp[rowChanger][colChanger + 1])
                                landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = table.matris[rowChanger][colChanger];
                        } else
                            landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = landAndWaterTableTemp[rowChanger][colChanger + 1];
                    }
                }

            }
        }

        for(int rowChanger = 1; rowChanger <row+1; rowChanger ++){
            for(int colChanger = 1; colChanger < col+1; colChanger ++){
                if(landAndWaterTableTemp[rowChanger][colChanger] > landAndWaterTableTemp[rowChanger-1][colChanger-1]){
                    if(rowChanger == 1 || colChanger == 1) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                    else{
                        if(table.matris[rowChanger-1][colChanger-1] > landAndWaterTableTemp[rowChanger-1][colChanger-1]) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                        if(table.matris[rowChanger-1][colChanger-1] <= landAndWaterTableTemp[rowChanger-1][colChanger-1]){
                            landAndWaterTableTemp[rowChanger][colChanger] = Math.max(landAndWaterTableTemp[rowChanger-1][colChanger-1],table.matris[rowChanger-1][colChanger-1]);
                        }
                    }
                }
            }
        }
        for(int rowChanger = 1; rowChanger <row+1; rowChanger ++){
            for(int colChanger = col; colChanger > 0; colChanger --){
                if(landAndWaterTableTemp[rowChanger][colChanger] > landAndWaterTableTemp[rowChanger-1][colChanger+1]){
                    if(rowChanger == 1 || colChanger == col) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                    else{
                        if(table.matris[rowChanger-1][colChanger-1] > landAndWaterTableTemp[rowChanger-1][colChanger+1]) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                        if(table.matris[rowChanger-1][colChanger-1] <= landAndWaterTableTemp[rowChanger-1][colChanger+1]){
                            landAndWaterTableTemp[rowChanger][colChanger] = Math.max(landAndWaterTableTemp[rowChanger-1][colChanger+1],table.matris[rowChanger-1][colChanger-1]);
                        }
                    }
                }
            }
        }
        for(int rowChanger = row; rowChanger > 0; rowChanger --){
            for(int colChanger = col; colChanger > 0; colChanger --){
                if(landAndWaterTableTemp[rowChanger][colChanger] > landAndWaterTableTemp[rowChanger+1][colChanger+1]){
                    if(rowChanger == row || colChanger == col) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                    else{
                        if(table.matris[rowChanger-1][colChanger-1] > landAndWaterTableTemp[rowChanger+1][colChanger+1]) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                        if(table.matris[rowChanger-1][colChanger-1] <= landAndWaterTableTemp[rowChanger+1][colChanger+1]){
                            landAndWaterTableTemp[rowChanger][colChanger] = Math.max(landAndWaterTableTemp[rowChanger+1][colChanger+1],table.matris[rowChanger-1][colChanger-1]);
                        }
                    }
                }
            }
        }
        for(int rowChanger = row; rowChanger > 0; rowChanger --){
            for(int colChanger = 1; colChanger < col+1; colChanger ++){
                if(landAndWaterTableTemp[rowChanger][colChanger] > landAndWaterTableTemp[rowChanger+1][colChanger-1]){
                    if(rowChanger == row || colChanger == 1)
                        landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                    else{
                        if(table.matris[rowChanger-1][colChanger-1] > landAndWaterTableTemp[rowChanger+1][colChanger-1]) landAndWaterTableTemp[rowChanger][colChanger] = table.matris[rowChanger-1][colChanger-1];
                        if(table.matris[rowChanger-1][colChanger-1] <= landAndWaterTableTemp[rowChanger+1][colChanger-1]){
                            landAndWaterTableTemp[rowChanger][colChanger] = Math.max(landAndWaterTableTemp[rowChanger+1][colChanger-1],table.matris[rowChanger-1][colChanger-1]);
                        }
                    }
                }
            }
        }

        for (int rowChanger = 0; rowChanger < row; rowChanger++) {
            for (int colChanger = 0; colChanger < col; colChanger++) {
                if (landAndWaterTableTemp[rowChanger + 1][colChanger] < landAndWaterTableTemp[rowChanger + 1][colChanger + 1]) {
                    int difference = landAndWaterTableTemp[rowChanger + 1][colChanger + 1] - landAndWaterTableTemp[rowChanger + 1][colChanger];
                    if (difference > landAndWaterTableTemp[rowChanger + 1][colChanger]) {
                        landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = table.matris[rowChanger][colChanger];

                    }
                    if (difference <= landAndWaterTableTemp[rowChanger + 1][colChanger]) {
                        if (colChanger > 0) {
                            if (table.matris[rowChanger][colChanger] <= landAndWaterTableTemp[rowChanger + 1][colChanger])
                                landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = landAndWaterTableTemp[rowChanger + 1][colChanger];
                            if (table.matris[rowChanger][colChanger] > landAndWaterTableTemp[rowChanger + 1][colChanger])
                                landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = table.matris[rowChanger][colChanger];
                        } else
                            landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = landAndWaterTableTemp[rowChanger + 1][colChanger];
                    }
                }

            }
        }
        for (int colChanger = 0; colChanger < col; colChanger++) {
            for (int rowChanger = 0; rowChanger < row; rowChanger++) {
                if (landAndWaterTableTemp[rowChanger][colChanger + 1] < landAndWaterTableTemp[rowChanger + 1][colChanger + 1]) {
                    int difference = landAndWaterTableTemp[rowChanger + 1][colChanger + 1] - landAndWaterTableTemp[rowChanger][colChanger + 1];
                    if (difference > landAndWaterTableTemp[rowChanger][colChanger + 1]) {
                        landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = table.matris[rowChanger][colChanger];

                    }
                    if (difference <= landAndWaterTableTemp[rowChanger][colChanger + 1]) {
                        if (rowChanger > 0) {
                            if (table.matris[rowChanger][colChanger] <= landAndWaterTableTemp[rowChanger][colChanger + 1])
                                landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = landAndWaterTableTemp[rowChanger][colChanger + 1];
                            if (table.matris[rowChanger][colChanger] > landAndWaterTableTemp[rowChanger][colChanger + 1])
                                landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = table.matris[rowChanger][colChanger];
                        } else
                            landAndWaterTableTemp[rowChanger + 1][colChanger + 1] = landAndWaterTableTemp[rowChanger][colChanger + 1];
                    }
                }

            }
        }
        for (int colChanger = 0; colChanger < col; colChanger++) {
            for (int rowChanger = row + 1; rowChanger > 1; rowChanger--) {
                if (landAndWaterTableTemp[rowChanger][colChanger + 1] < landAndWaterTableTemp[rowChanger - 1][colChanger + 1]) {
                    int difference = landAndWaterTableTemp[rowChanger - 1][colChanger + 1] - landAndWaterTableTemp[rowChanger][colChanger + 1];
                    if (difference > landAndWaterTableTemp[rowChanger][colChanger + 1]) {
                        landAndWaterTableTemp[rowChanger - 1][colChanger + 1] = table.matris[rowChanger - 2][colChanger];

                    }
                    if (difference <= landAndWaterTableTemp[rowChanger][colChanger + 1]) {
                        if (rowChanger < row + 1) {
                            if (table.matris[rowChanger - 1 - 1][colChanger] <= landAndWaterTableTemp[rowChanger][colChanger + 1])
                                landAndWaterTableTemp[rowChanger - 1][colChanger + 1] = landAndWaterTableTemp[rowChanger][colChanger + 1];
                            if (table.matris[rowChanger - 1 - 1][colChanger] > landAndWaterTableTemp[rowChanger][colChanger + 1])
                                landAndWaterTableTemp[rowChanger - 1][colChanger + 1] = table.matris[rowChanger - 1 - 1][colChanger];
                        } else
                            landAndWaterTableTemp[rowChanger - 1 - 1][colChanger + 1] = landAndWaterTableTemp[rowChanger][colChanger + 1];
                    }
                }

            }
        }
        for (int rowChanger = 0; rowChanger < row; rowChanger++) {
            for (int colChanger = col + 1; colChanger > 1; colChanger--) {
                if (landAndWaterTableTemp[rowChanger + 1][colChanger] <= landAndWaterTableTemp[rowChanger + 1][colChanger - 1]) {
                    int difference = landAndWaterTableTemp[rowChanger + 1][colChanger - 1] - landAndWaterTableTemp[rowChanger + 1][colChanger];

                    if (difference > landAndWaterTableTemp[rowChanger + 1][colChanger]) {
                        landAndWaterTableTemp[rowChanger + 1][colChanger - 1] = table.matris[rowChanger][colChanger - 2];

                    }
                    if (difference <= landAndWaterTableTemp[rowChanger + 1][colChanger]) {
                        if (colChanger < col + 1) {
                            if (table.matris[rowChanger][colChanger - 2] <= landAndWaterTableTemp[rowChanger + 1][colChanger])
                                landAndWaterTableTemp[rowChanger + 1][colChanger - 1] = landAndWaterTableTemp[rowChanger + 1][colChanger];
                            if (table.matris[rowChanger][colChanger - 2] > landAndWaterTableTemp[rowChanger + 1][colChanger])
                                landAndWaterTableTemp[rowChanger + 1][colChanger - 1] = table.matris[rowChanger][colChanger - 2];
                        } else
                            landAndWaterTableTemp[rowChanger + 1][colChanger - 1] = landAndWaterTableTemp[rowChanger + 1][colChanger];
                    }
                }

            }
        }

        for (int rowChanger = 1; rowChanger < row + 1; rowChanger++) {
            for (int colChanger = 1; colChanger < col + 1; colChanger++) {
                waterTableTemp[rowChanger][colChanger] = landAndWaterTableTemp[rowChanger][colChanger] - table.matris[rowChanger - 1][colChanger - 1];
            }
        }
        return waterTableTemp;
    }

    //recursive neighboor checker
    public static double rec_set_neighboor(int[][] watertable, int row,int col, boolean[][] markedlist,int totalRow,int totalCol,String mark,String[][] table,int a){
        // if this is a lake or out of the map, does nothing.
        if(row<0 || col < 0 || row == totalRow || col == totalCol || watertable[row+1][col+1] == 0 || markedlist[row][col]) return a;
        //else marks them with the String input Mark, after this island mark will be change
        table[row][col] = mark;
        markedlist[row][col] = true;
        a += watertable[row+1][col+1];
        int[][] changeList = new int[9][2];
        int indexchange = 0;
        for(int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                changeList[indexchange][0] = i;
                changeList[indexchange][1] = j;
                indexchange++;
            }
        }
        for (int[] change : changeList){
            // checks the neighboors if there are lake or not
            rec_set_neighboor(watertable,row+change[0],col+change[1],markedlist,totalRow,totalCol,mark,table,a);
        }
        return a;
    }
}
