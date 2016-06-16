/*
 * This file is part of fReSCO. It is subject to the license terms in
 * the LICENSE file found in the top-level directory of this distribution.
 * You may not use this file except in compliance with the License.
 */
package de.dfki.resc28.fresco.converters.exceptions;

public class ContentNotFoundException extends Exception 
{
	public ContentNotFoundException() { }
	
	public ContentNotFoundException(String message)
	{
		super(message);
	}

	public ContentNotFoundException(Throwable cause)
	{
		super(cause);
	}

	public ContentNotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public ContentNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	private static final long serialVersionUID = -1343170817753189345L;
}
