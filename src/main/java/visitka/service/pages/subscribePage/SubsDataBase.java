package visitka.service.pages.subscribePage;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class SubsDataBase {

    @Getter
    private static final List<Long> subs = new ArrayList<>();

    public static void add(long id){subs.add(id);}

    public static void remove(long id){subs.remove(id);}

    public static boolean contains(long id){return subs.contains(id);}
}
