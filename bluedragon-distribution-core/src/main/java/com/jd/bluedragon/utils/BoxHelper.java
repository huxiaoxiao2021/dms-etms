package com.jd.bluedragon.utils;


import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;

public class BoxHelper {

    public static Boolean isLuxuryForForward(Box box) {
        if (box == null || box.getType() == null) {
            return Boolean.FALSE;
        }

        if (Box.TYPE_BS.equalsIgnoreCase(box.getType())) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static Boolean isLuxuryForForward2(Box box) {
        if (box == null || box.getType() == null) {
            return Boolean.FALSE;
        }

        if (Box.TYPE_BS.equalsIgnoreCase(box.getType()) || Box.TYPE_BC.equalsIgnoreCase(box.getType())) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static Boolean isLuxuryForReverse(Box box) {
        if (box == null || box.getType() == null) {
            return Boolean.FALSE;
        }

        if (Box.TYPE_TS.equalsIgnoreCase(box.getType())
                || Box.TYPE_GS.equalsIgnoreCase(box.getType())) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static Boolean isOrdinaryForForward(Box box) {
        if (box == null || box.getType() == null) {
            return Boolean.FALSE;
        }

        if (Box.TYPE_BC.equalsIgnoreCase(box.getType())) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static Boolean isOrdinaryForWarehouse(Box box) {
        if (box == null || box.getType() == null) {
            return Boolean.FALSE;
        }

        if (Box.TYPE_TC.equalsIgnoreCase(box.getType())) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static Boolean isLuxuryForWarehouse(Box box) {
        if (box == null || box.getType() == null) {
            return Boolean.FALSE;
        }

        if (Box.TYPE_TS.equalsIgnoreCase(box.getType())) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static Boolean isLuxuryForWarehouse2(Box box) {
        if (box == null || box.getType() == null) {
            return Boolean.FALSE;
        }

        if (Box.TYPE_TS.equalsIgnoreCase(box.getType()) || Box.TYPE_BC.equalsIgnoreCase(box.getType())) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static Boolean isOrdinaryForAfterSale(Box box) {
        if (box == null || box.getType() == null) {
            return Boolean.FALSE;
        }

        if (Box.TYPE_GC.equalsIgnoreCase(box.getType())) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static Boolean isLuxuryForAfterSale(Box box) {
        if (box == null || box.getType() == null) {
            return Boolean.FALSE;
        }

        if (Box.TYPE_GS.equalsIgnoreCase(box.getType())) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static Boolean isLuxuryForAfterSale2(Box box) {
        if (box == null || box.getType() == null) {
            return Boolean.FALSE;
        }

        if (Box.TYPE_GS.equalsIgnoreCase(box.getType()) || Box.TYPE_BC.equalsIgnoreCase(box.getType())) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     *
     * 校验操作人分拣中心与箱号始发地是否一致
     * @param request
     * @return true:一致  false：不一致
     */
    public static boolean isTheSameSiteWithOprator(FilterContext request){

        Integer opratorSite = request.getCreateSiteCode();
        Integer boxCreateSite = request.getBox().getCreateSiteCode();
        return opratorSite.intValue() == boxCreateSite.intValue();
    }

}
