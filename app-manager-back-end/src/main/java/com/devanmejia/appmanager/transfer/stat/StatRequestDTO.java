package com.devanmejia.appmanager.transfer.stat;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StatRequestDTO {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime from;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime to;
    private String timeZone;
    @NotNull
    private StatType type;

    public StatRequestDTO(OffsetDateTime from, OffsetDateTime to) {
        this.from = from.toLocalDateTime();
        this.to = to.toLocalDateTime();
        this.timeZone = from.getOffset().toString();
    }

    public OffsetDateTime getFrom() {
        if (from != null) {
            return OffsetDateTime.of(from, ZoneOffset.of(timeZone));
        }
        return OffsetDateTime.now(ZoneOffset.of(timeZone));
    }

    public OffsetDateTime getTo() {
        if (to != null) {
            return OffsetDateTime.of(to, ZoneOffset.of(timeZone));
        }
        return type.getDefaultDurationGenerator().apply(getFrom());
    }
}
