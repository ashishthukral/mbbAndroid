package common;

import java.util.Arrays;
import java.util.List;

public enum Student {

	// assumption - only 1 teacher per course
	FRODO(111,"Frodo",Arrays.asList(Course.PHY,Course.MATHS)),BILBO(112,"Bilbo",Arrays.asList(Course.PHY,Course.CHE));


	private final Integer _id;
	private final String _name;
	private final List<Course> _courses;

	private Student(Integer iId,String iName,List<Course> iCourses){
		_id=iId;
		_name=iName;
		_courses=iCourses;
	}

	public static final String fetchNameForId(Integer iId){
		String aName="";
		for(Student s:Student.values()){
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
