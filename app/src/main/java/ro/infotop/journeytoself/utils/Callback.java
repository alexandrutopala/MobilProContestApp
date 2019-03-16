package ro.infotop.journeytoself.utils;

@FunctionalInterface
public interface Callback {
    void call();

    static Callback empty() {
        return () -> {};
    }
}
