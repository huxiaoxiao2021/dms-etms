package com.jd.bluedragon.utils.location;

import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.location.dto.LatLng;
import com.jd.bluedragon.utils.location.dto.Rectangle;
import com.jd.tms.jdi.dto.GisAreaCoordsDto;
import com.jd.tms.jdi.dto.GisAreaDto;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;


/**
 * GeoUtils类提供若干几何算法，用来帮助用户判断点与矩形、 圆形、多边形线、多边形面的关系, 并提供计算折线长度和多边形的面积的公式。
 *
 * 参考百度地图GeoUtils.js类,基于Baidu Map API 1.2。
 *
 * @author suihonghua
 */
@Slf4j
public class GeoUtils {

    /**
     * 地球长半轴
     */
    public static final double EARTHRADIUS = 6370996.81D;
    /**
     * 坐标转换值
     */
    public static final double X_PI = Math.PI * 3000.0 / 180.0;

    private static final double a = 6378245.0;// 坐标转换常量
    private static final double ee = 0.00669342162296594323;// 坐标转换常量

    private static final int AREA_SHAPE_CIRCLE = 1;
    private static final int AREA_SHAPE_POLYGON = 2;

    /**
     * 经纬度坐标转换为平面坐标
     *
     * @author suihonghua
     * @param latlng
     * @return
     * @
     */
    public static LatLng transformLatLngToXY(LatLng latlng)  {
        double x = latlng.getLng() * Math.PI * EARTHRADIUS / 180.0;
        double tmp = Math.log(Math.tan((90 + latlng.getLat()) * Math.PI / 360.0)) / (Math.PI / 180.0);
        double y = tmp * Math.PI * EARTHRADIUS / 180.0;
        return new LatLng(y, x);
    }

    /**
     * 平面坐标转换为经纬度坐标
     *
     * @author suihonghua
     * @param xy
     * @return
     * @
     */
    public static LatLng transformXYToLatLng(LatLng xy)  {
        double lng = (xy.getLng() / (Math.PI * EARTHRADIUS)) * 180.0;
        double tmp = (xy.getLat() / (Math.PI * EARTHRADIUS)) * 180.0;
        double lat = 180.0 / Math.PI * (2 * Math.atan(Math.exp(tmp * Math.PI / 180.0)) - Math.PI / 2.0);
        return new LatLng(lat, lng);
    }

    /**
     * 将火星坐标(GCJ-02)转换成 百度坐标(BD-09)。
     *
     * 地球坐标 (WGS84)国际标准: GPS。<br>
     * 火星坐标 (GCJ-02)中国标准:谷歌地图，高德地图，腾讯地图。<br>
     * 百度坐标 (BD-09)百度标准:百度地图。<br>
     *
     * @author suihonghua
     * @param point
     * @return
     */
    public static LatLng transformGCJ02ToBD09(LatLng point)  {
        double x = point.getLng();
        double y = point.getLat();
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * X_PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * X_PI);
        double lng = z * Math.cos(theta) + 0.0065;
        double lat = z * Math.sin(theta) + 0.006;
        return new LatLng(lat, lng);
    }

    /**
     * 将百度坐标(BD-09)转换成 火星坐标(GCJ-02)
     *
     * 地球坐标 (WGS84)国际标准: GPS。<br>
     * 火星坐标 (GCJ-02)中国标准:谷歌地图，高德地图，腾讯地图。<br>
     * 百度坐标 (BD-09)百度标准:百度地图。<br>
     *
     * @author suihonghua
     * @param point
     * @return
     */
    public static LatLng transformBD09ToGCJ02(LatLng point)  {
        double x = point.getLng() - 0.0065;
        double y = point.getLat() - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * X_PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * X_PI);
        double lng = z * Math.cos(theta);
        double lat = z * Math.sin(theta);
        return new LatLng(lat, lng);
    }

    /**
     * 将地球坐标 (WGS84)转换成 火星坐标 (GCJ-02)
     *
     * 参考(https://on4wp7.codeplex.com/SourceControl/changeset/view/21483#353936)
     * 实现<br>
     *
     * 地球坐标 (WGS84)国际标准: GPS。<br>
     * 火星坐标 (GCJ-02)中国标准:谷歌地图，高德地图，腾讯地图。<br>
     * 百度坐标 (BD-09)百度标准:百度地图。<br>
     *
     * @author suihonghua
     * @param point
     * @return
     */
    public static LatLng transformWGS84ToGCJ02(LatLng point)  {
        if (outOfChina(point)) {
            return new LatLng(point);
        }
        double dLat = transformLat(point.getLng() - 105.0, point.getLat() - 35.0);
        double dLon = transformLon(point.getLng() - 105.0, point.getLat() - 35.0);
        double radLat = point.getLat() / 180.0 * Math.PI;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * Math.PI);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * Math.PI);
        double mgLat = point.getLat() + dLat;
        double mgLon = point.getLng() + dLon;
        return new LatLng(mgLat, mgLon);
    }

    /**
     * 纬度中间计算值(WGS84 to GCJ-02)
     *
     * @author suihonghua
     * @param x
     * @param y
     * @return
     */
    private static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x * Math.PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * Math.PI) + 40.0 * Math.sin(y / 3.0 * Math.PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * Math.PI) + 320 * Math.sin(y * Math.PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    /**
     * 经度中间计算值(WGS84 to GCJ-02)
     *
     * @author suihonghua
     * @param x
     * @param y
     * @return
     */
    private static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x * Math.PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * Math.PI) + 40.0 * Math.sin(x / 3.0 * Math.PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * Math.PI) + 300.0 * Math.sin(x / 30.0 * Math.PI)) * 2.0 / 3.0;
        return ret;
    }

    /**
     * 判断经纬度坐标点是否在中国境外
     *
     * @author suihonghua
     * @return
     */
    public static boolean outOfChina(LatLng point) {
        if (point.getLng() < 72.004 || point.getLng() > 137.8347)
            return true;
        if (point.getLat() < 0.8293 || point.getLat() > 55.8271)
            return true;
        return false;
    }

    /**
     * 判断点是否在圆形内
     *
     * @author suihonghua
     * @param point
     *            点对象
     * @param circleCenter
     *            圆心坐标
     * @param circleRadius
     *            圆形半径
     * @return 点在圆形内返回true,否则返回false
     * @
     */
    public static boolean isPointInCircle(LatLng point, LatLng circleCenter, double circleRadius)  {
        // point与圆心距离小于圆形半径，则点在圆内，否则在圆外
        double dis = getDistance(point, circleCenter);
        if (dis <= circleRadius) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断点是否在矩形内
     *
     * @author suihonghua
     * @param point
     *            点对象
     * @param rect
     *            矩形对象
     * @return 点在矩形内返回true,否则返回false
     */
    public static boolean isPointInRectangle(LatLng point, Rectangle rect)  {
        LatLng sw = rect.getSouthWest(); // 西南脚点
        LatLng ne = rect.getNorthEast(); // 东北脚点
        return (point.getLng() >= sw.getLng() && point.getLng() <= ne.getLng() && point.getLat() >= sw.getLat() && point.getLat() <= ne.getLat());
    }

    /**
     * 判断点是否在多边形内(如果点位于多边形的顶点或边上，也算做点在多边形内)
     *
     * @author suihonghua
     * @param point
     *            点对象
     * @param path
     *            边界路径
     * @return 点在多边形内返回true,否则返回false
     * @
     */
    public static boolean isPointInPolygon(LatLng point, List<LatLng> path)  {
        // 首先判断点是否在多边形的外包矩形内，如果在，则进一步判断，否则返回false
        Rectangle rect = getEnclosingRectangle(path);
        if (!isPointInRectangle(point, rect)) {
            return false;
        }
        // 下述代码来源：http://paulbourke.net/geometry/insidepoly/，进行了部分修改
        // 基本思想是利用射线法，计算射线与多边形各边的交点，如果是偶数，则点在多边形外，否则
        // 在多边形内。还会考虑一些特殊情况，如点在多边形顶点上，点在多边形边上等特殊情况。

        int N = path.size();
        boolean boundOrVertex = true; // 如果点位于多边形的顶点或边上，也算做点在多边形内，直接返回true
        int intersectCount = 0;// cross points count of x
        double precision = 2e-10; // 浮点类型计算时候与0比较时候的容差
        LatLng p1, p2;// neighbour bound vertices
        LatLng p = point; // 测试点

        p1 = path.get(0);// left vertex
        for (int i = 1; i <= N; ++i) {// check all rays
            if (p.equals(p1)) {
                return boundOrVertex;// p is an vertex
            }

            p2 = path.get(i % N);// right vertex
            if (p.getLat() < Math.min(p1.getLat(), p2.getLat()) || p.getLat() > Math.max(p1.getLat(), p2.getLat())) {
                // ray is outside of our interests
                p1 = p2;
                continue;// next ray left point
            }

            if (p.getLat() > Math.min(p1.getLat(), p2.getLat()) && p.getLat() < Math.max(p1.getLat(), p2.getLat())) {
                // ray is crossing over by the algorithm (common part of)
                if (p.getLng() <= Math.max(p1.getLng(), p2.getLng())) {
                    // x is before of ray
                    if (p1.getLat() == p2.getLat() && p.getLng() >= Math.min(p1.getLng(), p2.getLng())) {
                        // overlies on a horizontal ray
                        return boundOrVertex;
                    }
                    if (p1.getLng() == p2.getLng()) {
                        // ray is vertical
                        if (p1.getLng() == p.getLng()) {
                            // overlies on a vertical ray
                            return boundOrVertex;
                        } else {// before ray
                            ++intersectCount;
                        }
                    } else {// cross point on the left side
                        double xinters = (p.getLat() - p1.getLat()) * (p2.getLng() - p1.getLng()) / (p2.getLat() - p1.getLat()) + p1.getLng();
                        // cross point of lng
                        if (Math.abs(p.getLng() - xinters) < precision) {
                            // overlies on a ray
                            return boundOrVertex;
                        }
                        if (p.getLng() < xinters) {
                            // before ray
                            ++intersectCount;
                        }
                    }
                }
            } else {
                // special case when ray is crossing through the vertex
                if (p.getLat() == p2.getLat() && p.getLng() <= p2.getLng()) {
                    // p crossing over p2
                    LatLng p3 = path.get((i + 1) % N); // next vertex
                    if (p.getLat() >= Math.min(p1.getLat(), p3.getLat()) && p.getLat() <= Math.max(p1.getLat(), p3.getLat())) {
                        // p.lat lies between p1.lat & p3.lat
                        ++intersectCount;
                    } else {
                        intersectCount += 2;
                    }
                }
            }
            p1 = p2;// next ray left point
        }
        if (intersectCount % 2 == 0) {
            // 偶数在多边形外
            return false;
        } else {
            // 奇数在多边形内
            return true;
        }
    }

    /**
     * 判断点是否在折线上
     *
     * @author suihonghua
     * @param point
     *            点对象
     * @param path
     *            折线路径
     * @return 点在折线上返回true,否则返回false
     * @
     */
    public static boolean isPointOnPolyline(LatLng point, List<LatLng> path)  {
        // 首先判断点是否在线的外包矩形内，如果在，则进一步判断，否则返回false
        Rectangle rect = getEnclosingRectangle(path);
        if (!isPointInRectangle(point, rect)) {
            return false;
        }
        // 判断点是否在线段上，设点为Q，线段为P1P2 ，
        // 判断点Q在该线段上的依据是：( Q - P1 ) × ( P2 - P1 ) = 0，且 Q 在以 P1，P2为对角顶点的矩形内
        for (int i = 0; i < path.size() - 1; i++) {
            LatLng curPt = path.get(i);
            LatLng nextPt = path.get(i + 1);
            if (isPointOnStraightLine(point, curPt, nextPt)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断点是否在直线上
     *
     * @author suihonghua
     * @param point
     *            点对象
     * @param point1
     *            直线起点
     * @param point2
     *            直线终点
     * @return
     * @
     */
    public static boolean isPointOnStraightLine(LatLng point, LatLng point1, LatLng point2)  {
        // 首先判断point是否在point1和point2之间，即：此判断该点是否在该线段的外包矩形内
        if (point.getLng() >= Math.min(point1.getLng(), point2.getLng()) && point.getLng() <= Math.max(point1.getLng(), point2.getLng())
                && point.getLat() >= Math.min(point1.getLat(), point2.getLat()) && point.getLat() <= Math.max(point1.getLat(), point2.getLat())) {
            // 判断点是否在直线上公式
            double precision = (point1.getLng() - point.getLng()) * (point2.getLat() - point.getLat()) - (point2.getLng() - point.getLng())
                    * (point1.getLat() - point.getLat());
            if (precision < 2e-10 && precision > -2e-10) {// 实质判断是否接近0
                return true;
            }
        }
        return false;
    }

    /**
     * 获取圆形的外切矩形
     *
     * @author suihonghua
     * @param circleCenter
     *            圆心坐标
     * @param circleRadius
     *            圆形半径
     * @return
     * @
     */
    public static Rectangle getEnclosingRectangle(LatLng circleCenter, double circleRadius)  {
        LatLng center_xy = transformLatLngToXY(circleCenter);// 圆心二维坐标
        double max_y = center_xy.getLat() + circleRadius;
        double max_x = center_xy.getLng() + circleRadius;
        double min_y = center_xy.getLat() - circleRadius;
        double min_x = center_xy.getLng() - circleRadius;

        LatLng north_east = transformXYToLatLng(new LatLng(max_y, max_x));
        LatLng south_west = transformXYToLatLng(new LatLng(min_y, min_x));
        return getRectangle(north_east.getLat(), north_east.getLng(), south_west.getLat(), south_west.getLng());
    }

    /**
     * 获取折线的外切矩形
     *
     * @author suihonghua
     * @param path
     *            折线路径
     * @return 外切矩形
     * @
     */
    public static Rectangle getEnclosingRectangle(List<LatLng> path)  {
        double max_lat = 0.0D;// 最大纬度
        double max_lng = 0.0D;// 最大经度
        double min_lat = 90.0D;// 最小纬度
        double min_lng = 180.0D;// 最小经度
        for (LatLng latLng : path) {
            max_lat = Math.max(max_lat, latLng.getLat());
            max_lng = Math.max(max_lng, latLng.getLng());
            min_lat = Math.min(min_lat, latLng.getLat());
            min_lng = Math.min(min_lng, latLng.getLng());
        }
        return getRectangle(max_lat, max_lng, min_lat, min_lng);
    }

    /**
     * 构建矩形对象Rectangle
     *
     * @author suihonghua
     * @param max_lat
     * @param max_lng
     * @param min_lat
     * @param min_lng
     * @return
     * @
     */
    public static Rectangle getRectangle(double max_lat, double max_lng, double min_lat, double min_lng)  {
        Rectangle rect = new Rectangle();
        rect.setNorthEast(new LatLng(max_lat, max_lng));
        rect.setNorthWest(new LatLng(max_lat, min_lng));
        rect.setSouthEast(new LatLng(min_lat, max_lng));
        rect.setSouthWest(new LatLng(min_lat, min_lng));
        return rect;
    }

    /**
     * 获取矩形中心点坐标
     *
     * @author suihonghua
     * @param rectangle
     * @return
     * @
     */
    public static LatLng getRectangleCenter(Rectangle rectangle)  {
        double center_lat = (rectangle.getNorthEast().getLat() + rectangle.getSouthWest().getLat()) / 2;// 中心纬度
        double center_lng = (rectangle.getNorthEast().getLng() + rectangle.getSouthWest().getLng()) / 2;// 中心经度
        return new LatLng(center_lat, center_lng);
    }

    /**
     * 计算折线或者点数组的长度
     *
     * @author suihonghua
     * @param path
     *            折线对象或者点数组
     * @return 折线或点数组对应的长度
     * @
     */
    public static double getPolylineDistance(List<LatLng> path)  {
        if (path == null || path.size() < 2) {// 小于2个点，返回0
            return 0;
        }
        // 遍历所有线段将其相加，计算整条线段的长度
        double totalDis = 0.0D;
        for (int i = 0; i < path.size() - 1; i++) {
            LatLng curPt = path.get(i);
            LatLng nextPt = path.get(i + 1);
            double dis = getDistance(curPt, nextPt);
            totalDis += dis;
        }
        return totalDis;
    }

    /**
     * 计算两点之间的距离,两点坐标必须为经纬度
     *
     * @author suihonghua
     * @param point1
     * @param point2
     * @return 两点之间距离，单位为米
     * @
     */
    public static double getDistance(LatLng point1, LatLng point2)  {
        double x1 = _getLoop(point1.getLng(), -180, 180);
        double y1 = _getRange(point1.getLat(), -74, 74);
        double x2 = _getLoop(point2.getLng(), -180, 180);
        double y2 = _getRange(point2.getLat(), -74, 74);

        x1 = degreeToRad(x1);
        y1 = degreeToRad(y1);
        x2 = degreeToRad(x2);
        y2 = degreeToRad(y2);

        return EARTHRADIUS * Math.acos((Math.sin(y1) * Math.sin(y2) + Math.cos(y1) * Math.cos(y2) * Math.cos(x2 - x1)));
    }

    /**
     * 根据提供的经度和纬度、以及半径，取得此半径内的最大最小经纬度
     * The circumference of the earth is 24,901 miles.
     * 24,901/360 = 69.17 miles / degree
     *
     * @param radius 单位米
     * see http://snipperize.todayclose.com/snippet/php/SQL-Query-to-Find-All-Retailers-Within-a-Given-Radius-of-a-Latitude-and-Longitude--65095/
     * return minLat,minLng,maxLat,maxLng
     */
    public static double[] getAround(double lat, double lng, int radius){
        Double latitude = lat;
        Double longitude = lng;

        Double degree = (24901 * 1609) / 360.0;
        double raidusMile = radius;

        Double dpmLat = 1 / degree;
        Double radiusLat = dpmLat * raidusMile;
        Double minLat = latitude - radiusLat;
        Double maxLat = latitude + radiusLat;

        Double mpdLng = degree * Math.cos(latitude * (Math.PI / 180));
        Double dpmLng = 1 / mpdLng;
        Double radiusLng = dpmLng * raidusMile;
        Double minLng = longitude - radiusLng;
        Double maxLng = longitude + radiusLng;
        return new double[]{minLat,minLng,maxLat,maxLng};
    }

    /**
     * 将度转化为弧度
     *
     * @author suihonghua
     * @param {degree} Number 度
     * @returns {Number} 弧度
     */
    public static double degreeToRad(double degree) {
        return Math.PI * degree / 180;
    }

    /**
     * 将弧度转化为度
     *
     * @author suihonghua
     * @param {radian} Number 弧度
     * @returns {Number} 度
     */
    public static double radToDegree(double rad) {
        return (180 * rad) / Math.PI;
    }

    /**
     * 将v值限定在a,b之间，纬度使用
     *
     * @author suihonghua
     */
    private static double _getRange(double v, double a, double b) {
        v = Math.max(v, a);
        v = Math.min(v, b);
        return v;
    }

    /**
     * 将v值限定在a,b之间，经度使用
     *
     * @author suihonghua
     */
    private static double _getLoop(double v, double a, double b) {
        while (v > b) {
            v -= b - a;
        }
        while (v < a) {
            v += b - a;
        }
        return v;
    }

    /**
     * 计算多边形面或点数组构建图形的面积,注意：坐标类型只能是经纬度，且不适合计算自相交多边形的面积
     *
     * @author suihonghua
     * @param path
     *            多边形面对象或者点数组
     * @return 多边形面或点数组构成图形的面积
     * @
     */
    public static double getPolygonArea(List<LatLng> path)  {
        if (path == null || path.size() < 3) {// 小于3个顶点，不能构建面
            return 0;
        }

        double totalArea = 0;// 初始化总面积
        double LowX = 0.0;
        double LowY = 0.0;
        double MiddleX = 0.0;
        double MiddleY = 0.0;
        double HighX = 0.0;
        double HighY = 0.0;
        double AM = 0.0;
        double BM = 0.0;
        double CM = 0.0;
        double AL = 0.0;
        double BL = 0.0;
        double CL = 0.0;
        double AH = 0.0;
        double BH = 0.0;
        double CH = 0.0;
        double CoefficientL = 0.0;
        double CoefficientH = 0.0;
        double ALtangent = 0.0;
        double BLtangent = 0.0;
        double CLtangent = 0.0;
        double AHtangent = 0.0;
        double BHtangent = 0.0;
        double CHtangent = 0.0;
        double ANormalLine = 0.0;
        double BNormalLine = 0.0;
        double CNormalLine = 0.0;
        double OrientationValue = 0.0;
        double AngleCos = 0.0;
        double Sum1 = 0.0;
        double Sum2 = 0.0;
        int Count2 = 0;
        int Count1 = 0;
        double Sum = 0.0;
        double Radius = EARTHRADIUS; // 6378137.0,WGS84椭球半径
        int Count = path.size();
        for (int i = 0; i < Count; i++) {
            if (i == 0) {
                LowX = path.get(Count - 1).getLng() * Math.PI / 180;
                LowY = path.get(Count - 1).getLat() * Math.PI / 180;
                MiddleX = path.get(0).getLng() * Math.PI / 180;
                MiddleY = path.get(0).getLat() * Math.PI / 180;
                HighX = path.get(1).getLng() * Math.PI / 180;
                HighY = path.get(1).getLat() * Math.PI / 180;
            } else if (i == Count - 1) {
                LowX = path.get(Count - 2).getLng() * Math.PI / 180;
                LowY = path.get(Count - 2).getLat() * Math.PI / 180;
                MiddleX = path.get(Count - 1).getLng() * Math.PI / 180;
                MiddleY = path.get(Count - 1).getLat() * Math.PI / 180;
                HighX = path.get(0).getLng() * Math.PI / 180;
                HighY = path.get(0).getLat() * Math.PI / 180;
            } else {
                LowX = path.get(i - 1).getLng() * Math.PI / 180;
                LowY = path.get(i - 1).getLat() * Math.PI / 180;
                MiddleX = path.get(i).getLng() * Math.PI / 180;
                MiddleY = path.get(i).getLat() * Math.PI / 180;
                HighX = path.get(i + 1).getLng() * Math.PI / 180;
                HighY = path.get(i + 1).getLat() * Math.PI / 180;
            }
            AM = Math.cos(MiddleY) * Math.cos(MiddleX);
            BM = Math.cos(MiddleY) * Math.sin(MiddleX);
            CM = Math.sin(MiddleY);
            AL = Math.cos(LowY) * Math.cos(LowX);
            BL = Math.cos(LowY) * Math.sin(LowX);
            CL = Math.sin(LowY);
            AH = Math.cos(HighY) * Math.cos(HighX);
            BH = Math.cos(HighY) * Math.sin(HighX);
            CH = Math.sin(HighY);
            CoefficientL = (AM * AM + BM * BM + CM * CM) / (AM * AL + BM * BL + CM * CL);
            CoefficientH = (AM * AM + BM * BM + CM * CM) / (AM * AH + BM * BH + CM * CH);
            ALtangent = CoefficientL * AL - AM;
            BLtangent = CoefficientL * BL - BM;
            CLtangent = CoefficientL * CL - CM;
            AHtangent = CoefficientH * AH - AM;
            BHtangent = CoefficientH * BH - BM;
            CHtangent = CoefficientH * CH - CM;
            AngleCos = (AHtangent * ALtangent + BHtangent * BLtangent + CHtangent * CLtangent)
                    / (Math.sqrt(AHtangent * AHtangent + BHtangent * BHtangent + CHtangent * CHtangent) * Math.sqrt(ALtangent * ALtangent + BLtangent
                    * BLtangent + CLtangent * CLtangent));
            AngleCos = Math.acos(AngleCos);
            ANormalLine = BHtangent * CLtangent - CHtangent * BLtangent;
            BNormalLine = 0 - (AHtangent * CLtangent - CHtangent * ALtangent);
            CNormalLine = AHtangent * BLtangent - BHtangent * ALtangent;
            if (AM != 0)
                OrientationValue = ANormalLine / AM;
            else if (BM != 0)
                OrientationValue = BNormalLine / BM;
            else
                OrientationValue = CNormalLine / CM;
            if (OrientationValue > 0) {
                Sum1 += AngleCos;
                Count1++;
            } else {
                Sum2 += AngleCos;
                Count2++;
            }
        }
        double tempSum1, tempSum2;
        tempSum1 = Sum1 + (2 * Math.PI * Count2 - Sum2);
        tempSum2 = (2 * Math.PI * Count1 - Sum1) + Sum2;
        if (Sum1 > Sum2) {
            if ((tempSum1 - (Count - 2) * Math.PI) < 1)
                Sum = tempSum1;
            else
                Sum = tempSum2;
        } else {
            if ((tempSum2 - (Count - 2) * Math.PI) < 1)
                Sum = tempSum2;
            else
                Sum = tempSum1;
        }
        totalArea = (Sum - (Count - 2) * Math.PI) * Radius * Radius;
        return totalArea; // 返回总面积
    }
    
    public static boolean judgePointInArea(GisAreaDto area, LatLng latLng) {
        if (area == null) {
            return false;
        }
        if (area.getAreaShape() == null || AREA_SHAPE_CIRCLE == area.getAreaShape()) {
            LatLng nodeLatLng = new LatLng(area.getLat(), area.getLng());
            return isPointInCircle(latLng, nodeLatLng, area.getRadius());
        } else if (area.getAreaShape() == AREA_SHAPE_POLYGON) {
            log.info("GeoUtils.judgePointInArea.areaShape is 2,多边形判断逻辑begin，param：{}-{}", JsonHelper.toJson(area), JsonHelper.toJson(latLng));
            Point point = GeometryUtil.createPoint(new Coordinate(latLng.getLng(), latLng.getLat()));
            List<GisAreaCoordsDto> coordsDtoList = area.getGisAreaCoordsDtoList();
            if (CollectionUtils.isEmpty(coordsDtoList)) {
                return false;
            }
            Coordinate[] coords = new Coordinate[coordsDtoList.size() + 1];
            for (int i = 0; i < coordsDtoList.size(); i++) {
                GisAreaCoordsDto gisAreaCoordsDto = coordsDtoList.get(i);
                coords[i] = new Coordinate(gisAreaCoordsDto.getLng(), gisAreaCoordsDto.getLat());
            }
            coords[coordsDtoList.size()] = new Coordinate(coordsDtoList.get(0).getLng(), coordsDtoList.get(0).getLat());
            return GeometryUtil.isPointInPolygon(point, coords);
        }
        return false;
    }
}
