package org.poo.exchange;
import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.ExchangeInput;

@Getter
@Setter
public final class Exchange {
    private final String from;
    private final String to;
    private final double rate;
    private final int timestamp;
    public Exchange(final ExchangeInput exchangeInput) {
        from = exchangeInput.getFrom();
        to = exchangeInput.getTo();
        rate = exchangeInput.getRate();
        timestamp = exchangeInput.getTimestamp();
    }
}
