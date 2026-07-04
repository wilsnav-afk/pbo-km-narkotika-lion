package util;

import java.util.Scanner;

/**
 * Static helpers for reading validated input from the console.
 *
 * Every method here loops until the user types something acceptable,
 * catching the parse exceptions internally. Callers therefore always
 * get back a usable value and never have to wrap their own try-catch
 * around keyboard input — that is the whole point of this class.
 */
public final class InputHandler {

    private InputHandler() {
        // utility class, no instances needed
    }

    /** Keeps asking until the user enters a valid whole number. */
    public static int readInt(String prompt, Scanner scanner) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine().trim();
            try {
                return Integer.parseInt(raw);
            } catch (NumberFormatException e) {
                System.out.println("  ! \"" + raw + "\" is not a whole number, try again.");
            }
        }
    }

    /** Keeps asking until the user enters a valid decimal number. */
    public static double readDouble(String prompt, Scanner scanner) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine().trim();
            try {
                return Double.parseDouble(raw);
            } catch (NumberFormatException e) {
                System.out.println("  ! \"" + raw + "\" is not a number, try again.");
            }
        }
    }

    /** Keeps asking until the user types something that isn't blank. */
    public static String readNonEmpty(String prompt, Scanner scanner) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine().trim();
            if (!raw.isEmpty()) {
                return raw;
            }
            System.out.println("  ! Input cannot be empty, try again.");
        }
    }

    /**
     * Reads a menu choice and rejects anything outside [min, max].
     * Builds on readInt, so non-numeric input is already handled.
     */
    public static int readChoice(String prompt, int min, int max, Scanner scanner) {
        while (true) {
            int value = readInt(prompt, scanner);
            if (value >= min && value <= max) {
                return value;
            }
            System.out.println("  ! Please pick a number between " + min + " and " + max + ".");
        }
    }
}
