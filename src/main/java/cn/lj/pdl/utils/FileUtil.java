package cn.lj.pdl.utils;

import org.apache.commons.io.FilenameUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author luojian
 * @date 2019/11/29
 */
public class FileUtil {

    public static boolean isImageFile(MultipartFile file) {
        return StringUtils.startsWithIgnoreCase(file.getContentType(), "image/");
    }

    public static boolean isImageFile(String fileName) {
        return StringUtils.endsWithIgnoreCase(fileName, ".jpg") ||
               StringUtils.endsWithIgnoreCase(fileName, ".jpeg") ||
               StringUtils.endsWithIgnoreCase(fileName, ".png");
    }

    public static boolean isZipFile(MultipartFile file) {
        return file.getContentType() != null && file.getContentType().contains("zip");
    }

    public static String getExtension(MultipartFile file) {
        return FilenameUtils.getExtension(file.getOriginalFilename());
    }

    public static String clearRedundantSuffixSlash(String path) {
        return StringUtils.trimTrailingCharacter(path, '/') + "/";
    }

    public static String clearAllSuffixSlash(String path) {
        return StringUtils.trimTrailingCharacter(path, '/');
    }

    public static String getResourcesFilePath(String relativePath) {
        return FileUtil.class.getClassLoader().getResource(relativePath).getPath();
    }
}
