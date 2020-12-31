import java.applet.*;
import java.awt.*;
import java.util.Vector;

public class RCCSLineAnim extends RCAnimate implements Runnable
{
    RCPolygon poly1;
    RCPolygon poly2;


    Vector cspairs;
    int ncspairs;
    Vector newcspairs;
    int nnewcspairs;

    RCCanvas canv;
    int pid;
    RCVertex main;
    RCVertex ymin1, ymax2;
    RCVertex p1, q2;
    RCVertex p1next, q2next;
    RCVertex p1prev, q2prev;
    RCVertex cp1,cq2;
    
    boolean ip1, iq2;
    int iid;
    double ap1, aq2;
    int polysize1, polysize2;
    boolean start;
    boolean computing;

    RCCSLineAnim(RCS ctrl)
    {
	this.ctrl = ctrl;
	this.poly1 = ctrl.m_Polygon;
	this.poly2 = ctrl.n_Polygon;
	this.canv = ctrl.drawarea;
    }

    public synchronized void init1()
    {
	computing = false;
	
	polysize1 = poly1.size();
	polysize2 = poly2.size();

	cspairs = new Vector();
	newcspairs = new Vector();
	ncspairs = nnewcspairs = 0;
	main = null;
	
	ymin1 = poly1.vertexAt(0);
	ymax2 = poly2.vertexAt(0);
	
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
		if (current.y > ymax2.y)
		    ymax2 = current;
	    }
	last = poly2.vertexAt(polysize2 - 1);
	if (last.y >= ymax2.y)
	    ymax2 = last;

	p1 = cp1 = ymin1;
	q2 = cq2 = ymax2;

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
	Color CSColor = Color.green;
	Color RCColor1 = new Color(100,100,255);
	Color RCColor2 = new Color(255,100,100);

	if (threadrun)
	    {
		g.setColor(RCColor1);
		if (!cvertical)
		    {
			poly1.RCLine(cp1, cslope, g);
			g.setColor(RCColor2);
			poly2.RCLine(cq2, cslope, g);
		    }
		else
		    {
			cp1.drawVLine(g);
			g.setColor(RCColor2);
			cq2.drawVLine(g);
		    }
		
		//draw CS-Lines
		g.setColor(CSColor);
		if (finished)
		    {
			nnewcspairs = newcspairs.size();
			cspairs.removeAllElements();
			for (index = 0; index < nnewcspairs; index++)
			    {
				RCPair bpair = (RCPair)newcspairs.elementAt(index);
				cspairs.addElement(bpair);
			    }
		    }
		if (cspairs != null)
		    {
			ncspairs = cspairs.size();
			for (index = 0; index < ncspairs; index++)
			    {
				RCPair cspair = (RCPair)cspairs.elementAt(index);
				RCVertex cs1 = cspair.p;
				RCVertex cs2 = cspair.q;
				cs1.v2vLine(cs2,g);
			    }
		    }
	    }
    }

    private synchronized void checkCSPair()
    {
	//System.out.println("\n\nLooking for cspair");
	//System.out.println("Increment ID = " + iid);
	int test1, test2, test3, test4;
	if ((iid == 1) || (iid == 2))
	    {
		//check (p1,q2)
		RCVertex p1p = poly1.vertexPrev(p1);
		RCVertex p1n = poly1.vertexNext(p1);
		RCVertex q2p = poly2.vertexPrev(q2);
		RCVertex q2n = poly2.vertexNext(q2);
		test1 = p1.leftturn(p1,q2,p1p);
		test2 = p1.leftturn(p1,q2,p1n);
		test3 = p1.leftturn(p1,q2,q2p);
		test4 = p1.leftturn(p1,q2,q2n);
		//System.out.println("p1p, p1n, q2p, q2n = " + p1p.n + " " + p1n.n + " " + q2p.n + " " + q2n.n);
		//System.out.println("Test results = " + test1 + " " + test2 + " " + test3 + " " + test4);
		if ((test1 == test2) && (test3 == test4) && (test1 != test3))
		    {
			//found new cspair
			//System.out.println("New cspair (p,q) = (" + p1.n + "," + q2.n + ")");
			RCPair cspair = new RCPair(p1,q2);
			newcspairs.addElement(cspair);
		    }
	    }
	if (iid == 5)
	    {
		//check (p1prev,q2)
		RCVertex p1p = poly1.vertexPrev(p1prev);
		RCVertex p1n = poly1.vertexNext(p1prev);
		RCVertex q2p = poly2.vertexPrev(q2);
		RCVertex q2n = poly2.vertexNext(q2);
		test1 = p1prev.leftturn(p1prev,q2,p1p);
		test2 = p1prev.leftturn(p1prev,q2,p1n);
		test3 = p1prev.leftturn(p1prev,q2,q2p);
		test4 = p1prev.leftturn(p1prev,q2,q2n);
		//System.out.println("p1p, p1n, q2p, q2n = " + p1p.n + " " + p1n.n + " " + q2p.n + " " + q2n.n);
		//System.out.println("Test results = " + test1 + " " + test2 + " " + test3 + " " + test4);
		if ((test1 == test2) && (test3 == test4) && (test1 != test3))
		    {
			//found new cspair
			//System.out.println("New cspair (p,q) = (" + p1prev.n + "," + q2.n + ")");
			RCPair cspair = new RCPair(p1prev,q2);
			newcspairs.addElement(cspair);
		    }
		
		//check (p1,q2prev)
		p1p = poly1.vertexPrev(p1);
		p1n = poly1.vertexNext(p1);
		q2p = poly2.vertexPrev(q2prev);
		q2n = poly2.vertexNext(q2prev);
		test1 = p1.leftturn(p1,q2prev,p1p);
		test2 = p1.leftturn(p1,q2prev,p1n);
		test3 = p1.leftturn(p1,q2prev,q2p);
		test4 = p1.leftturn(p1,q2prev,q2n);
		//System.out.println("p1p, p1n, q2p, q2n = " + p1p.n + " " + p1n.n + " " + q2p.n + " " + q2n.n);
		//System.out.println("Test results = " + test1 + " " + test2 + " " + test3 + " " + test4);
		if ((test1 == test2) && (test3 == test4) && (test1 != test3))
		    {
			//found new cspair
			//System.out.println("New cspair (p,q) = (" + p1.n + "," + q2prev.n + ")");
			RCPair cspair = new RCPair(p1,q2prev);
			newcspairs.addElement(cspair);
		    }

		//check (p1,q2)
		p1p = poly1.vertexPrev(p1);
		p1n = poly1.vertexNext(p1);
		q2p = poly2.vertexPrev(q2);
		q2n = poly2.vertexNext(q2);
		test1 = p1.leftturn(p1,q2,p1p);
		test2 = p1.leftturn(p1,q2,p1n);
		test3 = p1.leftturn(p1,q2,q2p);
		test4 = p1.leftturn(p1,q2,q2n);
		//System.out.println("p1p, p1n, q2p, q2n = " + p1p.n + " " + p1n.n + " " + q2p.n + " " + q2n.n);
		//System.out.println("Test results = " + test1 + " " + test2 + " " + test3 + " " + test4);
		if ((test1 == test2) && (test3 == test4) && (test1 != test3))
		    {
			//found new cspair
			//System.out.println("New cspair (p,q) = (" + p1.n + "," + q2.n + ")");
			RCPair cspair = new RCPair(p1,q2);
			newcspairs.addElement(cspair);
		    }
	    }
    }
	
    public synchronized void step()
    {   
	//System.out.println("\nSTEP\n");
	if (start) // if we've just started we're not going to determine any distances
	    {
		newcspairs.removeAllElements();
	    }
	nextPoints();
	checkCSPair();
    }

    private synchronized void nextPoints()
    {
	// Now determine next points;
	//System.out.println("Determining next points");
	//System.out.println("Current points p1,q2 = " + p1.n + " " + q2.n);
	//System.out.println("Slope = " + slope);
	ap1 = poly1.vertexLineAngle(p1, slope, vertical);
	aq2 = poly2.vertexLineAngle(q2, slope, vertical);
	//System.out.println("ap1 = " + ap1);
	//System.out.println("aq2 = " + aq2);
	double minangle = Math.min(ap1,aq2);
	//System.out.println("Min. angle = " + minangle);

	ip1 = iq2 = false;
	main = null;
	iid=0;
	pid = 0;

	p1next = p1prev = p1;
	q2next = q2prev = q2;

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
	if (Math.abs(aq2 - minangle) < 0.002)
	    {
		//System.out.println("q2 " + q2.n);
		iq2 = true;
		q2next = poly2.vertexNext(q2);
		if (main == null)
		    {
			main = q2next;
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
	    if (iq2)
		{
		    //System.out.println("Main is q2next");
		    if (q2.x == q2next.x)
			{
				// Vertical case
			    vertical = true;
			    slope = 0; //just to have a value
			}
		    else
			{
				//System.out.println("Non-vertical case");
			    vertical = false;
			    if (q2.x > q2next.x)
				slope = (q2.y - q2next.y) / (q2.x - q2next.x);
			    else
				slope = (q2next.y - q2.y) / (q2next.x - q2.x);
				//System.out.println("New slope is " + slope);
			}
		}
	if ((p1 == ymin1) && (q2 == ymax2) && (!start))
	    {
		//System.out.println("Start over");
		start = true;
		p1 = ymin1;
		q2 = ymax2;
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
		q2 = q2next;
	    }
	//System.out.println("New points p1, q2 = " + p1.n + " " + q2.n);
	//System.out.println("Slope = " + slope);
    }

    public synchronized void run()
    {
	threadrun = true;
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
			cq2 = q2;
			cvertical = vertical;
			cslope = slope;
			repaint();
			Thread.sleep(stime + ftime);
		    }
		threadrun = false;
	    }
	catch (InterruptedException e)
	    {
		System.out.println("Exception in CSLineAnim");
	    }
    }

    public void repaint()
    {
	ctrl.drawarea.repaint();
    }
}
