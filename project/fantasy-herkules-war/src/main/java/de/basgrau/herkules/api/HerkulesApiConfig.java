package de.basgrau.herkules.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import de.basgrau.hermes.shared.SharedValues;

/**
 * HerkulesApiConfig.
 * @author basgrau
 *
 */
@ApplicationPath(SharedValues.HERKULES_PATH)
public class HerkulesApiConfig extends Application {
	public Set<Class<?>> getClasses() {
		return new HashSet<Class<?>>(Arrays.asList(HerkulesApi.class));
	}
}