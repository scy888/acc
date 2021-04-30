package common;


import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * @description:
 * @program: IdeaProjects
 * @author: jiancai.zhou
 * @date: 2019-07-01 17:37
 **/
@Slf4j
public class SftpUtils {
    private final static Logger LOGGER = LoggerFactory.getLogger(SftpUtils.class);

    /**
     * 连接sftp服务器
     *
     * @param serverIP 服务IP
     * @param port     端口
     * @param userName 用户名
     * @param password 密码
     * @throws Exception Exception
     */
    public static Channel connectServer(String serverIP, int port, String userName, String password, int timeout) throws Exception {
        JSch jsch = new JSch();
        // 根据用户名，主机ip，端口获取一个Session对象
        Session session = jsch.getSession(userName, serverIP, port);
        // 设置密码
        session.setPassword(password);
        // 为Session对象设置Properties
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.setTimeout(timeout);
        // 通过Session建立链接
        session.connect();
        // 打开SFTP通道
        Channel channel = session.openChannel("sftp");
        // 建立SFTP通道的连接
        channel.connect();

        return channel;
    }

    /**
     * 自动关闭资源
     */
    public static void close(Channel channel) throws JSchException {
        if (channel != null) {
            Session session = channel.getSession();
            if (session != null) {
                session.disconnect();
            }
            channel.disconnect();
        }

    }

    public static List<ChannelSftp.LsEntry> getDirList(ChannelSftp channelSftp, String path) throws SftpException {
        /**
         * @Author chongyang.sheng
         * @Date 9:51 2020/6/17
         * @Param [channelSftp, path]
         * @return java.util.List<com.jcraft.jsch.ChannelSftp.LsEntry>
        **/
        List<ChannelSftp.LsEntry> list = new ArrayList<>();
        if (channelSftp != null) {
            Vector vv = channelSftp.ls(path);
            if (vv == null || vv.size() == 0) {
                return list;
            } else {
                Object[] aa = vv.toArray();
                for (Object anAa : aa) {
                    ChannelSftp.LsEntry temp = (ChannelSftp.LsEntry) anAa;
                    list.add(temp);
                }
            }
        }
        return list;
    }

    /**
     * 下载文件
     *
     * @param remotePathFile 远程文件
     * @param localPathFile  本地文件[绝对路径]
     * @throws SftpException SftpException
     * @throws IOException   IOException
     */
    public static void downloadFile(ChannelSftp channelSftp, String remotePathFile, String localPathFile) throws SftpException, IOException {
        try (FileOutputStream os = new FileOutputStream(new File(localPathFile))) {
            if (channelSftp == null) {
                throw new IOException("sftp server not login");
            }
            channelSftp.get(remotePathFile, os);
        }
    }

    /**
     * 上传文件
     *
     * @param remoteFile 远程文件
     * @param localFile
     * @throws SftpException
     * @throws IOException
     */
    public static void uploadFile(ChannelSftp channelSftp, String remoteFile, String localFile) throws SftpException, IOException {
        try (FileInputStream in = new FileInputStream(new File(localFile))) {
            if (channelSftp == null) {
                throw new IOException("sftp server not login");
            }
            channelSftp.put(in, remoteFile);
        }
    }

    /**
     * 根目录
     *
     * @return String
     */
    public static String homeDir(ChannelSftp channelSftp) {
        try {
            return channelSftp.getHome();
        } catch (SftpException e) {
            return "/";
        }
    }

    public static void createDirIfNotExists(ChannelSftp channelSftp, String dirPath) {
        LOGGER.info("createDirIfNotExists dirPath={}", dirPath);
        if (dirPath != null && !"".equals(dirPath)) {
            try {
                String[] dirs = Arrays.stream(dirPath.split("/"))
                        .filter(e -> e != null && !"".equals(e))
                        .toArray(String[]::new);

                for (String dir : dirs) {
                    try {
                        channelSftp.cd(dir);
                    } catch (Exception ignore) {
                        try {
                            File tempFile = new File(dir);
                            if (!tempFile.exists()) {
                                LOGGER.info("dir = " + tempFile.getAbsolutePath());
                                channelSftp.mkdir(dir);
                            }
                        } catch (SftpException e) {
                            LOGGER.error("Create directory failure, directory:{}", dir, e);
                        }
                        try {
                            channelSftp.cd(dir);
                        } catch (SftpException e) {
                            LOGGER.error("Change directory failure, directory:{}", dir, e);
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.info("createDirIfNotExists fail. dirPath={}", dirPath, e);
            }
        }
    }

    public static void createDirIfNotExists(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }

    }


    public static void exec(Session sshSession, String command) {
        ChannelExec channelExec = null;
        try {
            System.out.println("Session connected.");
            System.out.println("Opening Channel.");
            Channel channel = sshSession.openChannel("exec");
            channelExec = (ChannelExec) channel;
            channelExec.setCommand(command);
            channelExec.connect();
        } catch (Exception e) {
            LOGGER.error("command exec fail");
            channelExec = null;
        } finally {
            channelExec.disconnect();
        }
    }

    /**
     * 复制远程文件到目标目录
     *
     * @param srcSftpFilePath
     * @param distSftpFilePath
     * @return
     * @throws SftpException
     * @throws IOException
     * @author inber
     * @since 2012.02.09
     */
    public static boolean copyFile(ChannelSftp channelSftp, String srcSftpFilePath, String distSftpFilePath) throws Exception {
        boolean isExistDistPath = false;

        if (!dirExist(channelSftp, srcSftpFilePath)) {
            LOGGER.error("source file not exist!");
            return false;
        }
        if (!dirExist(channelSftp, distSftpFilePath)) {
//                createDirIfNotExists(channelSftp, distSftpFilePath);
            LOGGER.info("dirExist distSftpFilePath = " + distSftpFilePath);
            createDirIfNotExists(distSftpFilePath);
            isExistDistPath = true;
        }
        if (isExistDistPath) {
            String fileName = srcSftpFilePath.substring(srcSftpFilePath.lastIndexOf("/") + 1);
            ByteArrayInputStream srcFtpFileStreams = getByteArrayInputStreamFile(channelSftp, srcSftpFilePath);
            log.info("srcFtpFileStreams:{},fileName:{},srcSftpFilePath:{}",srcFtpFileStreams,fileName,srcSftpFilePath);
            //                channelSftp.put(srcFtpFileStreams, distSftpFilePath);
            LOGGER.info("new File(distSftpFilePath) = " + new File(distSftpFilePath).getAbsolutePath());
            OutputStream outputStream = new FileOutputStream(new File(distSftpFilePath + "/" + fileName));
            outputStream.write(inputStreamToByte(srcFtpFileStreams));
            outputStream.flush();
            outputStream.close();
        }

        return true;
    }

    /**
     * 获取远程文件字节流
     *
     * @param sftpFilePath
     * @return
     * @throws SftpException
     * @throws IOException
     * @author inber
     * @since 2012.02.09
     */
    public static ByteArrayInputStream getByteArrayInputStreamFile(ChannelSftp channelSftp, String sftpFilePath) throws SftpException, IOException {
        boolean b = existFile(channelSftp, sftpFilePath);
        if (b) {
            InputStream inputStream = channelSftp.get(sftpFilePath);
            log.info("inputStream:{}",inputStream);
            byte[] srcFtpFileByte = inputStreamToByte(inputStream);
            ByteArrayInputStream srcFtpFileStreams = new ByteArrayInputStream(srcFtpFileByte);
            return srcFtpFileStreams;
        }
        log.info("ByteArrayInputStream=>b:{}",b);
        return null;
    }

    /**
     * inputStream类型转换为byte类型
     *
     * @param iStrm
     * @return
     * @throws IOException
     * @author inber
     * @since 2012.02.09
     */
    public static byte[] inputStreamToByte(InputStream iStrm) throws IOException {
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        int ch;
        while ((ch = iStrm.read()) != -1) {
            bytestream.write(ch);
        }
        byte imgdata[] = bytestream.toByteArray();
        bytestream.close();
        return imgdata;
    }

    /**
     * 判断文件夹是否存在
     *
     * @param path
     * @return
     */
    public static boolean dirExist(ChannelSftp channelSftp, String path) {
        try {
            Vector<?> vector = channelSftp.ls(path);
            return vector == null ? false : true;
        } catch (SftpException e) {
            log.info("无法使用sftp的ls命令:{}",e.getMessage());
            return false;
        }
    }

    /**
     * 判断远程文件是否存在
     *
     * @param srcSftpFilePath
     * @return 返回文件大小，如返回-2 文件不存在，-1文件读取异常
     * @throws SftpException
     * @author inber
     * @see
     * @since 2012.02.09
     */
    public static boolean existFile(ChannelSftp channelSftp, String srcSftpFilePath) {
        long filesize = 0;//文件大于等于0则存在
        try {
            SftpATTRS sftpATTRS = channelSftp.lstat(srcSftpFilePath);
            filesize = sftpATTRS.getSize();
        } catch (Exception e) {
            filesize = -1;//获取文件大小异常
            if (e.getMessage().toLowerCase().equals("no such file")) {
                filesize = -2;//文件不存在
            }
        }
        return filesize >= 0;
    }

}