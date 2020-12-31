import java.applet.*;
import java.awt.*;
import java.util.Vector;
// import RCVertex;

class STH {
    public static void main(String[] args) {
    RCPolygon poly1;
    RCPolygon poly2;
    RCVEPair minpair;
    RCVEPair newminpair;
    double dist;
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
    // Polygon 1
    // (5., 1.), (4., 2.), (4., 3.), (5., 4.), (6., 4.), (7., 3.),
                              // (7., 2.), (6., 1.), (5., 1.)
    // RCVertex a;
    Vector v1 = new Vector();
    float a = 5;
    float b = 5;
    RCVertex c = new RCVertex(a, b, -1);
    RCS rcs = new RCS();
    rcs.order.addElement(c);
    // poly1 = new RCPolygon(v1);
    // RCVertex(4.0, 2.0);
    // RCVertex(4.0, 3.0);
    // RCVertex(5.0, 4.0);
    // RCVertex(6.0, 4.0);
    // RCVertex(7.0, 3.0);
    // RCVertex(7.0, 2.0);
    // RCVertex(6.0, 1.0);
    // RCVertex(5.0, 1.0);
    }
}