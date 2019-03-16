package ro.infotop.journeytoself.utils;

public interface ParametricCallback<E> {
    void call(E e);

    static ParametricCallback empty() {
        return (e) -> {};
    }
}
