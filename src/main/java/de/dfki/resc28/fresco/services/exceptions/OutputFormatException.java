/*
 * This file is part of fReSCO. It is subject to the license terms in
 * the LICENSE file found in the top-level directory of this distribution.
 * You may not use this file except in compliance with the License.
 */
package de.dfki.resc28.fresco.services.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class OutputFormatException extends WebApplicationException 
{
	private static final long serialVersionUID = 2017467132295824629L;

	public OutputFormatException(String outputFormat) 
	{
		super(Response.status(Response.Status.NOT_ACCEPTABLE)
        			  .entity(String.format("The requested conversion into %s is not available. Sorry!", outputFormat))
        			  .type(MediaType.TEXT_PLAIN)
        			  .build());
    }
}
