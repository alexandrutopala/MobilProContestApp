package ro.infotop.journeytoself.utils;

public final class MatchingManager {
    public enum MatchGrade {
        // TODO: modify this accordingly to reality
        LOWEST(63), LOW(10), MODERATE(5), MEDIUM(3), HIGH(1), HIGHEST(0);

        int grade = 5;

        MatchGrade(int grade) {
            this.grade = grade;
        }
    }

    private static MatchGrade maxBound = MatchGrade.HIGH;

    private MatchingManager() {
    }

    public static boolean codesAreMatching(long MC1, long mc2) {
        int levDist = AlgorithmicUtils.getLevenshteinDistanceForBinaryRepresentationOf(MC1, mc2);

        return levDist <= maxBound.grade;
    }

    public static MatchGrade getMaxBound() {
        return maxBound;
    }

    public static void setMaxBound(MatchGrade mg) {
        MatchingManager.maxBound = mg;
    }
}
