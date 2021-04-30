package common;

import org.apache.commons.compress.archivers.zip.ZipUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Zip压缩/解压缩
 *
 * @since 2020.06.12
 */
public class FileZipUtil {

    /**
     * 缓冲区大小
     */
    private static final int BUFFER = 1024;

    private static final String CHARSET = "UTF-8";

    /**
     * 压缩方法
     * （可以压缩空的子目录）
     *
     * @param srcDictionary 要压缩的目录
     * @param zipFileName   目标压缩全文件名
     * @return
     */
    public static void zip(String srcDictionary, String zipFileName) throws IOException {
        File srcFile = new File(srcDictionary);
        List<File> fileList = getAllFiles(srcFile);//所有要压缩的文件
        byte[] buffer = new byte[BUFFER];//缓冲器
        ZipEntry zipEntry = null;
        int readLength = 0;//每次读出来的长度

        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFileName));
        for (File file : fileList) {
            if (file.isFile()) { //若是文件，则压缩这个文件
                zipEntry = new ZipEntry(getRelativePath(srcDictionary, file));
                zipEntry.setSize(file.length());
                zipEntry.setTime(file.lastModified());
                zipOutputStream.putNextEntry(zipEntry);

                InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

                while ((readLength = inputStream.read(buffer, 0, BUFFER)) != -1) {
                    zipOutputStream.write(buffer, 0, readLength);
                }

                inputStream.close();
            } else {
                //若是目录（即空目录）则将这个目录写入zip条目
                zipEntry = new ZipEntry(getRelativePath(srcDictionary, file) + "/");
                zipOutputStream.putNextEntry(zipEntry);
            }

        }

        zipOutputStream.close();
    }

    /**
     * 解压缩方法
     *
     * @param zipFileName zip文件名全路径
     * @param destPath    解压目标路径
     * @return
     */
    public static boolean unzip(String zipFileName, String destPath) {

        try {
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFileName));
            ZipEntry zipEntry = null;
            byte[] buffer = new byte[BUFFER];//缓冲器
            int readLength = 0;//每次读出来的长度

            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (zipEntry.isDirectory()) {//若是zip条目目录，则需创建这个目录
                    File dir = new File(destPath + "/" + zipEntry.getName());

                    if (!dir.exists()) {
                        dir.mkdirs();
                        continue;//跳出
                    }
                }

                File file = createFile(destPath, zipEntry.getName());//若是文件，则需创建该文件

                OutputStream outputStream = new FileOutputStream(file);

                while ((readLength = zipInputStream.read(buffer, 0, BUFFER)) != -1) {
                    outputStream.write(buffer, 0, readLength);
                }

                outputStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean unzip(InputStream inputStream) {

        try {
            ZipInputStream zipInputStream = new ZipInputStream(inputStream);
            ZipEntry zipEntry = null;
            byte[] buffer = new byte[BUFFER];//缓冲器
            int readLength = 0; //每次读出来的长度

            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                while ((readLength = zipInputStream.read(buffer, 0, BUFFER)) != -1) {
                    outputStream.write(buffer, 0, readLength);
                }
                outputStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 取的给定源目录下的所有文件及空的子目录
     * 递归实现
     *
     * @param srcFile
     * @return
     */
    private static List<File> getAllFiles(File srcFile) {
        List<File> fileList = new ArrayList<File>();
        File[] tmp = null;
        if (srcFile.isDirectory()) {
            tmp = srcFile.listFiles();
            for (int i = 0; i < tmp.length; i++) {
                if (tmp[i].isFile()) {
                    fileList.add(tmp[i]);
                }

                if (tmp[i].isDirectory()) {
                    if (tmp[i].listFiles().length != 0) {
                        //若不是空目录，则递归添加其下的目录和文件
                        fileList.addAll(getAllFiles(tmp[i]));
                    } else {
                        //若是空目录，则添加这个目录到fileList
                        fileList.add(tmp[i]);
                    }
                }
            }
        } else {
            fileList.add(srcFile);
        }
        return fileList;
    }
    /**
     * 取相对路径
     * 依据文件名和压缩源路径得到文件在压缩源路径下的相对路径
     *
     * @param dirPath 压缩源路径
     * @param file
     * @return 相对路径
     */
    private static String getRelativePath(String dirPath, File file) {
        File dir = new File(dirPath);
        String relativePath = file.getName();

        while (true) {
            file = file.getParentFile();

            if (file == null) {
                break;
            }

            if (file.equals(dir)) {
                break;
            } else {
                relativePath = file.getName() + "/" + relativePath;
            }
        }    // end while

        return relativePath;
    }
    /**
     * 创建文件
     * 根据压缩包内文件名和解压缩目的路径，创建解压缩目标文件，
     * 生成中间目录
     *
     * @param dstPath  解压缩目的路径
     * @param fileName 压缩包内文件名
     * @return 解压缩目标文件
     * @throws IOException
     */
    private static File createFile(String dstPath, String fileName) throws IOException {
        String[] dirs = fileName.split("/"); //将文件名的各级目录分解
        File file = new File(dstPath);

        if (dirs.length > 1) { //文件有上级目录
            for (int i = 0; i < dirs.length - 1; i++) {
                file = new File(file, dirs[i]); //依次创建文件对象知道文件的上一级目录
            }
            if (!file.exists()) {
                file.mkdirs(); //文件对应目录若不存在，则创建
            }
            file = new File(file, dirs[dirs.length - 1]); //创建文件
            return file;
        } else {
            if (!file.exists()) {
                file.mkdirs();//若目标路径的目录不存在，则创建
            }
            file = new File(file, dirs[0]);//创建文件
            return file;
        }
    }
}
