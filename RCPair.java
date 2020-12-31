public class RCPair {
    RCVertex p;
    RCVertex q;

    RCPair(RCVertex a, RCVertex b) {
        p = a;
        q = b;
    }

    public String toString() {
        String s = new String();
        s = "[" + p.toString() + "," + q.toString() + "]";
        return s;
    }
}
