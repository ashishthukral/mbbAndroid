package domain;

import java.util.Date;

import utils.MySQLFieldSkip;
import utils.SQLiteFieldSkip;


public class MbbData {

	private Integer _id;
	@MySQLFieldSkip
	@SQLiteFieldSkip
	private Date _infoTimeDate;
	private String _infoTime;
	private String _infoDeadlineTime;
	private Integer _courseId;
	private Integer _teacherId;
	private String _infoType;
	private String _infoDetails;
	@SQLiteFieldSkip
	private Character _retired;
	@MySQLFieldSkip
	private Character _rendered;

	@Override
	public String toString(){
		return _id+"---"+_infoTime/*+"---"+_infoTimeDate*/+"---"+_teacherId+"---"+_courseId+"---"+_infoType+"---"+_infoDetails+"---"+_infoDeadlineTime+"---"+_retired;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_id == null) ? 0 : _id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MbbData other = (MbbData) obj;
		if (_id == null) {
			if (other._id != null)
				return false;
		} else if (!_id.equals(other._id))
			return false;
		return true;
	}

	public Integer getId() {
		return _id;
	}
	public void setId(Integer iId) {
		_id = iId;
	}
	public String getInfoTime() {
		return _infoTime;
	}
	public void setInfoTime(String iInfoTime) {
		_infoTime = iInfoTime;
	}
	public Date getInfoTimeDate() {
		return _infoTimeDate;
	}
	public void setInfoTimeDate(Date iInfoTimeDate) {
		_infoTimeDate = iInfoTimeDate;
	}
	public Integer getCourseId() {
		return _courseId;
	}
	public void setCourseId(Integer iCourseId) {
		_courseId = iCourseId;
	}
	public Integer getTeacherId() {
		return _teacherId;
	}
	public void setTeacherId(Integer iTeacherId) {
		_teacherId = iTeacherId;
	}
	public String getInfoType() {
		return _infoType;
	}
	public void setInfoType(String iInfoType) {
		_infoType = iInfoType;
	}
	public String getInfoDetails() {
		return _infoDetails;
	}
	public void setInfoDetails(String iInfoDetails) {
		_infoDetails = iInfoDetails;
	}

	public Character getRetired() {
		return _retired;
	}
	public void setRetired(Character iRetired) {
		_retired = iRetired;
	}

	public Character getRendered() {
		return _rendered;
	}

	public void setRendered(Character iRendered) {
		_rendered = iRendered;
	}

	public String getInfoDeadlineTime() {
		return _infoDeadlineTime;
	}

	public void setInfoDeadlineTime(String iInfoDeadlineTime) {
		_infoDeadlineTime = iInfoDeadlineTime;
	}

}
