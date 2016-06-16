/*
 * This file is part of fReSCO. It is subject to the license terms in
 * the LICENSE file found in the top-level directory of this distribution.
 * You may not use this file except in compliance with the License.
 */
package de.dfki.resc28.fresco.services.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.jena.riot.Lang;

public class InputFormatException extends WebApplicationException 
{
	private static final long serialVersionUID = 2451820342585627008L;

	public InputFormatException(Lang requiredInputLang) 
	{
		super(Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE)
        			  .entity(String.format("Input is not of content-type %s.", requiredInputLang.getContentType().getContentType()))
        			  .type(MediaType.TEXT_PLAIN)
        			  .build());
    }
}
 