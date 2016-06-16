/*
 * This file is part of fReSCO. It is subject to the license terms in
 * the LICENSE file found in the top-level directory of this distribution.
 * You may not use this file except in compliance with the License.
 */
package de.dfki.resc28.fresco;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import de.dfki.resc28.fresco.services.JenaConversionService;



/**
 * @author resc01
 *
 */
@ApplicationPath("/")
public class Server extends Application 
{
	@Override
	public Set<Class<?>> getClasses() 
	{
		super.getClasses();
		final Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add( JenaConversionService.class);
		return classes;
	}
}
