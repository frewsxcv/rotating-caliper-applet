public class AnimThread extends Thread
{

    int stime = 1000;
    RCAnimate ctrl;
    boolean animon;
    Thread t;
    
    AnimThread(RCAnimate ctrl, String s)
    {
	super(s);
	this.ctrl = ctrl;
	t = new Thread((Runnable)ctrl);
	ctrl.threadstart = false;
	ctrl.threadrun = true;
	t.start();
    }
    
    public void run()
    {
	try
	  {
	      System.out.println("AnimThread start");
	      stime = ctrl.ctrl.sleeptime;
	      ctrl.i = ctrl.start_i;
	      do
		  {
		      ctrl.threadstart = true;
		      ctrl.step();
		      ctrl.i = ctrl.new_i;
		      while (!ctrl.finished)
			  {
			      Thread.sleep(10);
			  }
		      Thread.sleep(stime);
		  }
	      while (animon);
	      ctrl.threadrun = false;
	  }
	catch (InterruptedException e)
	    {
		System.out.println("Exception in AnimThread");
	    }
    }
}

