import java.util.Vector;

abstract class RCAnimate
{
    int i, new_i, start_i;
    RCS ctrl;
    Thread anim;
    boolean threadrun, finished;
    boolean animon;
    boolean animpause = false;
    boolean paused = false;
    boolean atend = false;
    boolean clockwise = true;
    double angle, incr, endangle;
    float slope, oldslope, cslope = 0;
    boolean vertical, oldvertical, cvertical = false;
    
    Vector solution;
    int nsolution;
    boolean solnfound;

    public synchronized void step(){}
    
    public synchronized void run()
    {
	threadrun = true;
	int ftime = 0;
	int stime = 0;
	try
	    {
		i = start_i;
		stime = ctrl.sleeptime;
		ftime = ctrl.frametime;
		repaint();
		Thread.sleep(stime);
		while (animon)
		    {
			animpause = ctrl.pause;
			while (animpause)
			    {
				paused = true;
				Thread.sleep(ftime);
				animpause = ctrl.pause;
			    }
			if (paused)
			    {
				atend = false;
				Thread.sleep(stime);
			    }
			paused = false;
			stime = ctrl.sleeptime;
			ftime = ctrl.frametime;
			step();
			i = new_i;
			lineAnimInit();
			while (!finished)
			    {
				Thread.sleep(ftime);
				animpause = ctrl.pause;
				lineAnim();
			    }
			cvertical = vertical;
			cslope = slope;
			repaint();
			Thread.sleep(stime + ftime);
		    }
		threadrun = false;
	    }
	catch (InterruptedException e)
	    {
		System.out.println("Exception in RCAnimate");
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
	    if (clockwise)
		angle = -Math.PI/2;
	    else
		angle = Math.PI/2;
	else
	    angle = Math.atan((double)cslope);
	if (vertical)
	    if (clockwise)
		endangle = -Math.PI/2;
	    else
		endangle = Math.PI/2;
	else
	    endangle = Math.atan((double)slope);
	//System.out.println("Starting slope, angle, vertical: " + cslope + " " + angle + " " + cvertical);
	//System.out.println("  Ending slope, angle, vertical: " + slope + " " + endangle + " " + vertical);
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
	if (Math.abs(angle - endangle) < Math.abs(incr))
	    finished = true;
	repaint();
    }
    
    public void repaint(){}
}
