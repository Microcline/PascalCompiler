import java.util.LinkedList;


public class Node {
	
	String color = "";
	String id = "";
	String type = "";
	int numParam = 0;
	int offset = -1;
	Node prev;
	Node next;
	Node down;
	private String idIn;
	LinkedList paramList;
	LinkedList typeList;
	// Default constructor
	public Node() {
		
	}
	
	// ex. a, blue, int, addr:0
	public Node(String idIn, String colorIn, String typeIn, int offsetIn) {
		id = idIn;
		color = colorIn;
		type = typeIn;
		offset = offsetIn;
		numParam = offsetIn;
		typeList = new LinkedList();
	}
	
//	public Node(String idIn, String colorIn, String typeIn, int paramIn) {
//		id = idIn;
//		color = colorIn;
//		type = typeIn;
//		numParam = paramIn;
//	}
	
	public Node(String idIn, String colorIn, String typeIn) {
		id = idIn;
		color = colorIn;
		type = typeIn;
		typeList = new LinkedList();
	}
	
	public Node(String idIn, String colorIn, String typeIn, int offsetIn,
			LinkedList paramIn) {
		id = idIn;
		color = colorIn;
		type = typeIn;
		offset = offsetIn;
		numParam = offsetIn;
		paramList = paramIn;
		typeList = new LinkedList();
	}

}
