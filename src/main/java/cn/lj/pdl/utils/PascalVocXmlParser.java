package cn.lj.pdl.utils;

import cn.lj.pdl.dto.dataset.annotation.DetectionBbox;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author luojian
 * @date 2020/2/4
 */
public class PascalVocXmlParser {

    private static final String OBJECT_START_TAG = "<object>";
    private static final String OBJECT_END_TAG = "</object>";
    private static final String IMAGE_FILENAME_TAG = "<filename>";
    private static final String NAME_TAG = "<name>";
    private static final String XMIN_TAG = "<xmin>";
    private static final String YMIN_TAG = "<ymin>";
    private static final String XMAX_TAG = "<xmax>";
    private static final String YMAX_TAG = "<ymax>";

    public static Pair<String, List<DetectionBbox>> parse(String xmlPath) {

        File xmlFile = new File(xmlPath);
        if(!xmlFile.exists()){
            throw new RuntimeException(String.format("xml path: '%s' not exist!", xmlPath));
        }

        String xmlContent;
        try{
            xmlContent = FileUtils.readFileToString(xmlFile, StandardCharsets.UTF_8);
        } catch (IOException e){
            throw new RuntimeException(e);
        }

        //Normally we'd use Jackson to parse XML, but Jackson has real trouble with multiple XML elements with
        //  the same name. However, the structure is simple and we can parse it manually (even though it's not
        // the most elegant thing to do :)
        String[] lines = xmlContent.split("\n");

        String imageFilename = null;
        List<DetectionBbox> bboxes = new ArrayList<>();
        for(int i = 0; i < lines.length; i++) {
            if (imageFilename == null && lines[i].contains(IMAGE_FILENAME_TAG)) {
                imageFilename = extractAndParseStr(lines[i]);
            }

            if(!lines[i].contains(OBJECT_START_TAG)){
                continue;
            }
            String name = null;
            Integer xmin = null;
            Integer ymin = null;
            Integer xmax = null;
            Integer ymax = null;
            while(!lines[i].contains(OBJECT_END_TAG)){
                if(name == null && lines[i].contains(NAME_TAG)){
                    int idxStartName = lines[i].indexOf('>') + 1;
                    int idxEndName = lines[i].lastIndexOf('<');
                    name = lines[i].substring(idxStartName, idxEndName);
                    i++;
                    continue;
                }
                if(xmin == null && lines[i].contains(XMIN_TAG)){
                    xmin = extractAndParse(lines[i]);
                    i++;
                    continue;
                }
                if(ymin == null && lines[i].contains(YMIN_TAG)){
                    ymin = extractAndParse(lines[i]);
                    i++;
                    continue;
                }
                if(xmax == null && lines[i].contains(XMAX_TAG)){
                    xmax = extractAndParse(lines[i]);
                    i++;
                    continue;
                }
                if(ymax == null && lines[i].contains(YMAX_TAG)){
                    ymax = extractAndParse(lines[i]);
                    i++;
                    continue;
                }

                i++;
            }

            if (imageFilename == null) {
                throw new RuntimeException("Invalid object format: no <filename> tag found in file " + xmlPath);
            }

            if(name == null){
                throw new RuntimeException("Invalid object format: no <name> tag found for object in file " + xmlPath);
            }

            if(xmin == null || ymin == null || xmax == null || ymax == null){
                throw new RuntimeException("Invalid object format: did not find all of <xmin>/<ymin>/<xmax>/<ymax> tags in " + xmlPath);
            }

            DetectionBbox bbox = new DetectionBbox();
            bbox.setClassName(name);
            bbox.setX(xmin);
            bbox.setY(ymin);
            bbox.setWidth(xmax - xmin);
            bbox.setHeight(ymax - ymin);

            bboxes.add(bbox);
        }

        return new ImmutablePair<>(imageFilename, bboxes);
    }

    private static int extractAndParse(String line){
        int idxStartName = line.indexOf('>') + 1;
        int idxEndName = line.lastIndexOf('<');
        String substring = line.substring(idxStartName, idxEndName);
        return (int)Double.parseDouble(substring);
    }

    private static String extractAndParseStr(String line){
        int idxStartName = line.indexOf('>') + 1;
        int idxEndName = line.lastIndexOf('<');
        return line.substring(idxStartName, idxEndName);
    }
}
