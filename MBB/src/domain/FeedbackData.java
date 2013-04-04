package domain;

import utils.MySQLFieldSkip;

public class FeedbackData {
	
	private Float _lookfeel;
	private Float _usability;
	private Float _quality;
	@MySQLFieldSkip
	private Float _avg;
	
	public Float getLookfeel() {
		return _lookfeel;
	}
	public void setLookfeel(Float iLookfeel) {
		_lookfeel = iLookfeel;
	}
	public Float getUsability() {
		return _usability;
	}
	public void setUsability(Float iUsability) {
		_usability = iUsability;
	}
	public Float getQuality() {
		return _quality;
	}
	public void setQuality(Float iQuality) {
		_quality = iQuality;
	}
	public Float getAvg() {
		_avg=(_lookfeel+_usability+_quality)/3;
		return _avg;
	}
	public void setAvg(Float iAvg) {
		_avg = iAvg;
	}
}
