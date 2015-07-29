package math;

import org.ejml.simple.SimpleMatrix;

import util.MatrixUtil;

/**
 * Class: Vanilla Logisitc Regression
 * ----------------------------------
 * 1 is true
 * 0 is false
 * Adds an intercept term.
 * @author mike + chris
 */
public class LogRegression {

	private static final boolean ADD_INTERCEPT = false;
	private static final double LEARNING_RATE = 0.001;
	private static final double THRESHOLD = 1E-4;
	private static final int MAX_ITERATIONS = 10000000;
	private SimpleMatrix theta;
	private String name = "";

	public LogRegression() {}

	public LogRegression(String name) {
		this.name = name;
	}

	public void trainWithRestarts(SimpleMatrix features, SimpleMatrix labels, 
			int numRestarts) {
		SimpleMatrix bestTheta = null;
		double bestLoss = 0;
		for(int i = 0; i < numRestarts; i++) {
			train(features, labels);
			double loss = getError(features, labels);
			if(bestTheta == null || loss < bestLoss) {
				bestLoss = loss;
				bestTheta = new SimpleMatrix(theta);
			}
		}
		theta = bestTheta;
	}

	public void train(SimpleMatrix f, SimpleMatrix labels) {
		// should randomize the order of rows in the feature matrix
		// mini-batch size is 1

		// add a col of ones to the feature matrix
		
		
		SimpleMatrix fPrime = addInterceptFeature(f);
		theta = MatrixUtil.randomVector(fPrime.numCols(), 1).transpose();

		for(int i = 0; i < MAX_ITERATIONS; i++) {
			int row = (i % fPrime.numRows());
			SimpleMatrix x = fPrime.extractMatrix(row, row+1, 0, fPrime.numCols());
			double label = labels.get(row); 
			SimpleMatrix thetaTemp = new SimpleMatrix(theta);
			double gradSize = 0;
			for(int col = 0; col < fPrime.numCols(); col++) {
				double activation = logistic(x);
				double loss = label - activation;
				double grad = loss * x.get(col);
				double update = theta.get(col) + LEARNING_RATE * grad;
				gradSize += Math.pow(grad, 2);
				thetaTemp.set(col, update);
			}
			theta = thetaTemp;
			gradSize = Math.sqrt(gradSize);
			if(gradSize < THRESHOLD) {
				System.out.println("finished in " + i + " iterataion");
				break;
			}
			//if(i % 1000 == 0) System.out.println(i + ": " + gradSize);
		}
	}
	
	public void trainBFGS(SimpleMatrix f, SimpleMatrix labels) {
		
		/*SimpleMatrix fPrime = addInterceptFeature(f);
		int n = fPrime.numCols();
		
		int[][] features = new int[fPrime.numRows()][];
		for(int i = 0; i < fPrime.numRows(); i++) {
			features[i] = new int[fPrime.numCols()];
			for(int j = 0; j < fPrime.numCols(); j++) {
				features[i][j] = j;
			}
		}
		
		double[][] x = MatrixUtil.asMatrix(fPrime);
		
		int[] y = new int[labels.getNumElements()];
		for(int i = 0; i < y.length; i++) {
			y[i] = (int) labels.get(i);
		}
		
		double[] init = new double[n];
		for(int i = 0; i < init.length; i++) {
			init[i] = Math.random();
		}
		
		DiffFunction fn = new LogisticObjectiveFunction(n, features, x, y);
		QNMinimizer minimizer = new QNMinimizer();
		double[] min = minimizer.minimize(fn, 1E-50, init);
		
		theta = new SimpleMatrix(1, n);
		for(int i = 0; i < min.length; i++) {
			theta.set(i, min[i]);
		}*/
		
		throw new RuntimeException("depricated");
	}
	
	public double test(SimpleMatrix X, SimpleMatrix y) {
		int numCorrect = 0;
		for(int i = 0; i < X.numRows(); i++) {
			SimpleMatrix f = X.extractVector(true, i);
			int prediction = predict(f);
			double truth = y.get(i);
			if(truth == prediction) {
				numCorrect++;
			}
		}
		return 100.0 * numCorrect /  X.numRows();
	}

	/**
	 * Assumes the intercept has not been added.
	 * Takes in a horizontal vector
	 */
	public int predict(SimpleMatrix feature) {
		return predict(feature, 0.5);
	}

	/**
	 * Assumes the intercept has not been added.
	 * Takes in a horizontal vector
	 */
	public int predict(SimpleMatrix feature, double threshold) {
		if(feature.numRows() > 1) {
			throw new RuntimeException("feature input should be a horizontal vector");
		}
		SimpleMatrix fPrime = addInterceptFeature(feature);
		return (logistic(fPrime) >= threshold) ? 1 : 0; 
	}
	
	/**
	 * Assumes the intercept has not been added.
	 * Takes in a horizontal vector
	 */
	public double probability(SimpleMatrix x) {
		if(x.numRows() > 1) {
			throw new RuntimeException("feature input should be a horizontal vector");
		}
		SimpleMatrix xPrime = addInterceptFeature(x);
		return logistic(xPrime);
	}
	
	public String getName() {
		return name;
	}
	
	private SimpleMatrix addInterceptFeature(SimpleMatrix f) {
		if(ADD_INTERCEPT) {
			int numFeatures = f.numCols();
			int numSamples = f.numRows();
			SimpleMatrix fPrime = f.combine(0, numFeatures, MatrixUtil.ones(numSamples, 1));
			return fPrime;
		} else {
			return f;
		}
	}
	
	public double thetaDot(SimpleMatrix x) {
		SimpleMatrix fPrime = addInterceptFeature(x);
		return theta.transpose().mult(fPrime).get(0);
	}

	/**
	 * Assumes the intercept has already been added.
	 * Takes in a horizontal vector
	 */
	private double logistic(SimpleMatrix x) {
		double z = theta.mult(x.transpose()).get(0, 0);
		return 1.0 / (1.0 +Math.exp(-z));
	}

	private double getError(SimpleMatrix data, SimpleMatrix labels) {
		SimpleMatrix fPrime = addInterceptFeature(data);
		// implement this to be actual loss
		SimpleMatrix y = fPrime.mult(theta.transpose());
		SimpleMatrix predict = MatrixUtil.elementwiseApplyLogistic(y);
		int loss = 0;
		for(int i = 0; i < predict.getNumElements(); i++) {
			int label = predict.get(i) > 0 ? 1 : 0;
			if(labels.get(i) != label) loss++;
		}
		return loss;
	}
	
	public static void main(String[] args) {
		int n = 20;
		SimpleMatrix m = new SimpleMatrix(n, 2);
		SimpleMatrix l = new SimpleMatrix(n, 1);
		for(int i = 0; i < n; i++) {
			double v = Math.random();
			m.set(i, 0, v);
			l.set(i, v > 0.5 ? 1 : 0);
		}
		LogRegression r = new LogRegression();
		r.train(m, l);
		int correct = 0;
		for(int i = 0; i < n; i++) {
			SimpleMatrix f = m.extractVector(true, i);
			int prediction = r.predict(f);
			if(prediction != l.get(i)) {
				System.out.println("no");
				System.out.println(f);
			} else {
				correct++;
			}
		}
		System.out.println(100.0 * correct / n);
	}
}
