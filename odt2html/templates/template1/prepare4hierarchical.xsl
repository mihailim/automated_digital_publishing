<?xml version="1.0" encoding="UTF-8"?>
<!--
Copyright (C) 2014  Stephan Kreutzer

This file is part of template1 for odt2html.

template1 for odt2html is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License version 3 or any later version,
as published by the Free Software Foundation.

template1 for odt2html is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License 3 for more details.

You should have received a copy of the GNU General Public License
along with template1 for odt2html. If not, see <http://www.gnu.org/licenses/>.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns="http://www.w3.org/1999/xhtml" exclude-result-prefixes="xhtml">
  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"/>

  <xsl:template match="xhtml:html">
    <xsl:comment> This file was generated by prepare4hierarchical of template1 for odt2html (https://github.com/skreutzer/automated_digital_publishing/). </xsl:comment><xsl:text>&#xA;</xsl:text>
    <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
      <head>
        <title>
          <xsl:for-each select="xhtml:body/xhtml:p">
            <xsl:if test="@class='title'">
              <xsl:for-each select="text()|*/text()">
                <xsl:value-of select="."/>
              </xsl:for-each>
            </xsl:if>
          </xsl:for-each>
        </title>
      </head>
      <body>
        <xsl:apply-templates select="xhtml:body/xhtml:p"/>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="xhtml:html/xhtml:body/xhtml:p[@class='part_5f_heading']">
    <div class="part">
      <h1>
        <xsl:for-each select="text()|*/text()">
          <xsl:value-of select="."/>
        </xsl:for-each>
      </h1>
    </div>
  </xsl:template>
  
  <xsl:template match="xhtml:html/xhtml:body/xhtml:p[@class='chapter_5f_heading']">
    <div class="chapter">
      <h2>
        <xsl:for-each select="text()|*/text()">
          <xsl:value-of select="."/>
        </xsl:for-each>
      </h2>
    </div>
  </xsl:template>
  
  <xsl:template match="xhtml:html/xhtml:body/xhtml:p[@class='section_5f_heading']">
    <div class="section">
      <h3>
        <xsl:for-each select="text()|*/text()">
          <xsl:value-of select="."/>
        </xsl:for-each>
      </h3>
    </div>
  </xsl:template>
  
  <xsl:template match="xhtml:html/xhtml:body/xhtml:p[@class='subsection_5f_heading']">
    <div class="subsection">
      <h4>
        <xsl:for-each select="text()|*/text()">
          <xsl:value-of select="."/>
        </xsl:for-each>
      </h4>
    </div>
  </xsl:template>

  <xsl:template match="xhtml:html/xhtml:body/xhtml:p[@class='paragraph_5f_first' or @class='paragraph']">
    <p class="paragraph_default">
      <xsl:apply-templates/>
    </p>
  </xsl:template>

  <xsl:template match="xhtml:html/xhtml:body/xhtml:p[@class='paragraph_5f_first' or @class='paragraph']//text()">
    <xsl:value-of select="."/>
  </xsl:template>

  <xsl:template match="*/xhtml:a">
    <a>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates/>
    </a>
  </xsl:template>

  <xsl:template match="text()|@*"/>

</xsl:stylesheet>
