package ro.infotop.journeytoself.repository;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ro.infotop.journeytoself.commons.Disease;

public class IllnessesRepo extends DelayedRepository{


    public IllnessesRepo() {
        super(false);
    }

    public IllnessesRepo(boolean simulateDelay) {
        super(simulateDelay);
    }

    public List<Disease> findAllIllnesses() {
        if (simulateDelay) sleep(LONG_LATENCY);
        return new ArrayList<>(
                Arrays.asList(Disease.values())
        );
    }


    public static List<Disease> generateRandomListOfIllnesses() {
        Random r = new Random();
        Disease[] ILLNESSES = Disease.values();
        int illnesses = r.nextInt(ILLNESSES.length-1) + 1;
        boolean[] used = new boolean[ILLNESSES.length];
        int count = 0;
        int index;
        List<Disease> list = new ArrayList<>(illnesses);

        while (count < illnesses) {
            index = r.nextInt(ILLNESSES.length);
            if (!used[index]) {
                used[index] = true;
                list.add(ILLNESSES[index]);
                count++;
            }
        }

        return list;
    }

    @Deprecated
    public long findMask(String disease) {
        return Disease.findMask(disease);
    }

    @Deprecated
    public long findMask(Disease d) {
        return Disease.findMask(d);
    }
}
