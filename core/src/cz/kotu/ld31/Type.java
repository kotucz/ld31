package cz.kotu.ld31;

import java.util.EnumSet;
import java.util.Set;

/**
 * @author tkotula
 */
enum Type {
    VOID,
    STONE,
    TARGET,
    HOLE,
    // non-destructible
    BORDER,
    SOLID,
    //
    ;

    private static final Set<Type> isStatic = EnumSet.of(BORDER, SOLID);

    boolean isStatic() {
        return isStatic.contains(this);
    }

}
