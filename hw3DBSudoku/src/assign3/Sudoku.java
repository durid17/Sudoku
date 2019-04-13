package assign3;

import java.util.*;

/*
 * Encapsulates a Sudoku grid to be solved.
 * CS108 Stanford.
 */
public class Sudoku {
	
	private class Spot implements Comparable<Spot>{
		private int num;
		private int row;
		private int col;
		private int assignableNumber;
		private static final int MAX_NUMBER = 9;
		
		public Spot(int num , int row , int col){
			this.num = num;
			this.row = row;
			this.col = col;
			this.assignableNumber = 0;
		}
		
		//copy constructor
		public Spot(Spot sp){
			this.num = sp.num;
			this.row = sp.row;
			this.col = sp.col;
			this.assignableNumber = sp.assignableNumber;
		}
		
		public int getNumber() {
			return num;
		}
		
		public boolean setNumber(int num) {
			if(num < 0 || num > MAX_NUMBER) {
				return false;
			}
			if(num == 0) {
				this.num = num;
				return true;
			}
			boolean[] possible = getAssignableNumbers();
			if(possible[num]){
				this.num = num;
				return true;
			}
			
			return false;
		}
		
		
		private boolean[] getAssignableNumbers() {
			boolean [] possible = new boolean[10];
			//initialization
			for(int i = 0 ; i < 10 ; i++) {
				possible[i] = true;
			}
			//get Column numbers
			for(int i = 0 ; i < SIZE; i++) {
				possible[grid[row][i].getNumber()] = false;
			}
			//get Row numbers
			for(int i = 0 ; i < SIZE; i++) {
				possible[grid[i][col].getNumber()] = false;
			}
			int squareRow = row/PART;
			int squareCol = col/PART;
			// get Square numbers
			for(int i = 0 ;i <= 2; i++) {
				for(int j = 0 ; j <= 2; j++) {
					possible[grid[squareRow * PART + i][squareCol * PART + j].getNumber()] = false;
				}
			}
			return possible;
		}
		
		public void countAssignableNumbers() {
			boolean[] possible = getAssignableNumbers();
			for(int i = 1 ; i < 10 ; i++) {
				if(possible[i]) assignableNumber++;
			}
		}

		@Override
		public int compareTo(Spot compareSpot) {
			return this.assignableNumber - compareSpot.assignableNumber;
		}
	}	
		
	// Provided grid data for main/testing
	// The instance variable strategy is up to you.
	
	private Spot[][] start;
	private Spot[][] grid;
	private Spot[][] result;
	private int numberOfSolves;
	private long time;
	private List<Spot> list;
	
	
	// Provided easy 1 6 grid
	// (can paste this text into the GUI too)
	public static final int[][] easyGrid = Sudoku.stringsToGrid(
	"1 6 4 0 0 0 0 0 2",
	"2 0 0 4 0 3 9 1 0",
	"0 0 5 0 8 0 4 0 7",
	"0 9 0 0 0 6 5 0 0",
	"5 0 0 1 0 2 0 0 8",
	"0 0 8 9 0 0 0 3 0",
	"8 0 9 0 4 0 2 0 0",
	"0 7 3 5 0 9 0 0 1",
	"4 0 0 0 0 0 6 7 9");
	
	
	// Provided medium 5 3 grid
	public static final int[][] mediumGrid = Sudoku.stringsToGrid(
	 "530070000",
	 "600195000",
	 "098000060",
	 "800060003",
	 "400803001",
	 "700020006",
	 "060000280",
	 "000419005",
	 "000080079");
	
	// Provided hard 3 7 grid
	// 1 solution this way, 6 solutions if the 7 is changed to 0
	public static final int[][] hardGrid = Sudoku.stringsToGrid(
	"3 7 0 0 0 0 0 8 0",
	"0 0 1 0 9 3 0 0 0",
	"0 4 0 7 8 0 0 0 3",
	"0 9 3 8 0 0 0 1 2",
	"0 0 0 0 4 0 0 0 0",
	"5 2 0 0 0 6 7 9 0",
	"6 0 0 0 2 1 0 4 0",
	"0 0 0 5 3 0 9 0 0",
	"0 3 0 0 0 0 0 5 1");
	
	
	public static final int SIZE = 9;  // size of the whole 9x9 puzzle
	public static final int PART = 3;  // size of each 3x3 part
	public static final int MAX_SOLUTIONS = 100;
	
	// Provided various static utility methods to
	// convert data formats to int[][] grid.
	
	/**
	 * Returns a 2-d grid parsed from strings, one string per row.
	 * The "..." is a Java 5 feature that essentially
	 * makes "rows" a String[] array.
	 * (provided utility)
	 * @param rows array of row strings
	 * @return grid
	 */
	public static int[][] stringsToGrid(String... rows) {
		int[][] result = new int[rows.length][];
		for (int row = 0; row<rows.length; row++) {
			result[row] = stringToInts(rows[row]);
		}
		return result;
	}
	
	
	/**
	 * Given a single string containing 81 numbers, returns a 9x9 grid.
	 * Skips all the non-numbers in the text.
	 * (provided utility)
	 * @param text string of 81 numbers
	 * @return grid
	 */
	public static int[][] textToGrid(String text) {
		int[] nums = stringToInts(text);
		if (nums.length != SIZE*SIZE) {
			throw new RuntimeException("Needed 81 numbers, but got:" + nums.length);
		}
		
		int[][] result = new int[SIZE][SIZE];
		int count = 0;
		for (int row = 0; row<SIZE; row++) {
			for (int col=0; col<SIZE; col++) {
				result[row][col] = nums[count];
				count++;
			}
		}
		return result;
	}
	
	
	/**
	 * Given a string containing digits, like "1 23 4",
	 * returns an int[] of those digits {1 2 3 4}.
	 * (provided utility)
	 * @param string string containing ints
	 * @return array of ints
	 */
	public static int[] stringToInts(String string) {
		int[] a = new int[string.length()];
		int found = 0;
		for (int i=0; i<string.length(); i++) {
			if (Character.isDigit(string.charAt(i))) {
				a[found] = Integer.parseInt(string.substring(i, i+1));
				found++;
			}
		}
		int[] result = new int[found];
		System.arraycopy(a, 0, result, 0, found);
		return result;
	}


	// Provided -- the deliverable main().
	// You can edit to do easier cases, but turn in
	// solving hardGrid.
	public static void main(String[] args) {
		Sudoku sudoku;
		sudoku = new Sudoku(hardGrid);
		
		System.out.println(sudoku); // print the raw problem
		int count = sudoku.solve();
		System.out.println("solutions:" + count);
		System.out.println("elapsed:" + sudoku.getElapsed() + "ms");
		System.out.println(sudoku.getSolutionText());
	
		sudoku = new Sudoku(easyGrid);
		
		System.out.println(sudoku); // print the raw problem
		count = sudoku.solve();
		System.out.println("solutions:" + count);
		System.out.println("elapsed:" + sudoku.getElapsed() + "ms");
		System.out.println(sudoku.getSolutionText());
		
		sudoku = new Sudoku(mediumGrid);
		
		System.out.println(sudoku); // print the raw problem
		count = sudoku.solve();
		System.out.println("solutions:" + count);
		System.out.println("elapsed:" + sudoku.getElapsed() + "ms");
		System.out.println(sudoku.getSolutionText());
		
		
	}
	

	/**
	 * Sets up based on the given ints.
	 */
	public Sudoku(int[][] ints) {
		start = new Spot[SIZE][SIZE];
		grid = new Spot[SIZE][SIZE];
		for(int i = 0 ; i < SIZE ; i++) {
			for(int j = 0 ; j < SIZE; j++) {
				start[i][j] = new Spot(ints[i][j] , i , j);
				grid[i][j] = new Spot(ints[i][j] , i , j);
			}
		}
		numberOfSolves = 0;
		list = new ArrayList<Spot>();
		for(int i = 0 ; i < SIZE ; i++) {
			for(int j = 0 ; j < SIZE; j++) {
				if(grid[i][j].getNumber() == 0) {
					grid[i][j].countAssignableNumbers();
					list.add(grid[i][j]);
				}
//				System.out.print(grid[i][j].assignableNumber + " ");
			}
//			System.out.println();
		}
//		System.out.println();
		Collections.sort(list);		
	}
	
	public Sudoku(String str) {
		this(textToGrid(str));
	}
	
	
	/**
	 * Solves the puzzle, invoking the underlying recursive search.
	 */
	public int solve() {
		long startTime = System.currentTimeMillis();
		int index = 0;
		solvePuzzle(index);
		long endTime = System.currentTimeMillis();
		time = endTime - startTime;
		return numberOfSolves; // YOUR CODE HERE
	}
	
	private void solvePuzzle(int index) {
		if(numberOfSolves >= MAX_SOLUTIONS) return;
		if(index == list.size()) {
			if(numberOfSolves == 0)  copy(grid);
			numberOfSolves++;
			return;
		}
//		System.out.println(index + " " + list.get(index).row + " " + list.get(index).col );
//		System.out.println(list.get(index).getRow() + " " +
//				list.get(index).getCol() + " " + list.get(index).getNumber());
		for(int i = 1; i <= 9 ; i++) {
			if(list.get(index).setNumber(i)) {
				grid[list.get(index).row][list.get(index).col].setNumber(i);
				solvePuzzle(index + 1);
				grid[list.get(index).row][list.get(index).col].setNumber(0);
				list.get(index).setNumber(0);
			}else {
//				System.out.println(index + "    " + i);
			}
		}
		
	}


	private void copy(Spot[][] source) {
		result = new Spot[SIZE][SIZE];
		for(int i = 0 ; i < SIZE; i++) {
			for(int j = 0 ; j < SIZE; j++) {
				result[i][j] = new Spot(source[i][j]);
			}
		}
	}
	
	private String gridToString(Spot[][] gr) {
		String s = "";
		for(int i = 0 ; i < SIZE; i++) {
			for(int j = 0 ; j < SIZE; j++) {
//				System.out.println(i + " " + j);
				s = s + gr[i][j].getNumber() + " ";
			}
			s += "\n";
		}
		return s;
	}

	public String getSolutionText() {
		if(numberOfSolves == 0) return "";
		return gridToString(result);
	}
	
	public long getElapsed() {
		return time;
	}
	
	@Override
	public String toString() {
		return gridToString(start);
	}

}
