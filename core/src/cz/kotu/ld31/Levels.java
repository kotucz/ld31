package cz.kotu.ld31;

public class Levels {

    final Level level0 = new Level(
    3, 3,
    "0XT" +
    "   " +
    "  X"
    );

    final Level level1 = new Level(
    4, 4,
    "X0  " +
    "T   " +
    "   X" +
    "X   "
    );

    final Level level2 = new Level(
    4, 4,
    "X0  " +
    "   X" +
    "XT  " +
    "    "
    );

    final Level level4 = new Level(
    7, 7,
    "   X   " +
    " 0   0 " +
    "       " +
    "X  T  X" +
    "       " +
    " 0   0 " +
    "   X   "
    );

    final Level[] LIST = new Level[]{
    level0,
    level1,
    level2,
    level4
    };

}
