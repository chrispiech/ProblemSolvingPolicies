package util;

import java.util.Map;

public abstract class LineSearch {

	public abstract Map<Object, Double> getDerivative(Map<Object, Double> x);
	
	/*public double getStepSize() {        
		Map<Object, Double> currentInitGradientDot = o.getInitialGradient();
		//Should update all in the objective
		o.updateAlpha(initialStep);     
		int nrIterations = 0;
		//System.out.println("tried alpha" + initialStep + " value " + o.getCurrentValue());
		while(!WolfeConditions.suficientDecrease(o,c1)){                        
			if(nrIterations >= maxIterations){
				o.printLineSearchSteps();       
				return -1;
			}
			double alpha=o.getAlpha();
			double alphaTemp = 
					Interpolation.quadraticInterpolation(o.getOriginalValue(), o.getInitialGradient(), alpha, o.getCurrentValue());
			if(alphaTemp >= sigma1 || alphaTemp <= sigma2*o.getAlpha()){
				//                      System.out.println("using alpha temp " + alphaTemp);
				alpha = alphaTemp;
			}else{
				//                      System.out.println("Discarding alpha temp " + alphaTemp);
				alpha = alpha*contractionFactor;
			}
			//              double alpha =o.getAlpha()*contractionFactor;

			o.updateAlpha(alpha);
			//System.out.println("tried alpha" + alpha+ " value " + o.getCurrentValue());
			nrIterations++;                 
		}

		System.out.println("Leavning line search used:");
		o.printLineSearchSteps();       

		previousInitGradientDot = currentInitGradientDot;
		previousStepPicked = o.getAlpha();
		return o.getAlpha();
	}*/

}
