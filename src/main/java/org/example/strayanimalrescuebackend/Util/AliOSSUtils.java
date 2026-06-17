package org.example.strayanimalrescuebackend.Util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class AliOSSUtils {

    public static String upload(MultipartFile file) throws IOException {
        String endpoint = getRequiredEnv("ALI_OSS_ENDPOINT");
        String accessKeyId = getRequiredEnv("ALI_OSS_ACCESS_KEY_ID");
        String accessKeySecret = getRequiredEnv("ALI_OSS_ACCESS_KEY_SECRET");
        String bucketName = getRequiredEnv("ALI_OSS_BUCKET_NAME");

        InputStream inputStream = file.getInputStream();

        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ossClient.putObject(bucketName, fileName, inputStream);

        String url = endpoint.split("//")[0] + "//" + bucketName + "." + endpoint.split("//")[1] + "/" + fileName;
        ossClient.shutdown();
        return url;
    }

    private static String getRequiredEnv(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required environment variable: " + name);
        }
        return value;
    }
}
