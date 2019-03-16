package ro.infotop.journeytoself.utils;

import java.util.Random;

public final class AlgorithmicUtils {
    private static byte[][] matrix;

    private AlgorithmicUtils() {
    }

    static {
        matrix = new byte[65][65];

        for (byte i = 0; i < matrix.length; i++) {
            matrix[0][i] = matrix[i][0] = i;
        }
    }

    static int getLevenshteinDistanceForBinaryRepresentationOf(long N1, long n2) {
        if (MatchingManager.getMaxBound().grade <= MatchingManager.MatchGrade.HIGH.grade) {
            return applyRealLevenshtein(N1, n2);
        } else {
            return applyPseudoLevenshtein(N1, n2);
        }
    }

    private static int applyRealLevenshtein(long N1, long n2) {
        String s1 = getBinaryRepresentationAsString(N1);
        String s2 = getBinaryRepresentationAsString(n2);
        int dif = s1.length() - s2.length();

        if (dif > 0) {
            s2 = new StringBuilder(s2).insert(0, generateZeroSequence(dif)).toString();
        } else if (dif < 0) {
            s1 = new StringBuilder(s1).insert(0, generateZeroSequence(-dif)).toString();
        }

        int size = s1.length();

        // s1 -> i
        // s2 -> j
        for (int i = 1; i <= size; i++) {
            for (int j = 1; j <= size; j++) {
                if (s1.charAt(i-1) == s2.charAt(j-1)) {
                    matrix[i][j] = matrix[i-1][j-1];
                    continue;
                }
                matrix[i][j] = (byte) (Math.min(
                        matrix[i-1][j], Math.min(matrix[i-1][j-1], matrix[i][j-1])
                ) + 1);
            }
        }

        return matrix[size][size];
    }

    private static int applyPseudoLevenshtein(long N1, long n2) {
        byte onesOfN1 = getNumberOfOnesFromBinaryRepresentation(N1);

        long result = N1 & n2;
        byte onesOfResult = getNumberOfOnesFromBinaryRepresentation(result);

        return onesOfN1 - onesOfResult;
    }

    private static byte getNumberOfOnesFromBinaryRepresentation(long x) {
        byte count = 0;

        while (x > 0) {
            if ((x & 1) == 1) {
                count++;
            }
            x = x >> 1;
        }
        return count;
    }

    private static String getBinaryRepresentationAsString(long x) {
        StringBuilder sb = new StringBuilder();

        while (x > 0) {
            if ((x & 1) == 1) {
                sb.append(1);
            } else {
                sb.append(0);
            }
            x >>= 1;
        }
        sb.reverse();
        return sb.toString();
    }

    private static String generateZeroSequence(int n) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < n; i++) {
            sb.append(0);
        }
        return sb.toString();
    }

}
