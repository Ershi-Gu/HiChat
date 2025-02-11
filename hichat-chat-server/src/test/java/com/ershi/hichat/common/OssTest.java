package com.ershi.hichat.common;

import com.ershi.hichat.oss.MinioTemplate;
import com.ershi.hichat.oss.domain.OssReq;
import com.ershi.hichat.oss.domain.OssResp;
import io.minio.messages.Bucket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class OssTest {

    @Autowired
    private MinioTemplate minioTemplate;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void getUploadUrl() {
        OssReq ossReq = OssReq.builder()
                .fileName("webgate北向接口调用.pdf")
                .filePath("")
                .autoPath(false)
                .build();
        OssResp preSignedObjectUrl = minioTemplate.getPreSignedObjectUrl(ossReq);
        System.out.println(preSignedObjectUrl);
    }

    @Test
    public void getBuckets() {
        List<Bucket> buckets = minioTemplate.listBuckets();
        for (Bucket bucket : buckets) {
            System.out.println(bucket.name());
        }
    }

    @Test
    public void test() {
        MinioTemplate bean = applicationContext.getBean(MinioTemplate.class);
        MinioTemplate bean1 = applicationContext.getBean(MinioTemplate.class);
        System.out.println(bean);
    }
}
