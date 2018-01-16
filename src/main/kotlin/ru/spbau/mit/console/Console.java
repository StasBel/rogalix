package ru.spbau.mit.console;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class storing some useful static functions very helpful on working with terminal.
 */
public class Console {

    private static String ttyConfig;

    public static void main(String[] args) {

        try {
            setTerminalToCBreak();

            int i = 0;
            while (true) {

                System.out.println("" + i++);

                if (System.in.available() != 0) {
                    int c = System.in.read();
                    if (c == 0x1B) {
                        break;
                    }
                }

            } // end while
        } catch (IOException e) {
            System.err.println("IOException");
        } catch (InterruptedException e) {
            System.err.println("InterruptedException");
        } finally {
            try {
                stty(ttyConfig.trim());
            } catch (Exception e) {
                System.err.println("Exception restoring tty config");
            }
        }

    }

    /**
     * Wait for the next keyboard pressing char in non-blocking mode.
     *
     * @return pressed char
     */
    public static char getCh() {
        int res = 0x1B;

        try {
            setTerminalToCBreak();

            while (true) {
                if (System.in.available() != 0) {
                    int c = System.in.read();
                    res = (char) c;
                    break;
                }
            } // end while
        } catch (IOException e) {
            System.err.println("IOException");
        } catch (InterruptedException e) {
            System.err.println("InterruptedException");
        } finally {
            try {
                stty(ttyConfig.trim());
            } catch (Exception e) {
                System.err.println("Exception restoring tty config");
            }
        }

        return (char) res;
    }

    private static void setTerminalToCBreak() throws IOException, InterruptedException {

        ttyConfig = stty("-g");

        // set the console to be character-buffered instead of line-buffered
        stty("-icanon min 1");

        // disable character echoing
        stty("-echo");
    }

    /**
     * Execute the stty command with the specified arguments
     * against the current active terminal.
     *
     * @param args arguments passed to execute
     */
    private static String stty(final String args)
            throws IOException, InterruptedException {
        String cmd = "stty " + args + " < /dev/tty";

        return exec(new String[]{
                "sh",
                "-c",
                cmd
        });
    }

    /**
     * Execute the specified command and return the output
     * (both stdout and stderr).
     *
     * @param cmd command to execute
     */
    private static String exec(final String[] cmd)
            throws IOException, InterruptedException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        Process p = Runtime.getRuntime().exec(cmd);
        int c;
        InputStream in = p.getInputStream();

        while ((c = in.read()) != -1) {
            bout.write(c);
        }

        in = p.getErrorStream();

        while ((c = in.read()) != -1) {
            bout.write(c);
        }

        p.waitFor();

        return new String(bout.toByteArray());
    }

    /**
     * Clears the console output. Should works on both UNIX* and WIN.
     */
    public static void clearConsole() {
        System.out.print("\033[H\033[2J");
    }

}
