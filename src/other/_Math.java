package other;

public class _Math {
	static double calcAngle(int x, int y){
		double angle = 0;
		double conversion = 180/Math.PI;

		if(x>0 && y>0){
			angle = 0 + Math.atan(y/x) * conversion;
		}else if(x<0 && y>0){
			angle = 90 + Math.atan(-x/y) * conversion;
		}else if(x<0 && y<0){
			angle = 180 + Math.atan(-y/-x) * conversion;
		}else if(x>0 && y<0){
			angle = 270 + Math.atan(x/-y) * conversion;
		}else{
			System.out.println("Error, calculating angle");
			System.exit(-1);
		}
		return angle;
	}

	public static int random(int min, int max) {
		max = max + 1;
		int value = (int) (min + (Math.random() * ((max - min) )));
		return value;
	}
}
