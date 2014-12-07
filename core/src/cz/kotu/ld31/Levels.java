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
    "0  X" +
    "  X " +
    "XT  " +
    "   X"
    );

    final Level level5_2 = new Level(
    5, 5,
    "0  X " +
    "X    " +
    "  0T " +
    "     " +
    " X   "
    );

    final Level level7_2_1 = new Level(
    7, 7,
    "  X    " +
    "     X " +
    " X     " +
    "   T   " +
    "  X X  " +
    "X     X" +
    "0  X  0"
    );

    final Level level7_2_2 = new Level(
    7, 7,
    "  X    " +
    "     X " +
    " X     " +
    "   T   " +
    "  X  X " +
    "X     X" +
    "0  X  0"
    );

    final Level level7_2_3 = new Level(
    7, 7,
    "  X    " +
    "     X " +
    " X     " +
    "   T   " +
    " X   X " +
    "X     X" +
    "0  X  0"
    );

    final Level level7_4 = new Level(
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
    level5_2,
    level7_2_1,
    level7_2_2,
    level7_2_3,
    level7_4,
    };

}
