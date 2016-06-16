/*
 * This file is part of fReSCO. It is subject to the license terms in
 * the LICENSE file found in the top-level directory of this distribution.
 * You may not use this file except in compliance with the License.
 */
package de.dfki.resc28.fresco.utils;

public class ContentType 
{ 
	//================================================================================
    // Static creation methods
    //================================================================================
    
    /**
     * 
     * @param mediaTypeString
     * @return
     */
    public static ContentType create(String mediaTypeString) 
    {
        if ( mediaTypeString == null )
            return null ;
        ContentType ct = new ContentType(MediaType.create(mediaTypeString)) ;
        return ct ;
    }

    /**
     * 
     * @param ctString
     * @param charset
     * @return
     */
    public static ContentType create(String ctString, String charset) 
    {
        MediaType.ParsedMediaType x = MediaType.parse(ctString) ;
        x.params.put(m_charsetParamName, charset) ;
        return new ContentType(new MediaType(x)) ;
    }

    
	//================================================================================
    // Private Constructor
    //================================================================================
	
	/**
	 * 
	 * @param mediaType
	 */
    private ContentType(MediaType mediaType) 
    {
        m_mediaType = mediaType ;
    }
    

	//================================================================================
    // Public methods
    //================================================================================

    /**
     * 
     * @return
     */
    public String getContentType() 
    {
        return m_mediaType.getContentType() ;
    }

    /**
     * 
     * @return
     */
    public String getCharset() 
    {
        return m_mediaType.getCharset() ;
    }

    /**
     * 
     * @return
     */
    public String getType() 
    {
        return m_mediaType.getType() ;
    }

    /**
     * 
     * @return
     */
    public String getSubType() 
    {
        return m_mediaType.getSubType() ;
    }

    /**
     * 
     * @return
     */
    public String toHeaderString() 
    {
        return m_mediaType.toHeaderString() ;
    }


    @Override
    public int hashCode() 
    {
        final int prime = 31 ;
        int result = 1 ;
        result = prime * result + ((m_mediaType == null) ? 0 : m_mediaType.hashCode()) ;
        return result ;
    }

    @Override
    public boolean equals(Object obj) 
    {
        if ( this == obj )
            return true ;
        if ( obj == null )
            return false ;
        if ( getClass() != obj.getClass() )
            return false ;
        ContentType other = (ContentType)obj ;
        if ( m_mediaType == null ) {
            if ( other.m_mediaType != null )
                return false ;
        } else if ( !m_mediaType.equals(other.m_mediaType) )
            return false ;
        return true ;
    }

    @Override
    public String toString() 
    {
        return m_mediaType.toString() ;
    }

	//================================================================================
    // Member variables
    //================================================================================
    
    private MediaType m_mediaType ;
    private static final String m_charsetParamName = "charset" ;
}