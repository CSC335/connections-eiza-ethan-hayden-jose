public class Word {
    private String text;
    private DifficultyColor color;

    public Word(String text, DifficultyColor color) {
        this.text = text;
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public DifficultyColor getColor() {
        return color;
    }
}
