package visitka.service.pages.investPage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.data.xy.DefaultOHLCDataset;
import org.jfree.data.xy.OHLCDataItem;
import org.jfree.data.xy.OHLCDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;
import utils.messages.MessageBuilder;
import utils.messages.keyboard.InlineKeyboardBuilder;
import utils.pages.interfaces.Page;
import utils.tuples.Pair;
import visitka.invest.DataLoader;
import visitka.utils.Emoji;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

@Component
public class InvestPage implements Page {

    private final String [] tickers = {"SBERP", "TATNP", "MOEX", "GAZP", "PIKK"};

    public static final String NAME = "/invest";

    @Autowired
    DataLoader dataLoader;

    private final Random random = new Random();

    private final Logger logger = LogManager.getLogger();

    @Override
    public List<Pair<PartialBotApiMethod<Message>, Boolean>> executeCallback(Update update) throws TelegramApiException {
        logger.info("{} вызвал через кнопку команду /invest", update.getCallbackQuery().getMessage().getChat().getUserName());

        MessageBuilder messageBuilder = new MessageBuilder();
        InlineKeyboardBuilder keyboardBuilder = new InlineKeyboardBuilder();
        keyboardBuilder = keyboardBuilder.addButton(Emoji.UNAMUSED.emoji() + " На главную", "/start").nextRow();

        String ticker = tickers[random.nextInt(0 ,7)];

        String info;
        var candles = dataLoader.loadCandlesData(ticker);

        InputFile picture;

        try {
            info = dataLoader.infoByTicker(ticker);
            picture = visualizeCandles(ticker, candles);
        } catch (ExecutionException | InterruptedException | IOException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }

        SendPhoto photo = messageBuilder.createPhotoMessage(keyboardBuilder.build(), update.getCallbackQuery().getMessage().getChatId(),
                info, picture);

        return Stream.of(photo).map(e -> new Pair<PartialBotApiMethod<Message>, Boolean>(e, true)).toList();
    }


    private InputFile visualizeCandles(String ticker, List<HistoricCandle> candles) throws IOException {
        var barSeries = dataLoader.getBarSeries(ticker, candles);

        OHLCDataset candleDataset = createCandleDataset(barSeries);

        XYPlot plot = new XYPlot(candleDataset, new DateAxis("Дата"), new NumberAxis("Цена"), null);

        plot.setRenderer(new CandlestickRenderer());
        setBackgroundColor(plot);

        ((NumberAxis) plot.getRangeAxis()).setAutoRangeIncludesZero(false);

        JFreeChart chart = new JFreeChart("Свечной график " + ticker, JFreeChart.DEFAULT_TITLE_FONT, plot, true);

        BufferedImage image = chart.createBufferedImage(2000, 1000);

        ByteArrayOutputStream chartOutput = new ByteArrayOutputStream();
        ImageIO.write(image, "png", chartOutput);
        chartOutput.close();

        return new InputFile(new ByteArrayInputStream(chartOutput.toByteArray()), "file");
    }

    private void setBackgroundColor(XYPlot plot){
        ZonedDateTime time = ZonedDateTime.now();
        int hour = time.getHour();

        if(hour >= 6 && hour <= 17){
            plot.setBackgroundPaint(new Color(255, 255, 255));
        }else{
            plot.setBackgroundPaint(new Color(61, 58, 58));
        }
    }

    private OHLCDataset createCandleDataset(BarSeries series){
        return new DefaultOHLCDataset(
                series.getName(),
                series.getBarData().stream()
                        .map(bar -> new OHLCDataItem(
                                Date.from(bar.getEndTime()),
                                bar.getOpenPrice().doubleValue(),
                                bar.getHighPrice().doubleValue(),
                                bar.getLowPrice().doubleValue(),
                                bar.getClosePrice().doubleValue(),
                                bar.getVolume().doubleValue()
                        )).toArray(OHLCDataItem[]::new)
        );
    }
}
