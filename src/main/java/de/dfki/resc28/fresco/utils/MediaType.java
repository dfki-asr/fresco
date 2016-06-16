/*
 * This file is part of fReSCO. It is subject to the license terms in
 * the LICENSE file found in the top-level directory of this distribution.
 * You may not use this file except in compliance with the License.
 */
package de.dfki.resc28.fresco.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.jena.atlas.web.WebLib;
import static org.apache.jena.atlas.lib.Lib.hashCodeObject;

public class MediaType 
{
	/**
	 * The outcome of parsing
	 * @see MediaType#parse
	 */
    static class ParsedMediaType 
    {
        public String              type ;
        public String              subType ;
        public Map<String, String> params = new LinkedHashMap<String, String>() ;
    }


    //================================================================================
    // Constructors
    //================================================================================

    /**
     * 
     * @param parser
     */
	protected MediaType(ParsedMediaType parser) 
	{
	        this.type = parser.type ;
	        this.subType = parser.subType ;
	        this.params = parser.params ;
	}

	/**
	 * 
	 * @param other
	 */
	public MediaType(MediaType other) 
	{
		this.type = other.type ;
	    this.subType = other.subType ;
	    // Order preserving copy.
	    this.params = new LinkedHashMap<String, String>(other.params) ;
	}

	/**
	 * Create a media type from type and subType
	 * @param type
	 * @param subType
	 */
	public MediaType(String type, String subType) 
	{
		this(type, subType, null) ;
	}

	/**
	 * Create a media type from type and subType and charset param
	 * @param type
	 * @param subType
	 * @param charset
	 */
	public MediaType(String type, String subType, String charset) 
	{
		this.type = type ;
		this.subType = subType ;
		this.params = new LinkedHashMap<String, String>() ;
		if ( charset != null )
			setParameter(strCharset, charset) ;
	}

	
	//================================================================================
    // Properties
    //================================================================================
	
    /**
     * 
     * @return
     */
    public String getContentType() 
    {
        if ( strContentType != null )
            return strContentType ;
        if ( subType == null )
            return type ;
        return type + "/" + subType ;
    }

    /**
     * 
     * @return
     */
    public String getSubType() 
    {
        return subType ;
    }

    /**
     * 
     * @return
     */
    public String getType() 
    {
        return type ;
    }

    /**
     * 
     * @return
     */
    public String getCharset() 
    {
        return getParameter(strCharset) ;
    }

    /**
     * 
     * @param name
     * @return
     */
    public String getParameter(String name) 
    {
        return params.get(name) ;
    }

    /**
     * 
     * @param name
     * @param value
     */
    private void setParameter(String name, String value) 
    {
        params.put(name, value) ;
        strContentType = null ;
    }


	//================================================================================
    // Create & Parse
    //================================================================================
	
	/**
	 * 
	 * @param contentType
	 * @param charset
	 * @return
	 */
	public static MediaType create(String contentType, String charset) 
	{
		ParsedMediaType mediaType = parse(contentType) ;
	    if ( charset != null )
	    	mediaType.params.put(strCharset, charset) ;
	    return new MediaType(mediaType) ;
	}

	/**
	 * 
	 * @param string
	 * @return
	 */
	public static MediaType createFromContentType(String string) 
	{
		return new MediaType(parse(string)) ;
	}
	
	/**
	 * 
	 * @param contentType
	 * @param subType
	 * @param charset
	 * @return
	 */
	public static MediaType create(String contentType, String subType, String charset) 
	{
		return new MediaType(contentType, subType, charset) ;
    }

	/**
	 * 
	 * @param string
	 * @return
	 */
    public static MediaType create(String string) 
    {
    	if ( string == null )
    		return null ;
    	return new MediaType(parse(string)) ;
    }

    /**
     * 
     * @param string
     * @return
     */
	public static ParsedMediaType parse(String string) 
	{
		ParsedMediaType mt = new ParsedMediaType() ;
		
		String[] x = WebLib.split(string, ";") ;
	    String[] t = WebLib.split(x[0], "/") ;
	    mt.type = t[0] ;
		    
		        if ( t.length > 1 )
	            mt.subType = t[1] ;

	        for (int i = 1; i < x.length; i++) {
	            // Each a parameter
	            String z[] = WebLib.split(x[i], "=") ;
	            if ( z.length == 2 )
	                mt.params.put(z[0], z[1]) ;
	        }
	        return mt ;
	    }

	
	//================================================================================
    // Auxiliaries
    //================================================================================
	
	/**
	 * Format for use in HTTP header
	 * @return
	 */
    public String toHeaderString() 
    {
        StringBuilder b = new StringBuilder() ;
        b.append(type) ;
        if ( subType != null )
            b.append("/").append(subType) ;

        for (Map.Entry<String, String> entry : params.entrySet()) 
        {
            b.append(";") ;
            b.append(entry.getKey()) ;
            b.append("=") ;
            b.append(entry.getValue()) ;
        }
        return b.toString() ;
    }

	    /**
	     */
    /**
     * Format to show structure - intentionally different from header form so
	 * you can tell parsing happened correctly
     */
    @Override
    public String toString() 
    {
        StringBuilder b = new StringBuilder() ;
        b.append("[") ;
        b.append(type) ;
        if ( subType != null )
            b.append("/").append(subType) ;
        for (String k : params.keySet()) 
        {
            if ( k.equals("boundary") )
                continue ;
            String v = params.get(k) ;
            b.append(" ") ;
            b.append(k) ;
            b.append("=") ;
            b.append(v) ;
        }
        b.append("]") ;
        return b.toString() ;
    }

    /**
     * 
     */
    @Override
    public int hashCode() 
    {
        return hashCodeObject(type, 1) ^ hashCodeObject(subType, 2) ^ hashCodeObject(params, 3) ;
    }

    /**
     * Comparator
     */
    @Override
    public boolean equals(Object object) 
    {
        if ( this == object )
            return true ;
        if ( !(object instanceof MediaType) )
            return false ;
        MediaType mt = (MediaType)object ;
        return Objects.equals(type, mt.type) && Objects.equals(subType, mt.subType) && Objects.equals(params, mt.params) ;
    }


	//================================================================================
    // Member variables
    //================================================================================
	
    private static final String strCharset = "charset" ;
    private final String type ;
    private final String  subType ;
	private final Map<String, String> params ;// Keys in insertion order.
    private String strContentType = null ; // A cache.
}



