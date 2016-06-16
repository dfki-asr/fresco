/*
 * This file is part of fReSCO. It is subject to the license terms in
 * the LICENSE file found in the top-level directory of this distribution.
 * You may not use this file except in compliance with the License.
 */
package de.dfki.resc28.fresco.services;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import de.dfki.resc28.fresco.constants.ContentTypes;
import de.dfki.resc28.fresco.converters.JenaRdfConverter;
import de.dfki.resc28.fresco.converters.exceptions.ContentNotFoundException;
import de.dfki.resc28.fresco.converters.exceptions.ConversionFailedException;
import de.dfki.resc28.fresco.converters.exceptions.InvalidContentException;
import de.dfki.resc28.fresco.converters.exceptions.UnknownInputFormatException;
import de.dfki.resc28.fresco.utils.ContentType;


/**
 * The JenaConversionService is a RESTful service for multi-format RDF conversion.  
 * It provides syntax translations between data formats ranging from RDF/XML to Turtle or Trix. 
 * The JenaConversionService allows for conversions triggered either by URI or by direct text input. 
 * The usage of the detect parameter will prompt the JenaConversionService to try to determine the input format automatically.
 * 
 * Furthermore it comes with a straightforward Linked API.
 * 
 * @author resc01
 */
@Path("")
public class JenaConversionService
{
	//================================================================================
    // Affordance-related request handlers
    //================================================================================
	
	@OPTIONS
	@Path("{inputFormat: (json-ld|n3|n-quads|n-triples|rdf-json|rdf-xml|trig|trix|turtle)}/{outputFormat: (json-ld|n3|n-quads|n-triples|rdf-json|rdf-xml|trig|trix|turtle)}")
	@Produces("text/turtle")
	public Response describeAction( @PathParam("inputFormat") String inputFormat, @PathParam("outputFormat") String outputFormat )
	{
		ST actionTemplate = new STGroupFile(fContext.getRealPath("/affordances/action.stg")).getInstanceOf("ACTION");

		actionTemplate.add("baseUri", fRequestUriInfo.getBaseUri().toString());
		actionTemplate.add("source", inputFormat);
		actionTemplate.add("target", outputFormat);
		actionTemplate.add("ctSource", SUPPORTED_LANGUAGES.get(inputFormat).toHeaderString());
		actionTemplate.add("ctTarget", SUPPORTED_LANGUAGES.get(outputFormat).toHeaderString());
		
		String actionDescription = actionTemplate.render();
		
		return Response.ok(actionDescription)
					   .build();
	} 


	//================================================================================
    // RdfConversion-related request handlers
    //================================================================================

	/**
	 * Converts RDF representation of user-specified inputUri from user-specified inputFormat to user-specified outputFormat via GET interface. 
	 * TODO: add ExceptionMapper for 404 errors
	 * 
	 * @param inputFormat: (json-ld|n3|n-quads|n-triples|rdf-json|rdf-xml|trig|trix|turtle)
	 * @param outputFormat: (json-ld|n3|n-quads|n-triples|rdf-json|rdf-xml|trig|trix|turtle)
	 * @param inputUri: a valid URI for a RDF document
	 * @return
	 */
	@GET
	@Path("{inputFormat: (json-ld|n3|n-quads|n-triples|rdf-json|rdf-xml|trig|trix|turtle)}/{outputFormat: (json-ld|n3|n-quads|n-triples|rdf-json|rdf-xml|trig|trix|turtle)}")
	public Response convertByGET( @PathParam("inputFormat") String inputFormat,
							 	  @PathParam("outputFormat") String outputFormat,
							 	  @HeaderParam("content-type") String contentType,
							 	  @HeaderParam("accept") String accept,
							 	  @QueryParam("uri") String inputUri)
	{
		try 
		{
			// Which MIME types should we prefer? Since path-fragments can't be null, we default to these?
			ContentType source = SUPPORTED_LANGUAGES.get(inputFormat) ;
			ContentType target = SUPPORTED_LANGUAGES.get(outputFormat) ;

			// In case header fields are set, we check for MIME type collisions
//			if ((contentType != null) && !SUPPORTED_LANGUAGES.get(contentType).equals(source))
//				throw new WebApplicationException("Invalid 'content-type' header!");
//			else if ((accept != "*/*") && !SUPPORTED_LANGUAGES.get(accept).equals(target))
//				throw new WebApplicationException("Invalid 'accept' header!");
			
			// perform conversion
			URI contentLocation = fRequestUriInfo.getRequestUri();
			StreamingOutput out = new JenaRdfConverter().convert( source, target, new URI(inputUri), contentLocation);
					
			// generate response
			return Response.ok(out)
						   .type(SUPPORTED_LANGUAGES.get(outputFormat).getContentType())
						   .header("Content-Location", contentLocation)
						   .build();	
		} 
		catch (URISyntaxException e) 
		{
			// FIXME: catch all exceptions and handle as WebExceptions
			throw new WebApplicationException();
		} 
		catch (ContentNotFoundException e) 
		{
			// FIXME: catch all exceptions and handle as WebExceptions
			throw new WebApplicationException();
		} 
		catch (ConversionFailedException e) 
		{
			// FIXME: catch all exceptions and handle as WebExceptions
			throw new WebApplicationException();
		}
	}

	/**
	 * Auto-converts RDF representation of user-specified inputUri to user-specified outputFormat via GET interface. 
	 * @param outputFormat
	 * @param inputUri
	 * @return
	 */
	@GET
	@Path("/detect/{outputFormat: (json-ld|n3|n-quads|n-triples|rdf-json|rdf-xml|trig|trix|turtle)}")
	public Response autoConvertByGET( @PathParam("outputFormat") String outputFormat, 
									  @QueryParam("uri") String inputUri)
	{
		try 
		{
			// perform conversion
			StreamingOutput out = new JenaRdfConverter().convert( SUPPORTED_LANGUAGES.get(outputFormat),
																  new URI(inputUri) );				
			// generate response
			return Response.ok(out)
						   .type(SUPPORTED_LANGUAGES.get(outputFormat).getContentType())
						   .header("Content-Location", inputUri)
						   .build();	
		} 
		catch (URISyntaxException e) 
		{
			// FIXME: catch all exceptions and handle as WebExceptions
			throw new WebApplicationException();
		} 
		catch (ContentNotFoundException e) 
		{
			// FIXME: catch all exceptions and handle as WebExceptions
			throw new WebApplicationException();
		} 
		catch (ConversionFailedException e) 
		{
			// FIXME: catch all exceptions and handle as WebExceptions
			throw new WebApplicationException();
		} 
		catch (UnknownInputFormatException e) 
		{
			// FIXME: catch all exceptions and handle as WebExceptions
			throw new WebApplicationException();
		}
	}

	/**
	 * TODO: we need a user-provided baseUri for the result graph in case there isn't any @base or @prefix in inputGraph!
	 * TODO: JenaRdfConverter defaults to fresco as baseUri in case non is provided!
	 * 
	 * Converts user-specified RDF representation from user-specified inputFormat to user-specified outputFormat via Direct-POST interface.
	 * @param inputFormat
	 * @param outputFormat
	 * @param documentContent
	 * @return
	 */
	@POST
	@Path("{inputFormat: (json-ld|n3|n-quads|n-triples|rdf-json|rdf-xml|trig|trix|turtle)}/{outputFormat: (json-ld|n3|n-quads|n-triples|rdf-json|rdf-xml|trig|trix|turtle)}")
	public Response convertByDirectPOST( @PathParam("inputFormat") String inputFormat,
		 	  							 @PathParam("outputFormat") String outputFormat,
		 	  							 InputStream documentContent)
	{
		try 
		{
			StreamingOutput out = new JenaRdfConverter().convert( SUPPORTED_LANGUAGES.get(inputFormat), 
																  SUPPORTED_LANGUAGES.get(outputFormat), 
																  documentContent,
																  fRequestUriInfo.getAbsolutePath() );
			
			// generate response
			return Response.ok(out)
						   .type(SUPPORTED_LANGUAGES.get(outputFormat).getContentType())
						   .build();
		}
		catch (ConversionFailedException e) 
		{
			// FIXME: catch all exceptions and handle as WebExceptions
			throw new WebApplicationException();
		} 
		catch (InvalidContentException e) 
		{
			// FIXME: catch all exceptions and handle as WebExceptions
			throw new WebApplicationException();
		}
	}
	
	/**
	 * TODO: we need a user-provided baseUri for the result graph in case there isn't any @base or @prefix in inputGraph!
	 * TODO: JenaRdfConverter defaults to fresco as baseUri in case non is provided!
	 * 
	 * Auto-converts user-specified RDF representation to user-specified outputFormat via Direct-POST interface.
	 * @param inputFormatHint
	 * @param outputFormat
	 * @param documentContent
	 * @return
	 */
	@POST
	@Path("/detect/{outputFormat: (json-ld|n3|n-quads|n-triples|rdf-json|rdf-xml|trig|trix|turtle)}")
	public Response autoConvertByDirectPOST( @HeaderParam("Content-Type") String inputFormatHint,
		 	  							 	 @PathParam("outputFormat") String outputFormat,
		 	  							 	 InputStream documentContent)
	{
		try 
		{
			StreamingOutput out = new JenaRdfConverter().convert( inputFormatHint, 
					  											  SUPPORTED_LANGUAGES.get(outputFormat), 
					  											  documentContent,
					  											  fRequestUriInfo.getAbsolutePath() );		
				
			// generate response
			return Response.ok(out)
						   .type(SUPPORTED_LANGUAGES.get(outputFormat).getContentType())
						   .build();
		}
		catch (ConversionFailedException e) 
		{
			// FIXME: catch all exceptions and handle as WebExceptions
			throw new WebApplicationException();
		} 
		catch (InvalidContentException e) 
		{
			// FIXME: catch all exceptions and handle as WebExceptions
			throw new WebApplicationException();
		} 
		catch (UnknownInputFormatException e) 
		{
			// FIXME: catch all exceptions and handle as WebExceptions
			throw new WebApplicationException();		}
	}

	


	
//	/**
//	 * TODO: this is just a dirty hack!
//	 * 
//	 * @return
//	 */
//	@OPTIONS
//	@Produces("text/turtle")
//	public Response options(@PathParam("inputFormat") String inputFormat)
//    {
//		// FIXME: add "/" to baseUri since otherwise JENA will fuck up!
//		final Model affordanceModel = FileManager.get().loadModel(fContext.getRealPath("/rdf/turtle.ttl"), fRequestUriInfo.getAbsolutePath().toString(), "TURTLE");
//	
//		StreamingOutput out = new StreamingOutput() 
//		{
//			public void write(OutputStream output) throws IOException, WebApplicationException 
//			{
//				RDFDataMgr.write(output, affordanceModel, Lang.TURTLE) ;
//			}
//		};
//		
//		return Response.ok(out)
//					   .type(Lang.TURTLE.getContentType().getContentType())
//					   .build();
//    }

//	/**
//	 * TODO: this is just a dirty hack!
//	 * TODO: http://localhost:8080/trix/turtle/?uri=http://www.google.de
//	 * 
//	 * @param outputFormat
//	 * @return
//	 */
//	@OPTIONS
//	@Path("/{outputFormat: (json-ld|n3|n-quads|n-triples|rdf-json|rdf-xml|trig|trix|turtle)}/")
//	@Produces("text/turtle")
//	public Response actions(@PathParam("inputFormat") String inputFormat,
//							@PathParam("outputFormat") String outputFormat)
//	{
//		// determine outputLang from PathParam
//		final Lang outputLang = SUPPORTED_LANGUAGES.get(outputFormat);
//		if (outputLang == null)
//		{
//			throw new OutputFormatException(outputFormat);
//		}
//
//		// FIXME: strip all queryParams from requestUri since this will be the baseUri for the affordanceModel!
//		final Model affordanceModel = FileManager.get().loadModel(fContext.getRealPath("/rdf/turtle2rdfxml.ttl"), fRequestUriInfo.getAbsolutePath().toString(), "TURTLE");
//		
//		StreamingOutput out = new StreamingOutput() 
//		{
//			public void write(OutputStream output) throws IOException, WebApplicationException 
//			{
//				RDFDataMgr.write(output, affordanceModel, Lang.TURTLE) ;
//			}
//		};
//		
//		return Response.ok(out)
//					   .type(Lang.TURTLE.getContentType().getContentType())
//					   .build();
//	}
	

	//================================================================================
    // Member variables
    //================================================================================
	
	@Context HttpServletRequest fRequest;
	@Context protected ServletContext fContext;
	@Context protected HttpHeaders fRequestHeaders;
	@Context protected UriInfo fRequestUriInfo;	
	
	private static final Map<String, ContentType> SUPPORTED_LANGUAGES;
	static 
	{
		Map<String, ContentType> map = new HashMap<String, ContentType>();
		map.put("json-ld", ContentTypes.CT_APPLICATION_JSON_LD);
		map.put("n3", ContentTypes.CT_TEXT_N3);
		map.put("n-quads", ContentTypes.CT_APPLICATION_NQUADS);
		map.put("n-triples", ContentTypes.CT_APPLICATION_NTRIPLES);
		map.put("rdf-json", ContentTypes.CT_APPLICATION_RDF_JSON);
		map.put("rdf-xml", ContentTypes.CT_APPLICATION_RDFXML);
		map.put("trig", ContentTypes.CT_TEXT_TRIG);
		map.put("trix", ContentTypes.CT_APPLICATION_TRIX);
		map.put("turtle", ContentTypes.CT_TEXT_TURTLE);

		SUPPORTED_LANGUAGES = Collections.unmodifiableMap(map);
	}
}
