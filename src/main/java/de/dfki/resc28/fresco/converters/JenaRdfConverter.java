/*
 * This file is part of fReSCO. It is subject to the license terms in
 * the LICENSE file found in the top-level directory of this distribution.
 * You may not use this file except in compliance with the License.
 */
package de.dfki.resc28.fresco.converters;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;

import de.dfki.resc28.fresco.converters.exceptions.ContentNotFoundException;
import de.dfki.resc28.fresco.converters.exceptions.ConversionFailedException;
import de.dfki.resc28.fresco.converters.exceptions.InvalidContentException;
import de.dfki.resc28.fresco.converters.exceptions.UnknownInputFormatException;
import de.dfki.resc28.fresco.utils.ContentType;
import de.dfki.resc28.fresco.vocabularies.CNT;


/**
 * The JenaRdfConverter is a multi-format RDF converter based on the Jena framework.
 * It provides translations between data formats ranging from RDF/XML to Turtle or Trix.
 * The JenaRdfConverter may be used to determine the input format automatically, however
 * this feature is experimental and very limited.
 *   
 * @author resc01
 *
 */
public class JenaRdfConverter implements IRdfConverter 
{
	/**
	 * 
	 */
	public StreamingOutput convert(final ContentType inputFormat, 
								   final ContentType outputFormat, 
								   final URI documentUri,
								   final URI requestUri) 
								   throws ContentNotFoundException, ConversionFailedException 
	{
		try
		{
			// load RDF graph from inputUri
			final Model inputModel = RDFDataMgr.loadModel( documentUri.toString(), 
														   RDFLanguages.contentTypeToLang(inputFormat.getContentType()) );
			
			
			// make a deep copy of the inputModel
			final Model outputModel = ModelFactory.createModelForGraph(inputModel.getGraph());
		
			// insert triples according to actn:Producible into outputModel
			// ASK { VALUES ?b { IRI(concat("http://localhost:8080/rdf-xml/json-ld?uri=", str(?a))) } ?b rdf:type cnt:Content ; dct:isFormatOf ?a ; dct:format 'application/ld+json' . }
			Resource a = outputModel.createResource(documentUri.toString());
			Resource b = outputModel.createResource(requestUri.toString());
			outputModel.add(b, RDF.type, CNT.Content);
			outputModel.add(b, DCTerms.isFormatOf, a);
			outputModel.add(b, DCTerms.format, outputFormat.getContentType());
			
			// convert RDF graph into outputFormat
			StreamingOutput out = new StreamingOutput() 
			{
				// FIXME: how to handle/customize these exceptions?
				public void write(OutputStream output) throws IOException, WebApplicationException
				{
					RDFDataMgr.write( output, 
									  outputModel, 
									  RDFLanguages.contentTypeToLang(outputFormat.getContentType())) ;
				}
			};
			
			return out;
			
		}
		catch (Exception e)
		{
			// RiotNotFoundException - if the location is not found - the model is unchanged.
			throw new ContentNotFoundException();
		}
	}
	
	/**
	 * 
	 */
	public StreamingOutput convert(final ContentType outputFormat, 
								   final URI documentUri)
								   throws ContentNotFoundException, ConversionFailedException, UnknownInputFormatException
	{
		// sniff content-type of inputUri
		Lang inputLang = RDFDataMgr.determineLang(documentUri.toString(), null, null);
		
		if (inputLang == null)
			throw new UnknownInputFormatException();
		
		// FIXME!
		return convert( ContentType.create(inputLang.getContentType().getContentType()),
						outputFormat, 
						documentUri ,
						null );
	}

	/**
	 * 
	 */
	public StreamingOutput convert(final ContentType inputFormat, 
								   final ContentType outputFormat,
								   final InputStream documentContent,
								   final URI absoluteRequestUri)
								   throws InvalidContentException, ConversionFailedException
	{
		try 
		{
			// load documentContent into RDF model
			final Model inputModel = ModelFactory.createDefaultModel();
			inputModel.read( documentContent, 
							 absoluteRequestUri.toString() + "/" + UUID.randomUUID().toString(),	// serves as proxy baseUri  
							 inputFormat.getContentType());
					
			// convert RDF graph into outputFormat
			StreamingOutput out = new StreamingOutput() 
			{
				// FIXME: how to handle/customize these exceptions?
				public void write(OutputStream output) throws IOException, WebApplicationException
				{
					RDFDataMgr.write( output, 
									  inputModel, 
									  RDFLanguages.contentTypeToLang(outputFormat.getContentType())) ;
				}
			};
			
			return out;
		}
		catch (Exception e)
		{
			throw new InvalidContentException();
		}
	}


	/**
	 * FIXME: Find a better way to detect content-type!
	 * FIXME: How about parsing the content?
	 * 
	 * TODO: Add mime-type to signature; otherwise no detect possible!
	 */
	public StreamingOutput convert(final String inputFormatHint,
								   final ContentType outputFormat,
								   final InputStream documentContent,
								   final URI absoluteRequestUri)
								   throws InvalidContentException, ConversionFailedException, UnknownInputFormatException
	{
		try
		{
			// load documentContent into RDF model
			final Model inputModel = ModelFactory.createDefaultModel();
			inputModel.read( documentContent, 
							 absoluteRequestUri.toString() + "/" + UUID.randomUUID().toString(),	// serves as proxy baseUri 
						     inputFormatHint);
					
			// convert RDF graph into outputFormat
			StreamingOutput out = new StreamingOutput() 
			{
				// FIXME: how to handle/customize these exceptions?
				public void write(OutputStream output) throws IOException, WebApplicationException
				{
					RDFDataMgr.write( output, 
									  inputModel, 
									  RDFLanguages.contentTypeToLang(outputFormat.getContentType())) ;
				}
			};
			
			return out;
		}
		catch (Exception e)
		{
			throw new UnknownInputFormatException();
		}
	}
}
