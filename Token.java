
public class Token {
	public int tokenType = 0;
	public int attribute= 0;
    public String lexeme = "";
    public int line = 0;
    public int value;
	
    public Token(int tokenTypeIn, int attributeIn, String lexIn,
    		int lineNumber) {
		tokenType = tokenTypeIn;
		attribute = attributeIn;
		lexeme = lexIn;
		line = lineNumber;
		
        
        if(tokenTypeIn == 1) {
        	if(attributeIn == 1) {
        		lexeme = "=";
        	}
        	if(attributeIn == 2) {
        		lexeme = "<>";
        	}
        	if(attributeIn == 3) {
        		lexeme = "<";
        	}
        	if(attributeIn == 4) {
        		lexeme = "<=";
        	}
        	if(attributeIn == 5) {
        		lexeme = ">=";
        	}
        	if(attributeIn == 6) {
        		lexeme = ">";
        	}
        }
        if(tokenTypeIn == 2) {
        	if(attributeIn == 1) {
        		lexeme = "+";
        	}
        	if(attributeIn == 2) {
        		lexeme = "-";
        	}
        	if(attributeIn == 3) {
        		lexeme = "or";
        	}
        }
        if(tokenTypeIn == 3) {
        	if(attributeIn == 1) {
        		lexeme = "*";
        	}
        	if(attributeIn == 2) {
        		lexeme = "/";
        	}
        	if(attributeIn == 3) {
        		lexeme = "div";
        	}
        	if(attributeIn == 4) {
        		lexeme = "mod";
        	}
        	if(attributeIn == 5) {
        		lexeme = "and";
        	}
        }
        if(tokenTypeIn == 4) {
        	if(attributeIn == 1) {
        		lexeme = "[";
        	}
        	if(attributeIn == 2) {
        		lexeme = "]";
        	}
        	if(attributeIn == 3) {
        		lexeme = "(";
        	}
        	if(attributeIn == 4) {
        		lexeme = ")";
        	}
        	if(attributeIn == 5) {
        		lexeme = ";";
        	}
        	if(attributeIn == 6) {
        		lexeme = ":";
        	}
        	if(attributeIn == 7) {
        		lexeme = ",";
        	}
        	if(attributeIn == 8) {
        		lexeme = ".";
        	}
        	if(attributeIn == 9) {
        		lexeme = "..";
        	}

        }
        if(tokenTypeIn == 5) {
        	lexeme = "begin";
        }
        if(tokenTypeIn == 6) {
        	lexeme = "end";
        }
        if(tokenTypeIn == 7) {
        	lexeme = "program";
        }
        if(tokenTypeIn == 8) {
        	lexeme = "var";
        }
        if(tokenTypeIn == 9) {
        	lexeme = "function";
        }
        if(tokenTypeIn == 10) {
        	lexeme = "if";
        }
        if(tokenTypeIn == 11) {
        	lexeme = "then";
        }
        if(tokenTypeIn == 12) {
        	lexeme = "else";
        }
        if(tokenTypeIn == 13) {
        	lexeme = "integer";
        }
        if(tokenTypeIn == 14) {
        	lexeme = "array";
        }
        if(tokenTypeIn == 15) {
        	lexeme = "of";
        }
        if(tokenTypeIn == 16) {
        	lexeme = "real";
        }
        if(tokenTypeIn == 17) {
        	lexeme = "procedure";
        }
        if(tokenTypeIn == 18) {
        	lexeme = "while";
        }
        if(tokenTypeIn == 19) {
        	lexeme = "do";
        }
        if(tokenTypeIn == 20) {
        	lexeme = "not";
        }
        if(tokenTypeIn == 21) {
        	lexeme = ":=";
        }
        if(tokenTypeIn == 22) {
        	lexeme = "id";	// Placeholder id
        }
        if(tokenTypeIn == 23) {
        	lexeme = "longreal";
        }
        if(tokenTypeIn == 24) {
        	lexeme = "call";
        }
        if(tokenTypeIn == 26) {
        	lexeme = "int";
        }
        if(tokenTypeIn == 27) {
        	lexeme = "real";
        }
//        if(tokenTypeIn == 27) {
//        	lexeme = lexIn;
//        }
        if(tokenTypeIn == 99) {
        	lexeme = lexIn;
        }
	}
    
	public Token(int tokenTypeIn, int attributeIn, int lineNumber) {
		tokenType = tokenTypeIn;
		attribute = attributeIn;
		line = lineNumber;
        
        if(tokenTypeIn == 1) {
        	if(attributeIn == 1) {
        		lexeme = "=";
        	}
        	if(attributeIn == 2) {
        		lexeme = "<>";
        	}
        	if(attributeIn == 3) {
        		lexeme = "<";
        	}
        	if(attributeIn == 4) {
        		lexeme = "<=";
        	}
        	if(attributeIn == 5) {
        		lexeme = ">=";
        	}
        	if(attributeIn == 6) {
        		lexeme = ">";
        	}
        }
        if(tokenTypeIn == 2) {
        	if(attributeIn == 1) {
        		lexeme = "+";
        	}
        	if(attributeIn == 2) {
        		lexeme = "-";
        	}
        	if(attributeIn == 3) {
        		lexeme = "or";
        	}
        }
        if(tokenTypeIn == 3) {
        	if(attributeIn == 1) {
        		lexeme = "*";
        	}
        	if(attributeIn == 2) {
        		lexeme = "/";
        	}
        	if(attributeIn == 3) {
        		lexeme = "div";
        	}
        	if(attributeIn == 4) {
        		lexeme = "mod";
        	}
        	if(attributeIn == 5) {
        		lexeme = "and";
        	}
        }
        if(tokenTypeIn == 4) {
        	if(attributeIn == 1) {
        		lexeme = "[";
        	}
        	if(attributeIn == 2) {
        		lexeme = "]";
        	}
        	if(attributeIn == 3) {
        		lexeme = "(";
        	}
        	if(attributeIn == 4) {
        		lexeme = ")";
        	}
        	if(attributeIn == 5) {
        		lexeme = ";";
        	}
        	if(attributeIn == 6) {
        		lexeme = ":";
        	}
        	if(attributeIn == 7) {
        		lexeme = ",";
        	}
        	if(attributeIn == 8) {
        		lexeme = ".";
        	}
        	if(attributeIn == 9) {
        		lexeme = "..";
        	}

        }
        if(tokenTypeIn == 5) {
        	lexeme = "begin";
        }
        if(tokenTypeIn == 6) {
        	lexeme = "end";
        }
        if(tokenTypeIn == 7) {
        	lexeme = "program";
        }
        if(tokenTypeIn == 8) {
        	lexeme = "var";
        }
        if(tokenTypeIn == 9) {
        	lexeme = "function";
        }
        if(tokenTypeIn == 10) {
        	lexeme = "if";
        }
        if(tokenTypeIn == 11) {
        	lexeme = "then";
        }
        if(tokenTypeIn == 12) {
        	lexeme = "else";
        }
        if(tokenTypeIn == 13) {
        	lexeme = "integer";
        }
        if(tokenTypeIn == 14) {
        	lexeme = "array";
        }
        if(tokenTypeIn == 15) {
        	lexeme = "of";
        }
        if(tokenTypeIn == 16) {
        	lexeme = "real";
        }
        if(tokenTypeIn == 17) {
        	lexeme = "procedure";
        }
        if(tokenTypeIn == 18) {
        	lexeme = "while";
        }
        if(tokenTypeIn == 19) {
        	lexeme = "do";
        }
        if(tokenTypeIn == 20) {
        	lexeme = "not";
        }
        if(tokenTypeIn == 21) {
        	lexeme = ":=";
        }
        if(tokenTypeIn == 22) {
        	lexeme = "id";	// Placeholder id
        }
        if(tokenTypeIn == 23) {
        	lexeme = "longreal";
        }
        if(tokenTypeIn == 24) {
        	lexeme = "call";
        }
        if(tokenTypeIn == 26) {
        	lexeme = "int";
        }
        if(tokenTypeIn == 27) {
        	lexeme = "real";
        }
        if(tokenTypeIn == 99) {
        	lexeme = "LEXERR";
        }
        

	}
	
	public Token(int tokenTypeIn, int attributeIn, int lineNumber, int val) {
		tokenType = tokenTypeIn;
		attribute = attributeIn;
		line = lineNumber;
		value = val;
        
        if(tokenTypeIn == 1) {
        	if(attributeIn == 1) {
        		lexeme = "=";
        	}
        	if(attributeIn == 2) {
        		lexeme = "<>";
        	}
        	if(attributeIn == 3) {
        		lexeme = "<";
        	}
        	if(attributeIn == 4) {
        		lexeme = "<=";
        	}
        	if(attributeIn == 5) {
        		lexeme = ">=";
        	}
        	if(attributeIn == 6) {
        		lexeme = ">";
        	}
        }
        if(tokenTypeIn == 2) {
        	if(attributeIn == 1) {
        		lexeme = "+";
        	}
        	if(attributeIn == 2) {
        		lexeme = "-";
        	}
        	if(attributeIn == 3) {
        		lexeme = "or";
        	}
        }
        if(tokenTypeIn == 3) {
        	if(attributeIn == 1) {
        		lexeme = "*";
        	}
        	if(attributeIn == 2) {
        		lexeme = "/";
        	}
        	if(attributeIn == 3) {
        		lexeme = "div";
        	}
        	if(attributeIn == 4) {
        		lexeme = "mod";
        	}
        	if(attributeIn == 5) {
        		lexeme = "and";
        	}
        }
        if(tokenTypeIn == 4) {
        	if(attributeIn == 1) {
        		lexeme = "[";
        	}
        	if(attributeIn == 2) {
        		lexeme = "]";
        	}
        	if(attributeIn == 3) {
        		lexeme = "(";
        	}
        	if(attributeIn == 4) {
        		lexeme = ")";
        	}
        	if(attributeIn == 5) {
        		lexeme = ";";
        	}
        	if(attributeIn == 6) {
        		lexeme = ":";
        	}
        	if(attributeIn == 7) {
        		lexeme = ",";
        	}
        	if(attributeIn == 8) {
        		lexeme = ".";
        	}
        	if(attributeIn == 9) {
        		lexeme = "..";
        	}

        }
        if(tokenTypeIn == 5) {
        	lexeme = "begin";
        }
        if(tokenTypeIn == 6) {
        	lexeme = "end";
        }
        if(tokenTypeIn == 7) {
        	lexeme = "program";
        }
        if(tokenTypeIn == 8) {
        	lexeme = "var";
        }
        if(tokenTypeIn == 9) {
        	lexeme = "function";
        }
        if(tokenTypeIn == 10) {
        	lexeme = "if";
        }
        if(tokenTypeIn == 11) {
        	lexeme = "then";
        }
        if(tokenTypeIn == 12) {
        	lexeme = "else";
        }
        if(tokenTypeIn == 13) {
        	lexeme = "integer";
        }
        if(tokenTypeIn == 14) {
        	lexeme = "array";
        }
        if(tokenTypeIn == 15) {
        	lexeme = "of";
        }
        if(tokenTypeIn == 16) {
        	lexeme = "real";
        }
        if(tokenTypeIn == 17) {
        	lexeme = "procedure";
        }
        if(tokenTypeIn == 18) {
        	lexeme = "while";
        }
        if(tokenTypeIn == 19) {
        	lexeme = "do";
        }
        if(tokenTypeIn == 20) {
        	lexeme = "not";
        }
        if(tokenTypeIn == 21) {
        	lexeme = ":=";
        }
        if(tokenTypeIn == 22) {
        	lexeme = "id";	// Placeholder id
        }
        if(tokenTypeIn == 23) {
        	lexeme = "longreal";
        }
        if(tokenTypeIn == 24) {
        	lexeme = "call";
        }
        if(tokenTypeIn == 26) {
        	lexeme = "int";
        }
        if(tokenTypeIn == 27) {
        	lexeme = "real";
        }
        if(tokenTypeIn == 99) {
        	lexeme = "LEXERR";
        }
	}
	public int getAttribute() {
		return attribute;
	}
	public int getTokenType() {
		return tokenType;
	}
}
