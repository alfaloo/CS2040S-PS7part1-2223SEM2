import java.util.Arrays;
public class MazeSolverOld implements IMazeSolver {
	private static final int NORTH = 0, SOUTH = 1, EAST = 2, WEST = 3;
	private static int[][] DELTAS = new int[][] {
		{ -1, 0 }, // North
		{ 1, 0 }, // South
		{ 0, 1 }, // East
		{ 0, -1 } // West
	};
	private Maze maze;
	private int startRow, startCol;
	private int endRow, endCol;
	private int count;
	private int[][] shortestPath;

	public MazeSolverOld() {
		this.maze = null;
	}

	@Override
	public void initialize(Maze maze) {
		this.maze = maze;
		this.shortestPath = new int[this.maze.getRows()][this.maze.getColumns()];
	}

	@Override
	public Integer pathSearch(int startRow, int startCol, int endRow, int endCol) throws Exception {
		if (maze == null) {
			throw new Exception("Oh no! You cannot call me without initializing the maze!");
		}

		if (startRow < 0 || startCol < 0 || startRow >= maze.getRows() || startCol >= maze.getColumns() ||
				endRow < 0 || endCol < 0 || endRow >= maze.getRows() || endCol >= maze.getColumns()) {
			throw new IllegalArgumentException("Invalid start/end coordinate");
		}

		// set all visited flag to false
		// before we begin our search
		for (int i = 0; i < maze.getRows(); ++i) {
			for (int j = 0; j < maze.getColumns(); ++j) {
				this.shortestPath[i][j] = -1;
				maze.getRoom(i, j).onPath = false;
			}
		}
		this.shortestPath[startRow][startCol] = 0;

		this.startRow = startRow;
		this.startCol = startCol;
		this.endRow = endRow;
		this.endCol = endCol;

		map(startRow, startCol);
		retrace(this.endRow, this.endCol);
		return count;

	}

	private boolean canGo(int row, int col, int dir) {
		// not needed since our maze has a surrounding block of wall
		// but Joe the Average Coder is a defensive coder!
		if (row + DELTAS[dir][0] < 0 || row + DELTAS[dir][0] >= maze.getRows()) return false;
		if (col + DELTAS[dir][1] < 0 || col + DELTAS[dir][1] >= maze.getColumns()) return false;

		switch (dir) {
			case NORTH:
				return !maze.getRoom(row, col).hasNorthWall();
			case SOUTH:
				return !maze.getRoom(row, col).hasSouthWall();
			case EAST:
				return !maze.getRoom(row, col).hasEastWall();
			case WEST:
				return !maze.getRoom(row, col).hasWestWall();
		}

		return false;
	}

	private int getPathValue(int row, int col, int direction) {
		return this.shortestPath[row + DELTAS[direction][0]][col + DELTAS[direction][1]];
	}

	private void setPathValue(int row, int col, int direction, int value) {
		this.shortestPath[row + DELTAS[direction][0]][col + DELTAS[direction][1]] = value;
	}

	private void map(int row, int col) {
		for (int direction = 0; direction < 4; ++direction) {
			if (canGo(row, col, direction) && (getPathValue(row, col, direction) == -1 ||
					getPathValue(row, col, direction) > this.shortestPath[row][col] + 1)) { // can we go in that direction?
				// yes we can :)
				setPathValue(row, col, direction, this.shortestPath[row][col] + 1);
			}
		}
		for (int direction = 0; direction < 4; ++direction) {
			if (canGo(row, col, direction) &&
					getPathValue(row, col, direction) == this.shortestPath[row][col] + 1) { // can we go in that direction?
				// yes we can :)
				map(row + DELTAS[direction][0], col + DELTAS[direction][1]);
			}
		}
	}

	private int[] findMaxPos(int row, int col) {
		int[] pos = new int[2];
		for (int direction = 0; direction < 4; ++direction) {
			if (canGo(row, col, direction) && getPathValue(row, col, direction) == this.shortestPath[row][col] - 1) { // can we go in that direction?
				// yes we can :)
				pos[0] = row + DELTAS[direction][0];
				pos[1] = col + DELTAS[direction][1];
				return pos;
			}
		}
		return pos;
	}

	private void retrace(int row, int col) {
		this.maze.getRoom(row, col).onPath = true;
		if(this.shortestPath[row][col] == 0) return;
		int[] nextPos = findMaxPos(row, col);
		count++;
		retrace(nextPos[0], nextPos[1]);
	}

	@Override
	public Integer numReachable(int k) throws Exception {
		int result = 0;
		for (int i = 0; i < maze.getRows(); ++i) {
			for (int j = 0; j < maze.getColumns(); ++j) {
				IMazeSolver solver = new MazeSolverOld();
				solver.initialize(this.maze);
				if (solver.pathSearch(startRow, startCol, i, j) == k) result++;
			}
		}
		return result;
	}

	public static void main(String[] args) {
		// Do remember to remove any references to ImprovedMazePrinter before submitting
		// your code!
		try {
			Maze maze = Maze.readMaze("maze-horizontal.txt");
			IMazeSolver solver = new MazeSolverOld();
			solver.initialize(maze);

			System.out.println(solver.pathSearch(0,0,0,1));
			MazePrinter.printMaze(maze);

//			for (int i = 0; i <= 9; ++i) {
//				System.out.println("Steps " + i + " Rooms: " + solver.numReachable(i));
//			}

			System.out.println();

			System.out.println(solver.pathSearch(0,0,2,0));
			MazePrinter.printMaze(maze);

			for (int i = 0; i <= 9; ++i) {
				System.out.println("Steps " + i + " Rooms: " + solver.numReachable(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
