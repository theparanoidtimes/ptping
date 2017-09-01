package org.theparanoidtimes.ptping;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public abstract class PingResult {

    protected final List<String> resultLines;

    PingResult(List<String> resultLines) {
        this.resultLines = resultLines.stream()
                .filter(s -> (s != null && !s.isEmpty()))
                .map(String::trim)
                .collect(toList());
    }

    public String getCompleteOutput() {
        return resultLines.stream()
                .collect(Collectors.joining("\n"));
    }

    public abstract String getPingTarget();

    public abstract String getTargetIp();

    public abstract Integer getPingBytes();

    public abstract List<AttemptLine> getAttemptLines();

    public abstract List<AttemptLine> getSuccessfulAttempts();

    public abstract boolean isHostUnknown();

    public abstract boolean isPartialSuccessful();

    public abstract boolean isTimeout();

    public abstract boolean isSuccessful();

    public abstract Double getRoundTripAverageTime();

    public abstract Double getRoundTripMaximumTime();

    public abstract Double getRoundTripMinimumTime();

    public abstract Double getRoundTripTimeMDevTime();

    public abstract Double getLostPackagesPercentage();

    public abstract Integer getLostPackagesCount();

    public abstract Integer getReceivedPackagesCount();

    public abstract Integer getSentPackagesCount();

    public abstract Double getPackageTransmissionTime();

    @Override
    public String toString() {
        return getCompleteOutput();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PingResult that = (PingResult) o;

        return resultLines.equals(that.resultLines);
    }

    @Override
    public int hashCode() {
        return resultLines.hashCode();
    }

    protected String doGetGroup(String input, String match, int groupNumber) {
        Matcher matcher = Pattern.compile(match).matcher(input);
        if (matcher.matches())
            return matcher.group(groupNumber);
        return null;
    }

    protected Integer toIntValue(String input) {
        return input != null ? Integer.parseInt(input) : null;
    }

    protected Double toDoubleValue(String input) {
        return input != null ? Double.parseDouble(input) : null;
    }

    protected String getFromLine(String lineMatch, int group) {
        if (isHostUnknown())
            return null;
        return resultLines.stream()
                .filter(s1 -> s1.matches(lineMatch))
                .findFirst()
                .map(s -> doGetGroup(s, lineMatch, group))
                .orElse(null);
    }

    protected String getFromFirstLine(String match, int group) {
        return doGetGroup(firstLine(), match, group);
    }

    protected String firstLine() {
        return resultLines.get(0);
    }

    public abstract class AttemptLine {

        protected final String line;

        AttemptLine(String line) {
            this.line = line;
        }

        @Override
        public String toString() {
            return line;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AttemptLine that = (AttemptLine) o;

            return line != null ? line.equals(that.line) : that.line == null;
        }

        @Override
        public int hashCode() {
            return line != null ? line.hashCode() : 0;
        }

        public abstract boolean isTimeout();

        public abstract boolean isSuccessful();

        public abstract Integer getTTL();

        public abstract Double getTime();

        public abstract Integer getBytes();

        public abstract String getResponseAddress();

        public abstract String getResponseIp();

        public abstract Integer getICMPSeqNumber();
    }
}
