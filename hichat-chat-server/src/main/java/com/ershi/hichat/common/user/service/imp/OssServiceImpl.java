package com.ershi.hichat.common.user.service.imp;

import com.ershi.hichat.common.common.utils.AssertUtil;
import com.ershi.hichat.common.user.domain.enums.OssSceneEnum;
import com.ershi.hichat.common.user.domain.vo.request.oss.UploadFileReq;
import com.ershi.hichat.common.user.service.OssService;
import com.ershi.hichat.oss.MinioTemplate;
import com.ershi.hichat.oss.domain.OssReq;
import com.ershi.hichat.oss.domain.OssResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OssServiceImpl implements OssService {

    @Autowired
    private MinioTemplate minioTemplate;

    /**
     * 获取临时上传链接
     * @param uid
     * @param uploadFileReq
     * @return {@link OssResp } 返回代签名的临时上传链接，以及成功后可供下载的链接
     */
    @Override
    public OssResp getUploadUrl(Long uid, UploadFileReq uploadFileReq) {
        OssSceneEnum ossSceneEnum = OssSceneEnum.of(uploadFileReq.getScene());
        AssertUtil.isNotEmpty(ossSceneEnum, "文件上传场景有误");
        OssReq ossReq = OssReq.builder()
                .fileName(uploadFileReq.getFileName())
                .filePath(ossSceneEnum.getPath())
                .uid(uid)
                .build();
        return minioTemplate.getPreSignedObjectUrl(ossReq);
    }
}
