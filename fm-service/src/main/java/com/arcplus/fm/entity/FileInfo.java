package com.arcplus.fm.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class FileInfo implements Serializable {

    private static final long serialVersionUID = -1438078028040922174L;

    /** 文件的md5 */
    private String id;
    /** 原始文件名 */
    private String name;
    /** 是否是图片 */
    private Boolean isImg;
    private String contentType;
    private long size;
    private String path;
    private String url;
    /**
     * 文件来源
     *
     */
    private String source;
    private Date createTime;
}
