import java.applet.*;
import java.awt.*;
import java.util.Vector;
import java.lang.Math;
import java.awt.Graphics;

public class RCPolygon extends Vector {
    RCS controller;
    boolean clockwise;
    int id;
    float minx, miny, maxx, maxy;
    float extra = 40;


    RCPolygon(RCS controller) {
        super();
        this.controller = controller;
        minx = 10000;
        miny = 10000;
        maxx = -10000;
        maxy = -10000;
    }

    public void clearPolygon() {
        minx = 10000;
        miny = 10000;
        maxx = -10000;
        maxy = -10000;
        removeAllElements();
    }

    public String polyString() {
        String s = new String();
        s = "Polygon";
        if (id == 1)
            s = s + " 1: {";
        else
            s = s + " 2: (";
        int nSize = this.size();
        int i;
        for (i = 0; i < nSize; i++) {
            RCVertex v;
            v = this.vertexAt(i);
            s = s + v.toString();
        }
        s = s + "}";
        return s;
    }

    public void polyPrint() {
        System.out.println(polyString());
    }

    public void polyInfo() {
        String sPolyInfo = new String();
        sPolyInfo = polyString();
        if (id == 1)
            controller.lab2.setText(sPolyInfo);
        else
            controller.lab3.setText(sPolyInfo);
    }


    public void polyDraw(Graphics gr) {
        Color drawcolor = new Color(0, 0, 0);
        gr.setColor(drawcolor);
        RCVertex vxFrom;
        RCVertex vxTo;
        int nSize = size();
        for (int i = 0; i < nSize; i++) {
            vxFrom = vertexAt(i);
            if (i == (nSize - 1))
                vxTo = vertexAt(0);
            else
                vxTo = vertexAt(i + 1);
            vxFrom.v2vLine(vxTo, gr);
        }
    }

    public void drawVertices(Graphics g) {
        Color drawcolor = new Color(127, 127, 127);
        g.setColor(drawcolor);
        RCVertex v;
        int nSize = size();
        for (int i = 0; i < nSize; i++) {
            v = vertexAt(i);
            g.fillOval((int)(v.x - 2), (int)(v.yC() - 2), 5, 5);
        }
    }

    public void polyDebug(int num) {
        RCVertex vtemp;
        int i = 0;
        int[] x_array;
        int[] y_array;
        int[] i_array;
        x_array = new int[num];
        y_array = new int[num];
        i_array = new int[num];
        while (i < num) {
            vtemp = vertexAt(i);
            x_array[i] = (int) vtemp.x;
            y_array[i] = (int) vtemp.y;
            i_array[i] = vertexIndex(vtemp);
            i++;
        }
    }


    public void addVertex(RCVertex p) {
        addElement(p);
    }

    public void addCheckVertex(RCVertex newvx) {
        boolean added = true;
        int nVSize = newvx.n;
        addVertex(newvx);
        if (nVSize >= 3) {
            int polydir = Lt((nVSize - 3), (nVSize - 2), (nVSize - 1));
            int dir0 = Lt((nVSize - 2), (nVSize - 1), nVSize);
            int dir1 = Lt((nVSize - 1), nVSize, 0);
            int dir2 = Lt(nVSize, 0, 1);
            if ((dir0 == -1) || (dir1 == -1) || (dir2 == -1)) {
                removeElementAt(nVSize);
                added = false;
            } else if ((polydir != dir0) || (polydir != dir1) | (polydir != dir2)) {
                removeElementAt(nVSize);
                added = false;
            }
        }
        if (added) {
            if (newvx.x < minx)
                minx = newvx.x;
            if (newvx.y < miny)
                miny = newvx.y;
            if (newvx.x > maxx)
                maxx = newvx.x;
            if (newvx.y > maxy)
                maxy = newvx.y;
        }
        //System.out.println("minx,miny,maxx,maxy = " + minx + " " + miny + " " + maxx + " " + maxy);
    }


    public int vertexIndex(RCVertex p) {
        return p.n;
    }

    public RCVertex vertexAt(int i) {
        return (RCVertex)elementAt(i);
    }

    public RCVertex vertexNext(RCVertex p) {
        int nSize = this.size();
        int newind = (p.n + 1) % nSize;
        return this.vertexAt(newind);
    }

    public RCVertex vertexPrev(RCVertex p) {
        int nSize = this.size();
        int newind = (p.n + nSize - 1) % nSize;
        return this.vertexAt(newind);
    }

    public int Lt(int i, int j, int k) {
        RCVertex temp = new RCVertex(0, 0, 0);
        int ltres = temp.leftturn(this.vertexAt(i), this.vertexAt(j), this.vertexAt(k));
        return ltres;
    }


    public boolean containsPoly(RCPolygon poly) {
        int size = this.size();
        int polysize = poly.size();
        int i, j;
        int in;
        if (clockwise)
            in = 0;
        else
            in = 1;
        boolean contains = true;
        for (i = 0; i < polysize; i++) {
            RCVertex q = poly.vertexAt(i);
            for (j = 0; j < size; j++) {
                RCVertex p = this.vertexAt(j);
                RCVertex pnext = this.vertexNext(p);
                if (p.leftturn(p, pnext, q) != in)
                    return false;
            }
        }
        return true;
    }

    private boolean nestedPoly(RCPolygon poly) {
        RCBridgeAnim ba = new RCBridgeAnim(controller);
        ba.init1();
        ba.computing = true;
        do
            ba.step();
        while (!ba.start);
        Vector v = ba.newbridges;
        if (v.size() == 0)
            return true;
        else
            return false;
    }

    private boolean interPoly(RCPolygon poly) {
        RCCSLineAnim csa = new RCCSLineAnim(controller);
        csa.init1();
        csa.computing = true;
        do
            csa.step();
        while (!csa.start);
        Vector v = csa.newcspairs;
        if (v.size() == 0)
            return true;
        else
            return false;
    }

    public boolean intersectsPoly(RCPolygon poly) {
        return (interPoly(poly) || nestedPoly(poly));
    }

    public Vector antipodalPairs(RCS controller) {
        try {
            controller.order.removeAllElements();
            //System.out.println("\nGenerating antipodal pairs for:");
            //System.out.println(this.polyString());

            RCVertex p = this.vertexAt((this.size() - 1)); // step 1

            //System.out.println("p = Vertex(n) = " + p.toString());

            RCVertex q = this.vertexNext(p); // step 2

            //System.out.println("q = farthest from p = " + q.toString());

            Vector app = new Vector();

            double area1;
            double area2;

            area1 = Math.abs(p.area(p, this.vertexNext(p), this.vertexNext(q)));
            area2 = Math.abs(p.area(p, this.vertexNext(p), q));

            while (area1 > area2) { // step 3
                q = this.vertexNext(q); // step 3

                //System.out.println("q = farthest from pn-p0 = " + q.toString());

                area1 = Math.abs(p.area(p, this.vertexNext(p), this.vertexNext(q)));
                area2 = Math.abs(p.area(p, this.vertexNext(p), q));
            }

            RCVertex q0 = q; // step 4

            //System.out.println("q0 = q");

            RCVertex p0 = this.vertexAt(0);

            //System.out.println("p0 = " + p0.toString());

            controller.order.addElement(q);

            while (q.n != p0.n) { // step 5
                p = vertexNext(p); // step 6

                controller.order.addElement(p);

                //System.out.println("p is now " + p.toString());

                RCPair newapp = new RCPair(p, q); // step 7
                app.addElement(newapp); // step 7

                //System.out.println("New a.p.p.: " + newapp.toString());

                while (Math.abs(p.area(p, vertexNext(p), vertexNext(q))) > Math.abs(p.area(p, vertexNext(p), q))) { // step 8
                    q = vertexNext(q); // step 9
                    controller.order.addElement(q);

                    //System.out.println("q is now " + q.toString());
                    if ((p.n != q0.n) || (q.n != p0.n)) { // step 10
                        newapp = new RCPair(p, q); // step 10
                        //System.out.println("New a.p.p.: " + newapp.toString());
                        app.addElement(newapp); // step 10
                    } else {
                        //controller.order.removeElementAt(controller.order.size() - 1);
                        return app; // Missing part in algorithm
                    }
                }
                if (p.area(p, vertexNext(p), vertexNext(q)) == p.area(p, vertexNext(p), q)) { // step 11

                    //System.out.println("Parallel case");

                    if ((p.n != q0.n) || (q != vertexAt((this.size() - 1)))) { // step 12
                        newapp = new RCPair(p, vertexNext(q)); // step 12
                        app.addElement(newapp); // step 12

                        //System.out.println("New a.p.p.: " + newapp.toString());
                    } else {
                        // this whole part has been added on
                        newapp = new RCPair(vertexNext(p), q);
                        app.addElement(newapp);

                        //System.out.println("NEW New a.p.p.: " + newapp.toString());
                        //controller.order.removeElementAt(controller.order.size() - 1);
                        return app;
                    }
                }
            }
            //controller.order.removeElementAt(controller.order.size() - 1);
            return app;
        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }

    public Vector vertexEdgeApps(Vector app) {
        try {
            Vector veApps = new Vector();
            int psize = this.size();
            int i, j;

            //System.out.println("\nPolygon size: " + psize);
            for (i = 0 ; i < psize ; i++) {
                Vector edge = new Vector();
                //RCVertex ev1 = this.vertexAt(i);
                //RCVertex ev2 = this.vertexNext(ev1);
                //RCPair ev = new RCPair(ev1, ev2);
                //edge.addElement(ev);
                veApps.addElement(edge);
            }

            int appsize = app.size();
            //System.out.println("\n\n----------------------------------------------\nAnti-podal pairs: " + appsize);
            RCPair pair = (RCPair) app.elementAt(0);
            RCVertex p0 = pair.p;
            RCVertex q0 = pair.q;
            RCVertex p = p0;
            RCVertex qi = q0;
            RCVertex qf = q0;
            RCVertex qprev = q0;
            int appind = 0;
            Vector oneedge;

            //System.out.println("\nInitial pair: " + p0.n + "," + q0.n);
            while (p.n != q0.n) {
                //System.out.println("\nOuter loop begin\nappind = " + appind + " p = " + p.n + " qi = " + qi.n + " qf = " + qf.n);
                RCVertex vtest = this.vertexNext(qi);
                if (qprev.n == vtest.n) {
                    //System.out.println("Parallel case");
                    RCVertex prevp = this.vertexPrev(p);
                    oneedge = (Vector)veApps.elementAt(prevp.n);
                    oneedge.removeAllElements();
                    oneedge.addElement(qi);
                    oneedge.addElement(qprev);
                    //System.out.println("  Edge "+prevp.n+": vertices "+qi.n+" "+qprev.n);
                }
                while (p.n == pair.p.n) {
                    appind++;
                    pair = (RCPair)app.elementAt(appind);
                }
                RCPair temp;
                if (appind > 0)
                    temp = (RCPair)app.elementAt(appind - 1);
                else {
                    //System.out.println("problem");
                    temp = (RCPair)app.elementAt(appsize - 1);
                }
                qf = temp.q;
                //System.out.println("Inner loop end\nappind = " + appind + " p = " + p.n + " qi = " + qi.n + " qf = " + qf.n);
                for (i = qi.n; i < qf.n; i++) {
                    oneedge = (Vector)veApps.elementAt(i);
                    oneedge.addElement(p);
                    //System.out.println("  Edge "+i+": vertex "+p.n);
                }
                oneedge = (Vector)veApps.elementAt(p.n);
                oneedge.addElement(this.elementAt(qf.n));
                //System.out.println("  Edge "+p.n+": vertex "+qf.n);
                p = pair.p;
                qprev = qf;
                qf = qi = pair.q;
                //System.out.println("Outer loop end\nappind = " + appind + " p = " + p.n + " qi = " + qi.n + " qf = " + qf.n);
            }

            //Deal with q0

            //System.out.println("\nDealing with q0");
            //System.out.println("  appind="+appind+" p="+p.n+" qi="+qi.n+" qf="+qf.n+" qprev="+qprev.n);
            RCVertex vtest = this.vertexNext(qi);
            if (qprev.n == vtest.n) {
                //System.out.println("Parallel case");
                RCVertex prevp = this.vertexPrev(q0);
                oneedge = (Vector)veApps.elementAt(prevp.n);
                oneedge.removeAllElements();
                oneedge.addElement(qi);
                oneedge.addElement(qprev);
                //System.out.println("  Edge "+prevp.n+": vertices "+qi.n+" "+qprev.n);
            }
            while (p.n == pair.p.n) {
                appind++;
                if (appind == appsize) {
                    //System.out.println("  Back to first app");
                    appind = 0;
                }
                pair = (RCPair)app.elementAt(appind);
            }
            int tempind;
            if (appind == 0)
                tempind = appsize - 1;
            else
                tempind = appind - 1;
            RCPair temp = (RCPair)app.elementAt(tempind);
            qf = temp.q;
            //System.out.println("  appind="+appind+" p="+p.n+" qi="+qi.n+" qf="+qf.n+" qprev="+qprev.n);
            for (i = qi.n; i < qf.n; i++) {
                oneedge = (Vector)veApps.elementAt(i);
                oneedge.addElement(q0);
                //System.out.println("  Edge "+i+": vertex "+q0.n);
            }
            oneedge = (Vector)veApps.elementAt(psize - 1);
            oneedge.addElement(this.elementAt(q0.n));
            //System.out.println("  Edge "+(psize - 1)+": vertex "+q0.n);

            //Deal with parallel pnp0 and q0 next(q0) edges
            //System.out.println("\nDeal with parallel pn and q0 edges");
            if (app.size() == (appind + 1)) {
                oneedge = (Vector)veApps.elementAt(q0.n);
                oneedge.addElement(this.vertexAt(psize - 1));
                RCPair last = (RCPair) app.elementAt(appind);
                RCVertex test = this.vertexPrev(last.p);
                if (test.n == q0.n) {
                    //System.out.println("Parallel case");
                    oneedge = (Vector)veApps.elementAt(psize - 1);
                    oneedge.removeAllElements();
                    oneedge.addElement(q0);
                    RCVertex q0next = this.vertexNext(q0);
                    oneedge.addElement(q0next);
                    //System.out.println("  Edge "+(psize-1)+": vertices "+q0.n+" "+q0next.n);
                }
            }


            //debug
            //System.out.println("Vertex-Edge Pairs\n");
            for (i = 0; i < psize ; i++) {
                Vector edge = (Vector) veApps.elementAt(i);
                int noapps = edge.size();
                //System.out.println("Edge " + i + " no. apps: " + noapps);
                for (j = 0; j < noapps ; j++) {
                    RCVertex v = (RCVertex) edge.elementAt(j);
                    int vind = v.n;
                    //System.out.println("   Vertex " + vind);
                }
            }
            return veApps;
        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }


    public void appDraw(Vector app, Graphics g) {
        RCPair pair;
        RCVertex pv;
        RCVertex qv;
        int nPairs = app.size();
        Color appColor = new Color(255, 100, 100);
        g.setColor(appColor);
        for (int i = 0; i < nPairs; i++) {
            pair = (RCPair)app.elementAt(i);
            pv = pair.p;
            qv = pair.q;
            pv.v2vLine(qv, g);
        }
    }

    public void RCLine(RCVertex p, float m, Graphics g) {
        int x1, y1, x2, y2;
        float ymin, ymax = 0;
        float xmin = 0;
        float xmax = p.WW;
        float b = p.y - m * p.x;
        ymin = m * xmin + b;
        ymax = m * xmax + b;
        if ((ymin < 0) || (ymin > p.WH) || (ymax < 0) || (ymax > p.WH)) {
            ymin = 0;
            ymax = p.WH;
            xmin = (ymin - b) / m;
            xmax = (ymax - b) / m;
        }

        x1 = (int)xmin;
        y1 = (int)(p.WH - ymin);
        x2 = (int)xmax;
        y2 = (int)(p.WH - ymax);
        g.drawLine(x1, y1, x2, y2);
        //System.out.println("Line: ("+xmin+","+(p.WH-ymin)+")-("+xmax+","+(p.WH-ymax)+")");
        //System.out.println("(" + x1 + "," + y1 + ")-(" + x2 + "," + y2 + ")");
    }

    public void RCCutLine(RCVertex p, float m, Graphics g) {
        //System.out.println("\nminx,miny,maxx,maxy = " + minx + " " + miny + " " + maxx + " " + maxy);
        //System.out.println("slope = " + m);
        int x1, y1, x2, y2;
        float ymin, ymax = 0;
        float xmin = minx - extra;
        float xmax = maxx + extra;
        float b = p.y - m * p.x;
        //System.out.println("b = " + b);
        ymin = m * xmin + b;
        ymax = m * xmax + b;
        //System.out.println("xmin,ymin,xmax,ymax = " + xmin + " " + ymin + " " + xmax + " " + ymax);

        if ((ymin < (miny - extra)) || (ymin > (maxy + extra))) {
            if (ymin < ymax)
                ymin = miny - extra;
            else
                ymin = maxy + extra;
            xmin = (ymin - b) / m;
        }
        if ((ymax < (miny - extra)) || (ymax > (maxy + extra))) {
            if (ymax > ymin)
                ymax = maxy + extra;
            else
                ymax = miny - extra;
            xmax = (ymax - b) / m;
        }

        x1 = (int)xmin;
        y1 = (int)(p.WH - ymin);
        x2 = (int)xmax;
        y2 = (int)(p.WH - ymax);
        g.drawLine(x1, y1, x2, y2);
        //System.out.println("Line: ("+xmin+","+(p.WH-ymin)+")-("+xmax+","+(p.WH-ymax)+")");
        //System.out.println("(" + x1 + "," + y1 + ")-(" + x2 + "," + y2 + ")");
    }

    public void RCCutVline(RCVertex p, Graphics g) {
        int x, y1, y2;
        x = (int)p.x;
        y1 = (int)(p.WH - (miny - extra));
        y2 = (int)(p.WH - (maxy + extra));
        g.drawLine(x, y1, x, y2);
    }

    private void RCVline(RCVertex p, Graphics g) {
        g.drawLine((int)p.x, 0, (int)p.x, (int)p.WH);
    }


    public int appAnim(int count, Vector app, Graphics g) {
        int i = count;
        RCPair pair, nextpair;
        RCVertex pv, qv, pvnext, qvnext;
        RCVertex pxmin, pxmax;
        RCVertex qxmin, qxmax;
        float slope;
        boolean vertical = false;
        int nPairs = app.size();
        Color appColor = new Color(255, 100, 255);
        Color RCColor = new Color(100, 100, 255);
        if (i < nPairs) {
            vertical = false;
            pxmin = pxmax = qxmin = qxmax = pv = qv = pvnext = qvnext = null;
            pair = (RCPair)app.elementAt(i);
            if (i == (nPairs - 1)) {
                nextpair = (RCPair)app.elementAt(0);
                pv = pair.p;
                qv = pair.q;
                pvnext = nextpair.q;
                qvnext = nextpair.p;
            } else {
                nextpair = (RCPair)app.elementAt(i + 1);
                pv = pair.p;
                qv = pair.q;
                pvnext = nextpair.p;
                qvnext = nextpair.q;
            }
            if (qv == qvnext) {
                if (pv.x > pvnext.x) {
                    pxmax = pv;
                    pxmin = pvnext;
                } else {
                    if (pv.x < pvnext.x) {
                        pxmin = pv;
                        pxmax = pvnext;
                    } else {
                        vertical = true;
                    }
                }
                if (!vertical) {
                    slope = ((float)(pxmax.y - pxmin.y)) / ((float)(pxmax.x - pxmin.x));
                    g.setColor(RCColor);
                    RCLine(pv, slope, g);
                    RCLine(qv, slope, g);
                } else {
                    g.setColor(RCColor);
                    pv.drawVLine(g);
                    qv.drawVLine(g);
                }
            } else {
                if (qv.x > qvnext.x) {
                    qxmax = qv;
                    qxmin = qvnext;
                } else {
                    if (qv.x < qvnext.x) {
                        qxmin = qv;
                        qxmax = qvnext;
                    } else {
                        vertical = true;
                    }
                }
                if (!vertical) {
                    slope = ((float)(qxmax.y - qxmin.y)) / ((float)(qxmax.x - qxmin.x));
                    g.setColor(RCColor);
                    RCLine(pv, slope, g);
                    RCLine(qv, slope, g);
                } else {
                    g.setColor(RCColor);
                    pv.drawVLine(g);
                    qv.drawVLine(g);
                }
            }
        }
        if (i == (nPairs - 1)) {
            i = 0;
        } else
            i++;
        return i;
    }

    public Vector polyDiameter(Vector app) {
        Vector distvec = new Vector();
        RCPair pair;
        RCVertex pv, qv;
        int nPairs = app.size();
        double dist = 0.0;
        double newdist = 0.0;
        for (int i = 0; i < nPairs; i++) {
            pair = (RCPair)app.elementAt(i);
            pv = pair.p;
            qv = pair.q;
            newdist = pv.v2vDist(qv);
            if (dist < newdist) {
                distvec.removeAllElements();
                distvec.addElement(pair);
                dist = newdist;
            } else {
                if (dist == newdist ) {
                    distvec.addElement(pair);
                }
            }
        }
        return distvec;
    }

    public Vector polyWidth(Vector veapp) {
        try {
            Vector widthvec = new Vector();
            Vector oneedge;
            int veappsize = veapp.size();
            int i;
            double width = 9999;
            double newwidth = 9999;
            boolean par = false;

            for (i = 0; i < veappsize; i++) {
                oneedge = (Vector) veapp.elementAt(i);
                if (oneedge.size() == 1)
                    par = false;
                else if (oneedge.size() == 2)
                    par = true;
                else {
                    //System.out.println("Problem in Vertex-Edge Apps vector");
                    return null;
                }
                RCVertex ep = this.vertexAt(i);
                RCVertex eq = this.vertexNext(ep);
                String s = "i=" + i + " edge=(" + ep.n + "," + eq.n + ")";
                RCVertex er = (RCVertex) oneedge.elementAt(0);
                if (par) {
                    RCVertex es = (RCVertex) oneedge.elementAt(1);
                    if ((es.n == this.size() - 1) && (er.n == 0)) {
                        RCVertex temp = er;
                        er = es;
                        es = temp;
                        temp = null;
                    }
                    s = s + " edge=(" + er.n + "," + es.n + ")";
                } else
                    s = s + " vertex=" + er.n;
                //System.out.println(s);

                newwidth = Math.abs(ep.area(ep, eq, er) / ep.v2vDist(eq));
                //System.out.println("New width = " + newwidth);
                if (newwidth < width) {
                    //System.out.println("New minimum");
                    widthvec.removeAllElements();
                    RCVEPair vep = new RCVEPair(ep, eq, er, par);
                    widthvec.addElement(vep);
                    width = newwidth;
                } else if (newwidth == width) {
                    //System.out.println("Equal minimum");
                    RCVEPair vep = new RCVEPair(ep, eq, er, par);
                    widthvec.addElement(vep);
                }
            }
            //System.out.println("Finished calculating... "+widthvec.size()+" widths");
            return widthvec;
        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }

    public void drawDiam(Vector diamvec, Graphics g) {
        Color appColor = new Color(100, 255, 100);
        g.setColor(appColor);
        int nDiams = diamvec.size();
        RCPair pair;
        RCVertex pv, qv;
        for (int i = 0; i < nDiams; i++) {
            pair = (RCPair)diamvec.elementAt(i);
            pv = pair.p;
            qv = pair.q;
            pv.v2vLine(qv, g);
        }
    }


    public void drawWidth(Vector widthvec, Graphics g) {
        try {
            Color widthColor = Color.orange;
            g.setColor(widthColor);
            int nWidths = widthvec.size();
            //System.out.println("No. of widths: " + nWidths);
            RCVEPair vepair;
            for (int i = 0; i < nWidths; i++) {
                vepair = (RCVEPair)widthvec.elementAt(i);
                RCPair edge = vepair.e;
                RCVertex p = edge.p;
                RCVertex q = edge.q;
                RCVertex r = vepair.v;
                boolean par = vepair.prl;
                //System.out.println("i="+i+" edge=("+p.n+","+q.n+") vx="+r.n+" par="+par);
                if (!par) {
                    p.v2vLine(q, g);
                    g.fillOval((int)(r.x - 4), (int)(r.yC() - 4), 9, 9);
                } else {
                    RCVertex s = this.vertexNext(r);
                    p.v2vLine(q, g);
                    r.v2vLine(s, g);
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void drawCircle(RCS ctrl, Graphics g) {
        Color circleColor = Color.green;
        g.setColor(circleColor);
        Vector diam = ctrl.polydiam;
        int diamsize = diam.size();
        if (diamsize > 0) {
            RCPair diampair = (RCPair) diam.elementAt(0);
            RCVertex dp = diampair.p;
            RCVertex dq = diampair.q;
            //System.out.println("dp = "+dp.x+" "+dp.y+"   dq = "+dq.x+" "+dq.y);
            double dm = dp.v2vDist(dq);
            double rad = dm / 2.0;
            double centerx = ((double)(dp.x + dq.x)) / 2.0;
            double centery = ((double)(dp.y + dq.y)) / 2.0;
            double ulx = centerx - rad;
            double uly = centery + rad;

            int ulxc = (int)ulx;
            int ulyc = (int)(dp.WH - uly);
            int diamc = (int)dm;

            g.drawOval(ulxc, ulyc, diamc, diamc);
        }
    }


    public RCVertex unitVector(float slope, RCVertex p) {
        float tansq = slope * slope;
        float cossq = 1 / (1 + tansq);
        float sinsq = 1 - cossq;
        float cos = 0;
        float sin = 0;
        int polysize = this.size();
        RCVertex pnext = vertexAt((p.n + 1) % polysize);
        RCVertex pprev = vertexAt((p.n + polysize - 1) % polysize);
        if (slope != 0) {
            cos = (float)Math.sqrt(cossq);
            sin = (float)Math.sqrt(sinsq);
            if (pnext.x > p.x) {
                if (pprev.x > p.x) {
                    if ((pprev.y >= p.y) && (pnext.y >= p.y)) {
                        if (slope > 0) {
                            float slprev, slnext;
                            slprev = (pprev.y - p.y) / (pprev.x - p.x);
                            slnext = (pnext.y - p.y) / (pnext.x - p.x);
                            if ((clockwise && (slope <= slprev)) || ((!clockwise) && (slope >= slprev))) {
                                cos = -cos;
                                sin = -sin;
                            }
                        } else { //slope < 0
                            if (clockwise) {
                                cos = -cos;
                            } else {
                                sin = -sin;
                            }
                        }
                    } else {
                        if ((pprev.y <= p.y) && (pnext.y <= p.y))
                            if (slope > 0) {
                                if (!clockwise) {
                                    cos = -cos;
                                    sin = -sin;
                                }
                            } else {
                                float slprev, slnext;
                                slprev = (pprev.y - p.y) / (pprev.x - p.x);
                                slnext = (pnext.y - p.y) / (pnext.x - p.x);
                                if (clockwise) {
                                    if (slope <= slprev) {
                                        cos = -cos;
                                    } else {
                                        sin = -sin;
                                    }
                                } else {
                                    if (slope <= slnext) {
                                        sin = -sin;
                                    } else {
                                        cos = -cos;
                                    }
                                }
                            }
                        else {
                            if (slope > 0) {
                                if (!clockwise) {
                                    cos = -cos;
                                    sin = -sin;
                                }
                            } else {
                                if (clockwise) {
                                    cos = -cos;
                                } else {
                                    sin = -sin;
                                }
                            }
                        }
                    }
                } else { //pprev.x <= p.x
                    if (slope < 0)
                        sin = -sin;
                }
            } else { //pnext.x <= p.x
                if (pnext.x < p.x) {
                    if (pprev.x < p.x) {
                        if ((pprev.y >= p.y) && (pnext.y >= p.y)) {
                            if (slope > 0) {
                                if (clockwise) {
                                    cos = -cos;
                                    sin = -sin;
                                }
                            } else {
                                float slprev, slnext;
                                slprev = (p.y - pprev.y) / (p.x - pprev.x);
                                slnext = (p.y - pnext.y) / (p.x - pnext.x);
                                if (clockwise) {
                                    if (slope <= slprev) {
                                        sin = -sin;
                                    } else {
                                        cos = -cos;
                                    }
                                } else {
                                    if (slope <= slnext) {
                                        cos = -cos;
                                    } else {
                                        sin = -sin;
                                    }
                                }
                            }
                        } else {
                            if ((pprev.y <= p.y) && (pnext.y <= p.y)) {
                                if (slope > 0) {
                                    float slprev, slnext;
                                    slprev = (p.y - pprev.y) / (p.x - pprev.x);
                                    slnext = (p.y - pnext.y) / (p.x - pnext.x);
                                    if ((clockwise && (slope >= slnext)) || ((!clockwise) && (slope >= slnext))) {
                                        cos = -cos;
                                        sin = -sin;
                                    }
                                } else {
                                    if (clockwise) {
                                        sin = -sin;
                                    } else {
                                        cos = -cos;
                                    }
                                }
                            } else {
                                if (slope > 0) {
                                    if (clockwise) {
                                        cos = -cos;
                                        sin = -sin;
                                    }
                                } else {
                                    if (clockwise) {
                                        sin = -sin;
                                    } else {
                                        cos = -cos;
                                    }
                                }
                            }
                        }
                    } else { //pprev.x >= p.x
                        cos = -cos;
                        if (slope > 0) {
                            sin = -sin;
                        }
                    }
                } else { //pnext.x = p.x
                    if (pprev.x > p.x) {
                        cos = -cos;
                        if (slope > 0) {
                            sin = -sin;
                        }
                    } else { //pprev.x < p.x (can't be equal)
                        if (slope < 0) {
                            sin = -sin;
                        }
                    }
                }
            }
        } else {
            //slope is 0
            sin = 0;
            if (pnext.x > p.x) {
                cos = 1;
            }
            else if (pnext.x < p.x) {
                cos = -1;
            }
            else if (pnext.x == p.x) {
                if (pprev.x < p.x) {
                    cos = 1;
                }
                else {
                    //pprev > p.x (can't be equal)
                    cos = -1;
                }
            }
        }
        RCVertex u = new RCVertex(p.x + 100 * cos, p.y + 100 * sin, p.n);
        System.out.println("Unit Vector, slope: " + u.x + " " + u.y + " " + slope);
        System.out.println("Unit Vector pprev, p, pnext: " + pprev.x + " " + pprev.y + " " + p.x + " " + p.y + " " + pnext.x + " " + pnext.y + " ");
        return u;
    }

    public RCVertex unitPVector(RCVertex p, RCVertex u) {
        boolean vertical;
        float slope, sperp;
        slope = 0;
        if (p.x == u.x)
            vertical = true;
        else
            vertical = false;
        if (!vertical)
            if (p.y == u.y)
                slope = 0;
            else if (u.x > p.x)
                slope = (u.y - p.y) / (u.x - p.x);
            else
                slope = (p.y - u.y) / (p.x - u.x);
        float upx, upy;
        if (vertical) {
            upy = p.y;
            if (u.y > p.y)
                upx = p.x + 100;
            else
                upx = p.x - 100;
            RCVertex perpunit = new RCVertex(upx, upy, -1);
            return perpunit;
        } else { // not vertical
            if (slope == 0) {
                upx = p.x;
                if (u.x > p.x)
                    upy = p.y - 100;
                else
                    upy = p.y + 100;
                RCVertex perpunit = new RCVertex(upx, upy, -1);
                return perpunit;
            } else { // not special case
                sperp = -1 / slope;
                float tansq = sperp * sperp;
                float cossq = 1 / (1 + tansq);
                float sinsq = 1 - cossq;
                float cos = (float)Math.sqrt(cossq);
                float sin = (float)Math.sqrt(sinsq);
                if (u.x > p.x) {
                    sin = -sin;
                    if (slope < 0)
                        cos = -cos;
                } else { //u.x < p.x
                    if (slope > 0)
                        cos = -cos;
                }
                RCVertex perpunit = new RCVertex ((p.x + 100 * cos), (p.y + 100 * sin), -1);
                System.out.println("Perp Unit Vector: " + perpunit.x + " " + perpunit.y);
                return perpunit;
            }
        }
    }



    public double vertexLineAngle(RCVertex p, float m, boolean vert) {
        //System.out.println("***** vertexLineAngle");
        int polysize = this.size();
        // NEXT POINT INDEX
        RCVertex pnext = vertexAt((p.n + 1) % polysize);
        RCVertex pprev = vertexAt((p.n + polysize - 1) % polysize);
        RCVertex punit;
        float slope = 0;
        boolean vertical = vert;
        boolean clockwise = true;
        if (!vertical) {
            slope = m;
        }
        else
            vertical = true;
        //System.out.println("***** pprev, p, pnext = " + pprev.n + " " + p.n + " " + pnext.n);
        //System.out.println("***** slope, vert = " + slope + " " + vertical);
        if (!vertical) {
            System.out.println("VLA: calculating min unit vector");
            punit = unitVector(slope, p);
        }
        else {
            if (this.clockwise)
                if (p.x > pprev.x)
                    punit = new RCVertex(p.x, p.y - 100, p.n);
                else if (p.x < pprev.x)
                    punit = new RCVertex(p.x, p.y + 100, p.n);
                else if (p.x == pprev.x) {
                    if (p.y > pprev.y)
                        punit = new RCVertex(p.x, p.y + 100, p.n);
                    else if (p.y < pprev.y)
                        punit = new RCVertex(p.x, p.y - 100, p.n);
                    else
                        punit = null;
                } else
                    punit = null;
            else if (p.x > pprev.x)
                punit = new RCVertex(p.x, p.y + 100, p.n);
            else if (p.x < pprev.x)
                punit = new RCVertex(p.x, p.y - 100, p.n);
            else if (p.x == pprev.x) {
                if (p.y > pprev.y)
                    punit = new RCVertex(p.x, p.y + 100, p.n);
                else if (p.y < pprev.y)
                    punit = new RCVertex(p.x, p.y - 100, p.n);
                else
                    punit = null;
            } else
                punit = null;
        }
        if (punit != null) {
            // System.out.println("***** punit = (" + punit.x + "," + punit.y + ")");
        } else {
            // System.out.println("***** punit = null");
        }

        double triarea = p.area(p, punit, pnext);
        double edgelen = p.v2vDist(pnext);
        double sine = triarea / (0.5 * 100.0 * edgelen);
        if (sine < -1.0)
            sine = -1.0;
        if (sine > 1.0)
            sine = 1.0;
        double angle;

        //System.out.println("***** Clockwise? " + clockwise);
        //System.out.println("***** Area = " + triarea);
        //System.out.println("***** Edge length = " + edgelen);
        //System.out.println("***** Sine = " + sine);

        RCVertex perpunit = unitPVector(p, punit);
        boolean obtuse = false;
        int left = p.leftturn(p, perpunit, pnext);
        if (clockwise) {
            if (left == 0)
                obtuse = true;
            if (left == -1)
                angle = Math.PI / 2;
            else if (!obtuse)
                angle = Math.asin(-sine);
            else
                angle = Math.PI - Math.asin(-sine);
        } else {
            if (left == 0)
                obtuse = true;
            if (left == -1)
                angle = Math.PI / 2;
            else if (!obtuse)
                angle = Math.asin(sine);
            else
                angle = Math.PI - Math.asin(sine);
        }
        //System.out.println("***** Angle = " + angle);
        return angle;
    }

    public String appString(Vector app) {
        int appsize = app.size();
        int i;
        String s = new String();
        s = "Antipodal pairs:\n{";
        for (i = 0; i < appsize; i++) {
            RCPair pq = (RCPair)app.elementAt(i);
            if (i == (appsize - 1))
                s = s + pq.toString() + "}";
            else
                s = s + pq.toString() + "\n  ";
        }
        return s;
    }

    public void appPrint(Vector app) {
        System.out.println(appString(app));
    }

}
