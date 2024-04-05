<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format" exclude-result-prefixes="#all" version="2.0">

    <xsl:output encoding="UTF-8" byte-order-mark="yes" indent="yes"/>



    <xsl:template match="/html" xpath-default-namespace="http://www.timehouse.fi/schemas/HtmlLike">
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
                            <xsl:apply-templates select=".//section[@data-type = 'pykala']"
                                mode="toc"/>
                        </fo:table-body>
                    </fo:table>



                    <fo:block font-weight="bold" space-after="2cm">LAINSÄÄDÄNTÖ</fo:block>

                    <fo:block font-size="35pt" font-weight="bold" space-after="5mm">
                        <xsl:value-of select="head/title"/>
                    </fo:block>

                    <fo:block font-size="15pt" font-weight="bold">
                        <xsl:value-of
                            select="//article/div[@data-type = 'nimitieto']/span[@data-type = 'nimi']/text()"
                        />
                    </fo:block>

                    <fo:table border-bottom="1pt solid grey" space-after="1cm" space-before="5mm">
                        <fo:table-column column-width="40%"/>
                        <fo:table-column column-width="60%"/>
                        <fo:table-body>
                            <fo:table-row>
                                <fo:table-cell font-weight="bold" padding="2mm">
                                    <fo:block>Säädöskokoelmanumero:</fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="2mm">
                                    <fo:block>
                                        <xsl:apply-templates
                                            select=".//div[@data-type = 'saadosnro']"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <fo:table-row>
                                <fo:table-cell font-weight="bold" padding="2mm">
                                    <fo:block>Voimaantulopäivä:</fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="2mm">
                                    <fo:block>
                                        <xsl:value-of
                                            select=".//div[@data-type = 'voimaantulo']/time"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <fo:table-row>
                                <fo:table-cell font-weight="bold" padding="2mm">
                                    <fo:block>Antopäivä:</fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="2mm">
                                    <fo:block>
                                        <xsl:value-of select=".//div[@data-type = 'antotieto']/time"
                                        />
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <fo:table-row>
                                <fo:table-cell font-weight="bold" padding="2mm">
                                    <fo:block>Muutokset:</fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="2mm">
                                    <fo:block>
                                        <xsl:apply-templates
                                            select=".//section[@data-type = 'historia']"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>
                    </fo:table>

                    <xsl:apply-templates select=".//section[@data-type = 'pykala']"/>


                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>

    <xsl:template match="div[@data-type = 'saadosnro']"
        xpath-default-namespace="http://www.timehouse.fi/schemas/HtmlLike">
        <xsl:value-of select="span[@data-type = 'snro']"/>
        <xsl:text>/</xsl:text>
        <xsl:value-of select="span[@data-type = 'vuosiluku']"/>
    </xsl:template>

    <xsl:template match="a" xpath-default-namespace="http://www.timehouse.fi/schemas/HtmlLike">
        <fo:basic-link external-destination="{@href}">
            <xsl:apply-templates/>
        </fo:basic-link>
    </xsl:template>

    <xsl:template match="section[@data-type = 'pykala']"
        xpath-default-namespace="http://www.timehouse.fi/schemas/HtmlLike">
        <fo:block font-weight="bold" font-size="16pt" space-before="1cm" space-after="5mm"
            keep-with-next="always" id="{generate-id(.)}">
            <xsl:value-of select="span[@data-type = 'numero']"/>
            <xsl:text> §. </xsl:text>
            <xsl:value-of select="span[@data-type = 'nimi']"/>
        </fo:block>
        <xsl:apply-templates select="section"/>
    </xsl:template>

    <xsl:template match="section[@data-type = 'pykala']" mode="toc"
        xpath-default-namespace="http://www.timehouse.fi/schemas/HtmlLike">

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

    <xsl:template match="section[@data-type = 'kohta']"
        xpath-default-namespace="http://www.timehouse.fi/schemas/HtmlLike">
        <fo:block margin-left="1cm" space-after="5mm" space-before="5mm">
            <xsl:apply-templates/>
        </fo:block>

    </xsl:template>


    <xsl:template match="section[@data-type = 'momentti']"
        xpath-default-namespace="http://www.timehouse.fi/schemas/HtmlLike">
        <fo:block space-after="5mm">
            <xsl:apply-templates/>
        </fo:block>
    </xsl:template>

    <xsl:template match="i" xpath-default-namespace="http://www.timehouse.fi/schemas/HtmlLike">
        <fo:inline font-style="italic">
            <xsl:apply-templates/>
        </fo:inline>
    </xsl:template>



</xsl:stylesheet>
