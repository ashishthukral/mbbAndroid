package common;

import java.util.Arrays;
import java.util.List;

public enum Professor {
	
	// assumption - only 1 teacher per course
	BALBOA(998,"Dr. Balboa",Arrays.asList(Course.MATHS)),TYLER(999,"Dr. Tyler",Arrays.asList(Course.PHY,Course.CHE));

	
	private final Integer _id;
	private final String _name;
	private final List<Course> _courses;
	
	private Professor(Integer iId,String iName,List<Course> iCourses){
		_id=iId;
		_name=iName;
		_courses=iCourses;
	}
	
	public static final String fetchNameForId(Integer iId){
		String aName="";
		for(Professor s:Professor.values()){
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

	public List<Course> getCourses() {
		return _courses;
	}
	
	
}
