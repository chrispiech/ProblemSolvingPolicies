package util;

import org.ejml.simple.SimpleMatrix;

public class MatrixUtil {

	public static SimpleMatrix randomMatrix(int rows, int cols, double std) {
		SimpleMatrix m = new SimpleMatrix(rows, cols);
		for(int r = 0; r < rows; r++) {
			for(int c = 0; c < cols; c++) {
				m.set(r, c, RandomUtil.gauss(0, std));
			}
		}
		return m;
	}

	public static SimpleMatrix randomVector(int numElems, double std) {
		SimpleMatrix m = new SimpleMatrix(numElems, 1);
		for(int r = 0; r < numElems; r++) {
			double value = RandomUtil.gauss(0, std);
			m.set(r, value);
		}
		return m;
	}
	
	public static double max(SimpleMatrix z) {
		Double max = null;
		for(int r = 0; r < z.numRows(); r++) {
			for(int c = 0; c < z.numCols(); c++) {
				if(max == null || z.get(r, c) > max) {
					max = z.get(r, c);
				}
			}
		}
		return max;
	}
	
	public static SimpleMatrix plus(SimpleMatrix a, double b) {
		SimpleMatrix out = new SimpleMatrix(a);
		for(int r = 0; r < a.numRows(); r++) {
			for(int c = 0; c < a.numCols(); c++) {
				double v = a.get(r, c);
				out.set(r, c, v + b);
			}
		}
		return out;
	}
	
	public static SimpleMatrix minus(SimpleMatrix a, double b) {
		return plus(a, -b);
	}

	/**
	 * Method: Softmax
	 * ---------------
	 * This method implements the softmax function.
	 * http://en.wikipedia.org/wiki/Softmax_function.
	 * Instead of calculating as written in the definition, we first
	 * subtract the max to prevent problems with overflow.
	 */
	public static SimpleMatrix softmax(SimpleMatrix z) {
		if(!z.isVector()) throw new RuntimeException("Not vector!");
		double maxZ = max(z);
		SimpleMatrix zMinusMax = minus(z, maxZ);
		SimpleMatrix softMax = normalizeToOne(exp(zMinusMax));
		validate(softMax);
		return softMax;
	}
	
	public static void validate(SimpleMatrix m) {
		for(int r = 0; r < m.numRows(); r++) {
			for(int c = 0; c < m.numCols(); c++) {
				double v = m.get(r, c);
				if(Double.isInfinite(v)) {
					throw new RuntimeException("inf!");
				}
				if(Double.isNaN(v)) {
					throw new RuntimeException("nan");
				}
			}
		}
	}
	
	public static SimpleMatrix elementwiseApplyLogistic(SimpleMatrix x) {
		SimpleMatrix y = new SimpleMatrix(x);
		for(int r = 0; r < x.numRows(); r++) {
			for(int c = 0; c < x.numCols(); c++) {
				double y_rc = NeuralUtils.sigmoid(x.get(r, c));
				y.set(r, c, y_rc);
			}
		}
		return y;
	}
	

	private static SimpleMatrix normalizeToOne(SimpleMatrix m) {
		double sum = sum(m);
		return m.scale(1.0 / sum);
	}

	private static double sum(SimpleMatrix m) {
		double s = 0;
		for(int r = 0; r < m.numRows(); r++) {
			for(int c = 0; c < m.numCols(); c++) {
				double v = m.get(r, c);
				s += v;
			}
		}
		return s;
	}

	public static SimpleMatrix exp(SimpleMatrix v) {
		if(!v.isVector()) throw new RuntimeException("Not vector!");
		SimpleMatrix out = new SimpleMatrix(v);
		for(int i = 0; i < v.getNumElements(); i++) {
			double value = v.get(i);
			out.set(i, Math.exp(value));
		}
		return out;
	}

	public static int argmax(SimpleMatrix v) {
		if(!v.isVector()) throw new RuntimeException("Not vector!");
		int size = v.getNumElements();
		Integer arg = null;
		Double maxValue = null;
		for(int i = 0; i < size; i++) {
			double value = v.get(i);
			if(maxValue == null || value > maxValue) {
				maxValue = value;
				arg = i;
			}
		}
		return arg;
	}

	public static SimpleMatrix basis(int size, int i) {
		SimpleMatrix v = new SimpleMatrix(size, 1);
		v.set(i, 1);
		return v;
	}

	public static String toString(SimpleMatrix m) {
		String str = "";
		for(int r = 0; r < m.numRows(); r++) {
			String row = "";
			for(int c = 0; c < m.numCols(); c++) {
				row += m.get(r, c) + ", ";
			}
			str += row;
			if(r != m.numRows() - 1) str += "\n";
		}
		return str;
	}

	public static double euclidDist(SimpleMatrix v1, SimpleMatrix v2) {
		if(!v1.isVector()) throw new RuntimeException("not vector");
		if(!v2.isVector()) throw new RuntimeException("not vector");
		if(v1.getNumElements() != v2.getNumElements()) {
			throw new RuntimeException("not same size!");
		}
		int size = v1.getNumElements();
		double diffSquared = 0;
		for(int i = 0; i < size; i++) {
			double value1 = v1.get(i);
			double value2 = v2.get(i);
			double diff = Math.abs(value1 - value2);
			diffSquared += diff * diff;
		}
		return Math.sqrt(diffSquared);
	}
	
	public static boolean equals(SimpleMatrix m1, SimpleMatrix m2) {
		if(m1.numRows() != m2.numRows()) return false;
		if(m1.numCols() != m2.numCols()) return false;
		for(int i = 0; i < m1.getNumElements(); i++) {
			if(m1.get(i) != m2.get(i)) return false;
		}
		return true;
	}

	public static void setRow(SimpleMatrix m, int r, SimpleMatrix row) {
		for(int c = 0; c < m.numCols(); c++) {
			m.set(r, c, row.get(c));
		}
	}

	public static SimpleMatrix ones(int rows, int cols) {
		SimpleMatrix ones = new SimpleMatrix (rows, cols);
		for(int r = 0; r < rows; r++) {
			for(int c = 0; c < cols; c++) {
				ones.set(r, c, 1);
			}
		}
		return ones;
	}

	public static double[][] asMatrix(SimpleMatrix m) {
		double[][] x = new double[m.numRows()][];
		for(int r = 0; r < m.numRows(); r++) {
			x[r] = new double[m.numCols()];
			for(int c = 0; c < m.numCols(); c++) {
				x[r][c] = m.get(r, c);
			}
		}
		return x;
	}

}
