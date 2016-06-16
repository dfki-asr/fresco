/*
 * This file is part of fReSCO. It is subject to the license terms in
 * the LICENSE file found in the top-level directory of this distribution.
 * You may not use this file except in compliance with the License.
 */
package de.dfki.resc28.fresco.converters;

import java.io.InputStream;
import java.net.URI;

import javax.ws.rs.core.StreamingOutput;

import de.dfki.resc28.fresco.converters.exceptions.ContentNotFoundException;
import de.dfki.resc28.fresco.converters.exceptions.ConversionFailedException;
import de.dfki.resc28.fresco.converters.exceptions.InvalidContentException;
import de.dfki.resc28.fresco.converters.exceptions.UnknownInputFormatException;
import de.dfki.resc28.fresco.utils.*;



public interface IRdfConverter 
{
	/**
	 * 
	 * @param inputFormat
	 * @param outputFormat
	 * @param documentUri
	 * @return
	 * @throws ContentNotFoundException
	 * @throws ConversionFailedException
	 */
	public abstract StreamingOutput convert(final ContentType inputFormat, 
											final ContentType outputFormat, 
											final URI documentUri,
											final URI requestUri) 
											throws ContentNotFoundException, ConversionFailedException;

	/**
	 * 
	 * @param outputFormat
	 * @param documentUri
	 * @return
	 * @throws ContentNotFoundException
	 * @throws ConversionFailedException
	 * @throws UnknownInputFormatException
	 */
	public abstract StreamingOutput convert(final ContentType outputFormat, 
											final URI documentUri)
											throws ContentNotFoundException, ConversionFailedException, UnknownInputFormatException;

	/**
	 * 
	 * @param inputFormat
	 * @param outputFormat
	 * @param documentContent
	 * @param absoluteRequestUri: Serves as baseUri in case documentContent contains unresolved relative URIs
	 * @return
	 * @throws InvalidContentException
	 * @throws ConversionFailedException
	 */
	public abstract StreamingOutput convert(final ContentType inputFormat, 
											final ContentType outputFormat, 
											final InputStream documentContent,
											final URI absoluteRequestUri)
											throws InvalidContentException, ConversionFailedException;
	
	/**
	 * 
	 * @param inputFormatHint
	 * @param outputFormat
	 * @param documentContent
	 * @param absoluteRequestUri: Serves as baseUri in case documentContent contains unresolved relative URIs
	 * @return
	 * @throws InvalidContentException
	 * @throws ConversionFailedException
	 * @throws UnknownInputFormatException
	 */
	public abstract StreamingOutput convert(final String inputFormatHint,
											final ContentType outputFormat, 
											final InputStream documentContent,
											final URI absoluteRequestUri)
											throws InvalidContentException, ConversionFailedException, UnknownInputFormatException;
	

}
