import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

/**
 *
 * @author pulkit
 */
public class Board {
    JFrame frame;
    JPanel panel;
    JButton[][] button=new JButton[10][10];
    int rows=5;
    int cols=5;
    Point[][] col=new Point[10][10];//(0 none,1 green,2 red),size
    int turn=1;//1==green,2==red
    
    void startGame() throws IOException{
	Object[] options = {"Play", "Exit"};
	int val= JOptionPane.showOptionDialog(null,
		"Wanna Play the Game?",
		"Its All about Chain Reaction :P",
		JOptionPane.DEFAULT_OPTION,
		JOptionPane.QUESTION_MESSAGE,
		null,
		options,
		options[0]);
	if(val==0){
	    disp();
	}
    }
    
    void disp() throws IOException{
	frame=new JFrame();
	turn=1;
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setSize(450,485);
	frame.setResizable(false);
	frame.setTitle("Chain Reaction");
	GridLayout grid=new GridLayout(rows,cols);
	grid.setVgap(0);
	grid.setHgap(0);
	panel=new JPanel(grid);
	for(int i=0;i<rows;i++){
	    for(int j=0;j<cols;j++){
		col[i][j]=new Point(0,0);
		button[i][j]=new JButton(new ImageIcon(getClass().getResource("/res/b.jpg")));
		button[i][j].setBorder(BorderFactory.createLineBorder(Color.green));
		button[i][j].addActionListener(new ImgListener());
		button[i][j].putClientProperty("id", new Point(i,j));
		panel.add(button[i][j]);
	    }
	}
	frame.getContentPane().add(panel);
	frame.setLocationRelativeTo(null);
	frame.setVisible(true);
    }
    
    class ImgListener implements ActionListener{
	Queue q=new LinkedList();
	int winner=0;
	boolean hasWon=false;
	int r=0,g=0;
	
	@Override
	public void actionPerformed(ActionEvent e) {
	    JButton b=(JButton) e.getSource();
	    Object property=b.getClientProperty("id");
	    int x=0,y=0;
	    
	    if(property instanceof Point){
	        Point pt=(Point) property;
	        x=pt.x;
	        y=pt.y;
	    }
	    if((turn==1 && (col[x][y].x==0 || col[x][y].x==1)) || (turn==2 && (col[x][y].x==0 || col[x][y].x==2))){
		q.add(new Point(x,y));
		try {
		    multiply();
		} catch (InterruptedException ex) {
		    //do Nothing
		}
		if(hasWon==false){
		    for(int i=0;i<rows;i++){
			for(int j=0;j<cols;j++){
				if(turn==1){
			        button[i][j].setBorder(BorderFactory.createLineBorder(Color.red));
			    }
			    else{
				button[i][j].setBorder(BorderFactory.createLineBorder(Color.green));
			    }
			}
		    }
		    turn=(turn==1 ? 2 : 1);
		}
		else{
		    frame.dispose();
		    try {
			startGame();
		    } catch (IOException ex) {
			//do Nothing
		    }
		}
	    }
	}
	
	void multiply() throws InterruptedException{
	    while(q.isEmpty()==false){
		Point tp=(Point) q.peek();
		q.remove();
		r=0;g=0;
		if(tp.x>=0 && tp.x<rows && tp.y>=0 && tp.y<cols){
		    col[tp.x][tp.y].y++;
		    if(checkpt(tp)){
			int num=col[tp.x][tp.y].y;
		        button[tp.x][tp.y].setIcon(new ImageIcon(getClass().getResource("/res/"+turn+num+".jpg")));
			col[tp.x][tp.y].x=0;
		        col[tp.x][tp.y].y=0;
			q.add(new Point(tp.x+1,tp.y));
			q.add(new Point(tp.x,tp.y+1));
			q.add(new Point(tp.x-1,tp.y));
			q.add(new Point(tp.x,tp.y-1));
			if(turn==1){
			    g+=2;
			}
			else r+=2;
			button[tp.x][tp.y].setIcon(new ImageIcon(getClass().getResource("/res/b.jpg")));
		    }
		    else{
			col[tp.x][tp.y].x=turn;
		        int num=col[tp.x][tp.y].y;
		        //System.out.println(turn+num);
		        button[tp.x][tp.y].setIcon(new ImageIcon(getClass().getResource("/res/"+turn+num+".jpg")));
		    }
		    if(check()){
			hasWon=true;
			q.clear();
		        JOptionPane.showMessageDialog(null, "Player "+winner+" wins. :)", "Congrats",JOptionPane.INFORMATION_MESSAGE);
		    }
		}
	    }
	}
	
	boolean checkpt(Point pt){
	    boolean flag=false;
	    if(col[pt.x][pt.y].y==2){
		if( (pt.x==0 && (pt.y==0 || pt.y==cols-1)) ){
		    flag=true;
		}
		if( (pt.x==rows-1 && (pt.y==0 || pt.y==cols-1)) ){
		    flag=true;
		}
		return flag;
	    }
	    if(col[pt.x][pt.y].y==3){
		if(pt.x==0 || pt.x==rows-1){
		    if(pt.y>=1 && pt.y<=cols-2){
			flag=true;
		    }
		}
		if(pt.y==0 || pt.y==cols-1){
		    if(pt.x>=1 && pt.x<=rows-2){
			flag=true;
		    }
		}
		return flag;
	    }
	    if(col[pt.x][pt.y].y==4){
		flag=true;
	    }
	    return flag;
	}
	
	boolean check(){
	    for(int i=0;i<rows;i++){
		for(int j=0;j<cols;j++){
		    if(col[i][j].x==1) g++;
		    if(col[i][j].x==2) r++;
		}
	    }
	    if(r==0 && g>=2){
		System.out.println(r+" "+g);
		winner=turn;
		return true;
	    }
	    if(r>=2 && g==0){
		winner=turn;
		return true;
	    }
	    return false;
	}
    }
}