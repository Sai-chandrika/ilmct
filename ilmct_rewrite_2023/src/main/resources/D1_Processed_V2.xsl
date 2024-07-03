<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" indent="yes"/>
    <xsl:strip-space elements="*"/>
    <xsl:template match="/">
        <otm:Transmission xmlns:otm="http://xmlns.oracle.com/apps/otm/transmission/v6.4">
            <otm:TransmissionBody>
                <otm:GLogXMLElement>
                    <otm:TransactionHeader>
                        <otm:Refnum>
                            <otm:RefnumQualifierGid>
                                <otm:Gid>
                                    <otm:DomainName>
                                        <xsl:value-of
                                                select="substring-before(/Feed/FeedContent/D1ProcessedEvent/LoadId,'.')"/>
                                    </otm:DomainName>
                                    <otm:Xid>MESSAGE_TYPE</otm:Xid>
                                </otm:Gid>
                            </otm:RefnumQualifierGid>
                            <otm:RefnumValue>ILMCT_DELIVERY_PROCESSED</otm:RefnumValue>
                        </otm:Refnum>
                    </otm:TransactionHeader>
                    <otm:ShipmentStatus>
                        <otm:StatusCodeGid>
                            <otm:Gid>
                                <otm:DomainName><xsl:value-of
                                        select="substring-before(/Feed/FeedContent/D1ProcessedEvent/LoadId,'.')"/></otm:DomainName>
                                <otm:Xid>D1-PROCESSED</otm:Xid>
                            </otm:Gid>
                        </otm:StatusCodeGid>
                        <otm:ResponsiblePartyGid>
                            <otm:Gid>
                                <otm:DomainName><xsl:value-of
                                        select="substring-before(/Feed/FeedContent/D1ProcessedEvent/LoadId,'.')"/></otm:DomainName>
                                <otm:Xid>ILMCT</otm:Xid>
                            </otm:Gid>
                        </otm:ResponsiblePartyGid>
                        <otm:ShipmentGid>
                            <otm:Gid>
                                <otm:DomainName><xsl:value-of
                                        select="substring-before(/Feed/FeedContent/D1ProcessedEvent/LoadId,'.')"/></otm:DomainName>
                                <otm:Xid><xsl:value-of
                                        select="substring-after(/Feed/FeedContent/D1ProcessedEvent/LoadId,'.')"/></otm:Xid>
                            </otm:Gid>
                        </otm:ShipmentGid>
                    </otm:ShipmentStatus>
                </otm:GLogXMLElement>
            </otm:TransmissionBody>
        </otm:Transmission>
    </xsl:template>
</xsl:stylesheet>