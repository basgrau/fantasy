package de.basgrau.hades.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import de.basgrau.hermes.shared.SharedValues;

@ApplicationPath(SharedValues.UNTERWELT_PATH)
public class HadesUnterweltApiConfig extends Application {
	public Set<Class<?>> getClasses() {
		return new HashSet<Class<?>>(Arrays.asList(HadesUnterweltAPI.class));
	}
}
