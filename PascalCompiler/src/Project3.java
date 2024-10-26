import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Scanner;
/**
 * @author Jacob Sherrill
 */



public class Project3 {
	static Token token = new Token(0, 0, 0);
	static char[] buffer = new char[72];
	static int lineCounter = 0;
	static int f = 0;	// Forward pointer for buffer
	static int b = 0;	// Back pointer for buffer
	static int c = 0;	// Digit counter for int, real, longreal machines
	static String idRes = "";
	static String numString = "";
	static LinkedList reservedWordTable = new LinkedList();
	static LinkedList symbolTable = new LinkedList();
	public static LinkedList<Token> tokenList = new LinkedList<Token>();
	
	public static LinkedList<String> listingList = new LinkedList<String>();

	static File listingFile = new File("Listing.txt");
	static File tokenFile = new File("Token");
	static File reservedFile = new File("Reserved.txt");
	static File sourceFile = new File("SourceProject3.txt");

	static PrintWriter listingWriter;
	static PrintWriter tokenWriter;
	static Scanner reservedScanner;
	static Scanner sourceScanner;

	public static void main(String [] args) throws FileNotFoundException {
		init();	//Load source(r), reserved(r), listing(w), token(w)
		clearBuffer();
		getNextToken();

		// While not at the end of the source file
		while(sourceScanner.hasNextLine()) {
			// Put the source file line into the buffer
			// TODO I flipped these
			lineCounter++;
			getNextLine();

			// Write the buffer line to the listing file
			// LEXERRs will be written below each line
//			writeBufferToListing();

			// Get all tokens on the line of the source file
			getNextToken();
			if(!(sourceScanner.hasNextLine())) {
				// Last token is (EOF, NULL)
				tokenWriter.printf("\t\t\t98 (EOF)"
						+ "\t\t\t0 (NULL)\n");
				tokenList.add(new Token(98, 0, lineCounter));
			}
		}


		// TODO token printer
		//		FOR(INT I = 0; I < TOKENLIST.SIZE(); I++) {
		//			SYSTEM.OUT.PRINTLN(TOKENLIST.GET(I).TOKENTYPE + " " + 
		//					TOKENLIST.GET(I).ATTRIBUTE);
		//		}
		lexicalAnalyzer();
	}
	private static String getTokenReference(int type, int attribute) {
        if(type == 1) {
        	if(attribute == 1) {
        		return "=";
        	}
        	if(attribute == 2) {
        		return "<>";
        	}
        	if(attribute == 3) {
        		return "<";
        	}
        	if(attribute == 4) {
        		return "<=";
        	}
        	if(attribute == 5) {
        		return ">=";
        	}
        	if(attribute == 6) {
        		return ">";
        	}
        }
        if(type == 2) {
        	if(attribute == 1) {
        		return "+";
        	}
        	if(attribute == 2) {
        		return "-";
        	}
        	if(attribute == 3) {
        		return "or";
        	}
        }
        if(type == 3) {
        	if(attribute == 1) {
        		return "*";
        	}
        	if(attribute == 2) {
        		return "/";
        	}
        	if(attribute == 3) {
        		return "div";
        	}
        	if(attribute == 4) {
        		return "mod";
        	}
        	if(attribute == 5) {
        		return "and";
        	}
        }
        if(type == 4) {
        	if(attribute == 1) {
        		return "[";
        	}
        	if(attribute == 2) {
        		return "]";
        	}
        	if(attribute == 3) {
        		return "(";
        	}
        	if(attribute == 4) {
        		return ")";
        	}
        	if(attribute == 5) {
        		return ";";
        	}
        	if(attribute == 6) {
        		return ":";
        	}
        	if(attribute == 7) {
        		return ",";
        	}
        	if(attribute == 8) {
        		return ".";
        	}
        	if(attribute == 9) {
        		return "..";
        	}

        }
        if(type == 5) {
        	return "begin";
        }
        if(type == 6) {
        	return "end";
        }
        if(type == 7) {
        	return "program";
        }
        if(type == 8) {
        	return "var";
        }
        if(type == 9) {
        	return "function";
        }
        if(type == 10) {
        	return "if";
        }
        if(type == 11) {
        	return "then";
        }
        if(type == 12) {
        	return "else";
        }
        if(type == 13) {
        	return "integer";
        }
        if(type == 14) {
        	return "array";
        }
        if(type == 15) {
        	return "of";
        }
        if(type == 16) {
        	return "real";
        }
        if(type == 17) {
        	return "procedure";
        }
        if(type == 18) {
        	return "while";
        }
        if(type == 19) {
        	return "do";
        }
        if(type == 20) {
        	return "not";
        }
        if(type == 21) {
        	return ":=";
        }
        if(type == 22) {
        	return "id";	// Placeholder id
        }
        if(type == 23) {
        	return "longreal";
        }
        if(type == 24) {
        	return "call";
        }
        if(type == 99) {
        	return "LEXERR";
        }
		
		return null;
	}



	/**
	 * Opens source(r), reserved(r), listing(w), token(w) files
	 * @throws FileNotFoundException 
	 */
	private static void init() throws FileNotFoundException {
		listingWriter = new PrintWriter("Listing.txt");
		tokenWriter = new PrintWriter(tokenFile);
		reservedScanner = new Scanner(reservedFile);
		sourceScanner = new Scanner(sourceFile);

		// Writes header on token file
		tokenWriter.printf("Line No.\tLexeme\t\tTOKEN-TYPE\tATTRIBUTE\n");

		// "Reserved word file: This file is read in during the 
		// initialization process and its information is stored in the
		// reserved word table"
		// "Keywords are reserved and appear in boldface in the grammar"(p. 749)
		while(reservedScanner.hasNext()) {
			reservedWordTable.add(reservedScanner.next());
		}
		System.out.println("Files loaded for reading/writing");
	}

	/**
	 * This method goes through machines and returns tokens
	 * @return
	 * @throws FileNotFoundException 
	 */
	private static Object getNextToken() throws FileNotFoundException {
		// Now go through machines		
		for(int i = 0; i < buffer.length && buffer[f] != '\u0000'; i++) {						
			// Begin WHITESPACE machine code
			if(Character.isWhitespace(buffer[f]) || buffer[f] == '\n' ||
					buffer[f] == '\t' || buffer[f] == '\r' ||
					Character.isSpaceChar(buffer[f])) {
				f++;
				b = f;
				while(Character.isWhitespace(buffer[f]) || buffer[f] == '\n' ||
						buffer[f] == '\t' || buffer[f] == '\r' ||
						Character.isSpaceChar(buffer[f])) {
					f++;
					b = f;
					getNextToken();
				}	
			}
			// End WHITESPACE machine code
			
			// Begin ID/RES machine
			if(Character.isLetter(buffer[f])) {
				idRes += buffer[f];
				f++;
				while(Character.isLetter(buffer[f]) 
						|| Character.isDigit(buffer[f])) {
					idRes += buffer[f];
					f++;
				}
				if(idRes.length() <= 10) {
					reservedScanner = new Scanner(reservedFile);
					// If it's a reserved word, 
					if(reservedWordTable.contains(idRes)) {						
						while(reservedScanner.hasNext()) {
							String next = reservedScanner.next();
							if(idRes.equals(next)) {
								int tokenType = reservedScanner.nextInt();
								int attr = reservedScanner.nextInt();
								tokenWriter.printf(lineCounter + "\t\t\t" 
										+ idRes	+ "\t\t" + tokenType 
										+ " (RES)\t\t\t"
										+ attr + "\n");
								tokenList.add(new Token(tokenType, attr,
										idRes, lineCounter));
							}
						}						
					}
					//					Else it's not a reserved word, and instead is an ID!
					else {
						// If the symbol table contains the ID, 
						if(symbolTable.contains(idRes)) {
							tokenWriter.printf(lineCounter + "\t\t\t" + idRes
									+ "\t\t\t25 (ID)\t\t"
									+ "loc" + symbolTable.indexOf(idRes)
									+ " (ptr to sym tab)"
									+ "\n");
							tokenList.add(new Token(25, 
									symbolTable.indexOf(idRes)
									, idRes, lineCounter));
						}
						// Else the ID is not in the symbol table
						else {
							symbolTable.add(idRes);
							tokenWriter.printf(lineCounter + "\t\t\t" + idRes
									+ "\t\t\t25 (ID)\t\t\t"
									+ symbolTable.indexOf(idRes)
									+ " (ptr to sym tab)"
									+ "\n");
							tokenList.add(new Token(25, 0, idRes, lineCounter));
						}
					}
					b = f;
					idRes = "";
					getNextToken();
				}
				else if(idRes.length() > 10) {
					listingList.set(lineCounter-1, listingList.get(lineCounter-1)
							+ "LEXERR:\tWord too long:\t"
							+ idRes +"\n");
//					listingWriter.printf("LEXERR:\tWord too long:\t"
//							+ idRes +"\n");
					tokenWriter.printf(lineCounter + "\t\t\t" + idRes 
							+  "\t99 (LEXERR)\t\t"
							+ "6 (LONGWORD)\t\n");
					tokenList.add(new Token(99, 6, lineCounter));
					f++;
					idRes = "";
				}

			}
			// End ID/RES machine
			
			// Begin ASSIGNOP machine code
			if(buffer[f] == ':') {
				f++;
				if(buffer[f] == '=') {
					tokenWriter.printf(lineCounter + "\t\t\t:="
							+ "\t\t\t21 (ASSIGNOP)\t1 (ASSIGN)\n");
					tokenList.add(new Token(21, 0, lineCounter));
					f++;
					b = f;
					getNextToken();
				}
				else if(buffer[f] != '=') {
					f = b;
				}
			}
			// Begin RELOP machine: <>, <=, <, =, >=, >
			if(buffer[f] == '<') {
				f++;
				// NE
				if(buffer[f] == '>') {
					tokenWriter.printf(lineCounter + "\t\t\t<>"
							+ "\t\t\t1 (RELOP)\t2 (NE)\n");
					tokenList.add(new Token(1, 2, lineCounter));
					f++;
					b = f;
					getNextToken();
				}
				// LE
				else if(buffer[f] == '=') {
					f++;
					tokenWriter.printf(lineCounter + "\t\t\t<=\t\t\t1 (RELOP)"
							+ "\t\t4 (LE)\n");
					tokenList.add(new Token(1, 4, lineCounter));
					b = f;
					getNextToken();
				}
				// LT/other
				else {
					tokenWriter.printf(lineCounter + "\t\t\t<\t\t\t1 (RELOP)"
							+ "\t3 (LT)\n");
					tokenList.add(new Token(1, 3, lineCounter));
					b = f;
					getNextToken();
				}
			}
			if(buffer[f] == '=') {
				f++;
				tokenWriter.printf(lineCounter + "\t\t\t=\t\t\t1 (RELOP)"
						+ "\t1 (EQ)\n");
				tokenList.add(new Token(1, 1, lineCounter));
				b = f;
				getNextToken();
			}
			if(buffer[f] == '>') {
				f++;
				if(buffer[f] == '=') {
					f++;
					tokenWriter.printf(lineCounter + "\t\t\t>=\t\t\t1 (RELOP)"
							+ "\t5 (GE)\n");
					tokenList.add(new Token(1, 5, lineCounter));
					b = f;
					getNextToken();
				}
				else {
					tokenWriter.printf(lineCounter + "\t\t\t>\t\t\t1 (RELOP)\t"
							+ "6 (GT)\n");
					tokenList.add(new Token(1, 6, lineCounter));
					b = f;
					getNextToken();
				}
			}
			// End RELOP machine

			// LONGREAL machine only
			if(Character.isDigit(buffer[f])) {
				numString += buffer[f];
				// TODO changing
//				if(buffer[f] == '0' && Character.isDigit(buffer[f+1])) {
//					// LEXERR: leading zero
//					tokenWriter.printf(lineCounter + "\t\t\t" + numString
//							+ "\t\t99 (LEXERR)\t"
//							+ "5 (LEADZERO)\t" + "\n");
//					tokenList.add(new Token(99, 5));
//					listingWriter.printf("LEXERR:\tLeading zero"
//							+ ":\t"	+ numString +"\n");	
//				}
				f++;
				c++;
				while(Character.isDigit(buffer[f]) && c <= 5) {
					numString += buffer[f];
					f++;
					c++;
				}
				if(buffer[f] == '.') {
					numString += buffer[f];
					f++;
					c = 0;
					while(Character.isDigit(buffer[f]) && c <= 5) {
						numString += buffer[f];
						f++;
						c++;
					}

					if(buffer[f] == 'E') {
						if(c > 5) {
							// LEXERR: Digits after decimal point too long
							listingList.set(lineCounter-1, listingList.get(lineCounter-1)
									+ "LEXERR:\tReal second part too long"
									+ ":\t"	+ numString +"\n");
							tokenWriter.printf(lineCounter + "\t\t\t" + numString
									+ "\t\t99 (LEXERR)\t"
									+ "3 (RLLONGSCND)\t" + "\n");
							tokenList.add(new Token(99, 3, lineCounter));
//							listingWriter.printf("LEXERR:\tReal second part too long"
//									+ ":\t"	+ numString +"\n");	
						}

						numString += buffer[f];
						f++;
						c = 0;
						// Sign logic
						if(buffer[f] == '+' || buffer[f] == '-') {
							numString += buffer[f];
							f++;

							if(Character.isDigit(buffer[f])) {
								numString += buffer[f];
								f++;
								c++;
								while(Character.isDigit(buffer[f])) {
									numString += buffer[f];
									f++;
									c++;
								}
								
								if(c > 2) {
									// TODO I fixed this.  
									//LEXERR: Digits after decimal point too long
									tokenWriter.printf(lineCounter + "\t\t\t" + numString
											+ "\t\t99 (LEXERR)\t"
											+ "4 (EXPLONG)\t" + "\n");
									tokenList.add(new Token(99, 4, lineCounter));
									listingList.set(lineCounter-1, listingList.get(lineCounter-1)
											+ "LEXERR:\tExp long"
											+ ":\t"	+ numString +"\n");
//									listingWriter.printf("LEXERR:\tExp long"
//											+ ":\t"	+ numString +"\n");	
									numString = "";
									getNextToken();
									
								}
								else {
									// LONGREAL
									tokenWriter.printf(lineCounter + "\t\t\t"
											+ numString
											+ "\t\t28 (LONGREAL)\t"
											+ "0 (NULL)\n");
									tokenList.add(new Token(28, 0, lineCounter));
									numString = "";
									getNextToken();

								}
							}
						}
					}
					else if(Character.isDigit(buffer[f])) {

					}


				}
				numString = "";
				c = 0;
				f = b;
			}

			// REAL machine only
			if(Character.isDigit(buffer[f])) {
				int z = 1;
				numString += buffer[f];
				//				if(buffer[f] == '0' && Character.isDigit(buffer[f+1])) {
				//				       // LEXERR: leading zero
				//					tokenWriter.printf(lineCounter + "\t\t\t" + numString
				//							+ "\t\t99 (LEXERR)\t"
				//							+ "5 (LEADZERO)\t" + "\n");
				//					tokenList.add(new Token(99, 5));
				//					listingWriter.printf("LEXERR:\tLeading zero"
				//							+ ":\t"	+ numString +"\n");	
				//				}
				f++;
				c++;
				while(Character.isDigit(buffer[f])) {// && c <= 5) {
					numString += buffer[f];
					f++;
					c++;
					z++;
				}
	
				if(buffer[f] == '.') {
					

					
					numString += buffer[f];
					f++;
					c = 0;
					// TODO changing
					while(Character.isDigit(buffer[f])) {// && c <= 5) {
						numString += buffer[f];
						f++;
						c++;
					}
					if (c > 5) {
						tokenWriter.printf(lineCounter + "\t\t\t" + numString
								+ "\t\t99 (LEXERR)\t"
								+ "3 (RL2NDLONG)\t" + "\n");
						tokenList.add(new Token(99, 3, lineCounter));
						listingList.set(lineCounter-1, listingList.get(lineCounter-1)
								+ "LEXERR:\tReal 2nd part too long"
								+ ":\t"	+ numString +"\n");
//						listingWriter.printf("LEXERR:\tReal 2nd part too long"
//								+ ":\t"	+ numString +"\n");	
						numString = "";
						c = 0;
						b = f;
						getNextToken();
					}
					else if(z > 5) {
						// LEXERR: real first part too long
						tokenWriter.printf(lineCounter + "\t\t\t" + numString
								+ "\t\t99 (LEXERR)\t"
								+ "10 (RL1STLONG)\t" + "\n");
						tokenList.add(new Token(99, 3, lineCounter));
						listingList.set(lineCounter-1, listingList.get(lineCounter-1)
								+ "LEXERR:\tReal 1st part too long"
								+ ":\t"	+ numString +"\n");
//						listingWriter.printf("LEXERR:\tReal 1st part too long"
//								+ ":\t"	+ numString +"\n");	
						numString = "";
						z = 0;
						b = f;
						getNextToken();
					}
					// Check if buffer[f-1] isn't decimal
					else if(Character.isDigit(buffer[f-1])) {
						c = 0;
						b = f;
						tokenWriter.printf(lineCounter + "\t\t\t"
								+ numString
								+ "\t\t27 (REAL)\t\t"
								+ "0 (NULL)\n");
						tokenList.add(new Token(27, 0, lineCounter));
						numString = "";
						getNextToken();
					}



				}

				else if(Character.isDigit(buffer[f])) {

				}
				numString = "";
				c = 0;
				f = b;
			}

			// INT machine only
			if(Character.isDigit(buffer[f])) {
				numString += buffer[f];
				//			    if(buffer[f] == '0' && Character.isDigit(buffer[f+1])) {
				//				       // LEXERR: leading zero
				//					tokenWriter.printf(lineCounter + "\t\t\t" + numString
				//							+ "\t\t99 (LEXERR)\t"
				//							+ "5 (LEADZERO)\t" + "\n");
				//					tokenList.add(new Token(99, 5));
				//					listingWriter.printf("LEXERR:\tLeading zero"
				//							+ ":\t"	+ numString +"\n");	
				//				}
				f++;
				c++;
				while(Character.isDigit(buffer[f]) && c <= 10) {
					numString += buffer[f];
					f++;
					c++;
				}
				if(c > 10) {
					// TODO: LEXERR: Int too long
					tokenWriter.printf(lineCounter + "\t\t\t" + numString
							+ "\t\t99 (LEXERR)\t"
							+ "7 (INTLONG)\t" + "\n");
					tokenList.add(new Token(99, 7, lineCounter));
					listingList.set(lineCounter-1, listingList.get(lineCounter-1)
							+ "LEXERR:\tInt too long"
							+ ":\t"	+ numString +"\n");
//					listingWriter.printf("LEXERR:\tInt too long"
//							+ ":\t"	+ numString +"\n");	
				}
				else {
					if(numString.startsWith("0") && numString.length()
							> 1) { 
						// LEXERR: leading zero
						tokenWriter.printf(lineCounter + "\t\t\t" + numString
								+ "\t\t99 (LEXERR)\t"
								+ "5 (LEADZERO)\t" + "\n");
						tokenList.add(new Token(99, 5, numString, lineCounter));
						listingList.set(lineCounter-1, listingList.get(lineCounter-1)
								+ "LEXERR:\tLeading zero"
								+ ":\t"	+ numString +"\n");
//						listingWriter.printf("LEXERR:\tLeading zero"
//								+ ":\t"	+ numString +"\n");
						numString = "";
						b = f;
						getNextToken();
					}
					else {
						tokenWriter.printf(lineCounter + "\t\t\t"
								+ numString
								+ "\t\t\t26 (INT)\t"
								+ "0 (NULL)\n");
						tokenList.add(new Token(26, 0, lineCounter));
						numString = "";
						b = f;
						getNextToken();
					}

				}
			}


			// End LONGREAL, REAL, INT machine code



			// Begin CATCHALL machine: ., .., [], (), ;, :, +, -, *, /, ,
			if(buffer[f] == '.') {
				f++;
				if(buffer[f] == '.') {
					tokenWriter.printf(lineCounter + "\t\t\t.."
							+ "\t\t\t4 (CATCHALL)\t9 (DOTDOT)\n");
					tokenList.add(new Token(4, 9, lineCounter));
					f++;
					b = f;
					getNextToken();
				}
				else {
					tokenWriter.printf(lineCounter + "\t\t\t."
							+ "\t\t\t4 (CATCHALL)\t8 (DOT)\n");
					tokenList.add(new Token(4, 8, lineCounter));
					b = f;
					getNextToken();
				}
			}
//			if(buffer[f] == '4') {
//				tokenWriter.printf(lineCounter + "\t\t\t"
//						+ numString
//						+ "\t\t\t26 (INT)\t"
//						+ "0 (NULL)\n");
//				tokenList.add(new Token(26, 0));
//				numString = "";
//				b = f;
//				getNextToken();
//			}
			if(buffer[f] == '[') {
				tokenWriter.printf(lineCounter + "\t\t\t["
						+ "\t\t\t4 (CATCHALL)\t1 (LEFTBRACK)\n");
				tokenList.add(new Token(4, 1, lineCounter));
				f++;
				b = f;
				getNextToken();
			}
			if(buffer[f] == ']') {
				tokenWriter.printf(lineCounter + "\t\t\t]"
						+ "\t\t\t4 (CATCHALL)\t2 (RIGHTBRACK)\n");
				tokenList.add(new Token(4, 2, lineCounter));
				f++;
				b = f;
				getNextToken();
			}
			if(buffer[f] == '(') {
				tokenWriter.printf(lineCounter + "\t\t\t("
						+ "\t\t\t4 (CATCHALL)\t3 (LEFTPAREN)\n");
				tokenList.add(new Token(4, 3, lineCounter));
				f++;
				b = f;
				getNextToken();
			}
			if(buffer[f] == ')') {
				tokenWriter.printf(lineCounter + "\t\t\t)"
						+ "\t\t\t4 (CATCHALL)\t4 (RIGHTPAREN)\n");
				tokenList.add(new Token(4, 4, lineCounter));
				f++;
				b = f;
				getNextToken();
			}
			if(buffer[f] == ';') {
				tokenWriter.printf(lineCounter + "\t\t\t;"
						+ "\t\t\t4 (CATCHALL)\t5 (SEMICOLON)\n");
				tokenList.add(new Token(4, 5, lineCounter));
				f++;
				b = f;
				getNextToken();
			}
			if(buffer[f] == ':') {
				tokenWriter.printf(lineCounter + "\t\t\t:"
						+ "\t\t\t4 (CATCHALL)\t\t6 (COLON)\n");
				tokenList.add(new Token(4, 6, lineCounter));
				f++;
				b = f;
				getNextToken();
			}
			if(buffer[f] == ',') {
				tokenWriter.printf(lineCounter + "\t\t\t,"
						+ "\t\t\t4 (CATCHALL)\t\t7 (COMMA)\n");
				tokenList.add(new Token(4, 7, lineCounter));
				f++;
				b = f;
				getNextToken();
			}
			if(buffer[f] == '+') {
				tokenWriter.printf(lineCounter + "\t\t\t+"
						+ "\t\t\t2 (ADDOP)\t\t1 (PLUS)\n");
				tokenList.add(new Token(2, 1, lineCounter));
				f++;
				b = f;
				getNextToken();
			}
			if(buffer[f] == '-') {
				tokenWriter.printf(lineCounter + "\t\t\t-"
						+ "\t\t\t2 (ADDOP)\t\t2 (MINUS)\n");
				tokenList.add(new Token(2, 2, lineCounter));
				f++;
				b = f;
				getNextToken();
			}
			if(buffer[f] == '*') {
				f++;
				b = f;
				tokenWriter.printf(lineCounter + "\t\t\t*"
						+ "\t\t\t3 (MULOP)\t\tMULT\n");
				tokenList.add(new Token(3, 1, lineCounter));
				getNextToken();
			}
			if(buffer[f] == '/') {
				f++;
				b = f;
				tokenWriter.printf(lineCounter + "\t\t\t/"
						+ "\t\t\t3 (MULOP)\t\t2 (DIV)\n");
				tokenList.add(new Token(3, 2, lineCounter));
				getNextToken();
			}


			// Begin source file newline
			// If a newline character is encountered, break/move to next line
			if(buffer[f] == '\u0000') {
				f = 0;
				b = 0;
				break;
			}
			// TODO i'm changing this
			if(buffer[f] == 'E' && buffer[f+1] == '4') {
				if(symbolTable.contains("E4")) {
					tokenWriter.printf(lineCounter + "\t\t\t" + "E4"
							+ "\t\t\t25 (ID)\t\t"
							+ "loc" + symbolTable.indexOf("E4")
							+ " (ptr to sym tab)"
							+ "\n");
					tokenList.add(new Token(25, 
							symbolTable.indexOf("E4"), lineCounter));
				}
				// Else the ID is not in the symbol table
				else {
					symbolTable.add("E4");
					tokenWriter.printf(lineCounter + "\t\t\t" + "E4"
							+ "\t\t\t25 (ID)\t\t\t"
							+ symbolTable.indexOf("E4")
							+ " (ptr to sym tab)"
							+ "\n");
					tokenList.add(new Token(25, 0, lineCounter));
				}
				f++;
				b = f;
			}
			
			// E
			if(buffer[f] == 'E') {
				if(symbolTable.contains("E")) {
					tokenWriter.printf(lineCounter + "\t\t\t" + "E"
							+ "\t\t\t25 (ID)\t\t"
							+ "loc" + symbolTable.indexOf("E")
							+ " (ptr to sym tab)"
							+ "\n");
					tokenList.add(new Token(25, 
							symbolTable.indexOf("E"), lineCounter));
				}
				// Else the ID is not in the symbol table
				else {
					symbolTable.add("E");
					tokenWriter.printf(lineCounter + "\t\t\t" + "E"
							+ "\t\t\t25 (ID)\t\t\t"
							+ symbolTable.indexOf("E")
							+ " (ptr to sym tab)"
							+ "\n");
					tokenList.add(new Token(25, 0, lineCounter));
				}
				f++;
				b = f;
			}
			
			// After all of the machines are through, if we haven't found
			// a token that matches what's on the buffer, return an error
			// Write to listing file and token file			
			if(!(Character.isDigit(buffer[f])) && !(Character.isWhitespace(buffer[f]) 
					|| buffer[f] == '\n' 
					|| buffer[f] == '\t' 
					|| buffer[f] == '\r'
					|| Character.isSpaceChar(buffer[f])
					|| buffer[f] == '%'
					|| buffer[f] == 'E'
					)) {
				tokenWriter.printf(lineCounter + "\t\t\t" + buffer[f] + "\t\t\t"
						+"99"
						+ " (LEXERR)\t" +  "1 (UNRECOGSYM)" +"\n");
				listingList.set(lineCounter-1, listingList.get(lineCounter-1)
						+ "LEXERR:\tUnrecognized Symbol:\t"
						+ buffer[f] + "\n");
				String x = buffer[f] + "";
				tokenList.add(new Token(99, 1, x, lineCounter));
//				listingWriter.printf("LEXERR:\tUnrecognized Symbol:\t"
//						+ buffer[f] + "\n");
			}
//			 TODO WHAT IS THIS?
//			IF(CHARACTER.ISDIGIT(BUFFER[F])) {
//				TOKENWRITER.PRINTF(LINECOUNTER + "\T\T\T"
//						+ BUFFER[F]
//								+ "\T\T\T26 (INT)\T"
//								+ "0 (NULL)\N");
//				TOKENLIST.ADD(NEW TOKEN(26, 0));
//
//			}
			if(buffer[f] == '%') {
				tokenWriter.print(lineCounter + "\t\t\t" + "%" + "\t\t\t"
						+"99"
						+ " (LEXERR)\t" +  "1 (UNRECOGSYM)" +"\n");
				tokenList.add(new Token(99, 1, lineCounter));
				listingList.set(lineCounter-1, listingList.get(lineCounter-1)
						+ "LEXERR:\tUnrecognized Symbol:\t"
						+ "%" + "\n");
//				listingWriter.print("LEXERR:\tUnrecognized Symbol:\t"
//						+ "%" + "\n");
				
			}
			// Move on in the buffer after unrecognized symbol
			f++;
			b = f;
		}		
		// Out of machine codes, so set buffer indices to zero
		f = 0;
		b = 0;		
		// Clear the buffer once all tokens have been gotten from it
		clearBuffer();
		return null;
	}

	/**
	 * This method will write the buffer's contents to the listing file
	 * with line number prefixed
	 * 
	 */
	private static void writeBufferToListing() {
		// TODO delete line
		listingWriter.printf(lineCounter + "\t\t");
//		listingList.add(lineCounter + "\t\t");
		for(int i = 0; i < buffer.length; i++) {
			// Check to see we haven't gone past our string
			if(buffer[i] != '\u0000') {
				listingWriter.print(buffer[i]);
			}
		}
		listingWriter.println();
	}

	/**
	 * This method is called once the source line has been tokenized
	 */
	private static void clearBuffer() {
		for(int i = 0; i < buffer.length; i++) {
			buffer[i] = '\u0000';
		}
	}

	/**
	 * This method will take a line from the source file and put it into the
	 * character buffer
	 * @return
	 */
	private static String getNextLine() {
		String line = sourceScanner.nextLine();
		listingList.add(lineCounter + "\t\t" + line + "\n");
		if (line.length() > 72) {
			listingWriter.printf("Source line too long: max 72 chars\n");
		}

		for(int i = 0; i < line.length(); i++) {
			buffer[i] = line.charAt(i);
		}
		return null;
	}

	private static void terminate() {
		listingWriter.close();
		tokenWriter.close();
		reservedScanner.close();
		sourceScanner.close();
		System.out.println("Files closed");
	}

	/*
	 * 
	 * 
	 * TODO lexeme list for token error reporting?
	 * 
	 * 
	 * 
	 */

	// LEXICAL ANALYZER

	// Main driver
	private static void lexicalAnalyzer() {
		parse();
	}

	private static void parse() {
		token = tokenList.pop();
		program();  // Start symbol
		match(98, 0); // End of file marker - 98
		// Write to listing file
		for(int i = 0; i < listingList.size(); i++) {
			listingWriter.print(listingList.get(i));
		}
		// Close all open files and clean up
		terminate();
		// Write listing nodes to listing file
		
	}

	private static void match(int type, int attribute) {
		System.out.println("Match " + type + " " + attribute +
				" ,  given " + token.tokenType + " " + token.attribute
				+ "\t\t" + token.lexeme);
		// TODO attributes too!
		if((type == token.tokenType && attribute == token.attribute) 
				&& token.tokenType == 98) {
			System.out.println("Success - reached end of file");

		}
		// IDs
		else if(type == 25 && token.tokenType == 25 
				&& token.tokenType != 98) {
			token = tokenList.pop();
		}
		else if((type == token.tokenType && attribute == token.attribute)
				&& token.tokenType != 98) {
			//&& attribute == token.attribute) {
			//		    	System.out.println("Match of token and type");
			token = tokenList.pop();
//			System.out.println(token.lexeme);
		}
		else if(type != token.tokenType 
				|| attribute != token.attribute) {
			System.out.println("SYNTAX ERROR: Expected "
					+ getTokenReference(token.tokenType, token.attribute)
					+ ", received " + 
					token.lexeme + " " + token.tokenType + " " 
					+ token.attribute);
			System.out.println(lineCounter);
			listingList.set(token.line-1, listingList.get(token.line-1)
					+ "SYNTAX ERROR: Expected "
					+ getTokenReference(type, attribute)
					+ ", received " + 
					token.lexeme + "\n");
			token = tokenList.pop();
		}
		else {
			token = tokenList.pop(); // Make sure you don't do this twice
		}
	}

	private static void program() {
		System.out.println("program(), " + token.tokenType);
		if(token.tokenType == 7) { 	// program
			match(7, 0);		// program
			match(25, 0);	// id
			match(4, 3);		// (
			identifier_list();
			match(4, 4);	// )
			match(4, 5);	// ;
			program_tail();
		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of 'program'"
					+ " given " + token.lexeme );
			listingList.set(token.line, listingList.get(lineCounter-1)
					+ "SYNTAX ERROR: Expecting one of 'program'"
					+ " given " + token.lexeme + "\n");

			 while(token.tokenType != 98) {   // Error recovery
					token = tokenList.pop();
			 }
		}
	}



	private static void program_tail() {
		// program' -> sub_decs cmpd_stmt .
		// program' -> cmpd_stmt .
		// program' -> declarations program''
		System.out.println("program_tail(), " + token.tokenType);
		if(token.tokenType == 17) { 	// procedure
			subprogram_declarations();
			compound_statement();
			match(4, 8);	// .
		}
		else if(token.tokenType == 5) {	// begin
			compound_statement();
			match(4, 8);	// .
		}
		else if(token.tokenType == 8) {	// var
			declarations();
			program_tail_tail();
		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of 'procedure',"
					+ "'begin', 'var'"
					+ " given " + token.lexeme );
			listingList.set(token.line-1, listingList.get(token.line-1)
					+ "SYNTAX ERROR: Expecting one of 'procedure',"
					+ "'begin', 'var'"
					+ " given " + token.lexeme + "\n");
			 while(token.tokenType != 98) {   // Error recovery
			 		token = tokenList.pop();
			 }
		}
	}



	private static void program_tail_tail() {
		// program'' -> subprog_decs cmpd_stmt .
		// program'' -> cmpd_stmt .
		System.out.println("program_tail_tail(), " + token.tokenType);
		if(token.tokenType == 17) { 	// procedure
			subprogram_declarations();
			compound_statement();
			match(4, 8);	// .
		}
		else if(token.tokenType == 5) {	//begin
			compound_statement();
			match(4, 8);	// .
		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of 'procedure'"
					+ ", 'begin'"
					+ " given " + token.lexeme);
			listingList.set(token.line, listingList.get(lineCounter-1)
					+ "SYNTAX ERROR: Expecting one of 'procedure'"
					+ ", 'begin'"
					+ " given " + token.lexeme + "\n");
			while(token.tokenType != 98) {
				token = tokenList.pop();
			}
		}			
		

	}



	private static void declarations() {
		// declarations -> var id : type ; declarations'
		System.out.println("declarations(), " + token.tokenType);
		if(token.tokenType == 8) { 	// var
			match(8, 0);	// var
			match(25, 0);	// id
			match(4, 6);	// :
			type();
			match(4, 5);	// ;
			declarations_tail();
		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of 'var'"
					+ " given " + token.lexeme);
			listingList.set(token.line-1, listingList.get(lineCounter-1)
					+ "SYNTAX ERROR: Expecting one of 'var'"
					+ " given " + token.lexeme + "\n");
			// procedure, begin, $: follows - 17, 5, 98
	        while(token.tokenType != 17
	        		&& token.tokenType != 5 
	        		&& token.tokenType != 98) {   // Error recovery
	            token = tokenList.pop();
	        }
		}
	}



	private static void declarations_tail() {
		// declarations_tail -> var id : type ; declarations' | e
		System.out.println("declarations_tail(), " + token.tokenType);
		if(token.tokenType == 8) { 	// var
			match(8, 0);	// var
			match(25, 0);	// id
			match(4, 6);	// :
			type();
			match(4, 5);	// ;
			declarations_tail();
		}
		else if(token.tokenType == 17 || 
				token.tokenType == 5) { // procedure, begin
			// NoOp, epsilon
		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of 'var'"
					+ "'procedure', 'begin'"
					+ " given " + token.lexeme);
			listingList.set(token.line-1, listingList.get(token.line-1)
					+ "SYNTAX ERROR: Expecting one of 'var'"
					+ "'procedure', 'begin'"
					+ " given " + token.lexeme + "\n");
			// procedure, begin, $: follows - 17, 5, 98
	        while(token.tokenType != 17
	        		&& token.tokenType != 5 
	        		&& token.tokenType != 98) {   // Error recovery
	            token = tokenList.pop();
	        }
		}

	}

	private static void type() {
		// type -> standard_type
		// type -> standard_type
		// type -> array [ num .. num ] of standard_type
		System.out.println("type(), " + token.tokenType);
		if(token.tokenType == 13) { 	// integer
			standard_type();
		}
		else if(token.tokenType == 16) { // real
			standard_type();
		}
		else if(token.tokenType == 14) {	// array
			match(14, 0);	// array
			match(4, 1);	// [
			match(13, 0);	// num	// TODO Assumption: integer
			match(4, 9);	// ..
			match(13, 0);	// num (integer)
			match(4, 2);	// ]
			match(15, 0);	// of
			standard_type();
		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of 'integer'"
					+ ", 'real', 'array'"
					+ ",  given: " + token.tokenType + " " 
					+ token.attribute);
			listingList.set(token.line-1, listingList.get(token.line-1)
					+ "SYNTAX ERROR: Expecting one of 'integer'"
					+ ", 'real', 'array'"
					+ ",  given: " + token.lexeme 
					+ "\n");
			// follows: ;, ), $
	        while(token.tokenType != 98   // Error recovery
	        		&& (token.tokenType != 4 && token.attribute != 5)
	        		&& (token.tokenType != 4 && token.attribute != 4)) {
	            token = tokenList.pop();
	        }
		}

	}



	private static void standard_type() {
		// standard_type -> integer
		// standard_type -> real
		System.out.println("standard_type(), " + token.tokenType);
		if(token.tokenType == 13) { 	// integer
			match(13, 0);
		}
		else if(token.tokenType == 16) { // real
			match(16, 0);
		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of 'integer'"
					+ ", real "
					+ " given " + token.lexeme);
			listingList.set(token.line-1, listingList.get(lineCounter-1)
					+ "SYNTAX ERROR: Expecting one of 'integer'"
					+ ", real "
					+ " given " + token.lexeme + "\n");
			// follows: ;, ), $
	        while(token.tokenType != 98   // Error recovery
	        		&& (token.tokenType != 4 && token.attribute != 5)
	        		&& (token.tokenType != 4 && token.attribute != 4)) {
	            token = tokenList.pop();
	        }
		}
	}

	private static void compound_statement() {
		// cmpd_stmt -> begin cmpd_stmt'
		System.out.println("compdound_statement(), " + token.tokenType);
		if(token.tokenType == 5) { 	// begin
			match(5, 0);	// begin
			compound_statement_tail();
		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of 'begin'"
					+ " given " + token.lexeme);
			listingList.set(token.line-1, listingList.get(lineCounter-1)
					+ "SYNTAX ERROR: Expecting one of 'begin'"
					+ " given " + token.lexeme + "\n");
			// . ; end else $
	        while(token.tokenType != 98   // Error recovery
	        		&& (token.tokenType != 4 && token.attribute != 8)
	        		&& (token.tokenType != 4 && token.attribute != 5)
	        		&& token.tokenType != 12
	        		&& token.tokenType != 6) {
	            token = tokenList.pop();
	        }
		}
	}

	private static void compound_statement_tail() {
		// cmpd_stmt' -> opt_stmts end
		// cmpd_stmt' -> opt_stmts end
		// cmpd_stmt' -> opt_stmts end
		// cmpd_stmt' -> opt_stmts end
		// cmpd_stmt' -> opt_stmts end
		// cmpd_stmt' -> end		// TODO is this right?

		System.out.println("compdound_statement_tail(), " + token.tokenType);
		if(token.tokenType == 25) { 	// id
			optional_statements();
			match(6, 0);	// end
		}
		else if(token.tokenType == 24) { 	// call
			optional_statements();
			match(6, 0);	// end
		}
		else if(token.tokenType == 5) { 	// begin
			optional_statements();
			match(6, 0);	// end
		}
		else if(token.tokenType == 18) { 	// while
			optional_statements();
			match(6, 0);	// end
		}
		else if(token.tokenType == 10) { 	// if
			optional_statements();
			match(6, 0);	// end
		}
		else if(token.tokenType == 6) { 	// end
			match(6, 0);	// end
		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of 'begin'"
					+ "'call', 'id', 'while', 'if', 'end'"
					+ " given " + token.lexeme);
			listingList.set(token.line-1, listingList.get(lineCounter-1)
					+ "SYNTAX ERROR: Expecting one of 'begin'"
					+ "'call', 'id', 'while', 'if', 'end'"
					+ " given " + token.lexeme + "\n");
			// . ; end else $
	        while(token.tokenType != 98   // Error recovery
	        		&& (token.tokenType != 4 && token.attribute != 8)
	        		&& (token.tokenType != 4 && token.attribute != 5)
	        		&& token.tokenType != 12
	        		&& token.tokenType != 6) {
	            token = tokenList.pop();
	        }
		}
	}

	private static void optional_statements() {
		// optional_statements -> statement_list
		// optional_statements -> statement_list
		// optional_statements -> statement_list
		// optional_statements -> statement_list
		// optional_statements -> statement_list

		System.out.println("optional_statements(), " + token.tokenType);
		if(token.tokenType == 25) { 	// id
			statement_list();
		}
		if(token.tokenType == 24) { 	// call
			statement_list();	    	
		}
		if(token.tokenType == 5) { 	// begin
			statement_list();
		}
		if(token.tokenType == 18) { 	// while
			statement_list();
		}
		if(token.tokenType == 10) { 	// if
			statement_list();
		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of 'id'"
					+ "'call', 'begin', 'while', 'if'"
					+ " given " + token.lexeme);
			listingList.set(token.line-1, listingList.get(lineCounter-1)
					+ "SYNTAX ERROR: Expecting one of 'id'"
					+ "'call', 'begin', 'while', 'if'"
					+ " given " + token.lexeme + "\n");
			// end $
	        while(token.tokenType != 98 
	        		&& token.tokenType != 6) {
	        	token = tokenList.pop();
	        }
		}
	}

	private static void statement_list() {
		// Stmt_list -> stmt stmt_list'
		System.out.println("statement_list(), " + token.tokenType);
		if(token.tokenType == 5) {	// begin
			statement();
			statement_list_tail();
		}
		else if(token.tokenType == 25) { 	// id
			statement();
			statement_list_tail();
		}
		else if(token.tokenType == 24) {	// call
			statement();
			statement_list_tail();
		}
		else if(token.tokenType == 18) {	// while
			statement();
			statement_list_tail();
		}
		else if(token.tokenType == 10) {	// if
			statement();
			statement_list_tail();
		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of 'id'"
					+ "'call', 'begin', 'while', 'if'"
					+ " given " + token.lexeme );
			listingList.set(token.line-1, listingList.get(token.line-1)
					+ "SYNTAX ERROR: Expecting one of 'id'"
					+ "'call', 'begin', 'while', 'if'"
					+ " given " + token.lexeme + "\n");
			// end, $
			while(token.tokenType != 6
					&& token.tokenType != 98) {
				token = tokenList.pop();
			}
		}
	}

	private static void statement_list_tail() {
		// Stmt_list' -> ; stmt stmt_list' - ;
		// Stmt_list' -> e - end

		System.out.println("statement_list_tail(), " + token.tokenType);
		if(token.tokenType == 4 && token.attribute == 5) {	// ;
			match(4, 5);	// ;
			statement();
			statement_list_tail();
		}
		// TODO I added this, for the . error
		else if(token.tokenType == 4 && token.attribute == 8) {
			match(4, 8);	// .
			statement();
			statement_list_tail();
		}
		else if(token.tokenType == 6) { 	// end
			// NoOp, epsilon
			return;
		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of ';', 'end'"
					+ " given " + token.lexeme );
			listingList.set(token.line-1, listingList.get(token.line-1)
					+ "SYNTAX ERROR: Expecting one of ';', 'end'"
					+ " given " + token.lexeme + "\n");
			// end, $
			while(token.tokenType != 6
					&& token.tokenType != 98) {
				token = tokenList.pop();
			}
		}
	}

	private static void statement() {
		// statement -> compound_statement - begin
		// Stmt -> variable assignop expression - id
		// Stmt -> procedure_statement - call
		// Stmt -> while exp do stmt - while
		// Stmt -> if expression then stmt stmt' - if

		System.out.println("statement(), " + token.tokenType);
		if(token.tokenType == 5) {	// begin
			compound_statement();
		}
		else if(token.tokenType == 25) { 	// id
			variable();
			match(21, 0);	// :=
			expression();
		}
		else if(token.tokenType == 24) {	// call
			procedure_statement();
		}
		else if(token.tokenType == 18) {	// while
			match(18, 0);	// while
			expression();
			match(19, 0);	// do
			statement();
		}
		else if(token.tokenType == 10) {	// if
			match(10, 0);	// if
			expression();
			match(11, 0);	// then
			statement();
			statement_tail();
		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of 'id'"
					+ "'call', 'begin', 'while', 'if'"
					+ " given " + token.lexeme );
			listingList.set(token.line, listingList.get(token.line)
					+ "SYNTAX ERROR: Expecting one of 'id'"
					+ "'call', 'begin', 'while', 'if'"
					+ " given " + token.lexeme + "\n");
			// else, end, ;, $
			while(token.tokenType != 98
					&& token.tokenType != 6
					&& token.tokenType != 12
					&& (token.tokenType != 4 && token.attribute != 5)) {
				token = tokenList.pop();
			}
		}
	}
	
	private static void statement_tail() {
		// stmt' -> else statement - else
		// stmt' -> e - else, ;, end

		System.out.println("statement_tail(), " + token.tokenType);
		// TODO parse table anomaly
		if(token.tokenType == 12) {	// else
			match(12, 0);	// else
			statement();
		}
		else if(token.tokenType == 25
				|| token.tokenType == 6
				|| (token.tokenType == 4 && token.attribute == 5)) {
			// else, ;, end
			// NoOp, epsilon
		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of 'else'"
					+ "';', 'end'"
					+ " given " + token.lexeme );
			listingList.set(token.line, listingList.get(lineCounter-1)
					+ "SYNTAX ERROR: Expecting one of 'else'"
					+ "';', 'end'"
					+ " given " + token.lexeme + "\n");
			// else, end, ;, $
			while(token.tokenType != 98
					&& token.tokenType != 6
					&& token.tokenType != 12
					&& (token.tokenType != 4 && token.attribute != 5)) {
				token = tokenList.pop();
			}
		}			
	}



	private static void procedure_statement() {
		// procedure_statement -> call id procedure_statement'			
		System.out.println("procedure_statement(), " + token.tokenType);
		if(token.tokenType == 24) {	// call
			match(24, 0);	// call
			match(25, 0);	// id
			procedure_statement_tail();
		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of 'call'"
					+ " given " + token.lexeme );
			listingList.set(token.line, listingList.get(lineCounter-1)
					+ "SYNTAX ERROR: Expecting one of 'call'"
					+ " given " + token.lexeme + "\n");
			// else, end, ;, $
			while(token.tokenType != 98
					&& token.tokenType != 6
					&& token.tokenType != 12
					&& (token.tokenType != 4 && token.attribute != 5)) {
				token = tokenList.pop();
			}
		}
	}

	private static void procedure_statement_tail() {
		// procedure_statement_tail -> ( expression_list ) - (
		// procedure_statement_tail -> e - else, ;, end
		System.out.println("procedure_statement_tail(), " + token.tokenType);
		if(token.tokenType == 4 && token.attribute ==3) {	// (
			match(4, 3);	// (
			expression_list();
			match(4, 4);
		}
		else if(token.tokenType == 12) {	// else
			// NoOp, epsilon
		}
		else if(token.tokenType == 4 && token.attribute == 5) {	// ;
			// NoOp, epsilon
		}
		else if(token.tokenType == 6) {	// end
			// NoOp, epsilon
		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of '(', 'else'"
					+ ", ';', 'end'"
					+ " given " + token.lexeme );
			listingList.set(token.line, listingList.get(lineCounter-1)
					+ "SYNTAX ERROR: Expecting one of '(', 'else'"
					+ ", ';', 'end'"
					+ " given " + token.lexeme + "\n");
			// else, end, ;, $
			while(token.tokenType != 98
					&& token.tokenType != 6
					&& token.tokenType != 12
					&& (token.tokenType != 4 && token.attribute != 5)) {
				token = tokenList.pop();
			}
		}
	}

	private static void expression_list() {
		// expression_list -> expression expression_list' 
		System.out.println("expression_list(), " + token.tokenType);
		if(token.tokenType == 25) { 	// id
			expression();
			expression_list_tail();
		}
		else if(token.tokenType == 13 || token.tokenType == 16 
				|| token.tokenType == 23) {	// integer, real, longreal
			expression();
			expression_list_tail();
		}
		else if(token.tokenType == 20) {	// not
			expression();
			expression_list_tail();
		}
		else if(token.tokenType == 2 && token.attribute == 1) {	// +
			expression();
			expression_list_tail();
		}
		else if(token.tokenType == 2 && token.attribute == 2) {	// -
			expression();
			expression_list_tail(); 	
		}
		else if(token.tokenType == 4 && token.attribute == 3) {	// (
			expression();
			expression_list_tail();
		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of 'id', 'num'"
					+ "'not', '+', '-', '('"
					+ " given " + token.lexeme);
			listingList.set(token.line, listingList.get(lineCounter-1)
					+ "SYNTAX ERROR: Expecting one of 'id', 'num'"
					+ "'not', '+', '-', '('"
					+ " given " + token.lexeme + "\n");
			// ), $
			while(token.tokenType != 98
					&& (token.tokenType != 4 && token.attribute != 4)) {
				token = tokenList.pop();
			}
		}
	}



	private static void expression_list_tail() {
		// expression_list_tail -> , expression expression_list' - ,
		// expression_list_tail -> e - )

		System.out.println("expression_list_tail(), " + token.tokenType);
		if(token.tokenType == 4 && token.attribute == 7) { 	// ,
			match(4, 7);	// ,
			expression();
			expression_list_tail();
		}
		else if(token.tokenType == 4 && token.attribute == 4) {	// )
			// NoOp, epsilon
		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of ',', ')'"
					+ " given " + token.lexeme);
			listingList.set(token.line, listingList.get(lineCounter-1)
					+ "SYNTAX ERROR: Expecting one of ',', ')'"
					+ " given " + token.lexeme + "\n");
			// ), $
			while(token.tokenType != 98
					&& (token.tokenType != 4 && token.attribute != 4)) {
				token = tokenList.pop();
			}
		}
	}

	private static void variable() {
		// variable -> id variable'
		System.out.println("variable(), " + token.tokenType);
		if(token.tokenType == 25) { 	// id
			match(25, 0);
			variable_tail();
		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of 'id'"
					+ " given " + token.lexeme);
			listingList.set(token.line, listingList.get(lineCounter-1)
					+ "SYNTAX ERROR: Expecting one of 'id'"
					+ " given " + token.lexeme + "\n");
			// assignop, $
			while(token.tokenType != 98
					&& token.tokenType != 21) {
				token = tokenList.pop();
			}
		}
	}

	private static void variable_tail() {
		// variable' -> e - assignop
		// variable' -> [ expression ] - [
		System.out.println("variable_tail(), " + token.tokenType);
		if(token.tokenType == 21) { 	// :=
			// NoOp, epsilon
		}
		else if(token.tokenType == 4 && token.attribute == 1) {	// [
			match(4, 1);	// [
			expression();
			match(4, 2);	// ]
		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of ':=', '['"
					+ " given " + token.lexeme);
			listingList.set(token.line, listingList.get(lineCounter-1)
					+ "SYNTAX ERROR: Expecting one of ':=', '['"
					+ " given " + token.lexeme + "\n");
			// assignop, $
			while(token.tokenType != 98
					&& token.tokenType != 21) {
				token = tokenList.pop();
			}
		}
	}

	private static void expression() {
		// expression -> simple_expression expression'
		System.out.println("expression(), " + token.tokenType);
		if(token.tokenType == 25) { 	// id
			simple_expression();
			expression_tail();
		}
		else if(token.tokenType == 26 || token.tokenType == 27
				|| token.tokenType == 28) {	// int, real, longreal
			simple_expression();
			expression_tail();
		}
		else if(token.tokenType == 20) {	// not
			simple_expression();
			expression_tail();
		}
		else if(token.tokenType == 2 && token.attribute == 1) {	// +
			simple_expression();
			expression_tail();
		}
		else if(token.tokenType == 2 && token.attribute == 2) {	// -
			simple_expression();
			expression_tail();	    	
		}
		else if(token.tokenType == 4 && token.attribute == 3) {	// (
			simple_expression();
			expression_tail();
		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of 'id', 'num'"
					+ "'not', '+', '-', '('"
					+ " given " + token.lexeme + " " + token.tokenType);
			listingList.set(token.line, listingList.get(token.line)
					+ "SYNTAX ERROR: Expecting one of 'id', 'num'"
					+ "'not', '+', '-', '('"
					+ " given " + token.lexeme + "\n");
			// do, then, else, end, $
			// ;, ,, ), ]  
			while(token.tokenType != 98
					&& token.tokenType != 6
					&& token.tokenType != 12
					&& token.tokenType != 11
					&& token.tokenType != 19
					&& (token.tokenType != 4 && token.tokenType != 5)
					&& (token.tokenType != 4 && token.tokenType != 4)
					&& (token.tokenType != 4 && token.tokenType != 2)
					&& (token.tokenType != 4 && token.tokenType != 7)) {
				token = tokenList.pop();
			}
		}
	}

	private static void expression_tail() {
		// expression' -> relop simple_expression - relop
		// expression' -> e - ), ], do, then, ,, else, ;, end
		System.out.println("expression_tail(), " + token.tokenType);
		if(token.tokenType == 1 && token.attribute == 1) { 	// relop
			match(1, 1);
			simple_expression();
		}
		else if(token.tokenType == 1 && token.attribute == 2) { // relop
			match(1, 2);
			simple_expression();
		}
		else if(token.tokenType == 1 && token.attribute == 3) { // relop
			match(1, 3);
			simple_expression();
		} 
		else if(token.tokenType == 1 && token.attribute == 4) { // relop
			match(1, 4);
			simple_expression();
		} 
		else if(token.tokenType == 1 && token.attribute == 5) { // relop
			match(1, 5);
			simple_expression();
		} 
		else if(token.tokenType == 1 && token.attribute == 6) { // relop
			match(1, 6);
			simple_expression();
		} 
		else if(token.tokenType == 4 && token.attribute == 4) {	// )
			// NoOp, epsilon
		}
		else if(token.tokenType == 4 && token.attribute == 2) {	// ]
			// NoOp, epsilon
		}
		else if(token.tokenType == 19) {	// do
			// NoOp, epsilon
		}
		else if(token.tokenType == 11) {	// then
			// NoOp, epsilon
		}
		else if(token.tokenType == 4 && token.attribute == 7) {	// ,
			// NoOp, epsilon
		}
		else if(token.tokenType == 12) {	// else
			// NoOp, epsilon
		}
		else if(token.tokenType == 4 && token.attribute == 5) {	// ;
			// NoOp, epsilon
		}
		else if(token.tokenType == 6) {	// end
			// NoOp, epsilon
		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of "
					+ "'relop', '), ']', 'do', 'then', ',', 'else', ';', "
					+ "'end' "
					+ " given " + token.lexeme);
			listingList.set(token.line, listingList.get(lineCounter-1)
					+ "SYNTAX ERROR: Expecting one of "
					+ "'relop', '), ']', 'do', 'then', ',', 'else', ';', "
					+ "'end' "
					+ " given " + token.lexeme + "\n");
			// do, then, else, end, $
			// ;, ,, ), ] 
			while(token.tokenType != 98
					&& token.tokenType != 6
					&& token.tokenType != 12
					&& token.tokenType != 11
					&& token.tokenType != 19
					&& (token.tokenType != 4 && token.tokenType != 5)
					&& (token.tokenType != 4 && token.tokenType != 4)
					&& (token.tokenType != 4 && token.tokenType != 2)
					&& (token.tokenType != 4 && token.tokenType != 7)) {
				token = tokenList.pop();
			}
		}	
	}

	private static void simple_expression() {
		// simple_expression -> term simple_expression' - id ( num not
		// simple_expression -> sign term simple_expression' - + -
		System.out.println("simple_expression(), " + token.tokenType);
		if(token.tokenType == 25) { 	// id
			term();
			simple_expression_tail();
		}
		// These are NUMs
		else if(token.tokenType == 26 || token.tokenType == 27 
				|| token.tokenType == 28) {	// integer, real, longreal
			term();
			simple_expression_tail();
		}
		else if(token.tokenType == 20) {	// not
			term();
			simple_expression_tail();
		}
		else if(token.tokenType == 2 && token.attribute == 1) {	// +
			sign();
			term();
			simple_expression_tail();
		}
		else if(token.tokenType == 2 && token.attribute == 2) {	// -
			sign();
			term();
			simple_expression_tail();	
		}
		else if(token.tokenType == 4 && token.attribute == 3) {	// (
			term();
			simple_expression_tail();
		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of 'id', 'num'"
					+ "'not', '+', '-', '('"
					+ " given " + token.lexeme);
			listingList.set(token.line-1, listingList.get(token.line-1)
					+ "SYNTAX ERROR: Expecting one of 'id', 'num'"
					+ "'not', '+', '-', '('"
					+ " given " + token.lexeme + "\n");
			// do, then, else, end, $
			// ;, ,, ), ] 
			// relop
			while(token.tokenType != 98
					&& token.tokenType != 6
					&& token.tokenType != 12
					&& token.tokenType != 11
					&& token.tokenType != 19
					&& (token.tokenType != 4 && token.tokenType != 5)
					&& (token.tokenType != 4 && token.tokenType != 4)
					&& (token.tokenType != 4 && token.tokenType != 2)
					&& (token.tokenType != 4 && token.tokenType != 7)
					&& token.tokenType != 1) {
				token = tokenList.pop();
			}
		}
	}

	private static void simple_expression_tail() {
		// simple_expression' -> addop term simple_expression - addop
		// simple_expression' -> e - relop, ), ], do, then, else, ;, end
		System.out.println("simple_expression_tail(), " + token.tokenType);
		if(token.tokenType == 2) { 	// addop
			if(token.attribute == 1) {
				match(2, 1);	// 3 1
			}
			if(token.attribute == 2) {
				match(2, 2);	
			}
			if(token.attribute == 3) {
				match(2, 3);
			}
		}
		else if(token.tokenType == 1
				// relop, end, ), ], do, then, ,, else, ;							
				|| token.tokenType == 6
				|| (token.tokenType == 4 && token.attribute == 2)
				|| (token.tokenType == 4 && token.attribute == 4)
				|| token.tokenType == 19
				|| token.tokenType == 11
				|| (token.tokenType == 4 && token.attribute == 7)
				|| token.tokenType == 12
				|| (token.tokenType == 4 && token.attribute == 5)) { 
			// NoOp, epsilon
		}

		else {
			System.out.println("SYNTAX ERROR: Expecting one of "
					+ "addop, relop, ), ], do, then, ,, else, ;, end"
					+ " given " + token.lexeme);
			listingList.set(token.line, listingList.get(lineCounter-1)
					+ "SYNTAX ERROR: Expecting one of "
					+ "addop, relop, ), ], do, then, ,, else, ;, end"
					+ " given " + token.lexeme + "\n");
			// do, then, else, end, $
			// ;, ,, ), ] 
			// relop
			while(token.tokenType != 98
					&& token.tokenType != 6
					&& token.tokenType != 12
					&& token.tokenType != 11
					&& token.tokenType != 19
					&& (token.tokenType != 4 && token.tokenType != 5)
					&& (token.tokenType != 4 && token.tokenType != 4)
					&& (token.tokenType != 4 && token.tokenType != 2)
					&& (token.tokenType != 4 && token.tokenType != 7)
					&& token.tokenType != 1) {
				token = tokenList.pop();
			}
		}
	}

	private static void sign() {
		// sign -> + | -			
		System.out.println("sign(), " + token.tokenType);
		if(token.tokenType == 24) {	// call
			match(24, 0);	// call
			match(25, 0);	// id
			procedure_statement_tail();
		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of '+', '-'"
					+ ", given " + token.lexeme );
			listingList.set(token.line, listingList.get(lineCounter-1)
					+ "SYNTAX ERROR: Expecting one of '+', '-'"
					+ ", given " + token.lexeme + "\n");
			// id, num, (, not, $
			while(token.tokenType != 98
					&& token.tokenType != 20
					&& (token.tokenType != 4 && token.attribute != 3)
					&& token.tokenType != 26
					&& token.tokenType != 27
					&& token.tokenType != 28
					&& token.tokenType != 25){
				token = tokenList.pop();
			}
		}
	}

	private static void term() {
		// term -> factor term' - num ( not id
		System.out.println("term(), " + token.tokenType);
		if(token.tokenType == 4 && token.attribute == 3) { 	// (
			factor();
			term_tail();
		}
		else if(token.tokenType == 26 || token.tokenType == 27
				|| token.tokenType == 28) {	// integer, real, longreal
			factor();
			term_tail();
		}
		else if(token.tokenType == 20) {	// not
			factor();
			term_tail();
		}
		else if(token.tokenType == 25) {	// id
			factor();
			term_tail();
		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of ')', 'num'"
					+ ", 'not', 'id' "
					+ " given " + token.lexeme);
			listingList.set(token.line, listingList.get(lineCounter-1)
					+ "SYNTAX ERROR: Expecting one of ')', 'num'"
					+ ", 'not', 'id' "
					+ " given " + token.lexeme + "\n");
			// do, then, else, end, $
			// ;, ,, ), ] 
			// relop
			// addop
			while(token.tokenType != 98
					&& token.tokenType != 6
					&& token.tokenType != 12
					&& token.tokenType != 11
					&& token.tokenType != 19
					&& (token.tokenType != 4 && token.tokenType != 5)
					&& (token.tokenType != 4 && token.tokenType != 4)
					&& (token.tokenType != 4 && token.tokenType != 2)
					&& (token.tokenType != 4 && token.tokenType != 7)
					&& token.tokenType != 1
					&& token.tokenType != 2) {
				token = tokenList.pop();
			}
		}			
	}

	private static void term_tail() {
		// term' -> mulop factor term' - mulop
		// term' -> e - addop, relop, ), ], do, then, ,, else, ;, end
		System.out.println("term_tail(), " + token.tokenType);
		if(token.tokenType == 3) { 	// mulop
			if(token.attribute == 1) {
				match(3, 1);	// 3 1
			}
			if(token.attribute == 2) {
				match(3, 2);	
			}
			if(token.attribute == 3) {
				match(3, 3);
			}
			if(token.attribute == 4) {
				match(3, 4);
			}
			if(token.attribute == 5) {
				match(3, 5);
			}

		}
		else if(token.tokenType == 1
				|| token.tokenType == 2		// addop, relop, end,
				// ), ], do, then, ,, else, ;,
				|| token.tokenType == 6
				|| (token.tokenType == 4 && token.attribute == 2)
				|| (token.tokenType == 4 && token.attribute == 4)
				|| token.tokenType == 19
				|| token.tokenType == 11
				|| (token.tokenType == 4 && token.attribute == 7)
				|| token.tokenType == 12
				|| (token.tokenType == 4 && token.attribute == 5)) { 
			// NoOp, epsilon
		}

		else {
			System.out.println("SYNTAX ERROR: Expecting one of "
					+ "addop, relop, mulop, ), ], do, then, ,, else, ;, end"
					+ " given " + token.lexeme);
			listingList.set(token.line, listingList.get(lineCounter-1)
					+ "SYNTAX ERROR: Expecting one of "
					+ "addop, relop, mulop, ), ], do, then, ,, else, ;, end"
					+ " given " + token.lexeme + "\n");
			// do, then, else, end, $
			// ;, ,, ), ] 
			// relop
			// addop
			while(token.tokenType != 98
					&& token.tokenType != 6
					&& token.tokenType != 12
					&& token.tokenType != 11
					&& token.tokenType != 19
					&& (token.tokenType != 4 && token.tokenType != 5)
					&& (token.tokenType != 4 && token.tokenType != 4)
					&& (token.tokenType != 4 && token.tokenType != 2)
					&& (token.tokenType != 4 && token.tokenType != 7)
					&& token.tokenType != 1
					&& token.tokenType != 2) {
				token = tokenList.pop();
			}
		}
	}

	private static void factor() {
		// factor -> num | ( exp ) | not factor | id factor'
		System.out.println("factor(), " + token.tokenType);
		if(token.tokenType == 25) { 	// id
			match(25, 0);	// id
			factor_tail();
		}
		else if(token.tokenType == 26) {	// int
			match(26, 0);	// int
		}
		else if(token.tokenType == 28) {	// longreal
			match(28, 0);	// longreal
		}
		else if(token.tokenType == 16) {	// real
			match(27, 0);	// real
		}

		else if(token.tokenType == 20) {	// not
			match(20, 0);	// not
			factor();
		}
		else if(token.tokenType == 4 && token.attribute == 3) {	// (
			match(4, 3);	// (
			expression();
			match(4, 4);	// )
		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of 'id', 'num'"
					+ "'not', '('"
					+ " given " + token.lexeme);
			listingList.set(token.line, listingList.get(lineCounter-1)
					+ "SYNTAX ERROR: Expecting one of 'id', 'num'"
					+ "'not', '('"
					+ " given " + token.lexeme + "\n");
			// do, then, else, end, $, ;, ,, ), ], relop, addop, mulop
			while(token.tokenType != 98
					&& token.tokenType != 6
					&& token.tokenType != 12
					&& token.tokenType != 11
					&& token.tokenType != 19
					&& (token.tokenType != 4 && token.tokenType != 5)
					&& (token.tokenType != 4 && token.tokenType != 4)
					&& (token.tokenType != 4 && token.tokenType != 2)
					&& (token.tokenType != 4 && token.tokenType != 7)
					&& token.tokenType != 1
					&& token.tokenType != 2
					&& token.tokenType != 3) {
				token = tokenList.pop();
			}
		}
	}

	private static void factor_tail() {
		// factor' -> [ exp ] - [
		// factor' -> e - mulop, addop, relop, ), ], do, then, ,, else, ;,
		// end
		System.out.println("factor_tail(), " + token.tokenType + " "
				+ token.attribute);
		if(token.tokenType == 4 && token.attribute == 1) { 	// [
			match(4, 1);	// [
			expression();
			match(4, 2);	// ]
		}
		else if(token.tokenType == 1
				|| token.tokenType == 2		// mulop, addop, relop, end, 
				|| token.tokenType == 3		// ), ], do, then, ,, else, ;,
				|| token.tokenType == 6
				|| (token.tokenType == 4 && token.attribute == 2)
				|| (token.tokenType == 4 && token.attribute == 4)
				|| token.tokenType == 19
				|| token.tokenType == 11
				|| (token.tokenType == 4 && token.attribute == 7)
				|| token.tokenType == 12
				|| (token.tokenType == 4 && token.attribute == 5)) { 
			// NoOp, epsilon
			return;
		}

		else {
			System.out.println("SYNTAX ERROR: Expecting one of "
					+ "addop, relop, mulop, ), ], do, then, ,, else, ;, end"
					+ " given " + token.lexeme);
			listingList.set(token.line, listingList.get(lineCounter-1)
					+ "SYNTAX ERROR: Expecting one of "
					+ "addop, relop, mulop, ), ], do, then, ,, else, ;, end"
					+ " given " + token.lexeme + "\n");
			// do, then, else, end, $, ;, ,, ), ], relop, addop, mulop
			while(token.tokenType != 98
					&& token.tokenType != 6
					&& token.tokenType != 12
					&& token.tokenType != 11
					&& token.tokenType != 19
					&& (token.tokenType != 4 && token.tokenType != 5)
					&& (token.tokenType != 4 && token.tokenType != 4)
					&& (token.tokenType != 4 && token.tokenType != 2)
					&& (token.tokenType != 4 && token.tokenType != 7)
					&& token.tokenType != 1
					&& token.tokenType != 2
					&& token.tokenType != 3) {
				token = tokenList.pop();
			}
		}
	}



	private static void subprogram_declarations() {
		// subprgm_decs -> subprgm_dec ; subprgm_decs'
		System.out.println("subprogram_declarations(), " + token.tokenType);
		if(token.tokenType == 17) { 	// procedure
			subprogram_declaration();
			match(4, 5);	// ;
			subprogram_declarations_tail();
		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of 'procedure'"
					+ " given " + token.lexeme);
			listingList.set(token.line, listingList.get(lineCounter-1)
					+ "SYNTAX ERROR: Expecting one of 'procedure'"
					+ " given " + token.lexeme + "\n");
			// begin, $
			while(token.tokenType != 98
					&& token.tokenType != 5) {
				token = tokenList.pop();
			}
		}
	}

	private static void subprogram_declarations_tail() {
		// subprogram_declarations' -> subprgm_dec ; subprgm_decs' - proc. 
		// subprgm_decs' -> e - begin
		if(token.tokenType == 98) {
			match(98, 0);
		}
		System.out.println("subprogram_declarations_tail(), "
				+ token.tokenType);
		if(token.tokenType == 17) { 	// procedure
			subprogram_declaration();
			match(4, 5);	// ;
			subprogram_declarations_tail();
		}
		else if(token.tokenType == 5) {	// begin
			// NoOp, epsilon
		}
		else {
//			// TODO i added this
//			if(token.tokenType == 98) {
//				match(98, 0);
//			}
			System.out.println("SYNTAX ERROR: Expecting one of 'procedure'"
					+ "'begin', "
					+ " given " + token.lexeme + token.tokenType);
			listingList.set(token.line, listingList.get(lineCounter-1)
					+ "SYNTAX ERROR: Expecting one of 'procedure'"
					+ "'begin', "
					+ " given " + token.lexeme + "\n");
			// begin, $
			while(token.tokenType != 98
					&& token.tokenType != 5) {
				token = tokenList.pop();
			}
		}
	}

	private static void subprogram_declaration() {
		// subprogram_declaration -> subprogram_head -> subprogram_dec'
		System.out.println("subprogram_declaration(), " + token.tokenType);
		if(token.tokenType == 17) { 	// procedure
			subprogram_head();
			subprogram_declaration_tail();
		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of 'procedure'"
					+ " given " + token.lexeme);
			listingList.set(token.line, listingList.get(lineCounter-1)
					+ "SYNTAX ERROR: Expecting one of 'procedure'"
					+ " given " + token.lexeme + "\n");
			// ;, $
			while(token.tokenType != 98
					&& (token.tokenType != 4 && token.attribute != 5)) {
				token = tokenList.pop();
			}
		}
	}

	private static void subprogram_declaration_tail() {
		// subprogram_declaration' -> cmpd_stmt - begin
		// subprgm_dec' -> subprgm_decs cmpd_stmt - procedure
		// subprgm_dec' -> decs subprgm_dec'' - var
		System.out.println("subprogram_declaration(), " + token.tokenType);
		// TODO
		if(token.tokenType == 98) {
			match(98, 0);
		}
		if(token.tokenType == 17) { 	// procedure
			subprogram_declarations();
			compound_statement();
		}
		else if(token.tokenType == 5) {	// begin
			compound_statement();
		}
		else if(token.tokenType == 8) {	// var
			declarations();
			subprogram_declaration_tail_tail();
		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of 'procedure'"
					+ "'begin', 'var'"
					+ " given " + token.lexeme);
			listingList.set(token.line, listingList.get(lineCounter-1)
					+ "SYNTAX ERROR: Expecting one of 'procedure'"
					+ "'begin', 'var'"
					+ " given " + token.lexeme + "\n");
			// ;, $
			while(token.tokenType != 98
					&& (token.tokenType != 4 && token.attribute != 5)) {
				token = tokenList.pop();
			}
		}
	}

	private static void subprogram_declaration_tail_tail() {
		// subprogram_declaration'' -> subprogram_decs cmpd_stmt
		// subprgm_dec'' -> compound_statement - begin
		System.out.println("subprogram_declaration_tail_tail(), "
				+ token.tokenType);
		if(token.tokenType == 17) { 	// procedure
			subprogram_declarations();
			compound_statement();
		}
		else if(token.tokenType == 5) {	// begin
			compound_statement();
		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of 'procedure'"
					+ "'begin', "
					+ " given " + token.lexeme);
			listingList.set(token.line, listingList.get(lineCounter-1)
					+ "SYNTAX ERROR: Expecting one of 'procedure'"
					+ "'begin', "
					+ " given " + token.lexeme + "\n");
			// ;, $
			while(token.tokenType != 98
					&& (token.tokenType != 4 && token.attribute != 5)) {
				token = tokenList.pop();
			}
		}
	}

	private static void subprogram_head() {
		// subprogram_head -> procedure id subprogram_head'
		System.out.println("subprogram_head(), " + token.tokenType);
		if(token.tokenType == 17) { 	// procedure
			match(17, 0);	// procedure
			match(25, 0);	// id
			subprogram_head_tail();
		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of 'procedure'"
					+ " given " + token.lexeme);
			listingList.set(token.line, listingList.get(lineCounter-1)
					+ "SYNTAX ERROR: Expecting one of 'procedure'"
					+ " given " + token.lexeme + "\n");
			// begin, procedure, var, $
			while(token.tokenType != 98
					&& token.tokenType != 5
					&& token.tokenType != 8
					&& token.tokenType != 17) {
				token = tokenList.pop();
			}
		}
	}

	private static void subprogram_head_tail() {
		// subprogram_head' -> arguments ; - ( 
		// subprgm_head' -> ; - ;
		System.out.println("subprogram_head_tail(), " + token.tokenType);
		if(token.tokenType == 4 && token.attribute == 3) { 	// (
			arguments();
			match(4, 5);	// ;
		}
		else if(token.tokenType == 5) {	// ;
			match(4, 5);	// ;
		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of '(', ';' "
					+ " given " + token.lexeme);
			listingList.set(token.line, listingList.get(lineCounter-1)
					+ "SYNTAX ERROR: Expecting one of '(', ';' "
					+ " given " + token.lexeme + "\n");
			// begin, procedure, var, $
			while(token.tokenType != 98
					&& token.tokenType != 5
					&& token.tokenType != 8
					&& token.tokenType != 17) {
				token = tokenList.pop();
			}
		}
	}

	private static void arguments() {
		// arguments -> ( parameter_list )
		System.out.println("arguments(), " + token.tokenType);
		if(token.tokenType == 4 && token.attribute == 3) { 	// (
			match(4, 3);	// (
			parameter_list();
			match(4, 4);	// )
		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of '(', "
					+ " given " + token.lexeme);
			listingList.set(token.line, listingList.get(lineCounter-1)
					+ "SYNTAX ERROR: Expecting one of '(', "
					+ " given " + token.lexeme + "\n");
			// ;, $
			while(token.tokenType != 98
					&& (token.tokenType != 4 && token.tokenType != 5)) {
				token = tokenList.pop();
			}
		}
	}

	private static void parameter_list() {
		// parameter_list() -> id : type parameter_list'
		System.out.println("arguments(), " + token.tokenType);
		if(token.tokenType == 25) { 	// id
			match(25, 0);	// id
			match(4, 6);	// :
			type();
			parameter_list_tail();
		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of '(', "
					+ " given " + token.lexeme);
			listingList.set(token.line, listingList.get(lineCounter-1)
					+ "SYNTAX ERROR: Expecting one of '(', "
					+ " given " + token.lexeme + "\n");
			// ), $
			while(token.tokenType != 98
					&& (token.tokenType != 4 && token.attribute != 4)) {
				token = tokenList.pop();
			}
		}
	}

	private static void parameter_list_tail() {
		// parameter_list() -> id : type parameter_list'
		System.out.println("arguments(), " + token.tokenType);
		if(token.tokenType == 4 && token.attribute == 5) { 	// ;
			match(4, 5);	// ;
			match(25, 0);	// id
			match(4, 6);	// :
			type();
			parameter_list_tail();

		}
		else if(token.tokenType == 4 && token.attribute == 4) {	// )
			// NoOp, epsilon
		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of '(', "
					+ " given " + token.lexeme);
			listingList.set(token.line, listingList.get(lineCounter-1)
					+ "SYNTAX ERROR: Expecting one of '(', "
					+ " given " + token.lexeme + "\n");
			// ), $
			while(token.tokenType != 98
					&& (token.tokenType != 4 && token.attribute != 4)) {
				token = tokenList.pop();
			}
		}
	}

	private static void identifier_list() {
		System.out.println("identifier_list(), " + token.tokenType);
		if(token.tokenType == 25) { 	// id
			match(25, 0);	// id
			// id_list -> id id_list'
			identifier_list_tail();

		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of 'id'"
					+ " given " + token.lexeme );
			listingList.set(token.line, listingList.get(lineCounter-1)
					+ "SYNTAX ERROR: Expecting one of 'id'"
					+ " given " + token.lexeme + "\n");
			// ), $
			while(token.tokenType != 98
					&& (token.tokenType != 4 && token.attribute != 4)) {
				token = tokenList.pop();
			}
		}
	}

	private static void identifier_list_tail() {
		// id_list' -> e | , id id_list'

		System.out.println("identifier_list_tail(), " + token.tokenType);
		if(token.tokenType == 4 && token.attribute == 4) { 	// )
			// Epsilon - NoOp, do nothing
		}
		else if (token.tokenType == 4 && token.attribute == 7) { // ,
			match(4, 7);	// ,
			match(25, 0);	// id
			identifier_list_tail();
		}
		else {
			System.out.println("SYNTAX ERROR: Expecting one of 'e', ',',"
					+ " given " + token.lexeme );
			listingList.set(token.line, listingList.get(lineCounter-1)
					+ "SYNTAX ERROR: Expecting one of 'e', ',',"
					+ " given " + token.lexeme + "\n");
			// ), $
			while(token.tokenType != 98
					&& (token.tokenType != 4 && token.attribute != 4)) {
				token = tokenList.pop();
			}
		}
		
	}
	
	
}

