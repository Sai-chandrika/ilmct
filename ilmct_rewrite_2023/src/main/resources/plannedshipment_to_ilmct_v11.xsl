<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="/">
		<Feed>
			<FeedHeader>
				<FeedSource>OTM</FeedSource>
				<FeedGenDtTime>
					<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/TransactionHeader/ObjectModInfo/InsertDt/GLogDate"/>
				</FeedGenDtTime>
			</FeedHeader>
			<xsl:variable name="ShipperLocation" select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/InvolvedParty[InvolvedPartyQualifierGid/Gid/Xid='SHIPPER']/InvolvedPartyLocationRef/LocationRef/LocationGid/Gid/Xid"/>
			<xsl:variable name="ConsigneeLocation" select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/InvolvedParty[InvolvedPartyQualifierGid/Gid/Xid='CONSIGNEE']/InvolvedPartyLocationRef/LocationRef/LocationGid/Gid/Xid"/>
			<xsl:variable name="ordershipunit" select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipUnit/ReleaseShipUnitGid/Gid/Xid"/>
			<xsl:variable name="CARRIAGETYPE" select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='CARRIAGE_TYPE']/ShipmentRefnumValue"/>
			<FeedContent>
				<Load>
					<LoadID>
						<xsl:value-of select="concat(/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentGid/Gid/DomainName,'.',/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentGid/Gid/Xid)"/>
					</LoadID>
					<LoadName>
						<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentGid/Gid/DomainName"/>
					</LoadName>
					<Partition>
						<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentGid/Gid/DomainName"/>
					</Partition>
					<Mode>
						<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/TransportModeGid/Gid/Xid"/>
					</Mode>
					<CarrierID>
						<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ServiceProviderGid/Gid/Xid"/>
					</CarrierID>
					<CarrierName>
						<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ServiceProviderAlias[ServiceProviderAliasQualifierGid/Gid/Xid='CARRIER_NAME']/ServiceProviderAliasValue"/>
					</CarrierName>
					<CallBackRequired>
						<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='ETA_UPDATE_REQD']/ShipmentRefnumValue"/>
					</CallBackRequired>
					<TrackingProvider>
						<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='TRACKING_PROVIDER']/ShipmentRefnumValue"/>
					</TrackingProvider>
					<LanguagePreference>
						<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='LANGUAGE_PREFERENCE']/ShipmentRefnumValue"/>
					</LanguagePreference>
					<ShipType>
						<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='LOGISTICS']/ShipmentRefnumValue"/>
					</ShipType>
					<LoadReferences>
						<LoadReference LoadReferenceType="COMMODITY">
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='COMMODITY']/ShipmentRefnumValue"/>
						</LoadReference>
						<LoadReference LoadReferenceType="GPSEnabled">
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='GPS_ENABLED']/ShipmentRefnumValue"/>
						</LoadReference>
						<LoadReference LoadReferenceType="DriverPhone">
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='DRIVER_CONTACT']/ShipmentRefnumValue"/>
						</LoadReference>
						<!--OFCE LOGIC ADDED for  VehicleNumber -->
						<LoadReference LoadReferenceType="VehicleNumber">
							<xsl:choose>
								<xsl:when test="(/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='TRUCK_NO']/ShipmentRefnumValue)">
									<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='TRUCK_NO']/ShipmentRefnumValue"/>
								</xsl:when>
								<xsl:when test="(/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/FlexFieldStrings/Attribute3)">
									<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/FlexFieldStrings/Attribute3"/>
								</xsl:when>
							</xsl:choose>
						</LoadReference>
						<LoadReference LoadReferenceType="WagonNumber">
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='WAGON_NO']/ShipmentRefnumValue"/>
						</LoadReference>
						<LoadReference LoadReferenceType="LegType">
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='LEG_TYPE']/ShipmentRefnumValue"/>
						</LoadReference>
						<LoadReference LoadReferenceType="LeadTime">
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='LEAD_TIME']/ShipmentRefnumValue"/>
						</LoadReference>
						<LoadReference LoadReferenceType="TrailerNumber">
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/FlexFieldStrings/ATTRIBUTE4"/>
						</LoadReference>
						<LoadReference LoadReferenceType="BN">
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='BN']/ShipmentRefnumValue"/>
						</LoadReference>
						<LoadReference LoadReferenceType="BM">
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='BM']/ShipmentRefnumValue"/>
						</LoadReference>
						<LoadReference LoadReferenceType="GID">
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='GLOBAL_ID']/ShipmentRefnumValue"/>
						</LoadReference>
						<LoadReference LoadReferenceType="PO">
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='PO_NUMBER']/ShipmentRefnumValue"/>
						</LoadReference>
						<LoadReference LoadReferenceType="IMO">
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='IMO_NUMBER']/ShipmentRefnumValue"/>
						</LoadReference>
						<LoadReference LoadReferenceType="INCOTERMS">
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/CommercialTerms/IncoTermGid/Gid/Xid"/>
							<xsl:text> </xsl:text>
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/CommercialTerms/TermLocationText"/>
						</LoadReference>
						<LoadReference LoadReferenceType="DriverName">
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='DRIVER_NAME']/ShipmentRefnumValue"/>
						</LoadReference>
						<LoadReference LoadReferenceType="VesselNumber">
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='VESSEL_NAME']/ShipmentRefnumValue"/>
						</LoadReference>
						<LoadReference LoadReferenceType="LinerNumber">
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='CARRIER']/ShipmentRefnumValue"/>
						</LoadReference>
						<LoadReference LoadReferenceType="POL">
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='SOURCE_PORT']/ShipmentRefnumValue"/>
						</LoadReference>
						<LoadReference LoadReferenceType="POD">
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='DESTINATION_PORT']/ShipmentRefnumValue"/>
						</LoadReference>
						<LoadReference LoadReferenceType="MBOL">
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='MBOL_NUMBER']/ShipmentRefnumValue"/>
						</LoadReference>
						<LoadReference LoadReferenceType="HBOL">
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='HBOL']/ShipmentRefnumValue"/>
						</LoadReference>
						<LoadReference LoadReferenceType="Voyage">
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='V3']/ShipmentRefnumValue"/>
						</LoadReference>
						<LoadReference LoadReferenceType="ContainerTracking">
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='IS_CONTAINER_TRACKING']/ShipmentRefnumValue"/>
						</LoadReference>
						<LoadReference LoadReferenceType="CarriageType">
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='CARRIAGE_TYPE']/ShipmentRefnumValue"/>
						</LoadReference>
						<LoadReference LoadReferenceType="NUMBER_OF_ORDERS">
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='NUMBER_OF_ORDERS']/ShipmentRefnumValue"/>
						</LoadReference>
						<LoadReference LoadReferenceType="HOS">
							<xsl:choose>
								<xsl:when test="(/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='CONSIDER_HOS']/ShipmentRefnumValue='Y')">true</xsl:when>
							</xsl:choose>
							<xsl:choose>
								<xsl:when test="(/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='CONSIDER_HOS']/ShipmentRefnumValue='N')">false</xsl:when>
							</xsl:choose>
						</LoadReference>
						<!--OFCE LOGIC ADDED for  LogisticsContact -->
						<LoadReference LoadReferenceType="LogisticsContact">
							<xsl:choose>
								<xsl:when test="(/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='LOGISTICS_CONTACT']/ShipmentRefnumValue)">
									<xsl:value-of select="Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='LOGISTICS_CONTACT']/ShipmentRefnumValue"/>
								</xsl:when>
								<xsl:when test="(/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='LOGISTICS']/ShipmentRefnumValue)">
									<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='LOGISTICS']/ShipmentRefnumValue"/>
								</xsl:when>
							</xsl:choose>
						</LoadReference>
						<LoadReference LoadReferenceType="CN">
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='CN']/ShipmentRefnumValue"/>
						</LoadReference>
						<LoadReference LoadReferenceType="HazardousFlag">
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='HAZARDOUS_FLAG']/ShipmentRefnumValue"/>
						</LoadReference>
						<LoadReference LoadReferenceType="HazardousType">
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='HAZARDOUS_TYPE']/ShipmentRefnumValue"/>
						</LoadReference>
						<LoadReference LoadReferenceType="FlightNumber">
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='FLIGHT_NO']/ShipmentRefnumValue"/>
						</LoadReference>
						<LoadReference LoadReferenceType="TrainNumber">
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='TRAIN_NO']/ShipmentRefnumValue"/>
						</LoadReference>
						<LoadReference LoadReferenceType="SOURCE_PORT">
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='SOURCE_PORT']/ShipmentRefnumValue"/>
						</LoadReference>
						<LoadReference LoadReferenceType="DESTINATION_PORT">
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='DESTINATION_PORT']/ShipmentRefnumValue"/>
						</LoadReference>
						<LoadReference LoadReferenceType="PICK_UP_LOCATION">
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='PICK_UP_LOCATION']/ShipmentRefnumValue"/>
						</LoadReference>
						<LoadReference LoadReferenceType="DESTINATION_LOCATION">
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='DESTINATION_LOCATION']/ShipmentRefnumValue"/>
						</LoadReference>
						<!--OFCE LOGIC ADDED for  SHIPMENT_MATERIAL -->
						<LoadReference LoadReferenceType="SHIPMENT_MATERIAL">
							<xsl:choose>
								<xsl:when test="(/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='SHIPMENT_MATERIAL']/ShipmentRefnumValue)">
									<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='SHIPMENT_MATERIAL']/ShipmentRefnumValue"/>
								</xsl:when>
								<xsl:otherwise>OTHERS</xsl:otherwise>
							</xsl:choose>
						</LoadReference>
						<LoadReference LoadReferenceType="CONTAINER_NUMBERS">
							<xsl:choose>
								<xsl:when test="(/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='CONTAINER_NUMBERS']/ShipmentRefnumValue)">
									<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='CONTAINER_NUMBERS']/ShipmentRefnumValue"/>
								</xsl:when>
								<xsl:when test="(/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='REFERENCE_CONTAINER_NUMBER']/ShipmentRefnumValue)">
									<xsl:value-of
											select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='REFERENCE_CONTAINER_NUMBER']/ShipmentRefnumValue"/>
								</xsl:when>
							</xsl:choose>
						</LoadReference>
						<!--<LoadReference LoadReferenceType="TransmitETA">
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='TransmitETA']/ShipmentRefnumValue"/>
						</LoadReference>-->
						<LoadReference LoadReferenceType="TransmitETA">
							<xsl:value-of
									select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='ETA PROPAGATION']/ShipmentRefnumValue"/>
						</LoadReference>
						<LoadReference LoadReferenceType="LegType">
							<xsl:value-of
									select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='LEG_TYPE']/ShipmentRefnumValue"/>
						</LoadReference>
					</LoadReferences>
					<SpecialServices>
						<xsl:for-each select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentSpecialService">
							<xsl:if test="(SpecialServiceGid/Gid/Xid !='LOAD') and (SpecialServiceGid/Gid/Xid !='UNLOAD')">
								<SpecialService>
									<xsl:value-of select="SpecialServiceGid/Gid/Xid"/>
								</SpecialService>
							</xsl:if>
						</xsl:for-each>
					</SpecialServices>
					<Source>
						<SourceLocationID>
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/SourceLocationRef/LocationRef/LocationGid/Gid/Xid"/>
						</SourceLocationID>
					</Source>
					<Destination>
						<DestLocationID>
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/DestinationLocationRef/LocationRef/LocationGid/Gid/Xid"/>
						</DestLocationID>
					</Destination>
					<StartDate>
						<DateTime>
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/StartDt/GLogDate"/>
						</DateTime>
						<TZId>
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/StartDt/TZId"/>
						</TZId>
					</StartDate>
					<EndDate>
						<DateTime>
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/EndDt/GLogDate"/>
						</DateTime>
						<TZId>
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/EndDt/TZId"/>
						</TZId>
					</EndDate>
					<Shipper>
						<LocationID>
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/InvolvedParty[InvolvedPartyQualifierGid/Gid/Xid='SHIPPER']/InvolvedPartyLocationRef/LocationRef/LocationGid/Gid/Xid"/>
						</LocationID>
					</Shipper>
					<Consignee>
						<!--<ConsigneeName>
                <xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/Location[LocationGid/Gid/Xid=$ConsigneeLocation]/LocationName"/>
              </ConsigneeName> -->
						<LocationID>
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/InvolvedParty[InvolvedPartyQualifierGid/Gid/Xid='CONSIGNEE']/InvolvedPartyLocationRef/LocationRef/LocationGid/Gid/Xid"/>
						</LocationID>
					</Consignee>
					<!--<Forwarder>
						<LocationID>
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ServiceProviderAlias[ServiceProviderAliasQualifierGid/Gid/Xid='CARRIER_NAME']/ServiceProviderAliasValue"/>
						</LocationID>
					</Forwarder>-->
					<Forwarder>
						<LocationID>
							<xsl:value-of select="substring-after(/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ServiceProviderAlias[ServiceProviderAliasQualifierGid/Gid/Xid='GLOG']/ServiceProviderAliasValue,'.')"/>
						</LocationID>
					</Forwarder>
					<LoadMeasure>
						<TotalWeight>
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/TotalWeightVolume/WeightVolume/Weight/WeightValue"/>
						</TotalWeight>
						<TotalWeightUOM>
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/TotalWeightVolume/WeightVolume/Weight/WeightUOMGid/Gid/Xid"/>
						</TotalWeightUOM>
						<WeightUtilization>
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader2/WeightUtil"/>
						</WeightUtilization>
						<TotalVolume>
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/TotalWeightVolume/WeightVolume/Volume/VolumeValue"/>
						</TotalVolume>
						<TotalVolumeUOM>
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/TotalWeightVolume/WeightVolume/Volume/VolumeUOMGid/Gid/Xid"/>
						</TotalVolumeUOM>
						<VolumeUtilization>
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader2/VolumeUtil"/>
						</VolumeUtilization>
						<PalletCount>
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/TotalShipUnitCount"/>
						</PalletCount>
						<ERUUtilization>
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader2/EquipRefUnitUtil"/>
						</ERUUtilization>
						<MaterialCount>
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/TotalPackagedItemCount"/>
						</MaterialCount>
						<TruckType>
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/SEquipment/EquipmentGroupGid/Gid/Xid"/>
						</TruckType>
						<TotalOrders>
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/NumOrderReleases"/>
						</TotalOrders>
						<TotalStops>
							<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/StopCount"/>
						</TotalStops>
					</LoadMeasure>
					<xsl:for-each select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/SEquipment">
						<Container>
							<ContainerID>
								<xsl:value-of select="SEquipmentGid/Gid/Xid"/>
							</ContainerID>
							<ContainerNumber>
								<xsl:choose>
									<xsl:when test="(/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='CONTAINER_NUMBERS']/ShipmentRefnumValue)">
										<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='CONTAINER_NUMBERS']/ShipmentRefnumValue"/>
									</xsl:when>
									<xsl:when test="(/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='REFERENCE_CONTAINER_NUMBER']/ShipmentRefnumValue)">
										<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentHeader/ShipmentRefnum[ShipmentRefnumQualifierGid/Gid/Xid='REFERENCE_CONTAINER_NUMBER']/ShipmentRefnumValue"/>
									</xsl:when>
								</xsl:choose>
							</ContainerNumber>
							<ContainerType>
								<xsl:value-of select="EquipmentGroupGid/Gid/Xid"/>
							</ContainerType>
							<SealNumber>
								<xsl:value-of select="SEquipmentSeal/SealNumber"/>
							</SealNumber>
						</Container>
					</xsl:for-each>
					<Stops>
						<xsl:for-each select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipmentStop">
							<Stop>
								<StopNum>
									<xsl:value-of select="StopSequence"/>
								</StopNum>
								<StopType>
									<xsl:value-of select="StopType"/>
								</StopType>
								<StopLocation>
									<xsl:value-of select="LocationRef/LocationGid/Gid/Xid"/>
								</StopLocation>
								<PlannedArrival>
									<DateTime>
										<xsl:value-of select="ArrivalTime/EventTime/PlannedTime/GLogDate"/>
									</DateTime>
									<TZId>
										<xsl:value-of select="ArrivalTime/EventTime/PlannedTime/TZId"/>
									</TZId>
								</PlannedArrival>
								<EstimatedArrival>
									<DateTime>
										<xsl:value-of select="ArrivalTime/EventTime/EstimatedTime/GLogDate"/>
									</DateTime>
									<TZId>
										<xsl:value-of select="ArrivalTime/EventTime/EstimatedTime/TZId"/>
									</TZId>
								</EstimatedArrival>
								<PlannedDeparture>
									<DateTime>
										<xsl:value-of select="DepartureTime/EventTime/PlannedTime/GLogDate"/>
									</DateTime>
									<TZId>
										<xsl:value-of select="DepartureTime/EventTime/PlannedTime/TZId"/>
									</TZId>
								</PlannedDeparture>
								<EstimatedDeparture>
									<DateTime>
										<xsl:value-of select="DepartureTime/EventTime/EstimatedTime/GLogDate"/>
									</DateTime>
									<TZId>
										<xsl:value-of select="DepartureTime/EventTime/EstimatedTime/TZId"/>
									</TZId>
								</EstimatedDeparture>
								<StopContent>
									<ActivityType>
										<xsl:value-of select="ShipmentStopDetail/Activity"/>
									</ActivityType>
									<Pallet>
										<xsl:for-each select="ShipmentStopDetail">
											<PalletID>
												<xsl:value-of select="ShipUnitGid/Gid/Xid"/>
											</PalletID>
										</xsl:for-each>
									</Pallet>
								</StopContent>
							</Stop>
						</xsl:for-each>
					</Stops>
					<Orders>
						<xsl:for-each select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/Release">
							<Order>
								<OrderID>
									<xsl:value-of select="ReleaseGid/Gid/Xid"/>
								</OrderID>
								<GlobalID>
									<!--ABC|ASSAD value taken at planned shipment release refnum section with qual GLOBAL_ID-->
									<xsl:value-of select="ReleaseRefnum[ReleaseRefnumQualifierGid/Gid/Xid='GLOBAL_ID']/ReleaseRefnumValue"/>
								</GlobalID>
								<BN>
									<!--2312312312 value taken at planned shipment release refnum section with qual BN-->
									<xsl:value-of select="ReleaseRefnum[ReleaseRefnumQualifierGid/Gid/Xid='BN']/ReleaseRefnumValue"/>
								</BN>
								<ShipFromLocationRef>
									<LocationID>
										<xsl:choose>
											<xsl:when test="(PlanFromLocationGid/LocationGid/Gid/Xid)">
												<xsl:value-of select="PlanFromLocationGid/LocationGid/Gid/Xid"/>
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="ShipFromLocationRef/LocationRef/LocationGid/Gid/Xid"/>
											</xsl:otherwise>
										</xsl:choose>
										<!--SP-US93 value taken at planned shipment release shipfromloc section -->
										<!-- <xsl:value-of select="PlanFromLocationGid/LocationGid/Gid/Xid"/> -->
									</LocationID>
								</ShipFromLocationRef>
								<ShipToLocationRef>
									<LocationID>
										<!--0001208343 value taken at planned shipment release shiptoloc section-->
										<!-- <xsl:value-of select="PlanToLocationGid/LocationGid/Gid/Xid"/> -->
										<xsl:choose>
											<xsl:when test="(PlanToLocationGid/LocationGid/Gid/Xid)">
												<xsl:value-of select="PlanToLocationGid/LocationGid/Gid/Xid"/>
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="ShipToLocationRef/LocationRef/LocationGid/Gid/Xid"/>
											</xsl:otherwise>
										</xsl:choose>
									</LocationID>
								</ShipToLocationRef>
							</Order>
						</xsl:for-each>
					</Orders>
					<Pallets>
						<xsl:for-each select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/ShipUnit">
							<Pallet>
								<PalletID>
									<xsl:value-of select="ShipUnitGid/Gid/Xid"/>
								</PalletID>
								<PalletCount>
									<xsl:value-of select="ShipUnitCount"/>
								</PalletCount>
								<SourceLocationID>
									<xsl:value-of select="ShipFromLocationRef/LocationRef/LocationGid/Gid/Xid"/>
								</SourceLocationID>
								<DestLocationID>
									<xsl:value-of select="ShipToLocationRef/LocationRef/LocationGid/Gid/Xid"/>
								</DestLocationID>
								<Order>
									<OrderID>
										<xsl:value-of select="ShipUnitContent/ReleaseGid/Gid/Xid"/>
									</OrderID>
								</Order>
								<xsl:for-each select="ShipUnitContent">
									<PalletContent>
										<ProductID>
											<xsl:value-of select="PackagedItemRef/PackagedItemGid/Gid/Xid"/>
										</ProductID>
										<xsl:variable select="PackagedItemRef/PackagedItemGid/Gid/Xid" name="packitem"/>
										<ProductName>
											<xsl:value-of select="/Transmission/TransmissionBody/GLogXMLElement/PlannedShipment/Shipment/PackagedItem/Item[ItemGid/Gid/Xid=$packitem]/ItemName"/>
										</ProductName>
										<Quantity>
											<xsl:value-of select="ItemQuantity/PackagedItemCount"/>
										</Quantity>
										<NetWeight>
											<xsl:value-of select="ItemQuantity/WeightVolume/Weight/WeightValue"/>
										</NetWeight>
										<NetWeightUOM>
											<xsl:value-of select="ItemQuantity/WeightVolume/Weight/WeightUOMGid/Gid/Xid"/>
										</NetWeightUOM>
										<NetVolume>
											<xsl:value-of select="ItemQuantity/WeightVolume/Volume/VolumeValue"/>
										</NetVolume>
										<NetVolumeUOM>
											<xsl:value-of select="ItemQuantity/WeightVolume/Volume/VolumeUOMGid/Gid/Xid"/>
										</NetVolumeUOM>
										<MaterialReference MaterialReferenceType="CustomerItemNumber">
											<xsl:value-of select="ShipUnitLineRefnum[ShipUnitLineRefnumQualifierGid/Gid/Xid='CUSTOMER_ITEM_NUMBER']/ShipUnitLineRefnumValue"/>
										</MaterialReference>
										<MaterialReference MaterialReferenceType="Commodity">
											<xsl:value-of select="ShipUnitLineRefnum[ShipUnitLineRefnumQualifierGid/Gid/Xid='COMMODITY']/ShipUnitLineRefnumValue"/>
										</MaterialReference>
									</PalletContent>
								</xsl:for-each>
							</Pallet>
						</xsl:for-each>
					</Pallets>
				</Load>
			</FeedContent>
		</Feed>
	</xsl:template>
</xsl:stylesheet>
