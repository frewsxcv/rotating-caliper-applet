public class RCBox
{
    RCVertex pr, ps, qr, qs;
    RCVertex edgev;

    RCBox(RCVertex vpr, RCVertex vps, RCVertex vqr, RCVertex vqs, RCVertex main)
    {
	pr = vpr;
	ps = vps;
	qr = vqr;
	qs = vqs;
	edgev = main;
    }
}
