package com.inspirage.ilct.util;
import com.inspirage.ilct.documents.*;
import com.inspirage.ilct.enums.RoleType;
import com.inspirage.ilct.repo.LocationDocRepository;
import com.inspirage.ilct.repo.OrdersV2Repository;
import com.inspirage.ilct.xml.FeedBean;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import org.springframework.util.ObjectUtils;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.awt.*;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Utility {

    public static final Logger logger = LoggerFactory.getLogger("Utility");

    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

    public static boolean isEmpty(Object object) {
        return object == null;
    }

    @Autowired
    private static LocationDocRepository locationDocRepository;

    public static List<String> getUserLocations(User user, LocationDocRepository locationRepository) {
        locationDocRepository = locationRepository;
        List<String> locationIdList = new ArrayList<String>();

        if (user.getRole().equals(RoleType.PLANNER)) {
            if (user.getRules() != null && !user.getRules().isEmpty()) {
                locationIdList.addAll(user.getRules());
            } else if (user.getLocationCity() != null && !user.getLocationCity().isEmpty()) {
                List<LocationDoc> locations = locationDocRepository.findByCity(user.getLocationCity().trim());
                locationIdList.addAll(
                        locations.stream().map(location -> location.getSiteId().trim()).collect(Collectors.toList()));
            } else if (user.getLocationProvince() != null && !user.getLocationProvince().isEmpty()
                    && !user.getLocationProvince().equalsIgnoreCase("null")) {
                List<LocationDoc> locations = locationDocRepository.findByProvince(user.getLocationProvince().trim());
                locationIdList.addAll(
                        locations.stream().map(location -> location.getSiteId().trim()).collect(Collectors.toList()));
            } else if (user.getLocationCountryCode() != null && !user.getLocationCountryCode().isEmpty()
                    && !user.getLocationCountryCode().equalsIgnoreCase("null")) {
                List<LocationDoc> locations = locationDocRepository
                        .findByCountryCode(user.getLocationCountryCode().trim());
                locationIdList.addAll(
                        locations.stream().map(location -> location.getSiteId().trim()).collect(Collectors.toList()));
            }
        }
        return locationIdList;
    }
    public static Date formatDate(final String date, final SimpleDateFormat format) {
        try {
            return format.parse(date.trim());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static LocalDateTime convertDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static RoleType getRoleType(String roleName) {
        return Arrays.stream(RoleType.values())
                .filter(roleType -> roleType.name().equalsIgnoreCase(roleName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No matching role found for " + roleName));
    }

    public static Boolean isRoleExist(String role) {
        return Stream.of(RoleType.values()).map(RoleType::name).toList().contains(role);
    }
    public static Boolean checkRole(RoleType role) {
        return RoleType.DEFAULT.equals(role);
    }
    public static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
    public static Pageable pageable(int pageIndex, int numberOfRecords) {
        return PageRequest.of(pageIndex, numberOfRecords);
    }

    public static String getRequestedFrom(HttpServletRequest request) {
        return getClientIpAddress(request);
    }

    public static String getRandomColorCode() {
        return "#" + Integer.toHexString(generateDarkColor().getRGB()).substring(2);

    }

    private static Color generateDarkColor() {
        return generateRandomColour();
    }
    private static Color generateRandomColour() {

        return Color.getHSBColor(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat());
    }


    private static String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = { "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR" };

        for (String headerName : headerNames) {
            String ip = request.getHeader(headerName);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }

    public static String toString(InputStream is, String encoding) {
        final StringBuilder sw = new StringBuilder();
        try {
            final InputStreamReader in = new InputStreamReader(is, encoding);
            int n;
            char[] buffer = new char[4096];
            while (-1 != (n = in.read(buffer))) {
                sw.append(buffer, 0, n);
            }
        } catch (IOException e) {
           // e.printStackTrace();
        }
        return sw.toString();
    }

    public static boolean isEmpty(List<?> object) {
        return object == null || object.isEmpty();
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
    public static boolean validateEmail(String regex, String input) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(input).matches();
    }

    public static Double roundUpVal(Double d) {
        return new BigDecimal(d).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }


    public static double parseDoubleOrNull(String str) {
        return !StringUtils.isEmpty(str) ? Double.parseDouble(str) : 0;
    }

    public static Double convertToFahrenheit(String celsius) {
        return roundUpVal(Double.parseDouble(celsius) * 1.8 + 32);
    }

    public static Double KPH_TO_MPH(double kph) {
        return Utility.roundUpVal(0.6214 * kph);
    }

    public static Double MPH_TO_KPH(double mph) {
        return Utility.roundUpVal(mph * 1.60934);
    }

    public static Double KPH_TO_MeterPerSec(double kph) {
        return Utility.roundUpVal(kph * (5 / 18.0));
    }

    public static Double MPH_TO_MeterPerSec(double mph) {
        return Utility.roundUpVal(mph * 0.4470389);
    }

    public static String getTimeZone(User user) {
        return (user != null && !StringUtils.isEmpty(user.getTimeZone())) ? user.getTimeZone()
                : ZoneId.systemDefault().getId();
    }

    protected static String generateRandomString() {
        String SALTCHARS = "1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 5) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    public static String generateEncodedString(String input) {
        if (input != null && !input.isEmpty()) {
            return Base64.getEncoder().withoutPadding().encodeToString(input.getBytes(StandardCharsets.UTF_8));
        } else {
            return "";
        }
    }

    public static Map<String, String> generateEncryptedUrls(Object obj, String createdDate, boolean isContainerTracking,
                                                            String trackTraceUrl, String mode) {
        Map<String, String> urls = new HashMap<String, String>();
        String url = "";

        if (!Utility.isEmpty(trackTraceUrl)) {
            url = trackTraceUrl;
        }
        if (mode.equalsIgnoreCase(Constants.EXPRESS)) {
            url += "express/";
        } else if (isContainerTracking) {
            url += "container/";
        } else {
            url += "vehicle/";
        }
        if (obj instanceof String val) {
            String randomString1 = generateRandomString();
            String randomString2 = generateRandomString();
            String randomString3 = generateRandomString();
            url += generateEncodedString(randomString1 + val + randomString2);
            url += "/";
            url += generateEncodedString(createdDate);
            url += "/";
            url += generateEncodedString(randomString3);
            url += "/track/shipment";
            urls.put(val, url);
        }
        return urls;
    }

    public static Map<String, String> generateEncryptedOrderUrls(ShipmentV2 shipmentV2, String createdDate, String trackTraceUrl, String
            loadId, OrdersV2Repository ordersV2Repository, boolean isSingleLegShipment, boolean isMultiLegShipment) {
        List<OrderV2Doc> orderV2Docs = new ArrayList<>();
        Map<String, String> orderUrls = new HashMap<>();
        for (String order : shipmentV2.getOrders()) {
            String url = "";
            if (!Utility.isEmpty(trackTraceUrl)) {
                url = trackTraceUrl;
            }
            if (isMultiLegShipment) {
                url += "multi-modal-order/";
            }
            if (isSingleLegShipment) {
                url += "vehicle/";
            }
            if (!StringUtils.isEmpty(order)) {
                String randomString1 = generateRandomString();
                String randomString2 = generateRandomString();
                /*String randomString3 = generateRandomString();*/
                url += generateEncodedString(randomString1 + loadId + randomString2);
                url += "/";
                url += generateEncodedString(createdDate);
                url += "/";
                url += generateEncodedString(order);
                url += "/track/order";
                Optional<OrderV2Doc> byOrderId = ordersV2Repository.findByOrderId(order);
                if (byOrderId.isPresent()) {
                    OrderV2Doc orderV2Doc = byOrderId.get();
                    orderV2Doc.setTtUrl(url);
                    orderV2Docs.add(orderV2Doc);
                }
                orderUrls.put(order, url);
            }
        }
        ordersV2Repository.saveAll(orderV2Docs);
        return orderUrls;
    }

    public static List<ShipmentStopV2> getSortedStopsV2(ShipmentV2 shipment) {

        List<ShipmentStopV2> sortedStops = shipment.getStops();
        sortedStops.sort(Comparator.comparing(ShipmentStopV2::getStopNumber));
        return sortedStops;
    }

    public static ShipmentStopV2 getFirstStop(ShipmentV2 shipment) {
        List<ShipmentStopV2> sortedStops = getSortedStopsV2(shipment);
        return !sortedStops.isEmpty() ? sortedStops.get(0) : null;
    }

    public static String generateDecodedString(String input) {
        if (input != null && !input.isEmpty()) {
            byte[] decodedBytes = Base64.getDecoder().decode(input);
            return new String(decodedBytes);
        } else {
            return "";
        }
    }

    public static ShipmentStopV2 getLastDeliveryStop(ShipmentV2 shipment) {

        List<ShipmentStopV2> sortedStops = shipment.getStops().stream()
                .filter(x -> x.getStopType().equalsIgnoreCase("D")).collect(Collectors.toList());
        sortedStops.sort(Comparator.comparing(ShipmentStopV2::getStopNumber));

        ShipmentStopV2 stop = (sortedStops != null && sortedStops.size() > 0) ? sortedStops.get(0) : null;

        return stop;
    }

    public static ShipmentStopV2 getLastDDeliveryStop(ShipmentV2 shipment) {

        List<ShipmentStopV2> sortedStops = shipment.getStops().stream()
                .filter(x -> x.getStopType().equalsIgnoreCase("D")).collect(Collectors.toList());
        sortedStops.sort(Comparator.comparing(ShipmentStopV2::getStopNumber));

        return (sortedStops != null && sortedStops.size() > 0) ? (sortedStops.get(sortedStops.size() - 1)) : null;
    }

    public static String convertObjectToXML(FeedBean object) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(FeedBean.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter sw = new StringWriter();
            jaxbMarshaller.marshal(object, sw);
            String xmlContent = sw.toString();
            return xmlContent;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static ShipmentStopV2 getStopMatchesWithSequence(ShipmentV2 shipment, Integer sequence) {
        return shipment.getStops().stream()
                .filter(shipmentStopV21 -> sequence == shipmentStopV21.getStopNumber()).findFirst().orElse(null);
    }

    public static boolean isRoleExist(RoleType role) {
        return Stream.of(RoleType.values()).toList().contains(role);
    }

    public static ShipmentStopV2 getLastStop(ShipmentV2 shipment) {
        List<ShipmentStopV2> sortedStops = getSortedStopsV2(shipment);
        return (sortedStops != null && sortedStops.size() > 0) ? sortedStops.get(sortedStops.size() - 1) : null;
    }

    public static boolean isNotEmpty(String message) {
        return message != null && !message.trim().isEmpty();
    }

}
