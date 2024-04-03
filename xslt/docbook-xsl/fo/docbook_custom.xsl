<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:d="http://docbook.org/ns/docbook"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    version="1.0">
    <xsl:import href="profile-docbook.xsl"/>
    
    <xsl:template match="processing-instruction('carriagereturn')">&#160; <fo:block/>
    </xsl:template>

    <xsl:template match="processing-instruction('tabstop')">
        <xsl:text>&#160;</xsl:text>
    </xsl:template>


</xsl:stylesheet>