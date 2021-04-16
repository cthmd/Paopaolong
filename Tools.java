package paopaolong;

import java.awt.*;
import java.util.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Tools extends Bubble implements Runnable{
	private static final long serialVersionUID = 7526472295622776147L;
	public FallingGrid fg;
	public int type;
	public final int bomb_x = Game.BWIDTH / 2 + 100 + Game.LEFTMARGIN;
	public final int bomb_y = 830;
	public final int rainbow_x = bomb_x+100;
	public final int rainbow_y = 830;
	public final int layser_x = bomb_x+200;
	public final int layser_y = 830;
	public final int goldcoin_x = bomb_x+300;
	public final int goldcoin_y = 830;
	public BufferedImage bombimage;
	public BufferedImage rainbowimage;
	public BufferedImage laserimage;
	public BufferedImage goldcoinimage;


	public Tools(FallingGrid fg){
		super(fg);
		this.fg = fg;
		try{
			bombimage = ImageIO.read(this.getClass().getResource("/images/bomb.png"));		
			rainbowimage = ImageIO.read(this.getClass().getResource("/images/rainbow.png"));
			laserimage = ImageIO.read(this.getClass().getResource("/images/laser.png"));
			goldcoinimage = ImageIO.read(this.getClass().getResource("/images/goldcoin.png"));
		}catch(Exception e){}
	}

	public void gate(int c, int column, int row){
		type = c;
		if(type == 6) bomb(column, row);
		if(type == 7) rainbow(column, row);
		if(type == 8) laser();
		if(type == 9) goldcoin(column, row);
	}

	private void bomb(int column, int row){
		for(int i = -1; i <= 1; i++){
			for(int j = -2; j <= 2; j++){
				try{
					if(fg.grid[column + j][row + i] > 0){
						fg.grid[column + j][row + i] = 0;			
					}
				}catch(Exception e){}
			}
		}
	}

	private void rainbow(int column, int row){
		ArrayList<Integer> color_list = new ArrayList<Integer>();
		for(int i = -1; i <= 1; i++){
			for(int j = -2; j <= 2; j++){
				try{
					if(fg.grid[column + j][row + i] > 0 && (j + i != 0)){
						color_list.add(fg.grid[column + j][row + i]);	
					}
				}catch(Exception e){}
			}
		}
		super.color =  (new Random()).nextInt(5) + 1;
		try{
			int index = (new Random()).nextInt(color_list.size());	
			super.color = color_list.get(index);
		}catch(Exception e){}
		fg.grid[column][row] = super.color;

		ArrayList<int[]> pop_list = new ArrayList<int[]>();
		pop_list.add(new int[] {column, row});
		popcheck(pop_list, pop_list.get(0));
		pop(pop_list);
	}

	private void laser(){
		System.out.println("laser");
		for(int i = 1; i < FallingGrid.MAX_ROW; i++){
			for(int j = 0; j < FallingGrid.MAX_COLUMN; j++){
				if((i + j) % 2 == 0){
					fg.grid[j][i] = 0;
				}
			}
		}
	}

	private void goldcoin(int column, int row){
		Game.time = Game.time + 10;
		if(Game.time >= Game.timelimit){
			Game.time = Game.timelimit;
		}
		fg.grid[column][row] = 0;
	}

	public void paint_bomb(Graphics g){
		g.drawImage(bombimage, bomb_x, bomb_y, this);
	}

	public void paint_rainbow(Graphics g){
		g.drawImage(rainbowimage, rainbow_x, rainbow_y, this);
	}

	public void paint_layser(Graphics g){
		g.drawImage(laserimage, layser_x, layser_y, this);
	}

	public void paint_goldcoin(Graphics g){
		g.drawImage(goldcoinimage, goldcoin_x, goldcoin_y, this);
	}

	public void paint_pending(Graphics g, int type){
		BufferedImage image = bombimage;
		if(type == 6){
			image = bombimage;
		}
		else if(type == 7){
			image = rainbowimage;
		}
		else if(type == 8){
			image = laserimage;
		}
		else if(type == 9){
			image = goldcoinimage;
		}
		g.drawImage(image,(int)Game.BWIDTH / 2 + Game.LEFTMARGIN - r, (int)Game.BHEIGHT + Game.TOPMARGIN - r, this);		
	}
}