import java.applet.*;
import java.awt.*;
import java.util.Vector;

public class RCAppAnim extends RCAnimate implements Runnable
{
    RCPolygon poly;
    Vector app;
    int appsize;
    int pass=-1;

    RCCanvas canv;
    RCVertex pv, qv;
    int limit;
    boolean nolimit = false;
    
    RCAppAnim(RCS ctrl)
    {
	this.ctrl = ctrl;
	this.poly = ctrl.m_Polygon;
	this.app = ctrl.polyapp;
	this.canv = ctrl.drawarea;
    }


    public synchronized void init()
    {
	appsize = app.size();
	i = appsize - 1;
	start_i = i;

	ctrl.pause = false;
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
	Color RCColor = new Color(100,100,255);
	Color appColor = new Color(255,100,255);

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
		
		g.setColor(appColor);
		
		if (!nolimit)
		    {
			for (index = 0; index < (limit-1); index++)
			    {
				RCPair pr = (RCPair) app.elementAt(index);
				RCVertex v1 = pr.p;
				RCVertex v2 = pr.q;
				v1.v2vLine(v2,g);
			    }
			if (finished)
			    {
				RCPair pr = (RCPair) app.elementAt(limit-1);
				RCVertex v1 = pr.p;
				RCVertex v2 = pr.q;
				v1.v2vLine(v2,g);
			    }
		    }
		else
		    for (index = 0; index < app.size(); index++)
			{
				RCPair pr = (RCPair) app.elementAt(index);
				RCVertex v1 = pr.p;
				RCVertex v2 = pr.q;
				v1.v2vLine(v2,g);
			}
	    }
    }
	

    public synchronized void step()
    {   
	nolimit = false;
	RCPair pair, nextpair;
	RCVertex pvnext, qvnext;
	RCVertex pxmin, pxmax;
	RCVertex qxmin, qxmax;
	boolean sameq = false;
	
	oldvertical = vertical;
	vertical = false;
	pxmin=pxmax=qxmin=qxmax=null;
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
			pxmax = pv;
			pxmin = pvnext;
		    }
		else
		    {
			if (pv.x < pvnext.x)
			    {
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
		if (qv.x > qvnext.x)
		    {
			qxmax = qv;
			qxmin = qvnext;
		    }
		else
		    {
			if (qv.x < qvnext.x)
			    {
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
		int pedgeind = pv.n;
		int qedgeind = qv.n;
		Vector qve = (Vector)ctrl.polyveapp.elementAt(qedgeind);
		if (qve.size() > 1)
		    incr = 3;
	    }
	if (i != (appsize - 1))
	    {
		if ((i + incr+ 1) > (appsize - 1))
		    limit = appsize;
		else
		    limit = i + incr + 1;
		if ((i + incr) > (appsize - 1))
		    new_i = appsize - 1;
		else
		    new_i = i + incr;
	    }
	else
	    {
		limit = 1;
		RCPair pr = (RCPair) app.elementAt(0);
		RCVertex v2 = pr.q;
		if (pv.n != v2.n)
		    {
			limit = 2;
			new_i=1;
		    }
		else
		    new_i=0;
		if ((ctrl.pauseatend) && (pass > 0))
		    {
			ctrl.pauseNow();
			nolimit = true;
		    }
		pass++;
	    }
    }
    
    public void repaint()
    {
	ctrl.drawarea.repaint();
    }
}
