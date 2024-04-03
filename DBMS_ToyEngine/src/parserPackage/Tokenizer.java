package parserPackage;

import java.util.StringTokenizer;
import java.util.Vector;

public class Tokenizer {

	public Vector<Token> tokens = new Vector<Token>();
	private int currentToken = 0;

	public Tokenizer(String input) {
		this.tokens = codeToTokens(input);
	}

	public Token getNextToken() {
		this.currentToken++;
		if (currentToken < tokens.size()) {
			return tokens.elementAt(this.currentToken);
		}
		return null;
	}

	public Token getCurrentToken() {
		if (currentToken < tokens.size()) {
			return tokens.elementAt(this.currentToken);
		}
		return null;
	}

	public void setTokenPointer(int index) {
		currentToken = index;
	}

	public void resetTokenPointer() {
		currentToken = 0;
	}

	public Vector<Token> codeToTokens(String input) {
		Vector<Token> tokens = new Vector<Token>();
		StringTokenizer strSplit = new StringTokenizer(input,"\"'",true); // To split the string by " and '
		while(strSplit.hasMoreTokens()) {
			String str = strSplit.nextToken();
			if(str.equals("\"")) {
				String temp = "";
				while(strSplit.hasMoreTokens()) {
					str = strSplit.nextToken();
					if(str.equals("\"")) {
						break;
					}
					temp += str;
				}
				tokens.add(new Token(TokenType.STRING, temp));
			}
			else if(str.equals("'")) {
				String temp = "";
				while(strSplit.hasMoreTokens()) {
					str = strSplit.nextToken();
					if(str.equals("'")) {
						break;
					}
					temp += str;
				}
				tokens.add(new Token(TokenType.STRING, temp));
			}
			else{
				String[] words = str.split("[ \t\n]+"); // Characters you want to ignore
				for (String unDivWord : words){
					StringTokenizer divWord = new StringTokenizer(unDivWord,",()<>=!",true); 
					// these are characters you want to use as Delimiters but need as tokens
					while(divWord.hasMoreTokens()) {
						String word = divWord.nextToken();
						if (word.equals(">")) {
							if(divWord.hasMoreTokens()) {
								String nextWord = divWord.nextToken();
								if(nextWord.equals("=")) {
									tokens.add(new Token(TokenType.GE, word+nextWord));
								} else {
									tokens.add(new Token(TokenType.GT, word));
								}
							} else {
								tokens.add(new Token(TokenType.GT, word));
							}
						} else if (word.equals("<")) {
							if(divWord.hasMoreTokens()) {
								String nextWord = divWord.nextToken();
								if(nextWord.equals("=")) {
									tokens.add(new Token(TokenType.LE, word+nextWord));
								} else {
									tokens.add(new Token(TokenType.LT, word));
								}
							} else {
								tokens.add(new Token(TokenType.LT, word));
							}
						} else if (word.equals("=")) {
							if(divWord.hasMoreTokens()) {
								String nextWord = divWord.nextToken();
								if(nextWord.equals("=")) {
									tokens.add(new Token(TokenType.EQ, word+nextWord));
								} else {
									System.out.println("Invalid token: " + word);
								}
							} else {
								System.out.println("Invalid token: " + word);
							}
						} else if (word.equals("!")) {
							if(divWord.hasMoreTokens()) {
								String nextWord = divWord.nextToken();
								if(nextWord.equals("=")) {
									tokens.add(new Token(TokenType.NE, word+nextWord));
								} else {
									tokens.add(new Token(TokenType.NOT, word));
								}
							} else {
								tokens.add(new Token(TokenType.NOT, word));
							}
							tokens.add(new Token(TokenType.NE, word));
						} else if (word.equals("and")) {
							tokens.add(new Token(TokenType.AND, word));
						} else if (word.equals("or")) {
							tokens.add(new Token(TokenType.OR, word));
						} else if (word.equals("not")) {
							tokens.add(new Token(TokenType.NOT, word));
						} else if (word.equals("create")) {
							tokens.add(new Token(TokenType.CREATE, word));
						} else if (word.equals("insert")) {
							tokens.add(new Token(TokenType.INSERT, word));
						} else if (word.equals("int") || word.equals("float") || word.equals("string")) {
							tokens.add(new Token(TokenType.DATATYPE, word));
						} else if (word.equals("select")) {
							tokens.add(new Token(TokenType.SELECT, word));
						} else if (word.equals("(")) {
							tokens.add(new Token(TokenType.LEFT_PAREN, word));
						} else if (word.equals(")")) {
							tokens.add(new Token(TokenType.RIGHT_PAREN, word));
						} else if (word.equals(",")) {
							tokens.add(new Token(TokenType.COMMA, word));
						} else if (word.matches("[0-9]+")) {
							tokens.add(new Token(TokenType.INTNUM, word));
						} else if (word.matches("[0-9]+\\.[0-9]+")) {
							tokens.add(new Token(TokenType.FLOATNUM, word));
						} else if (word.matches("where")) {
							tokens.add(new Token(TokenType.WHERE, word));
						} else if (word.equals("from")) { 
							tokens.add(new Token(TokenType.FROM, word));
						} else if (word.equals("*")) {
							tokens.add(new Token(TokenType.STAR,word));
						} else if (word.equals("into")) {
							tokens.add(new Token(TokenType.INTO,word));
						} else if (word.matches("[a-zA-Z][a-zA-Z0-9_]*")) {
							tokens.add(new Token(TokenType.VARIABLE, word));
						} else if (word.equals(""))	 {
							// do nothing
						}
						else {
							StringTokenizer divWord2 = new StringTokenizer(word,".",true);
							while(divWord2.hasMoreTokens()) {
								String word2 = divWord2.nextToken();
								if(word2.equals(".")) {
									tokens.add(new Token(TokenType.DOT, word2));
								} else if (word2.matches("[a-zA-Z][a-zA-Z0-9_]*")) {
									tokens.add(new Token(TokenType.VARIABLE, word2));
								} else {
									System.out.println("Invalid token: " + word2);
								}
							}
						}
					}
				}
			}
		}
		this.tokens = tokens;
		return tokens;
	}

//	public static void main(String[] args) {
//		Tokenizer tokenizer = new Tokenizer("create table mytab (name string, age int, salary float)");
//		for (Token token : tokenizer.tokens) {
//			System.out.println(token.type + " : " + token.value);
//		}
//	}

}
