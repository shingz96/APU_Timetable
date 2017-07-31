package com.shing.aputimetable.utils;

import com.shing.aputimetable.entity.ApuClass;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class QueryUtils {

    /**
     * Return an array list that contain of all classes for the given intake code
     *
     * @param intake Intake code
     * @return array list that contain of all classes
     */
    public static ArrayList getAllClass(String intake) {
        String date = MyDateUtils.formatDate(MyDateUtils.getMondayDate(), "yyyy-MM-dd");
        String url = String.format("http://webspace.apiit.edu.my/intake-timetable/intake-result.php?week=%s&intake_Search_Week=%s&selectIntakeAll=", date, intake.toUpperCase());

        try {
            String html = makeHttpRequest(url);
            String content = getTimetableContent(html);
            return extractClass(content);
        } catch (Exception ex) {
            System.out.println("Error extracting timetable: - no timetable found for this week!");
        }
        return null;
    }

    /**
     * Make request to a given url and return response from the server
     * such as json, html source code, etc.
     *
     * @param url URL to be requested
     * @return {@link String} Response from the server
     */
    public static String makeHttpRequest(String url) {
        //Open Connection & Retrieve HTML
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        String html = "";
        try {
            Response response = client.newCall(request).execute();
            html = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return html;
    }

    /**
     * Parse and return an array list that contain of all classes for the given html source code
     *
     * @param html Html source code that contain of timetable content
     * @return {@link ArrayList} array list that contain of all classes
     */
    private static ArrayList extractClass(String html) {
        ArrayList<String> classes = findAllByTag(html, "tr");
        ArrayList<ApuClass> allClassDetails = new ArrayList<>();
        for (String eachClass : classes) {
            ArrayList<String> classDetails = findAllByTag(eachClass, "td");
            ApuClass c = new ApuClass();
            c.setDay(classDetails.get(0).split(",")[0].trim());
            c.setDate(classDetails.get(0).split(",")[1].trim());
            c.setTime(classDetails.get(1).trim());
            c.setRoom(classDetails.get(2).trim());
            c.setLocation(classDetails.get(3).trim());
            c.setSubject(classDetails.get(4).trim());
            c.setLecturer(classDetails.get(5).trim());
            allClassDetails.add(c);
        }
        return allClassDetails;
    }


    /**
     * Return only the timetable content from given Html source code
     *
     * @param html Html source code of APU timetable
     * @return {@link String} Html source code that only contain of timetable content
     */
    private static String getTimetableContent(String html) {
        String startSplitter = "<table class=\"timetable-display\">";
        String endSplitter = "</tr></table>";
        int start = html.indexOf(startSplitter) + startSplitter.length();
        int end = html.indexOf(endSplitter) + 5;
        html = html.substring(start, end);
        html = html.split("Lecturer</th></tr>")[1];
        return html;
    }

    /**
     * Find by a given Html tag and return the content into an arraylist from given Html source code
     *
     * @param html Html source code of APU timetable
     * @param tag  Html Tag
     * @return {@link ArrayList} list that content of all the content with the given Html tag
     */
    private static ArrayList<String> findAllByTag(String html, String tag) {
        ArrayList<String> extract = new ArrayList<>();
        String startTag = String.format("<%s>", tag);
        String endTag = String.format("</%s>", tag);
        while (html.contains(startTag)) {
            int startIndex = html.indexOf(startTag);
            int endIndex = html.indexOf(endTag);
            int dataStartIndex = startIndex + 2 + tag.length();//+2 open closing <>
            extract.add(html.substring(dataStartIndex, endIndex));
            html = html.substring(endIndex + 2 + tag.length() + 1);//+3 open closing slash </>
        }
        return extract;
    }
}