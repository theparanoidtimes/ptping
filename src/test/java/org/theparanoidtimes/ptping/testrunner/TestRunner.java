package org.theparanoidtimes.ptping.testrunner;

import org.theparanoidtimes.ptping.PingCommandRunner;
import org.theparanoidtimes.ptping.PingResult;

import java.util.List;

public class TestRunner {

    public static void main(String[] args) {
        pingSingleTarget();
        pingMultipleTargets();
    }

    private static void pingSingleTarget() {
        PingResult pingResult = PingCommandRunner.executePing("google.com");
        System.out.println(pingResult.getCompleteOutput());

        System.out.println(pingResult.getPingTarget());
        System.out.println(pingResult.getTargetIp());
        System.out.println(pingResult.getPingBytes());
        System.out.println(pingResult.getSentPackagesCount());
        System.out.println(pingResult.getReceivedPackagesCount());
        System.out.println(pingResult.getLostPackagesCount());
        System.out.println(pingResult.getLostPackagesPercentage());
        System.out.println(pingResult.getPackageTransmissionTime());
        System.out.println(pingResult.getRoundTripMinimumTime());
        System.out.println(pingResult.getRoundTripMaximumTime());
        System.out.println(pingResult.getRoundTripAverageTime());
        System.out.println(pingResult.getRoundTripTimeMDevTime());
        System.out.println(pingResult.getAttemptLines());
        pingResult.getAttemptLines().forEach(attemptLine ->
                System.out.println(attemptLine.isSuccessful()
                        + " " + attemptLine.isTimeout()
                        + " " + attemptLine.getBytes()
                        + " " + attemptLine.getTime()
                        + " " + attemptLine.getTTL()
                        + " " + attemptLine.getResponseAddress()
                        + " " + attemptLine.getResponseIp()
                        + " " + attemptLine.getICMPSeqNumber()));
        System.out.println(pingResult.getSuccessfulAttempts());
        System.out.println(pingResult.isSuccessful());
        System.out.println(pingResult.isTimeout());
        System.out.println(pingResult.isHostUnknown());
        System.out.println(pingResult.isPartialSuccessful());
    }

    private static void pingMultipleTargets() {
        List<PingResult> pingResults = PingCommandRunner.executePingToTargets("yahoo.com", "hotmail.com");
        System.out.println(pingResults);
    }
}
