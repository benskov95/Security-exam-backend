package fetchers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.CatFactDTO;
import dto.ChuckDTO;
import dto.CombinedDTO;
import dto.DadDTO;
import dto.KanyeDTO;
import dto.YesNoDTO;
import java.io.IOException;
import utils.HttpUtils;

import java.util.concurrent.*;

public class ExampleFetcher {
    private static String chuckURL = "https://api.chucknorris.io/jokes/random";
    private static String dadURL = "https://icanhazdadjoke.com";
    private static String catFactURL = "https://meowfacts.herokuapp.com/";
    private static String kanyeURL = "https://api.kanye.rest/";
    private static String yesNoURL = "https://yesno.wtf/api";

    public static String fetchData(ExecutorService threadPool, Gson gson) throws InterruptedException, ExecutionException, TimeoutException, IOException {

        Callable<ChuckDTO> chuckTask = new Callable<ChuckDTO>() {
            @Override
            public ChuckDTO call() throws Exception {
                String chuck = HttpUtils.fetchData(chuckURL);
                ChuckDTO chuckDTO = gson.fromJson(chuck, ChuckDTO.class);
                return chuckDTO;
            }
        };

        Callable<DadDTO> dadTask = new Callable<DadDTO>() {
            @Override
            public DadDTO call() throws Exception {
                String dad = HttpUtils.fetchData(dadURL);
                DadDTO dadDTO = gson.fromJson(dad, DadDTO.class);
                return dadDTO;
            }
        };

        Callable<CatFactDTO> catTask = new Callable<CatFactDTO>() {
            @Override
            public CatFactDTO call() throws Exception {
                String cat = HttpUtils.fetchData(catFactURL);
                CatFactDTO catDTO = gson.fromJson(cat, CatFactDTO.class);
                return catDTO;
            }
        };
        
         Callable<KanyeDTO> kanyeTask = new Callable<KanyeDTO>() {
            @Override
            public KanyeDTO call() throws Exception {
                String kanye = HttpUtils.fetchData(kanyeURL);
                KanyeDTO kanyeDTO = gson.fromJson(kanye, KanyeDTO.class);
                return kanyeDTO;
            }
        };
         
          Callable<YesNoDTO> yesNoTask = new Callable<YesNoDTO>() {
            @Override
            public YesNoDTO call() throws Exception {
                String yesNo = HttpUtils.fetchData(yesNoURL);
                YesNoDTO yesNoDTO = gson.fromJson(yesNo, YesNoDTO.class);
                return yesNoDTO;
            }
        };

        Future<ChuckDTO> futureChuck = threadPool.submit(chuckTask);
        Future<DadDTO> futureDad = threadPool.submit(dadTask);
        Future<CatFactDTO> futureCat = threadPool.submit(catTask);
        Future<KanyeDTO> futureKanye = threadPool.submit(kanyeTask);
        Future<YesNoDTO> futureYesNo = threadPool.submit(yesNoTask);

        ChuckDTO chuck = futureChuck.get(5, TimeUnit.SECONDS);
        DadDTO dad = futureDad.get(5, TimeUnit.SECONDS);
        CatFactDTO cat = futureCat.get(5, TimeUnit.SECONDS);
        KanyeDTO kanye = futureKanye.get(5, TimeUnit.SECONDS);
        YesNoDTO yesNo = futureYesNo.get(5, TimeUnit.SECONDS);

        CombinedDTO combinedDTO = new CombinedDTO(chuck, dad, cat, kanye, yesNo);
        String combinedJson = gson.toJson(combinedDTO);

        return combinedJson;
    }
}
