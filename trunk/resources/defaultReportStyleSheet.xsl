<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:cbr="http://crypticbit.com/iphone/result">

<xsl:template match="/">
  <html>
  <body>
    <h1>iPhone Analyzer Default Report</h1>
    
    <h2>Device Info</h2>
    <table border="1">
    <xsl:for-each select="/report/device-info/cbir.variable">
    <tr>
      <td><xsl:value-of select="@name"/></td>
      <td><xsl:value-of select="@value"/></td>
    </tr>
    </xsl:for-each>
    </table>
    
    <h2>Query Results</h2>
    <xsl:for-each select="/report/show-only-hits/cbir.resultset">
      <h3>Query: '<xsl:value-of select="@query"/>' (<xsl:value-of select="@query-type"/>)</h3>
      Found matches in:
      <ul>
      <xsl:for-each select="cbir.file-result">
        <li><xsl:value-of select="@file-name"/> (<xsl:value-of select="@hit-count"/>)</li>
      </xsl:for-each>
      </ul>
      <xsl:for-each select="cbir.file-result">
        <h4><xsl:value-of select="@file-name"/></h4>
        <ul>
        <xsl:for-each select="cbir.hit">
          <li><xsl:value-of select="@location-description"/></li>
        </xsl:for-each>
        </ul>
      </xsl:for-each>
    </xsl:for-each>
    
    <xsl:for-each select="/report/show-only-context/cbir.resultset">
      <h3>Query: '<xsl:value-of select="@description"/>' (<xsl:value-of select="@query-type"/>)</h3>
      Found matches in:
      <ul>
      <xsl:for-each select="cbir.file-result">
        <li><xsl:value-of select="@file-name"/> (<xsl:value-of select="@hit-count"/>)</li>
      </xsl:for-each>
      </ul>
      <xsl:for-each select="cbir.file-result">
        <h4><xsl:value-of select="@file-name"/></h4><ol>
        <xsl:for-each select="cbir.hit">
          <xsl:variable name="url" select="@location-matcher"/>     
          <li><a href="{$url}">
          <xsl:value-of select="."/>
          </a></li>
        </xsl:for-each></ol>
      </xsl:for-each>
    </xsl:for-each>
    
    <h2>Errors</h2>
    <ul>
    <xsl:for-each select="//cbir.error">
    	<li><xsl:value-of select="."/></li>
    </xsl:for-each>
    </ul>
  </body>
  </html>
</xsl:template>

</xsl:stylesheet>