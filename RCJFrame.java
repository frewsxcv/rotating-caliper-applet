import java.awt.*;
import java.awt.event.*;
// import com.sun.java.swing.*;
import javax.swing.*;

public class RCJFrame extends JFrame
{
    RCS ctrl;
    
    public  RCJFrame(RCS controller, String title)
    {
	super(title);
	this.ctrl = controller;
	addWindowListener(new WindowAdapter()
			  {
			      public void windowClosing(WindowEvent e)
				  {
				      exitapplet();
				  }
			  });
    }
    
    public void exitapplet()
    {
	ctrl.stop();
    }
}

