package org.example.strayanimalrescuebackend.Util;

import org.example.strayanimalrescuebackend.model.RescueReport;

import java.util.List;

public class StringUtils {
    public static List<RescueReport> removeQuotationMarks(List<RescueReport> rescueReportList){
        for (RescueReport rescueReport : rescueReportList) {
            StringUtils.removeQuotationMark(rescueReport);
        }

        return rescueReportList;

    }

    public static RescueReport removeQuotationMark(RescueReport rescueReport){
        System.out.println(rescueReport);
        String photo = rescueReport.getPhotos();
        if (photo.charAt(0) == '['){
            photo = photo.substring(1, photo.length() - 1);
        }
        String newPhoto = photo.substring(1, photo.length() - 1);
        rescueReport.setPhotos(newPhoto);
        return rescueReport;

    }


}
