<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format" exclude-result-prefixes="#all" version="2.0">

    <xsl:output encoding="UTF-8" byte-order-mark="yes" indent="yes"/>



    <xsl:template match="/html">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:layout-master-set>

                <fo:simple-page-master master-name="body" page-width="21.59cm" page-height="27.94cm"
                    margin-top="0.25in" margin-bottom="0.25in" margin-left="1in" margin-right="1in">
                    <fo:region-body margin-bottom="0.5in" margin-top="0.25in" column-gap="12pt"
                        column-count="1" margin-left="0in" margin-right="0in"/>
                </fo:simple-page-master>

                <fo:page-sequence-master master-name="body-master">
                    <fo:repeatable-page-master-alternatives>
                        <fo:conditional-page-master-reference master-reference="body"
                            odd-or-even="any"/>
                        <fo:conditional-page-master-reference odd-or-even="any"
                            master-reference="body"/>
                    </fo:repeatable-page-master-alternatives>
                </fo:page-sequence-master>


            </fo:layout-master-set>
            <fo:page-sequence master-reference="body-master" hyphenation-remain-character-count="2">


                <fo:flow flow-name="xsl-region-body" font-size="10pt" line-height="1.5em">


                    <xsl:apply-templates select="head/title"/>


                    <xsl:apply-templates select="body/article/table"/>
                    
                    <xsl:apply-templates select="body/article/*[not(self::table)]"/>



                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>

    <xsl:template match="head/title">
        <fo:block font-size="15pt" font-weight="bold" space-after="5mm">
            <xsl:apply-templates/>
        </fo:block>

    </xsl:template>

    <xsl:template match="table">
        <fo:table border-bottom="1pt solid grey" space-after="1cm" space-before="5mm">
            <fo:table-column column-width="40%"/>
            <fo:table-column column-width="60%"/>
            <fo:table-body>
                <xsl:apply-templates/>
            </fo:table-body>
        </fo:table>
    </xsl:template>

    <xsl:template match="tr">
        <fo:table-row>
            <xsl:apply-templates/>
        </fo:table-row>
    </xsl:template>

    <xsl:template match="th | td">
        <fo:table-cell font-weight="bold" padding="2mm">
            <fo:block>
                <xsl:apply-templates/>
            </fo:block>
        </fo:table-cell>
    </xsl:template>


    <xsl:template match="a">
        <xsl:choose>
            <xsl:when test="not(parent::p)">
                <p>
                    <fo:basic-link external-destination="{@href}">
                        <xsl:apply-templates/>
                    </fo:basic-link>
                </p>
            </xsl:when>
            <xsl:otherwise>
                <fo:basic-link external-destination="{@href}">
                    <xsl:apply-templates/>
                </fo:basic-link>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    
    
    <xsl:template match="h2">
        <fo:block space-after="5mm" font-size="13pt" font-weight="bold">
            <xsl:apply-templates/>
        </fo:block>
    </xsl:template>

    <xsl:template match="h3">
        <fo:block space-after="5mm" font-size="11pt" font-weight="bold">
            <xsl:apply-templates/>
        </fo:block>
    </xsl:template>
    
    <xsl:template match="p">
        <fo:block space-after="5mm">
            <xsl:apply-templates/>
        </fo:block>
    </xsl:template>
    

    <xsl:template match="br">
        <fo:block/>
    </xsl:template>


    <xsl:template match="i">
        <fo:inline font-style="italic">
            <xsl:apply-templates/>
        </fo:inline>
    </xsl:template>

    <xsl:template match="b">
        <fo:inline font-weight="bold">
            <xsl:apply-templates/>
        </fo:inline>
    </xsl:template>



</xsl:stylesheet>
