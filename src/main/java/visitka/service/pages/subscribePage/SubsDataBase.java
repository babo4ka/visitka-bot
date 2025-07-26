package visitka.service.pages.subscribePage;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


public class SubsDataBase {

    @Getter
    private static final List<Long> subs = new ArrayList<>();

    static Logger logger = LogManager.getLogger();

    public static void add(long id){
        subs.add(id);
        logger.info("подписался {}", id);
    }

    public static void remove(long id){
        subs.remove(id);
        logger.info("отписался {}", id);
    }

    public static boolean contains(long id){return subs.contains(id);}
}
