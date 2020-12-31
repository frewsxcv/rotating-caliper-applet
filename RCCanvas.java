import java.applet.*;
import java.awt.*;
import java.util.Vector;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

// import com.sun.java.swing.*;
import javax.swing.*;

public class RCCanvas extends JComponent implements MouseListener,MouseMotionListener
{
    Dimension m_dimImage; // size of offscreen image
    RCS controller;
    RCJIFrame parent;
    boolean compmoved=false;
    
    public  RCCanvas(RCS controller)
    {
	super();
	this.controller = controller;
	addMouseListener(this);
	addMouseMotionListener(this);
	Cursor chc = new Cursor(Cursor.CROSSHAIR_CURSOR);
	setCursor(chc);
	Color white = new Color(255,255,255);
	this.setBackground(white);
	parent = controller.drawwin;
	parent.addComponentListener(new ComponentAdapter() {
	    public void componentMoved(ComponentEvent e)
		{
		    compmoved = true;
		}});
    }

    public void pointerinfo()
    {
	RCVertex cpt = new RCVertex(controller.m_dimCursorLoc);
	cpt.dim2vx();
	String sCursorLoc = "Pointer location: (" + cpt.x + "," + cpt.y + ")";
	controller.lab1.setText(sCursorLoc);
    }

    public void paint(Graphics g)
    {
	// make sure that the image is the same size as
	// the applet's window
	ResizeImage(false);
	
	// clear the off-screen image (not the on-screen one)
	Color colFG = Color.black;
	Color colBG = new Color(255,255,255);
	g.setColor(colBG);
	g.fillRect(0, 0, m_dimImage.width, m_dimImage.height);
	g.setColor(colFG);
	
	controller.m_Polygon.polyDraw(g);
	controller.n_Polygon.polyDraw(g);
	controller.m_Polygon.drawVertices(g);
	controller.n_Polygon.drawVertices(g);
	
	if (controller.drawapp)
	    controller.m_Polygon.appDraw(controller.polyapp, g);
	if (controller.rcanim)
	    controller.rcaa.draw(g);
	if (controller.drawdiam)
	    controller.m_Polygon.drawDiam(controller.polydiam, g);
	if (controller.drawwidth)
	    controller.m_Polygon.drawWidth(controller.polywidth, g);
	if (controller.dmanim)
	    controller.rcda.draw(g);
	if (controller.wdanim)
	    controller.rcwa.draw(g);
	if (controller.aboxanim)
	    controller.rcaba.draw(g);
	if (controller.pboxanim)
	    controller.rcpba.draw(g);
	if (controller.maxd2panim)
	    controller.rcmaxd2pa.draw(g);
	if (controller.mind2panim)
	    controller.rcmind2pa.draw(g);
	if (controller.bridgeanim)
	    controller.rcbridgea.draw(g);
	if (controller.cslineanim)
	    controller.rccslinea.draw(g);
    }

    public void ResizeImage(boolean force)
    {
	// get the size of the applet's window
	Dimension dim = getSize();
	int nWidth = dim.width;
	int nHeight= dim.height;
		        
	// compare that to the size of our image;
	// if it hasn't changed...
	if (!force)
	    {
		if (m_dimImage != null &&
		    m_dimImage.width == nWidth &&
		    m_dimImage.height== nHeight)
		    {
			// ...don't do anything
			return;
		    }
	    }
    
	m_dimImage = new Dimension(nWidth, nHeight);
	RCVertex vupd = new RCVertex(0,0,0);
	vupd.getWindowSize(controller);
    
    }

    public void mouseEntered(MouseEvent event)
    {
    }
    
    public void mouseExited(MouseEvent event)
    {
    }
    
    public void mousePressed(MouseEvent event)
    {
	if (!(controller.rcanim || controller.dmanim || controller.wdanim || controller.aboxanim || controller.pboxanim) || !(controller.draw1))
	    {
		if (event.getClickCount() == 1)
		    {
			boolean draw1 = controller.draw1;
			RCPolygon poly;
			if (draw1)
			    poly = controller.m_Polygon;
			else
			    poly = controller.n_Polygon;
			int polysize = poly.size();
			int x = event.getX();
			int y = event.getY();
			RCVertex newvertex = new RCVertex(true, x, y, polysize);
			poly.addCheckVertex(newvertex);
			int newpolysize = poly.size();
			if ((polysize >= 4) && (newpolysize > polysize) && draw1)
			    {
				controller.polyapp = poly.antipodalPairs(controller);
				controller.polyveapp = poly.vertexEdgeApps(controller.polyapp);
				controller.polydiam = poly.polyDiameter(controller.polyapp);
				controller.polywidth = poly.polyWidth(controller.polyveapp);
			    }
			if (newpolysize == 1)
			    {
				if (draw1)
				    {
					controller.Iclearb.setEnabled(true);
					controller.IIclearb1.setEnabled(true);
					poly.id = 1;
				    }
				else
				    {
					controller.IIclearb2.setEnabled(true);
					poly.id = 2;
				    }
			    }
			if (newpolysize == 4)
			    {
				RCVertex z = poly.vertexAt(0);
				RCVertex o = poly.vertexAt(1);
				RCVertex t = poly.vertexAt(2);
				if (z.leftturn(z,o,t) == 1)
				    poly.clockwise = false;
				else
				    poly.clockwise = true;
				if (draw1)
				    controller.enableControls1();
				else
				    controller.enableControls2();
			    }
			poly.polyInfo();
		    }
		repaint();
	    }
    }
    
    public void mouseReleased(MouseEvent event)
    {
    }

    public void mouseClicked(MouseEvent event)
    {
    }

    public void mouseMoved(MouseEvent e)
    {
	compmoved = false;
	int x = e.getX();
	int y = e.getY();
	controller.m_dimCursorLoc = new Dimension(x, y);
	pointerinfo();
	repaint();
    }

    public void mouseDragged(MouseEvent e)
    {
    }

}





