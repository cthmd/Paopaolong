package paopaolong;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import javax.sound.sampled.*;


public class Bubble extends JComponent implements Runnable{
	private static final long serialVersionUID = 7526472295622776147L;
	public final int r = 50 , speed = 3;
	public final int pop_speed = 20;
	public double bx, by;
	public Tools powerup;
	public double outangle;
	public boolean path_finished = true;
	public int color;
	public int color_next;
	public int count;
	public FallingGrid fg;


	public Bubble(FallingGrid fg){
		super();
		this.fg = fg;
	}

	public void run(){
		if(color > Game.totalcolor){
			powerup = new Tools(fg);
		}
		bx = Game.BWIDTH / 2 + Game.LEFTMARGIN - r;
		by = Game.BHEIGHT + Game.TOPMARGIN - r;
		path_finished = false;
		while (collision() == false) {
			Game.shoot = true;
		    by = by - Math.abs(speed * Math.sin(outangle));
		    bx = bx - speed * Math.cos(outangle);
		    if(bx < 0 || bx > Game.BWIDTH + Game.LEFTMARGIN - 2 * r){
		    	outangle = Math.PI - outangle;
		    	bx = bx - speed * Math.cos(outangle);
		    }
		    try{Thread.sleep(1);} catch(Exception e){}
		    repaint();
		}
		Game.shoot = false;
		path_finished = true;
		color = color_next;
		color_next = (new Random()).nextInt(Game.totalcolor) + 1;
	}

	public void paint_bubblepath(Graphics g){
		if(color == 6){
			g.drawImage(powerup.bombimage, (int)bx, (int)by, this);
		}
		else if(color == 7){
			g.drawImage(powerup.rainbowimage, (int)bx, (int)by, this);
		}
		else if(color == 8){
			g.drawImage(powerup.laserimage, (int)bx, (int)by, this);
		}
		else if(color == 9){
			g.drawImage(powerup.goldcoinimage, (int)bx, (int)(by), this);
		}
		else{
			g.setColor(pick_color(color));
			g.fillOval((int)bx, (int)by, 2 * r, 2 * r);	
		}
	}

	public void paint_pendingbubble(Graphics g){
		g.setColor(pick_color(color));
		g.fillOval((int)Game.BWIDTH / 2 + Game.LEFTMARGIN - r, (int)Game.BHEIGHT + Game.TOPMARGIN - r, 2 * r, 2 * r);		
	}

	public void paint_nextbubble(Graphics g){
		g.setColor(pick_color(color_next));
		g.fillOval(Game.BWIDTH/2 - 200 + Game.LEFTMARGIN,830,2*r,2*r);
	}

	public boolean collision(){ // detect ball collision with grid
		if(by <= 0.3 * r){
			double smallest_distance = Game.BWIDTH;
			int closest = 0;
			double bcx = bx + r;
			for(int column = 0; column < FallingGrid.MAX_COLUMN; column++){
				if(fg.grid[column][0] == 0){
					int gbcx = column * r + r + Game.LEFTMARGIN;
					if(Math.abs(gbcx - bcx) <= smallest_distance){
						smallest_distance = Math.abs(gbcx - bcx);
						closest = column;
					}
				}
			}
			snap(closest, 0, bcx, 0);
			return true;
		}
		for(int row = 0; row < FallingGrid.MAX_ROW; row++){
			for(int column = 0; column < FallingGrid.MAX_COLUMN; column++){
				if(fg.grid[column][row] > 0){
					int gbcx = column * r + r + Game.LEFTMARGIN;
					int gbcy = row * 2 * (r - 3) + r + Game.TOPMARGIN;
					double bcx = bx + r, bcy = by + r; 
					if(Math.sqrt((gbcy-bcy)*(gbcy-bcy) + (gbcx-bcx)*(gbcx-bcx)) <= 2 * r - 5){
						snap(column, row, bcx, bcy);
						return true;
					}
				}
			}
		}
		return false;
	}

	private void snap(int column, int row, double bcx, double bcy){ // place ball into correct position
		double shortest_distance = 4 * r;
		int[] closest = new int[2];
		for(int i = -1; i <= 1; i++){
			for(int j = -2; j <= 2; j++){
				try{
					if(fg.grid[column + j][row + i] == 0){
						int gbcx = (column + j) * r + r + Game.LEFTMARGIN;
						int gbcy = (row + i) * 2 * (r - 3) + r + Game.TOPMARGIN;
						double distance = Math.sqrt((gbcy-bcy)*(gbcy-bcy) + (gbcx-bcx)*(gbcx-bcx));
						if(distance <= shortest_distance){
							shortest_distance = distance;
							closest[0] = column + j;
							closest[1] = row + i;
						}
					}
				}catch(Exception e){}
			}
		}
		
		if(color > Game.totalcolor){
			fg.grid[closest[0]][closest[1]] = 0;
			powerup.gate(color, closest[0], closest[1]);
			drop();
		}
		else{
			fg.grid[closest[0]][closest[1]] = color;
			clearcontrol(closest[0], closest[1]);		
		}
	}

	private void clearcontrol(int column, int row){ 
		ArrayList<int[]> pop_list = new ArrayList<int[]>();
		pop_list.add(new int[] {column, row});
		popcheck(pop_list, pop_list.get(0));
		pop(pop_list);
		drop();
	}

	public void pop(ArrayList<int[]> pop_list){ // clear balls with same color
		if(pop_list.size() > 2){
			bx = -200; by = -200;
			try{Thread.sleep(pop_speed);}catch(Exception e){}
			for(int i = 0; i < pop_list.size(); i++){
				fg.grid[(pop_list.get(i))[0]][(pop_list.get(i))[1]] = 0;
				try{Thread.sleep(pop_speed);}catch(Exception e){}
				repaint();
			}
			playsound("pop.wav");
		}
	}

	public void drop(){ // clear balls that levitate
		for(int i = FallingGrid.MAX_ROW - 1; i > 0; i--){
			for(int j = 0; j < FallingGrid.MAX_COLUMN; j++){
				if(fg.grid[j][i] > 0){
					ArrayList<int[]> levitate_list = new ArrayList<int[]>();
					levitate_list.add(new int[] {j, i});
					if(levitatecheck(j, i, levitate_list) == true){
						fg.grid[j][i] = 0;
						try{Thread.sleep(pop_speed);}catch(Exception e){}
					}	
				}
			}
		}
	}

	public ArrayList<int[]> popcheck(ArrayList<int[]> pop_list, int[] startposition){ // check if ball should be cleared
		int column = startposition[0], row = startposition[1];
		for(int i = -1; i <= 1; i++){
			for(int j = -2; j <= 2; j++){
				try{
					if(fg.grid[column + j][row + i] == color){ // balls with matching colour
						int[] temp = new int[] {column + j, row + i}; // list of balls that might need to be cleared
						boolean contain = false;
						for(int k = 0; k < pop_list.size(); k++){ // check all balls within list
							if((pop_list.get(k))[0] == temp[0] && (pop_list.get(k))[1] == temp[1]){ 
								contain = true;
								break;
							}
						}
						if(contain == false){
							pop_list.add(temp);
							popcheck(pop_list, temp); // check balls around 
						}
					}
				}
				catch(Exception e){}
			}
		}
		return pop_list;
	}

	private boolean levitatecheck(int column, int row, ArrayList<int[]> levitate_list){ // check if ball is levitating
		for(int i = -1; i <= 0; i++){
			for(int j = -2; j <= 2; j++){
				boolean contain = false;
				for(int k = 0; k < levitate_list.size(); k++){
					if((levitate_list.get(k))[0] == column + j && (levitate_list.get(k))[1] == row + i){
						contain = true;
						break;
					}
				}
				if(contain == false){
					int cvalue = 0;
					try{
						cvalue = fg.grid[column + j][row + i];
					}catch(Exception e){}
					if(cvalue > 0 && row + i <= 0){
						return false;
					}
					if(cvalue > 0){
						levitate_list.add(new int[] {column + j, row + i});
						return levitatecheck(column + j, row + i, levitate_list);
					}
				}
			}
		}
		return true;
	}

	private void playsound(String sound){
  		try{
   			Clip clip = AudioSystem.getClip();
			AudioInputStream player = AudioSystem.getAudioInputStream(this.getClass().getResource("/sound/"+sound));
        	clip.open(player);
			clip.start();
  		}catch (Exception e){}
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
