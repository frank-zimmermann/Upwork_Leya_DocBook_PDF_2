<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:d="http://docbook.org/ns/docbook" exclude-result-prefixes="#all" version="2.0">

    <xsl:output encoding="UTF-8" byte-order-mark="yes"/>


    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="processing-instruction('carriagereturn')[parent::phrase[ancestor::footnote]]"
        xpath-default-namespace="http://docbook.org/ns/docbook">&#160;</xsl:template>    

    <xsl:template match="phrase[parent::footnote]"
        xpath-default-namespace="http://docbook.org/ns/docbook">
        <para>
            <xsl:copy>
                <xsl:apply-templates/>
            </xsl:copy>
        </para>
    </xsl:template>

    <xsl:template match="footnote" xpath-default-namespace="http://docbook.org/ns/docbook">
        <xsl:copy>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="colgroup" xpath-default-namespace="http://docbook.org/ns/docbook">
        <colgroup>
            <!-- Count the number of col elements -->
            <xsl:variable name="colCount" select="count(col)"/>
            <!-- Calculate the percentage for each col element -->
            <xsl:apply-templates select="col">
                <xsl:with-param name="colCount" select="$colCount"/>
            </xsl:apply-templates>
        </colgroup>
    </xsl:template>

    <!-- Template to match col elements -->
    <xsl:template match="col" xpath-default-namespace="http://docbook.org/ns/docbook">
        <!-- Calculate the percentage width for each col -->
        <xsl:param name="colCount"/>
        <xsl:variable name="percentage" select="format-number(100 div $colCount, '#.##')"/>
        <!-- Create a new col element -->
        <xsl:element name="col">
            <!-- Set width attribute to calculated percentage -->
            <xsl:attribute name="width"><xsl:value-of select="$percentage"/>%</xsl:attribute>
        </xsl:element>
    </xsl:template>

    <xsl:template match="section" xpath-default-namespace="http://docbook.org/ns/docbook">
        <xsl:copy>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
