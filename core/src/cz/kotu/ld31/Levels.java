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
    "T0 X " +
    "X    " +
    "  0T " +
    "     " +
    " X   "
    );

    /** Nice level! */
    final Level level6_6_3 = new Level(
    6, 6,
    "X    X" +
    "   X  " +
    "X T   " +
    "   T 0" +
    "   TX0" +
    " X   0"
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

    /** Nice level! */
    final Level level7_6_2_11 = new Level(
    7, 6,
    " 0X    " +
    " X   X " +
    "   T   " +
    "    X  " +
    "X     X" +
    " XTX0X "
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

    final Level level7_2_22 = new Level(
    7, 7,
    "  X    " +
    "     X " +
    " X     " +
    " X T   " +
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

    /** Not solvable! */
    final Level level7_2_4 = new Level(
    7, 7,
    "  X    " +
    "    X  " +
    " X     " +
    "   T   " +
    " X   X " +
    "X     X" +
    "0 X X 0"
    );

    /** Nice level! */
    final Level level7_6_2_5 = new Level(
    7, 6,
    "   X  0" +
    "     X " +
    " X  T  " +
    "    T 0" +
    "     X " +
    "  XX   "
    );

    final Level level7_4 = new Level(
    7, 7,
    "   X   " +
    "T0   0T" +
    "       " +
    "X  T  X" +
    "       " +
    " 0   0 " +
    "T  X   "
    );

    final Level[] LIST = new Level[]{
    level0,
    level1,
    level2,
    level5_2,
    level6_6_3,
    level7_6_2_11,
//    level7_2_2,
//    level7_2_22,
//    level7_2_3,
//    level7_2_4,
    level7_6_2_5,
    level7_4,
    };

}
