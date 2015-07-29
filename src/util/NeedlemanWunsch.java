package util;

public class NeedlemanWunsch {
	private static final int DELETE_SCORE = 2;
	private static final int INSERT_SCORE = 1;
	private char[] mSeqA;
	private char[] mSeqB;
	private int[][] mD;
	private int mScore;
	private String mAlignmentSeqA = "";
	private String mAlignmentSeqB = "";

	public static void align(char[] seqA, char[] seqB) {
		NeedlemanWunsch alg = new NeedlemanWunsch();
		alg.init(seqA, seqB);
		alg.process(); 
		alg.backtrack();
		alg.printScoreAndAlignments();
	}

	private void init(char[] seqA, char[] seqB) {
		mSeqA = seqA;
		mSeqB = seqB;
		mD = new int[mSeqA.length + 1][mSeqB.length + 1];
		for (int i = 0; i <= mSeqA.length; i++) {
			for (int j = 0; j <= mSeqB.length; j++) {
				if (i == 0) {
					mD[i][j] = -j;
				} else if (j == 0) {
					mD[i][j] = -i;
				} else {
					mD[i][j] = 0;
				}
			}
		}
	}

	private void process() {
		for (int i = 1; i <= mSeqA.length; i++) {
			for (int j = 1; j <= mSeqB.length; j++) {
				int scoreDiag = mD[i-1][j-1] + alignWeight(i, j);
				int scoreLeft = mD[i][j-1] - DELETE_SCORE;
				int scoreUp = mD[i-1][j] - INSERT_SCORE;
				mD[i][j] = Math.max(Math.max(scoreDiag, scoreLeft), scoreUp);
			}
		}
	}

	private void backtrack() {
		int i = mSeqA.length;
		int j = mSeqB.length;
		mScore = mD[i][j];
		while (i > 0 && j > 0) {                        
			if (mD[i][j] == mD[i-1][j-1] + alignWeight(i, j)) {                          
				mAlignmentSeqA += mSeqA[i-1];
				mAlignmentSeqB += mSeqB[j-1];
				i--;
				j--;                            
				continue;
			} else if (mD[i][j] == mD[i][j-1] - DELETE_SCORE) {
				mAlignmentSeqA += "-";
				mAlignmentSeqB += mSeqB[j-1];
				j--;
				continue;
			} else {
				mAlignmentSeqA += mSeqA[i-1];
				mAlignmentSeqB += "-";
				i--;
				continue;
			}
		}
		mAlignmentSeqA = new StringBuffer(mAlignmentSeqA).reverse().toString();
		mAlignmentSeqB = new StringBuffer(mAlignmentSeqB).reverse().toString();
	}

	private int alignWeight(int i, int j) {
		if (mSeqA[i - 1] == mSeqB[j - 1]) {
			return 1;
		} else {
			return -1;
		}
	}

	private void printMatrix() {
		System.out.println("D =");
		for (int i = 0; i < mSeqA.length + 1; i++) {
			for (int j = 0; j < mSeqB.length + 1; j++) {
				System.out.print(String.format("%4d ", mD[i][j]));
			}
			System.out.println();
		}
		System.out.println();
	}

	private void printScoreAndAlignments() {
		System.out.println("Score: " + mScore);
		System.out.println("Sequence A: " + mAlignmentSeqA);
		System.out.println("Sequence B: " + mAlignmentSeqB);
		System.out.println();
	}

	public static void main(String [] args) {   
		String a = "MMR";
		String b = "MLMRM";
		char[] seqA = a.toCharArray();
		char[] seqB = b.toCharArray();
		align(seqA, seqB);
	}
}