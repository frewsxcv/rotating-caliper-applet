import java.applet.*;
import java.awt.*;
import java.util.Vector;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.util.Dictionary;
import java.util.Hashtable;

/*SWING classes JDK 1.1*/
// import com.sun.java.swing.*;
// import com.sun.java.swing.event.ChangeListener;
// import com.sun.java.swing.event.ChangeEvent;
// import com.sun.java.swing.border.*;

// SWING classes JDK 1.2
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.border.*;

public class RCS extends JApplet implements ActionListener,ItemListener, ChangeListener
{
    JCheckBox Iappcb, Idiamcb, Iwidthcb;
    JButton  Ircanimb, Idmanimb, Iwdanimb, Iaboxanimb, Ipboxanimb, Ianimstopb, Iclearb, Ipauseb, Icontinueb;
    JLabel Idisplab, Ianimlab, Iclearlab;
    JPanel Idispsubpane;
    JPanel Ianim1pane, Ianim2pane, Ianimstoppane, Ianimsubpane;
    JPanel Idisppane, Iseparator, Ianimpane, Idispanimpane;
    JPanel Iclearpane, Imainpane;
    
    JRadioButton IIdraw1rb, IIdraw2rb;
    ButtonGroup IIdrawrbgrp;
    JButton IIclearb1, IIclearb2, IImaxd2panimb, IImind2panimb, IIbridgeanimb, IIcslineanimb, IIanimstopb, IIpauseb, IIcontinueb;
    JLabel IIdrawlab, IIanimlab, IIclearlab;
    JPanel IIdrawpane, IIdcseparator, IIclearpane, IIdrawclearpane;
    JPanel IIanim1pane, IIanim2pane, IIanimstoppane, IIanimsubpane;
    JPanel IIseparator, IIanimpane, IImainsubpane;
    JPanel IImainpane;

    JLabel IIIs1label, IIIs2label, IIIsmainlabel;
    JPanel IIIspane1, IIIspane2, IIIssubpane, IIIspane;
    JSlider animdelaytimes, animframetimes;
    JCheckBox IIIpauseatend;

    JPanel lpane, lpane1, lpane2;
    JLabel lab1, lab2, lab3, lab4;

    RCJIFrame drawwin;
    RCCanvas drawarea;

    RCJFrame mainwindow;
    JDesktopPane desktop;
    JTabbedPane tabpane;


    JLabel mainapplabel;

    int sleeptime = 500;
    int frametime = 10;

    public RCAppAnim rcaa;
    public RCDiamAnim rcda;
    public RCWidthAnim rcwa;
    public RCABoxAnim rcaba;
    public RCPBoxAnim rcpba;
    public RCMaxD2PAnim rcmaxd2pa;
    public RCMinD2PAnim rcmind2pa;
    public RCBridgeAnim rcbridgea;
    public RCCSLineAnim rccslinea;
    
    public RCPolygon m_Polygon, n_Polygon;
    public Vector polyapp, polydiam, polywidth, polyveapp, order;

    public boolean draw1 = true;
    public boolean enable1, enable2 = false;

    public boolean pause = false;
    public boolean pauseatend = true;

    public boolean drawapp = false;
    public boolean drawdiam = false;
    public boolean drawwidth = false;
    public boolean drawcirc = false;
    public boolean rcanim = false;
    public boolean dmanim = false;
    public boolean wdanim = false;
    public boolean aboxanim = false;
    public boolean pboxanim = false;
    public boolean maxd2panim = false;
    public boolean mind2panim = false;
    public boolean bridgeanim = false;
    public boolean cslineanim = false;
	
    // the current cursor location
    public Dimension m_dimCursorLoc;
    
	
    public RCS()
    {
	order = new Vector();
	m_Polygon = new RCPolygon(this);
	n_Polygon = new RCPolygon(this);
	m_dimCursorLoc = new Dimension(0, 0);
    }

    public String getAppletInfo()
    {
	return "Name: Rotating Calipers\r\n" +
	    "Author: Hormoz Pirzadeh\r\n" +
	    "McGill University";
    }


    public void init()
    {
	getContentPane().setLayout(new BorderLayout());
	mainapplabel = new JLabel("Applet initializing...", JLabel.CENTER);
	getContentPane().add("Center", mainapplabel);
	mainwindow = new RCJFrame(this, "Rotating Calipers Applet");
	mainwindow.getContentPane().setLayout(new BorderLayout());
	mainwindow.setSize(900,700);

	//Borders
	EmptyBorder emptyborder = (EmptyBorder) BorderFactory.createEmptyBorder(5,5,5,5);
	BevelBorder rbevborder = (BevelBorder) BorderFactory.createRaisedBevelBorder();
	LineBorder lineborder = (LineBorder) BorderFactory.createLineBorder(Color.darkGray);
	CompoundBorder bevel = (CompoundBorder) BorderFactory.createCompoundBorder(rbevborder,emptyborder);
	CompoundBorder line  = (CompoundBorder) BorderFactory.createCompoundBorder(lineborder,emptyborder);
					    

	//Slider
	animdelaytimes = new JSlider(JSlider.VERTICAL, 0, 4000, 500);
	animdelaytimes.setMajorTickSpacing(1000);
	animdelaytimes.setMinorTickSpacing(250);
	animdelaytimes.setPaintTicks(true);
	animdelaytimes.setSnapToTicks(true);
	Dictionary labelTable = new Hashtable();
	labelTable.put( new Integer( 0 ), new JLabel("0") );
	labelTable.put( new Integer( 1000 ), new JLabel("1") );
	labelTable.put( new Integer( 2000 ), new JLabel("2") );
	labelTable.put( new Integer( 3000 ), new JLabel("3") );
	labelTable.put( new Integer( 4000 ), new JLabel("4") );
	animdelaytimes.setLabelTable(labelTable);
	animdelaytimes.setPaintLabels(true);

	animframetimes = new JSlider(JSlider.VERTICAL, 0, 50, 10);
	animframetimes.setMajorTickSpacing(10);
	animframetimes.setMinorTickSpacing(5);
	animframetimes.setPaintTicks(true);
	animframetimes.setPaintLabels(true);
	animframetimes.setSnapToTicks(true);


	//Listeners
	animdelaytimes.addChangeListener(this);
	animframetimes.addChangeListener(this);


	//Labels
	lab1 = new JLabel("Pointer location: (X,Y)");
	lab2 = new JLabel("Polygon 1: ()");
	lab3 = new JLabel("Polygon 2: ()");
	lab4 = new JLabel("Draw area size: Width x Height");


	///////////////////////////////////////////////////////////
	//TAB I
	///////////////////////////////////////////////////////////

	Iappcb = new JCheckBox("Anti-podal pairs");
	Iappcb.setMnemonic('a');
	Iappcb.setSelected(false);
	Iappcb.setEnabled(false);
	Idiamcb = new JCheckBox("Diameter");
	Idiamcb.setMnemonic('d');
	Idiamcb.setSelected(false);
	Idiamcb.setEnabled(false);
	Iwidthcb = new JCheckBox("Width");
	Iwidthcb.setMnemonic('w');
	Iwidthcb.setSelected(false);
	Iwidthcb.setEnabled(false);
	Iappcb.addItemListener(this);
	Idiamcb.addItemListener(this);
	Iwidthcb.addItemListener(this);


	Ircanimb = new JButton("Anti-podal pairs");
	Ircanimb.setActionCommand("rcanim");
	Ircanimb.setEnabled(false);
	Idmanimb = new JButton("Diameter");
	Idmanimb.setActionCommand("dmanim");
	Idmanimb.setEnabled(false);
	Iwdanimb = new JButton("Width");
	Iwdanimb.setActionCommand("wdanim");
	Iwdanimb.setEnabled(false);
	Iaboxanimb = new JButton("Min. Area Box");
	Iaboxanimb.setActionCommand("aboxanim");
	Iaboxanimb.setEnabled(false);
	Ipboxanimb = new JButton("Min. Perimeter Box");
	Ipboxanimb.setActionCommand("pboxanim");
	Ipboxanimb.setEnabled(false);
	Ianimstopb = new JButton("Stop");
	Ianimstopb.setActionCommand("animstop");
	Ianimstopb.setEnabled(false);
	Ipauseb = new JButton("Pause");
	Ipauseb.setEnabled(false);
	Ipauseb.setActionCommand("pause");
	Icontinueb = new JButton("Continue");
	Icontinueb.setEnabled(false);
	Icontinueb.setActionCommand("continue");
	Iclearb = new JButton("Polygon 1");
	Iclearb.setEnabled(false);
	Iclearb.setActionCommand("clear1");

	Ircanimb.addActionListener(this);
	Idmanimb.addActionListener(this);
	Iwdanimb.addActionListener(this);
	Iaboxanimb.addActionListener(this);
	Ipboxanimb.addActionListener(this);
	Ianimstopb.addActionListener(this);
	Ipauseb.addActionListener(this);
	Icontinueb.addActionListener(this);
	Iclearb.addActionListener(this);
	
	Idisplab = new JLabel("Display", SwingConstants.CENTER);

	Idispsubpane = new JPanel();
	Idispsubpane.setLayout(new BorderLayout());
	Idispsubpane.add("North",Iappcb);
	Idispsubpane.add("Center",Idiamcb);
	Idispsubpane.add("South",Iwidthcb);
	
	Idisppane = new JPanel();
	Idisppane.setBorder(line);
	Idisppane.setLayout(new BorderLayout());
	Idisppane.add("North",Idisplab);
	Idisppane.add("Center",Idispsubpane);

	Ianimlab = new JLabel("Animate", SwingConstants.CENTER);

	Ianim1pane = new JPanel();
	Ianim1pane.setLayout(new BorderLayout());
	Ianim1pane.add("North", Ircanimb);
	Ianim1pane.add("Center",Idmanimb);
	Ianim1pane.add("South",Iwdanimb);
	Ianim2pane = new JPanel();
	Ianim2pane.setLayout(new BorderLayout());
	Ianim2pane.add("North",Iaboxanimb);
	Ianim2pane.add("Center",Ipboxanimb);
	Ianimstoppane = new JPanel();
	Ianimstoppane.setLayout(new BorderLayout());
	Ianimstoppane.add("North", Ipauseb);
	Ianimstoppane.add("Center", Icontinueb);
	Ianimstoppane.add("South",Ianimstopb);
	Ianimsubpane = new JPanel();
	Ianimsubpane.setLayout(new BorderLayout());
	Ianimsubpane.add("North",Ianim1pane);
	Ianimsubpane.add("Center",Ianim2pane);
	Ianimsubpane.add("South",Ianimstoppane);

	Ianimpane = new JPanel();
	Ianimpane.setBorder(line);
	Ianimpane.setLayout(new BorderLayout());
	Ianimpane.add("North",Ianimlab);
	Ianimpane.add("Center",Ianimsubpane);

	Iseparator = new JPanel();
	Iseparator.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

	Idispanimpane = new JPanel();
	Idispanimpane.setLayout(new BorderLayout());
	Idispanimpane.add("North", Idisppane);
	Idispanimpane.add("Center", Iseparator);
	Idispanimpane.add("South", Ianimpane);

	Iclearlab = new JLabel("Clear", SwingConstants.CENTER);
	
	Iclearpane = new JPanel();
	Iclearpane.setLayout(new BorderLayout());
	Iclearpane.setBorder(line);
	Iclearpane.add("North", Iclearlab);
	Iclearpane.add("Center",Iclearb);

	JPanel Imainpane = new JPanel();
	Imainpane.setBorder(bevel);
	Imainpane.setLayout(new BorderLayout());
	Imainpane.add("North", Idispanimpane);
	Imainpane.add("South", Iclearpane);

	////////////////////////////////////////////////////////////////
	lpane1 = new JPanel();
	lpane1.setLayout(new BorderLayout());
	lpane2 = new JPanel();
	lpane2.setLayout(new BorderLayout());

	lpane1.add("North", lab1);
	lpane1.add("Center", lab2);	

	lpane1.add("South", lab3);
	lpane2.add("Center", lab4);

	lpane = new JPanel();
	lpane.setBorder(bevel);
	lpane.setLayout(new BorderLayout());
	lpane.add("North", lpane1);
	lpane.add("Center", lpane2);

	///////////////////////////////////////////////////////////////
	//TAB II
	///////////////////////////////////////////////////////////////
	
	IIdrawlab = new JLabel("Draw", SwingConstants.CENTER);

	IIdraw1rb = new JRadioButton("Polygon 1", true);
	IIdraw2rb = new JRadioButton("Polygon 2", false);
	IIdraw1rb.setActionCommand("draw1");
	IIdraw2rb.setActionCommand("draw2");
	IIdraw1rb.addActionListener(this);
	IIdraw2rb.addActionListener(this);
	IIdrawrbgrp = new ButtonGroup();
	IIdrawrbgrp.add(IIdraw1rb);
	IIdrawrbgrp.add(IIdraw2rb);

	IIdcseparator = new JPanel();
	IIdcseparator.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
	
	IIdrawpane = new JPanel();
	IIdrawpane.setLayout(new BorderLayout());
	IIdrawpane.add("North", IIdrawlab);
	IIdrawpane.add("Center", IIdraw1rb);
	IIdrawpane.add("South", IIdraw2rb);

	IIclearlab = new JLabel("Clear", SwingConstants.CENTER);
	IIclearb1 = new JButton("Polygon 1");
	IIclearb1.setEnabled(false);
	IIclearb1.setActionCommand("clear1");
	IIclearb2 = new JButton("Polygon 2");
	IIclearb2.setEnabled(false);
	IIclearb2.setActionCommand("clear2");
	IIclearb1.addActionListener(this);
	IIclearb2.addActionListener(this);

	IIclearpane = new JPanel();
	IIclearpane.setLayout(new BorderLayout());
	IIclearpane.add("North", IIclearlab);
	IIclearpane.add("Center", IIclearb1);
	IIclearpane.add("South", IIclearb2);
	
	IIdrawclearpane = new JPanel();
	IIdrawclearpane.setLayout(new BorderLayout());
	IIdrawclearpane.setBorder(line);
	IIdrawclearpane.add("North", IIdrawpane);
	IIdrawclearpane.add("Center", IIdcseparator);
	IIdrawclearpane.add("South", IIclearpane);

	IIseparator = new JPanel();
	IIseparator.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

	IIanimlab = new JLabel("Animate", SwingConstants.CENTER);
	IImaxd2panimb = new JButton("Max. distance");
	IImaxd2panimb.setActionCommand("maxd2panim");
	IImaxd2panimb.setEnabled(false);
	IImind2panimb = new JButton("Min. distance");
	IImind2panimb.setActionCommand("mind2panim");
	IImind2panimb.setEnabled(false);
	IIbridgeanimb = new JButton("Bridge points");
	IIbridgeanimb.setActionCommand("bridgeanim");
	IIbridgeanimb.setEnabled(false);
	IIcslineanimb = new JButton("CS lines");
	IIcslineanimb.setActionCommand("cslineanim");
	IIcslineanimb.setEnabled(false);
	IIanimstopb = new JButton("Stop");
	IIanimstopb.setActionCommand("animstop");
	IIanimstopb.setEnabled(false);
	IIpauseb = new JButton("Pause");
	IIpauseb.setActionCommand("pause");
	IIpauseb.setEnabled(false);
	IIcontinueb = new JButton("Continue");
	IIcontinueb.setActionCommand("continue");
	IIcontinueb.setEnabled(false);
	IImaxd2panimb.addActionListener(this);
	IImind2panimb.addActionListener(this);
	IIbridgeanimb.addActionListener(this);
	IIcslineanimb.addActionListener(this);
	IIanimstopb.addActionListener(this);
	IIpauseb.addActionListener(this);
	IIcontinueb.addActionListener(this);

	IIanim1pane = new JPanel();
	IIanim1pane.setLayout(new BorderLayout());
	IIanim1pane.add("North", IImaxd2panimb);
	IIanim1pane.add("Center", IImind2panimb);
	IIanim1pane.add("South", IIbridgeanimb);

	IIanim2pane = new JPanel();
	IIanim2pane.setLayout(new BorderLayout());
	IIanim2pane.add("North", IIcslineanimb);

	IIanimstoppane = new JPanel();
	IIanimstoppane.setLayout(new BorderLayout());
	IIanimstoppane.add("North", IIpauseb);
	IIanimstoppane.add("Center", IIcontinueb);
	IIanimstoppane.add("South", IIanimstopb);
	
	IIanimsubpane = new JPanel();
	IIanimsubpane.setLayout(new BorderLayout());
	IIanimsubpane.add("North", IIanim1pane);
	IIanimsubpane.add("Center", IIanim2pane);
	IIanimsubpane.add("South", IIanimstoppane);
	
	IIanimpane = new JPanel();
	IIanimpane.setLayout(new BorderLayout());
	IIanimpane.setBorder(line);
	IIanimpane.add("North", IIanimlab);
	IIanimpane.add("Center", IIanimsubpane);
	
	IImainsubpane = new JPanel();
	IImainsubpane.setLayout(new BorderLayout());
	IImainsubpane.add("North", IIdrawclearpane);
	IImainsubpane.add("Center", IIseparator);
	IImainsubpane.add("South", IIanimpane);

	IImainpane = new JPanel();
	IImainpane.add("North", IImainsubpane);
	IImainpane.setBorder(bevel);
	////////////////////////////////////////////////////////////////////
	//TAB III
	////////////////////////////////////////////////////////////////////

	IIIspane1 = new JPanel();
	IIIspane1.setLayout(new BorderLayout());
	IIIs1label = new JLabel("Step (s)", JLabel.LEFT);
	IIIspane1.add("North",IIIs1label);
	IIIspane1.add("Center",animdelaytimes);

	IIIspane2 = new JPanel();
	IIIspane2.setLayout(new BorderLayout());
	IIIs2label = new JLabel("Frame (ms)", JLabel.RIGHT);
	IIIspane2.add("North",IIIs2label);
	IIIspane2.add("Center",animframetimes);

	IIIpauseatend = new JCheckBox("Pause when done?");
	IIIpauseatend.setEnabled(true);
	IIIpauseatend.setSelected(true);
	IIIpauseatend.addItemListener(this);

	IIIssubpane = new JPanel();
	IIIssubpane.setLayout(new BorderLayout());
	IIIsmainlabel = new JLabel("Delay times", JLabel.CENTER);
	IIIssubpane.add("North", IIIsmainlabel);
	IIIssubpane.add("West", IIIspane1);
	IIIssubpane.add("Center", IIIspane2);
	IIIssubpane.add("South", IIIpauseatend);

	IIIspane = new JPanel();
	IIIspane.setLayout(new BorderLayout());
	IIIspane.setBorder(bevel);
	IIIspane.add("North", IIIssubpane);


	////////////////////////////////////////////////////////////////////
	//MAIN FRAME
	////////////////////////////////////////////////////////////////////
	desktop = new JDesktopPane();

	tabpane = new JTabbedPane();
	tabpane.addTab("1 Poly", null, Imainpane, "1-Polygon problems");
	tabpane.addTab("2 Poly", null, IImainpane, "2-Polygon problems");
	tabpane.addTab("Settings", null, IIIspane, "Animation settings");
	tabpane.setSelectedIndex(0);
	
	mainwindow.getContentPane().add("South",lpane);
	mainwindow.getContentPane().add("West", tabpane);
	mainwindow.getContentPane().add("Center", desktop);
	mainwindow.show();

	drawwin = new RCJIFrame(this, "Drawing window");
	drawarea = new RCCanvas(this);
	Container wincomp = drawwin.getContentPane();
	wincomp.add(drawarea);
		
	desktop.add(drawwin);
	drawwin.show();
	drawwin.setSize(500,400);
    }

    public void destroy()
    {
    }

    public void start()
    {
	RCVertex v_init = new RCVertex(0,0,0);
	v_init.getWindowSize(this);
	mainapplabel.setText("Applet started");
	pause = false;
    }
	
    public void stop()
    {
	drawwin.setVisible(false);
    }

    public void actionPerformed(java.awt.event.ActionEvent e)
    {
	if (e.getActionCommand().equals("rcanim"))
	    {
		rcanim = true;
		Ianimstopb.setEnabled(true);
		IIanimstopb.setEnabled(true);
		Ipauseb.setEnabled(true);
		IIpauseb.setEnabled(true);
		Ircanimb.setEnabled(false);
		Idmanimb.setEnabled(false);
		Iwdanimb.setEnabled(false);
		Iaboxanimb.setEnabled(false);
		Ipboxanimb.setEnabled(false);
		IImaxd2panimb.setEnabled(false);
		IImind2panimb.setEnabled(false);
		IIbridgeanimb.setEnabled(false);
		IIcslineanimb.setEnabled(false);
		Iclearb.setEnabled(false);
		IIclearb1.setEnabled(false);
		rcaa = new RCAppAnim(this);
		rcaa.init();
		rcaa.animon = true;
	    }
	if (e.getActionCommand().equals("dmanim"))
	    {
		dmanim = true;
		Ianimstopb.setEnabled(true);
		IIanimstopb.setEnabled(true);
		Ipauseb.setEnabled(true);
		IIpauseb.setEnabled(true);
		Ircanimb.setEnabled(false);
		Idmanimb.setEnabled(false);
		Iwdanimb.setEnabled(false);
		Iaboxanimb.setEnabled(false);
		Ipboxanimb.setEnabled(false);
		IImaxd2panimb.setEnabled(false);
		IImind2panimb.setEnabled(false);
		IIbridgeanimb.setEnabled(false);
		IIcslineanimb.setEnabled(false);
		Iclearb.setEnabled(false);
		IIclearb1.setEnabled(false);
		rcda = new RCDiamAnim(this);
		rcda.init();
		rcda.animon = true;
	    }
	if (e.getActionCommand().equals("wdanim"))
	    {
		wdanim = true;
		Ianimstopb.setEnabled(true);
		IIanimstopb.setEnabled(true);
		Ipauseb.setEnabled(true);
		IIpauseb.setEnabled(true);
		Ircanimb.setEnabled(false);
		Idmanimb.setEnabled(false);
		Iwdanimb.setEnabled(false);
		Iaboxanimb.setEnabled(false);
		Ipboxanimb.setEnabled(false);
		IImaxd2panimb.setEnabled(false);
		IImind2panimb.setEnabled(false);
		IIbridgeanimb.setEnabled(false);
		IIcslineanimb.setEnabled(false);
		Iclearb.setEnabled(false);
		IIclearb1.setEnabled(false);
		rcwa = new RCWidthAnim(this);
		rcwa.init();
		rcwa.animon = true;
	    }
	if (e.getActionCommand().equals("aboxanim"))
	    {
		aboxanim = true;
		Ianimstopb.setEnabled(true);
		IIanimstopb.setEnabled(true);
		Ipauseb.setEnabled(true);
		IIpauseb.setEnabled(true);
		Ircanimb.setEnabled(false);
		Idmanimb.setEnabled(false);
		Iwdanimb.setEnabled(false);
		Iaboxanimb.setEnabled(false);
		Ipboxanimb.setEnabled(false);
		IImaxd2panimb.setEnabled(false);
		IImind2panimb.setEnabled(false);
		IIbridgeanimb.setEnabled(false);
		IIcslineanimb.setEnabled(false);
		Iclearb.setEnabled(false);
		IIclearb1.setEnabled(false);
		rcaba = new RCABoxAnim(this);
		rcaba.init();
		rcaba.animon = true;
	    }
	if (e.getActionCommand().equals("pboxanim"))
	    {
		pboxanim = true;
		Ianimstopb.setEnabled(true);
		IIanimstopb.setEnabled(true);
		Ipauseb.setEnabled(true);
		IIpauseb.setEnabled(true);
		Ircanimb.setEnabled(false);
		Idmanimb.setEnabled(false);
		Iwdanimb.setEnabled(false);
		Iaboxanimb.setEnabled(false);
		Ipboxanimb.setEnabled(false);
		IImaxd2panimb.setEnabled(false);
		IImind2panimb.setEnabled(false);
		IIbridgeanimb.setEnabled(false);
		IIcslineanimb.setEnabled(false);
		Iclearb.setEnabled(false);
		IIclearb1.setEnabled(false);
		rcpba = new RCPBoxAnim(this);
		rcpba.init();
		rcpba.animon = true;
	    }
	if (e.getActionCommand().equals("maxd2panim"))
	    {
		if (m_Polygon.clockwise == n_Polygon.clockwise)
		    {
			maxd2panim = true;
			Ianimstopb.setEnabled(true);
			IIanimstopb.setEnabled(true);
			Ipauseb.setEnabled(true);
			IIpauseb.setEnabled(true);
			Ircanimb.setEnabled(false);
			Idmanimb.setEnabled(false);
			Iwdanimb.setEnabled(false);
			Iaboxanimb.setEnabled(false);
			Ipboxanimb.setEnabled(false);
			IImaxd2panimb.setEnabled(false);
			IImind2panimb.setEnabled(false);
			IIbridgeanimb.setEnabled(false);
			IIcslineanimb.setEnabled(false);
			Iclearb.setEnabled(false);
			IIclearb1.setEnabled(false);
			IIclearb2.setEnabled(false);
			rcmaxd2pa = new RCMaxD2PAnim(this);
			rcmaxd2pa.init();
			rcmaxd2pa.animon = true;
		    }
		else
		    {
			String s;
			if (m_Polygon.clockwise)
			    s = "Polygon 1 is clockwise\nwhile Polygon 2 is counterclockwise";
			else
			    s = "Polygon 1 is counterclockwise\nwhile Polygon 2 is clockwise";
			s = s + "\nPlease correct the problem"; 
			JOptionPane.showMessageDialog(mainwindow, s, "Error message", JOptionPane.ERROR_MESSAGE);
		    }
	    }
	if (e.getActionCommand().equals("mind2panim"))
	    {
		if (m_Polygon.clockwise == n_Polygon.clockwise)
		    {
			boolean intersection;
			intersection = m_Polygon.intersectsPoly(n_Polygon);
			if (!intersection)
			    {
				mind2panim = true;
				Ianimstopb.setEnabled(true);
				IIanimstopb.setEnabled(true);
				Ipauseb.setEnabled(true);
				IIpauseb.setEnabled(true);
				Ircanimb.setEnabled(false);
				Idmanimb.setEnabled(false);
				Iwdanimb.setEnabled(false);
				Iaboxanimb.setEnabled(false);
				Ipboxanimb.setEnabled(false);
				IImaxd2panimb.setEnabled(false);
				IImind2panimb.setEnabled(false);
				IIbridgeanimb.setEnabled(false);
				IIcslineanimb.setEnabled(false);
				Iclearb.setEnabled(false);
				IIclearb1.setEnabled(false);
				IIclearb2.setEnabled(false);
				rcmind2pa = new RCMinD2PAnim(this);
				rcmind2pa.init();
				rcmind2pa.animon = true;
			    }
			else
			    {
				String s;
				s = "The polygons intersect\nCannot proceed";
				JOptionPane.showMessageDialog(mainwindow, s, "Error message", JOptionPane.ERROR_MESSAGE);
			    }
		    }
		else
		    {
			String s;
			if (m_Polygon.clockwise)
			    s = "Polygon 1 is clockwise\nwhile Polygon 2 is counterclockwise";
			else
			    s = "Polygon 1 is counterclockwise\nwhile Polygon 2 is clockwise";
			s = s + "\nPlease correct the problem"; 
			JOptionPane.showMessageDialog(mainwindow, s, "Error message", JOptionPane.ERROR_MESSAGE);
		    }
	    }
	if (e.getActionCommand().equals("bridgeanim"))
	    {
		if (m_Polygon.clockwise == n_Polygon.clockwise)
		    {
			bridgeanim = true;
			Ianimstopb.setEnabled(true);
			IIanimstopb.setEnabled(true);
			Ipauseb.setEnabled(true);
			IIpauseb.setEnabled(true);
			Ircanimb.setEnabled(false);
			Idmanimb.setEnabled(false);
			Iwdanimb.setEnabled(false);
			Iaboxanimb.setEnabled(false);
			Ipboxanimb.setEnabled(false);
			IImaxd2panimb.setEnabled(false);
			IImind2panimb.setEnabled(false);
			IIbridgeanimb.setEnabled(false);
			IIcslineanimb.setEnabled(false);
			Iclearb.setEnabled(false);
			IIclearb1.setEnabled(false);
			IIclearb2.setEnabled(false);
			rcbridgea = new RCBridgeAnim(this);
			rcbridgea.init();
			rcbridgea.animon = true;
		    }
		else
		    {
			String s;
			if (m_Polygon.clockwise)
			    s = "Polygon 1 is clockwise\nwhile Polygon 2 is counterclockwise";
			else
			    s = "Polygon 1 is counterclockwise\nwhile Polygon 2 is clockwise";
			s = s + "\nPlease correct the problem"; 
			JOptionPane.showMessageDialog(mainwindow, s, "Error message", JOptionPane.ERROR_MESSAGE);
		    }
	    }
	if (e.getActionCommand().equals("cslineanim"))
	    {
		if (m_Polygon.clockwise == n_Polygon.clockwise)
		    {
			cslineanim = true;
			Ianimstopb.setEnabled(true);
			IIanimstopb.setEnabled(true);
			Ipauseb.setEnabled(true);
			IIpauseb.setEnabled(true);
			Ircanimb.setEnabled(false);
			Idmanimb.setEnabled(false);
			Iwdanimb.setEnabled(false);
			Iaboxanimb.setEnabled(false);
			Ipboxanimb.setEnabled(false);
			IImaxd2panimb.setEnabled(false);
			IImind2panimb.setEnabled(false);
			IIbridgeanimb.setEnabled(false);
			IIcslineanimb.setEnabled(false);
			Iclearb.setEnabled(false);
			IIclearb1.setEnabled(false);
			IIclearb2.setEnabled(false);
			rccslinea = new RCCSLineAnim(this);
			rccslinea.init();
			rccslinea.animon = true;
		    }
		else
		    {
			String s;
			if (m_Polygon.clockwise)
			    s = "Polygon 1 is clockwise\nwhile Polygon 2 is counterclockwise";
			else
			    s = "Polygon 1 is counterclockwise\nwhile Polygon 2 is clockwise";
			s = s + "\nPlease correct the problem"; 
			JOptionPane.showMessageDialog(mainwindow, s, "Error message", JOptionPane.ERROR_MESSAGE);
		    }
	    }
	if (e.getActionCommand().equals("animstop"))
	    {
		pause=false;
		Ircanimb.setEnabled(true);
		Idmanimb.setEnabled(true);
		Iwdanimb.setEnabled(true);
		Iaboxanimb.setEnabled(true);
		Ipboxanimb.setEnabled(true);

		if (enable2)
		    {
			IImaxd2panimb.setEnabled(true);
			IImind2panimb.setEnabled(true);
			IIbridgeanimb.setEnabled(true);
			IIcslineanimb.setEnabled(true);
			IIclearb2.setEnabled(true);
		    }

		Ianimstopb.setEnabled(false);
		IIanimstopb.setEnabled(false);
		Ipauseb.setEnabled(false);
		IIpauseb.setEnabled(false);
		Icontinueb.setEnabled(false);
		IIcontinueb.setEnabled(false);
		if (!(drawapp || drawdiam || drawwidth))
		    {
			Iclearb.setEnabled(true);
			IIclearb1.setEnabled(true);
		    }
		if (rcanim)
		    {
			rcanim = false;
			rcaa.animon = false;
			rcaa = null;
		    }
		if (dmanim)
		    {
			dmanim = false;
			rcda.animon = false;
			rcda = null;
		    }
		if (wdanim)
		    {
			wdanim = false;
			rcwa.animon = false;
			rcwa = null;
		    }
		if (aboxanim)
		    {
			aboxanim = false;
			rcaba.animon = false;
			rcaba = null;
		    }
		if (pboxanim)
		    {
			pboxanim = false;
			rcpba.animon = false;
			rcpba = null;
		    }
		if (maxd2panim)
		    {
			maxd2panim = false;
			rcmaxd2pa.animon = false;
			rcmaxd2pa = null;
		    }
		if (mind2panim)
		    {
			mind2panim = false;
			rcmind2pa.animon = false;
			rcmind2pa = null;
		    }
		if (bridgeanim)
		    {
			bridgeanim = false;
			rcbridgea.animon = false;
			rcbridgea = null;
		    }
		if (cslineanim)
		    {
			cslineanim = false;
			rccslinea.animon = false;
			rccslinea = null;
		    }
	    }
	if (e.getActionCommand().equals("pause"))
	    {
		pause = true;
		Ipauseb.setEnabled(false);
		IIpauseb.setEnabled(false);
		Icontinueb.setEnabled(true);
		IIcontinueb.setEnabled(true);
	    }
	if (e.getActionCommand().equals("continue"))
	    {
		pause = false;
		Ipauseb.setEnabled(true);
		IIpauseb.setEnabled(true);
		Icontinueb.setEnabled(false);
		IIcontinueb.setEnabled(false);
	    }
	if (e.getActionCommand().equals("draw1"))
	    {
		draw1 = true;
	    }
	if (e.getActionCommand().equals("draw2"))
	    {
		draw1 = false;
	    }
	if (e.getActionCommand().equals("clear1"))
	    {
		m_Polygon.clearPolygon();
		m_Polygon.polyInfo();
		disableControls1();
	    }
	if (e.getActionCommand().equals("clear2"))
	    {
		n_Polygon.clearPolygon();
		n_Polygon.polyInfo();
		disableControls2();
	    }
	drawarea.repaint();
    }

    public void itemStateChanged(ItemEvent e)
    {
	Object source = e.getItemSelectable();
	int state = e.getStateChange();
	boolean bstate;
	if (state == ItemEvent.SELECTED)
	    bstate = true;
	else
	    bstate = false;
	if (source == IIIpauseatend)
	    pauseatend = bstate;
	else
	    {
		if (source == Iappcb)
		    drawapp=bstate;
		if (source == Idiamcb)
		    drawdiam = bstate;
		if (source == Iwidthcb)
		    drawwidth = bstate;
		if (drawapp || drawdiam || drawwidth)
		    {
			Iclearb.setEnabled(false);
			IIclearb1.setEnabled(false);
		    }
		else
		    if (!(rcanim || dmanim || wdanim || aboxanim || pboxanim || maxd2panim || mind2panim) || bridgeanim || cslineanim)
			{
			    Iclearb.setEnabled(true);
			    IIclearb1.setEnabled(true);
			}
		drawarea.repaint();
	    }
    }
    
    public void pauseNow()
    {
	pause = true;
	Ipauseb.setEnabled(false);
	IIpauseb.setEnabled(false);
	Icontinueb.setEnabled(true);
	IIcontinueb.setEnabled(true);
    }

    public void stateChanged(ChangeEvent e)
    {
	JSlider source = (JSlider)e.getSource();
	if ((source.equals(animdelaytimes) && (!source.getValueIsAdjusting())))
	    sleeptime = (int)source.getValue();
	else
	    if ((source.equals(animframetimes)) && (!source.getValueIsAdjusting()))
		frametime = (int)source.getValue();
    }
    

    public void enableControls1()
    {
	enable1 = true;
	Iappcb.setEnabled(true);
	Idiamcb.setEnabled(true);
	Iwidthcb.setEnabled(true);
	Ircanimb.setEnabled(true);
	Idmanimb.setEnabled(true);
	Iwdanimb.setEnabled(true);
	Iaboxanimb.setEnabled(true);
	Ipboxanimb.setEnabled(true);
	if (enable2)
	    {
		IImaxd2panimb.setEnabled(true);
		IImind2panimb.setEnabled(true);
		IIbridgeanimb.setEnabled(true);
		IIcslineanimb.setEnabled(true);
	    }
    }

    public void enableControls2()
    {
	enable2=true;
	if (enable1)
	    {
		IImaxd2panimb.setEnabled(true);
		IImind2panimb.setEnabled(true);
		IIbridgeanimb.setEnabled(true);
		IIcslineanimb.setEnabled(true);
	    }
    }

    public void disableControls1()
    {
	enable1 = false;
	polyapp = null;
	polyveapp = null;
	polydiam = null;
	polywidth = null;
	order.removeAllElements();
	
	drawapp = false;
	drawdiam = false;
	drawwidth = false;
	rcanim = false;
	dmanim = false;
	wdanim = false;
	aboxanim = false;
	pboxanim = false;
	maxd2panim = false;
	mind2panim = false;
	bridgeanim = false;
	cslineanim = false;

	Iappcb.setEnabled(false);
	Idiamcb.setEnabled(false);
	Iwidthcb.setEnabled(false);
	Ircanimb.setEnabled(false);
	Idmanimb.setEnabled(false);
	Iwdanimb.setEnabled(false);
	Iaboxanimb.setEnabled(false);
	Ipboxanimb.setEnabled(false);
	IImaxd2panimb.setEnabled(false);
	IImind2panimb.setEnabled(false);
	IIbridgeanimb.setEnabled(false);
	IIcslineanimb.setEnabled(false);
	Ianimstopb.setEnabled(false);
	IIanimstopb.setEnabled(false);
	
	Iclearb.setEnabled(false);
	IIclearb1.setEnabled(false);
	
    }

    public void disableControls2()
    {
	enable2=false;
	IIclearb2.setEnabled(false);
	IImaxd2panimb.setEnabled(false);
	IImind2panimb.setEnabled(false);
	IIbridgeanimb.setEnabled(false);
	IIcslineanimb.setEnabled(false);
    }
}
