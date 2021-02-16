package dto;

public class CombinedDTO {
    
    private String chuckJoke;
    private String chuckRef;
    private String dadJoke;
    private String dadRef;
    private String catFact;
    private String quote;
    private String answer;
    private String image;

    public CombinedDTO(ChuckDTO chuckDTO, DadDTO dadDTO, CatFactDTO catDTO, KanyeDTO kanyeDTO, YesNoDTO yesDTO) {
        this.chuckJoke = chuckDTO.getValue();
        this.chuckRef = "https://api.chucknorris.io/jokes/random";
        this.dadJoke = dadDTO.getJoke();
        this.dadRef = "https://icanhazdadjoke.com";
        this.catFact = catDTO.getData().get(0);
        this.quote = kanyeDTO.getQuote();
        this.answer = yesDTO.getAnswer();
        this.image = yesDTO.getImage();

    }

    public String getChuckJoke() {
        return chuckJoke;
    }

    public void setChuckJoke(String chuckJoke) {
        this.chuckJoke = chuckJoke;
    }

    public String getChuckRef() {
        return chuckRef;
    }

    public void setChuckRef(String chuckRef) {
        this.chuckRef = chuckRef;
    }

    public String getDadJoke() {
        return dadJoke;
    }

    public void setDadJoke(String dadJoke) {
        this.dadJoke = dadJoke;
    }

    public String getDadRef() {
        return dadRef;
    }

    public void setDadRef(String dadRef) {
        this.dadRef = dadRef;
    }

    public String getCatFact() {
        return catFact;
    }

    public void setCatFact(String catFact) {
        this.catFact = catFact;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    
    
    
}
