<?xml version="1.0" encoding="UTF-8"?>
<!--
Copyright (C) 2014  Stephan Kreutzer

This file is part of epub2wordpress1 workflow.

epub2wordpress1 workflow is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License version 3 or any later version,
as published by the Free Software Foundation.

epub2wordpress1 workflow is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License 3 for more details.

You should have received a copy of the GNU General Public License
along with epub2wordpress1 workflow. If not, see <http://www.gnu.org/licenses/>.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns="http://www.w3.org/1999/xhtml" exclude-result-prefixes="xhtml">
  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" doctype-public="-//W3C//DTD XHTML 1.1//EN" doctype-system="http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd"/>

  <xsl:template match="/xhtml:html">
    <xsl:comment> This file was generated by html2wordpress1.xsl for epub2wordpress1 workflow (https://github.com/publishing-systems/automated_digital_publishing/). </xsl:comment><xsl:text>&#xA;</xsl:text>
    <html xmlns="http://www.w3.org/1999/xhtml">
      <xsl:apply-templates/>
    </html>
  </xsl:template>
  
  <xsl:template match="/xhtml:html/xhtml:head">
    <head>
      <title>
        <xsl:value-of select="./xhtml:title"/>
      </title>
      <meta http-equiv="content-type" content="application/xhtml+xml; charset=UTF-8"/>
    </head>
  </xsl:template>

  <xsl:template match="/xhtml:html/xhtml:body">
    <body>
      <xsl:apply-templates/>
    </body>
  </xsl:template>

  <xsl:template match="/xhtml:html/xhtml:body//xhtml:div">
    <div>
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <xsl:template match="/xhtml:html/xhtml:body//xhtml:p">
    <p>
      <xsl:apply-templates/>
    </p>
  </xsl:template>

  <xsl:template match="/xhtml:html/xhtml:body//xhtml:span">
    <span>
      <xsl:apply-templates/>
    </span>
  </xsl:template>

  <xsl:template match="/xhtml:html/xhtml:body//xhtml:div//text()|/xhtml:html/xhtml:body//xhtml:p//text()|/xhtml:html/xhtml:body//xhtml:span//text()">
    <xsl:value-of select="."/>
  </xsl:template>

  <xsl:template match="text()|@*"/>

</xsl:stylesheet>
