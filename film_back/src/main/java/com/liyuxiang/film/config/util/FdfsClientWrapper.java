package com.liyuxiang.film.config.util;

import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * @author: YinLei
 * Package:  com.englishcode.fastdfsdemo.utils
 * @date: 2021/6/18 10:03
 * @Description: Fastdfs客户端工具类
 * @version: 1.0
 */
@Component
public class FdfsClientWrapper {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    /**
     * 文件上传
     * @param bytes     文件字节
     * @param fileSize  文件大小
     * @param extension 文件扩展名
     * @return 返回文件路径（卷名和文件名）
     */
    public String uploadFile(byte[] bytes, long fileSize, String extension) {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        // 元数据
        Set<MetaData> metaDataSet = new HashSet<MetaData>();
        metaDataSet.add(new MetaData("dateTime", LocalDateTime.now().toString()));
        metaDataSet.add(new MetaData("Author","liyuxiang"));
        StorePath storePath = fastFileStorageClient.uploadFile(bais, fileSize, extension, metaDataSet);
        return storePath.getFullPath();
    }

    /**
     * 下载文件
     * @param filePath 文件路径
     * @return 文件字节
     * @throws IOException
     */
    public byte[] downloadFile(String filePath) throws IOException {
        byte[] bytes = null;
        if (StringUtils.isNotBlank(filePath)) {
            String group = filePath.substring(0, filePath.indexOf("/"));
            String path = filePath.substring(filePath.indexOf("/") + 1);
            DownloadByteArray byteArray = new DownloadByteArray();
            bytes = fastFileStorageClient.downloadFile(group, path, byteArray);
        }
        return bytes;
    }

    /**
     * 删除文件
     * @param filePath 文件路径
     */
    public void deleteFile(String filePath) {
        if (StringUtils.isNotBlank(filePath)) {
            fastFileStorageClient.deleteFile(filePath);
        }
    }

}
