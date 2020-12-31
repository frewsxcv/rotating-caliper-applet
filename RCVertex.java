import java.awt.Point;
import java.awt.Dimension;
import java.awt.Graphics;
import java.lang.Math;

public class RCVertex {

    public static float WW = 0;
    public static float WH = 0;
    float x;
    float y;
    int n;

    RCVertex (int xcoord, int ycoord, int index) {
        x = (float) xcoord;
        y = (float) ycoord;
        n = index;
    }

    RCVertex (float xcoord, float ycoord, int index) {
        x = xcoord;
        y = ycoord;
        n = index;
    }

    RCVertex (boolean dim, int xcoord, int ycoord, int index) {
        this((float)xcoord, (float)ycoord, index);
        if (dim = true)
            this.dim2vx();
    }

    RCVertex (Dimension d) {
        x = (float)d.width;
        y = (float)d.height;
        n = -1;
    }

    public float yC() {
        return WH - y;
    }

    public void v2vLine(RCVertex vto, Graphics g) {
        int x1, y1, x2, y2;
        x1 = (int)x;
        y1 = (int)yC();
        x2 = (int)vto.x;
        y2 = (int)vto.yC();
        g.drawLine(x1, y1, x2, y2);
    }

    public void drawVLine(Graphics g) {
        int x1, y1, x2, y2;
        x1 = (int)x;
        y1 = 0;
        x2 = x1;
        y2 = (int)WH;
        g.drawLine(x1, y1, x2, y2);
    }

    public double area(RCVertex a, RCVertex b, RCVertex c) {
        double d_area;
        d_area = (double)(0.5 * (a.x * b.y - a.y * b.x + a.y * c.x - a.x * c.y + b.x * c.y - c.x * b.y));
        return d_area;
    }

    public int leftturn(RCVertex a, RCVertex b, RCVertex c) {
        double narea = a.area(a, b, c);
        //System.out.println(a.toString() + b.toString() + c.toString() + ": area = " + narea);

        if (narea > 0.0)
            return 1;
        else {
            if (narea < 0.0)
                return 0;
            else
                return -1;
        }
    }

    public void dim2vx() {
        y = WH - y;
    }

    public void vx2dim() {
        this.dim2vx();
    }

    public double v2vDist(RCVertex p) {
        return Math.sqrt(Math.pow((p.x - x), 2) + Math.pow((p.y - y), 2));
    }

    public double v2lineDist(RCVertex p, RCVertex q) {
        double dpq = p.v2vDist(q);
        if (dpq == 0.0)
            return 0;
        else
            return Math.abs((double)(this.x * p.y - this.y * p.x + this.y * q.x - this.x * q.y + p.x * q.y - q.x * p.y)) / dpq;
    }

    public boolean equals(RCVertex p) {
        return ((this.x == p.x) && (this.y == p.y));
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append('(').append(n).append(':').append((int)x).append(',').append((int)y).append(')');
        return sb.toString();
    }

    public void getWindowSize(RCS controller) {
        Dimension dim = new Dimension();
        dim = controller.drawarea.getSize();
        WW = dim.width - 1;
        WH = dim.height - 1;
        controller.lab4.setText("Draw area size: " + WW + " x " + WH);
    }
}
