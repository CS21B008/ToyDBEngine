package parserPackage;

import java.util.StringTokenizer;
import java.util.Vector;

public class Parser {

	Vector<Token> tokens ; // for error handling maybe...
	
	public Parser() {
		
	}

	private Vector<String> checkCreate(Tokenizer lexer) throws Exception {
		// System.out.println("Checking create");
		Vector<String> intermediateCode = new Vector<String>();
		String code = "";
		String name = "";
		Token nextToken = new Token(null,null);
		nextToken.copy(lexer.getCurrentToken());
		if(!(nextToken.type != null && nextToken.type == TokenType.CREATE)){
			throw new Exception("Invalid syntax: Expecting 'create' keyword");
		}
		nextToken.copy(lexer.getNextToken());
		if(!(nextToken.type != null && nextToken.type == TokenType.VARIABLE)){
			throw new Exception("Invalid syntax: Expecting table name after 'create' keyword");
		}
		name = nextToken.value;
		nextToken.copy(lexer.getNextToken());
		if(!(nextToken.type != null && nextToken.type == TokenType.INTNUM)){
			throw new Exception("Invalid syntax: Expecting number of attributes after table name");
		}
		int numAttr = Integer.parseInt(nextToken.value);
		code = "create_table "+name;
		intermediateCode.add(code);
		for(int i=0;i<numAttr;i++){
			nextToken.copy(lexer.getNextToken());
			if(!(nextToken.type != null && nextToken.type == TokenType.DATATYPE)){
				throw new Exception("Invalid syntax: Expecting data type after number of attributes");
			}
			String dataType = nextToken.value;
			nextToken.copy(lexer.getNextToken());
			if(!(nextToken.type != null && nextToken.type == TokenType.VARIABLE)){
				throw new Exception("Invalid syntax: Expecting attribute name after data type");
			}
			String attrName = nextToken.value;
			code = "add_attribute "+dataType+" "+attrName;
			intermediateCode.add(code);
		}
		// change token pointer to next token so other functions can use it
		lexer.getNextToken();
		return intermediateCode;
	}

	private Vector<String> checkInsert(Tokenizer lexer) throws Exception {
		Vector<String> intermediateCode = new Vector<String>();
		String code = "";
		String tableName = "";
		Token nextToken = new Token(null,null);
		nextToken.copy(lexer.getCurrentToken());
		if(!(nextToken.type != null && nextToken.type == TokenType.INSERT)){
			throw new Exception("Invalid syntax: Expecting 'insert' keyword");
		}
		nextToken.copy(lexer.getNextToken());
		if(!(nextToken.type != null && nextToken.type == TokenType.INTO)){
			throw new Exception("Invalid syntax: Expecting 'into' keyword after 'insert'");
		}
		nextToken.copy(lexer.getNextToken());
		if(!(nextToken.type != null && nextToken.type == TokenType.VARIABLE)){
			throw new Exception("Invalid syntax: Expecting table name after 'into' keyword");
		}
		tableName = nextToken.value;
		nextToken.copy(lexer.getNextToken());
		if(!(nextToken.type != null && nextToken.type == TokenType.LEFT_PAREN)){
			throw new Exception("Invalid syntax: Expecting '(' after table name");
		}
		String values = "";
		while(true){
			nextToken.copy(lexer.getNextToken());
			if(!(nextToken.type != null)){
				throw new Exception("Invalid syntax: Expecting values or ')' after '('");
			}
			if(nextToken.type == TokenType.RIGHT_PAREN){
				values = values.substring(0, values.length()-1);
				code = "insert_into "+tableName+" "+values;
				intermediateCode.add(code);
				break;
			}
			if(!(nextToken.type == TokenType.STRING || nextToken.type == TokenType.INTNUM || nextToken.type == TokenType.FLOATNUM)){
				throw new Exception("Invalid syntax: Expecting values of type String or Int or Float");
			}
			values = values + nextToken.value + ",";
			nextToken.copy(lexer.getNextToken());
			if(nextToken.type != null && nextToken.type == TokenType.COMMA){
				continue;
			}else if(nextToken.type != null && nextToken.type == TokenType.RIGHT_PAREN){
				values = values.substring(0, values.length()-1);
				code = "insert_into "+tableName+" "+values;
				intermediateCode.add(code);
				break;
			}else{
				throw new Exception("Invalid syntax: Expecting ',' or ')' after value");
			}
		}
		// change token pointer to next token so other functions can use it
		lexer.getNextToken();
		return intermediateCode;
	}

	private String checkWhere(Tokenizer lexer) throws Exception{
		String code = "";
		Token nextToken = new Token(null,null);
		nextToken.copy(lexer.getCurrentToken());
		if(!(nextToken.type != null && nextToken.type == TokenType.WHERE)){
			throw new Exception("Invalid syntax: Expecting 'where' keyword");
		}
		nextToken.copy(lexer.getNextToken());


		return code;
	}

	private String checkContiCol(Tokenizer lexer) throws Exception{
		String code = "";
		Token nextToken = new Token(null,null);
		nextToken.copy(lexer.getCurrentToken());
		if(!(nextToken.type != null && nextToken.type == TokenType.VARIABLE)){
			throw new Exception("Invalid syntax: Expecting column name");
		}
		code = nextToken.value;
		nextToken.copy(lexer.getNextToken());
		if(nextToken.type != null && nextToken.type == TokenType.DOT){
			nextToken.copy(lexer.getNextToken());
			if(!(nextToken.type != null && nextToken.type == TokenType.VARIABLE)){
				throw new Exception("Invalid syntax: Expecting column name after '.'");
			}
			code = code+"."+nextToken.value;
			nextToken.copy(lexer.getNextToken());
		}
		if(nextToken.type != null && nextToken.type == TokenType.COMMA){
			nextToken.copy(lexer.getNextToken());
			code = code + "," + checkContiCol(lexer);
		}
		return code;
	}

	private String checkContiTab(Tokenizer lexer) throws Exception{
		String code = "";
		Token nextToken = new Token(null,null);
		nextToken.copy(lexer.getCurrentToken());
		if(!(nextToken.type != null && nextToken.type == TokenType.VARIABLE)){
			throw new Exception("Invalid syntax: Expecting table name");
		}
		code = nextToken.value;
		nextToken.copy(lexer.getNextToken());
		if(nextToken.type != null && nextToken.type == TokenType.COMMA){
			nextToken.copy(lexer.getNextToken());
			code = code + "," + checkContiTab(lexer);
		}
		return code;
	}

	private String checkSelectStar(Tokenizer lexer) throws Exception{
		String code = "";
		Token nextToken = new Token(null,null);
		nextToken.copy(lexer.getCurrentToken());
		if(!(nextToken.type != null && nextToken.type == TokenType.STAR)){
			throw new Exception("Invalid syntax: Expecting '*' after 'select'");
		}
		nextToken.copy(lexer.getNextToken());
		if(!(nextToken.type != null && nextToken.type == TokenType.FROM)){
			throw new Exception("Invalid syntax: Expecting 'from' after '*'");
		}
		nextToken.copy(lexer.getNextToken());
		if(!(nextToken.type != null && nextToken.type == TokenType.VARIABLE)){
			throw new Exception("Invalid syntax: Expecting table name after 'from'");
		}
		String tables = nextToken.value;
		nextToken.copy(lexer.getNextToken());
		if(nextToken.type != null && nextToken.type == TokenType.COMMA){
			nextToken.copy(lexer.getNextToken());
			tables = tables + "," + checkContiTab(lexer);
		}
		code = code+tables;
		if(nextToken.type != null && nextToken.type == TokenType.WHERE){
			code = code+" "+ checkWhere(lexer);
		}
		return code;
	}

	private String checkSelectCols(Tokenizer lexer) throws Exception{
		String code = "";
		Token nextToken = new Token(null,null);
		nextToken.copy(lexer.getCurrentToken());
		String colnames = checkContiCol(lexer);
		nextToken.copy(lexer.getCurrentToken());
		if(!(nextToken.type != null && nextToken.type == TokenType.FROM)){
			throw new Exception("Invalid syntax: Expecting 'from' after column names");
		}
		nextToken.copy(lexer.getNextToken());
		if(!(nextToken.type != null && nextToken.type == TokenType.VARIABLE)){
			throw new Exception("Invalid syntax: Expecting table name after 'from'");
		}
		String tables = checkContiTab(lexer);
		nextToken.copy(lexer.getCurrentToken());
		code = "select "+colnames+" from "+tables;
		if(nextToken.type != null && nextToken.type == TokenType.WHERE){
			code = code+" "+ checkWhere(lexer);
		}
		return code;
	}

	private Vector<String> checkSelect(Tokenizer lexer) throws Exception{
		Vector<String> intermediateCode = new Vector<String>();
		String code = "";
		Token nextToken = new Token(null,null);
		nextToken.copy(lexer.getCurrentToken());
		if(!(nextToken.type != null && nextToken.type == TokenType.SELECT)){
			throw new Exception("Invalid syntax: Expecting 'select' keyword");
		}
		nextToken.copy(lexer.getNextToken());
		if(!(nextToken.type != null && (nextToken.type == TokenType.STAR || nextToken.type == TokenType.VARIABLE))){
			throw new Exception("Invalid syntax: Expecting '*' or column name after 'select' keyword");
		}
		if(nextToken.type == TokenType.STAR){
			code = "select_all from "+checkSelectStar(lexer);
			StringTokenizer codes = new StringTokenizer(code,"\n");
			while(codes.hasMoreTokens()){
				intermediateCode.add(codes.nextToken());
			}
		}else if(nextToken.type == TokenType.VARIABLE){
			code = "select "+checkSelectCols(lexer);
			StringTokenizer codes = new StringTokenizer(code,"\n");
			while(codes.hasMoreTokens()){
				intermediateCode.add(codes.nextToken());
			}
		}else{
			throw new Exception("Invalid syntax: Expecting '*' or column name after 'select' keyword");
		}

		return intermediateCode;
	}

	public Vector<String> parse(Tokenizer lexer) {
		// while(lexer.getCurrentToken() != null){
		// 	System.out.println(lexer.getCurrentToken().type+" : "+lexer.getCurrentToken().value);
		// 	lexer.getNextToken();
		// }
		lexer.resetTokenPointer();
		Vector<String> intermediateCode = new Vector<String>();
		// System.out.println("Parsing...");
		/* CFG:
		 * Start -> CREATE Start |
		 * 			INSERT Start |
		 * 			SELECT Start ;
		 * CREATE -> "create" String Num ATTR;
		 * ATTR -> DataType String ATTR | stop condition;
		 * INSERT -> "insert" "into" String "(" comma separated values ")" ;
		 * SELECT -> "select" "*" "from" String ["where" COND]| 
		 * 			 "select" COLOUMS "from" String ["where" COND];
		 * COND -> String OP String COND | ;
		 * COLOUMS -> String "." String CONT | String CONT;
		 * CONT -> "," COLOUMS | ;
		 */

		// convention processing starts from current token and each function will change the token pointer to farther tokens
		if(lexer.tokens.size() == 0) {
			return null;
		}
		try{
			while(lexer.getCurrentToken() != null){
				if(lexer.getCurrentToken().type == TokenType.CREATE){
					intermediateCode.addAll(checkCreate(lexer));
				}else if(lexer.getCurrentToken().type == TokenType.INSERT){
					intermediateCode.addAll(checkInsert(lexer));
				}else if(lexer.getCurrentToken().type == TokenType.SELECT){
					intermediateCode.addAll(checkSelect(lexer));
				}else{
					throw new Exception("Invalid syntax: Expecting 'create' or 'insert' or 'select' keyword");
				}
			}
		}catch(Exception e){
			System.out.println("Parsing Error:\n"+e);
			return null;
		}

		return intermediateCode;
	}

	// public static void main(String[] args) {
	// 	String input = "create table1 2 int a int b insert into table1 (2,3) select * from table1" ;
	// 	Tokenizer lexer = new Tokenizer(input);
	// 	Parser parser = new Parser();
	// 	for(int i=0;i<lexer.tokens.size();i++) {
	// 		System.out.println(lexer.tokens.elementAt(i).type);
	// 	}
	// 	Vector<String> intermediateCode = parser.parse(lexer);
	// 	if(intermediateCode == null) {
	// 	}else {
	// 		for(int i=0;i<intermediateCode.size();i++ ) {
	// 			System.out.println(intermediateCode.elementAt(i));
	// 		}
	// 	}
	// }

}
