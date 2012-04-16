<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<!-- Firefox
<xsl:template match="/events/list">
-->

<!-- iPhone Analyzer -->
<xsl:template match="/">

  <html>
  <body>
	<h2>People</h2>
	<xsl:for-each select="event/conceptFactory/whoEntries/*/who">
		<ul>
			<xsl:for-each select="linked-list/com.crypticbit.ipa.entity.concept.wrapper.WhoSet_-WhoEntry">
				<li><xsl:value-of select="key"/>: <xsl:value-of select="value"/></li>
			</xsl:for-each>
		</ul>
		<hr/>
	</xsl:for-each>
	<h2>Concepts</h2>
    <table border="1">
      <tr>
        <th>When</th>
        <th>Where</th>
        <th>Who ref</th>
		<th>Who</th>
        <th>What</th>
        <th>Info</th>
      </tr>
      <xsl:for-each select="event">
        <tr>
          <td><xsl:value-of select="entry/when"/></td>
          <td><xsl:value-of select="format-number(entry/where/latitude, '0.00')"/>,<xsl:value-of select="format-number(entry/where/longitude,'0.00')"/>,+-<xsl:value-of select="entry/where/accuracy"/>m</td>
          <td><xsl:value-of select="entry/who/@reference"/></td>

          <td><xsl:value-of select="what"/></td>
          <td><xsl:value-of select="description"/></td>
        </tr>
      </xsl:for-each>
    </table>
  </body>
  </html>
</xsl:template>

</xsl:stylesheet>