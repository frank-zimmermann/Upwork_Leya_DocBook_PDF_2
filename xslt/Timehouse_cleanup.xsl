<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="2.0">

    <xsl:output encoding="UTF-8" byte-order-mark="yes" indent="yes" method="xml" version="1.0"/>


    <xsl:template match="*">
        <xsl:element name="{local-name()}">
            <xsl:apply-templates select="node() | @*"/>
        </xsl:element>
        <!--<xsl:message>
            <xsl:text>Warning: No explicit template for element </xsl:text>
            <xsl:value-of select="name()"/>
        </xsl:message>-->
    </xsl:template>

    <xsl:template match="@*[namespace-uri() = '']">
        <xsl:attribute name="{local-name()}">
            <xsl:value-of select="."/>
        </xsl:attribute>
    </xsl:template>


    <!-- not needed elements -->
    <xsl:template match="/*/meta"/>
    <xsl:template match="meta" xpath-default-namespace="http://www.timehouse.fi/schemas/HtmlLike"/>

    <xsl:template match="div[@hidden = 'hidden']"
        xpath-default-namespace="http://www.timehouse.fi/schemas/HtmlLike"/>

    <xsl:template match="article[@data-subtype = 'KkoJudgment']/header"
        xpath-default-namespace="http://www.timehouse.fi/schemas/HtmlLike"/>

    <xsl:template match="strong" xpath-default-namespace="http://www.timehouse.fi/schemas/HtmlLike">
        <b>
            <xsl:apply-templates/>
        </b>
    </xsl:template>
    
    <xsl:template match="p[p]" xpath-default-namespace="http://www.timehouse.fi/schemas/HtmlLike">
        <xsl:apply-templates/>
    </xsl:template>
    
    <!--<xsl:template
        match="html | head | head/title | body | article | header | table | tr | th | td | p | br"
        xpath-default-namespace="http://www.timehouse.fi/schemas/HtmlLike" exclude-result-prefixes="#all">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>-->



</xsl:stylesheet>
