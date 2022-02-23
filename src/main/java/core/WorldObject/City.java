package core.WorldObject;

import googledocs.legis.results.LocationResult;

import java.util.HashSet;
import java.util.Set;

public class City {
	
	public static final City TESTCITY = new City();
	
	public final int locationX = 0, locationY = 0; // TODO
	
	@Override
	public String toString() {
		return "TESTCITY"; // TODO
	}
	
	public Set<Long> getNobles() {
		return new HashSet<>(); // TODO
	}
	public Set<LocationResult> getArea() {
		return new HashSet<>(); // TODO
	}
	
}
