package lexical;

/**
 * 符号类，用于词法分析
 */
public class Token {
    private String sym = "";
    private String id = "";
    private String num = "";
    private String content = "";

    public Token(String sym, String id, String num, String content) {
        this.sym = sym;
        this.id = id;
        this.num = num;
        this.content = content;
    }

    public Token(String sym, String id, String num, String content, int position) {
        this.sym = sym;
        this.id = id;
        this.num = num;
        this.content = content;
        this.position = position;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "Token{" +
                "sym='" + sym + '\'' +
                ", id='" + id + '\'' +
                ", num='" + num + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    public void setContent(String content) {
        this.content = content;
    }

    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Token(String sym, String id, String num) {
        super();
        this.sym = sym;
        this.id = id;
        this.num = num;
    }

    public String getSym() {
        return sym;
    }

    public void setSym(String sym) {
        this.sym = sym;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

}
