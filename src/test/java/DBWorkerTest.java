import com.fish.core.DBManager;
import com.fish.core.DBWorker;
import org.junit.Test;

import java.io.File;

/**
 * @author Fish
 * created by 2018-05-26 12:27
 */
public class DBWorkerTest
{
    @Test
    public void testQueryStrings()
    {
        DBManager.init(new File("DB.properties"));
        DBWorker dbWorker = DBManager.getDBWorker();

        System.out.println(dbWorker.queryStrings("book"));
    }
}
