package test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Java8 {
    public static int swich() {
        return switch (new Random().nextInt(30)) {
            case 0 -> 4;
            case 1 -> 23;
            case 2, 3, 4 -> 56;
            case 10, 19 -> 2301;
            default -> 157;
        };
    }

    public static List<Java8> diamond() {
        return new ArrayList<>() {};
    }
}
