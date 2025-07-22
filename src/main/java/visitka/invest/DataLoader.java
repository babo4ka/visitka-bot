package visitka.invest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.bars.TimeBarBuilder;
import org.ta4j.core.num.DecimalNum;
import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;
import ru.tinkoff.piapi.core.InvestApi;
import visitka.invest.config.InvestConfig;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
public class DataLoader {

    final InvestConfig config;

    final InvestApi api;

    private final Logger logger = LogManager.getLogger();

    @Autowired
    public DataLoader(InvestConfig config){
        this.config = config;

        api = InvestApi.createSandbox(config.getSandboxToken());
    }

    public List<HistoricCandle> loadCandlesData(String ticker){
        String figi = getFigiForShare(ticker);

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime from = now.minusDays(200);

        var unMutableList = api.getMarketDataService().getCandles(figi,
                from.toInstant(), now.toInstant(), CandleInterval.CANDLE_INTERVAL_DAY).join();

        logger.info("Получил свечи по {}, количество: {}", ticker, unMutableList.size());

        return new ArrayList<>(unMutableList);
    }

    public BarSeries getBarSeries(String ticker, List<HistoricCandle> candles){
        BarSeries series = new BaseBarSeriesBuilder().withName(ticker).build();

        Duration duration = Duration.ofDays(1);

        for(var candle : candles){
            Instant endTime = ZonedDateTime.ofInstant(
                    Instant.ofEpochSecond(candle.getTime().getSeconds(), candle.getTime().getNanos()),
                    ZoneId.of("Europe/Moscow")).toInstant();
            series.addBar(new TimeBarBuilder()
                    .openPrice(DecimalNum.valueOf(candle.getOpen().getUnits() + candle.getOpen().getNano()/1e9))
                    .closePrice(DecimalNum.valueOf(candle.getClose().getUnits() + candle.getClose().getNano()/1e9))
                    .highPrice(DecimalNum.valueOf(candle.getHigh().getUnits() + candle.getHigh().getNano()/1e9))
                    .lowPrice(DecimalNum.valueOf(candle.getLow().getUnits() + candle.getLow().getNano()/1e9))
                    .volume(DecimalNum.valueOf(candle.getVolume()))
                    .timePeriod(duration)
                    .endTime(endTime)
                    .build());
        }

        logger.info("Получил серию баров по {}, количество: {}", ticker, series.getBarCount());

        return series;
    }


    public String infoByTicker(String ticker) throws ExecutionException, InterruptedException {
        String figi = getFigiForShare(ticker);

        var instrumentsService = api.getInstrumentsService();
        var name = instrumentsService.getShareByTicker(ticker, "TQBR").join().getName();

        var price = api.getMarketDataService().getLastPrices(List.of(figi)).get().get(0).getPrice();
        double priceDouble = price.getUnits() + price.getNano()/1e9;

        String info = ticker + "\n" +
                name + "\n" +
                priceDouble;

        logger.info("Получил информацию по {}:\n {}", ticker, info);

        return info;
    }


    private String getFigiForShare(String ticker){
        return api.getInstrumentsService().getShareByTicker(ticker, "TQBR").join().getFigi();
    }

}
