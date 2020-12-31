import java.awt.*;
// import com.sun.java.swing.*;
import javax.swing.*;


public class RCJIFrame extends JInternalFrame
{
    RCS controller;
    
    public  RCJIFrame(RCS controller, String title)
    {
	super(title, true, false, true, true);
	this.setMinimumSize(new Dimension(150,150));
	Color white = new Color(255,255,255);
	this.setBackground(white);
	this.controller = controller;
    }
}

