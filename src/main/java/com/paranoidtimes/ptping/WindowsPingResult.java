package com.paranoidtimes.ptping;

import java.util.List;

import static java.util.stream.Collectors.toList;

class WindowsPingResult extends PingResult {

    private static final String UNKNOWN_HOST_RESULT_FIRST_LINE = "(Ping request could not find host )(.*)(\\. Please check the name and try again\\.)";
    private static final String REGULAR_RESULT_FIRST_LINE = "(Pinging )(.*)( \\[)(.*)(\\] with )(\\d+)( bytes of data:)";
    private static final String SUCCESS_ATTEMPT_LINE = "(Reply from )(.*)(: bytes=)(\\d+)( time(=|<))(\\d+)(ms)( TTL=)(\\d+)";
    private static final String TIMEOUT_ATTEMPT_LINE = "Request timed out\\.";
    private static final String PACKET_STATISTICS_LINE = "(Packets: Sent = )(\\d+)(, Received = )(\\d+)(, Lost = )(\\d+)( \\()(\\d+)(% loss\\),)";
    private static final String ROUND_TRIP_STATISTICS_LINE = "(Minimum = )(\\d+)(ms, Maximum = )(\\d+)(ms, Average = )(\\d+)(ms)";

    private static final int UNKNOWN_HOST_RESULT_FIRST_LINE_TARGET_LOCATION = 2;
    private static final int REGULAR_RESULT_FIRST_LINE_TARGET_LOCATION = 2;
    private static final int REGULAR_RESULT_FIRST_LINE_TARGET_IP_LOCATION = 4;
    private static final int REGULAR_RESULT_FIRST_LINE_PING_BYTES_LOCATION = 6;
    private static final int PACKET_STATISTICS_LINE_SENT_PACKAGES_LOCATION = 2;
    private static final int PACKET_STATISTICS_LINE_RECEIVED_PACKAGES_LOCATION = 4;
    private static final int PACKET_STATISTICS_LINE_LOST_PACKAGES_LOCATION = 6;
    private static final int PACKET_STATISTICS_LINE_LOST_PACKAGES_PERCENTAGE_LOCATION = 8;
    private static final int ROUND_TRIP_LINE_MINIMUM_LOCATION = 2;
    private static final int ROUND_TRIP_LINE_MAXIMUM_LOCATION = 4;
    private static final int ROUND_TRIP_LINE_AVERAGE_LOCATION = 6;

    WindowsPingResult(List<String> resultLines) {
        super(resultLines);
    }

    @Override
    public List<AttemptLine> getSuccessfulAttempts() {
        return getAttemptLines().stream()
                .filter(AttemptLine::isSuccessful)
                .collect(toList());
    }

    @Override
    public String getPingTarget() {
        if (isHostUnknown())
            return getFromFirstLine(UNKNOWN_HOST_RESULT_FIRST_LINE, UNKNOWN_HOST_RESULT_FIRST_LINE_TARGET_LOCATION);
        else
            return getFromFirstLine(REGULAR_RESULT_FIRST_LINE, REGULAR_RESULT_FIRST_LINE_TARGET_LOCATION);
    }

    @Override
    public String getTargetIp() {
        if (isHostUnknown())
            return null;
        return getFromFirstLine(REGULAR_RESULT_FIRST_LINE, REGULAR_RESULT_FIRST_LINE_TARGET_IP_LOCATION);
    }

    @Override
    public Integer getPingBytes() {
        if (isHostUnknown())
            return null;
        return toIntValue(getFromFirstLine(REGULAR_RESULT_FIRST_LINE, REGULAR_RESULT_FIRST_LINE_PING_BYTES_LOCATION));
    }

    @Override
    public Integer getSentPackagesCount() {
        return toIntValue(getFromLine(PACKET_STATISTICS_LINE, PACKET_STATISTICS_LINE_SENT_PACKAGES_LOCATION));
    }

    @Override
    public Integer getReceivedPackagesCount() {
        return toIntValue(getFromLine(PACKET_STATISTICS_LINE, PACKET_STATISTICS_LINE_RECEIVED_PACKAGES_LOCATION));
    }

    @Override
    public Integer getLostPackagesCount() {
        return toIntValue(getFromLine(PACKET_STATISTICS_LINE, PACKET_STATISTICS_LINE_LOST_PACKAGES_LOCATION));
    }

    @Override
    public Double getLostPackagesPercentage() {
        return toDoubleValue(getFromLine(PACKET_STATISTICS_LINE, PACKET_STATISTICS_LINE_LOST_PACKAGES_PERCENTAGE_LOCATION));
    }

    @Override
    public Double getPackageTransmissionTime() {
        return null;
    }

    @Override
    public Double getRoundTripMinimumTime() {
        return toDoubleValue(getFromLine(ROUND_TRIP_STATISTICS_LINE, ROUND_TRIP_LINE_MINIMUM_LOCATION));
    }

    @Override
    public Double getRoundTripMaximumTime() {
        return toDoubleValue(getFromLine(ROUND_TRIP_STATISTICS_LINE, ROUND_TRIP_LINE_MAXIMUM_LOCATION));
    }

    @Override
    public Double getRoundTripAverageTime() {
        return toDoubleValue(getFromLine(ROUND_TRIP_STATISTICS_LINE, ROUND_TRIP_LINE_AVERAGE_LOCATION));
    }

    @Override
    public Double getRoundTripTimeMDevTime() {
        return null;
    }

    @Override
    public List<AttemptLine> getAttemptLines() {
        return resultLines.stream()
                .filter(s -> s.matches(SUCCESS_ATTEMPT_LINE) || s.matches(TIMEOUT_ATTEMPT_LINE))
                .map(WindowsAttemptLine::new)
                .collect(toList());
    }

    @Override
    public boolean isSuccessful() {
        return !getAttemptLines().isEmpty() &&
                getAttemptLines().stream().filter(s -> !s.isSuccessful()).collect(toList()).isEmpty();
    }

    @Override
    public boolean isTimeout() {
        return !getAttemptLines().isEmpty() &&
                getAttemptLines().stream().filter(s -> !s.isTimeout()).collect(toList()).isEmpty();
    }

    @Override
    public boolean isPartialSuccessful() {
        return !getAttemptLines().isEmpty()
                && getAttemptLines().stream().anyMatch(attemptLine -> isSuccessful())
                && getAttemptLines().stream().anyMatch(attemptLine -> isTimeout());
    }

    @Override
    public boolean isHostUnknown() {
        return firstLine().matches(UNKNOWN_HOST_RESULT_FIRST_LINE);
    }

    private class WindowsAttemptLine extends AttemptLine {

        private static final int BYTES_LOCATION = 4;
        private static final int TIME_LOCATION = 7;
        private static final int TTL_LOCATION = 10;
        private static final int RESPONSE_ADDRESS_LOCATION = 2;

        WindowsAttemptLine(String line) {
            super(line);
        }

        @Override
        public boolean isTimeout() {
            return line.matches(TIMEOUT_ATTEMPT_LINE);
        }

        @Override
        public boolean isSuccessful() {
            return line.matches(SUCCESS_ATTEMPT_LINE);
        }

        @Override
        public Integer getBytes() {
            return toIntValue(getFromLine(BYTES_LOCATION));
        }

        @Override
        public Double getTime() {
            return toDoubleValue(getFromLine(TIME_LOCATION));
        }

        @Override
        public Integer getTTL() {
            return toIntValue(getFromLine(TTL_LOCATION));
        }

        @Override
        public String getResponseAddress() {
            return getFromLine(RESPONSE_ADDRESS_LOCATION);
        }

        @Override
        public String getResponseIp() {
            return null;
        }

        @Override
        public Integer getICMPSeqNumber() {
            return null;
        }

        private String getFromLine(int group) {
            if (isTimeout())
                return null;
            return doGetGroup(line, SUCCESS_ATTEMPT_LINE, group);
        }
    }
}
