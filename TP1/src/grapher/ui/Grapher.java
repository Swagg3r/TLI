package grapher.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.BasicStroke;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.Point;
import java.util.Vector;

import static java.lang.Math.*;

import grapher.fc.*;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.SwingUtilities;


public class Grapher extends JPanel {
	static final int MARGIN = 40;
	static final int STEP = 5;
	
	static final BasicStroke dash = new BasicStroke(1, BasicStroke.CAP_ROUND,
	                                                   BasicStroke.JOIN_ROUND,
	                                                   1.f,
	                                                   new float[] { 3.f, 3.f },
	                                                   0.f);
	
	static final BasicStroke boldDash = new BasicStroke(1, BasicStroke.CAP_ROUND,
											            BasicStroke.JOIN_ROUND,
											            1.f,
											            new float[] { 6.f, 6.f },
											            0.f);                                       
	
	protected int W = 400;
	protected int H = 300;
	
	protected double xmin, xmax;
	protected double ymin, ymax;

	protected Vector<Function> functions;
	protected Vector<Boolean> funcStates;
	protected Vector<Color> funcColors;
	
    protected Rectangle selectionRec;
	
	public Grapher() {		
		xmin = -PI/2.; xmax = 3*PI/2;
		ymin = -1.5;   ymax = 1.5;
		
		functions = new Vector<Function>();
		funcStates = new Vector<Boolean>();
		funcColors = new Vector<Color>();
		
		GrapherListener grapherListener = new GrapherListener();
        this.addMouseListener(grapherListener);
        this.addMouseMotionListener(grapherListener);
        this.addMouseWheelListener(grapherListener);
	}
	
	public void add(String expression) {
		add(FunctionFactory.createFunction(expression));
	}
	
	public void add(Function function) {
		functions.add(function);
		funcStates.add(false);
		funcColors.add(Color.BLACK);
		repaint();
	}
	
	public void remove(int[] indices) {
		for(int i = 0; i < indices.length; ++i) {
			functions.remove(indices[i] - i);
			funcStates.remove(indices[i] - i);
			funcColors.remove(indices[i] - i);
			//" - i" because the vectors get smaller with every iteration
    	}
		repaint();
	}
	
	public void edit(int index, int toEdit, Object newVal) {
		
		switch (toEdit) {
			case 0 :
				//edit formula
				/*the provided code doesn't allow to edit an existing function
				 * solution : insert a new one (at the same index), then
				 * delete the old one */
				//add new version
				functions.add(index, FunctionFactory.createFunction((String) newVal));
				funcColors.add(index, funcColors.get(index));
				funcStates.add(index, funcStates.get(index));
				//delete old version
				functions.remove(index+1);
				funcColors.remove(index+1);
				funcStates.remove(index+1);
				break;
			case 1 :
				//edit color
				funcColors.set(index, (Color) newVal);
				break;
		}
		repaint();
	}
    
	public void changeActiveFunctions(int[] selectedRows) {
		//reset all to false
		for(int i = 0; i < functions.size(); ++i) {
    		funcStates.set(i, false);
    	}
		//set selected to true (functions will appear in bold)
		for(int i = 0; i < selectedRows.length; ++i) {
			funcStates.set(selectedRows[i], true);
    	}
	}
		
	public Dimension getPreferredSize() {
		return new Dimension(W, H);
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		W = getWidth();
		H = getHeight();

		Graphics2D g2 = (Graphics2D)g;

		// background
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, W, H);
		
		g2.setColor(Color.BLACK);

		// box
		g2.translate(MARGIN, MARGIN);
		W -= 2*MARGIN;
		H -= 2*MARGIN;
		if(W < 0 || H < 0) { 
			return; 
		}
		
		g2.drawRect(0, 0, W, H);
		
		g2.drawString("x", W, H+10);
		g2.drawString("y", -10, 0);
		
	
		// plot
		g2.clipRect(0, 0, W, H);
		g2.translate(-MARGIN, -MARGIN);

		// x values
		final int N = W/STEP + 1;
		final double dx = dx(STEP);
		double xs[] = new double[N];
		int    Xs[] = new int[N];
		for(int i = 0; i < N; i++) {
			double x = xmin + i*dx;
			xs[i] = x;
			Xs[i] = X(x);
		}

		// draw functions
		for(int i = 0; i < functions.size(); ++i) {
			//set bold if function is "active"
			g2.setStroke(funcStates.get(i).booleanValue() == true ? new BasicStroke(2) : new BasicStroke(1));
			//set color
			g2.setColor(funcColors.get(i));
			// y values
			int Ys[] = new int[N];
			for(int j = 0; j < N; j++) {
				Ys[j] = Y(functions.get(i).y(xs[j]));
			}
			g2.drawPolyline(Xs, Ys, N);
		}
		
		// reset 
		g2.setStroke(new BasicStroke(1));
		g2.setColor(Color.BLACK);
		
		g2.setClip(null);

		// axes
		drawXTick(g2, 0);
		drawYTick(g2, 0);
		
		double xstep = unit((xmax-xmin)/10);
		double ystep = unit((ymax-ymin)/10);
		
		g2.setStroke(dash);
		g2.setColor(Color.GRAY);
		for(double x = xstep; x < xmax; x += xstep)  { drawXTick(g2, x); }
		for(double x = -xstep; x > xmin; x -= xstep) { drawXTick(g2, x); }
		for(double y = ystep; y < ymax; y += ystep)  { drawYTick(g2, y); }
		for(double y = -ystep; y > ymin; y -= ystep) { drawYTick(g2, y); }
		
		// selectionRec	
		if(selectionRec != null) {
			g2.setColor(Color.BLACK);
			g2.setStroke(boldDash);
			g2.draw(selectionRec);
		}
	}
	
	protected double dx(int dX) { 
		return  (double)((xmax-xmin)*dX/W);
	}
	protected double dy(int dY) {
		return -(double)((ymax-ymin)*dY/H);
	}

	protected double x(int X) {
		return xmin+dx(X-MARGIN);
	}
	protected double y(int Y) {
		return ymin+dy((Y-MARGIN)-H);
	}
	
	protected int X(double x) { 
		int Xs = (int)round((x-xmin)/(xmax-xmin)*W);
		return Xs + MARGIN; 
	}
	protected int Y(double y) { 
		int Ys = (int)round((y-ymin)/(ymax-ymin)*H);
		return (H - Ys) + MARGIN;
	}
		
	protected void drawXTick(Graphics2D g2, double x) {
		if(x > xmin && x < xmax) {
			final int X0 = X(x);
			g2.drawLine(X0, MARGIN, X0, H+MARGIN);
			g2.drawString((new Double(x)).toString(), X0, H+MARGIN+15);
		}
	}
	
	protected void drawYTick(Graphics2D g2, double y) {
		if(y > ymin && y < ymax) {
			final int Y0 = Y(y);
			g2.drawLine(0+MARGIN, Y0, W+MARGIN, Y0);
			g2.drawString((new Double(y)).toString(), 5, Y0);
		}
	}
	
	protected static double unit(double w) {
		double scale = pow(10, floor(log10(w)));
		w /= scale;
		if(w < 2)      { w = 2; } 
		else if(w < 5) { w = 5; }
		else           { w = 10; }
		return w * scale;
	}
	

	protected void translate(int dX, int dY) {
		double dx = dx(dX);
		double dy = dy(dY);
		xmin -= dx; xmax -= dx;
		ymin -= dy; ymax -= dy;
		repaint();	
	}
	
	protected void zoom(Point center, int dz) {
		double x = x(center.x);
		double y = y(center.y);
		double ds = exp(dz*.01);
		xmin = x + (xmin-x)/ds; xmax = x + (xmax-x)/ds;
		ymin = y + (ymin-y)/ds; ymax = y + (ymax-y)/ds;
		repaint();	
	}
	
	protected void zoom(Point p0, Point p1) {
		double x0 = x(p0.x);
		double y0 = y(p0.y);
		double x1 = x(p1.x);
		double y1 = y(p1.y);
		xmin = min(x0, x1); xmax = max(x0, x1);
		ymin = min(y0, y1); ymax = max(y0, y1);
		repaint();	
	}
	
	public enum State { UP, CLIC_OR_DRAG, DRAG }
	
	//GrapherListener (State machine using enum State)
	public class GrapherListener implements MouseListener, MouseMotionListener, MouseWheelListener {
		
		static final int D_DRAG = 50;
		
		protected Point pt;
		State state = State.UP;
		
		@Override
		public void mousePressed(MouseEvent e) {
			switch(state) {
				case UP:
					pt = e.getPoint();
					state = State.CLIC_OR_DRAG;
					selectionRec = new Rectangle(pt);
		            break;
		        default: 
		            throw new RuntimeException();
			}
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			switch(state) {
				case CLIC_OR_DRAG:
					if (SwingUtilities.isLeftMouseButton(e)){
						// Zoom in (+5%)
			            zoom(e.getPoint(), 5);
			        }
			        if (SwingUtilities.isRightMouseButton(e)) {
			        	// Zoom out (-5%)
			            zoom(e.getPoint(), -5);
			        }
					state = State.UP;
		            break;
				case DRAG:
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			        if (SwingUtilities.isRightMouseButton(e)) {
			            zoom(pt, e.getPoint());
			        }
					state = State.UP;
					selectionRec = null;
					break;
		        default: 
		            throw new RuntimeException();
			}
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			switch(state) {
				case CLIC_OR_DRAG:
					//left button : switch automatically to state DRAG			
			        if (SwingUtilities.isLeftMouseButton(e)) {
			        	state = State.DRAG;
			        }			        
			        //right button : don't switch if selection is too tiny	
			        if (SwingUtilities.isRightMouseButton(e) && pt.distance(e.getPoint()) > D_DRAG){
			        	selectionRec = new Rectangle(pt);
			        	int minX = Math.min(e.getX(), pt.x);
			            int minY = Math.min(e.getY(), pt.y);
			            int maxX = Math.max(e.getX(), pt.x);
			            int maxY = Math.max(e.getY(), pt.y);
			            selectionRec.x = minX;
			            selectionRec.y = minY;
			            selectionRec.width = maxX - minX;
			            selectionRec.height = maxY - minY;
			            
			            state = State.DRAG;
			        	repaint();
			        }
					break;
				case DRAG:
					//left button : move axes
			        if (SwingUtilities.isLeftMouseButton(e)){
			            setCursor(new Cursor(Cursor.HAND_CURSOR));
			            translate(e.getX() - (int)pt.getX(), e.getY() - (int)pt.getY());
			            pt = e.getPoint();
			        }
			    	//right button : adjust size of selectionRec
			        if (SwingUtilities.isRightMouseButton(e)) {
			        	selectionRec = new Rectangle(pt);
			        	int minX = Math.min(e.getX(), pt.x);
			            int minY = Math.min(e.getY(), pt.y);
			            int maxX = Math.max(e.getX(), pt.x);
			            int maxY = Math.max(e.getY(), pt.y);
			            selectionRec.x = minX;
			            selectionRec.y = minY;
			            selectionRec.width = maxX - minX;
			            selectionRec.height = maxY - minY;
			            //cancel selection if the rectangle is too tiny
			        	if (pt.distance(e.getPoint()) < D_DRAG){
			        		selectionRec = null;
			        		state = State.CLIC_OR_DRAG;
					    }
			        	repaint();
			        }
					break;
				default:					
					throw new RuntimeException();
			}
		}
		
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
	        if (e.getPreciseWheelRotation() < 0) {
	            //Zoom in (+5%)
	            zoom(e.getPoint(), 5);
	        }
	        else if (e.getPreciseWheelRotation() > 0) {
	            //Zoom out (-5%)
	            zoom(e.getPoint(), -5);
	        }
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
		}
		
		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
		}
		
	}
}
