package minesweeper;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import javax.swing.*;

public class Minesweeper extends JFrame {

	private int sx = 600;
	private int sy = 600;
	
	private int cx = 20, cy = 20;
	private int stepx = sx/cx, stepy = sy/cy;
	
	private CellPanel panel;
	private int mines = 40;
	private int time=0;
	private int minesLeft = mines;
	private JTextField minesField = new JTextField(5);
	
	private Font font = new Font("Verdana", Font.BOLD, (int)(stepx*0.6666));
	
	public Minesweeper() {
		super("Minesweeper!");
		setSize(sx+15, sy+100);
		
		createGUI();
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void createGUI() {
		panel = new CellPanel();
		panel.addMouseListener(new ML());
		setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel();
		JLabel cxLabel = new JLabel("Width");
		buttonPanel.add(cxLabel);
		final JTextField cxField = new JTextField(3);
		cxField.setText(Integer.toString(cx));
		buttonPanel.add(cxField);
		JLabel cyLabel = new JLabel("Height");
		buttonPanel.add(cyLabel);
		final JTextField cyField = new JTextField(3);
		cyField.setText(Integer.toString(cy));
		buttonPanel.add(cyField);
		JLabel mineLabel = new JLabel("Mines");
		buttonPanel.add(mineLabel);
		final JTextField mineField = new JTextField(3);
		mineField.setText(Integer.toString(mines));
		buttonPanel.add(mineField);
		JButton startGame = new JButton("Start Game");
		startGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cx = Integer.parseInt(cxField.getText());
				cy = Integer.parseInt(cyField.getText());
				mines = Integer.parseInt(mineField.getText());
				panel.init();
				panel.repaint();
				System.out.println("Starting new game");
			}
		});
		buttonPanel.add(startGame);
		JLabel timerLabel = new JLabel("Time");
		buttonPanel.add(timerLabel);
		final JTextField timeField = new JTextField(6);
		timeField.setEditable(false);
		Timer timer = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				time++;
				timeField.setText(Integer.toString(time));
			}
		});
		timer.start();
		buttonPanel.add(timeField);
		JLabel minesLabel = new JLabel("Mines Left");
		buttonPanel.add(minesLabel);
		minesField.setEditable(false);
		buttonPanel.add(minesField);
		
		add(buttonPanel, BorderLayout.SOUTH);
	}
	
	private class CellPanel extends JPanel {
		private Cell[][] cells;
		Random rand = new Random();
		public CellPanel() {
			init();
		}
		public void init() {
			time = 0;
			minesLeft = mines;
			minesField.setText(Integer.toString(minesLeft));
			stepx = sx/cx; stepy = sy/cy;
			font = new Font("Verdana", Font.BOLD, (int)(stepx*0.6666));
			cells = new Cell[cx][cy];
			for(int i=0; i<cx; i++) {
				for(int j=0; j<cy; j++) {
					cells[i][j] = new Cell(i, j);
				}
			}
			for(int i=0; i<mines; i++) {
				int x;
				int y;
				
				do {
					x = rand.nextInt(cx);
					y = rand.nextInt(cy);
				} while (cells[x][y].isMine());
				cells[x][y].setMine(true);
			}
			for(int i=0; i<cx; i++) {
				for(int j=0; j<cy; j++) {
					//System.err.println("[" + i + "][" + j + "]");
					if(i>0) if(cells[i-1][j].isMine()) cells[i][j].num++;
					if(j>0) if(cells[i][j-1].isMine()) cells[i][j].num++;
					if(i<cx-1) if(cells[i+1][j].isMine()) cells[i][j].num++;
					if(j<cy-1) if(cells[i][j+1].isMine()) cells[i][j].num++;
					if(i>0 && j>0) if(cells[i-1][j-1].isMine()) cells[i][j].num++;
					if(i<cx-1 && j<cy-1) if(cells[i+1][j+1].isMine()) cells[i][j].num++;
					if(i>0 && j<cy-1) if(cells[i-1][j+1].isMine()) cells[i][j].num++;
					if(i<cx-1 && j>0) if(cells[i+1][j-1].isMine()) cells[i][j].num++;
				}
			}
		}
		
		public void press(Point p, int button) {
			cells[p.x][p.y].revealed = true;
			if(button == 1) {
				expand(p.x, p.y);
				if(cells[p.x][p.y].isMine()) {
					System.out.println("You Lost");
					for(int i=0; i<cx; i++) {
						for(int j=0; j<cy; j++) {
							cells[i][j].revealed = true;
							if(cells[i][j].isMine())
								cells[i][j].c=Color.red;
						}
					}
					repaint();
					JOptionPane.showMessageDialog(this, "You got blown up!");
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					init();
				}
				
			}
			if(button == 3) {
				if(cells[p.x][p.y].isFlagged()) minesLeft++; else minesLeft--;
				minesField.setText(Integer.toString(minesLeft));
				cells[p.x][p.y].flag();
			}
			if(isWin()) {
				repaint();
				JOptionPane.showMessageDialog(this, "You Won in " + time + " seconds!");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				init();
			}
			repaint();
		}
		public void expand(int i, int j) {
			//cells[i][j].revealed = true;
			if(i>0) {
				if(!cells[i-1][j].isMine() && !cells[i-1][j].revealed) {
					cells[i-1][j].revealed = true;
					if (cells[i-1][j].num==0) {
						expand(i-1, j);
					}
				}
			}
			if(j>0) {
				if(!cells[i][j-1].isMine() && !cells[i][j-1].revealed) {
					cells[i][j-1].revealed = true;
					if (cells[i][j-1].num==0) {
						expand(i, j-1);
					}
				}
			}
			if(i<cx-1) {
				if(!cells[i+1][j].isMine() && !cells[i+1][j].revealed) {
					cells[i+1][j].revealed = true;
					if (cells[i+1][j].num==0) {
						expand(i+1, j);
					}
				}
			}
			if(j<cy-1) {
				if(!cells[i][j+1].isMine() && !cells[i][j+1].revealed) {
					cells[i][j+1].revealed = true;
					if (cells[i][j+1].num==0) {
						expand(i, j+1);
					}
				}
			}
			if(i>0 && j>0) {
				if(!cells[i-1][j-1].isMine() && !cells[i-1][j-1].revealed) {
					cells[i-1][j-1].revealed = true;
					if (cells[i-1][j-1].num==0) {
						expand(i-1, j-1);
					}
				}
			}
			if(i<cx-1 && j<cy-1) {
				if(!cells[i+1][j+1].isMine() && !cells[i+1][j+1].revealed) {
					cells[i+1][j+1].revealed = true;
					if (cells[i+1][j+1].num==0) {
						expand(i+1, j+1);
					}
				}
			}
			if(i>0 && j<cy-1) {
				if(!cells[i-1][j+1].isMine() && !cells[i-1][j+1].revealed) {
					cells[i-1][j+1].revealed = true;
					if (cells[i-1][j+1].num==0) {
						expand(i-1, j+1);
					}
				}
			}
			if(i<cx-1 && j>0) {
				if(!cells[i+1][j-1].isMine() && !cells[i+1][j-1].revealed) {
					cells[i+1][j-1].revealed = true;
					if (cells[i+1][j-1].num==0) {
						expand(i+1, j-1);
					}
				}
			}
		}
		
		public boolean isWin() {
			for(int i=0; i<cx; i++) {
				for(int j=0; j<cy; j++) {
					if(!cells[i][j].revealed || (cells[i][j].isFlagged() && !cells[i][j].isMine())) {
						return false;
					}
				}
			}
			return true;
		}
		
		@Override
		public void paint(Graphics g) {
			g.setFont(font);
			int stepx = sx/cx;
			int stepy =  sy/cy;
			for(int i=0; i<cx; i++) {
				for(int j=0; j<cy; j++) {
					g.setColor(Color.gray);
					if(cells[i][j].revealed) g.setColor(cells[i][j].c);
					g.fillRect(i*stepx, j*stepy, stepx, stepy);
					Graphics2D g2d = (Graphics2D) g;
					g2d.setStroke(new BasicStroke(3));
					g2d.setColor(Color.LIGHT_GRAY);
					g.drawRect(i*stepx+1, j*stepy+1, stepx-1, stepy-1);
					g.setColor(Color.black);
					if(cells[i][j].num>0 && !cells[i][j].isMine() && cells[i][j].revealed && !cells[i][j].isFlagged()) 
						g.drawString(Integer.toString(cells[i][j].num), stepx*i+stepx/4, stepy*(j+1)-stepy/4);
				}
			}
			g.setColor(Color.black);
			for(int i=0; i<cx; i++) {
				g.drawLine(stepx*i, 0, stepx*i, sy);
			}
			for(int i=0; i<cy; i++) {
				g.drawLine(0, stepy*i, sx, stepy*i);
			}
			
		}
	}
	
	private class Cell {
		boolean mine = false;
		int x, y;
		int num;
		Color c = Color.white;
		boolean revealed = false;
		private boolean flagged = false;
		
		public Cell(int x, int y) {
			this.x = x; this.y = y;
		}
		
		public Cell(boolean mine, int x, int y) {
			this.mine = mine;
			this.x = x; this.y = y;
		}
		
		public boolean isMine() {
			return mine;
		}
		public void setMine(boolean b) {
			mine = b;
			if(mine) c = Color.red;
		}
		
		public boolean isFlagged() {return flagged;}
		public void flag() {
			flagged = !flagged;
			if(flagged) c = Color.green;
			if(!flagged) {
				revealed = false;
				c = Color.white;
			}
		}
		
		public int getNum() {
			return num;
		}
	}
	
	private class ML extends MouseAdapter {
		public void mouseReleased(MouseEvent e) {
			Point mousePos = e.getPoint();
			mousePos.x = mousePos.x/stepx;
			mousePos.y = mousePos.y/stepy;
			if(e.getButton()==MouseEvent.BUTTON1) {
				panel.press(mousePos, 1);
			}
			else if(e.getButton()==MouseEvent.BUTTON3) {
				panel.press(mousePos, 3);
			}
			//System.out.println(mousePos.toString());
		}
	}
	
	public static void main(String[] args) {
		new Minesweeper();
	}
}
