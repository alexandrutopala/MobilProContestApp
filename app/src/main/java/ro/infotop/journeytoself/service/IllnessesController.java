package ro.infotop.journeytoself.service;

import java.util.List;

import ro.infotop.journeytoself.commons.Disease;
import ro.infotop.journeytoself.model.quizModel.Question;
import ro.infotop.journeytoself.model.userModel.Patient;
import ro.infotop.journeytoself.repository.IllnessesRepo;

@Deprecated
public class IllnessesController {
    private static final IllnessesController ourInstance = new IllnessesController();

    private IllnessesRepo illnessesRepo;

    public static IllnessesController getInstance() {
        return ourInstance;
    }

    private IllnessesController() {
        illnessesRepo = new IllnessesRepo(true);
    }

    public List<Disease> findAllIllnesses() {
        return illnessesRepo.findAllIllnesses();
    }

    @Deprecated
    public long generateMatcherCode(Disease ... diseases) {
        long code = 0;
        for (Disease d : diseases) {
            code |= illnessesRepo.findMask(d);
        }
        return code;
    }

    @Deprecated
    public long generateMatcherCode(List<Disease> diseases) {
        long code = 0;
        for (Disease d : diseases) {
            code |= illnessesRepo.findMask(d);
        }
        return code;
    }

    @Deprecated
    public boolean isAMatch(Patient p, Question q) {
        long patientCode = generateMatcherCode((Disease[]) p.getDiseases().toArray());
        return (patientCode & q.getMatcherCode()) != 0;
    }

    @Deprecated
    public static boolean codesAreMatching(long c1, long c2) {
        return (c1 & c2) != 0;
    }

}
