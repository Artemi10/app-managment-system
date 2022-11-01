package com.devanmejia.appmanager.transfer.stat;

import java.time.OffsetDateTime;
import java.util.function.Function;

public enum StatType {
    MONTH("monthStatService", (date) -> date.plusYears(1)),
    DAY("dayStatService", (date) -> date.plusMonths(1)),
    HOUR("hourStatService", (date) -> date.plusDays(1));

    private final String statServiceName;
    private final Function<OffsetDateTime, OffsetDateTime> defaultDurationGenerator;

    StatType(String statServiceName, Function<OffsetDateTime, OffsetDateTime> defaultDurationGenerator) {
        this.statServiceName = statServiceName;
        this.defaultDurationGenerator = defaultDurationGenerator;
    }

    public String getStatServiceName() {
        return statServiceName;
    }

    public Function<OffsetDateTime, OffsetDateTime> getDefaultDurationGenerator() {
        return defaultDurationGenerator;
    }
}
