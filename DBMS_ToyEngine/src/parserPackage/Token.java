package parserPackage;

public class Token {
	
    public TokenType type;
    public String value;
    
    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public Token(Token token) {
        this.type = token.type;
        this.value = token.value;
    }

    public void copy(Token token) {
        if(token == null){
            this.type = null;
            this.value = null;
            return;
        }
        this.type = token.type;
        this.value = token.value;
    }
}
