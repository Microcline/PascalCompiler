//import java.io.FileNotFoundException;
//
//
//public class SyntaxAnalyzer {
//	
//	static Token token = new Token(0, 0);
//	
//	public static void main(String [] args) throws FileNotFoundException {
//		LexicalAnalyzer.main(null);
//		parse();
//	}
//	
//
//	private static void parse() {
////	    token = getNextToken();
//	    program();  // Start symbol
//	    match(0, 0); // TODO: End of file marker
//	}
//
//private static void match(String string) {
//		// TODO Auto-generated method stub
//		
//	}
//
//
////	private static Token getNextToken() {
////		// TODO Auto-generated method stub
////		
////	}
//
//
//	private static void match(int type, int attribute) {
//	    if(type == token.tokenType && token.tokenType == '$') { // TODO $
//	        System.out.println("Success");
//	    }
//	    else if(type == token.tokenType && token.tokenType != '$') {
//	        token = getNextToken();
//	    }
//	    else if(type != token.tokenType) {
//	        System.out.println("SYNTAX ERROR: Expected " + type + " " + attribute + ", received " + token.lexeme + token.tokenType);
//	    }
//	    token = getNextToken();
//
//
//	}
//
//	private static void program() {
//	    if(token.tokenType == 7) { 	// program
//	        match("program");		// program
//	        match("id");	// id
//	        match("(");		// (
////	        identifier_list();
//	        match(";");	// ;
////	        program_tail();
//	    }
//	    else {
//	        System.out.println("SYNTAX ERROR: Expecting one of 'program'" );
//	        // TODO Fix this
//	        while(token.tokenType != synch(program)) {   // Error recovery
//	            token = getNextToken();
//	        }
//	    }
//	}
//	
//	
//}
