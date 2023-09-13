package com.liyuxiang.film.service;

import cn.hutool.core.util.IdUtil;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.liyuxiang.film.config.exception.FileException;
import com.liyuxiang.film.config.util.ConstantPropertiesUtils;
import com.liyuxiang.film.config.util.FdfsClientWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FileService {
    @Autowired
    private FdfsClientWrapper fdfsClientWrapper;

    /**
     * 存储文件到系统
     * @param file 文件
     * @return 文件名
     */
    public String storeFile(MultipartFile file) {
        // Normalize file name
        String fileName = file.getOriginalFilename(); // 拿到文件名
        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            byte[] bytes = file.getBytes();
            long fileSize = file.getSize();
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
            return fdfsClientWrapper.uploadFile(bytes,fileSize,extension);
        } catch (IOException ex) {
            throw new FileException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

//    /**
//     * 加载文件
//     * @param fileName 文件名
//     * @return 文件
//     */
//    public Resource loadFileAsResource(String fileName) {
//        try {
//            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
//            Resource resource = new UrlResource(filePath.toUri());
//            if(resource.exists()) {
//                return resource;
//            } else {
//                throw new FileException("File not found " + fileName);
//            }
//        } catch (MalformedURLException ex) {
//            throw new FileException("File not found " + fileName, ex);
//        }
//    }

    /**
     * 删除文件
     * @param filePath
     */
    public boolean deleteFile(String filePath) {
        try {
            // 删除文件
            fdfsClientWrapper.deleteFile(filePath);
            return true;
        } catch (Exception ex) {
            throw new FileException("Could not delete " + filePath + ". Please try again!", ex);
        } finally {
            return false;
        }
    }

    public String uploadMp4(MultipartFile file) throws IOException{

        String endpoint = ConstantPropertiesUtils.END_POINT;
        String accessKeyId = ConstantPropertiesUtils.ACCESS_KEY_ID;
        String accessKeySecret = ConstantPropertiesUtils.ACCESS_KEY_SECRET;
        String bucketName = ConstantPropertiesUtils.BUCKET_NAME;
        String url = null;

        //创建OSSClient实例。
        OSS ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);

        //获取上传文件输入流
        InputStream inputStream = file.getInputStream();
        //获取文件名称
        String fileName = file.getOriginalFilename();
        int i = fileName.lastIndexOf(".");
        String fileType = fileName.substring(i);
        String simpleUUID = IdUtil.simpleUUID();
        fileName = simpleUUID + fileType;
        //把文件按日期分类，构建日期路径：avatar/2019/02/26/文件名
        SimpleDateFormat format = new SimpleDateFormat("YYYY/MM/dd");
        String datePath = format.format(new Date());
        //拼接
        fileName = datePath + "/" + fileName;

        //调用oss方法上传到阿里云
        //第一个参数：Bucket名称
        //第二个参数：上传到oss文件路径和文件名称
        //第三个参数：上传文件输入流
        ossClient.putObject(bucketName, fileName, inputStream);

        //把上传后把文件url返回
        url = "https://" + bucketName + "." + endpoint + "/" + fileName;
        //关闭OSSClient
        ossClient.shutdown();

        return url;
    }

    public boolean deleteMp4(String fileName) {
        String endpoint = ConstantPropertiesUtils.END_POINT;
        String accessKeyId = ConstantPropertiesUtils.ACCESS_KEY_ID;
        String accessKeySecret = ConstantPropertiesUtils.ACCESS_KEY_SECRET;
        String bucketName = ConstantPropertiesUtils.BUCKET_NAME;

        String fileObj = "";
        String pattern = "(?<=com/).*";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(fileName);
        if (m.find()) {
            fileObj = m.group(); // 获取"2023/03/21/ba292f2693614cfea39f881364be705b.mp4"的形式
        }
        //创建OSSClient实例。
        OSS ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            // 删除文件或目录。如果要删除目录，目录必须为空。
            ossClient.deleteObject(bucketName, fileObj);
            return true;
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
            return false;
        }
    }
}

