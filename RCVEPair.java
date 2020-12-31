public class RCVEPair {
    RCPair e;
    RCVertex v;
    boolean prl;

    RCVEPair(RCPair edge, RCVertex vertex) {
        this.e = edge;
        this.v = vertex;
        this.prl = false;
    }

    RCVEPair(RCPair edge, RCVertex vertex, boolean parallel) {
        this.e = edge;
        this.v = vertex;
        this.prl = parallel;
    }

    RCVEPair(RCVertex ep, RCVertex eq, RCVertex vertex) {
        this.e = new RCPair(ep, eq);
        this.v = vertex;
        this.prl = false;
    }

    RCVEPair(RCVertex ep, RCVertex eq, RCVertex vertex, boolean parallel) {
        this.e = new RCPair(ep, eq);
        this.v = vertex;
        this.prl = parallel;
    }
}
