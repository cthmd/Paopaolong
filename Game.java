package paopaolong;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;

public class Game extends JPanel implements Runnable{
	private static final long serialVersionUID = 7526472295622776147L;
	public static int BWIDTH = 800;
	public static int BHEIGHT = 800;
	public static int TOPMARGIN = 20;
	public static int LEFTMARGIN = 20;
	public static int DEATHMARK;
	public static boolean shoot = false;
	public static int timelimit = 60;
	public static int totalcolor = 5;
	public static int time = timelimit;
	public JFrame gw;
	public int FWIDTH = 1000;
	public int FHEIGHT = 1000;
	public FallingGrid fg;
	public Shooter shooter;
	public Tools powerup;
	public Bubble b;
	public Countdown timer;
	public PauseControl pausemenu;
	public Endgame detect;
	public Thread gamethread;
	public boolean flag_first_bubble = true;
	public BufferedImage backgroundimage;
	public BufferedImage boardbackgroundimage;

	public Game(){
		DEATHMARK = (FallingGrid.MAX_ROW - 1)*2*(FallingGrid.r-7)+Game.TOPMARGIN;
		BWIDTH = FallingGrid.ballnumber * 100 + FallingGrid.r;
		LEFTMARGIN = 20 + (9 - FallingGrid.ballnumber) * 50;
		fg = new FallingGrid();
		shooter = new Shooter();
		powerup = new Tools(fg);
		b = new Bubble(fg);
		detect = new Endgame();
		b.color = (new Random()).nextInt(totalcolor) + 1;
		b.color_next = b.color;
		flag_first_bubble = false;
		this.addMouseMotionListener(new GameMouseListener()); 
        this.addMouseListener(new GameMouseListener()); 
        try{
        	backgroundimage = ImageIO.read(this.getClass().getResource("/images/background.jpg"));
        	boardbackgroundimage = ImageIO.read(this.getClass().getResource("/images/boardbackground.jpg"));
        }catch(Exception e){}
	}

	public void run(){
		playsound("start");
		timer = new Countdown();
        timer.start();
        while(CentralControl.runningflag == true){
        	if(CentralControl.runningflag == false){
        		break;
        	}
			try{
				Thread.sleep(5);
			}catch (Exception e){}
			repaint();
			while(CentralControl.pausedflag == true || CentralControl.wait == true){
				try{Thread.sleep(1);}catch(Exception e){}
        		if(CentralControl.runningflag == false){
        			break;
        		}
        	}
        }
        System.out.println("end");
	}

	public void paintComponent(Graphics g){
		try{
			super.paintComponent(g);
			g.drawImage(backgroundimage,0,0,this);
			g.setColor(Color.BLACK);
			g.fillRect(LEFTMARGIN - 10, TOPMARGIN - 10, BWIDTH + 20, BHEIGHT + 20);
			g.fillOval(BWIDTH / 2 + LEFTMARGIN - (b.r+10), BHEIGHT + TOPMARGIN -(b.r+10), 2 * (b.r+10), 2 *(b.r+10));		
			g.drawImage(boardbackgroundimage,LEFTMARGIN,TOPMARGIN,BWIDTH, BHEIGHT,this);
			g.setColor(Color.RED);
			g.fillRect(LEFTMARGIN, DEATHMARK + TOPMARGIN, BWIDTH, 10);

			g.setColor(Color.BLACK);
			g.fillRect(BWIDTH + LEFTMARGIN + 40, 25, 15, 50);
			g.fillRect(BWIDTH + LEFTMARGIN + 70, 25, 15, 50);

			g.fillRect(Game.BWIDTH/2 - 200 + Game.LEFTMARGIN - 10, 830, 10, 2*b.r+10);
			g.fillRect(Game.BWIDTH/2 - 200 + Game.LEFTMARGIN + 2*b.r, 830, 10, 2*b.r+10);
			g.fillRect(Game.BWIDTH/2 - 200 + Game.LEFTMARGIN - 10, 830 + 2*b.r, 2*b.r+20, 10);
			
			g.drawImage(boardbackgroundimage,Game.BWIDTH/2 - 200 + Game.LEFTMARGIN, 830, 2*b.r, 2*b.r,this);

			g.setColor(Color.BLACK);
			g.setFont(new Font("Microsoft YaHei UI BOLD", Font.BOLD, 50));
			g.drawString("NEXT", 80, 900);

			shooter.paint_gun(g);

			fg.paint_grid(g);
			if(shoot == true){
				b.paint_bubblepath(g);
			}
			else if(b.color > totalcolor){
				new Tools(fg).paint_pending(g, b.color);
			}
			else{
				b.paint_pendingbubble(g);
			}
			b.paint_nextbubble(g);

			powerup.paint_bomb(g);
			powerup.paint_rainbow(g);
			powerup.paint_layser(g);
			powerup.paint_goldcoin(g);

			timer.paint_healthbar(g);

			if(CentralControl.pausedflag == true){
				pausemenu = new PauseControl();
				pausemenu.paint_pause(g);
			}
			if(detect.win_or_lose() == 1){
				playsound("victory");
				CentralControl.wait = true;
				detect.paint_win(g);
				g.setColor(Color.WHITE);
				String times = Integer.toString(timelimit-time);
				if(time < 10){
					times = " " + times;
				}
				times = times + "S";
				g.setFont(new Font("Microsoft YaHei UI BOLD", Font.BOLD, 110));
				g.drawString(times,BWIDTH/2-300+LEFTMARGIN+370,BHEIGHT/2-250+TOPMARGIN+285);
			}
			if(detect.win_or_lose() == -1){
				playsound("defeat");
				CentralControl.wait = true;
				detect.paint_lose(g);
			}
		}catch(Exception e){}
	}

	public void playsound(String sound){
  		try{
   			Clip clip = AudioSystem.getClip();
			AudioInputStream player = AudioSystem.getAudioInputStream(this.getClass().getResource("/sound/"+sound+".wav"));
        	clip.open(player);
			clip.start();
  		}catch (Exception e){}
	}

	private class Endgame extends JComponent{ // detect if game should be finished win:1 lose:-1 continue:0
		BufferedImage winimage;
		BufferedImage loseimage;

		public Endgame(){
			try{
				winimage = ImageIO.read(this.getClass().getResource("/images/win.png"));
				loseimage = ImageIO.read(this.getClass().getResource("/images/lose.png"));
			}catch(Exception e){}
		}

		public void paint_win(Graphics g){
			g.drawImage(winimage,BWIDTH/2-300+LEFTMARGIN,BHEIGHT/2-250+TOPMARGIN,this);
		}

		public void paint_lose(Graphics g){
			g.drawImage(loseimage,BWIDTH/2-300+LEFTMARGIN,BHEIGHT/2-250+TOPMARGIN,this);
		}

		public int win_or_lose(){
			if(time <= 0){
				return -1;
			}
			for(int i = 0; i < FallingGrid.MAX_COLUMN; i++){
				if(fg.grid[i][FallingGrid.MAX_ROW - 1] > 0){
					return -1;
				}
			}
			int count = 0;
			for(int column = 0; column < FallingGrid.MAX_COLUMN; column++){
				for(int row = 0; row < FallingGrid.MAX_ROW; row++){
					if((column + row) % 2 == 0){
						if(fg.grid[column][row] > 0){
							count++;
						}
					}
				}
			}
			if(count <= 5 && time > 0){
				return 1;
			}
			return 0;
		}
	}


	private class GameMouseListener extends MouseAdapter{
		public void mouseMoved(MouseEvent e){
			if(CentralControl.pausedflag == false){
				shooter.mx = e.getX();
				shooter.my = e.getY();
			}
		}
		public void mouseClicked(MouseEvent e){
			if(e.getX() >= BWIDTH + LEFTMARGIN + 40 && e.getX() <= BWIDTH + LEFTMARGIN + 85 && e.getY() >= 25 && e.getY() <= 75){
				if(CentralControl.pausedflag == false){
					CentralControl.pausedflag = true;
					timer.pauseThread();
				}
				else{
					return;
				}
			}

			if(CentralControl.wait == true){
				if(e.getX() >= BWIDTH/2-300+LEFTMARGIN+142 && e.getX() <= BWIDTH/2-300+LEFTMARGIN+456 && e.getY() >= BHEIGHT/2-250+TOPMARGIN+337 && e.getY() <= BHEIGHT/2-250+TOPMARGIN+440){
					System.out.println("back");
					CentralControl.wait = false;
					CentralControl.pausedflag = false;
					CentralControl.runningflag = false;
				}
				return;
			}

			if(CentralControl.pausedflag == true){
				if(e.getX() >= BWIDTH/2-300+LEFTMARGIN+140 && e.getX() <= BWIDTH/2-300+LEFTMARGIN+460 && e.getY() >= BHEIGHT/2-150+TOPMARGIN+40 && e.getY() <= BHEIGHT/2-150+TOPMARGIN+125){
					System.out.println("resume");
					CentralControl.pausedflag = false;
					timer.resumeThread();
					return;
				}
				if(e.getX() >= BWIDTH/2-300+LEFTMARGIN+60 && e.getX() <= BWIDTH/2-300+LEFTMARGIN+540 && e.getY() >= BHEIGHT/2-150+TOPMARGIN+80 && e.getY() <= BHEIGHT/2-150+TOPMARGIN+260){
					System.out.println("main menu");
					CentralControl.pausedflag = false;
					CentralControl.runningflag = false;
					return;
				}
			}

			if(CentralControl.pausedflag == false){
				if(e.getX() >= LEFTMARGIN && e.getY() <= LEFTMARGIN + BWIDTH && e.getY() >= TOPMARGIN && e.getY() <= TOPMARGIN + BHEIGHT){
					if(b.path_finished == true){
						playsound("shoot");
						if(flag_first_bubble == true) b.color_next = b.color;
						b.outangle = Math.atan2(BHEIGHT + TOPMARGIN - e.getY(), BWIDTH / 2 + LEFTMARGIN - e.getX());
						new Thread(b, "shootout").start();
					}
				}
				if(e.getX() >= powerup.bomb_x && e.getX() <= powerup.bomb_x + 2*b.r && e.getY() >= powerup.bomb_y && e.getY() <= powerup.bomb_y + 2*b.r){
					b.color = 6;
				}
				if(e.getX() >= powerup.rainbow_x && e.getX() <= powerup.rainbow_x + 2*b.r && e.getY() >= powerup.rainbow_y && e.getY() <= powerup.rainbow_y + 2*b.r){
					b.color = 7;
				}
				if(e.getX() >= powerup.layser_x && e.getX() <= powerup.layser_x + 2*b.r && e.getY() >= powerup.layser_y && e.getY() <= powerup.layser_y + 2*b.r){
					b.color = 8;
				}
				if(e.getX() >= powerup.goldcoin_x && e.getX() <= powerup.goldcoin_x + 2*b.r && e.getY() >= powerup.goldcoin_y && e.getY() <= powerup.goldcoin_y + 2*b.r){
					b.color = 9;
				}
			}
		}
	}

	private class Shooter extends JComponent{ // get the angle of shooting
		public double mx = BWIDTH / 2 + LEFTMARGIN, my = 1;
		public void paint_gun(Graphics g){
			int sx = BWIDTH / 2 + LEFTMARGIN, sy = BHEIGHT + TOPMARGIN;
			double theta = Math.atan2(sy - my, sx - mx);
			int length = 1000;
			if(theta <= 0){ 
				length = 0;
			}
			int endx = sx - (int)(length * Math.cos(theta));
			int endy = sy - (int)(length * Math.sin(theta));
			while(endx > BWIDTH + LEFTMARGIN || endx < LEFTMARGIN || endy < TOPMARGIN){
				length = length - 5;
				endx = sx - (int)(length * Math.cos(theta));
				endy = sy - (int)(length * Math.sin(theta));
			}
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setColor(Color.GREEN);
			Stroke dash = new BasicStroke(10, BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND, 5, new float[] {35,30}, 0);
            g2.setStroke(dash);
			g2.drawLine(sx, sy, endx, endy);
			g2.setStroke(new BasicStroke(0));
		}
	}

	public class Countdown extends Thread{ // timer for the game
		int length = 480;
		int speed = length / timelimit;
		private final Object lock = new Object();
    	private boolean pause = false;
		
		public void run(){
			time = timelimit;
			System.out.println("start");
			while(time > 0 && CentralControl.runningflag == true){
				while (pause){
                	onPause();
            	}
				try{
					Thread.sleep(1000);
				}catch(Exception e){}
				time--;
			}
		}
		public void pauseThread(){
        	pause = true;
   		}
   		public void resumeThread(){
	        pause = false;
	        synchronized (lock){
	            lock.notify();
	        }
	    }
	    public void onPause() {
	        synchronized (lock) {
	            try {
	                lock.wait();
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        }
	    }
		public void paint_healthbar(Graphics g){
			g.setFont(new Font("Microsoft YaHei UI BOLD", Font.BOLD, 42));
			g.setColor(Color.BLACK);
			String times = Integer.toString(time);
			if(time < 10){
				times = " " + times;
			}
			g.drawString(times, BWIDTH + LEFTMARGIN + 40, 150);
			g.setColor(Color.BLACK);
			g.fillRect(BWIDTH + LEFTMARGIN + 40, 200, 50, 500);
			g.setColor(Color.RED);
			g.fillRect(BWIDTH + LEFTMARGIN + 50, 210, 30, 480);
			g.setColor(Color.WHITE);
			g.fillRect(BWIDTH + LEFTMARGIN + 50, 210, 30, speed * (timelimit - time));
		}
	}


	private class PauseControl extends JComponent{ // create pause menu
		BufferedImage image;
		public PauseControl(){
			try{
				image = ImageIO.read(this.getClass().getResource("/images/pausemenu.png"));
			}catch(Exception e){}
		}
		public void paint_pause(Graphics g){
			g.drawImage(image, BWIDTH/2-300+LEFTMARGIN,BHEIGHT/2-150+TOPMARGIN,this);
		}
	}
}

