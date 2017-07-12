package com.paranoidtimes.ptping;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PingCommandRunner {

    private enum OS {LINUX, WINDOWS, UNKNOWN}

    private static final String PING_COMMAND_KEYWORD = "ping";

    public static PingResult executePing(String target) throws PingCommandRunnerException {
        try {
            return executeForOS(target, getOsType());
        } catch (Exception e) {
            throw new PingCommandRunnerException(e);
        }
    }

    public static List<PingResult> executePingToTargets(String firstTarget, String... otherTargets) throws PingCommandRunnerException {
        try {
            final List<PingResult> results = new ArrayList<>();
            results.add(executePing(firstTarget));
            Arrays.stream(otherTargets).forEach(s -> {
                try {
                    results.add(executePing(s));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            return results;
        } catch (Exception e) {
            throw new PingCommandRunnerException(e);
        }
    }

    private static PingResult executeForOS(String target, OS osType) throws Exception {
        Process process;
        switch (osType) {
            case LINUX:
                process = Runtime.getRuntime().exec(linuxPingCommand(target));
                return new LinuxPingResult(getProcessOutput(process));
            case WINDOWS:
                process = Runtime.getRuntime().exec(windowsPingCommand(target));
                return new WindowsPingResult(getProcessOutput(process));
            default:
                throw new IllegalStateException("Unsupported OS!");
        }
    }

    private static List<String> getProcessOutput(Process process) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        List<String> resultLines = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            resultLines.add(line);
        }
        process.waitFor();
        return resultLines;
    }

    private static String windowsPingCommand(String target) {
        return PING_COMMAND_KEYWORD + " -n 5 " + target;
    }

    private static String[] linuxPingCommand(String target) {
        return new String[]{PING_COMMAND_KEYWORD, "-c", "5", target};
    }

    private static OS getOsType() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win"))
            return OS.WINDOWS;
        else if (os.contains("nix") || os.contains("nux") || os.contains("aix"))
            return OS.LINUX;
        else
            return OS.UNKNOWN;
    }
}
