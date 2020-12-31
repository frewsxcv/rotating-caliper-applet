import java.applet.*;
import java.awt.*;
import java.util.Vector;

public class RCBridgeAnim extends RCAnimate implements Runnable
{
    RCPolygon poly1;
    RCPolygon poly2;


    Vector bridges;
    int nbridges;
    Vector newbridges;
    int nnewbridges;

    RCCanvas canv;
    int pid;
    RCVertex main;
    RCVertex ymin1, ymin2;
    RCVertex p1, p2;
    RCVertex p1next, p2next;
    RCVertex p1prev, p2prev;
    RCVertex cp1,cp2;
    
    boolean ip1, ip2;
    int iid;
    double ap1, ap2;
    int polysize1, polysize2;
    boolean start;
    boolean computing;

    RCBridgeAnim(RCS ctrl)
    {
	this.ctrl = ctrl;
	this.poly1 = ctrl.m_Polygon;
	this.poly2 = ctrl.n_Polygon;
	this.canv = ctrl.drawarea;
    }

    public synchronized void init1()
    {
	computing=false;
	
	polysize1 = poly1.size();
	polysize2 = poly2.size();

	bridges = new Vector();
	newbridges = new Vector();
	nbridges = nnewbridges = 0;
	main = null;
	
	ymin1 = poly1.vertexAt(0);
	ymin2 = poly2.vertexAt(0);
	
	RCVertex current;
	int index;
	for (index=1; index < polysize1; index++)
	    {
		current = poly1.vertexAt(index);
		if (current.y < ymin1.y)
		    ymin1 = current;
	    }
	RCVertex last = poly1.vertexAt(polysize1 - 1);
	if (last.y <= ymin1.y)
	    ymin1 = last;

	for (index=1; index < polysize2; index++)
	    {
		current = poly2.vertexAt(index);
		if (current.y < ymin2.y)
		    ymin2 = current;
	    }
	last = poly2.vertexAt(polysize2 - 1);
	if (last.y <= ymin2.y)
	    ymin2 = last;

	p1 = cp1 = ymin1;
	p2 = cp2 = ymin2;

	start = true;
    }

    public synchronized void init()
    {
	init1();
	threadrun = false;
	finished = false;
	cvertical = oldvertical = vertical = false;
	cslope = oldslope = slope = 0;
	angle = incr = endangle = 0.0;
	anim = new Thread(this);
	anim.start();
    }
	
    public void draw(Graphics g)
    {
	int index;
	Color bridgeColor = Color.magenta.darker();
	Color RCColor1 = new Color(100,100,255);
	Color RCColor2 = new Color(255,100,100);

	if (threadrun)
	    {
		g.setColor(RCColor1);
		if (!cvertical)
		    {
			poly1.RCLine(cp1, cslope, g);
			g.setColor(RCColor2);
			poly2.RCLine(cp2, cslope, g);
		    }
		else
		    {
			cp1.drawVLine(g);
			g.setColor(RCColor2);
			cp2.drawVLine(g);
		    }
		
		//draw bridges
		g.setColor(bridgeColor);
		if (finished)
		    {
			nnewbridges = newbridges.size();
			bridges.removeAllElements();
			for (index = 0; index < nnewbridges; index++)
			    {
				RCPair bpair = (RCPair)newbridges.elementAt(index);
				bridges.addElement(bpair);
			    }
		    }
		if (bridges != null)
		    {
			nbridges = bridges.size();
			for (index = 0; index < nbridges; index++)
			    {
				RCPair bpair = (RCPair)bridges.elementAt(index);
				RCVertex b1 = bpair.p;
				RCVertex b2 = bpair.q;
				b1.v2vLine(b2,g);
			    }
		    }
	    }
    }

    private synchronized void checkBridge()
    {
	//System.out.println("\n\nLooking for bridge");
	//System.out.println("Increment ID = " + iid);
	int test1, test2, test3, test4;
	if ((iid == 1) || (iid == 2))
	    {
		//check (p1,p2)
		RCVertex p1p = poly1.vertexPrev(p1);
		RCVertex p1n = poly1.vertexNext(p1);
		RCVertex p2p = poly2.vertexPrev(p2);
		RCVertex p2n = poly2.vertexNext(p2);
		test1 = p1.leftturn(p1,p2,p1p);
		test2 = p1.leftturn(p1,p2,p1n);
		test3 = p1.leftturn(p1,p2,p2p);
		test4 = p1.leftturn(p1,p2,p2n);
		//System.out.println("p1p, p1n, p2p, p2n = " + p1p.n + " " + p1n.n + " " + p2p.n + " " + p2n.n);
		//System.out.println("Test results = " + test1 + " " + test2 + " " + test3 + " " + test4);
		if ((test1 == test2) && (test3 == test4) && (test1 == test3))
		    {
			//found new bridge
			//System.out.println("New bridge (p,q) = (" + p1.n + "," + p2.n + ")");
			RCPair bridge = new RCPair(p1,p2);
			newbridges.addElement(bridge);
		    }
	    }
	if (iid == 3)
	    {
		//check (p1prev,p2)
		RCVertex p1p = poly1.vertexPrev(p1prev);
		RCVertex p1n = poly1.vertexNext(p1prev);
		RCVertex p2p = poly2.vertexPrev(p2);
		RCVertex p2n = poly2.vertexNext(p2);
		test1 = p1prev.leftturn(p1prev,p2,p1p);
		test2 = p1prev.leftturn(p1prev,p2,p1n);
		test3 = p1prev.leftturn(p1prev,p2,p2p);
		test4 = p1prev.leftturn(p1prev,p2,p2n);
		//System.out.println("p1p, p1n, p2p, p2n = " + p1p.n + " " + p1n.n + " " + p2p.n + " " + p2n.n);
		//System.out.println("Test results = " + test1 + " " + test2 + " " + test3 + " " + test4);
		if ((test1 == test2) && (test3 == test4) && (test1 == test3))
		    {
			//found new bridge
			//System.out.println("New bridge (p,q) = (" + p1prev.n + "," + p2.n + ")");
			RCPair bridge = new RCPair(p1prev,p2);
			newbridges.addElement(bridge);
		    }
		
		//check (p1,p2prev)
		p1p = poly1.vertexPrev(p1);
		p1n = poly1.vertexNext(p1);
		p2p = poly2.vertexPrev(p2prev);
		p2n = poly2.vertexNext(p2prev);
		test1 = p1.leftturn(p1,p2prev,p1p);
		test2 = p1.leftturn(p1,p2prev,p1n);
		test3 = p1.leftturn(p1,p2prev,p2p);
		test4 = p1.leftturn(p1,p2prev,p2n);
		//System.out.println("p1p, p1n, p2p, p2n = " + p1p.n + " " + p1n.n + " " + p2p.n + " " + p2n.n);
		//System.out.println("Test results = " + test1 + " " + test2 + " " + test3 + " " + test4);
		if ((test1 == test2) && (test3 == test4) && (test1 == test3))
		    {
			//found new bridge
			//System.out.println("New bridge (p,q) = (" + p1.n + "," + p2prev.n + ")");
			RCPair bridge = new RCPair(p1,p2prev);
			newbridges.addElement(bridge);
		    }

		//check (p1,p2)
		p1p = poly1.vertexPrev(p1);
		p1n = poly1.vertexNext(p1);
		p2p = poly2.vertexPrev(p2);
		p2n = poly2.vertexNext(p2);
		test1 = p1.leftturn(p1,p2,p1p);
		test2 = p1.leftturn(p1,p2,p1n);
		test3 = p1.leftturn(p1,p2,p2p);
		test4 = p1.leftturn(p1,p2,p2n);
		//System.out.println("p1p, p1n, p2p, p2n = " + p1p.n + " " + p1n.n + " " + p2p.n + " " + p2n.n);
		//System.out.println("Test results = " + test1 + " " + test2 + " " + test3 + " " + test4);
		if ((test1 == test2) && (test3 == test4) && (test1 == test3))
		    {
			//found new bridge
			//System.out.println("New bridge (p,q) = (" + p1.n + "," + p2.n + ")");
			RCPair bridge = new RCPair(p1,p2);
			newbridges.addElement(bridge);
		    }
	    }
    }
	
    public synchronized void step()
    {   
	//System.out.println("\nSTEP\n");
	if (start) // if we've just started we're not going to determine any distances
	    {
		newbridges.removeAllElements();
	    }
	nextPoints();
	checkBridge();
    }

    private synchronized void nextPoints()
    {
	// Now determine next points;
	//System.out.println("Determining next points");
	//System.out.println("Current points p1,p2 = " + p1.n + " " + p2.n);
	//System.out.println("Slope = " + slope);
	ap1 = poly1.vertexLineAngle(p1, slope, vertical);
	ap2 = poly2.vertexLineAngle(p2, slope, vertical);
	//System.out.println("ap1 = " + ap1);
	//System.out.println("ap2 = " + ap2);
	double minangle = Math.min(ap1,ap2);
	//System.out.println("Min. angle = " + minangle);

	ip1 = ip2 = false;
	main = null;
	iid=0;
	pid = 0;

	p1next = p1prev = p1;
	p2next = p2prev = p2;

	//System.out.println("Which point is main?");
	if (Math.abs(ap1 - minangle) < 0.002)
	    {
		//System.out.println("p1 " + p1.n);
		ip1 = true;
		p1next = poly1.vertexNext(p1);
		main = p1next;
		pid = 1;
		iid += 1;
	    }
	if (Math.abs(ap2 - minangle) < 0.002)
	    {
		//System.out.println("p2 " + p2.n);
		ip2 = true;
		p2next = poly2.vertexNext(p2);
		if (main == null)
		    {
			main = p2next;
			pid = 2;
		    }
		iid += 2;
	    }

	//System.out.println("Found main");
	//System.out.println("main = " + pid + ":" + main.n);
	oldslope = slope;
	oldvertical = vertical;
	if (ip1)
	    {
		//System.out.println("Main is p1next");
		if (p1.x == p1next.x)
		    {
			// Vertical case
			vertical = true;
			slope = 0; //just to have a value
		    }
		else
		    {
			//System.out.println("Non-vertical case");
			vertical = false;
			if (p1.x > p1next.x)
			    slope = (p1.y - p1next.y) / (p1.x - p1next.x);
			else
			    slope = (p1next.y - p1.y) / (p1next.x - p1.x);
			//System.out.println("New slope is " + slope);
		    }
	    }
	else 
	    if (ip2)
		{
		    //System.out.println("Main is p2next");
		    if (p2.x == p2next.x)
			{
				// Vertical case
			    vertical = true;
			    slope = 0; //just to have a value
			}
		    else
			{
				//System.out.println("Non-vertical case");
			    vertical = false;
			    if (p2.x > p2next.x)
				slope = (p2.y - p2next.y) / (p2.x - p2next.x);
			    else
				slope = (p2next.y - p2.y) / (p2next.x - p2.x);
				//System.out.println("New slope is " + slope);
			}
		}
	if ((p1 == ymin1) && (p2 == ymin2) && (!start))
	    {
		//System.out.println("Start over");
		start = true;
		p1 = ymin1;
		p2 = ymin2;
		vertical = false;
		slope = 0;
		if (ctrl.pauseatend && (!computing))
		    ctrl.pauseNow();
	    }
	else
	    {
		//System.out.println("Continue");
		start = false;
		p1 = p1next;
		p2 = p2next;
	    }
	//System.out.println("New points p1, p2 = " + p1.n + " " + p2.n);
	//System.out.println("Slope = " + slope);
    }

    public synchronized void run()
    {
	threadrun = true;
	//int fps = 20;
	//float fsleeptime = (float)(1000/(float)fps);
	//int sleeptime = (int) fsleeptime;
	int ftime = 0;
	int stime = 0;
	try
	    {
		ftime = ctrl.frametime;
		stime = ctrl.sleeptime;
		repaint();
		Thread.sleep(stime);
		while (animon)
		    {
			animpause = ctrl.pause;
			while (animpause)
			    {
				paused = true;
				Thread.sleep(ftime);
				animpause=ctrl.pause;
			    }
			if (paused)
			    Thread.sleep(stime);
			paused = false;
			ftime = ctrl.frametime;
			stime = ctrl.sleeptime;
			step();
			lineAnimInit();
			while (!finished)
			    {
				Thread.sleep(ftime);
				animpause=ctrl.pause;
				lineAnim();
			    }
			cp1 = p1;
			cp2 = p2;
			cvertical = vertical;
			cslope = slope;
			repaint();
			Thread.sleep(stime + ftime);
		    }
		threadrun = false;
	    }
	catch (InterruptedException e)
	    {
		System.out.println("Exception in BridgeAnim");
	    }
    }

    public void repaint()
    {
	ctrl.drawarea.repaint();
    }
}
