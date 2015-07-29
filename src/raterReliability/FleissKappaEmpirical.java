/*
        Copyright (c) 2009-2011
                Speech Group at Informatik 5, Univ. Erlangen-Nuremberg, GERMANY
                Korbinian Riedhammer
                Tobias Bocklet

        This file is part of the Java Speech Toolkit (JSTK).

        The JSTK is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        The JSTK is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with the JSTK. If not, see <http://www.gnu.org/licenses/>.
 */
package raterReliability;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

import util.RandomUtil;


/**
 * Krippendorffs Alpha, e.g. http://en.wikipedia.org/wiki/Krippendorff%27s_Alpha
 * 
 * @author sikoried
 */
public final class FleissKappaEmpirical {

	private static final int BOOTSTRAP_ITERATIONS = 100000;

	public static double agreement(int[][] data, int[] options) {
		return new FleissKappaEmpirical().run(data, options);
	}

	private int[][] data;
	private int[] numOptions;

	private int numRaters;
	private int numRated;

	private double run(int[][] data, int[] numOptions) {
		this.data = data;
		this.numOptions = numOptions;
		this.numRaters = data.length;
		this.numRated = data[0].length;

		int possible = countPossible();
		double Pe = estimatePe(possible);
		double Pa = calculatePa(possible);

		return (Pa - Pe) / (1.0 - Pe);
	}

	private double calculatePa(int possible) {
		int matches = countPairAgreements(data);
		return (double)(matches) / possible;
	}

	private double estimatePe(int possible) {
		int sumMatches = 0;
		int sumPossible = 0;
		for(int i = 0; i < BOOTSTRAP_ITERATIONS; i++) {
			if(i % 10000 == 0) System.out.println(i);
			int[][] randomData = populateRandom();
			sumMatches += countPairAgreements(randomData);
			sumPossible += possible;
		}
		return (double)(sumMatches) / sumPossible;
	}

	private int countPossible() {
		int[][] uniformData = populateAllOnes();
		return countPairAgreements(uniformData);
	}

	private int countPairAgreements(int[][] data) {
		int numSame = 0;
		for(int c = 0; c < numRated; c++) {
			for(int r1 = 0; r1 < numRaters; r1++) {
				for(int r2 = r1 + 1; r2 < numRaters; r2++) {
					if(empty(r1, c) || empty(r2, c)) continue;
					if(data[r1][c] == data[r2][c]) {
						numSame++;
					}
				}
			}
		}
		return numSame;
	}

	private boolean empty(int r, int c) {
		return data[r][c] == -1;
	}


	private int[][] populateRandom() {
		int[][] m = copyData();
		for(int r = 0; r < numRaters; r++) {
			for(int c = 0; c < numRated; c++) {
				int value = RandomUtil.randInt(numOptions[c]);
				set(m, r, c, value);
			}
		}
		return m;
	}

	private int[][] populateAllOnes() {
		int[][] m = copyData();
		for(int r = 0; r < numRaters; r++) {
			for(int c = 0; c < numRated; c++) {
				set(m, r, c, 1);
			}
		}
		return m;
	}

	private int[][] copyData() {
		int[][] m = new int[numRaters][];
		for(int r = 0; r < numRaters; r++) {
			m[r] = new int[numRated];
			for(int c = 0; c < numRated; c++) {
				m[r][c] = data[r][c];
			}
		}
		return m;
	}

	private void set(int[][] m, int r, int c, int value) {
		if(empty(r, c)) {
			m[r][c] = -1;
		} else {
			m[r][c] = value;
		}
	}

	public static void main(String[] args) {
		System.out.println("FAKE DATA");
		int [][] data = {
				{1, 4, 2, 6, -1},
				{1, -1, 2, 4, -1},
		};

		int[] options = {2, 2, 2, 2, 2};

		System.out.println("alpha nominal = " + FleissKappaEmpirical.agreement(data, options));
	}
}