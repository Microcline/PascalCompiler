
public class VarType {
	public String id = "";
	public String type = "";
	public String prevType = "";
	
	public VarType() {
		
	}
	public VarType(String idIn, String typeIn) {
		id = idIn;
		type = typeIn;
	}
	
	public String getId() {
		return id;
	}
	public String getType() {
		return type;
	}
	public String getPrevType() {
		return prevType;
	}
}
