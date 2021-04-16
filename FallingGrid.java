package paopaolong;

import java.awt.*;
import java.util.*;

public class FallingGrid{
	private static final long serialVersionUID = 7526472295622776147L;
	public int[][] grid;
	public static int[][] config_grid;
	public static int ballnumber = 8;
	public static int MAX_COLUMN = 17;
	public static int MAX_ROW = 8;
	public static int r = 50;
	public static int level = 3;

	public FallingGrid(){
		MAX_COLUMN = ballnumber * 2;
		grid = new int[MAX_COLUMN][MAX_ROW];
		for(int row = 0; row < MAX_ROW; row++){
			for(int column = 0; column < MAX_COLUMN; column++){
				if((row + column) % 2 == 0){
					grid[column][row] = (new Random()).nextInt(Game.totalcolor) + 1;
					if(row >= level) grid[column][row] = 0;
				}
				else grid[column][row] = -1;
			}
		}
		if(config_grid != null){
			grid = config_grid;
		}
	}

	public void user_config_grid(int[][] grid){
		this.grid = grid;
	}

	public void paint_grid(Graphics g){
		for(int row = 0; row < MAX_ROW; row++){
			for(int column = 0; column < MAX_COLUMN; column++){
				if(grid[column][row] > 0){
					g.setColor(pick_color(grid[column][row]));
					g.fillOval(column*r+Game.LEFTMARGIN, row*2*(r-7)+Game.TOPMARGIN, 2*r, 2*r);
				}
			}
		}
	}

	private Color pick_color(int a){
		if(a == 1) return new Color(175,94,156);;
		if(a == 2) return new Color(20,162,212);
		if(a == 3) return new Color(0,177,106);
		if(a == 4) return new Color(247,148,30);
		if(a == 5) return new Color(200,197,67);

		if(a == 6) return Color.BLACK;
		if(a == 7) return Color.CYAN;
		if(a == 8) return Color.RED;
		return Color.GRAY;
	}
}