package org.mga44.court.vacancy;

import java.util.Set;

public interface Sequencable<T, U> {
    boolean enabled(Set<Step> enabled);

    U execute(T input);

    void writeResult(U output); //TODO: Change API - return file
}
