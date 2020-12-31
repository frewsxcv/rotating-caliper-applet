import java.applet.*;
import java.awt.*;
import java.util.Vector;

public class RCPBoxAnim extends RCAnimate implements Runnable
{
    RCPolygon poly;
    Vector veapp;

    Vector smallestboxes;
    int nboxes;
    RCBox smallestbox;
    double perim;

    RCCanvas canv;
    RCVertex p, q, r, s;
    float sp, sq, sr, ss;
    double ap, aq, ar, as;
    boolean vp, vq, vr, vs, start;
    boolean cvp, cvq, cvr, cvs;
    float csp, csq, csr, css;
    RCVertex xmin,xmax,ymin,ymax;
    RCVertex pnext, qnext, rnext, snext, main;
    RCVertex cp, cq, cr, cs;
    int id;

    RCPBoxAnim(RCS ctrl)
    {
	this.ctrl = ctrl;
	this.poly = ctrl.m_Polygon;
	this.veapp = ctrl.polyveapp;
	this.canv = ctrl.drawarea;
    }

    public synchronized void init()
    {
	i = new_i = start_i = 0; //part of RCAnimate but we don't need it

	perim = Double.POSITIVE_INFINITY;
	smallestboxes = new Vector();
	main = null;

	xmin = poly.vertexAt(0);
	ymin = poly.vertexAt(0);
	xmax = poly.vertexAt(0);
	ymax = poly.vertexAt(0);

	int polysize = poly.size();
	int index;
	for (index=1; index < polysize; index++)
	    {
		RCVertex current;
		current = poly.vertexAt(index);
		if (current.x < xmin.x)
		    xmin = current;
		if (current.x > xmax.x)
		    xmax = current;
		if (current.y < ymin.y)
		    ymin = current;
		if (current.y > ymax.y)
		    ymax = current;
	    }
	cp = p = xmin;
	cq = q = xmax;
	cr = r = ymin;
	cs = s = ymax;
	vp = vq = true;
	vr = vs = false;
	sp = sq = sr = ss = 0;
	cvp = cvq = vp;
	cvr = cvs = vr;
	csp = csq = csr = css = sp;
	start = true;
	
	threadrun = false;
	finished = false;

	clockwise = poly.clockwise;
	angle = incr = endangle = 0.0;
	anim = new Thread(this);
	anim.start();
    }
	
    public void draw(Graphics g)
    {
	int index;
	Color boxColor = Color.magenta;
	Color RCColor = new Color(100,100,255);
	
	if (threadrun)
	    {
		g.setColor(RCColor);
		if (!cvp)
		    {
			poly.RCLine(cp, csp, g);
			poly.RCLine(cq, csq, g);
		    }
		else
		    {
			cp.drawVLine(g);
			cq.drawVLine(g);
		    }
		if (!cvr)
		    {
			poly.RCLine(cr, csr, g);
			poly.RCLine(cs, css, g);
		    }
		else
		    {
			cr.drawVLine(g);
			cs.drawVLine(g);
		    }
		
		g.setColor(boxColor);
		nboxes = smallestboxes.size();
		for (index=0; index < nboxes; index++)
		    {
			RCBox boxi = (RCBox) smallestboxes.elementAt(i);
			RCVertex a = boxi.pr;
			RCVertex b = boxi.ps;
			RCVertex c = boxi.qr;
			RCVertex d = boxi.qs;
			a.v2vLine(b, g);
			b.v2vLine(d, g);
			d.v2vLine(c, g);
			c.v2vLine(a, g);
		    }
	    }
    }
	
    public synchronized void step()
    {   
	//System.out.println("\nSTEP\n");
	int polysize = poly.size();

	if (!start) // if we've just started we're not going to determine any boxes
	    {
		//System.out.println("\n\nDetermining box");
		//System.out.println("main = " + main.n);
		int edgeindex = (main.n + polysize - 1)%polysize;
		//System.out.println("edge = " + edgeindex);
		Vector edgeapps = (Vector) veapp.elementAt(edgeindex);
		RCVertex edgep1 = poly.vertexAt(edgeindex);
		RCVertex edgep2 = main;
		//System.out.println("p1 = " + edgep1.n + " p2 = " + edgep2.n);
		RCVertex app0 = (RCVertex) edgeapps.elementAt(0);
		if (edgeapps.size()>1)
		    { // correction for order of elements in edgeapps
			RCVertex app1 = (RCVertex) edgeapps.elementAt(1);
			if ((app1.n == (polysize - 1)) && (app0.n == 0))
			    app0 = app1;
		    }
		//System.out.println("Corresponding app = " + app0.n);
		double side1 = app0.v2lineDist(edgep1,edgep2);
		double side2 = 0;
		if ((main == p) || (main == q))
		    {
			// determine side length for r,s
			if (vr)
			    {
				RCVertex s2 = new RCVertex(s.x, s.y + 100, -1);
				side2 = r.v2lineDist(s,s2);
			    }
			else
			    {
				RCVertex s2 = poly.unitVector(sr, s);
				side2 = r.v2lineDist(s,s2);
			    }
		    }
		else
		    if ((main == r) || (main == s))
			{
			    // determine side length for p,q
			    if (vp)
				{
				    RCVertex q2 = new RCVertex(q.x, q.y + 100, -1);
				    side2 = p.v2lineDist(q,q2);
				}
			    else
				{
				    RCVertex q2 = poly.unitVector(sp, q);
				    side2 = p.v2lineDist(q,q2);
				}
			}
		double newperim = 2 * (side1 + side2);
		
		//System.out.println("side1 = " + side1);
		//System.out.println("side2 = " + side2);
		//System.out.println("newperim = " + newperim);
		
		if (newperim < perim)
		    {
			// We have a new smallest box
			// Remove all previous and calculate coordinates for this one...
			smallestboxes.removeAllElements();
		    }
		if (newperim <= perim)
		    {
			//calculate box coordinates
			RCVertex pr, ps, qr, qs;
			pr = ps = qr = qs = null;
			if (vp) //thus vp & vq & !vr & !vs
			    {
				pr = new RCVertex(p.x, r.y, -1);
				ps = new RCVertex(p.x, s.y, -1);
				qr = new RCVertex(q.x, r.y, -1);
				qs = new RCVertex(q.x, s.y, -1);
			    }
			else //!vp & !vq
			    {
				if (vr) //!vp & !vq & vr & vs
				    {
					pr = new RCVertex(r.x, p.y, -1);
					ps = new RCVertex(s.x, p.y, -1);
					qr = new RCVertex(r.x, q.y, -1);
					qs = new RCVertex(s.x, q.y, -1);
				    }
				else // !vp & !vq & !vr & !vs
				    {
					float x0, y0, m0, x1, y1, m1, xcoord, ycoord;

					// for pr
					x0 = p.x;
					y0 = p.y;
					m0 = sp;
					x1 = r.x;
					y1 = r.y;
					m1 = sr;
					xcoord = (y1 - y0 + m0*x0 - m1*x1)/(m0 - m1);
					ycoord = y0 + m0*(xcoord - x0);
					pr = new RCVertex(xcoord, ycoord, -1);

					// for ps
					x0 = p.x;
					y0 = p.y;
					m0 = sp;
					x1 = s.x;
					y1 = s.y;
					m1 = ss;
					xcoord = (y1 - y0 + m0*x0 - m1*x1)/(m0 - m1);
					ycoord = y0 + m0*(xcoord - x0);
					ps = new RCVertex(xcoord, ycoord, -1);
					
					// for qr
					x0 = q.x;
					y0 = q.y;
					m0 = sq;
					x1 = r.x;
					y1 = r.y;
					m1 = sr;
					xcoord = (y1 - y0 + m0*x0 - m1*x1)/(m0 - m1);
					ycoord = y0 + m0*(xcoord - x0);
					qr = new RCVertex(xcoord, ycoord, -1);

					// for qs
					x0 = q.x;
					y0 = q.y;
					m0 = sq;
					x1 = s.x;
					y1 = s.y;
					m1 = ss;
					xcoord = (y1 - y0 + m0*x0 - m1*x1)/(m0 - m1);
					ycoord = y0 + m0*(xcoord - x0);
					qs = new RCVertex(xcoord, ycoord, -1);
				    }
			    }
			smallestbox = new RCBox(pr, ps, qr, qs, main);
			smallestboxes.addElement(smallestbox);
			perim = newperim;
			//System.out.println("perim = " + perim);
		    }
	    }
	else
	    {
		smallestboxes.removeAllElements();
		perim = Double.POSITIVE_INFINITY;
	    }

	// Now determine next points;
	//System.out.println("Determining next points");
	//System.out.println("Current points p,q,r,s = " + p.n + " " + q.n + " " + r.n + " " + s.n);
	//System.out.println("Slope for p = " + sp + " " + vp);
	//System.out.println("Slope for q = " + sq + " " + vq);
	//System.out.println("Slope for r = " + sr + " " + vr);
	//System.out.println("Slope for s = " + ss + " " + vs);
	ap = poly.vertexLineAngle(p, sp, vp);
	aq = poly.vertexLineAngle(q, sq, vq);
	ar = poly.vertexLineAngle(r, sr, vr);
	as = poly.vertexLineAngle(s, ss, vs);
	//System.out.println("ap = " + ap);
	//System.out.println("aq = " + aq);
	//System.out.println("ar = " + ar);
	//System.out.println("as = " + as);
	boolean ip, iq, ir, is;
	ip = iq = ir = is = false;
	double minangle = Math.min(Math.min(ap,aq), Math.min(ar,as));
	//System.out.println("Min. angle = " + minangle);

	main = null;

	pnext = p;
	qnext = q;
	rnext = r;
	snext = s;

	//System.out.println("Which point is main?");
	if (Math.abs(ap - minangle) < 0.002)
	    {
		//System.out.println("p" + p.n);
		ip = iq = ir = is = false;
		ip = true;
		id = 1;
		pnext = poly.vertexNext(p);
		main = pnext;
	    }
	if (Math.abs(aq - minangle) < 0.002)
	    {
		//System.out.println("q" + q.n);
		ip = iq = ir = is = false;
		iq = true;
		id = 2;
		qnext = poly.vertexNext(q);
		if (main == null)
		    main = qnext;
	    }
	if (Math.abs(ar - minangle) < 0.002)
	    {
		//System.out.println("r" + r.n);
		ip = iq = ir = is = false;
		ir = true;
		id = 3;
		rnext = poly.vertexNext(r);
		if (main == null)
		    main = rnext;
	    }
	if (Math.abs(as - minangle) < 0.002)
	    {
		//System.out.println("s" + s.n);
		ip = iq = ir = is = false;
		is = true;
		id = 4;
		snext = poly.vertexNext(s);
		if (main == null)
		    main = snext;
	    }
	//System.out.println("Found main");
	//System.out.println("main = " + main.n);
	if (ip)
	    {
		//System.out.println("Main is pnext");
		oldvertical = vp;
		oldslope = sp;
		if (p.x == pnext.x)
		    {
			// Vertical case
			vp = vq = true;
			vr = vs = false;
			sp = sq = sr = ss = 0; //p and q get slope 0 just to have a value
		    }
		else
		    {
			//System.out.println("Non-vertical case");
			vp = vq = false;
			if (p.x > pnext.x)
			    sp = (p.y - pnext.y) / (p.x - pnext.x);
			else
			    sp = (pnext.y - p.y) / (pnext.x - p.x);
			sq = sp;
			//System.out.println("New slope for p, q is " + sp);
			if (sp == 0)
			    {
				//System.out.println("Horizontal");
				sr = ss = 0; //just to assign a value
				vr = vs = true;
			    }
			else
			    {
				//System.out.println("Non-horizontal");
				sr = ss = -1/sp;
				vr = vs = false;
			    }
		    }
		vertical = vp;
		slope = sp;
	    }
	if (iq)
	    {
		//System.out.println("Main is qnext");
		oldvertical = vq;
		oldslope = sq;
		if (q.x == qnext.x)
		    {
			// Vertical case
			//System.out.println("Vertical case");
			vp = vq = true;
			vr = vs = false;
			sp = sq = sr = ss = 0; //p and q get slope 0 just to have a value
		    }
		else
		    {
			//System.out.println("Non-vertical case");
			vp = vq = false;
			if (q.x > qnext.x)
			    sq = (q.y - qnext.y) / (q.x - qnext.x);
			else
			    sq = (qnext.y - q.y) / (qnext.x - q.x);
			sp = sq;
			if (sp == 0)
			    {
				sr = ss = 0; //just to assign a value
				vr = vs = true;
			    }
			else
			    {
				sr = ss = -1/sp;
				vr = vs = false;
			    }
		    }
		vertical = vq;
		slope = sq;
	    }
	if (ir)
	    {
		//System.out.println("Main is rnext");
		oldvertical = vr;
		oldslope = sr;
		if (r.x == rnext.x)
		    {
			// Vertical case
			//System.out.println("Vertical case");
			vr = vs = true;
			vp = vq = false;
			sp = sq = sr = ss = 0; //r and s get slope 0 just to have a value
		    }
		else
		    {
			//System.out.println("Non-vertical case");
			vr = vs = false;
			if (r.x > rnext.x)
			    sr = (r.y - rnext.y) / (r.x - rnext.x);
			else
			    sr = (rnext.y - r.y) / (rnext.x - r.x);
			ss = sr;
			if (sr == 0)
			    {
				sp = sq = 0; //just to assign a value
				vp = vq = true;
			    }
			else
			    {
				sp = sq = -1/sr;
				vp = vq = false;
			    }
		    }
		vertical = vr;
		slope = sr;
	    }
	if (is)
	    {
		//System.out.println("Main is snext");
		oldvertical = vs;
		oldslope = ss;
		if (s.x == snext.x)
		    {
			// Vertical case
			//System.out.println("Vertical case");
			vr = vs = true;
			vp = vq = false;
			sp = sq = sr = ss = 0; //r and s get slope 0 just to have a value
		    }
		else
		    {
			//System.out.println("Non-vertical case");
			vr = vs = false;
			if (s.x > snext.x)
			    ss = (s.y - snext.y) / (s.x - snext.x);
			else
			    ss = (snext.y - s.y) / (snext.x - s.x);
			sr = ss;
			if (sr == 0)
			    {
				sp = sq = 0; //just to assign a value
				vp = vq = true;
			    }
			else
			    {
				sp = sq = -1/sr;
				vp = vq = false;
			    }
		    }
		vertical = vs;
		slope = ss;
	    }
	if (poly.clockwise)
	    {
		//System.out.println("Clockwise");
		if ((p == ymax) && (q == ymin) && (r == xmin) && (s == xmax))
		    {
			//System.out.println("Start over");
			start = true;
			p = xmin;
			q = xmax;
			r = ymin;
			s = ymax;
			vp = vq = true;
			vr = vs = false;
			sp = sq = sr = ss = 0;
			if (ip || iq)
			    vertical = true;
			if (ir || is)
			    vertical = false;
			slope = 0;
			if (ctrl.pauseatend)
			    ctrl.pauseNow();
		    }
		else
		    {
			//System.out.println("Continue");
			start = false;
			p = pnext;
			q = qnext;
			r = rnext;
			s = snext;
		    }
	    }
	else
	    {
		//System.out.println("Counter-clockwise");
		if ((p == ymin) && (q == ymax) && (r == xmax) && (s == xmin))
		    {
			//System.out.println("Start over");
			start = true;
			p = xmin;
			q = xmax;
			r = ymin;
			s = ymax;
			vp = vq = true;
			vr = vs = false;
			sp = sq = sr = ss = 0;
			if (ip || iq)
			    vertical = true;
			if (ir || is)
			    vertical = false;
			slope = 0;
			if (ctrl.pauseatend)
			    ctrl.pauseNow();
		    }
		else
		    {
			//System.out.println("Continue");
			start = false;
			p = pnext;
			q = qnext;
			r = rnext;
			s = snext;
		    }
	    }
	//System.out.println("New points p, q, r, s = " + p.n + " " + q.n + " " + r.n + " " + s.n);
	//System.out.println("Slope for p = " + sp + " " + vp);
	//System.out.println("Slope for q = " + sq + " " + vq);
	//System.out.println("Slope for r = " + sr + " " + vr);
	//System.out.println("Slope for s = " + ss + " " + vs);
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
			if (!start)
			    {
				cp = p;
				cq = q;
				cr = r;
				cs = s;
				cvertical = vertical;
				cslope = slope;
				if ((id == 1) || (id == 2))
				    {
					csp = csq = cslope;
					cvp = cvq = cvertical;
					if (cvertical)
					    {
						csr = css = 0;
						cvr = cvs = false;
					    }
					else
					    if (cslope == 0)
						{
						    csr = css = 0;
						    cvr = cvs = true;
						}
					    else
						{
						    csr = css = -1/cslope;
						    cvr = cvs = false;
						}
				    }
				else
				    if ((id == 3) || (id == 4))
					{
					    csr = css = cslope;
					    cvr = cvs = cvertical;
					    if (cvertical)
						{
						    csp = csq = 0;
						    cvp = cvq = false;
						}
					    else
						if (cslope == 0)
						    {
							csp = csq = 0;
							cvp = cvq = true;
						    }
						else
						    {
							csp = csq = -1/cslope;
							cvp = cvq = false;
						    }
					}
				repaint();
			    }
			else
			    {
				cp = p;
				cq = q;
				cr = r;
				cs = s;
				cslope = slope;
				cvertical = vertical;
				cvp = vp;
				cvq = vq;
				cvr = vr;
				cvs = vs;
				repaint();
			    }
			Thread.sleep(stime + ftime);
		    }
		threadrun = false;
	    }
	catch (InterruptedException e)
	    {
		System.out.println("Exception in PBoxAnim");
	    }
    }

    public synchronized void lineAnimInit()
    {
	finished = false;
	cslope = oldslope;
	cvertical = oldvertical;
	incr = -2*(Math.PI / 180.0);
	if (!clockwise)
	    incr = -incr;
	if (cvertical)
	    angle = Math.PI/2;
	else
	    angle = Math.atan((double)cslope);
	if (vertical)
	    endangle = Math.PI/2;
	else
	    endangle = Math.atan((double)slope);
	if (Math.abs(angle - endangle) < Math.abs(incr))
	    finished = true;
	else
	    finished = false;
    }
    
    public synchronized void lineAnim()
    {
	angle = angle + incr;
	if (angle < (-Math.PI/2))
	    angle += Math.PI;
	if (angle > (Math.PI/2))
	    angle -= Math.PI;
	if ((angle == (Math.PI/2)) || (angle == (-Math.PI/2)))
	    {
		cslope = 0;
		cvertical = true;
	    }
	else
	    {
		cslope = (float)Math.tan(angle);
		cvertical = false;
	    }
	if ((id == 1) || (id == 2))
	    {
		csp = csq = cslope;
		cvp = cvq = cvertical;
		if (cvertical)
		    {
			csr = css = 0;
			cvr = cvs = false;
		    }
		else
		    if (cslope == 0)
			{
			    csr = css = 0;
			    cvr = cvs = true;
			}
		    else
			{
			    csr = css = -1/cslope;
			    cvr = cvs = false;
			}
	    }
	else
	    if ((id == 3) || (id == 4))
		{
		    csr = css = cslope;
		    cvr = cvs = cvertical;
		    if (cvertical)
			{
			    csp = csq = 0;
			    cvp = cvq = false;
			}
		    else
			if (cslope == 0)
			    {
				csp = csq = 0;
				cvp = cvq = true;
			    }
			else
			    {
				csp = csq = -1/cslope;
				cvp = cvq = false;
			    }
		}
	if (Math.abs(angle - endangle) < Math.abs(incr))
	    finished = true;
	else
	    if (start)
		{
		    csp = csq = csr = css = 0;
		    cvp = cvq = true;
		    cvr = cvs = false;
		    finished = true;
		}
	repaint();
    }

    public void repaint()
    {
	ctrl.drawarea.repaint();
    }

}



