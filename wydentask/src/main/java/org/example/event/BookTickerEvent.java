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

/**
 * BookTickerEvent event for a symbol. Pushes any update to the best bid or
 * ask's price or quantity in real-time for a specified symbol.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookTickerEvent {

    @JsonIgnore
    private static final MathContext binancePriceNumberContext =
            new MathContext(8, RoundingMode.HALF_DOWN);
    @JsonIgnore
    private static BigDecimal marginPercentage = PropertyLoader.getMarginPercent();

    @JsonProperty("u")
    private long updateId;

    @JsonProperty("s")
    private String symbol;

    @JsonProperty("b")
    private BigDecimal bidPrice;

    @JsonProperty("B")
    private BigDecimal bidQuantity;

    @JsonProperty("a")
    private BigDecimal askPrice;

    @JsonProperty("A")
    private BigDecimal askQuantity;

    @Override
    public String toString() {
        return new StringBuilder()
                .append("\t| updateId: ").append(updateId)
                .append("\t| symbol: ").append(symbol)
                .append("\t| bestBidPrice: ").append(bidPrice == null ? "" : bidPrice.toPlainString())
                .append("\t| bestBidPriceWithMargin: ").append(
                        bidPrice == null ? "" : bidPrice.multiply(BigDecimal.ONE.add(marginPercentage), binancePriceNumberContext).toPlainString())
                .append("\t| bestBidVolume: ").append(bidQuantity == null ? "" : bidQuantity.toPlainString())
                .append("\t| bestAskPrice: ").append(askPrice == null ? "" : askPrice.toPlainString())
                .append("\t| bestAskPriceWithMargin: ").append(
                        //margin during sell (understood as binance fee) is worsening the price of selling even more, than best ask offer (when it comes to final income after tx)
                        askPrice == null ? "" : askPrice.multiply(BigDecimal.ONE.subtract(marginPercentage), binancePriceNumberContext).toPlainString())
                .append("\t| bestAskVolume: ").append(askQuantity == null ? "" : askQuantity.toPlainString())
                .toString();
    }
}

/*
https://binance-docs.github.io/apidocs/spot/en/#individual-symbol-book-ticker-streams

{
  "u":400900217,     // order book updateId
  "s":"BNBUSDT",     // symbol
  "b":"25.35190000", // best bid price
  "B":"31.21000000", // best bid qty
  "a":"25.36520000", // best ask price
  "A":"40.66000000"  // best ask qty
}


 */
