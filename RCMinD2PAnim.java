import java.applet.*;
import java.awt.*;
import java.util.Vector;

public class RCMinD2PAnim extends RCAnimate implements Runnable {
    RCPolygon poly1;
    RCPolygon poly2;


    RCVEPair minpair;
    RCVEPair newminpair;
    double dist;

    RCCanvas canv;
    int pid;
    RCVertex main;
    RCVertex ymin1, ymax1, ymin2, ymax2;
    RCVertex p1, q2;
    RCVertex p1next, q1next, p2next, q2next;
    RCVertex p1prev, q1prev, p2prev, q2prev;
    RCVertex cp1, cq1, cp2, cq2;

    boolean ip1, iq2;
    int iid;
    double ap1, aq2;
    int polysize1, polysize2;
    boolean start;

    RCMinD2PAnim(RCS ctrl) {
        this.ctrl = ctrl;
        this.poly1 = ctrl.m_Polygon;
        this.poly2 = ctrl.n_Polygon;
        this.canv = ctrl.drawarea;
    }

    public synchronized void init() {
        System.out.println("Initialising");
        polysize1 = poly1.size();
        polysize2 = poly2.size();

        minpair = null;
        newminpair = null;
        dist = Double.POSITIVE_INFINITY;
        main = null;

        ymin1 = poly1.vertexAt(0);
        ymax2 = poly2.vertexAt(0);

        RCVertex current;
        int index;
        for (index = 1; index < polysize1; index++) {
            current = poly1.vertexAt(index);
            if (current.y < ymin1.y)
                ymin1 = current;
        }
        RCVertex last = poly1.vertexAt(polysize1 - 1);
        if (last.y <= ymin1.y)
            ymin1 = last;

        for (index = 1; index < polysize2; index++) {
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
        threadrun = false;
        finished = false;
        cvertical = oldvertical = vertical = false;
        cslope = oldslope = slope = 0;
        angle = incr = endangle = 0.0;
        anim = new Thread(this);
        anim.start();
    }

    public void draw(Graphics g) {
        int index;
        Color minColor = Color.red.darker();
        Color RCColor1 = new Color(100, 100, 255);
        Color RCColor2 = new Color(255, 100, 100);

        if (threadrun) {
            g.setColor(RCColor1);
            if (!cvertical) {
                poly1.RCLine(cp1, cslope, g);
                g.setColor(RCColor2);
                poly2.RCLine(cq2, cslope, g);

            } else {
                cp1.drawVLine(g);
                g.setColor(RCColor2);
                cq2.drawVLine(g);
            }

            //draw min dists
            g.setColor(minColor);
            if (finished)
                minpair = newminpair;
            if (minpair != null) {
                RCVEPair vepair = minpair;
                RCPair pair = vepair.e;
                RCVertex vx = vepair.v;
                boolean par = vepair.prl;
                RCVertex a, b;
                a = pair.p;
                b = pair.q;
                if (vx == null)
                    a.v2vLine(b, g);
                else {
                    RCVertex intx;
                    if (!par) {
                        a.v2vLine(b, g);
                        g.fillOval((int)(vx.x - 4), (int)(vx.yC() - 4), 9, 9);
                        boolean vert;
                        if (a.x == b.x)
                            vert = true;
                        else
                            vert = false;
                        if (vert)
                            intx = new RCVertex(a.x, vx.y, -1);
                        else {
                            float sperp, s, c1, c2, xc, yc;
                            if (a.x > b.x)
                                s = (a.y - b.y) / (a.x - b.x);
                            else
                                s = (b.y - a.y) / (b.x - a.x);
                            if (s != 0)
                                sperp = -1 / s;
                            else
                                sperp = 0;
                            c1 = b.y - s * b.x;
                            c2 = vx.y - sperp * vx.x;
                            xc = (c1 - c2) / (sperp - s);
                            yc = s * xc + c1;
                            intx = new RCVertex(xc, yc, -1);
                        }
                        intx.v2vLine(vx, g);
                    } else {
                        RCVertex vxn = poly2.vertexNext(vx);
                        a.v2vLine(b, g);
                        vx.v2vLine(vxn, g);
                    }
                }
            }
        }
    }

    private synchronized void computeMin() {
        //System.out.println("\n\nDetermining distances");
        //System.out.println("main = " + pid + " " + main.n);
        double newdist = Double.POSITIVE_INFINITY;
        //RCPair min;
        //System.out.println("Increment ID = " + iid);
        RCVertex u, u1, u2;
        //System.out.println("**CM*** p1, p1prev, q2, q2prev = " + p1.n + " " + p1prev.n + " " + q2.n + " " + q2prev.n);
        if (iid == 1) {
            //System.out.println("**CM*** iid=1");
            //check (p1,q2)
            // System.out.println("**CM*** Checking (p1,q2)");
            newdist = p1.v2vDist(q2);
            //System.out.println("**CM***    dist = " +    dist);
            //System.out.println("**CM*** newdist = " + newdist);
            if (newdist <= dist) {
                newminpair = new RCVEPair(p1, q2, null, false);
                dist = newdist;
            }

            //check ([p1,p1prev],q2)
            //System.out.println("**CM*** Checking ([p1,p1prev],q2)");
            if (!vertical)
                if (slope != 0)
                    u = poly2.unitVector(-1 / slope, q2);
                else
                    u = new RCVertex(q2.x, q2.y + 100, -1);
            else
                u = poly2.unitVector(0, q2);
            int l1, l2;
            l1 = u.leftturn(u, q2, p1);
            l2 = u.leftturn(u, q2, p1prev);
            //System.out.println("**CM*** Checking candidate");
            if ((l1 != l2) && (l1 != -1) && (l2 != -1)) {
                //System.out.println("**CM*** Good candidate");
                newdist = q2.v2lineDist(p1prev, p1);
                //System.out.println("**CM***    dist = " +    dist);
                //System.out.println("**CM*** newdist = " + newdist);
                if (newdist <= dist) {
                    RCPair pr = new RCPair(p1prev, p1);
                    newminpair = new RCVEPair(pr, q2, false);
                    dist = newdist;
                }
            }
        }
        if (iid == 2) {
            //System.out.println("**CM*** iid=2");
            //check (p1,q2)
            //System.out.println("**CM*** Checking (p1,q2)");
            newdist = p1.v2vDist(q2);
            //System.out.println("**CM***    dist = " +    dist);
            //System.out.println("**CM*** newdist = " + newdist);
            if (newdist <= dist) {
                newminpair = new RCVEPair(p1, q2, null, false);
                dist = newdist;
            }
            //check (p1,[q2prev,q2])
            //System.out.println("**CM*** Checking (p1,[q2prev,q2])");
            if (!vertical) {
                System.out.println("iid 2 not vertical");
                if (slope != 0) {
                    System.out.println("iid 2 not vertical, slope not 0");
                    u = poly1.unitVector(-1 / slope, p1);
                }
                else {
                    System.out.println("iid 2 not vertical, slope 0");
                    u = new RCVertex(p1.x, p1.y + 100, -1);
                }
            } else {
                System.out.println("iid 2 vertical");
                u = poly1.unitVector(0, p1);
            }
            int l1, l2;
            l1 = u.leftturn(u, p1, q2);
            l2 = u.leftturn(u, p1, q2prev);
            //System.out.println("**CM*** Checking candidate");
            if ((l1 != l2) && (l1 != -1) && (l2 != -1)) {
                //System.out.println("**CM*** Good candidate");
                newdist = p1.v2lineDist(q2prev, q2);
                //System.out.println("**CM***    dist = " +    dist);
                //System.out.println("**CM*** newdist = " + newdist);
                if (newdist <= dist) {
                    RCPair pr = new RCPair(q2prev, q2);
                    newminpair = new RCVEPair(pr, p1, false);
                    dist = newdist;
                }
            }
        }
        if (iid == 3) {
            //check (p1,q2)
            newdist = p1.v2vDist(q2);
            if (newdist <= dist) {
                newminpair = new RCVEPair(p1, q2, null, false);
                dist = newdist;
            }
            //check (p1,q2prev)
            newdist = p1.v2vDist(q2prev);
            if (newdist <= dist) {
                newminpair = new RCVEPair(p1, q2prev, null, false);
                dist = newdist;
            }
            //check (p1prev,q2)
            newdist = p1prev.v2vDist(q2);
            if (newdist <= dist) {
                newminpair = new RCVEPair(p1prev, q2, null, false);
                dist = newdist;
            }
            //check (p1prev,p1)-(q2prev,q2)
            if (!vertical)
                if (slope != 0) {
                    u1 = poly1.unitVector(-1 / slope, p1prev);
                    u2 = poly1.unitVector(-1 / slope, p1);
                } else {
                    u1 = new RCVertex(p1prev.x, p1prev.y + 100, -1);
                    u2 = new RCVertex(p1.x, p1.y + 100, -1);
                }
            else {
                u1 = poly1.unitVector(0, p1prev);
                u2 = poly1.unitVector(0, p1);
            }
            int l11, l12, l21, l22;
            l11 = u1.leftturn(u1, p1prev, q2prev);
            l12 = u1.leftturn(u1, p1prev, q2);
            l21 = u2.leftturn(u2, p1, q2prev);
            l22 = u2.leftturn(u2, p1, q2);
            if (((l11 != l12) && (l11 != -1) && (l12 != -1)) || ((l21 != l22) && (l21 != -1) && (l22 != -2))) {
                newdist = p1.v2lineDist(q2prev, q2);
                if (newdist <= dist) {
                    RCPair pr = new RCPair(p1prev, p1);
                    newminpair = new RCVEPair(pr, q2prev, true);
                    dist = newdist;
                }
            }
        }
        //System.out.println("newdist = " + newdist);
        System.out.println("dist = " + dist);
    }

    public synchronized void step() {
        //System.out.println("\nSTEP\n");
        if (start) { // if we've just started we're not going to determine any distances
            minpair = null;
            dist = Double.POSITIVE_INFINITY;
        }
        nextPoints();
        computeMin();
        //System.out.println("\nDISTANCE\n" + dist);
    }

    private synchronized void nextPoints() {
        // Now determine next points;
        // System.out.println("Determining next points");
        // System.out.println("Current points p1,q2 = " + p1.n + " " + q2.n);
        System.out.println("Initial Slope: " + slope);
        System.out.println("Initial Vertical: " + vertical);
        ap1 = poly1.vertexLineAngle(p1, slope, vertical);
        aq2 = poly2.vertexLineAngle(q2, slope, vertical);
        // System.out.println("ap1 = " + ap1);
        // System.out.println("aq2 = " + aq2);
        System.out.println("p1 after VLA calls: " + p1.x + " " + p1.y);
        System.out.println("p1 idx after VLA calls: " + p1.n);
        System.out.println("Slope after VLA calls: " + slope);
        double minangle = Math.min(ap1, aq2);
        // System.out.println("Min. angle = " + minangle);

        ip1 = iq2 = false;
        main = null;
        iid = 0;
        pid = 0;

        p1next = p1prev = p1;
        q2next = q2prev = q2;
        // System.out.println("p1prev at start " + p1prev.x + " " + p1prev.y);
        // System.out.println("q2prev at start " + q2prev.x + " " + q2prev.y);
        // System.out.println("p1 at start " + p1.x + " " + p1.y);
        // System.out.println("q2 at start " + q2.x + " " + q2.y);
        // System.out.println("p1next at start " + p1next.x + " " + p1next.y);
        // System.out.println("q2next at start " + q2next.x + " " + q2next.y);

        //System.out.println("Which point is main?");
        System.out.println("ap1, min angle: " + ap1 + " " + minangle);
        if (Math.abs(ap1 - minangle) < 0.002) {
            // System.out.println("p1 " + p1.n);
            ip1 = true;
            p1next = poly1.vertexNext(p1);
            main = p1next;
            pid = 1;
            iid += 1;
        }
        System.out.println("aq2, min angle: " + aq2 + " " + minangle);
        if (Math.abs(aq2 - minangle) < 0.002) {
            // System.out.println("q2 " + q2.n);
            iq2 = true;
            q2next = poly2.vertexNext(q2);
            if (main == null) {
                main = q2next;
                pid = 2;
            }
            iid += 2;
        }
        // System.out.println("p1prev after angle lookup " + p1prev.x + " " + p1prev.y);
        // System.out.println("q2prev after angle lookup " + q2prev.x + " " + q2prev.y);
        // System.out.println("p1 after angle lookup " + p1.x + " " + p1.y);
        // System.out.println("q2 after angle lookup " + q2.x + " " + q2.y);
        // System.out.println("p1next after angle lookup " + p1next.x + " " + p1next.y);
        // System.out.println("q2next after angle lookup " + q2next.x + " " + q2next.y);
        System.out.println("Main is: " + main.x + " " + main.y);
        System.out.println("State ip 1: " + ip1);
        System.out.println("State iq 2: " + iq2);
        oldslope = slope;
        oldvertical = vertical;
        if (ip1) {
            //System.out.println("Main is p1next");
            if (p1.x == p1next.x) {
                // Vertical case
                vertical = true;
                slope = 0; //just to have a value
                System.out.println("ip 1 setting vertical true, slope 0");
            } else {
                //System.out.println("Non-vertical case");
                System.out.println("ip 1 not vertical");
                vertical = false;
                if (p1.x > p1next.x)
                    slope = (p1.y - p1next.y) / (p1.x - p1next.x);
                else
                    slope = (p1next.y - p1.y) / (p1next.x - p1.x);
                //System.out.println("New slope is " + slope);
            }
        } else if (iq2) {
            //System.out.println("Main is q2next");
            if (q2.x == q2next.x) {
                // Vertical case
                //System.out.println("Vertical case");
                System.out.println("iq 2 setting vertical true, slope 0");
                vertical = true;
                slope = 0;
            } else {
                //System.out.println("Non-vertical case");
                System.out.println("iq 2 not vertical");
                vertical = false;
                if (q2.x > q2next.x)
                    slope = (q2.y - q2next.y) / (q2.x - q2next.x);
                else
                    slope = (q2next.y - q2.y) / (q2next.x - q2.x);
                //System.out.println("New slope is " + slope);
            }
        }
        System.out.println("Slope is now: " + slope);
        if ((p1 == ymin1) && (q2 == ymax2) && (!start)) {
            System.out.println("Start over");
            start = true;
            p1 = ymin1;
            q2 = ymax2;
            vertical = false;
            slope = 0;
            if (ctrl.pauseatend)
                ctrl.pauseNow();
        } else {
            System.out.println("Continue\n");
            start = false;
            p1 = p1next;
            q2 = q2next;
        }
        // System.out.println("New points p1, q2 = " + p1.n + " " + q2.n);
        //System.out.println("Slope = " + slope);
    }

    public synchronized void run() {
        threadrun = true;
        //int fps = 20;
        //float fsleeptime = (float)(1000/(float)fps);
        //int sleeptime = (int) fsleeptime;
        int ftime = 0;
        int stime = 0;
        try {
            ftime = ctrl.frametime;
            stime = ctrl.sleeptime;
            repaint();
            Thread.sleep(stime);
            while (animon) {
                animpause = ctrl.pause;
                while (animpause) {
                    paused = true;
                    Thread.sleep(ftime);
                    animpause = ctrl.pause;
                }
                if (paused)
                    Thread.sleep(stime);
                paused = false;
                ftime = ctrl.frametime;
                stime = ctrl.sleeptime;
                step();
                lineAnimInit();
                while (!finished) {
                    Thread.sleep(ftime);
                    animpause = ctrl.pause;
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
        } catch (InterruptedException e) {
            System.out.println("Exception in MinD2PAnim");
        }
    }

    public void repaint() {
        ctrl.drawarea.repaint();
    }
}
