package ro.infotop.journeytoself.commons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Disease {
    DEPRESSION,
    STRESS,
    ANXIETY,
    FAMILY_PROBLEMS,
    COUPLE_PROBLEMS,
    PARANOIA,
    PHOBIA,
    OTHER;

    private static final String[] ILLNESSES = {
            "depresie",
            "stres",
            "anxietate",
            "probleme de familie",
            "probleme de cuplu",
            "paranoia",
            "fobii",
            "alta",
            "toate"
    };
    public static final String NO_DISEASE = "nicio problema";
    public static final String ALL_DISEASES = "toate";

    private static final long ALL_DISEASES_MASK = Long.MAX_VALUE;
    private static final long NO_DISEASE_MASK = 0b0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000L;
    private static final long DEPRESSION_DISEASE_MASK = 0b0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0001L;
    private static final long STRESS_DISEASE_MASK = 0b0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0010L;
    private static final long FAMILY_PROBLEMS_DISEASE_MASK = 0b0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0100L;
    private static final long ANXIETY_DISEASE_MASK = 0b0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_1000L;
    private static final long COUPLE_PROBLEMS_DISEASE_MASK = 0b0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0001_0000L;
    private static final long PARANOIA_DISEASE_MASK = 0b0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0010_0000L;
    private static final long PHOBIA_DISEASE_MASK = 0b0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0100_0000L;
    private static final long OTHER_DISEASE_MASK =
            Long.MAX_VALUE  ^ DEPRESSION_DISEASE_MASK
                            ^ STRESS_DISEASE_MASK
                            ^ FAMILY_PROBLEMS_DISEASE_MASK
                            ^ ANXIETY_DISEASE_MASK
                            ^ COUPLE_PROBLEMS_DISEASE_MASK
                            ^ PARANOIA_DISEASE_MASK
                            ^ PHOBIA_DISEASE_MASK;

    private static final Map<String, Long> STRING_MASKS = new HashMap<>();
    private static final Map<Disease, String> DISEASES = new HashMap<>();
    private static final Map<String, Disease> STRING_DISEASES = new HashMap<>();
    private static final Map<Long, String> MASKS_STRING = new HashMap<>();

    static {
        STRING_MASKS.put(ILLNESSES[0], DEPRESSION_DISEASE_MASK);
        STRING_MASKS.put(ILLNESSES[1], STRESS_DISEASE_MASK);
        STRING_MASKS.put(ILLNESSES[2], ANXIETY_DISEASE_MASK);
        STRING_MASKS.put(ILLNESSES[3], FAMILY_PROBLEMS_DISEASE_MASK);
        STRING_MASKS.put(ILLNESSES[4], COUPLE_PROBLEMS_DISEASE_MASK);
        STRING_MASKS.put(ILLNESSES[5], PARANOIA_DISEASE_MASK);
        STRING_MASKS.put(ILLNESSES[6], PHOBIA_DISEASE_MASK);
        STRING_MASKS.put(ILLNESSES[7], OTHER_DISEASE_MASK);
        STRING_MASKS.put(NO_DISEASE, NO_DISEASE_MASK);
        STRING_MASKS.put(ALL_DISEASES, ALL_DISEASES_MASK);

        MASKS_STRING.put(DEPRESSION_DISEASE_MASK, ILLNESSES[0]);
        MASKS_STRING.put(STRESS_DISEASE_MASK, ILLNESSES[1]);
        MASKS_STRING.put(ANXIETY_DISEASE_MASK, ILLNESSES[2]);
        MASKS_STRING.put(FAMILY_PROBLEMS_DISEASE_MASK, ILLNESSES[3]);
        MASKS_STRING.put(COUPLE_PROBLEMS_DISEASE_MASK, ILLNESSES[4]);
        MASKS_STRING.put(PARANOIA_DISEASE_MASK, ILLNESSES[5]);
        MASKS_STRING.put(PHOBIA_DISEASE_MASK, ILLNESSES[6]);
        MASKS_STRING.put(OTHER_DISEASE_MASK, ILLNESSES[7]);
        MASKS_STRING.put(NO_DISEASE_MASK, NO_DISEASE);
        MASKS_STRING.put(ALL_DISEASES_MASK, ALL_DISEASES);

        DISEASES.put(DEPRESSION, ILLNESSES[0]);
        DISEASES.put(STRESS, ILLNESSES[1]);
        DISEASES.put(ANXIETY, ILLNESSES[2]);
        DISEASES.put(FAMILY_PROBLEMS, ILLNESSES[3]);
        DISEASES.put(COUPLE_PROBLEMS, ILLNESSES[4]);
        DISEASES.put(PARANOIA, ILLNESSES[5]);
        DISEASES.put(PHOBIA, ILLNESSES[6]);
        DISEASES.put(OTHER, ILLNESSES[7]);

        STRING_DISEASES.put(ILLNESSES[0], DEPRESSION);
        STRING_DISEASES.put(ILLNESSES[1], STRESS);
        STRING_DISEASES.put(ILLNESSES[2], ANXIETY);
        STRING_DISEASES.put(ILLNESSES[3], FAMILY_PROBLEMS);
        STRING_DISEASES.put(ILLNESSES[4], COUPLE_PROBLEMS);
        STRING_DISEASES.put(ILLNESSES[5], PARANOIA);
        STRING_DISEASES.put(ILLNESSES[6], PHOBIA);
        STRING_DISEASES.put(ILLNESSES[7], OTHER);

    }

    public static long findMask(String disease) {
        Long mask = STRING_MASKS.get(disease);
        return mask != null ? mask : NO_DISEASE_MASK;
    }

    public static Long findMask(Disease d) {
        return findMask(getDiseaseAsString(d));
    }

    public static String getDiseaseAsString(Disease d) {
        return DISEASES.get(d);
    }

    public static Disease findDiseaseByString(String s) {
        return STRING_DISEASES.get(s);
    }

    public static long generateMatchingCode(List<Disease> diseases) {
        long code = 0;

        for (Disease d : diseases) {
            code |= findMask(d);
        }
        return code;
    }

    public static List<Disease> generateDiseasesList(long matcherCode) {
        List<Disease> diseases = new ArrayList<>();

        for (long mask : MASKS_STRING.keySet()) {
            if ((matcherCode & mask) != 0 && (mask != ALL_DISEASES_MASK)) {
                diseases.add(
                        STRING_DISEASES.get(MASKS_STRING.get(mask))
                );
            }
        }
        return diseases;
    }

    @Override
    public String toString() {
        String d = DISEASES.get(this);
        return d != null ? d : "N/A";
    }

}
