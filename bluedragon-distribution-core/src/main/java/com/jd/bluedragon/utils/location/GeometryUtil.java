package com.jd.bluedragon.utils.location;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.utils.location.dto.LatLng;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.index.strtree.STRtree;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Liwenhao
 * @Date: 2022/7/11 13:54
 */
public class GeometryUtil {
    //使用JTS的GeometryFactory来创建Geometry对象
    private static GeometryFactory geometryFactory = new GeometryFactory();
    
    /**
     * @Description: 创建一个点
     * @return: com.vividsolutions.jts.geom.Point
     */
    public static Point createPoint(Coordinate coord) {
        return geometryFactory.createPoint(coord);
    }
    
    /**
     * @Description: 从WKT创建一个点，WKT字符串创建 ，WKT字符串是SQL标准定义的一个实现
     * @return: com.vividsolutions.jts.geom.Point
     */
    public static Point createPointByWKT() throws ParseException {
        WKTReader reader = new WKTReader();
        Point point = (Point) reader.read("POLYGON ((121.014439 31.22519060183881, 121.01476917414698 31.22518172840054, 121.01509804506206 31.225155143110115, 121.01542431465958 31.225110950902412, 121.0157466951258 31.22504932620843, 121.01606391400428 31.224970512266513, 121.01637471922022 31.224874820161812, 121.01667788402428 31.224762627597997, 121.0169722118364 31.224634377405746, 121.01725654097001 31.224490575794228, 121.01752974921841 31.224331790352302, 121.01779075828514 31.224158647807403, 121.01803853804053 31.223971831550976, 121.01827211058777 31.223772078940236, 121.01849055412279 31.223560178386883, 121.01869300657205 31.22333696624437, 121.01887866899443 31.22310332350597, 121.01904680873358 31.222860172326655, 121.01919676230828 31.222608472382635, 121.01932793802945 31.22234921708281, 121.01943981833361 31.222083429647313, 121.01953196182343 31.22181215906831, 121.01960400500745 31.221536475969394, 121.01965566373221 31.22125746837959, 121.01968673430095 31.22097623743892, 121.01969709427472 31.220693893052296, 121.01968670295282 31.220411549508988, 121.01965560153033 31.220130321084962, 121.0196039129328 31.219851317645443, 121.01953184132805 31.219575640265, 121.0194396713178 31.219304376882473, 121.01932776681173 31.21903859800796, 121.01919656958886 31.218779352498636, 121.01904659755175 31.218527663420197, 121.01887844268066 31.218284524010212, 121.01869276869543 31.218050893759273, 121.01849030843479 31.217827694625413, 121.01827186096305 31.217615807396758, 121.01803828841581 31.217416068216618, 121.01779051259717 31.217229265284896, 121.0175295113418 31.217056135748656, 121.01725631465621 31.216897362794185, 121.01697200065458 31.216753572951998, 121.01667769130485 31.216625333625338, 121.01637454800246 31.216513150852013, 121.01606376698845 31.216417467308304, 121.01574657463041 31.216338660562858, 121.01542422258488 31.216277041587354, 121.01509798286018 31.21623285352993, 121.01476914279884 31.21620627075615, 121.014439 31.216197398161196, 121.01410885720115 31.21620627075615, 121.01378001713982 31.21623285352993, 121.01345377741511 31.216277041587354, 121.0131314253696 31.216338660562858, 121.01281423301154 31.216417467308304, 121.01250345199753 31.216513150852013, 121.01220030869514 31.216625333625338, 121.01190599934543 31.216753572951998, 121.01162168534378 31.216897362794185, 121.0113484886582 31.217056135748656, 121.01108748740283 31.217229265284896, 121.0108397115842 31.217416068216618, 121.01060613903694 31.217615807396758, 121.01038769156521 31.217827694625413, 121.01018523130456 31.218050893759273, 121.00999955731933 31.218284524010212, 121.00983140244826 31.218527663420197, 121.00968143041115 31.218779352498636, 121.00955023318828 31.21903859800796, 121.00943832868221 31.219304376882473, 121.00934615867196 31.219575640265, 121.00927408706721 31.219851317645443, 121.00922239846967 31.220130321084962, 121.00919129704718 31.220411549508988, 121.00918090572527 31.220693893052296, 121.00919126569904 31.22097623743892, 121.00922233626778 31.22125746837959, 121.00927399499254 31.221536475969394, 121.00934603817657 31.22181215906831, 121.00943818166638 31.222083429647313, 121.00955006197054 31.22234921708281, 121.00968123769172 31.222608472382635, 121.00983119126641 31.222860172326655, 121.00999933100557 31.22310332350597, 121.01018499342796 31.22333696624437, 121.01038744587721 31.223560178386883, 121.01060588941222 31.223772078940236, 121.01083946195948 31.223971831550976, 121.01108724171486 31.224158647807403, 121.0113482507816 31.224331790352302, 121.01162145903 31.224490575794228, 121.01190578816359 31.224634377405746, 121.01220011597572 31.224762627597997, 121.01250328077978 31.224874820161812, 121.01281408599573 31.224970512266513, 121.0131313048742 31.22504932620843, 121.01345368534042 31.225110950902412, 121.01377995493795 31.225155143110115, 121.01410882585301 31.22518172840054, 121.014439 31.22519060183881))");
        return point;
    }
    
    /**
     * @Description: 从WKT创建多个点
     * @return: com.vividsolutions.jts.geom.MultiPoint
     */
    public static MultiPoint createMulPointByWKT(String wellKnownText) throws ParseException {
        WKTReader reader = new WKTReader(geometryFactory);
//        MultiPoint mpoint = (MultiPoint) reader.read("MULTIPOINT(119.013388 31.715519, 119.32488 31.435678)");
        MultiPoint mpoint = (MultiPoint) reader.read(wellKnownText);
        return mpoint;
    }
    
    /**
     * @Description: create a line
     * @return: com.vividsolutions.jts.geom.LineString
     */
    public static LineString createLine() {
        Coordinate[] coords = new Coordinate[]{new Coordinate(119.013388, 31.715519), new Coordinate(119.32488, 31.435678)};
        LineString line = geometryFactory.createLineString(coords);
        return line;
    }
    
    /**
     * @Description: create a line by WKT
     * @return: com.vividsolutions.jts.geom.LineString
     */
    public static LineString createLineByWKT() throws ParseException {
        WKTReader reader = new WKTReader(geometryFactory);
        LineString line = (LineString) reader.read("LINESTRING(119.013388 31.715519, 119.32488 31.435678)");
        return line;
    }
    
    /**
     * @Description: create multiLine
     * @return: com.vividsolutions.jts.geom.MultiLineString
     */
    public static MultiLineString createMLine(LineString[] lineStrings) {
//        Coordinate[] coords1 = new Coordinate[]{new Coordinate(119.013388, 31.715519), new Coordinate(119.32488, 31.435678)};
//        LineString line1 = geometryFactory.createLineString(coords1);
//        Coordinate[] coords2 = new Coordinate[]{new Coordinate(118.797499, 32.087104), new Coordinate(118.798128, 31.968592)};
//        LineString line2 = geometryFactory.createLineString(coords2);
//        LineString[] lineStrings = new LineString[2];
//        lineStrings[0] = line1;
//        lineStrings[1] = line2;
//        MultiLineString ms = geometryFactory.createMultiLineString(lineStrings);
//        return ms;
        if (lineStrings == null || lineStrings.length == 0) {
            return null;
        }
        return geometryFactory.createMultiLineString(lineStrings);
    }
    
    /**
     * @Description: create multiLine by WKT
     * @return: com.vividsolutions.jts.geom.MultiLineString
     */
    public static MultiLineString createMLineByWKT() throws ParseException {
        WKTReader reader = new WKTReader(geometryFactory);
        MultiLineString line = (MultiLineString) reader.read("MULTILINESTRING((119.013388 31.715519, 119.32488 31.435678),(118.797499 32.087104,118.798128 31.968592))");
        return line;
    }
    
    /**
     * @Description: create a polygon(多边形)
     * @return:
     */
    public static Polygon createPolygon(Coordinate[] coords) {
        if (coords == null || coords.length == 0) {
            return null;
        }
//        Coordinate[] coords = new Coordinate[latLngList.size()];
//        for (int i = 0; i < latLngList.size(); i++) {
//            coords[i] = new Coordinate(latLngList.get(i).getLng(), latLngList.get(i).getLat());
//        }
        return geometryFactory.createPolygon(coords);
    }
    
    /**
     * @Description: create a polygon(多边形) by WKT
     * @return:
     */
    public static Polygon createPolygonByWKT(String wkt) throws ParseException {
        WKTReader reader = new WKTReader(geometryFactory);
        Polygon polygon = (Polygon) reader.read(wkt);
        return polygon;
    }
    
    /**
     * @Description: create multi polygon(多边形) by WKT
     * @return:
     */
    public static MultiPolygon createMulPolygonByWKT() throws ParseException {
        WKTReader reader = new WKTReader(geometryFactory);
        MultiPolygon mpolygon = (MultiPolygon) reader.read("MULTIPOLYGON(((40 10, 30 0, 40 10, 30 20, 40 10),(30 10, 30 0, 40 10, 30 20, 30 10)))");
        return mpolygon;
    }
    
    /**
     * @Description: create GeometryCollection  contain point or multiPoint or line or multiLine or polygon or multiPolygon
     * @return: com.vividsolutions.jts.geom.GeometryCollection
     */
    public static GeometryCollection createGeoCollect() throws ParseException {
        LineString line = createLine();
        Polygon poly = createPolygonByWKT(null);
        Geometry g1 = geometryFactory.createGeometry(line);
        Geometry g2 = geometryFactory.createGeometry(poly);
        Geometry[] garray = new Geometry[]{g1, g2};
        GeometryCollection gc = geometryFactory.createGeometryCollection(garray);
        return gc;
    }
    
    /**
     * create a Circle  创建一个圆，圆心(x,y) 半径RADIUS
     *
     * @param x
     * @param y
     * @param RADIUS
     * @return
     */
    public static Polygon createCircle(double x, double y, final double RADIUS) {
        final int SIDES = 100;//圆上面的点个数
        Coordinate coords[] = new Coordinate[SIDES + 1];
        for (int i = 0; i < SIDES; i++) {
            double angle = ((double) i / (double) SIDES) * Math.PI * 2.0;
            double dx = Math.cos(angle) * RADIUS;
            double dy = Math.sin(angle) * RADIUS;
            coords[i] = new Coordinate((double) x + dx, (double) y + dy);
        }
        coords[SIDES] = coords[0];
        LinearRing ring = geometryFactory.createLinearRing(coords);
        Polygon polygon = geometryFactory.createPolygon(ring, null);
        return polygon;
    }
    public static void main(String[] args) throws ParseException {
        Geometry geometryByWKT = createGeometryByWKT("POLYGON ((123.56278200000001 41.76594960183881, 123.56316054207265 41.765940728201016, 123.56353758989961 41.76591414231516, 123.56391165513719 41.76586994912551, 123.56428126122245 41.76580832307855, 123.56464494920522 41.76572950743397, 123.5650012835103 41.765633813303786, 123.5653488576073 41.765521618423605, 123.56568629956537 41.765393365660636, 123.56601227747086 41.76524956126462, 123.5663255046868 41.765090772868305, 123.5666247449331 41.764917627245744, 123.5669088171672 41.76473080783692, 123.56717660024653 41.76453105204876, 123.56742703735364 41.764319148343084, 123.56765914016708 41.76409593312307, 123.56787199276114 41.76386228743052, 123.56806475521942 41.763619133467, 123.56823666694771 41.76336743095263, 123.56838704967338 41.763108173336846, 123.56851531011928 41.7628423838763, 123.56862094234175 41.76257111159509, 123.56870352972354 41.76229542714368, 123.56876274661376 41.76201641857237, 123.56879835960876 41.761735187036706, 123.56881022846838 41.76145284245095, 123.56879830666469 41.761170499107514, 123.56876264156057 41.76088927127922, 123.56870337421799 41.76061026882187, 123.56862073883627 41.760334592794564, 123.56851506182326 41.76006333111479, 123.56838676050259 41.75979755426579, 123.56823634146257 41.75953831107278, 123.56806439855299 41.75928662456495, 123.56787161053829 41.75904348793931, 123.56765873841566 41.75880986064254, 123.56742662240954 41.758586664586126, 123.56717617865364 41.758374780509605, 123.56690839557433 41.75817504450662, 123.566624329989 41.75798824472696, 123.5663251029354 41.757815118267985, 123.56601189524798 41.75765634826747, 123.56568594289892 41.757512561209325, 123.56534853212214 41.7573843244529, 123.5650009943395 41.75727214399549, 123.56464470090918 41.757176462476856, 123.56428105771697 41.75709765743369, 123.56391149963164 41.757036039810856, 123.56353748484642 41.75699185273516, 123.56316048912858 41.756965270556684, 123.56278200000001 41.7569563981612, 123.56240351087143 41.756965270556684, 123.56202651515359 41.75699185273516, 123.56165250036837 41.757036039810856, 123.56128294228304 41.75709765743369, 123.56091929909083 41.757176462476856, 123.56056300566051 41.75727214399549, 123.56021546787787 41.7573843244529, 123.55987805710109 41.757512561209325, 123.55955210475203 41.75765634826747, 123.55923889706462 41.757815118267985, 123.55893967001101 41.75798824472696, 123.5586556044257 41.75817504450662, 123.55838782134637 41.758374780509605, 123.55813737759047 41.758586664586126, 123.55790526158435 41.75880986064254, 123.55769238946172 41.75904348793931, 123.55749960144702 41.75928662456495, 123.55732765853745 41.75953831107278, 123.55717723949743 41.75979755426579, 123.55704893817675 41.76006333111479, 123.55694326116374 41.760334592794564, 123.55686062578202 41.76061026882187, 123.55680135843944 41.76088927127922, 123.55676569333534 41.761170499107514, 123.55675377153163 41.76145284245095, 123.55676564039126 41.761735187036706, 123.55680125338627 41.76201641857237, 123.55686047027649 41.76229542714368, 123.55694305765826 41.76257111159509, 123.55704868988074 41.7628423838763, 123.55717695032664 41.763108173336846, 123.55732733305231 41.76336743095263, 123.55749924478059 41.763619133467, 123.55769200723887 41.76386228743052, 123.55790485983295 41.76409593312307, 123.55813696264639 41.764319148343084, 123.5583873997535 41.76453105204876, 123.55865518283281 41.76473080783692, 123.55893925506693 41.764917627245744, 123.55923849531321 41.765090772868305, 123.55955172252915 41.76524956126462, 123.55987770043465 41.765393365660636, 123.5602151423927 41.765521618423605, 123.56056271648971 41.765633813303786, 123.5609190507948 41.76572950743397, 123.56128273877756 41.76580832307855, 123.56165234486282 41.76586994912551, 123.56202641010042 41.76591414231516, 123.56240345792736 41.765940728201016, 123.56278200000001 41.76594960183881))");
        if (checkCircle(geometryByWKT)) {
            System.out.println(GeoUtils.getDistance(new LatLng(geometryByWKT.getCoordinates()[0].y, geometryByWKT.getCoordinates()[0].x), new LatLng(41.761453,123.562782)));
            System.out.println(geometryByWKT.getLength() / 2 / Math.PI);
        }

        geometryByWKT = createPolygonByWKT("POLYGON ((116.36185 39.675062, 116.361598 39.675286, 116.356684 39.674588, 116.356663 39.674019, 116.356385 39.673987, 116.356543 39.672884, 116.356873 39.672905, 116.356914 39.672592, 116.356599 39.672562, 116.356793 39.671507, 116.357081 39.671543, 116.357113 39.67141, 116.361732 39.671952, 116.362054 39.673676, 116.36193 39.674961, 116.36185 39.675062))");
        final Coordinate[] coordinates = geometryByWKT.getCoordinates();
        List<LatLng> mPoints = new ArrayList<>();
        for (Coordinate coordinate : coordinates) {
            mPoints.add(new LatLng(coordinate.y, coordinate.x));
        }
        final LatLng centerOfGravityPoint = getCenterOfGravityPoint(mPoints);
        System.out.println(JSON.toJSONString(centerOfGravityPoint));
    }
    
    
    /**
     * 获取不规则多边形重心点 
     *
     * @param mPoints
     * @return
     */
    public static LatLng getCenterOfGravityPoint(List<LatLng> mPoints) {
        double area = 0.0;//多边形面积  
        double Gx = 0.0, Gy = 0.0;// 重心的x、y  
        for (int i = 1; i <= mPoints.size(); i++) {
            double iLat = mPoints.get(i % mPoints.size()).getLat();
            double iLng = mPoints.get(i % mPoints.size()).getLng();
            double nextLat = mPoints.get(i - 1).getLat();
            double nextLng = mPoints.get(i - 1).getLng();
            double temp = (iLat * nextLng - iLng * nextLat) / 2.0;
            area += temp;
            Gx += temp * (iLat + nextLat) / 3.0;
            Gy += temp * (iLng + nextLng) / 3.0;
        }
        Gx = Gx / area;
        Gy = Gy / area;
        return new LatLng(Gx, Gy);
    }
    
    public static boolean isPointInPolygon(Point point, Coordinate[] coords) {
        Polygon p1 = createPolygon(coords);

        STRtree stRtree = new STRtree();
        stRtree.insert(p1.getEnvelopeInternal(), p1);

        List results = stRtree.query(point.getEnvelopeInternal());
        return CollectionUtils.isNotEmpty(results) && p1.contains(point);
    }
    
    /**
     * @Description: create a polygon(多边形) by WKT
     * @return:
     */
    public static Geometry createGeometryByWKT(String wkt) throws ParseException {
        WKTReader reader = new WKTReader(geometryFactory);
        return reader.read(wkt);
    }
    
    public static boolean checkCircle(Geometry fenceGeometry) {
        if(fenceGeometry == null){
            return false;
        }
        Coordinate[] coordinates = fenceGeometry.getCoordinates();
        if(coordinates == null || coordinates.length < 90) {
            return false;
        }
        
        double length = fenceGeometry.getLength();
        double area = fenceGeometry.getArea();
        if(area > 0) {
            double result = length * length / area;
            return result > Math.PI * 4;
        }
        return false;
    }
    
}
