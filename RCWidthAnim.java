import java.applet.*;
import java.awt.*;
import java.util.Vector;

public class RCWidthAnim extends RCAnimate implements Runnable
{
    RCPolygon poly;
    Vector app;
    int appsize;

    Vector veapp;
    Vector widthvec;
    Vector newwidthvec;
    int wvecsize;
    int newwvecsize;
    double dist;

    int pass;
    RCCanvas canv;
    RCVertex pv, qv;

    RCWidthAnim(RCS ctrl)
    {
	this.ctrl = ctrl;
	this.poly = ctrl.m_Polygon;
	this.app = ctrl.polyapp;
	this.veapp = ctrl.polyveapp;
	this.canv = ctrl.drawarea;
    }

    public synchronized void init()
    {
	atend = false;
	solnfound = false;
	pass=-1;
	appsize = app.size();
	i = app.size() - 1;
	start_i = i;

	widthvec = new Vector();
	wvecsize = widthvec.size();
	newwidthvec = new Vector();
	newwvecsize = newwidthvec.size();
	dist = Double.POSITIVE_INFINITY;

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
	Color widthColor = new Color(255,100,255);
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
		
		g.setColor(widthColor);
		
		if (finished)
		    {
			newwvecsize = newwidthvec.size();
			widthvec.removeAllElements();
			for (index = 0; index < newwvecsize; index++)
			    {
				RCVEPair pairi = (RCVEPair) newwidthvec.elementAt(index);
				widthvec.addElement(pairi);
			    }
		    }
		int polysize = poly.size();
		if (!(atend && ctrl.pauseatend))
		    {
			wvecsize = widthvec.size();
			for (index=0; index < wvecsize; index++)
			    {
				RCVEPair pairi = (RCVEPair) widthvec.elementAt(index);
				RCPair edge = pairi.e;
				RCVertex p = edge.p;
				RCVertex q = edge.q;
				RCVertex r = pairi.v;
				boolean par = pairi.prl;
				if (!par)
				    {
					p.v2vLine(q, g);
					g.fillOval((int)(r.x-4), (int)(r.yC()-4), 9, 9);
				    } 
				else
				    {
					RCVertex s = poly.vertexAt((r.n + 1) % polysize);
					p.v2vLine(q, g);
					r.v2vLine(s, g);
				    }
			    }
		    }
		else
		    {
			nsolution = solution.size();
			for (index=0; index < nsolution; index++)
			    {
				RCVEPair pairi = (RCVEPair) solution.elementAt(index);
				RCPair edge = pairi.e;
				RCVertex p = edge.p;
				RCVertex q = edge.q;
				RCVertex r = pairi.v;
				boolean par = pairi.prl;
				if (!par)
				    {
					p.v2vLine(q, g);
					g.fillOval((int)(r.x-4), (int)(r.yC()-4), 9, 9);
				    } 
				else
				    {
					RCVertex s = poly.vertexAt((r.n + 1) % polysize);
					p.v2vLine(q, g);
					r.v2vLine(s, g);
				    }
			    }
		    }
	    }
    }
	
    public synchronized void step()
    {   
	//note when we add width pairs here, we add them with the edge in the pair
	//as being the one with sequential vertices in pair and nextpair
	RCPair pair, nextpair;

	RCVertex pvnext, qvnext;
	RCVertex pxmin, pxmax;
	RCVertex qxmin, qxmax;
	RCVertex maxp, maxq;
	boolean sameq;
	double newdist;
	atend = false;

	oldvertical = vertical;
	vertical = false;
	pxmin=pxmax=qxmin=qxmax=pvnext=qvnext=maxp=maxq=null;
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
	boolean parallel;
	if (sameq)
	    {
		int pedgeind = pv.n;
		Vector pve = (Vector) veapp.elementAt(pedgeind);
		if (pve.size() > 1)
		    {
			incr = 3;
			parallel=true;
		    }
		else
		    {
			parallel=false;
		    }
		newdist = Math.abs(pv.area(pv,pvnext,qv)/pv.v2vDist(pvnext));
		if (newdist < dist)
		    {
			newwidthvec.removeAllElements();
			RCVEPair wd = new RCVEPair(pv, pvnext, qv, parallel);
			newwidthvec.addElement(wd);
			dist=newdist;
		    }
		else
		    if (newdist == dist)
			{
			    RCVEPair wd = new RCVEPair(pv, pvnext, qv, parallel);
			    newwidthvec.addElement(wd);
			}
	    }
	else
	    {
		int qedgeind = qv.n;
		Vector qve = (Vector) veapp.elementAt(qedgeind);
		if (qve.size() > 1)
		    {
			parallel = true;
			incr = 3;
		    }
		else
		    {
			parallel = false;
		    }
		newdist = Math.abs(pv.area(qv,qvnext,pv)/qv.v2vDist(qvnext));
		if (newdist < dist)
		    {
			newwidthvec.removeAllElements();
			RCVEPair wd = new RCVEPair(qv, qvnext, pv, parallel);
			newwidthvec.addElement(wd);
			dist=newdist;
		    }
		else
		    if (newdist == dist)
			{
			    RCVEPair wd = new RCVEPair(qv, qvnext, pv, parallel);
			    newwidthvec.addElement(wd);
			}
	    }
	if (i != (appsize - 1))
	    {
		if ((i + incr) > (appsize - 1))
		    new_i = appsize - 1;
		else
		    new_i = i + incr;
	    }
	else
	    {
		if (pass > 0)
		    {
			atend = true;
			//System.out.println("ATEND = " + atend);
			if (!solnfound)
			    {
				solution = new Vector();
				for (int ind = 0; ind < newwidthvec.size(); ind++)
				    {
					RCVEPair wp = (RCVEPair) newwidthvec.elementAt(ind);
					solution.addElement(wp);
				    }
			    }
			if (pv.n == pvnext.n)
			    solnfound = true;
		    }
		if (pv.n != pvnext.n) //parallel pnp0 and q0qnext edges
		    {
			new_i=1;
			newdist = Math.abs(pv.area(pv,qv,qvnext)/qv.v2vDist(qvnext));
			RCVEPair vepair = new RCVEPair(qv,qvnext,pvnext,true);
			if (newdist < dist)
			    {
				newwidthvec.removeAllElements();
				newwidthvec.addElement(vepair);
				dist = newdist;
				if (!solnfound)
				    {
					solution.removeAllElements();
					solution.addElement(vepair);
					solnfound = true;
				    }
			    }
			else
			    if (newdist == dist)
				{
				    newwidthvec.addElement(vepair);
				    if (!solnfound)
					{
					    solution.addElement(vepair);
					    solnfound = true;
					}
				}
		    }
		else
		    {
			newwidthvec.removeAllElements();
			new_i=0;
			dist = Math.abs(pv.area(pv,qv,qvnext)/qv.v2vDist(qvnext));
			RCVEPair vepair = new RCVEPair(qv,qvnext,pv,false);
			newwidthvec.addElement(vepair);
		    }
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



