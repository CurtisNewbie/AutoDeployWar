package com.curtisnewbie;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Scanner;

public class App {

    public static final Scanner sc = new Scanner(System.in);

    /** path to wildfly/ JBOSS-cli */
    private static String pathToCli;
    /** path to WAR, if it's currently not there, it doesn't matter */
    private static String warPath;
    private static String wildflyCmd;
    private static Runtime runtime;
    private static long lastChanged = -1;
    private static File warFile;

    public static void main(String[] args) throws Exception {

        String[] config = readConfig();
        String toCli = config[0];
        String toWar = config[1];
        if (toWar == null || toWar.isEmpty() || toCli == null || toCli.isEmpty()) {
            System.out.println("Path not provided");
            System.exit(0);
        } else {
            init(toWar, toCli);
            System.out.println("Starts listening to changes. \nWar: " + toWar + "\nCLI: " + toCli);
            detectChange();
        }
    }

    static void init(String toWar, String toCli) {
        warPath = toWar;
        warFile = new File(warPath);
        pathToCli = toCli;
        wildflyCmd = pathToCli + " --connect --command='deploy --force " + warPath + "';";
        runtime = Runtime.getRuntime();
    }

    static void detectChange() throws Exception {
        while (true) {
            if (lastChanged < 0) {
                lastChanged = getLastChangedTime();
            } else {
                long latest = getLastChangedTime();
                if (latest > lastChanged) {
                    lastChanged = latest;
                    System.out.println("- Change detected " + new Date());
                    deploy();
                }
            }
            Thread.sleep(500);
        }
    }

    static long getLastChangedTime() {
        return warFile.lastModified();
    }

    static void deploy() throws IOException {
        // deploy
        Process process = runtime.exec(new String[] { "bash", "-c", wildflyCmd });
        try {
            if (process.waitFor() == 0)
                System.out.println("WAR file has been deployed to the Wildfly server");
            else {
                System.out.println("Failed to deploy the war file to the server.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static String[] readConfig() {
        InputStream in = new App().getClass().getClassLoader().getResourceAsStream("config.txt");
        String[] res = new String[2];
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in));) {
            res[0] = br.readLine().substring(12);
            res[1] = br.readLine().substring(4);
        } catch (IOException e) {
            System.out.println("Failed to read config.");
            System.exit(0);
        }
        return res;
    }
}
