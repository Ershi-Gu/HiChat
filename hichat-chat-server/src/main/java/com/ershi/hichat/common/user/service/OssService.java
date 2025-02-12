package com.ershi.hichat.common.user.service;


import com.ershi.hichat.common.user.domain.vo.request.oss.UploadFileReq;
import com.ershi.hichat.oss.domain.OssResp;

/**
 * Oss 文件上传服务
 * @author Ershi
 * @date 2025/02/12
 */
public interface OssService {

    /**
     * 获取临时的上传链接
     */
    OssResp getUploadUrl(Long uid, UploadFileReq uploadFileReq);
}
