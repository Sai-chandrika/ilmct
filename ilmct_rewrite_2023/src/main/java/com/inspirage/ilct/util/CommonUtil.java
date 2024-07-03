package com.inspirage.ilct.util;

import com.inspirage.ilct.documents.LoadReference;
import com.inspirage.ilct.documents.ShipmentStopV2;
import com.inspirage.ilct.documents.ShipmentV2;
import com.inspirage.ilct.documents.User;
import jakarta.xml.bind.Marshaller;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Supplier;

public class CommonUtil {
    public static final Logger logger = LoggerFactory.getLogger("CommonUtil");

    public static List<Integer> generateSequenceBasedOnDates(List<String> dates) throws ParseException {
        List<Integer> sequence = new ArrayList<>();
        Map<Date, Integer> map1 = new LinkedHashMap<>();
        for (int i = 0; i < dates.size(); i++) {
            Date date = Utility.DATE_TIME_FORMAT.parse(dates.get(i));
            map1.put(date, i + 1);
        }
        Collections.sort(dates);
        for (int i = 0; i < dates.size(); i++) {
            Date date = Utility.DATE_TIME_FORMAT.parse(dates.get(i));
            map1.put(date, i + 1);
        }
        for (Map.Entry<Date, Integer> entry : map1.entrySet()) {
            sequence.add(entry.getValue());
        }
        return sequence;
    }

    public static String convertObjectToXML(Object request, Class clazz) {
        try {
            javax.xml.bind.JAXBContext jaxbContext = javax.xml.bind.JAXBContext.newInstance(clazz);
            javax.xml.bind.Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter sw = new StringWriter();
            jaxbMarshaller.marshal(request, sw);
            return sw.toString();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static ShipmentStopV2 getLastStopByType(ShipmentV2 shipment, String stopType) {
        ShipmentStopV2 otmStop = null;
        List<ShipmentStopV2> sortedStops = shipment.getStops();
        if (!Utility.isEmpty(sortedStops)) {
            sortedStops.sort(Comparator.comparing(ShipmentStopV2::getStopNumber));
            for (ShipmentStopV2 stop : sortedStops) {
                if (stop.getStopType().equalsIgnoreCase(stopType)) {
                    if (otmStop != null) {
                        if (stop.getStopNumber() > otmStop.getStopNumber()) {
                            otmStop = stop;
                        }
                    } else {
                        otmStop = stop;
                    }
                }
            }
        }
        return otmStop;
    }

    public static List<ShipmentV2> sortContainerShipments(List<ShipmentV2> legShipments) {
        List<ShipmentV2> orderedShipments = new ArrayList<>();
        List<String> carriageTypes = Constants.CARRIAGETYPES;
        for (String carriageType : carriageTypes) {
            List<ShipmentV2> shipments = new ArrayList<>();
            for (ShipmentV2 legShipment : legShipments) {
                String shipmentCarriageType = "";
                Optional<LoadReference> optionalLoadReference = legShipment.getLoadReferences().stream().filter(loadReference -> loadReference.getLoadReferenceType().equals("CarriageType")
                ).findAny();
                if (optionalLoadReference.isPresent()) {
                    shipmentCarriageType = optionalLoadReference.get().getContent();
                }
                if (shipmentCarriageType != null && shipmentCarriageType.equalsIgnoreCase(carriageType)) {
                    shipments.add(legShipment);
                }
            }
            //Commented based on Praveen,Pravas words--- order should be like PRE,MAIN,ON
           /* if (!Utility.isEmpty(shipments)) {
                shipments.sort(Comparator.comparing(shipmentV2 -> shipmentV2.getStartDate().getDateTime()));
                orderedShipments.addAll(shipments);
            }*/
            orderedShipments.addAll(shipments);
        }
        return orderedShipments;
    }

    public static String stylizer(String xsltFile, String xml) {
        String xmlString = "";
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream in = classLoader.getResourceAsStream(xsltFile);
            File stylesheet = File.createTempFile("stream2file", ".tmp");
            stylesheet.deleteOnExit();
            try (FileOutputStream out = new FileOutputStream(stylesheet)) {
                IOUtils.copy(in, out);
            }
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
            TransformerFactory tFactory = TransformerFactory.newInstance();
            StreamSource stylesource = new StreamSource(stylesheet);
            Transformer transformer = tFactory.newTransformer(stylesource);
            DOMSource source = new DOMSource(document);
            StreamResult sw = new StreamResult(new StringWriter());
            transformer.transform(source, sw);
            xmlString = sw.getWriter().toString();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            logger.error("Exception occured in " + new Object() {
            }.getClass().getEnclosingMethod().getName() + "\n" + errors);
        }
        return xmlString;
    }

    public static <T> Optional<T> resolve(Supplier<T> resolver) {
        try {
            T result = resolver.get();
            return Optional.ofNullable(result);
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }

    public static String getTimeZone(User user) {
        return (user != null && !StringUtils.isEmpty(user.getTimeZone())) ? user.getTimeZone()
                : ZoneId.systemDefault().getId();
    }
}
