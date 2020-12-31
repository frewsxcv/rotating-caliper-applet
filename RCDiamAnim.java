import java.applet.*;
import java.awt.*;
import java.util.Vector;

public class RCDiamAnim extends RCAnimate implements Runnable
{
    RCPolygon poly;
    Vector app;
    int appsize;
    Vector newmaxpairs;
    Vector maxpairs;
    int nnewmaxpairs;
    int nmaxpairs;
    double dist;

    int pass;
    RCCanvas canv;
    RCVertex pv, qv;

    RCDiamAnim(RCS ctrl)
    {
	this.ctrl = ctrl;
	this.poly = ctrl.m_Polygon;
	this.app = ctrl.polyapp;
	this.canv = ctrl.drawarea;
    }

    public synchronized void init()
    {
	atend = false;
	solnfound = false;
	appsize = app.size();
	i = app.size() - 1;
	start_i = i;

	pass=-1;

	newmaxpairs = new Vector();
	nnewmaxpairs = newmaxpairs.size();
	maxpairs = new Vector();
	nmaxpairs = maxpairs.size();
	dist = 0.0;

	threadrun = false;
	finished = false;
	step();
	cslope = oldslope = slope;
	angle = incr = endangle = 0.0;
	anim = new Thread(this);
	anim.start();
    }
	
    public void draw(Graphics g)
    {
	int index;
	Color diamColor = Color.blue.darker();
	Color RCColor = new Color(100,100,255);

	g.setColor(RCColor);
	if (threadrun)
	    {
		if (!cvertical)
		    {
			poly.RCLine(pv, cslope, g);
			poly.RCLine(qv, cslope, g);
		    }
		else
		    {
			pv.drawVLine(g);
			qv.drawVLine(g);
		    }
		
		g.setColor(diamColor);

		if (finished)
		    {
			//System.out.println(atend + " " + ctrl.pauseatend + " " + pass);
			nnewmaxpairs = newmaxpairs.size();
			maxpairs.removeAllElements();
			for (index=0; index < nnewmaxpairs; index++)
			    {
				RCPair pairi = (RCPair) newmaxpairs.elementAt(index);
				maxpairs.addElement(pairi);
			    }
		    }
		if (!(atend && ctrl.pauseatend))
		    {
			nmaxpairs = maxpairs.size();
			for (index=0; index < nmaxpairs; index++)
			    {
				RCPair pairi = (RCPair) maxpairs.elementAt(index);
				RCVertex v1 = pairi.p;
				RCVertex v2 = pairi.q;
				v1.v2vLine(v2, g);
			    }
		    }
		else
		    {
			nsolution = solution.size();
			for (index = 0; index < nsolution; index++)
			    {
				RCPair pairi = (RCPair) solution.elementAt(index);
				RCVertex v1 = pairi.p;
				RCVertex v2 = pairi.q;
				v1.v2vLine(v2, g);
			    }
		    }
	    }
    }
	
    public synchronized void step()
    {   
	RCPair pair, nextpair;
	RCVertex pvnext, qvnext;
	RCVertex pxmin, pxmax;
	RCVertex qxmin, qxmax;
	RCVertex maxp, maxq;
	boolean sameq;
	//System.out.println("STEP");
	atend = false;
	//System.out.println("ATEND = " + atend);

	oldvertical = vertical;
	vertical = false;
	pvnext=qvnext=pxmin=pxmax=qxmin=qxmax=maxp=maxq=null;
	oldslope = slope;
	
	pair = (RCPair)app.elementAt(i);
	if (i == (appsize - 1))
	    {
		nextpair = (RCPair)app.elementAt(0);
		pv = pair.p;
		qv = pair.q;
		pvnext = nextpair.q;
		qvnext = nextpair.p;
	    }
	else
	    {
		nextpair = (RCPair)app.elementAt(i+1);
		pv = pair.p;
		qv = pair.q;
		pvnext = nextpair.p;
		qvnext = nextpair.q;
	    }

	double newdist = pvnext.v2vDist(qvnext);
	if (newdist > dist)
	    {
		newmaxpairs.removeAllElements();
		newmaxpairs.addElement(nextpair);
		dist = newdist;
	    }
	else
	    if (newdist == dist)
		newmaxpairs.addElement(nextpair);
	
	if (qv == qvnext)
	    {
		sameq = true;
		if (pv.x > pvnext.x)
		    {
			vertical = false;
			pxmax = pv;
			pxmin = pvnext;
		    }
		else
		    {
			if (pv.x < pvnext.x)
			    {
				vertical = false;
				pxmin = pv;
				pxmax = pvnext;
			    }
			else
			    {
				vertical = true;
			    }
		    }
		if (!vertical)
		    slope = ((float)(pxmax.y - pxmin.y))/((float)(pxmax.x - pxmin.x));
	    }
	else
	    {
		sameq=false;
		if (qv.x > qvnext.x)
		    {
			vertical = false;
			qxmax = qv;
			qxmin = qvnext;
		    }
		else
		    {
			if (qv.x < qvnext.x)
			    {
				vertical = false;
				qxmin = qv;
				qxmax = qvnext;
			    }
			else
			    {
				vertical = true;
			    }
		    }
		if (!vertical)
		    slope = ((float)(qxmax.y - qxmin.y))/((float)(qxmax.x - qxmin.x));
	    }
	int incr = 1;
	int j;
	if (sameq)
	    {
		int pedgeind = pv.n;
		Vector pve = (Vector)ctrl.polyveapp.elementAt(pedgeind);
		if (pve.size() > 1)
		    incr = 3;
	    }
	else
	    {
		int qedgeind = qv.n;
		Vector qve = (Vector)ctrl.polyveapp.elementAt(qedgeind);
		if (qve.size() > 1)
		    incr = 3;
	    }
	if (i != (appsize - 1))
	    {
		int low, high;
		if ((i + 2) > (appsize - 1))
		    low = appsize - 1;
		else
		    low = i + 2;
		if ((i + incr+ 1) > (appsize - 1))
		    high = appsize;
		else
		    high = i + incr + 1;
		for (j = low; j < high; j++)
		    {
			RCPair pr = (RCPair) app.elementAt(j);
			RCVertex v1 = pr.p;
			RCVertex v2 = pr.q;
			
			newdist = v1.v2vDist(v2);
			if (newdist > dist)
			    {
				newmaxpairs.removeAllElements();
				newmaxpairs.addElement(pr);
				dist = newdist;
			    }
			else
			    if (newdist == dist)
				newmaxpairs.addElement(pr);
		    }
		if ((i + incr) > (appsize - 1))
		    new_i = appsize - 1;
		else
		    new_i = i + incr;
	    }
	else
	    {
		RCPair pr = (RCPair) app.elementAt(0);
		RCVertex v1 = pr.p;
		RCVertex v2 = pr.q;
		if (pass > 0)
		    {
			atend = true;
			//System.out.println("ATEND = " + atend);
			if (!solnfound)
			    {
				solution = new Vector();
				for (int ind = 0; ind < newmaxpairs.size(); ind++)
				    {
					RCPair mp = (RCPair) newmaxpairs.elementAt(ind);
					solution.addElement(mp);
				    }
			    }
			if (pv.n == v2.n)
			    solnfound = true;
		    }
		if (pv.n != v2.n) //parallel pnp0 and q0qnext edges
		    {
			new_i=1;
			pr = (RCPair) app.elementAt(1);
			v1 = pr.p;
			v2 = pr.q;
				
			newdist = v1.v2vDist(v2);
			if (newdist > dist)
			    {
				newmaxpairs.removeAllElements();
				newmaxpairs.addElement(pair);
				dist = newdist;
				if (!solnfound)
				    {
					solution.removeAllElements();
					solution.addElement(pair);
					solnfound = true;
				    }
			    }
			
			else
			    if (newdist == dist)
				{
				    newmaxpairs.addElement(pair);
				    if (!solnfound)
					{
					    solution.addElement(pair);
					    solnfound = true;
					}
				}
		    }
		else
		    {
			new_i=0;
			dist = v1.v2vDist(v2);
			newmaxpairs.removeAllElements();
			newmaxpairs.addElement(pr);
		    }

		////System.out.println("At end? Pause at end? " + atend + " " + ctrl.pauseatend);
		if ((ctrl.pauseatend) && (pass > 0))
		    ctrl.pauseNow();
		pass++;
	    }
    }
    
    public void repaint()
    {
	ctrl.drawarea.repaint();
    }

}



