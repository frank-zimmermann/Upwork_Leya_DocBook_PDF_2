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

                    <fo:table space-after="1cm">
                        <fo:table-column column-width="60%"/>
                        <fo:table-body>
                            <fo:table-row border-left="1pt solid grey" border-top="1pt solid grey"
                                border-bottom="1pt solid grey">
                                <fo:table-cell font-weight="bold" padding="2mm">
                                    <fo:block>
                                        <xsl:value-of
                                            select="//article/div[@data-type = 'nimitieto']/span[@data-type = 'nimi']/text()"
                                        />
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <xsl:apply-templates select=".//section[@data-type = 'artikla']"
                                mode="toc"/>
                        </fo:table-body>
                    </fo:table>


                    <fo:block font-size="35pt" font-weight="bold" space-after="5mm">
                        <xsl:value-of select="head/title"/>
                    </fo:block>



                    <xsl:apply-templates/>



                    <!--<xsl:apply-templates select=".//section[@data-type = 'pykala']"/>-->


                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>


    <xsl:template match="section[@data-type = 'artikla']" mode="toc">

        <fo:table-row border-left="1pt solid grey">
            <fo:table-cell padding="2mm">
                <fo:block>
                    <fo:basic-link internal-destination="{generate-id(.)}">
                        <xsl:value-of select="span[@data-type = 'numero']"/>
                        <xsl:text> §. </xsl:text>
                        <xsl:value-of select="span[@data-type = 'nimi']"/>
                    </fo:basic-link>
                </fo:block>
            </fo:table-cell>
        </fo:table-row>

    </xsl:template>

    <xsl:template match="section[@data-type = 'artikla']">
        <fo:block font-weight="bold" font-size="16pt" space-before="1cm" space-after="5mm"
            keep-with-next="always" id="{generate-id(.)}">
            <xsl:value-of select="span[@data-type = 'numero']"/>
            <xsl:text> §. </xsl:text>
            <xsl:value-of select="span[@data-type = 'nimi']"/>
        </fo:block>
        <xsl:apply-templates select="section"/>
    </xsl:template>

    <xsl:template match="article/div[@data-type = 'nimitieto']/span">
        <fo:block font-size="14pt" font-weight="bold" line-height="1.5em">
            <xsl:apply-templates/>
        </fo:block>
    </xsl:template>

    <xsl:template match="div | section | p | article/span | article/a">
        <fo:block space-after="5mm">
            <xsl:apply-templates/>
        </fo:block>
    </xsl:template>

    <xsl:template match="table">
        <fo:table space-after="1cm">
            <xsl:apply-templates select=".//col"/>
            <fo:table-body>
                <xsl:apply-templates select=".//tr"/>
            </fo:table-body>
        </fo:table>
    </xsl:template>

    <xsl:template match="col">
        <fo:table-column column-width="{@width}"/>
    </xsl:template>

    <xsl:template match="tr">
        <fo:table-row>
            <xsl:apply-templates/>
        </fo:table-row>
    </xsl:template>

    <xsl:template match="td | th">
        <fo:table-cell padding="2mm">
            
            <xsl:if test="@colspan">
                <xsl:attribute name="number-columns-spanned">
                    <xsl:value-of select="@colspan"/>
                </xsl:attribute>
            </xsl:if>
            
            <!-- border handling -->
            <xsl:if test="contains(@style, 'border-left')">
                <xsl:attribute name="border-left">1px solid #000000</xsl:attribute>
            </xsl:if>
            <xsl:if test="contains(@style, 'border-right')">
                <xsl:attribute name="border-right">1px solid #000000</xsl:attribute>
            </xsl:if>
            <xsl:if test="contains(@style, 'border-top')">
                <xsl:attribute name="border-top">1px solid #000000</xsl:attribute>
            </xsl:if>
            <xsl:if test="contains(@style, 'border-bottom') or parent::*/@data-rowsep > 0">
                <xsl:attribute name="border-bottom">1px solid #000000</xsl:attribute>
            </xsl:if>
            
            
            <fo:block>
                <xsl:if test="self::th">
                    <xsl:attribute name="font-weight">bold</xsl:attribute>
                </xsl:if>
                <xsl:apply-templates/>
            </fo:block>
        </fo:table-cell>
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

    <xsl:template match="a">
        <fo:basic-link external-destination="{@href}">
            <xsl:apply-templates/>
        </fo:basic-link>
    </xsl:template>


    <!-- remove not needed elements -->

    <xsl:template match="span[starts-with(@data-type, 'head')]"/>
    <xsl:template match="div[@hidden = 'hidden']"/>



</xsl:stylesheet>
