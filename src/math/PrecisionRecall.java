package math;

import java.util.ArrayList;
import java.util.List;

import org.ejml.simple.SimpleMatrix;

public class PrecisionRecall {
	
	private LogRegression r;
	private SimpleMatrix X;
	private SimpleMatrix y;
	
	private PrecisionRecall(LogRegression r, SimpleMatrix X, SimpleMatrix y) {
		this.r = r;
		this.X = X;
		this.y = y;
	}

	public static double auc(LogRegression r, SimpleMatrix X, SimpleMatrix y) {
		return new PrecisionRecall(r, X, y).run();
	}
	
	private double run() {
		/*List<Pair<Double, Double>> prCurve = new ArrayList<Pair<Double, Double>>();
		System.out.println("precision, recall");
		System.out.println("-----");
		for(double threshold = 0.0; threshold <= 1.0; threshold += 0.001) {
			Pair<Double, Double> recallPrecision = falseVsTruePositive(threshold);
			prCurve.add(recallPrecision);
			System.out.println(recallPrecision.first() + "\t" + recallPrecision.second());
		}*/
		throw new RuntimeException("depricated");
	}

	/**
	 * Method: Get Incorrect Correct
	 * -----------------------------
	 * For a given threshold in [0, 1] calculate the precision recall.
	 
	private Pair<Double, Double> falseVsTruePositive(double theta) {
		double truePositives = 0;
		double falsePositives = 0;
		double trueNegatives = 0;
		double falseNegatives = 0;

		
		for(int i = 0; i < X.numRows(); i++) {
			SimpleMatrix row = X.extractVector(true, i);
			int prediction = r.predict(row);
			int truth = (int)(y.get(i));
			if(truth == 1) {
				if(prediction == 1) {
					truePositives++;
				} else {
					falsePositives++;
				}
			} else {
				if(prediction == 1) {
					falseNegatives++;
				} else {
					trueNegatives++;
				}
			}
		}
		

		Double tpr = truePositives / (truePositives + falseNegatives);
		Double fpr = falsePositives / (falsePositives + trueNegatives);

		return new Pair<Double, Double>(fpr, tpr);
	}*/
	
}
