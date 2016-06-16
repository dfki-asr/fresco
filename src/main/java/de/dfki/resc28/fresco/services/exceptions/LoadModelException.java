/*
 * This file is part of fReSCO. It is subject to the license terms in
 * the LICENSE file found in the top-level directory of this distribution.
 * You may not use this file except in compliance with the License.
 */
package de.dfki.resc28.fresco.services.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class LoadModelException extends WebApplicationException 
{
	private static final long serialVersionUID = -8337108886716909118L;

	public LoadModelException(String inputUri) 
	{
		super(Response.status(421)
        			  .entity(String.format("Failed to retrieve input from %s.", inputUri))
        			  .type(MediaType.TEXT_PLAIN)
        			  .build());
    }
}
