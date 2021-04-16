package paopaolong;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;

public class CentralControl extends Game implements Runnable{
	private static final long serialVersionUID = 7526472295622776147L;
	public final int FWIDTH = 1100;
	public final int FHEIGHT = 1000;
	public static boolean startflag = false;
	public static boolean pausedflag = false;
	public static boolean runningflag = false;
	public static boolean wait = false;
	public static Thread controlthread;
	public static String gamelevel = "Normal";
	public String config;
	public JFrame gw;
	public Game game;
	public JLayeredPane menu;
	public JLayeredPane settingpanel;
	public JLabel levellabel;
	public BufferedImage mainmenuimage;
	public BufferedImage startbuttonimage;
	public BufferedImage settingbuttonimage;
	public BufferedImage titleimage;
	public BufferedImage easybuttonimage;
	public BufferedImage normalbuttonimage;
	public BufferedImage hardbuttonimage;
	public BufferedImage backbuttonimage;
	public BufferedImage importbuttonimage;
	public BufferedImage backgroundimage;

	public static int counter = 1;

	public CentralControl(){
		gw = new JFrame("");
		gw.setSize(FWIDTH, FHEIGHT);
		gw.setResizable(false);
		menu = new JLayeredPane();
		menu.setLayout(null);
		gw.setSize(FWIDTH, FHEIGHT);
		gw.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		construct_menu();
		gw.getContentPane().add(menu);
		gw.setVisible(true);
	}

	public void run(){
		while(true){
			while(startflag == false){
				try{Thread.sleep(1);}catch(Exception e){}
			}
			if(runningflag == false){
				System.out.println("here");
				runningflag = true;
				game = new Game();
				menu.setVisible(false);
				gw.getContentPane().add(game);
				gamethread = new Thread(game);
				gamethread.start();
				try{
					gamethread.join();
					System.out.println("joined");
				}catch(Exception e){}
				menu.setVisible(true);
				startflag = false;
				runningflag = false;
				pausedflag = false;
				wait = false;
				gw.getContentPane().remove(game);
			}
		}
	}

	public void construct_menu(){ // create main menu
		repaint();
		try{
			titleimage = ImageIO.read(this.getClass().getResource("/images/title.png"));
			startbuttonimage = ImageIO.read(this.getClass().getResource("/images/startbutton.png"));
			settingbuttonimage = ImageIO.read(this.getClass().getResource("/images/settingbutton.png"));
			backgroundimage = ImageIO.read(this.getClass().getResource("/images/background.jpg"));
		}catch(Exception ex){System.out.println("not found");}

		JLabel background = new JLabel(new ImageIcon(backgroundimage));
		background.setBounds(0,0,FWIDTH,FHEIGHT);
		menu.add(background, new Integer(1));

		JButton title = new JButton(new ImageIcon(titleimage));
		title.setBorderPainted(false);
		title.setFocusPainted(false);
		title.setContentAreaFilled(false);
		menu.add(title, new Integer(2));
		title.setBounds((FWIDTH-486)/2,100,486,167);

		JButton startbutton = new JButton(new ImageIcon(startbuttonimage));
		startbutton.setBorderPainted(false);
		startbutton.setFocusPainted(false);
		startbutton.setContentAreaFilled(false);
		startbutton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				startflag = true;
			}
		});
		menu.add(startbutton, new Integer(2));
		startbutton.setBounds(FWIDTH/2-300,300,600,200);

		JButton settingbutton = new JButton(new ImageIcon(settingbuttonimage));
		settingbutton.setBorderPainted(false);
		settingbutton.setFocusPainted(false);
		settingbutton.setContentAreaFilled(false);
		settingbutton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				setting();
			}
		});
		menu.add(settingbutton, new Integer(2));
		settingbutton.setBounds(FWIDTH/2-300,600,600,200);
	}

	public void setting(){ // create setting menu
		try{
			easybuttonimage = ImageIO.read(this.getClass().getResource("/images/easybutton.png"));
			normalbuttonimage = ImageIO.read(this.getClass().getResource("/images/normalbutton.png"));
			hardbuttonimage = ImageIO.read(this.getClass().getResource("/images/hardbutton.png"));
			backbuttonimage = ImageIO.read(this.getClass().getResource("/images/backbutton.png"));
			importbuttonimage = ImageIO.read(this.getClass().getResource("/images/importbutton.png"));
		}catch(Exception e){}
		settingpanel = new JLayeredPane();
		settingpanel.setLayout(null);
		JLabel background = new JLabel(new ImageIcon(backgroundimage));
		background.setBounds(0,0,FWIDTH,FHEIGHT);
		settingpanel.add(background, new Integer(1));

		levellabel = new JLabel("Level: " + gamelevel, SwingConstants.CENTER);
		levellabel.setFont(new Font("Microsoft YaHei UI BOLD", Font.BOLD, 42));
		levellabel.setBounds(0, 50, FWIDTH, 100);
		settingpanel.add(levellabel, new Integer(2));

		JButton[] levelbuttons = new JButton[3];
		levelbuttons[0] = new JButton(new ImageIcon(easybuttonimage));
		levelbuttons[0].setName("Easy");
		levelbuttons[1] = new JButton(new ImageIcon(normalbuttonimage));
		levelbuttons[1].setName("Normal");
		levelbuttons[2] = new JButton(new ImageIcon(hardbuttonimage));		
		levelbuttons[2].setName("Hard");
		for(int i = 0; i < 3; i++){
			levelbuttons[i].setBorderPainted(false);
			levelbuttons[i].setFocusPainted(false);
			levelbuttons[i].setContentAreaFilled(false);
			levelbuttons[i].addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					gamelevel = ((JButton)e.getSource()).getName();
					levellabel.setText("Level: " + gamelevel);
					set_level();
				}
			});
			levelbuttons[i].setBounds(FWIDTH/2-150, 200 + i*120, 300, 100);
			settingpanel.add(levelbuttons[i], new Integer(2));
		}

		JButton backbutton = new JButton(new ImageIcon(backbuttonimage));
		backbutton.setBorderPainted(false);
		backbutton.setFocusPainted(false);
		backbutton.setContentAreaFilled(false);
		backbutton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				settingpanel.setVisible(false);
				menu.setVisible(true);
			}
		});
		backbutton.setBounds(FWIDTH/2-50, 680, 100, 100);
		settingpanel.add(backbutton, new Integer(2));

		JButton importbutton = new JButton(new ImageIcon(importbuttonimage));
		importbutton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				FileDialog fdopen = new FileDialog(new Frame(), "Open", FileDialog.LOAD);
				fdopen.setVisible(true);
				if(!(fdopen.getDirectory() == null || fdopen.getFile() == null)){
					try{
						File in = new File(fdopen.getDirectory() + fdopen.getFile());
						long filesize = in.length();
						BufferedInputStream bis = new BufferedInputStream(new FileInputStream(in));  
						byte[] text = new byte[(int) filesize];
						int d, offset = 0;
			        	while (true) {  
			            	d = bis.read();
			            	if(d == -1) break;
			            	else text[offset++] = (byte)d;
			        	}
			        	bis.close();
			        	config = new String(text);
			        	userconfig();
			        	levellabel.setText("Level: Custom");
					}catch(Exception e1){}
				}
			}
		});
		importbutton.setBorderPainted(false);
		importbutton.setFocusPainted(false);
		importbutton.setContentAreaFilled(false);
		importbutton.setBounds(FWIDTH/2-150,560,300,100);
		settingpanel.add(importbutton, new Integer(2));

		gw.getContentPane().add(settingpanel);
		menu.setVisible(false);
		settingpanel.setVisible(true);
	}

	public void userconfig(){ // import user setting into game
		HashMap<String, Integer> uc = new HashMap<String, Integer>();
		uc.put("Time", 0);
		uc.put("Row", 0);
		uc.put("Column", 0);
		uc.put("Color", 0);
		for(String key : uc.keySet()){
			Scanner scan = new Scanner(config);
			String result = "";
			while(scan.hasNext()){
				String line = scan.nextLine();
				if(line.contains(key)){
					char[] c = line.toCharArray();
					for(int i = 0; i < c.length; i++){
						if(c[i] >= '0' && c[i] <= '9'){
							result+=c[i];
						}
					}
				}	
			}
			uc.put(key, Integer.parseInt(result));
			scan.close();
		}
		Game.timelimit = uc.get("Time");
		Game.totalcolor = uc.get("Color");
		FallingGrid.ballnumber = uc.get("Column");
		FallingGrid.MAX_ROW = uc.get("Row") + 1; // Allow one more row for endgame

		int[][] grid = new int[FallingGrid.ballnumber * 2][FallingGrid.MAX_ROW];
		for(int i = 0; i < FallingGrid.MAX_ROW; i++){
			for(int j = 0; j < FallingGrid.ballnumber * 2; j++){
				grid[j][i] = -1;
			}
		}

		char[] cust = config.toCharArray();
		int i = 0;
		for(int count = 0;count < 5;i++){
			if(cust[i] == '\n'){
				count++;
			}
		}
		for(int row = 0; row < FallingGrid.MAX_ROW; row++){
			int column = 0;
			if(row % 2 != 0){
				column = 1;
			}
			for(int count = 0; count < FallingGrid.ballnumber; count++){
				if(i >= cust.length) break;
				if(cust[i] == '.'){
					grid[column][row] = 0;
				}
				else if(row == FallingGrid.MAX_ROW - 1){
					grid[column][row] = 0;
				}
				else{
					grid[column][row] = cust[i] - '0';
				}
				column+=2;
				i++;
			}
			i++;
		}
		FallingGrid.config_grid = grid;
	}

	public static void set_level(){ // set level of game
		Game.totalcolor = 5;
		Game.timelimit = 60;
		FallingGrid.ballnumber = 8;
		Game.BWIDTH = FallingGrid.ballnumber * 100 + FallingGrid.r;
		FallingGrid.level = 3;
		if(gamelevel == "Easy"){
			FallingGrid.level = 3;
			FallingGrid.MAX_ROW = 8;
			Game.totalcolor = 4;
		}
		else if(gamelevel == "Normal"){
			FallingGrid.level = 3;
			FallingGrid.MAX_ROW = 8;
			Game.totalcolor = 5;
		}
		else if(gamelevel == "Hard"){
			FallingGrid.level = 5;
			FallingGrid.MAX_ROW = 9;
			Game.totalcolor = 5;
		}
		System.out.println(gamelevel);
	}

	public static void main(String[] args){
		CentralControl menu = new CentralControl();
		controlthread = new Thread(menu);
		controlthread.start();
		Sound bgm = new Sound();
		bgm.start();
	}
}

class Sound extends Thread{
	public void run(){
		try{
			Clip clip = AudioSystem.getClip();
			AudioInputStream sound = AudioSystem.getAudioInputStream(this.getClass().getResource("/sound/theme.wav"));
        	clip.open(sound);
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		}catch(Exception e){}
	}
}