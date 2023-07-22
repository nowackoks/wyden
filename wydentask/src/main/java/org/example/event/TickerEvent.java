package org.example.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.example.PropertyLoader;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Date;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public final class TickerEvent {

    @JsonIgnore
    private static final MathContext binancePriceNumberContext =
            new MathContext(8, RoundingMode.HALF_DOWN);
    @JsonIgnore
    private static BigDecimal marginPercentage = PropertyLoader.getMarginPercent();


    @JsonProperty("e")
    private String eventType;

    @JsonProperty("E")
    private long eventTime;

    @JsonProperty("s")
    private String symbol;

    @JsonProperty("p")
    private BigDecimal priceChange;

    @JsonProperty("v")
    private BigDecimal totalTradedBaseAssetVolume;

    @JsonProperty("q")
    private BigDecimal totalTradedQuoteAssetVolume;

    @Override
    public String toString() {
        return new StringBuilder()
                .append("\t| eventType: ").append(eventType)
                .append("\t| eventTime: ").append(new Date(eventTime))
                .append("\t| symbol: ").append(symbol)
                .append("\t| priceWithoutMargin: ").append(priceChange == null ? "" : priceChange.toPlainString())
                .append("\t| priceWithMargin: ").append(priceChange == null ? "" : priceChange.multiply(BigDecimal.ONE.add(marginPercentage), binancePriceNumberContext))
                .append("\t| totalTradedBaseAssetVolume: ").append(totalTradedBaseAssetVolume == null ? "" : totalTradedBaseAssetVolume.toPlainString())
                .append("\t| totalTradedQuoteAssetVolume: ").append(totalTradedQuoteAssetVolume == null ? "" : totalTradedQuoteAssetVolume.toPlainString())
                .toString();
    }
}
