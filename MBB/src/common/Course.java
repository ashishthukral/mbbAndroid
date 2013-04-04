package common;

public enum Course {
	
	CHE(776,"Chemistry"),PHY(777,"Physics"),MATHS(778,"Maths");

	
	private final Integer _id;
	private final String _name;
	
	private Course(Integer iId,String iName){
		_id=iId;
		_name=iName;
	}
	
	public static final String fetchNameForId(Integer iId){
		String aName="";
		for(Course s:Course.values()){
			if(s.getId().equals(iId)){
				aName=s.getName();
				break;
			}
		}
		return aName;
	}

	public Integer getId() {
		return _id;
	}

	public String getName() {
		return _name;
	}
	
	
}
