package org.theparanoidtimes.ptping;

import java.util.List;
import java.util.stream.Collectors;

class LinuxPingResult extends PingResult {

    private static final String REGULAR_FIRST_LINE = "(PING )(.*)( \\()(.*)(\\) )(\\d+)(\\()(\\d+)(\\))( bytes of data\\.)";
    private static final String UNKNOWN_HOST_FIRST_LINE = "(ping: unknown host )(.*)";
    private static final String SUCCESSFUL_ATTEMPT_LINE = "(\\d+)( bytes from )(.*)( \\()(.+)(\\))(: icmp_seq=)(\\d+)( ttl=)(\\d+)( time=)([0-9]+\\.[0-9]+)( ms)";
    private static final String PACKAGE_STATISTICS_LINE = "(\\d+)( packets transmitted, )(\\d+)( received, )(\\d+)(% packet loss, time )(\\d+)(ms)";
    private static final String ROUND_TRIP_STATISTICS_LINE = "(rtt min/avg/max/mdev = )([0-9]+\\.[0-9]+)(/)([0-9]+\\.[0-9]+)(/)([0-9]+\\.[0-9]+)(/)([0-9]+\\.[0-9]+)( ms)";

    private static final int PING_TARGET_LOCATION = 2;
    private static final int TARGET_IP_LOCATION = 4;
    private static final int BYTES_LOCATION = 6;
    //private static final int BYTES2_LOCATION = 8;
    private static final int PACKAGES_TRANSMITTED_LOCATION = 1;
    private static final int PACKAGES_RECEIVED_LOCATION = 3;
    private static final int PACKAGES_LOST_PERCENTAGE_LOCATION = 5;
    private static final int PACKAGES_TIME_LOCATION = 7;
    private static final int RTT_MIN_TIME_LOCATION = 2;
    private static final int RTT_AVG_TIME_LOCATION = 4;
    private static final int RTT_MAX_TIME_LOCATION = 6;
    private static final int RTT_MDEV_TIME_LOCATION = 8;

    LinuxPingResult(List<String> resultLines) {
        super(resultLines);
    }

    @Override
    public String getPingTarget() {
        if (isHostUnknown())
            return null;
        return getFromFirstLine(REGULAR_FIRST_LINE, PING_TARGET_LOCATION);
    }

    @Override
    public String getTargetIp() {
        if (isHostUnknown())
            return null;
        return getFromFirstLine(REGULAR_FIRST_LINE, TARGET_IP_LOCATION);
    }

    @Override
    public Integer getPingBytes() {
        if (isHostUnknown())
            return null;
        return toIntValue(getFromFirstLine(REGULAR_FIRST_LINE, BYTES_LOCATION));
    }

    @Override
    public List<AttemptLine> getAttemptLines() {
        if (isHostUnknown())
            return null;
        return resultLines.stream()
                .filter(s -> s.matches(SUCCESSFUL_ATTEMPT_LINE))
                .map(LinuxAttemptLine::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttemptLine> getSuccessfulAttempts() {
        return getAttemptLines();
    }

    @Override
    public boolean isHostUnknown() {
        return firstLine().matches(UNKNOWN_HOST_FIRST_LINE);
    }

    @Override
    public boolean isPartialSuccessful() {
        return false;
    }

    @Override
    public boolean isTimeout() {
        return firstLine().matches(REGULAR_FIRST_LINE)
                && getAttemptLines().isEmpty()
                && getLostPackagesPercentage() == 100
                && getReceivedPackagesCount() == 0;
    }

    @Override
    public boolean isSuccessful() {
        return firstLine().matches(REGULAR_FIRST_LINE)
                && getAttemptLines().stream()
                .allMatch(AttemptLine::isSuccessful);
    }

    @Override
    public Double getRoundTripAverageTime() {
        return toDoubleValue(getFromLine(ROUND_TRIP_STATISTICS_LINE, RTT_AVG_TIME_LOCATION));
    }

    @Override
    public Double getRoundTripMaximumTime() {
        return toDoubleValue(getFromLine(ROUND_TRIP_STATISTICS_LINE, RTT_MAX_TIME_LOCATION));
    }

    @Override
    public Double getRoundTripMinimumTime() {
        return toDoubleValue(getFromLine(ROUND_TRIP_STATISTICS_LINE, RTT_MIN_TIME_LOCATION));
    }

    @Override
    public Double getRoundTripTimeMDevTime() {
        return toDoubleValue(getFromLine(ROUND_TRIP_STATISTICS_LINE, RTT_MDEV_TIME_LOCATION));
    }

    @Override
    public Double getLostPackagesPercentage() {
        return toDoubleValue(getFromLine(PACKAGE_STATISTICS_LINE, PACKAGES_LOST_PERCENTAGE_LOCATION));
    }

    @Override
    public Integer getLostPackagesCount() {
        return null;
    }

    @Override
    public Integer getReceivedPackagesCount() {
        return toIntValue(getFromLine(PACKAGE_STATISTICS_LINE, PACKAGES_RECEIVED_LOCATION));
    }

    @Override
    public Integer getSentPackagesCount() {
        return toIntValue(getFromLine(PACKAGE_STATISTICS_LINE, PACKAGES_TRANSMITTED_LOCATION));
    }

    @Override
    public Double getPackageTransmissionTime() {
        return toDoubleValue(getFromLine(PACKAGE_STATISTICS_LINE, PACKAGES_TIME_LOCATION));
    }

    private class LinuxAttemptLine extends AttemptLine {

        private final int BYTES_LOCATION = 1;
        private final int RESPONSE_ADDRESS_LOCATION = 3;
        private final int RESPONSE_IP_LOCATION = 5;
        private final int ICMP_SEQ_LOCATION = 8;
        private final int TTL_LOCATION = 10;
        private final int TIME_LOCATION = 12;

        LinuxAttemptLine(String line) {
            super(line);
        }

        @Override
        public boolean isTimeout() {
            return false;
        }

        @Override
        public boolean isSuccessful() {
            return true;
        }

        @Override
        public Integer getTTL() {
            return toIntValue(doGetGroup(line, SUCCESSFUL_ATTEMPT_LINE, TTL_LOCATION));
        }

        @Override
        public Double getTime() {
            return toDoubleValue(doGetGroup(line, SUCCESSFUL_ATTEMPT_LINE, TIME_LOCATION));
        }

        @Override
        public Integer getBytes() {
            return toIntValue(doGetGroup(line, SUCCESSFUL_ATTEMPT_LINE, BYTES_LOCATION));
        }

        @Override
        public String getResponseAddress() {
            return doGetGroup(line, SUCCESSFUL_ATTEMPT_LINE, RESPONSE_ADDRESS_LOCATION);
        }

        @Override
        public String getResponseIp() {
            return doGetGroup(line, SUCCESSFUL_ATTEMPT_LINE, RESPONSE_IP_LOCATION);
        }

        @Override
        public Integer getICMPSeqNumber() {
            return toIntValue(doGetGroup(line, SUCCESSFUL_ATTEMPT_LINE, ICMP_SEQ_LOCATION));
        }
    }
}
