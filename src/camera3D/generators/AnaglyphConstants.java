package camera3D.generators;

import camera3D.generators.util.AnaglyphMatrix;

public class AnaglyphConstants {

	// Dubois Red Cyan
	public static AnaglyphMatrix LEFT_DUBOIS_REDCYAN = new AnaglyphMatrix(437, 449,
			164, -62, -62, -24, -48, -50, -17);
	public static AnaglyphMatrix RIGHT_DUBOIS_REDCYAN = new AnaglyphMatrix(-11, -32,
			-7, 377, 761, 9, -26, -93, 1234);

	// Dubois Magenta Green
	public static AnaglyphMatrix LEFT_DUBOIS_MAGENTAGREEN = new AnaglyphMatrix(-62,
			-158, -39, 284, 668, 143, -15, -27, 21);
	public static AnaglyphMatrix RIGHT_DUBOIS_MAGENTAGREEN = new AnaglyphMatrix(529,
			705, 24, -16, -15, -65, 9, 75, 937);

	// Dubois Amber Blue
	public static AnaglyphMatrix LEFT_DUBOIS_AMBERBLUE = new AnaglyphMatrix(1062,
			-205, 299, -26, 908, 68, -38, -173, 22);
	public static AnaglyphMatrix RIGHT_DUBOIS_AMBERBLUE = new AnaglyphMatrix(-16,
			-123, -17, 6, 62, -17, 94, 185, 911);
}
